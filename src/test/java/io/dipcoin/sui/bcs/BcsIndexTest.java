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
import io.dipcoin.sui.bcs.types.arg.object.ObjectArg;
import io.dipcoin.sui.bcs.types.arg.object.ObjectArgImmOrOwnedObject;
import io.dipcoin.sui.bcs.types.arg.object.ObjectArgSharedObject;
import io.dipcoin.sui.bcs.types.arg.object.SharedObjectRef;
import io.dipcoin.sui.bcs.types.auth.PasskeyAuthenticator;
import io.dipcoin.sui.bcs.types.gas.GasData;
import io.dipcoin.sui.bcs.types.gas.SuiObjectRef;
import io.dipcoin.sui.bcs.types.intent.*;
import io.dipcoin.sui.bcs.types.owner.Owner;
import io.dipcoin.sui.bcs.types.signature.*;
import io.dipcoin.sui.bcs.types.tag.*;
import io.dipcoin.sui.bcs.types.transaction.*;
import io.dipcoin.sui.constant.PerpPackage;
import io.dipcoin.sui.constant.TxConfig;
import io.dipcoin.sui.constant.WalletKeyPair;
import io.dipcoin.sui.constant.model.SuiWallet;
import io.dipcoin.sui.constant.perp.PackageConfig;
import io.dipcoin.sui.crypto.SuiKeyPair;
import io.dipcoin.sui.protocol.SuiClientTest;
import io.dipcoin.sui.protocol.http.request.UnsafeMoveCall;
import io.dipcoin.sui.pyth.model.PythNetwork;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author : Same
 * @datetime : 2025/7/11 19:20
 * @Description : BcsRegistry unit tests, test serialization functionality for all new types.
 */
@Slf4j
public class BcsIndexTest extends SuiClientTest {

    private static final SuiWallet suiWallet = WalletKeyPair.WALLETS.get(WalletKeyPair.OPERATOR);
    private static final SuiKeyPair user = suiWallet.getKeyPair();
    
    private SuiObjectRef testObjectRefDeposit;
    private SuiObjectRef testObjectRefTrade;
    private SuiObjectRef testObjectRefUpdatePrice;
    private SharedObjectRef testSharedObjectRef;
    private GasData testGasDataDeposit;
    private GasData testGasDataTrade;
    private GasData testGasDataUpdatePrice;

    @BeforeEach
    void setUpBcs() {
        // Invoke the parent class initialization first.
        super.setUp();
        // Create a Sui object reference for testing purposes.
        testObjectRefDeposit = new SuiObjectRef(
                suiWallet.getGasObjectId(),
                500452832L,
            "FZSJfo8uZMLjeppm4XSd1peiEnox8FYxMxPbZVmuUjqw"
        );
        // Create a Sui object reference for testing purposes.
        testObjectRefTrade = new SuiObjectRef(
                suiWallet.getGasObjectId(),
                500452833L,
            "2Vj5WEfsK5tt2t5o8QFNxzfs2MZATwDmrn5Yz8EyLiPk"
        );
        // Create a Sui object reference for testing purposes.
        testObjectRefUpdatePrice = new SuiObjectRef(
                suiWallet.getGasObjectId(),
                506327216L,
            "6q8LNkbpeodJXMdyKY14W6uygJ7Yf5pvuF1XfecBNbX4"
        );

        // Create a shared object reference for testing purposes.
        testSharedObjectRef = new SharedObjectRef(
            "0x000000000000000000000000000000000000000000000000fedcba0987654321",
            2L,
            true
        );
        
        // Create test gas data.
        testGasDataDeposit = new GasData(
            Arrays.asList(testObjectRefDeposit),
                user.address(),
            1000L,
                TxConfig.GAS_BUDGET
        );

        // Create test gas data.
        testGasDataTrade = new GasData(
            Arrays.asList(testObjectRefTrade),
                user.address(),
            1000L,
                TxConfig.GAS_BUDGET
        );

        // Create test gas data.
        testGasDataUpdatePrice = new GasData(
            Arrays.asList(testObjectRefUpdatePrice),
                user.address(),
            1000L,
                TxConfig.GAS_BUDGET
        );
    }
    
    @Test
    void testBasicTypesSerialization() throws IOException {
        log.info("Test primitive type serialization.");
        
        // test U8
        String u8Base64 = BcsRegistry.serializeToBase64((byte) 255, BcsRegistry.U8_SERIALIZER);
        log.info("U8: 255 -> {}", u8Base64);
        assertNotNull(u8Base64);
        assertTrue(u8Base64.length() > 0);
        
        // test U16
        String u16Base64 = BcsRegistry.serializeToBase64((short) 65535, BcsRegistry.U16_SERIALIZER);
        log.info("U16: 65535 -> {}", u16Base64);
        assertNotNull(u16Base64);
        assertTrue(u16Base64.length() > 0);
        
        // test U32
        String u32Base64 = BcsRegistry.serializeToBase64(2147483647, BcsRegistry.U32_SERIALIZER);
        log.info("U32: 2147483647 -> {}", u32Base64);
        assertNotNull(u32Base64);
        assertTrue(u32Base64.length() > 0);
        
        // test U64
        String u64Base64 = BcsRegistry.serializeToBase64(Long.MAX_VALUE, BcsRegistry.U64_SERIALIZER);
        log.info("U64: {} -> {}", Long.MAX_VALUE, u64Base64);
        assertNotNull(u64Base64);
        assertTrue(u64Base64.length() > 0);
        
        // test Bool
        String boolBase64 = BcsRegistry.serializeToBase64(true, BcsRegistry.BOOL_SERIALIZER);
        log.info("Bool: true -> {}", boolBase64);
        assertNotNull(boolBase64);
        assertTrue(boolBase64.length() > 0);
        
        // test String
        String stringBase64 = BcsRegistry.serializeToBase64("Hello World", BcsRegistry.STRING_SERIALIZER);
        log.info("String: 'Hello World' -> {}", stringBase64);
        assertNotNull(stringBase64);
        assertTrue(stringBase64.length() > 0);
    }
    
