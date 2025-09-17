/*
 * Copyright 2025 Dipcoin LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");you may not use this file except in compliance with
 * the License.You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software distributed under the License is distributed on
 * an "AS IS" BASIS,WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.dipcoin.sui.bcs;

import io.dipcoin.sui.bcs.types.arg.call.CallArg;
import io.dipcoin.sui.bcs.types.arg.call.CallArgObjectArg;
import io.dipcoin.sui.bcs.types.arg.call.CallArgPure;
import io.dipcoin.sui.bcs.types.arg.object.*;
import io.dipcoin.sui.bcs.types.arg.object.*;
import io.dipcoin.sui.bcs.types.auth.PasskeyAuthenticator;
import io.dipcoin.sui.bcs.types.gas.GasData;
import io.dipcoin.sui.bcs.types.gas.SuiObjectRef;
import io.dipcoin.sui.bcs.types.intent.*;
import io.dipcoin.sui.bcs.types.intent.*;
import io.dipcoin.sui.bcs.types.owner.Owner;
import io.dipcoin.sui.bcs.types.signature.*;
import io.dipcoin.sui.bcs.types.tag.*;
import io.dipcoin.sui.bcs.types.transaction.*;
import io.dipcoin.sui.bcs.types.signature.*;
import io.dipcoin.sui.bcs.types.tag.*;
import io.dipcoin.sui.bcs.types.transaction.*;
import io.dipcoin.sui.util.Numeric;
import io.dipcoin.sui.util.ObjectIdUtil;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author : Same
 * @datetime : 2025/7/10 19:15
 * @Description : Sui BCS serializer, provides all Sui-related BCS type definitions and serialization functionality, corresponding to TypeScript's `bcs.ts`.
 */
public class SuiBcs {
    
    // Constant definitions.
    public static final int SUI_ADDRESS_LENGTH = 32;
    
    // Cache serializers to improve performance.
    private static final Map<Class<?>, BcsSerializer.BcsTypeSerializer<?>> SERIALIZER_CACHE = new ConcurrentHashMap<>();
    
    /**
     * Address type serializer.
     */
    public static final BcsSerializer.BcsTypeSerializer<String> ADDRESS_SERIALIZER = (serializer, address) -> {
        // verify address
        if (!isValidSuiAddress(ObjectIdUtil.normalizeSuiAddress(address))) {
            throw new IllegalArgumentException("Invalid Sui address: " + address);
        }
        
        // serialize address
        serializer.writeAddress(address);
    };
    
