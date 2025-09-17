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

import io.dipcoin.sui.util.Numeric;
import io.dipcoin.sui.util.ObjectIdUtil;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : Same
 * @datetime : 2025/7/10 16:35
 * @Description : High-performance BCS serializer providing low-latency, high-throughput BCS encoding functionality
 */
public class BcsSerializer {
    
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private static final Map<Class<?>, BcsTypeSerializer<?>> TYPE_CACHE = new ConcurrentHashMap<>();
    
    private final ByteArrayOutputStream output;
    private final ByteBuffer buffer;
    
    public BcsSerializer() {
        this(DEFAULT_BUFFER_SIZE);
    }
    
    public BcsSerializer(int initialSize) {
        this.output = new ByteArrayOutputStream(initialSize);
        this.buffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
    }
    
    /**
     * Serialize u8 type
     */
    public BcsSerializer writeU8(byte value) throws IOException {
        output.write(value);
        return this;
    }
    
    /**
     * Serialize u16 type
     */
    public BcsSerializer writeU16(short value) throws IOException {
        buffer.clear();
        buffer.putShort(value);
        output.write(buffer.array(), 0, 2);
        return this;
    }
    
    /**
     * Serialize u32 type
     */
    public BcsSerializer writeU32(int value) throws IOException {
        buffer.clear();
        buffer.putInt(value);
        output.write(buffer.array(), 0, 4);
        return this;
    }
    
    /**
     * Serialize u64 type
     */
    public BcsSerializer writeU64(long value) throws IOException {
        buffer.clear();
        buffer.putLong(value);
        output.write(buffer.array(), 0, 8);
        return this;
    }
    
    /**
     * Serialize u128 type
     */
    public BcsSerializer writeU128(BigInteger value) throws IOException {
        if (value.compareTo(BigInteger.ZERO) < 0) {
            throw new IllegalArgumentException("u128 must be non-negative");
        }

        // Ensure little-endian byte order.
        byte[] bytes = value.toByteArray();
        int length = bytes.length;
        if (length > 16) {
            throw new IllegalArgumentException("u128 value too large");
        }
        // Calculate the number of padding bytes required.
        int padding = 16 - length;

        // Pad to 16 bytes.
        byte[] result = new byte[16];
        System.arraycopy(bytes, 0, result, padding, length);

        // Reverse byte order (little-endian).
        for (int i = 0; i < 8; i++) {
            byte temp = result[i];
            result[i] = result[15 - i];
            result[15 - i] = temp;
        }

        output.write(result);
        return this;
    }
    
    /**
     * Serialize u256 type
     */
    public BcsSerializer writeU256(BigInteger value) throws IOException {
        if (value.compareTo(BigInteger.ZERO) < 0) {
            throw new IllegalArgumentException("u256 must be non-negative");
        }

        // Ensure little-endian byte order.
        byte[] bytes = value.toByteArray();
        int length = bytes.length;
        if (length > 32) {
            throw new IllegalArgumentException("u256 value too large");
        }

        // Calculate the number of padding bytes required.
        int padding = 32 - length;
        
        byte[] result = new byte[32];
        System.arraycopy(bytes, 0, result, padding, length);
        
        // Reverse byte order (little-endian).
        for (int i = 0; i < 16; i++) {
            byte temp = result[i];
            result[i] = result[31 - i];
            result[31 - i] = temp;
        }

        output.write(result);
        return this;
    }
    
    /**
     * Serialize boolean type
     */
    public BcsSerializer writeBool(boolean value) throws IOException {
        output.write(value ? 1 : 0);
        return this;
    }

    /**
     * Serialize length ULEB128 encoding
     */
    public BcsSerializer writeUleb128(int value) throws IOException {
        do {
            byte b = (byte) (value & 0x7F);
            value >>= 7;
            if (value != 0) {
                b |= 0x80; // Mark that there are subsequent bytes.
            }
            output.write(b);
        } while (value != 0);
        return this;
    }