    @Test
    void testAddressAndObjectSerialization() throws IOException {
        log.info("Test address and object serialization.");
        
        // test address
        String address = "0x0000000000000000000000000000000000000000000000001234567890abcdef";
        String addressBase64 = BcsRegistry.serializeToBase64(address, BcsRegistry.ADDRESS_SERIALIZER);
        log.info("Address: {} -> {}", address, addressBase64);
        assertNotNull(addressBase64);
        assertTrue(addressBase64.length() > 0);
        
        // test SuiObjectRef
        String objectRefBase64 = BcsRegistry.serializeToBase64(testObjectRefDeposit, BcsRegistry.SUI_OBJECT_REF_SERIALIZER);
        log.info("SuiObjectRef: {} -> {}", testObjectRefDeposit, objectRefBase64);
        assertNotNull(objectRefBase64);
        assertTrue(objectRefBase64.length() > 0);
        
        // test SharedObjectRef
        String sharedObjectRefBase64 = BcsRegistry.serializeToBase64(testSharedObjectRef, BcsRegistry.SHARED_OBJECT_REF_SERIALIZER);
        log.info("SharedObjectRef: {} -> {}", testSharedObjectRef, sharedObjectRefBase64);
        assertNotNull(sharedObjectRefBase64);
        assertTrue(sharedObjectRefBase64.length() > 0);
        
        // test ObjectArg
        ObjectArg objectArg = new ObjectArgImmOrOwnedObject(testObjectRefDeposit);
        String objectArgBase64 = BcsRegistry.serializeToBase64(objectArg, BcsRegistry.OBJECT_ARG_SERIALIZER);
        log.info("ObjectArg: {} -> {}", objectArg, objectArgBase64);
        assertNotNull(objectArgBase64);
        assertTrue(objectArgBase64.length() > 0);
        
        // test CallArg
        CallArg callArg = new CallArgObjectArg(objectArg);
        String callArgBase64 = BcsRegistry.serializeToBase64(callArg, BcsRegistry.CALL_ARG_SERIALIZER);
        log.info("CallArg: {} -> {}", callArg, callArgBase64);
        assertNotNull(callArgBase64);
        assertTrue(callArgBase64.length() > 0);
    }
    
    @Test
    void testTypeTagSerialization() throws IOException {
        log.info("Test type tag serialization.");
        
        // Test primitive type tags.
        TypeTag boolTag = TypeTagBool.INSTANCE;
        String boolTagBase64 = BcsRegistry.serializeToBase64(boolTag, BcsRegistry.TYPE_TAG_SERIALIZER);
        log.info("TypeTag Bool: {} -> {}", boolTag, boolTagBase64);
        assertNotNull(boolTagBase64);
        assertTrue(boolTagBase64.length() > 0);
        
        TypeTag u64Tag = TypeTagU64.INSTANCE;
        String u64TagBase64 = BcsRegistry.serializeToBase64(u64Tag, BcsRegistry.TYPE_TAG_SERIALIZER);
        log.info("TypeTag U64: {} -> {}", u64Tag, u64TagBase64);
        assertNotNull(u64TagBase64);
        assertTrue(u64TagBase64.length() > 0);
        
        // Test vector type tags.
        TypeTag vectorTag = new TypeTagVector(u64Tag);
        String vectorTagBase64 = BcsRegistry.serializeToBase64(vectorTag, BcsRegistry.TYPE_TAG_SERIALIZER);
        log.info("TypeTag Vector: {} -> {}", vectorTag, vectorTagBase64);
        assertNotNull(vectorTagBase64);
        assertTrue(vectorTagBase64.length() > 0);
        
        // Test struct type tags.
        TypeTagStructTag structTag = new TypeTagStructTag(
            "0x0000000000000000000000000000000000000000000000001234567890abcdef",
            "test_module",
            "TestStruct",
            List.of(boolTag, u64Tag)
        );
        String structTagBase64 = BcsRegistry.serializeToBase64(structTag, BcsRegistry.STRUCT_TAG_SERIALIZER);
        log.info("TypeTag Struct: {} -> {}", structTag, structTagBase64);
        assertNotNull(structTagBase64);
        assertTrue(structTagBase64.length() > 0);
    }
    
    @Test
    void testTransactionSerialization() throws IOException {
        log.info("Test transaction serialization.");
        
        // test Argument
        Argument gasCoinArg = Argument.GasCoin.INSTANCE;
        String gasCoinArgBase64 = BcsRegistry.serializeToBase64(gasCoinArg, BcsRegistry.ARGUMENT_SERIALIZER);
        log.info("Argument GasCoin: {} -> {}", gasCoinArg, gasCoinArgBase64);
        assertNotNull(gasCoinArgBase64);
        assertTrue(gasCoinArgBase64.length() > 0);
        
        Argument inputArg = Argument.ofInput(1);
        String inputArgBase64 = BcsRegistry.serializeToBase64(inputArg, BcsRegistry.ARGUMENT_SERIALIZER);
        log.info("Argument Input: {} -> {}", inputArg, inputArgBase64);
        assertNotNull(inputArgBase64);
        assertTrue(inputArgBase64.length() > 0);
        
        // test ProgrammableMoveCall
        ProgrammableMoveCall depositMoveCall = new ProgrammableMoveCall(
            "0x0000000000000000000000000000000000000000000000001234567890abcdef",
            "test_module",
            "test_function",
            Arrays.asList(new TypeTagBool(), new TypeTagU64()),
            Arrays.asList(gasCoinArg, inputArg)
        );
        String depositMoveCallBase64 = BcsRegistry.serializeToBase64(depositMoveCall, BcsRegistry.PROGRAMMABLE_MOVE_CALL_SERIALIZER);
        log.info("ProgrammableMoveCall: {} -> {}", depositMoveCall, depositMoveCallBase64);
        assertNotNull(depositMoveCallBase64);
        assertTrue(depositMoveCallBase64.length() > 0);
        
        // test Command
        Command depositMoveCallCommand = new Command.MoveCall(depositMoveCall);
        String depositMoveCallCommandBase64 = BcsRegistry.serializeToBase64(depositMoveCallCommand, BcsRegistry.COMMAND_SERIALIZER);
        log.info("Command MoveCall: {} -> {}", depositMoveCallCommand, depositMoveCallCommandBase64);
        assertNotNull(depositMoveCallCommandBase64);
        assertTrue(depositMoveCallCommandBase64.length() > 0);
        
        // test ProgrammableTransaction
        ProgrammableTransaction programmableTx = new ProgrammableTransaction();
        programmableTx.addInput(new CallArgPure("test".getBytes(), PureBcs.BasePureType.VECTOR_U8));
        programmableTx.addCommands(Arrays.asList(depositMoveCallCommand));
        String programmableTxBase64 = BcsRegistry.serializeToBase64(programmableTx, BcsRegistry.PROGRAMMABLE_TRANSACTION_SERIALIZER);
        log.info("ProgrammableTransaction: {} -> {}", programmableTx, programmableTxBase64);
        assertNotNull(programmableTxBase64);
        assertTrue(programmableTxBase64.length() > 0);
        
        // test TransactionKind
        TransactionKind programmableKind = new TransactionKind.ProgrammableTransaction(programmableTx);
        String programmableKindBase64 = BcsRegistry.serializeToBase64(programmableKind, BcsRegistry.TRANSACTION_KIND_SERIALIZER);
        log.info("TransactionKind ProgrammableTransaction: {} -> {}", programmableKind, programmableKindBase64);
        assertNotNull(programmableKindBase64);
        assertTrue(programmableKindBase64.length() > 0);
        
        // test TransactionExpiration
        TransactionExpiration noneExpiration = TransactionExpiration.None.INSTANCE;
        String noneExpirationBase64 = BcsRegistry.serializeToBase64(noneExpiration, BcsRegistry.TRANSACTION_EXPIRATION_SERIALIZER);
        log.info("TransactionExpiration None: {} -> {}", noneExpiration, noneExpirationBase64);
        assertNotNull(noneExpirationBase64);
        assertTrue(noneExpirationBase64.length() > 0);
        
        TransactionExpiration epochExpiration = new TransactionExpiration.Epoch(1000L);
        String epochExpirationBase64 = BcsRegistry.serializeToBase64(epochExpiration, BcsRegistry.TRANSACTION_EXPIRATION_SERIALIZER);
        log.info("TransactionExpiration Epoch: {} -> {}", epochExpiration, epochExpirationBase64);
        assertNotNull(epochExpirationBase64);
        assertTrue(epochExpirationBase64.length() > 0);
        
        // test TransactionDataV1
        TransactionDataV1 transactionDataV1 = new TransactionDataV1(
            programmableKind,
            "0x0000000000000000000000000000000000000000000000001234567890abcdef",
                testGasDataDeposit,
                noneExpiration
        );
        String transactionDataV1Base64 = BcsRegistry.serializeToBase64(transactionDataV1, BcsRegistry.TRANSACTION_DATA_V1_SERIALIZER);
        log.info("TransactionDataV1: {} -> {}", transactionDataV1, transactionDataV1Base64);
        assertNotNull(transactionDataV1Base64);
        assertTrue(transactionDataV1Base64.length() > 0);
        
        // test TransactionData
        TransactionData transactionData = new TransactionData.V1(transactionDataV1);
        String transactionDataBase64 = BcsRegistry.serializeToBase64(transactionData, BcsRegistry.TRANSACTION_DATA_SERIALIZER);
        log.info("TransactionData: {} -> {}", transactionData, transactionDataBase64);
        assertNotNull(transactionDataBase64);
        assertTrue(transactionDataBase64.length() > 0);
    }

