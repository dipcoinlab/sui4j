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

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.DecoderException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author : Same
 * @datetime : 2025/7/11 16:45
 * @Description : PureBcsunit test
 * Verify the correctness of pure type BCS serialization.
 */
@Slf4j
public class PureBcsTest {
    
    @Test
    void testBasicTypeSerialization() throws IOException {
        log.info("Testing basic type serialization...");
        
        // test u64type
        String u64Base64 = PureBcs.serializeToBase64("u64", 123456789L);
        log.info("u64: 123456789 -> {}", u64Base64);
        
        // test string type
        String stringBase64 = PureBcs.serializeToBase64("string", "Hello, World!");
        log.info("string: 'Hello, World!' -> {}", stringBase64);
        
        // test bool type
        String boolBase64 = PureBcs.serializeToBase64("bool", true);
        log.info("bool: true -> {}", boolBase64);
        
        // test address type
        String addressBase64 = PureBcs.serializeToBase64("address", "0x1234567890abcdef");
        log.info("address: '0x1234567890abcdef' -> {}", addressBase64);
        
        // verify allBase64encode
        assertNotNull(u64Base64);
        assertNotNull(stringBase64);
        assertNotNull(boolBase64);
        assertNotNull(addressBase64);
        assertFalse(u64Base64.isEmpty());
        assertFalse(stringBase64.isEmpty());
        assertFalse(boolBase64.isEmpty());
        assertFalse(addressBase64.isEmpty());
    }
    
    @Test
    void testVectorTypeSerialization() throws IOException {
        log.info("Testing vector type serialization...");
        
        // test vector<u64>type
        List<Long> u64List = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        String vectorU64Base64 = PureBcs.serializeToBase64("vector<u64>", u64List);
        log.info("vector<u64>: {} -> {}", u64List, vectorU64Base64);
        
        // test vector<string>type
        List<String> stringList = Arrays.asList("hello", "world", "test");
        String vectorStringBase64 = PureBcs.serializeToBase64("vector<string>", stringList);
        log.info("vector<string>: {} -> {}", stringList, vectorStringBase64);
        
        // verify Base64encode
        assertNotNull(vectorU64Base64);
        assertNotNull(vectorStringBase64);
        assertFalse(vectorU64Base64.isEmpty());
        assertFalse(vectorStringBase64.isEmpty());
    }
    
    @Test
    void testSerializeToBase64() throws IOException {
        log.info("Testing serializeToBase64 method...");
        
        String base64 = PureBcs.serializeToBase64("u64", 123456789L);
        
        log.info("u64 serialized to Base64: {}", base64);
        
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
        String base64 = PureBcs.serializeToBase64("string", "Hello, World!");
        log.info("Original Base64: {}", base64);
        
        // deserialize
        Object deserialized = PureBcs.deserializeFromBase64("string", base64);
        log.info("Deserialized string: {}", deserialized);
        
        // Verify the deserialization result.
        assertNotNull(deserialized);
        assertEquals("Hello, World!", deserialized);
    }
    
    @Test
    void testEmptyVectorSerialization() throws IOException {
        log.info("Testing empty vector serialization...");
        
        // test null vector<u64>
        List<Long> emptyU64List = Arrays.asList();
        String emptyVectorBase64 = PureBcs.serializeToBase64("vector<u64>", emptyU64List);
        log.info("empty vector<u64>: {} -> {}", emptyU64List, emptyVectorBase64);
        
        // verify Base64encode
        assertNotNull(emptyVectorBase64);
        assertFalse(emptyVectorBase64.isEmpty());
        
        // Verify that deserialization can be performed correctly.
        Object deserialized = PureBcs.deserializeFromBase64("vector<u64>", emptyVectorBase64);
        assertNotNull(deserialized);
        assertTrue(deserialized instanceof List);
        assertTrue(((List<?>) deserialized).isEmpty());
    }
    
