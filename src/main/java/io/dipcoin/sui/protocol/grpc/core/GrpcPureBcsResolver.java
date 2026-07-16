package io.dipcoin.sui.protocol.grpc.core;

import io.dipcoin.sui.bcs.PureBcs;
import io.dipcoin.sui.bcs.PureBcs.BasePureType;
import io.dipcoin.sui.bcs.types.arg.call.CallArg;
import io.dipcoin.sui.bcs.types.arg.call.CallArgPure;
import io.dipcoin.sui.bcs.types.transaction.Argument;
import io.dipcoin.sui.bcs.types.transaction.Command;
import io.dipcoin.sui.bcs.types.transaction.ProgrammableMoveCall;
import io.dipcoin.sui.bcs.types.transaction.ProgrammableTransaction;
import io.dipcoin.sui.protocol.grpc.GrpcSuiClient;
import sui.rpc.v2.MovePackage.FunctionDescriptor;
import sui.rpc.v2.MovePackage.OpenSignature;
import sui.rpc.v2.MovePackage.OpenSignatureBody;
import sui.rpc.v2.MovePackageServiceOuterClass.GetFunctionRequest;
import sui.rpc.v2.MovePackageServiceOuterClass.GetFunctionResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : Same
 * @datetime : 2026/4/30
 * @Description : gRPC counterpart of {@link PureBcs#resolvePureArgsTypes}.
 *
 * <p>Re-uses {@link PureBcs} for the actual BCS deserialization, but obtains the
 * Move function signature via the {@code MovePackageService.GetFunction} gRPC API
 * (replacing the deprecated JSON-RPC {@code sui_getNormalizedMoveFunction}).
 *
 * <p>Supported pure parameter types — kept identical to the JSON-RPC version:
 * <ul>
 *   <li>All scalar primitives: {@code U8}, {@code U16}, {@code U32}, {@code U64},
 *       {@code U128}, {@code U256}, {@code BOOL}, {@code ADDRESS}</li>
 *   <li>{@code vector<u8>} (mapped to {@link BasePureType#VECTOR_U8})</li>
 *   <li>{@code SplitCoins.amounts} — hard-coded to {@code U64}</li>
 * </ul>
 *
 * <p>Datatypes (structs/enums), generic type parameters, and other vector element
 * types are silently skipped because they are not pure inputs.
 */
public class GrpcPureBcsResolver {

    /**
     * Cache of fully-qualified function name -> FunctionDescriptor, mirroring the
     * cache used by the JSON-RPC resolver in {@code QueryBuilder#getMoveFunction}.
     */
    private static final Map<String, FunctionDescriptor> FUNCTION_DESCRIPTOR_CACHE = new ConcurrentHashMap<>();

    private GrpcPureBcsResolver() {
    }

    /**
     * Parses the pure bytes of each {@code MoveCall} / {@code SplitCoins} input in the
     * given {@link ProgrammableTransaction} into typed values, using on-chain Move
     * function signatures fetched over gRPC.
     *
     * @param programmableTx the BCS-decoded programmable transaction whose pure inputs
     *                       still hold raw bytes only
     * @param grpcSuiClient  the gRPC client used to look up function descriptors
     */
    public static void resolvePureArgsTypes(ProgrammableTransaction programmableTx, GrpcSuiClient grpcSuiClient) {
        List<Command> commands = programmableTx.getCommands();
        if (commands == null || commands.isEmpty()) {
            return;
        }
        for (Command command : commands) {
            if (command instanceof Command.MoveCall moveCall) {
                resolveMoveCall(moveCall.getMoveCall(), programmableTx, grpcSuiClient);
            } else if (command instanceof Command.SplitCoins splitCoins) {
                resolveSplitCoins(splitCoins, programmableTx);
            }
        }
    }

    // =========================================================================
    // MoveCall
    // =========================================================================

    private static void resolveMoveCall(
            ProgrammableMoveCall moveCall,
            ProgrammableTransaction programmableTx,
            GrpcSuiClient grpcSuiClient) {

        List<Argument> arguments = moveCall.getArguments();
        if (arguments == null || arguments.isEmpty()) {
            return;
        }

        FunctionDescriptor descriptor = getFunctionDescriptor(
                grpcSuiClient,
                moveCall.getPackageId(),
                moveCall.getModule(),
                moveCall.getFunction());

        List<OpenSignature> parameters = descriptor.getParametersList();
        if (parameters == null || parameters.isEmpty()) {
            return;
        }

        int size = Math.min(parameters.size(), arguments.size());
        for (int i = 0; i < size; i++) {
            String type = derivePureTypeName(parameters.get(i).getBody());
            if (type == null) {
                continue;
            }
            Argument argument = arguments.get(i);
            if (!(argument instanceof Argument.Input input)) {
                continue;
            }
            CallArg callArg = findInputCallArg(programmableTx, input.getIndex());
            if (!(callArg instanceof CallArgPure pure)) {
                continue;
            }
            try {
                Object arg = PureBcs.deserializeFromBytes(type, pure.getRawBytes());
                pure.setArg(arg);
                pure.setBasePureType(BasePureType.valueOf(type.toUpperCase()));
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to deserialize MoveCall Pure type " + type, e);
            }
        }
    }

    // =========================================================================
    // SplitCoins
    // =========================================================================

    private static void resolveSplitCoins(
            Command.SplitCoins splitCoins,
            ProgrammableTransaction programmableTx) {

        List<Argument> amounts = splitCoins.getAmounts();
        if (amounts == null || amounts.isEmpty()) {
            return;
        }
        BasePureType u64 = BasePureType.U64;
        String type = u64.name();
        for (Argument amount : amounts) {
            if (!(amount instanceof Argument.Input input)) {
                continue;
            }
            CallArg callArg = findInputCallArg(programmableTx, input.getIndex());
            if (callArg == null) {
                throw new IllegalStateException(
                        "CallArg index " + input.getIndex() + " not found in SplitCoins of ProgrammableTransaction");
            }
            if (!(callArg instanceof CallArgPure pure)) {
                continue;
            }
            try {
                Object arg = PureBcs.deserializeFromBytes(type, pure.getRawBytes());
                pure.setArg(arg);
                pure.setBasePureType(u64);
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to deserialize SplitCoins Pure type " + type, e);
            }
        }
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    /**
     * Maps an {@link OpenSignatureBody} to the type name accepted by
     * {@link PureBcs#deserializeFromBytes(String, byte[])} and {@link BasePureType}.
     *
     * <p>Returns {@code null} for non-pure types (Datatype/TypeParameter/non-u8 vectors),
     * matching the silent-skip behaviour of the JSON-RPC implementation.
     */
    private static String derivePureTypeName(OpenSignatureBody body) {
        OpenSignatureBody.Type type = body.getType();
        return switch (type) {
            case BOOL, U8, U16, U32, U64, U128, U256, ADDRESS -> type.name();
            case VECTOR -> {
                if (body.getTypeParameterInstantiationCount() > 0
                        && body.getTypeParameterInstantiation(0).getType() == OpenSignatureBody.Type.U8) {
                    yield BasePureType.VECTOR_U8.name();
                }
                yield null;
            }
            default -> null;
        };
    }

    /**
     * Finds the {@link CallArg} at the given BCS positional index, delegating to the
     * {@link ProgrammableTransaction#getInputByIndex(int)} extension point so that
     * deserialized transactions (with duplicate pure inputs) resolve positionally.
     */
    private static CallArg findInputCallArg(ProgrammableTransaction programmableTx, int index) {
        return programmableTx == null ? null : programmableTx.getInputByIndex(index);
    }

    /**
     * Fetches and caches the {@link FunctionDescriptor} for the given package/module/function.
     */
    private static FunctionDescriptor getFunctionDescriptor(
            GrpcSuiClient grpcSuiClient,
            String packageId,
            String module,
            String function) {

        String key = packageId + "::" + module + "::" + function;
        FunctionDescriptor cached = FUNCTION_DESCRIPTOR_CACHE.get(key);
        if (cached != null) {
            return cached;
        }
        GetFunctionRequest request = GetFunctionRequest.newBuilder()
                .setPackageId(packageId)
                .setModuleName(module)
                .setName(function)
                .build();
        GetFunctionResponse response = grpcSuiClient.getFunction(request);
        if (!response.hasFunction()) {
            throw new IllegalStateException("Function descriptor not returned for " + key);
        }
        FunctionDescriptor descriptor = response.getFunction();
        FUNCTION_DESCRIPTOR_CACHE.put(key, descriptor);
        return descriptor;
    }
}
