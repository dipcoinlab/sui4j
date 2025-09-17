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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author : Same
 * @datetime : 2025/7/10 17:48
 * @Description : High-performance BCS deserializer providing low-latency, high-throughput BCS decoding functionality
 */
public class BcsDeserializer {
    
    private final ByteArrayInputStream input;
    private final ByteBuffer buffer;
    private final byte[] tempBuffer;
    
    public BcsDeserializer(byte[] data) {
        this.input = new ByteArrayInputStream(data);
        this.buffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        this.tempBuffer = new byte[8];
    }
    
    /**
     * Deserialize u8 type
     */
    public byte readU8() throws IOException {
        int value = input.read();
        if (value == -1) {
            throw new IOException("Unexpected end of input");
        }
        return (byte) value;
    }
    
    /**
     * Deserialize u16 type
     */
    public short readU16() throws IOException {
        int bytesRead = input.read(tempBuffer, 0, 2);
        if (bytesRead != 2) {
            throw new IOException("Unexpected end of input");
        }
        
        buffer.clear();
        buffer.put(tempBuffer, 0, 2);
        buffer.flip();
        return buffer.getShort();
    }
    
    /**
     * Deserialize u32 type
     */
    public int readU32() throws IOException {
        int bytesRead = input.read(tempBuffer, 0, 4);
        if (bytesRead != 4) {
            throw new IOException("Unexpected end of input");
        }
        
        buffer.clear();
        buffer.put(tempBuffer, 0, 4);
        buffer.flip();
        return buffer.getInt();
    }
    
    /**
     * Deserialize u64 type
     */
    public long readU64() throws IOException {
        int bytesRead = input.read(tempBuffer, 0, 8);
        if (bytesRead != 8) {
            throw new IOException("Unexpected end of input");
        }
        
        buffer.clear();
        buffer.put(tempBuffer, 0, 8);
        buffer.flip();
        return buffer.getLong();
    }
    
    /**
     * Deserialize u128 type
     */
    public BigInteger readU128() throws IOException {
        byte[] bytes = new byte[16];
        int bytesRead = input.read(bytes);
        if (bytesRead != 16) {
            throw new IOException("Unexpected end of input");
        }
        
        // Reverse byte order (little-endian to big-endian)
        for (int i = 0; i < 8; i++) {
            byte temp = bytes[i];
            bytes[i] = bytes[15 - i];
            bytes[15 - i] = temp;
        }
        
        return new BigInteger(bytes);
    }
    
    /**
     * Deserialize u256 type
     */
    public BigInteger readU256() throws IOException {
        byte[] bytes = new byte[32];
        int bytesRead = input.read(bytes);
        if (bytesRead != 32) {
            throw new IOException("Unexpected end of input");
        }
        
        // Reverse byte order (little-endian to big-endian)
        for (int i = 0; i < 16; i++) {
            byte temp = bytes[i];
            bytes[i] = bytes[31 - i];
            bytes[31 - i] = temp;
        }
        
        return new BigInteger(bytes);
    }
    
    /**
     * Deserialize boolean type
     */
    public boolean readBool() throws IOException {
        byte value = readU8();
        if (value != 0 && value != 1) {
            throw new IOException("Invalid boolean value: " + value);
        }
        return value == 1;
    }

    /**
     * Deserialize length ULEB128 encoding
     */
    public int readUleb128() throws IOException {
        int result = 0;
        int shift = 0;
        byte b;

        do {
            b = (byte) input.read();  // read next byte
            if (b == -1) {
                throw new IOException("Unexpected end of input during ULEB128 decoding");
            }

            // Concatenate 7-bit data into result
            result |= (b & 0x7F) << shift;
            shift += 7;

            // If shift exceeds 32 bits, the number value is too large
            if (shift >= 32) {
                throw new IOException("ULEB128 value exceeds maximum supported size (32-bit)");
            }
        } while ((b & 0x80) != 0);  // check continue bit

        return result;
    }
    
    /**
     * Deserialize string
     */
    public String readString() throws IOException {
        int length = readUleb128();
        byte[] bytes = new byte[length];
        int bytesRead = input.read(bytes);
        if (bytesRead != length) {
            throw new IOException("Unexpected end of input");
        }
        return new String(bytes);
    }
    
    /**
     * Deserialize address
     */
    public byte[] readAddress() throws IOException {
        byte[] bytes = new byte[32];
        int bytesRead = input.read(bytes);
        if (bytesRead != 32) {
            throw new IOException("Unexpected end of input");
        }
        return bytes;
    }

    /**
     * Deserialize byte array
     */
    public byte[] readBytes() throws IOException {
        int length = readUleb128();
        byte[] bytes = new byte[length];
        int bytesRead = input.read(bytes);
        if (bytesRead != length) {
            throw new IOException("Unexpected end of input");
        }
        return bytes;
    }
    
    /**
     * Deserialize vector<u8> vector
     */
    public byte[] readVector() throws IOException {
        int length = readUleb128();
        byte[] bytes = new byte[length];
        int bytesRead = input.read(bytes);
        if (bytesRead != length) {
            throw new IOException("Unexpected end of input");
        }
        return bytes;
    }

    /**
     * Deserialize vector
     */
    public <T> List<T> readVector(Function<BcsDeserializer, T> elementDeserializer) throws IOException {
        int size = readUleb128();
        List<T> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(elementDeserializer.apply(this));
        }
        return result;
    }
    
    /**
     * Deserialize Option type
     */
    public <T> T readOption(Function<BcsDeserializer, T> elementDeserializer) throws IOException {
        byte variant = readU8();
        if (variant == 0) {
            return null; // None
        } else if (variant == 1) {
            return elementDeserializer.apply(this); // Some
        } else {
            throw new IOException("Invalid option variant: " + variant);
        }
    }
    
    /**
     * Deserialize enum type
     */
    public <T> T readEnum(Map<Integer, Function<BcsDeserializer, T>> variantMap) throws IOException {
        int variant = readU32();
        Function<BcsDeserializer, T> deserializer = variantMap.get(variant);
        if (deserializer == null) {
            throw new IOException("Unknown enum variant: " + variant);
        }
        return deserializer.apply(this);
    }
    
    /**
     * Deserialize struct
     */
    public <T> T readStruct(Function<BcsDeserializer, T> structDeserializer) throws IOException {
        return structDeserializer.apply(this);
    }
    
    /**
     * Check if there is more data
     */
    public boolean hasMore() {
        return input.available() > 0;
    }
    
    /**
     * Get remaining bytes
     */
    public int available() {
        return input.available();
    }
    
    /**
     * Skip specified bytes
     */
    public long skip(long n) throws IOException {
        return input.skip(n);
    }
    
    /**
     * Reset deserializer
     */
    public void reset() {
        input.reset();
    }
    
    /**
     * Close resources
     */
    public void close() throws IOException {
        input.close();
    }
} 