    @Test
    void testLargeVectorSerialization() throws IOException {
        log.info("Testing large vector serialization...");
        
        // Create a list with a large number of elements.
        List<Long> largeList = Arrays.asList(
            1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L,
            11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L
        );
        
        String largeVectorBase64 = PureBcs.serializeToBase64("vector<u64>", largeList);
        log.info("large vector<u64>: {} -> {}", largeList, largeVectorBase64);
        
        // verify Base64encode
        assertNotNull(largeVectorBase64);
        assertFalse(largeVectorBase64.isEmpty());
        
        // Verify that deserialization can be performed correctly.
        Object deserialized = PureBcs.deserializeFromBase64("vector<u64>", largeVectorBase64);
        assertNotNull(deserialized);
        assertTrue(deserialized instanceof List);
        assertEquals(largeList.size(), ((List<?>) deserialized).size());
    }
    
    @Test
    void testInvalidBase64Deserialization() {
        log.info("Testing invalid Base64 deserialization...");
        
        // Test invalid Base64 strings.
        assertThrows(DecoderException.class, () -> {
            PureBcs.deserializeFromBase64("u64", "invalid_base64_string");
        });
        
        // test null string
        assertThrows(IOException.class, () -> {
            PureBcs.deserializeFromBase64("u64", "");
        });
        
        // test null
        assertThrows(NullPointerException.class, () -> {
            PureBcs.deserializeFromBase64("u64", null);
        });
    }
    
    @Test
    void testRoundTripSerialization() throws IOException {
        log.info("Testing round-trip serialization...");
        
        // Test complex data structures.
        List<String> complexList = Arrays.asList(
            "complex_string_1",
            "complex_string_2",
            "complex_string_3"
        );
        
        // serialize
        String base64 = PureBcs.serializeToBase64("vector<string>", complexList);
        log.info("Complex vector<string> serialized to Base64: {}", base64);
        
        // deserialize
        Object deserialized = PureBcs.deserializeFromBase64("vector<string>", base64);
        log.info("Complex vector<string> deserialized successfully");
        
        // Verify the deserialization result.
        assertNotNull(deserialized);
        assertTrue(deserialized instanceof List);
        List<?> deserializedList = (List<?>) deserialized;
        assertEquals(complexList.size(), deserializedList.size());
        
        for (int i = 0; i < complexList.size(); i++) {
            assertEquals(complexList.get(i), deserializedList.get(i));
        }
    }
    