    @Test
    void testMoveCallDepositSerialization() throws IOException {
        log.info("Test deposit moveCall serialize.");
        SuiWallet operator = WalletKeyPair.WALLETS.get(WalletKeyPair.OPERATOR);
        SuiKeyPair operatorKeyPair = operator.getKeyPair();

        PackageConfig depositConfig = PerpPackage.MOVE_CALL.get(PerpPackage.DEPOSIT);
        List<Object> arguments = Arrays.asList(
                PerpPackage.TRADE_PROJECT_CONFIG_ID,
                PerpPackage.BANK_ID,
                PerpPackage.TX_INDEXER_ID,
//                Base64.encode(Ed25519KeyPair.generate().privateKey()), // tx_hash
                new byte[]{73,83,57,104,113,85,68,73,76,78,43,56,74,51,110,103,107,122,69,50,69,75,88,43,103,88,122,120,78,65,99,105,75,97,73,109,112,79,75,119,77,55,99,61}, // tx_hash
                user.address(),
                new BigInteger("10000").multiply(BigInteger.TEN.pow(6)), // amount
                suiWallet.getUsdcObjectId()  // coin objectId
        );

        // Assemble the request.
        UnsafeMoveCall depositMoveCall = new UnsafeMoveCall();
        depositMoveCall.setSigner(user.address());
        depositMoveCall.setPackageObjectId(PerpPackage.OBJECT_ID);
        depositMoveCall.setModule(depositConfig.getModule());
        depositMoveCall.setFunction(depositConfig.getFunction());
        depositMoveCall.setTypeArguments(PerpPackage.TYPE_TAG);
        depositMoveCall.setArguments(arguments);
        depositMoveCall.setGas(suiWallet.getGasObjectId());
        depositMoveCall.setGasBudget(TxConfig.GAS_BUDGET);
        
        // serialize result
        // AAAHAQGnhgzinTPKWU2IJR0RMiNYzAs8ctSjOCH4q+ENnZ74tGq5VhwAAAAAAAEBZivknkl3Lp98rdtt20FOMNVwkHWikvbdBkmyBuY4UbFruVYcAAAAAAEBAa/xpftRPuGb6T03f80Iv5/86n0vk6NpM2KxHc1LK6o8arlWHAAAAAABAC0sSVM5aHFVRElMTis4SjNuZ2t6RTJFS1grZ1h6eE5BY2lLYUltcE9Ld003Yz0AIKhVP4Y3dfxH7r+vpC/KG3JZCFnq0PrmULa64uVuSF5jAAgA5AtUAgAAAAEAgSUy/vt/1+vAOV795gzVjSDqPH7hx7pPbgoN++rkWxvgTdQdAAAAACCVT5G2Bpl1+XC61x+Z0iuyKnhNSCZIKq3ucpfdNtvqTAEAUn/TzorViGWtcggzUqGfc9b7X6EUvVuKFFGnj+2yG28EYmFuawdkZXBvc2l0AQdSf9POitWIZa1yCDNSoZ9z1vtfoRS9W4oUUaeP7bIbbwRjb2luBENPSU4ABwEAAAEBAAECAAEDAAEEAAEFAAEGAKhVP4Y3dfxH7r+vpC/KG3JZCFnq0PrmULa64uVuSF5jAUWK3JWc8y49/a5zbM1+OzaBZuD+5tTCUKQnhCk4Iett4E3UHQAAAAAg2FLQnXKVLcb4rQ8wDezNcsrwFkRhSo4d6HZ+K8I+fPaoVT+GN3X8R+6/r6QvyhtyWQhZ6tD65lC2uuLlbkheY+gDAAAAAAAAAOH1BQAAAAAA

        Argument inputArg = Argument.ofInput(0);
        String inputArgBase64 = BcsRegistry.serializeToBase64(inputArg, BcsRegistry.ARGUMENT_SERIALIZER);
        log.info("Argument Input: {} -> {}", inputArg, inputArgBase64);
        assertNotNull(inputArgBase64);
        assertTrue(inputArgBase64.length() > 0);

        // test ProgrammableTransaction
        ProgrammableTransaction programmableTx = new ProgrammableTransaction();

        // test ProgrammableMoveCall
        ProgrammableMoveCall moveCall = new ProgrammableMoveCall(
                PerpPackage.OBJECT_ID,
                depositConfig.getModule(),
                depositConfig.getFunction(),
                TypeTagSerializer.parseStructTypeArgs(PerpPackage.TYPE_TAG.get(0), true),
                Arrays.asList(
                        // arg0
                        Argument.ofInput(programmableTx.addInput(new CallArgObjectArg(new ObjectArgSharedObject(new SharedObjectRef(
                                PerpPackage.TRADE_PROJECT_CONFIG_ID, 475445610L, false))))),
                        // arg1
                        Argument.ofInput(programmableTx.addInput(new CallArgObjectArg(new ObjectArgSharedObject(new SharedObjectRef(
                                PerpPackage.BANK_ID, 475445611L, true))))),
                        // arg2
                        Argument.ofInput(programmableTx.addInput(new CallArgObjectArg(new ObjectArgSharedObject(new SharedObjectRef(
                                PerpPackage.TX_INDEXER_ID, 475445610L, true))))),
                        // arg3
                        Argument.ofInput(programmableTx.addInput(new CallArgPure(new byte[]{73,83,57,104,113,85,68,73,76,78,43,56,74,51,110,103,107,122,69,50,69,75,88,43,103,88,122,120,78,65,99,105,75,97,73,109,112,79,75,119,77,55,99,61},
                                PureBcs.BasePureType.VECTOR_U8))),
                        // arg4
                        Argument.ofInput(programmableTx.addInput(new CallArgPure(user.address(),
                                PureBcs.BasePureType.ADDRESS))),
                        // arg5
                        Argument.ofInput(programmableTx.addInput(new CallArgPure(new BigInteger("10000").multiply(BigInteger.TEN.pow(6)).longValue(),
                                PureBcs.BasePureType.U64))),
                        // arg6
                        Argument.ofInput(programmableTx.addInput(new CallArgObjectArg(new ObjectArgImmOrOwnedObject(new SuiObjectRef(
                                suiWallet.getUsdcObjectId(), 500452832L, "B3r7s9f1WaYhwJbWcNiWt2mUyNXnsPEKGbXQrhvMXvB5"))))))
        );
        String depositMoveCallBase64 = BcsRegistry.serializeToBase64(moveCall, BcsRegistry.PROGRAMMABLE_MOVE_CALL_SERIALIZER);
        log.info("ProgrammableMoveCall: {} -> {}", moveCall, depositMoveCallBase64);
        assertNotNull(depositMoveCallBase64);
        assertTrue(depositMoveCallBase64.length() > 0);

        // test Command
        Command depositMoveCallCommand = new Command.MoveCall(moveCall);
        String depositMoveCallCommandBase64 = BcsRegistry.serializeToBase64(depositMoveCallCommand, BcsRegistry.COMMAND_SERIALIZER);
        log.info("Command MoveCall: {} -> {}", depositMoveCallCommand, depositMoveCallCommandBase64);
        assertNotNull(depositMoveCallCommandBase64);
        assertTrue(depositMoveCallCommandBase64.length() > 0);

        List<Command> commands = new ArrayList<>(List.of(
                depositMoveCallCommand
        ));
        programmableTx.addCommands(commands);
        
        String programmableTxBase64 = BcsRegistry.serializeToBase64(programmableTx, BcsRegistry.PROGRAMMABLE_TRANSACTION_SERIALIZER);
        log.info("ProgrammableTransaction: {} -> {}", programmableTx, programmableTxBase64);
        assertNotNull(programmableTxBase64);
        assertTrue(programmableTxBase64.length() > 0);

        // test TransactionKind
        TransactionKind programmableKind = new TransactionKind.ProgrammableTransaction(programmableTx);
        String programmableKindBase64 = BcsRegistry.serializeToBase64(programmableKind, BcsRegistry.TRANSACTION_KIND_SERIALIZER);
        log.info("TransactionKind ProgrammableTransaction: {} -> {}", programmableKind, programmableKindBase64);
        assertNotNull(programmableKindBase64);
        assertTrue(programmableKindBase64.length() > 0);

        // test TransactionExpiration
        TransactionExpiration noneExpiration = TransactionExpiration.None.INSTANCE;
        String noneExpirationBase64 = BcsRegistry.serializeToBase64(noneExpiration, BcsRegistry.TRANSACTION_EXPIRATION_SERIALIZER);
        log.info("TransactionExpiration None: {} -> {}", noneExpiration, noneExpirationBase64);
        assertNotNull(noneExpirationBase64);
        assertTrue(noneExpirationBase64.length() > 0);

        TransactionExpiration epochExpiration = new TransactionExpiration.Epoch(1000L);
        String epochExpirationBase64 = BcsRegistry.serializeToBase64(epochExpiration, BcsRegistry.TRANSACTION_EXPIRATION_SERIALIZER);
        log.info("TransactionExpiration Epoch: {} -> {}", epochExpiration, epochExpirationBase64);
        assertNotNull(epochExpirationBase64);
        assertTrue(epochExpirationBase64.length() > 0);

        // test TransactionDataV1
        TransactionDataV1 transactionDataV1 = new TransactionDataV1(
            programmableKind,
                user.address(),
                testGasDataDeposit,
                noneExpiration
        );
        String transactionDataV1Base64 = BcsRegistry.serializeToBase64(transactionDataV1, BcsRegistry.TRANSACTION_DATA_V1_SERIALIZER);
        log.info("TransactionDataV1: {} -> {}", transactionDataV1, transactionDataV1Base64);
        assertNotNull(transactionDataV1Base64);
        assertTrue(transactionDataV1Base64.length() > 0);

        // test TransactionData
        TransactionData transactionData = new TransactionData.V1(transactionDataV1);
        String transactionDataBase64 = BcsRegistry.serializeToBase64(transactionData, BcsRegistry.TRANSACTION_DATA_SERIALIZER);
        log.info("\nTransactionData: {} -> \n{}", transactionData, transactionDataBase64);

        String correctBytes = "AAAHAQEFpjDDbopsuf+Z4tJZXlXscNACqAaakMLRusC/oSJx+mq5VhwAAAAAAAEBFr6TAGo87W+i3eQoybhBi0mG79SrxpgNf0Nnu/1jg1NruVYcAAAAAAEBAa7RNSw/bypE/VITUPU6mPZ11LB8w2kWYH6uJMJlCpy5arlWHAAAAAABAC0sSVM5aHFVRElMTis4SjNuZ2t6RTJFS1grZ1h6eE5BY2lLYUltcE9Ld003Yz0AIOY/TRK88UXFEPJ4jwv4z4GnzVA11aUC2Ea5UXRXlzwdAAgA5AtUAgAAAAEA/X6sFkvhHEMVPzEKgHZ4hBpAr6RRzpMaSAiAaciF5l3gTdQdAAAAACCVT5G2Bpl1+XC61x+Z0iuyKnhNSCZIKq3ucpfdNtvqTAEAARSx1GVqxCqVI9occkHwKRkY+VF/0w8+boS5/Vs+NzAEYmFuawdkZXBvc2l0AQcfJ4iRi2CZWckFKh8AxJdldSrLJNmZl6ECkDvn2hjdDQRjb2luBENPSU4ABwEAAAEBAAECAAEDAAEEAAEFAAEGAOY/TRK88UXFEPJ4jwv4z4GnzVA11aUC2Ea5UXRXlzwdAcKoyzxURmdSL+NF9urISxTFnxOgF9y9qPSlbADsnaaA4E3UHQAAAAAg2FLQnXKVLcb4rQ8wDezNcsrwFkRhSo4d6HZ+K8I+fPbmP00SvPFFxRDyeI8L+M+Bp81QNdWlAthGuVF0V5c8HegDAAAAAAAAAOH1BQAAAAAA";
        log.info("\nCorrect binary: \n{} \nMy binary:\n{}", Arrays.toString(this.trans(Base64.decode(correctBytes))), Arrays.toString(this.trans(Base64.decode(transactionDataBase64))));
        log.info("\nTemporary:\n{} -> \n{}", operator.getGasObjectId(), Arrays.toString(this.trans(Hex.decode(operator.getGasObjectId().replace("0x", "")))));
        log.info("\nTemporary:\n0x458adc959cf32e3dfdae736ccd7e3b368166e0fee6d4c250a42784293821eb6d -> \n{}", Arrays.toString(this.trans(Hex.decode("458adc959cf32e3dfdae736ccd7e3b368166e0fee6d4c250a42784293821eb6d"))));
        assertNotNull(transactionDataBase64);
        assertTrue(transactionDataBase64.length() > 0);
        assertEquals(replaceBytes(correctBytes), transactionDataBase64);

        // 4. on chain
        if (ENABLE_SEND) {
            this.testExecuteTransactionBlock(transactionDataBase64, operatorKeyPair);
        }
    }

