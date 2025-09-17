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

import io.dipcoin.sui.bcs.types.effects.ExecutionStatus;
import io.dipcoin.sui.bcs.types.effects.GasCostSummary;
import io.dipcoin.sui.bcs.types.effects.TransactionEffects;
import io.dipcoin.sui.bcs.types.effects.TransactionEffectsV1;
import io.dipcoin.sui.bcs.types.gas.SuiObjectRef;
import io.dipcoin.sui.util.HashUtil;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.DecoderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author : Same
 * @datetime : 2025/7/11 14:15
 * @Description : EffectsBcs unit test
 * Verify the correctness of BCS serialization for transaction effects.
 */
@Slf4j
public class EffectsBcsTest {
    
    private GasCostSummary gasCostSummary;
    private TransactionEffectsV1 transactionEffectsV1;
    private TransactionEffects transactionEffects;
    private List<SuiObjectRef> testObjects;
    
    @BeforeEach
    void setUp() {
        // Create a test gas cost summary.
        gasCostSummary = new GasCostSummary(1000L, 500L, 200L, 50L);
        
        // Create a Sui object reference for testing purposes.
        testObjects = Arrays.asList(
            new SuiObjectRef("0x0b50fe6d7b86730f0f8d2e389d22d63c30a73a1034720a8a43bb5e322a9588e1", 1L, HashUtil.sha256Base58("digest1")),
            new SuiObjectRef("0xfcf8ed6af43ddd6b21299fc1766dcea3a0fd16edac0bc38ebb114a20bc7d4da3", 2L, HashUtil.sha256Base58("digest2"))
        );
        
        // Create test transaction effects V1.
        transactionEffectsV1 = new TransactionEffectsV1(
            ExecutionStatus.SUCCESS,
            12345L,
            gasCostSummary,
            testObjects, // created
            testObjects, // mutated
            testObjects, // deleted
                HashUtil.sha256Base58("transaction_digest_12345")
        );
        
        // Create test transaction effects.
        transactionEffects = new TransactionEffects(transactionEffectsV1);
    }
    