    @Test
    void testSpecialCharactersInStrings() throws IOException {
        log.info("Testing special characters in strings...");
        
        // Create a string containing special characters.
        String specialString = "Hello!@#$%^&*()_+-=[]{}|;':\",./<>? World";
        
        String base64 = PureBcs.serializeToBase64("string", specialString);
        log.info("Special characters string serialized to Base64: {}", base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // Verify that deserialization can be performed correctly.
        Object deserialized = PureBcs.deserializeFromBase64("string", base64);
        assertNotNull(deserialized);
        assertEquals(specialString, deserialized);
    }
    
    @Test
    void testZeroValues() throws IOException {
        log.info("Testing zero values...");
        
        // test zero
        String zeroU64Base64 = PureBcs.serializeToBase64("u64", 0L);
        log.info("zero u64: 0 -> {}", zeroU64Base64);
        
        String falseBoolBase64 = PureBcs.serializeToBase64("bool", false);
        log.info("false bool: false -> {}", falseBoolBase64);
        
        // verify Base64encode
        assertNotNull(zeroU64Base64);
        assertNotNull(falseBoolBase64);
        assertFalse(zeroU64Base64.isEmpty());
        assertFalse(falseBoolBase64.isEmpty());
        
        // Verify that deserialization can be performed correctly.
        Object deserializedU64 = PureBcs.deserializeFromBase64("u64", zeroU64Base64);
        Object deserializedBool = PureBcs.deserializeFromBase64("bool", falseBoolBase64);
        
        assertEquals(0L, deserializedU64);
        assertEquals(false, deserializedBool);
    }
    
    @Test
    void testMaxValues() throws IOException {
        log.info("Testing max values...");
        
        // test max value
        long u64Max = Long.MAX_VALUE;
        String maxU64Base64 = PureBcs.serializeToBase64("u64", u64Max);
        log.info("max u64: {} -> {}", u64Max, maxU64Base64);

        BigInteger u128Max = BigInteger.TWO.pow(127).subtract(BigInteger.ONE);
        String maxU128Base64 = PureBcs.serializeToBase64("u128", u128Max);
        log.info("max u128: {} -> {}", u128Max, maxU128Base64);
        
        // verify Base64encode
        assertNotNull(maxU64Base64);
        assertNotNull(maxU128Base64);
        assertFalse(maxU64Base64.isEmpty());
        assertFalse(maxU128Base64.isEmpty());
        
        // Verify that deserialization can be performed correctly.
        Object deserializedU64 = PureBcs.deserializeFromBase64("u64", maxU64Base64);
        Object deserializedU128 = PureBcs.deserializeFromBase64("u128", maxU128Base64);
        
        assertEquals(u64Max, deserializedU64);
        assertEquals(u128Max, deserializedU128);
    }
    
    @Test
    void testMultipleSerializations() throws IOException {
        log.info("Testing multiple serializations...");
        
        // Test serializing the same object multiple times.
        String base641 = PureBcs.serializeToBase64("u64", 123456789L);
        String base642 = PureBcs.serializeToBase64("u64", 123456789L);
        String base643 = PureBcs.serializeToBase64("u64", 123456789L);
        
        log.info("First serialization: {}", base641);
        log.info("Second serialization: {}", base642);
        log.info("Third serialization: {}", base643);

        // Verify that all serialization results are identical.
        assertEquals(base641, base642);
        assertEquals(base642, base643);
        
        // Verify that all serialization results can be deserialized correctly.
        Object deserialized1 = PureBcs.deserializeFromBase64("u64", base641);
        Object deserialized2 = PureBcs.deserializeFromBase64("u64", base642);
        Object deserialized3 = PureBcs.deserializeFromBase64("u64", base643);
        
        assertEquals(123456789L, deserialized1);
        assertEquals(123456789L, deserialized2);
        assertEquals(123456789L, deserialized3);
    }
    
    @Test
    void testInvalidTypeNames() {
        log.info("Testing invalid type names...");
        
        // Test invalid type names
        assertThrows(IllegalArgumentException.class, () -> {
            PureBcs.serializeToBase64("invalid_type", "value");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            PureBcs.serializeToBase64("", "value");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            PureBcs.serializeToBase64(null, "value");
        });
    }
    
    @Test
    void testIsValidPureTypeName() {
        log.info("Testing isValidPureTypeName method...");
        
        // Test valid type names.
        assertTrue(PureBcs.isValidPureTypeName("u64"));
        assertTrue(PureBcs.isValidPureTypeName("string"));
        assertTrue(PureBcs.isValidPureTypeName("bool"));
        assertTrue(PureBcs.isValidPureTypeName("address"));
        assertTrue(PureBcs.isValidPureTypeName("vector<u64>"));
        assertTrue(PureBcs.isValidPureTypeName("vector<string>"));
        assertTrue(PureBcs.isValidPureTypeName("option<u64>"));
        assertTrue(PureBcs.isValidPureTypeName("option<string>"));
        
        // Test invalid type names
        assertFalse(PureBcs.isValidPureTypeName("invalid_type"));
        assertFalse(PureBcs.isValidPureTypeName(""));
        assertFalse(PureBcs.isValidPureTypeName(null));
        assertFalse(PureBcs.isValidPureTypeName("vector<invalid_type>"));
        assertFalse(PureBcs.isValidPureTypeName("option<invalid_type>"));
    }
} 