    @Test
    void testPtbUpdatePriceSerialization() throws IOException {
        log.info("test updatePrice PTB serialize");

        // ----------------------- operator trade -----------------------
        SuiWallet operator = WalletKeyPair.WALLETS.get(WalletKeyPair.OPERATOR);
        SuiKeyPair operatorKeyPair = operator.getKeyPair();
        String feedId = "0xf9c0172ba10dfa4d19088d94f5bf61d3b54d5bd7483a322a982e1373ee8ea31b";

        // test ProgrammableMoveCall
        ProgrammableMoveCall moveCall = new ProgrammableMoveCall(
                "0xb5f4bf7d29a8e82eb5de521cf556f1953c9fdd3d73e7f371a156c6e1ee64f24b",
                "main",
                "use_pyth_price",
                new ArrayList<>(),
                Arrays.asList(Argument.ofInput(2),
                        Argument.ofInput(6)
                ));

        // test Command
        Command useUpdateMoveCall = new Command.MoveCall(moveCall);
        String depositMoveCallCommandBase64 = BcsRegistry.serializeToBase64(useUpdateMoveCall, BcsRegistry.COMMAND_SERIALIZER);
        log.info("Command MoveCall: \n{} -> \n{} -> \n{}", useUpdateMoveCall, trans(Base64.decode(depositMoveCallCommandBase64)), depositMoveCallCommandBase64);
        assertNotNull(depositMoveCallCommandBase64);
        assertTrue(depositMoveCallCommandBase64.length() > 0);

        // test ProgrammableTransaction
        ProgrammableTransaction programmableTx = pythClient.updatePrice(feedId, PythNetwork.TESTNET);
        programmableTx.addCommand(useUpdateMoveCall);

        String programmableTxBase64 = BcsRegistry.serializeToBase64(programmableTx, BcsRegistry.PROGRAMMABLE_TRANSACTION_SERIALIZER);
        log.info("ProgrammableTransaction: {}\n -> {}\n -> {}", programmableTx, Arrays.toString(this.trans(Base64.decode(programmableTxBase64))), programmableTxBase64);
        assertNotNull(programmableTxBase64);
        assertTrue(programmableTxBase64.length() > 0);

        // test TransactionKind
        TransactionKind programmableKind = new TransactionKind.ProgrammableTransaction(programmableTx);
        String programmableKindBase64 = BcsRegistry.serializeToBase64(programmableKind, BcsRegistry.TRANSACTION_KIND_SERIALIZER);
        log.info("TransactionKind ProgrammableTransaction: {} -> {}", programmableKind, programmableKindBase64);
        assertNotNull(programmableKindBase64);
        assertTrue(programmableKindBase64.length() > 0);

        // test TransactionExpiration
        TransactionExpiration noneExpiration = TransactionExpiration.None.INSTANCE;
        String noneExpirationBase64 = BcsRegistry.serializeToBase64(noneExpiration, BcsRegistry.TRANSACTION_EXPIRATION_SERIALIZER);
        log.info("TransactionExpiration None: {} -> {}", noneExpiration, noneExpirationBase64);
        assertNotNull(noneExpirationBase64);
        assertTrue(noneExpirationBase64.length() > 0);

        TransactionExpiration epochExpiration = new TransactionExpiration.Epoch(1000L);
        String epochExpirationBase64 = BcsRegistry.serializeToBase64(epochExpiration, BcsRegistry.TRANSACTION_EXPIRATION_SERIALIZER);
        log.info("TransactionExpiration Epoch: {} -> {}", epochExpiration, epochExpirationBase64);
        assertNotNull(epochExpirationBase64);
        assertTrue(epochExpirationBase64.length() > 0);

        // test TransactionDataV1
        TransactionDataV1 transactionDataV1 = new TransactionDataV1(
                programmableKind,
                user.address(),
                testGasDataUpdatePrice,
                noneExpiration
        );
        String transactionDataV1Base64 = BcsRegistry.serializeToBase64(transactionDataV1, BcsRegistry.TRANSACTION_DATA_V1_SERIALIZER);
        log.info("TransactionDataV1: {} -> {}", transactionDataV1, transactionDataV1Base64);
        assertNotNull(transactionDataV1Base64);
        assertTrue(transactionDataV1Base64.length() > 0);

        // test TransactionData
        TransactionData transactionData = new TransactionData.V1(transactionDataV1);
        String transactionDataBase64 = BcsRegistry.serializeToBase64(transactionData, BcsRegistry.TRANSACTION_DATA_SERIALIZER);
        log.info("\nTransactionData: {} -> \n{}", transactionData, transactionDataBase64);

        log.info("My binary:\n{}", Arrays.toString(this.trans(Base64.decode(transactionDataBase64))));
//        log.info("\nTemporary:\n{} -> \n{}", symbolPair.getPriceOracleId(), Arrays.toString(this.trans(Hex.decode(symbolPair.getPriceOracleId().replace("0x", "")))));
//        log.info("\nTemporary:\n{} -> \n{}", "0x31358d198147da50db32eda2562951d53973a0c0ad5ed738e9b17d88b213d790", Arrays.toString(this.trans(Hex.decode("31358d198147da50db32eda2562951d53973a0c0ad5ed738e9b17d88b213d790"))));
//        log.info("\nTemporary:\n{} -> \n{}", new BigInteger("12041355"), Arrays.toString(this.trans(Base64.decode(PureBcs.serializeToBase64(PureBcs.BasePureType.U64.name(), new BigInteger("12041355").longValue())))));
//        log.info("\nTemporary:\n{} -> \n{}", new BigInteger("1451"), Arrays.toString(this.trans(Base64.decode(PureBcs.serializeToBase64(PureBcs.BasePureType.U64.name(), new BigInteger("1451").longValue())))));
//        log.info("\nTemporary:\n{} -> \n{}", "false", Arrays.toString(this.trans(Base64.decode(PureBcs.serializeToBase64(PureBcs.BasePureType.BOOL.name(), false)))));
//        log.info("\nTemporary:\n{} -> \n{}", TxConfig.GAS_BUDGET, Arrays.toString(this.trans(Base64.decode(PureBcs.serializeToBase64(PureBcs.BasePureType.U128.name(), TxConfig.GAS_BUDGET)))));
//        log.info("\nTemporary:\n{} -> \n{}", "QAhVWQSRoqJpcEn1KjijbcjSNaV8t9dSQzj6dhkSjRn", Arrays.toString(this.trans(Base64.decode(PureBcs.serializeToBase64(PureBcs.BasePureType.STRING.name(), "QAhVWQSRoqJpcEn1KjijbcjSNaV8t9dSQzj6dhkSjRn")))));
        assertNotNull(transactionDataBase64);
        assertTrue(transactionDataBase64.length() > 0);

        // 4. on chain
        if (ENABLE_SEND) {
            this.testExecuteTransactionBlock(transactionDataBase64, operatorKeyPair);
        }
    }