    @Test
    void testGasCostSummarySerialization() throws IOException {
        log.info("Testing GasCostSummary serialization...");
        
        BcsSerializer serializer = new BcsSerializer();
        EffectsBcs.GAS_COST_SUMMARY_SERIALIZER.serialize(serializer, gasCostSummary);
        String base64 = serializer.toBase64();
        
        log.info("GasCostSummary: computationCost={}, storageCost={}, storageRebate={}, nonRefundableStorageFee={}",
                gasCostSummary.getComputationCost(), gasCostSummary.getStorageCost(),
                gasCostSummary.getStorageRebate(), gasCostSummary.getNonRefundableStorageFee());
        log.info("Serialized Base64: {}", base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // verify decode
        byte[] decoded = Base64.decode(base64);
        assertEquals(32, decoded.length); // Four u64 fields, each 8 bytes.
        
        // Verify that deserialization can be performed correctly.
        BcsDeserializer deserializer = new BcsDeserializer(decoded);
        long computationCost = deserializer.readU64();
        long storageCost = deserializer.readU64();
        long storageRebate = deserializer.readU64();
        long nonRefundableStorageFee = deserializer.readU64();
        
        assertEquals(gasCostSummary.getComputationCost(), computationCost);
        assertEquals(gasCostSummary.getStorageCost(), storageCost);
        assertEquals(gasCostSummary.getStorageRebate(), storageRebate);
        assertEquals(gasCostSummary.getNonRefundableStorageFee(), nonRefundableStorageFee);
    }
    
    @Test
    void testTransactionEffectsV1Serialization() throws IOException {
        log.info("Testing TransactionEffectsV1 serialization...");
        
        BcsSerializer serializer = new BcsSerializer();
        EffectsBcs.TRANSACTION_EFFECTS_V1_SERIALIZER.serialize(serializer, transactionEffectsV1);
        String base64 = serializer.toBase64();
        
        log.info("TransactionEffectsV1: status={}, executedEpoch={}, transactionDigest={}",
                transactionEffectsV1.getStatus(), transactionEffectsV1.getExecutedEpoch(),
                transactionEffectsV1.getTransactionDigest());
        log.info("Serialized Base64: {}", base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // verify decode
        byte[] decoded = Base64.decode(base64);
        assertTrue(decoded.length > 0);
        
        // Verify that deserialization can be performed correctly.
        BcsDeserializer deserializer = new BcsDeserializer(decoded);
        int statusVariant = deserializer.readU32();
        assertEquals(0, statusVariant); // SUCCESS variant
        
        long executedEpoch = deserializer.readU64();
        assertEquals(transactionEffectsV1.getExecutedEpoch(), executedEpoch);
    }
    
    @Test
    void testTransactionEffectsSerialization() throws IOException {
        log.info("Testing TransactionEffects serialization...");
        
        BcsSerializer serializer = new BcsSerializer();
        EffectsBcs.TRANSACTION_EFFECTS_SERIALIZER.serialize(serializer, transactionEffects);
        String base64 = serializer.toBase64();
        
        log.info("TransactionEffects: has V1 effects");
        log.info("Serialized Base64: {}", base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // verify decode
        byte[] decoded = Base64.decode(base64);
        assertTrue(decoded.length > 0);
        
        // Verify that deserialization can be performed correctly.
        BcsDeserializer deserializer = new BcsDeserializer(decoded);
        int version = deserializer.readU32();
        assertEquals(0, version); // V1 variant
    }
    
    @Test
    void testSerializeToBase64() throws IOException {
        log.info("Testing serializeToBase64 method...");
        
        String base64 = EffectsBcs.serializeToBase64(transactionEffects);
        
        log.info("TransactionEffects serialized to Base64: {}", base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // Verify that decoding is performed correctly.
        byte[] decoded = Base64.decode(base64);
        assertTrue(decoded.length > 0);
    }
    
    @Test
    void testDeserializeFromBase64() throws IOException {
        log.info("Testing deserializeFromBase64 method...");
        
        // serialize
        String base64 = EffectsBcs.serializeToBase64(transactionEffects);
        log.info("Original Base64: {}", base64);

        // deserialize
        TransactionEffects deserialized = EffectsBcs.deserializeFromBase64(base64);
        log.info("Deserialized TransactionEffects: has V1 effects");
        
        // Verify the deserialization result.
        assertNotNull(deserialized);
        assertNotNull(deserialized.getV1());
        
        TransactionEffectsV1 deserializedV1 = deserialized.getV1();
        System.out.println("Deserialized TransactionEffects: " + deserializedV1);
        assertEquals(transactionEffectsV1.getStatus(), deserializedV1.getStatus());
        assertEquals(transactionEffectsV1.getExecutedEpoch(), deserializedV1.getExecutedEpoch());
        assertEquals(transactionEffectsV1.getTransactionDigest(), deserializedV1.getTransactionDigest());
        
        // verify gas cost summary
        GasCostSummary deserializedGasCost = deserializedV1.getGasUsed();
        assertEquals(gasCostSummary.getComputationCost(), deserializedGasCost.getComputationCost());
        assertEquals(gasCostSummary.getStorageCost(), deserializedGasCost.getStorageCost());
        assertEquals(gasCostSummary.getStorageRebate(), deserializedGasCost.getStorageRebate());
        assertEquals(gasCostSummary.getNonRefundableStorageFee(), deserializedGasCost.getNonRefundableStorageFee());
    }
    
    @Test
    void testFailedExecutionStatus() throws IOException {
        log.info("Testing failed execution status...");
        
        // Create transaction effects with a failed status.
        TransactionEffectsV1 failedEffects = new TransactionEffectsV1(
            ExecutionStatus.FAILED,
            12345L,
            gasCostSummary,
            testObjects,
            testObjects,
            testObjects,
                HashUtil.sha256Base58("failed_transaction_digest")
        );
        
        TransactionEffects failedTransactionEffects = new TransactionEffects(failedEffects);
        
        BcsSerializer serializer = new BcsSerializer();
        EffectsBcs.TRANSACTION_EFFECTS_V1_SERIALIZER.serialize(serializer, failedEffects);
        String base64 = serializer.toBase64();
        
        log.info("Failed TransactionEffectsV1 serialized to Base64: {}", base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // verify decode
        byte[] decoded = Base64.decode(base64);
        BcsDeserializer deserializer = new BcsDeserializer(decoded);
        int statusVariant = deserializer.readU32();
        assertEquals(1, statusVariant); // FAILED variant
    }
    
    @Test
    void testEmptyObjectLists() throws IOException {
        log.info("Testing empty object lists...");
        
        // Create transaction effects with an empty list.
        TransactionEffectsV1 emptyEffects = new TransactionEffectsV1(
            ExecutionStatus.SUCCESS,
            12345L,
            gasCostSummary,
            Arrays.asList(), // empty created
            Arrays.asList(), // empty mutated
            Arrays.asList(), // empty deleted
                HashUtil.sha256Base58("empty_objects_digest")
        );
        
        TransactionEffects emptyTransactionEffects = new TransactionEffects(emptyEffects);
        
        String base64 = EffectsBcs.serializeToBase64(emptyTransactionEffects);
        log.info("Empty objects TransactionEffects serialized to Base64: {}", base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // Verify that deserialization can be performed correctly.
        TransactionEffects deserialized = EffectsBcs.deserializeFromBase64(base64);
        assertNotNull(deserialized);
        assertNotNull(deserialized.getV1());
        
        TransactionEffectsV1 deserializedV1 = deserialized.getV1();
        assertTrue(deserializedV1.getCreated().isEmpty());
        assertTrue(deserializedV1.getMutated().isEmpty());
        assertTrue(deserializedV1.getDeleted().isEmpty());
    }
    
    @Test
    void testLargeObjectLists() throws IOException {
        log.info("Testing large object lists...");
        
        // Create a list with a large number of objects.
        List<SuiObjectRef> largeList = Arrays.asList(
            new SuiObjectRef("0x1111111111111111111111111111111111111111111111111111111111111111", 1L, HashUtil.sha256Base58("digest1")),
            new SuiObjectRef("0x2222222222222222222222222222222222222222222222222222222222222222", 2L, HashUtil.sha256Base58("digest2")),
            new SuiObjectRef("0x3333333333333333333333333333333333333333333333333333333333333333", 3L, HashUtil.sha256Base58("digest3")),
            new SuiObjectRef("0x4444444444444444444444444444444444444444444444444444444444444444", 4L, HashUtil.sha256Base58("digest4")),
            new SuiObjectRef("0x5555555555555555555555555555555555555555555555555555555555555555", 5L, HashUtil.sha256Base58("digest5"))
        );
        
        TransactionEffectsV1 largeEffects = new TransactionEffectsV1(
            ExecutionStatus.SUCCESS,
            12345L,
            gasCostSummary,
            largeList, // large created
            largeList, // large mutated
            largeList, // large deleted
                HashUtil.sha256Base58("large_objects_digest")
        );
        
        TransactionEffects largeTransactionEffects = new TransactionEffects(largeEffects);
        
        String base64 = EffectsBcs.serializeToBase64(largeTransactionEffects);
        log.info("Large objects TransactionEffects serialized to Base64: {}", base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // Verify that deserialization can be performed correctly.
        TransactionEffects deserialized = EffectsBcs.deserializeFromBase64(base64);
        assertNotNull(deserialized);
        assertNotNull(deserialized.getV1());
        
        TransactionEffectsV1 deserializedV1 = deserialized.getV1();
        assertEquals(largeList.size(), deserializedV1.getCreated().size());
        assertEquals(largeList.size(), deserializedV1.getMutated().size());
        assertEquals(largeList.size(), deserializedV1.getDeleted().size());
    }
    
    @Test
    void testInvalidBase64Deserialization() {
        log.info("Testing invalid Base64 deserialization...");
        
        // Test invalid Base64 strings.
        assertThrows(DecoderException.class, () -> {
            EffectsBcs.deserializeFromBase64("invalid_base64_string");
        });
        
        // test null string
        assertThrows(IOException.class, () -> {
            EffectsBcs.deserializeFromBase64("");
        });
        
        // test null
        assertThrows(NullPointerException.class, () -> {
            EffectsBcs.deserializeFromBase64(null);
        });
    }
    
    @Test
    void testUnsupportedVersion() throws IOException {
        log.info("Testing unsupported version...");
        
        // Create a byte array containing an unsupported version.
        BcsSerializer serializer = new BcsSerializer();
        serializer.writeU32(999); // unsupported version
        String base64 = serializer.toBase64();
        
        log.info("Unsupported version Base64: {}", base64);
        
        // verify throw exception
        assertThrows(IllegalArgumentException.class, () -> {
            EffectsBcs.deserializeFromBase64(base64);
        });
    }
    
    @Test
    void testRoundTripSerialization() throws IOException {
        log.info("Testing round-trip serialization...");
        
        // create complex transaction effects
        GasCostSummary complexGasCost = new GasCostSummary(
            Long.MAX_VALUE, Long.MIN_VALUE, 0L, 1L
        );
        
        List<SuiObjectRef> complexObjects = Arrays.asList(
            new SuiObjectRef("0x" + "a".repeat(64), Long.MAX_VALUE, HashUtil.sha256Base58("very_long_digest_string_here")),
            new SuiObjectRef("0x" + "b".repeat(64), 0L, HashUtil.sha256Base58("short_digest"))
        );
        
        TransactionEffectsV1 complexEffects = new TransactionEffectsV1(
            ExecutionStatus.SUCCESS,
            Long.MAX_VALUE,
            complexGasCost,
            complexObjects,
            complexObjects,
            complexObjects,
                HashUtil.sha256Base58("complex_transaction_digest_with_special_chars_!@#$%^&*()")
        );
        
        TransactionEffects complexTransactionEffects = new TransactionEffects(complexEffects);
        
        // serialize
        String base64 = EffectsBcs.serializeToBase64(complexTransactionEffects);
        log.info("Complex TransactionEffects serialized to Base64: {}", base64);
        
        // deserialize
        TransactionEffects deserialized = EffectsBcs.deserializeFromBase64(base64);
        log.info("Complex TransactionEffects deserialized successfully");
        
        // verify all field
        TransactionEffectsV1 deserializedV1 = deserialized.getV1();
        assertEquals(complexEffects.getStatus(), deserializedV1.getStatus());
        assertEquals(complexEffects.getExecutedEpoch(), deserializedV1.getExecutedEpoch());
        assertEquals(complexEffects.getTransactionDigest(), deserializedV1.getTransactionDigest());
        
        GasCostSummary deserializedComplexGasCost = deserializedV1.getGasUsed();
        assertEquals(complexGasCost.getComputationCost(), deserializedComplexGasCost.getComputationCost());
        assertEquals(complexGasCost.getStorageCost(), deserializedComplexGasCost.getStorageCost());
        assertEquals(complexGasCost.getStorageRebate(), deserializedComplexGasCost.getStorageRebate());
        assertEquals(complexGasCost.getNonRefundableStorageFee(), deserializedComplexGasCost.getNonRefundableStorageFee());
        
        assertEquals(complexObjects.size(), deserializedV1.getCreated().size());
        assertEquals(complexObjects.size(), deserializedV1.getMutated().size());
        assertEquals(complexObjects.size(), deserializedV1.getDeleted().size());
    }
} 