    /**
     * Object summary serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<String> OBJECT_DIGEST_SERIALIZER = (serializer, digest) -> {
        byte[] digestBytes = Numeric.base58ToBytes(digest);
        if (digestBytes.length != 32) {
            throw new IllegalArgumentException("ObjectDigest must be 32 bytes");
        }
        serializer.writeBytes(digestBytes);
    };
    
    /**
     * SuiObjectRef serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<SuiObjectRef> SUI_OBJECT_REF_SERIALIZER = (serializer, ref) -> {
        ADDRESS_SERIALIZER.serialize(serializer, ref.getObjectId());
        serializer.writeU64(ref.getVersion());
        OBJECT_DIGEST_SERIALIZER.serialize(serializer, ref.getDigest());
    };
    
    /**
     * SharedObjectRef serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<SharedObjectRef> SHARED_OBJECT_REF_SERIALIZER = (serializer, ref) -> {
        ADDRESS_SERIALIZER.serialize(serializer, ref.getObjectId());
        serializer.writeU64(ref.getInitialSharedVersion());
        serializer.writeBool(ref.isMutable());
    };
    
    /**
     * ObjectArg serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<ObjectArg> OBJECT_ARG_SERIALIZER = (serializer, arg) -> {
        if (arg instanceof ObjectArgImmOrOwnedObject) {
            serializer.writeU8((byte) 0); // ImmOrOwnedObject variant
            SUI_OBJECT_REF_SERIALIZER.serialize(serializer, ((ObjectArgImmOrOwnedObject) arg).getObjectRef());
        } else if (arg instanceof ObjectArgSharedObject) {
            serializer.writeU8((byte) 1); // SharedObject variant
            SHARED_OBJECT_REF_SERIALIZER.serialize(serializer, ((ObjectArgSharedObject) arg).getObjectRef());
        } else if (arg instanceof ObjectArgReceiving) {
            serializer.writeU8((byte) 2); // Receiving variant
            SUI_OBJECT_REF_SERIALIZER.serialize(serializer, ((ObjectArgReceiving) arg).getObjectRef());
        } else {
            throw new IllegalArgumentException("Unknown ObjectArg type: " + arg.getClass());
        }
    };
    
    /**
     * CallArg serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<CallArg> CALL_ARG_SERIALIZER = (serializer, arg) -> {
        if (arg instanceof CallArgPure) {
            serializer.writeU8((byte) 0); // Pure variant
            CallArgPure pure = (CallArgPure) arg;
            BcsSerializer.BcsTypeSerializer<?> pureSerializer = PureBcs.getSerializer(pure.getBasePureType().name());
            ((BcsSerializer.BcsTypeSerializer<Object>) pureSerializer).serialize(serializer, pure.getArg());
        } else if (arg instanceof CallArgObjectArg) {
            serializer.writeU8((byte) 1); // Object variant
            CallArgObjectArg objArg = (CallArgObjectArg) arg;
            serializeObjectArg(serializer, objArg.getObjectArg());
        } else {
            throw new IllegalArgumentException("Unknown CallArg type: " + arg.getClass());
        }
    };
    
    /**
     * serialize ObjectArg
     */
    private static void serializeObjectArg(BcsSerializer serializer, ObjectArg arg) throws IOException {
        if (arg instanceof ObjectArgImmOrOwnedObject) {
            serializer.writeU8((byte) 0); // ImmOrOwnedObject variant
            SUI_OBJECT_REF_SERIALIZER.serialize(serializer, ((ObjectArgImmOrOwnedObject) arg).getObjectRef());
        } else if (arg instanceof ObjectArgSharedObject) {
            serializer.writeU8((byte) 1); // SharedObject variant
            SHARED_OBJECT_REF_SERIALIZER.serialize(serializer, ((ObjectArgSharedObject) arg).getObjectRef());
        } else if (arg instanceof ObjectArgReceiving) {
            serializer.writeU8((byte) 2); // Receiving variant
            SUI_OBJECT_REF_SERIALIZER.serialize(serializer, ((ObjectArgReceiving) arg).getObjectRef());
        } else {
            throw new IllegalArgumentException("Unknown ObjectArg type: " + arg.getClass());
        }
    }
    
    /**
     * TypeTag serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<TypeTag> TYPE_TAG_SERIALIZER = (serializer, tag) -> {
        serializeTypeTag(serializer, tag);
    };
    
    /**
     * serialize TypeTag
     */
    private static void serializeTypeTag(BcsSerializer serializer, TypeTag tag) throws IOException {
        if (tag instanceof TypeTagBool) {
            serializer.writeU8((byte) 0);
        } else if (tag instanceof TypeTagU8) {
            serializer.writeU8((byte) 1);
        } else if (tag instanceof TypeTagU64) {
            serializer.writeU8((byte) 2);
        } else if (tag instanceof TypeTagU128) {
            serializer.writeU8((byte) 3);
        } else if (tag instanceof TypeTagAddress) {
            serializer.writeU8((byte) 4);
        } else if (tag instanceof TypeTagSigner) {
            serializer.writeU8((byte) 5);
        } else if (tag instanceof TypeTagVector) {
            serializer.writeU8((byte) 6);
            serializeTypeTag(serializer, ((TypeTagVector) tag).getElementType());
        } else if (tag instanceof TypeTagStruct) {
            serializer.writeU8((byte) 7);
            serializeStructTag(serializer, ((TypeTagStruct) tag).getStructTag());
        } else if (tag instanceof TypeTagU16) {
            serializer.writeU8((byte) 8);
        } else if (tag instanceof TypeTagU32) {
            serializer.writeU8((byte) 9);
        } else if (tag instanceof TypeTagU256) {
            serializer.writeU8((byte) 10);
        } else {
            throw new IllegalArgumentException("Unknown TypeTag type: " + tag.getClass());
        }
    }
    