    @Test
    void testIntentSerialization() throws IOException {
        log.info("test Intent Transactionserialize");
        
        // test IntentScope
        IntentScope transactionDataScope = IntentScope.TransactionData.INSTANCE;
        String transactionDataScopeBase64 = BcsRegistry.serializeToBase64(transactionDataScope, BcsRegistry.INTENT_SCOPE_SERIALIZER);
        log.info("IntentScope TransactionData: {} -> {}", transactionDataScope, transactionDataScopeBase64);
        assertNotNull(transactionDataScopeBase64);
        assertTrue(transactionDataScopeBase64.length() > 0);
        
        // test IntentVersion
        IntentVersion v0Version = IntentVersion.V0.INSTANCE;
        String v0VersionBase64 = BcsRegistry.serializeToBase64(v0Version, BcsRegistry.INTENT_VERSION_SERIALIZER);
        log.info("IntentVersion V0: {} -> {}", v0Version, v0VersionBase64);
        assertNotNull(v0VersionBase64);
        assertTrue(v0VersionBase64.length() > 0);
        
        // test AppId
        AppId suiAppId = AppId.Sui.INSTANCE;
        String suiAppIdBase64 = BcsRegistry.serializeToBase64(suiAppId, BcsRegistry.APP_ID_SERIALIZER);
        log.info("AppId Sui: {} -> {}", suiAppId, suiAppIdBase64);
        assertNotNull(suiAppIdBase64);
        assertTrue(suiAppIdBase64.length() > 0);
        
        // test Intent
        Intent intent = new Intent(transactionDataScope, v0Version, suiAppId);
        String intentBase64 = BcsRegistry.serializeToBase64(intent, BcsRegistry.INTENT_SERIALIZER);
        log.info("Intent: {} -> {}", intent, intentBase64);
        assertNotNull(intentBase64);
        assertTrue(intentBase64.length() > 0);
        
        // test IntentMessage
        TransactionData transactionData = new TransactionData.V1(new TransactionDataV1(
            TransactionKind.ChangeEpoch.INSTANCE,
            "0x0000000000000000000000000000000000000000000000001234567890abcdef",
            testGasDataDeposit,
            TransactionExpiration.None.INSTANCE
        ));
        IntentMessage<TransactionData> intentMessage = new IntentMessage<>(intent, transactionData);
        String intentMessageBase64 = BcsRegistry.serializeToBase64(intentMessage, BcsRegistry.INTENT_MESSAGE_SERIALIZER);
        log.info("IntentMessage: {} -> {}", intentMessage, intentMessageBase64);
        assertNotNull(intentMessageBase64);
        assertTrue(intentMessageBase64.length() > 0);
    }