    /**
     * Serialize Pure length nested ULEB128 encoding
     */
    public BcsSerializer writeUleb128Pure(int length) throws IOException {
        // encode innerLen to ULEB128
        byte[] innerLenUleb = this.uleb128Encode(length);

        // encode totalLen to ULEB128
        int totalLen = innerLenUleb.length + length;
        byte[] totalLenUleb = uleb128Encode(totalLen);

        // write totalLen
        output.write(totalLenUleb);
        // write innerLen
        output.write(innerLenUleb);
        return this;
    }

    /**
     * Get length ULEB128 encoded byte array
     * @param value
     * @return
     */
    public byte[] uleb128Encode(int value) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        do {
            byte b = (byte) (value & 0x7F);
            value >>>= 7;
            if (value != 0) {
                b |= 0x80;
            }
            out.write(b);
        } while (value != 0);
        return out.toByteArray();
    }

    /**
     * Serialize string
     */
    public BcsSerializer writeString(String value) throws IOException {
        byte[] bytes = value.getBytes();
        writeUleb128(bytes.length);
        output.write(bytes);
        return this;
    }

    /**
     * Serialize byte array
     */
    public BcsSerializer writeBytes(byte[] value) throws IOException {
        writeUleb128(value.length);
        output.write(value);
        return this;
    }

    /**
     * Serialize address
     */
    public BcsSerializer writeAddress(String address) throws IOException {
        byte[] value = Numeric.hexStringToByteArray(ObjectIdUtil.normalizeSuiAddress(address));
        output.write(value);
        return this;
    }
    
    /**
     * Serialize vector<u8> vector
     */
    public <T> BcsSerializer writeVector(byte[] value) throws IOException {
        writeUleb128Pure(value.length);
        output.write(value);
        return this;
    }

    /**
     * Serialize vector
     */
    public <T> BcsSerializer writeVector(List<T> value, BcsTypeSerializer<T> elementSerializer) throws IOException {
        writeUleb128(value.size());
        for (T item : value) {
            elementSerializer.serialize(this, item);
        }
        return this;
    }
    
    /**
     * Serialize Option type
     */
    public <T> BcsSerializer writeOption(T value, BcsTypeSerializer<T> elementSerializer) throws IOException {
        if (value == null) {
            writeU8((byte) 0); // None
        } else {
            writeU8((byte) 1); // Some
            elementSerializer.serialize(this, value);
        }
        return this;
    }
    
    /**
     * Serialize enum type
     */
    public <T> BcsSerializer writeEnum(T value, Map<T, Integer> variantMap, BcsTypeSerializer<T> elementSerializer) throws IOException {
        Integer variant = variantMap.get(value);
        if (variant == null) {
            throw new IllegalArgumentException("Unknown enum variant: " + value);
        }
        writeUleb128(variant);
        elementSerializer.serialize(this, value);
        return this;
    }
    
    /**
     * Serialize struct
     */
    public <T> BcsSerializer writeStruct(T value, BcsTypeSerializer<T> structSerializer) throws IOException {
        structSerializer.serialize(this, value);
        return this;
    }
    
    /**
     * Get serialization result
     */
    public byte[] toByteArray() {
        return output.toByteArray();
    }
    
    /**
     * Get Base64 encoded serialization result
     */
    public String toBase64() {
        return Base64.toBase64String(toByteArray());
    }

    /**
     * Get Hex encoded serialization result
     */
    public String toHex() {
        return Hex.toHexString(toByteArray());
    }
    
    /**
     * Reset serializer
     */
    public void reset() {
        output.reset();
    }
    
    /**
     * Close resources
     */
    public void close() throws IOException {
        output.close();
    }
    
    /**
     * BCS type serializer interface
     */
    @FunctionalInterface
    public interface BcsTypeSerializer<T> {
        void serialize(BcsSerializer serializer, T value) throws IOException;
    }
    
    /**
     * Get cached type serializer
     */
    @SuppressWarnings("unchecked")
    public static <T> BcsTypeSerializer<T> getCachedSerializer(Class<T> clazz) {
        return (BcsTypeSerializer<T>) TYPE_CACHE.computeIfAbsent(clazz, k -> {
            throw new IllegalArgumentException("No serializer registered for type: " + clazz);
        });
    }
    
    /**
     * Register type serializer
     */
    public static <T> void registerSerializer(Class<T> clazz, BcsTypeSerializer<T> serializer) {
        TYPE_CACHE.put(clazz, serializer);
    }
} 