    /**
     * StructTag serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<TypeTagStructTag> STRUCT_TAG_SERIALIZER = (serializer, structTag) -> {
        serializeStructTag(serializer, structTag);
    };
    
    /**
     * serialize StructTag
     */
    private static void serializeStructTag(BcsSerializer serializer, TypeTagStructTag structTag) throws IOException {
        ADDRESS_SERIALIZER.serialize(serializer, structTag.getAddress());
        serializer.writeString(structTag.getModule());
        serializer.writeString(structTag.getName());
        serializer.writeVector(structTag.getTypeParams(), TYPE_TAG_SERIALIZER);
    }
    
    /**
     * GasData serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<GasData> GAS_DATA_SERIALIZER = (serializer, gasData) -> {
        serializer.writeVector(gasData.getPayment(), SUI_OBJECT_REF_SERIALIZER);
        ADDRESS_SERIALIZER.serialize(serializer, gasData.getOwner());
        serializer.writeU64(gasData.getPrice());
        serializer.writeU64(gasData.getBudget().longValue());
    };
    
    /**
     * Argument serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<Argument> ARGUMENT_SERIALIZER = (serializer, arg) -> {
        if (arg instanceof Argument.GasCoin) {
            serializer.writeU8((byte) 0); // GasCoin variant
        } else if (arg instanceof Argument.Input) {
            serializer.writeU8((byte) 1); // Input variant
            serializer.writeU16((short) ((Argument.Input) arg).getIndex());
        } else if (arg instanceof Argument.Result) {
            serializer.writeU8((byte) 2); // Result variant
            serializer.writeU16((short) ((Argument.Result) arg).getIndex());
        } else if (arg instanceof Argument.NestedResult) {
            serializer.writeU8((byte) 3); // NestedResult variant
            Argument.NestedResult nested = (Argument.NestedResult) arg;
            serializer.writeU16((short) nested.getResultIndex());
            serializer.writeU16((short) nested.getNestedIndex());
        } else {
            throw new IllegalArgumentException("Unknown Argument type: " + arg.getClass());
        }
    };
    
    /**
     * ProgrammableMoveCall serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<ProgrammableMoveCall> PROGRAMMABLE_MOVE_CALL_SERIALIZER = (serializer, call) -> {
        ADDRESS_SERIALIZER.serialize(serializer, call.getPackageId());
        serializer.writeString(call.getModule());
        serializer.writeString(call.getFunction());
        serializer.writeVector(call.getTypeArguments(), TYPE_TAG_SERIALIZER);
        serializer.writeVector(call.getArguments(), ARGUMENT_SERIALIZER);
    };
    
    /**
     * Command serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<Command> COMMAND_SERIALIZER = (serializer, command) -> {
        if (command instanceof Command.MoveCall) {
            serializer.writeU8((byte) 0); // MoveCall variant
            PROGRAMMABLE_MOVE_CALL_SERIALIZER.serialize(serializer, ((Command.MoveCall) command).getMoveCall());
        } else if (command instanceof Command.TransferObjects) {
            serializer.writeU8((byte) 1); // TransferObjects variant
            Command.TransferObjects transfer = (Command.TransferObjects) command;
            serializer.writeVector(transfer.getObjects(), ARGUMENT_SERIALIZER);
            ARGUMENT_SERIALIZER.serialize(serializer, transfer.getAddress());
        } else if (command instanceof Command.SplitCoins) {
            serializer.writeU8((byte) 2); // SplitCoins variant
            Command.SplitCoins split = (Command.SplitCoins) command;
            ARGUMENT_SERIALIZER.serialize(serializer, split.getCoin());
            serializer.writeVector(split.getAmounts(), ARGUMENT_SERIALIZER);
        } else if (command instanceof Command.MergeCoins) {
            serializer.writeU8((byte) 3); // MergeCoins variant
            Command.MergeCoins merge = (Command.MergeCoins) command;
            ARGUMENT_SERIALIZER.serialize(serializer, merge.getDestination());
            serializer.writeVector(merge.getSources(), ARGUMENT_SERIALIZER);
        } else if (command instanceof Command.Publish) {
            serializer.writeU8((byte) 4); // Publish variant
            Command.Publish publish = (Command.Publish) command;
            serializer.writeVector(publish.getModules(), (s, bytes) -> s.writeBytes(bytes));
            serializer.writeVector(publish.getDependencies(), ADDRESS_SERIALIZER);
        } else if (command instanceof Command.MakeMoveVec) {
            serializer.writeU8((byte) 5); // MakeMoveVec variant
            Command.MakeMoveVec makeVec = (Command.MakeMoveVec) command;
            // serializeOption<String>
            if (makeVec.getType() != null) {
                serializer.writeBool(true); // Some
                serializer.writeString(makeVec.getType());
            } else {
                serializer.writeBool(false); // None
            }
            serializer.writeVector(makeVec.getElements(), ARGUMENT_SERIALIZER);
        } else if (command instanceof Command.Upgrade) {
            serializer.writeU8((byte) 6); // Upgrade variant
            Command.Upgrade upgrade = (Command.Upgrade) command;
            serializer.writeVector(upgrade.getModules(), (s, bytes) -> s.writeBytes(bytes));
            serializer.writeVector(upgrade.getDependencies(), ADDRESS_SERIALIZER);
            ADDRESS_SERIALIZER.serialize(serializer, upgrade.getPackageId());
            ARGUMENT_SERIALIZER.serialize(serializer, upgrade.getTicket());
        } else {
            throw new IllegalArgumentException("Unknown Command type: " + command.getClass());
        }
    };
    
    /**
     * ProgrammableTransaction serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<ProgrammableTransaction> PROGRAMMABLE_TRANSACTION_SERIALIZER = (serializer, tx) -> {
        serializer.writeVector(tx.getInputList(), CALL_ARG_SERIALIZER);
        serializer.writeVector(tx.getCommands(), COMMAND_SERIALIZER);
    };
    
    /**
     * TransactionKind serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<TransactionKind> TRANSACTION_KIND_SERIALIZER = (serializer, kind) -> {
        if (kind instanceof TransactionKind.ProgrammableTransaction) {
            serializer.writeU8((byte) 0); // ProgrammableTransaction variant
            PROGRAMMABLE_TRANSACTION_SERIALIZER.serialize(serializer, ((TransactionKind.ProgrammableTransaction) kind).getProgrammableTransaction());
        } else if (kind instanceof TransactionKind.ChangeEpoch) {
            serializer.writeU8((byte) 1); // ChangeEpoch variant
        } else if (kind instanceof TransactionKind.Genesis) {
            serializer.writeU8((byte) 2); // Genesis variant
        } else if (kind instanceof TransactionKind.ConsensusCommitPrologue) {
            serializer.writeU8((byte) 3); // ConsensusCommitPrologue variant
        } else {
            throw new IllegalArgumentException("Unknown TransactionKind type: " + kind.getClass());
        }
    };
    
    /**
     * TransactionExpiration serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<TransactionExpiration> TRANSACTION_EXPIRATION_SERIALIZER = (serializer, expiration) -> {
        if (expiration instanceof TransactionExpiration.None) {
            serializer.writeU8((byte) 0); // None variant
        } else if (expiration instanceof TransactionExpiration.Epoch) {
            serializer.writeU8((byte) 1); // Epoch variant
            serializer.writeU64(((TransactionExpiration.Epoch) expiration).getEpoch());
        } else {
            throw new IllegalArgumentException("Unknown TransactionExpiration type: " + expiration.getClass());
        }
    };
    
    /**
     * TransactionDataV1 serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<TransactionDataV1> TRANSACTION_DATA_V1_SERIALIZER = (serializer, data) -> {
        TRANSACTION_KIND_SERIALIZER.serialize(serializer, data.getKind());
        ADDRESS_SERIALIZER.serialize(serializer, data.getSender());
        GAS_DATA_SERIALIZER.serialize(serializer, data.getGasData());
        TRANSACTION_EXPIRATION_SERIALIZER.serialize(serializer, data.getExpiration());
    };
    
    /**
     * TransactionData serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<TransactionData> TRANSACTION_DATA_SERIALIZER = (serializer, data) -> {
        if (data instanceof TransactionData.V1) {
            serializer.writeU8((byte) 0); // V1 variant
            TRANSACTION_DATA_V1_SERIALIZER.serialize(serializer, ((TransactionData.V1) data).getTransactionDataV1());
        } else {
            throw new IllegalArgumentException("Unknown TransactionData type: " + data.getClass());
        }
    };
    
    /**
     * IntentScope serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<IntentScope> INTENT_SCOPE_SERIALIZER = (serializer, scope) -> {
        if (scope instanceof IntentScope.TransactionData) {
            serializer.writeU8((byte) 0); // TransactionData variant
        } else if (scope instanceof IntentScope.TransactionEffects) {
            serializer.writeU8((byte) 1); // TransactionEffects variant
        } else if (scope instanceof IntentScope.CheckpointSummary) {
            serializer.writeU8((byte) 2); // CheckpointSummary variant
        } else if (scope instanceof IntentScope.PersonalMessage) {
            serializer.writeU8((byte) 3); // PersonalMessage variant
        } else {
            throw new IllegalArgumentException("Unknown IntentScope type: " + scope.getClass());
        }
    };
    
    /**
     * IntentVersion serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<IntentVersion> INTENT_VERSION_SERIALIZER = (serializer, version) -> {
        if (version instanceof IntentVersion.V0) {
            serializer.writeU8((byte) 0); // V0 variant
        } else {
            throw new IllegalArgumentException("Unknown IntentVersion type: " + version.getClass());
        }
    };
    
    /**
     * AppId serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<AppId> APP_ID_SERIALIZER = (serializer, appId) -> {
        if (appId instanceof AppId.Sui) {
            serializer.writeU8((byte) 0); // Sui variant
        } else {
            throw new IllegalArgumentException("Unknown AppId type: " + appId.getClass());
        }
    };
    
    /**
     * Intent serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<Intent> INTENT_SERIALIZER = (serializer, intent) -> {
        INTENT_SCOPE_SERIALIZER.serialize(serializer, intent.getScope());
        INTENT_VERSION_SERIALIZER.serialize(serializer, intent.getVersion());
        APP_ID_SERIALIZER.serialize(serializer, intent.getAppId());
    };
    
    /**
     * IntentMessage serializer TransactionData
     */
    public static final BcsSerializer.BcsTypeSerializer<IntentMessage<TransactionData>> INTENT_MESSAGE_SERIALIZER = (serializer, message) -> {
        INTENT_SERIALIZER.serialize(serializer, message.getIntent());
        TRANSACTION_DATA_SERIALIZER.serialize(serializer, message.getValue());
    };