    @Test
    void testIntentStringSerialization() throws IOException {
        log.info("testIntent Stringserialize");

        // test IntentMessage
//        String message = "000000000000006c6b935b8bbd4000000000000000000000016345785d8a000000000000000000008ac7230489e80000000000000000000000000197e39b9d70000000000000000032c42fef490b188931789797feb1125034ec8118a5d15ef927b9b60e07e2ca1e3a19c6f9b313c4de503b498a3f519462710c2ff8783a727c6db984735c1dbc8518446970436f696e000000000000006c6b935b8bbd4000000000000000000000016345785d8a000000000000000000008ac7230489e80000000000000000000000000197e39b9d70000000000000000032c42fef490b188931789797feb1125034ec8118a5d15ef927b9b60e07e2ca1e3a19c6f9b313c4de503b498a3f519462710c2ff8783a727c6db984735c1dbc8518446970436f696e000000000000006c6b935b8bbd4000000000000000000000016345785d8a000000000000000000008ac7230489e80000000000000000000000000197e39b9d70000000000000000032c42fef490b188931789797feb1125034ec8118a5d15ef927b9b60e07e2ca1e3a19c6f9b313c4de503b498a3f519462710c2ff8783a727c6db984735c1dbc8518446970436f696e000000000000006c6b935b8bbd4000000000000000000000016345785d8a000000000000000000008ac7230489e80000000000000000000000000197e39b9d70000000000000000032c42fef490b188931789797feb1125034ec8118a5d15ef927b9b60e07e2ca1e3a19c6f9b313c4de503b498a3f519462710c2ff8783a727c6db984735c1dbc8518446970436f696e";
        String message = "123";
        IntentMessage<String> intentMessage = new IntentMessage<>(IntentScope.PersonalMessage.INSTANCE, message);
        String intentMessageBase64 = BcsRegistry.serializeToBase64(intentMessage, BcsRegistry.INTENT_MESSAGE_STR_SERIALIZER);
        log.info("IntentMessage: {} -> {} -> {}", intentMessage, Base64.decode(intentMessageBase64), intentMessageBase64);
        assertNotNull(intentMessageBase64);
        assertTrue(intentMessageBase64.length() > 0);
    }
    
