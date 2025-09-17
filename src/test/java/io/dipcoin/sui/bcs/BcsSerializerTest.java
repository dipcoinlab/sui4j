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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author : Same
 * @datetime : 2025/7/11 12:35
 * @Description : BcsSerializer unit test
 * Validate the correctness of BCS encoding.
 */
@Slf4j
public class BcsSerializerTest {
    
    private BcsSerializer serializer;
    
    @BeforeEach
    void setUp() {
        serializer = new BcsSerializer();
    }
    
    @Test
    void testWriteU8() throws IOException {
        byte value = 42;
        serializer.writeU8(value);
        String base64 = serializer.toBase64();
        log.info("{} -> {}", value, base64);

        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // verify decode
        byte[] decoded = Base64.decode(base64);
        assertEquals(1, decoded.length);
        assertEquals(value, decoded[0]);
    }
    
    @Test
    void testWriteU16() throws IOException {
        short value = 12345;
        serializer.writeU16(value);
        String base64 = serializer.toBase64();
        log.info("{} -> {}", value, base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // verify decode
        byte[] decoded = Base64.decode(base64);
        assertEquals(2, decoded.length);
        
        // Validate little-endian byte order
        short decodedValue = (short) ((decoded[1] << 8) | (decoded[0] & 0xFF));
        assertEquals(value, decodedValue);
    }
    
    @Test
    void testWriteU32() throws IOException {
        int value = 123456789;
        serializer.writeU32(value);
        String base64 = serializer.toBase64();
        log.info("{} -> {}", value, base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // verify decode
        byte[] decoded = Base64.decode(base64);
        assertEquals(4, decoded.length);
        
        // Validate little-endian byte order
        int decodedValue = ((decoded[3] & 0xFF) << 24) | 
                          ((decoded[2] & 0xFF) << 16) | 
                          ((decoded[1] & 0xFF) << 8) | 
                          (decoded[0] & 0xFF);
        assertEquals(value, decodedValue);
    }
    
    @Test
    void testWriteU64() throws IOException {
        long value = 1234567890123456789L;
        serializer.writeU64(value);
        String base64 = serializer.toBase64();
        log.info("{} -> {}", value, base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // verify decode
        byte[] decoded = Base64.decode(base64);
        assertEquals(8, decoded.length);
        
        // Validate little-endian byte order
        long decodedValue = ((long) (decoded[7] & 0xFF) << 56) |
                           ((long) (decoded[6] & 0xFF) << 48) |
                           ((long) (decoded[5] & 0xFF) << 40) |
                           ((long) (decoded[4] & 0xFF) << 32) |
                           ((long) (decoded[3] & 0xFF) << 24) |
                           ((long) (decoded[2] & 0xFF) << 16) |
                           ((long) (decoded[1] & 0xFF) << 8) |
                           (decoded[0] & 0xFF);
        assertEquals(value, decodedValue);
    }
    
    @Test
    void testWriteU128() throws IOException {
        BigInteger value = BigInteger.valueOf(1234567890123456789L);
        serializer.writeU128(value);
        String base64 = serializer.toBase64();
        log.info("{} -> {}", value, base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // verify decode
        byte[] decoded = Base64.decode(base64);
        assertEquals(16, decoded.length);
    }
    
    @Test
    void testWriteU256() throws IOException {
        BigInteger value = BigInteger.valueOf(1234567890123456789L);
        serializer.writeU256(value);
        String base64 = serializer.toBase64();
        log.info("{} -> {}", value, base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // verify decode
        byte[] decoded = Base64.decode(base64);
        assertEquals(32, decoded.length);
    }
    
    @Test
    void testWriteBool() throws IOException {
        boolean value = true;
        serializer.writeBool(value);
        String base64 = serializer.toBase64();
        log.info("{} -> {}", value, base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // verify decode
        byte[] decoded = Base64.decode(base64);
        assertEquals(1, decoded.length);
        assertEquals(1, decoded[0]);
        
        // test false
        serializer.reset();
        serializer.writeBool(false);
        base64 = serializer.toBase64();
        decoded = Base64.decode(base64);
        assertEquals(1, decoded.length);
        assertEquals(0, decoded[0]);
    }
    
    @Test
    void testWriteString() throws IOException {
        String value = "Hello, World!";
        serializer.writeString(value);
        String base64 = serializer.toBase64();
        log.info("{} -> {}", value, base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // verify decode
        byte[] decoded = Base64.decode(base64);
        assertTrue(decoded.length > 4); // Length prefix + string content.
        
        // Validate the length prefix (little-endian)
        BcsDeserializer bcsDeserializer = new BcsDeserializer(decoded);
        int length = bcsDeserializer.readUleb128();
        assertEquals(value.length(), length);
    }
    
    @Test
    void testWriteBytes() throws IOException {
        byte[] value = {1, 2, 3, 4, 5};
        serializer.writeBytes(value);
        String base64 = serializer.toBase64();
        log.info("{} -> {}", value, base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // verify decode
        byte[] decoded = Base64.decode(base64);
        assertTrue(decoded.length > 4); // Length prefix + string content.
        
        // Validate the length prefix (little-endian)
        BcsDeserializer bcsDeserializer = new BcsDeserializer(decoded);
        int length = bcsDeserializer.readUleb128();
        assertEquals(value.length, length);
        
        // verify content
        byte[] content = Arrays.copyOfRange(decoded, 1, decoded.length);
        assertArrayEquals(value, content);
    }
    
    @Test
    void testWriteVector() throws IOException {
        List<Integer> values = Arrays.asList(1, 2, 3, 4, 5);
        serializer.writeVector(values, (s, v) -> s.writeU32(v));
        String base64 = serializer.toBase64();
        log.info("{} -> {}", values, base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // verify decode
        byte[] decoded = Base64.decode(base64);
        assertTrue(decoded.length > 4); // length prefix + vector content
        
        // Validate the length prefix (little-endian)
        BcsDeserializer bcsDeserializer = new BcsDeserializer(decoded);
        int length = bcsDeserializer.readUleb128();
        assertEquals(values.size(), length);
    }
    
    @Test
    void testWriteOption() throws IOException {
        // test Some
        int value = 42;
        serializer.writeOption(value, (s, v) -> s.writeU32(v));
        String base64 = serializer.toBase64();
        log.info("{} -> {}", value, base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // verify decode
        byte[] decoded = Base64.decode(base64);
        assertEquals(5, decoded.length); // 1-byte variant + 4-byte value.
        assertEquals(1, decoded[0]); // Some variant
        
        // test None
//        serializer.reset();
//        serializer.writeOption(null, (s, v) -> s.writeU32(v));
//        base64 = serializer.toBase64();
//        decoded = Base64.decode(base64);
//        assertEquals(1, decoded.length);
//        assertEquals(0, decoded[0]); // None variant
    }
    
    @Test
    void testComplexSerialization() throws IOException {
        // test complex serialization
        serializer.writeU32(12345);
        serializer.writeString("test");
        serializer.writeBool(true);
        serializer.writeU64(987654321L);
        
        String base64 = serializer.toBase64();
        log.info("result : {}", base64);
        
        // verify Base64 encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // Verify that decoding is performed correctly.
        byte[] decoded = Base64.decode(base64);
        assertTrue(decoded.length > 0);
    }
    
    @Test
    void testReset() throws IOException {
        serializer.writeU32(12345);
        String base641 = serializer.toBase64();
        
        serializer.reset();
        serializer.writeU32(67890);
        String base642 = serializer.toBase64();
        
        // Verify that the serialization results differ between the two instances.
        assertNotEquals(base641, base642);
    }
} 