    /**
     * IntentMessage serializer String
     */
    public static final BcsSerializer.BcsTypeSerializer<IntentMessage<String>> INTENT_MESSAGE_STR_SERIALIZER = (serializer, message) -> {
        INTENT_SERIALIZER.serialize(serializer, message.getIntent());
        serializer.writeString(message.getValue());
    };

    /**
     * CompressedSignature serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<CompressedSignature> COMPRESSED_SIGNATURE_SERIALIZER = (serializer, signature) -> {
        if (signature instanceof CompressedSignature.ED25519) {
            serializer.writeU8((byte) 0); // ED25519 variant
            serializer.writeBytes(((CompressedSignature.ED25519) signature).getSignature());
        } else if (signature instanceof CompressedSignature.Secp256k1) {
            serializer.writeU8((byte) 1); // Secp256k1 variant
            serializer.writeBytes(((CompressedSignature.Secp256k1) signature).getSignature());
        } else if (signature instanceof CompressedSignature.Secp256r1) {
            serializer.writeU8((byte) 2); // Secp256r1 variant
            serializer.writeBytes(((CompressedSignature.Secp256r1) signature).getSignature());
        } else if (signature instanceof CompressedSignature.ZkLogin) {
            serializer.writeU8((byte) 3); // ZkLogin variant
            serializer.writeBytes(((CompressedSignature.ZkLogin) signature).getSignature());
        } else {
            throw new IllegalArgumentException("Unknown CompressedSignature type: " + signature.getClass());
        }
    };
    
    /**
     * PublicKey serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<PublicKey> PUBLIC_KEY_SERIALIZER = (serializer, publicKey) -> {
        if (publicKey instanceof PublicKey.ED25519) {
            serializer.writeU8((byte) 0); // ED25519 variant
            serializer.writeBytes(((PublicKey.ED25519) publicKey).getPublicKey());
        } else if (publicKey instanceof PublicKey.Secp256k1) {
            serializer.writeU8((byte) 1); // Secp256k1 variant
            serializer.writeBytes(((PublicKey.Secp256k1) publicKey).getPublicKey());
        } else if (publicKey instanceof PublicKey.Secp256r1) {
            serializer.writeU8((byte) 2); // Secp256r1 variant
            serializer.writeBytes(((PublicKey.Secp256r1) publicKey).getPublicKey());
        } else if (publicKey instanceof PublicKey.ZkLogin) {
            serializer.writeU8((byte) 3); // ZkLogin variant
            serializer.writeBytes(((PublicKey.ZkLogin) publicKey).getPublicKey());
        } else {
            throw new IllegalArgumentException("Unknown PublicKey type: " + publicKey.getClass());
        }
    };
    
    /**
     * MultiSigPkMap serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<MultiSigPkMap> MULTI_SIG_PK_MAP_SERIALIZER = (serializer, pkMap) -> {
        PUBLIC_KEY_SERIALIZER.serialize(serializer, pkMap.getPubKey());
        serializer.writeU8((byte) pkMap.getWeight());
    };
    
    /**
     * MultiSigPublicKey serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<MultiSigPublicKey> MULTI_SIG_PUBLIC_KEY_SERIALIZER = (serializer, publicKey) -> {
        serializer.writeVector(publicKey.getPkMap(), MULTI_SIG_PK_MAP_SERIALIZER);
        serializer.writeU8((byte) publicKey.getThreshold());
    };
    
    /**
     * MultiSig serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<MultiSig> MULTI_SIG_SERIALIZER = (serializer, multiSig) -> {
        serializer.writeVector(multiSig.getSigs(), COMPRESSED_SIGNATURE_SERIALIZER);
        serializer.writeU8((byte) multiSig.getBitmap());
        MULTI_SIG_PUBLIC_KEY_SERIALIZER.serialize(serializer, multiSig.getMultisigPk());
    };
    
    /**
     * SenderSignedTransaction serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<SenderSignedTransaction> SENDER_SIGNED_TRANSACTION_SERIALIZER = (serializer, tx) -> {
        INTENT_MESSAGE_SERIALIZER.serialize(serializer, tx.getIntentMessage());
        serializer.writeVector(tx.getTxSignatures(), (s, bytes) -> s.writeBytes(bytes));
    };
    
    /**
     * PasskeyAuthenticator serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<PasskeyAuthenticator> PASSKEY_AUTHENTICATOR_SERIALIZER = (serializer, auth) -> {
        serializer.writeBytes(auth.getAuthenticatorData());
        serializer.writeString(auth.getClientDataJson());
        serializer.writeBytes(auth.getUserSignature());
    };
    
    /**
     * Owner serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<Owner> OWNER_SERIALIZER = (serializer, owner) -> {
        if (owner instanceof Owner.AddressOwner) {
            serializer.writeU8((byte) 0); // AddressOwner variant
            ADDRESS_SERIALIZER.serialize(serializer, ((Owner.AddressOwner) owner).getAddress());
        } else if (owner instanceof Owner.ObjectOwner) {
            serializer.writeU8((byte) 1); // ObjectOwner variant
            ADDRESS_SERIALIZER.serialize(serializer, ((Owner.ObjectOwner) owner).getAddress());
        } else if (owner instanceof Owner.Shared) {
            serializer.writeU8((byte) 2); // Shared variant
            serializer.writeU64(((Owner.Shared) owner).getInitialSharedVersion());
        } else if (owner instanceof Owner.Immutable) {
            serializer.writeU8((byte) 3); // Immutable variant
        } else if (owner instanceof Owner.ConsensusV2) {
            serializer.writeU8((byte) 4); // ConsensusV2 variant
            Owner.ConsensusV2 consensus = (Owner.ConsensusV2) owner;
            // serializeAuthenticator
            ADDRESS_SERIALIZER.serialize(serializer, consensus.getAuthenticator().getSingleOwner());
            serializer.writeU64(consensus.getStartVersion());
        } else {
            throw new IllegalArgumentException("Unknown Owner type: " + owner.getClass());
        }
    };
    
    /**
     * Validate if the Sui address is valid.
     */
    public static boolean isValidSuiAddress(String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }
        