    @Test
    void testSignatureSerialization() throws IOException {
        log.info("testsignserialize");
        
        // test CompressedSignature
        byte[] ed25519Signature = new byte[64];
        Arrays.fill(ed25519Signature, (byte) 0xAA);
        CompressedSignature ed25519Sig = new CompressedSignature.ED25519(ed25519Signature);
        String ed25519SigBase64 = BcsRegistry.serializeToBase64(ed25519Sig, BcsRegistry.COMPRESSED_SIGNATURE_SERIALIZER);
        log.info("CompressedSignature ED25519: {} -> {}", ed25519Sig, ed25519SigBase64);
        assertNotNull(ed25519SigBase64);
        assertTrue(ed25519SigBase64.length() > 0);
        
        // test PublicKey
        byte[] ed25519PublicKey = new byte[32];
        Arrays.fill(ed25519PublicKey, (byte) 0xBB);
        PublicKey ed25519PubKey = new PublicKey.ED25519(ed25519PublicKey);
        String ed25519PubKeyBase64 = BcsRegistry.serializeToBase64(ed25519PubKey, BcsRegistry.PUBLIC_KEY_SERIALIZER);
        log.info("PublicKey ED25519: {} -> {}", ed25519PubKey, ed25519PubKeyBase64);
        assertNotNull(ed25519PubKeyBase64);
        assertTrue(ed25519PubKeyBase64.length() > 0);
        
        // test MultiSigPkMap
        MultiSigPkMap pkMap = new MultiSigPkMap(ed25519PubKey, 1);
        String pkMapBase64 = BcsRegistry.serializeToBase64(pkMap, BcsRegistry.MULTI_SIG_PK_MAP_SERIALIZER);
        log.info("MultiSigPkMap: {} -> {}", pkMap, pkMapBase64);
        assertNotNull(pkMapBase64);
        assertTrue(pkMapBase64.length() > 0);
        
        // test MultiSigPublicKey
        MultiSigPublicKey multiSigPublicKey = new MultiSigPublicKey(Arrays.asList(pkMap), 1);
        String multiSigPublicKeyBase64 = BcsRegistry.serializeToBase64(multiSigPublicKey, BcsRegistry.MULTI_SIG_PUBLIC_KEY_SERIALIZER);
        log.info("MultiSigPublicKey: {} -> {}", multiSigPublicKey, multiSigPublicKeyBase64);
        assertNotNull(multiSigPublicKeyBase64);
        assertTrue(multiSigPublicKeyBase64.length() > 0);
        
        // test MultiSig
        MultiSig multiSig = new MultiSig(Arrays.asList(ed25519Sig), 1, multiSigPublicKey);
        String multiSigBase64 = BcsRegistry.serializeToBase64(multiSig, BcsRegistry.MULTI_SIG_SERIALIZER);
        log.info("MultiSig: {} -> {}", multiSig, multiSigBase64);
        assertNotNull(multiSigBase64);
        assertTrue(multiSigBase64.length() > 0);
    }
    
    @Test
    void testOwnerSerialization() throws IOException {
        log.info("testownerserialize");
        
        // test AddressOwner
        Owner addressOwner = new Owner.AddressOwner("0x0000000000000000000000000000000000000000000000001234567890abcdef");
        String addressOwnerBase64 = BcsRegistry.serializeToBase64(addressOwner, BcsRegistry.OWNER_SERIALIZER);
        log.info("Owner AddressOwner: {} -> {}", addressOwner, addressOwnerBase64);
        assertNotNull(addressOwnerBase64);
        assertTrue(addressOwnerBase64.length() > 0);
        
        // test ObjectOwner
        Owner objectOwner = new Owner.ObjectOwner("0x000000000000000000000000000000000000000000000000fedcba0987654321");
        String objectOwnerBase64 = BcsRegistry.serializeToBase64(objectOwner, BcsRegistry.OWNER_SERIALIZER);
        log.info("Owner ObjectOwner: {} -> {}", objectOwner, objectOwnerBase64);
        assertNotNull(objectOwnerBase64);
        assertTrue(objectOwnerBase64.length() > 0);
        
        // test Shared
        Owner sharedOwner = new Owner.Shared(1000L);
        String sharedOwnerBase64 = BcsRegistry.serializeToBase64(sharedOwner, BcsRegistry.OWNER_SERIALIZER);
        log.info("Owner Shared: {} -> {}", sharedOwner, sharedOwnerBase64);
        assertNotNull(sharedOwnerBase64);
        assertTrue(sharedOwnerBase64.length() > 0);
        
        // test Immutable
        Owner immutableOwner = Owner.Immutable.INSTANCE;
        String immutableOwnerBase64 = BcsRegistry.serializeToBase64(immutableOwner, BcsRegistry.OWNER_SERIALIZER);
        log.info("Owner Immutable: {} -> {}", immutableOwner, immutableOwnerBase64);
        assertNotNull(immutableOwnerBase64);
        assertTrue(immutableOwnerBase64.length() > 0);
        
        // test ConsensusV2
        Owner.Authenticator authenticator = new Owner.Authenticator("0x0000000000000000000000000000000000000000000000001234567890abcdef");
        Owner consensusV2Owner = new Owner.ConsensusV2(authenticator, 1000L);
        String consensusV2OwnerBase64 = BcsRegistry.serializeToBase64(consensusV2Owner, BcsRegistry.OWNER_SERIALIZER);
        log.info("Owner ConsensusV2: {} -> {}", consensusV2Owner, consensusV2OwnerBase64);
        assertNotNull(consensusV2OwnerBase64);
        assertTrue(consensusV2OwnerBase64.length() > 0);
    }
    
    @Test
    void testAuthSerialization() throws IOException {
        log.info("Test authentication serialization.");
        
        // test PasskeyAuthenticator
        byte[] authenticatorData = "authenticator_data".getBytes();
        String clientDataJson = "{\"type\":\"webauthn.get\",\"challenge\":\"test\"}";
        byte[] userSignature = "user_signature".getBytes();
        
        PasskeyAuthenticator passkeyAuth = new PasskeyAuthenticator(authenticatorData, clientDataJson, userSignature);
        String passkeyAuthBase64 = BcsRegistry.serializeToBase64(passkeyAuth, BcsRegistry.PASSKEY_AUTHENTICATOR_SERIALIZER);
        log.info("PasskeyAuthenticator: {} -> {}", passkeyAuth, passkeyAuthBase64);
        assertNotNull(passkeyAuthBase64);
        assertTrue(passkeyAuthBase64.length() > 0);
    }
    
    @Test
    void testComplexTransactionSerialization() throws IOException {
        log.info("test Complex Transaction Serialization");
        
        // Create complex transaction data.
        ProgrammableTransaction programmableTx = new ProgrammableTransaction();
        programmableTx.addInput(new CallArgPure("test_input".getBytes(), PureBcs.BasePureType.VECTOR_U8));
        programmableTx.addInput(new CallArgObjectArg(new ObjectArgImmOrOwnedObject(testObjectRefDeposit)));
        programmableTx.addCommands(Arrays.asList(
                        new Command.MoveCall(new ProgrammableMoveCall(
                                "0x0000000000000000000000000000000000000000000000001234567890abcdef",
                                "test_module",
                                "test_function",
                                List.of(TypeTagBool.INSTANCE, TypeTagU64.INSTANCE),
                                Arrays.asList(Argument.GasCoin.INSTANCE, Argument.ofInput(0))
                        )),
                        new Command.TransferObjects(
                                Arrays.asList(Argument.ofInput(1)),
                                Argument.ofInput(2)
                        )
                )
        );
        TransactionData transactionData = new TransactionData.V1(new TransactionDataV1(
            new TransactionKind.ProgrammableTransaction(programmableTx),
            "0x0000000000000000000000000000000000000000000000001234567890abcdef",
                testGasDataDeposit,
            new TransactionExpiration.Epoch(1000L)
        ));
        
        // create Intent
        Intent intent = new Intent(
            IntentScope.TransactionData.INSTANCE,
            IntentVersion.V0.INSTANCE,
            AppId.Sui.INSTANCE
        );
        
        // create IntentMessage
        IntentMessage<TransactionData> intentMessage = new IntentMessage<>(intent, transactionData);
        
        // createSenderSignedTransaction
        SenderSignedTransaction senderSignedTx = new SenderSignedTransaction(
            intentMessage,
            Arrays.asList("signature1".getBytes(), "signature2".getBytes())
        );
        
        String senderSignedTxBase64 = BcsRegistry.serializeToBase64(senderSignedTx, BcsRegistry.SENDER_SIGNED_TRANSACTION_SERIALIZER);
        log.info("SenderSignedTransaction: {} -> {}", senderSignedTx, senderSignedTxBase64);
        assertNotNull(senderSignedTxBase64);
        assertTrue(senderSignedTxBase64.length() > 0);
        
        // verify Base64encode
        byte[] decoded = Base64.decode(senderSignedTxBase64);
        assertTrue(decoded.length > 0);
        log.info("Decoded bytes length: {}", decoded.length);
    }
    
    @Test
    void testErrorHandling() {
        log.info("test error handle");
        
        // test null string
//        assertThrows(IOException.class, () -> {
//            BcsRegistry.serializeToBase64("", BcsRegistry.STRING_SERIALIZER);
//        });
        
        // test null
        assertThrows(NullPointerException.class, () -> {
            BcsRegistry.serializeToBase64(null, BcsRegistry.STRING_SERIALIZER);
        });
        
        // test invalid address
        assertThrows(IllegalArgumentException.class, () -> {
            BcsRegistry.serializeToBase64("invalid_address", BcsRegistry.ADDRESS_SERIALIZER);
        });
    }
    
    @Test
    void testPerformance() throws IOException {
        log.info("test performance");
        
        // test serialization performance with large datasets.
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 1000; i++) {
            String address = String.format("0x%064d", i);
            BcsRegistry.serializeToBase64(address, BcsRegistry.ADDRESS_SERIALIZER);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        log.info("Time consumed to serialize 1000 addresses: {}ms", duration);
        assertTrue(duration < 1000, "Serialization performance should be sufficiently fast.");
    }

    protected static int[] trans(byte[] data) {
        int[] unsignedBytes = new int[data.length];

        for (int i = 0; i < data.length; i++) {
            unsignedBytes[i] = data[i] & 0xFF;
        }
        return unsignedBytes;
    }

    protected static byte[] transFrom(int[] data) {
        byte[] unsignedBytes = new byte[data.length];

        for (int i = 0; i < data.length; i++) {
            if (data[i] < 0 || data[i] > 255) {
                throw new IllegalArgumentException(
                        "All values must be within the 0-255 range: " + data[i]);
            }
            unsignedBytes[i] = (byte) (data[i] & 0xFF);
        }
        return unsignedBytes;
    }

    /**
     * Replace the target byte sequence with a new byte sequence in the source byte array.
     * @param txBytes
     * @return The new byte array after replacement (returns the original array if the target is not found).
     */
    public static String replaceBytes(String txBytes) {
        byte[] source = Base64.decode(txBytes);
        String oldAddress = "458adc959cf32e3dfdae736ccd7e3b368166e0fee6d4c250a42784293821eb6d";
        String newAddress = "0b50fe6d7b86730f0f8d2e389d22d63c30a73a1034720a8a43bb5e322a9588e1";
        byte[] target = Hex.decode(oldAddress);
        byte[] replacement = Hex.decode(newAddress);
        if (target.length != replacement.length) {
            throw new IllegalArgumentException("Target and replacement must have the same length");
        }

        int index = findSubarray(source, target);
        if (index == -1) {
            System.out.println("Target not found in source");
            return Base64.toBase64String(source);
        }

        byte[] result = Arrays.copyOf(source, source.length);
        System.arraycopy(replacement, 0, result, index, replacement.length);
        return Base64.toBase64String(result);
    }

    /**
     * Find the starting position of the subarray within the byte array.
     * @param source bytes array
     * @param target bytes array
     * @return starting index (returns -1 if not found).
     */
    public static int findSubarray(byte[] source, byte[] target) {
        for (int i = 0; i <= source.length - target.length; i++) {
            boolean match = true;
            for (int j = 0; j < target.length; j++) {
                if (source[i + j] != target[j]) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return i;
            }
        }
        return -1;
    }
} 