        String normalized = ObjectIdUtil.normalizeSuiAddress(address);
        if (!normalized.startsWith("0x")) {
            return false;
        }
        
        String hex = normalized.substring(2);
        if (hex.length() != 64) {
            return false;
        }
        
        try {
            // Validate if it is a valid hexadecimal string.
            new BigInteger(hex, 16);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Register serializer to cache.
     */
    public static <T> void registerSerializer(Class<T> clazz, BcsSerializer.BcsTypeSerializer<T> serializer) {
        SERIALIZER_CACHE.put(clazz, serializer);
    }
    
    /**
     * Get cached serializer.
     */
    @SuppressWarnings("unchecked")
    public static <T> BcsSerializer.BcsTypeSerializer<T> getSerializer(Class<T> clazz) {
        return (BcsSerializer.BcsTypeSerializer<T>) SERIALIZER_CACHE.get(clazz);
    }
    
    /**
     * Serialize object to Base64 string.
     */
    public static <T> String serializeToBase64(T obj, BcsSerializer.BcsTypeSerializer<T> serializer) throws IOException {
        BcsSerializer bcsSerializer = new BcsSerializer();
        serializer.serialize(bcsSerializer, obj);
        return bcsSerializer.toBase64();
    }
    
    /**
     * Deserialize object from Base64 string.
     */
    public static <T> T deserializeFromBase64(String base64, Function<BcsDeserializer, T> deserializer) {
        byte[] data = Base64.decode(base64);
        BcsDeserializer bcsDeserializer = new BcsDeserializer(data);
        return deserializer.apply(bcsDeserializer);
    }

    /**
     * Serialize object to Hex string.
     */
    public static <T> String serializeToHex(T obj, BcsSerializer.BcsTypeSerializer<T> serializer) throws IOException {
        BcsSerializer bcsSerializer = new BcsSerializer();
        serializer.serialize(bcsSerializer, obj);
        return bcsSerializer.toHex();
    }

    /**
     * Deserialize object from Hex string.
     */
    public static <T> T deserializeFromHex(String hex, Function<BcsDeserializer, T> deserializer) {
        byte[] data = Hex.decode(hex);
        BcsDeserializer bcsDeserializer = new BcsDeserializer(data);
        return deserializer.apply(bcsDeserializer);
    }
} 