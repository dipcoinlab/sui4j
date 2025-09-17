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

import org.bouncycastle.util.encoders.Base64;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : Same
 * @datetime : 2025/7/10 20:42
 * @Description : Pure type BCS serializer, provides BCS serialization functionality for primitive types and generic types, corresponding to TypeScript's `pure.ts`.
 */
public class PureBcs {
    
    private static final Pattern VECTOR_PATTERN = Pattern.compile("^vector<(.+)>$");
    private static final Pattern OPTION_PATTERN = Pattern.compile("^option<(.+)>$");
    
    /**
     * Basic primitive types.
     */
    public enum BasePureType {
        U8, U16, U32, U64, U128, U256, BOOL, ID, STRING, ADDRESS, VECTOR_U8
    }
    
    /**
     * Get the BCS serializer by type name.
     */
    public static BcsSerializer.BcsTypeSerializer<?> getSerializer(String typeName) {
        if (typeName == null || typeName.trim().isEmpty()) {
            throw new IllegalArgumentException("Type name cannot be null or empty");
        }
        
        typeName = typeName.trim();
        
        // primitive types
        switch (typeName.toLowerCase()) {
            case "u8":
                return (serializer, value) -> serializer.writeUleb128(1).writeU8((Byte) value);
            case "u16":
                return (serializer, value) -> serializer.writeUleb128(2).writeU16((Short) value);
            case "u32":
                return (serializer, value) -> serializer.writeUleb128(4).writeU32((Integer) value);
            case "u64":
                return (serializer, value) -> serializer.writeUleb128(8).writeU64((Long) value);
            case "u128":
                return (serializer, value) -> serializer.writeUleb128(16).writeU128((BigInteger) value);
            case "u256":
                return (serializer, value) -> serializer.writeUleb128(32).writeU256((BigInteger) value);
            case "bool":
                return (serializer, value) -> serializer.writeUleb128(1).writeBool((Boolean) value);
            case "string":
                return (serializer, value) -> serializer.writeString((String) value);
            case "id":
            case "address":
                return (serializer, value) -> serializer.writeUleb128(32).writeAddress((String) value);
            case "vector_u8":
                return (serializer, value) -> serializer.writeVector((byte[]) value);
        }
        
        // vector type
        Matcher vectorMatch = VECTOR_PATTERN.matcher(typeName);
        if (vectorMatch.matches()) {
            String elementType = vectorMatch.group(1);
            BcsSerializer.BcsTypeSerializer<?> elementSerializer = getSerializer(elementType);
            return (serializer, value) -> {
                @SuppressWarnings("unchecked")
                List<Object> list = (List<Object>) value;
                serializer.writeVector(list, (BcsSerializer.BcsTypeSerializer<Object>) elementSerializer);
            };
        }
        
        // option type
        Matcher optionMatch = OPTION_PATTERN.matcher(typeName);
        if (optionMatch.matches()) {
            String elementType = optionMatch.group(1);
            BcsSerializer.BcsTypeSerializer<?> elementSerializer = getSerializer(elementType);
            return (serializer, value) -> {
                serializer.writeOption(value, (BcsSerializer.BcsTypeSerializer<Object>) elementSerializer);
            };
        }
        
        throw new IllegalArgumentException("Invalid Pure type name: " + typeName);
    }
    
    /**
     * Validate if the primitive type name is valid.
     */
    public static boolean isValidPureTypeName(String typeName) {
        if (typeName == null || typeName.trim().isEmpty()) {
            return false;
        }
        
        typeName = typeName.trim();
        
        // Check primitive types.
        for (BasePureType type : BasePureType.values()) {
            if (typeName.equalsIgnoreCase(type.name())) {
                return true;
            }
        }
        
        // check vector type
        Matcher vectorMatcher = VECTOR_PATTERN.matcher(typeName);
        if (vectorMatcher.matches()) {
            String elementType = vectorMatcher.group(1);
            return isValidPureTypeName(elementType);
        }
        
        // check option type
        Matcher optionMatcher = OPTION_PATTERN.matcher(typeName);
        if (optionMatcher.matches()) {
            String elementType = optionMatcher.group(1);
            return isValidPureTypeName(elementType);
        }
        
        return false;
    }
    
    /**
     * Serialize primitive type value to Base64.
     */
    public static String serializeToBase64(String typeName, Object value) throws IOException {
        BcsSerializer.BcsTypeSerializer<?> serializer = getSerializer(typeName);
        BcsSerializer bcsSerializer = new BcsSerializer();
        ((BcsSerializer.BcsTypeSerializer<Object>) serializer).serialize(bcsSerializer, value);
        return bcsSerializer.toBase64();
    }
    
    /**
     * Deserialize primitive type value from Base64.
     */
    public static Object deserializeFromBase64(String typeName, String base64) throws IOException {
        byte[] data = Base64.decode(base64);
        BcsDeserializer deserializer = new BcsDeserializer(data);
        return deserializeValue(deserializer, typeName);
    }
    
    /**
     * Deserialize value
     */
    private static Object deserializeValue(BcsDeserializer deserializer, String typeName) throws IOException {
        switch (typeName.toLowerCase()) {
            case "u8":
                deserializer.readUleb128();
                return deserializer.readU8();
            case "u16":
                deserializer.readUleb128();
                return deserializer.readU16();
            case "u32":
                deserializer.readUleb128();
                return deserializer.readU32();
            case "u64":
                deserializer.readUleb128();
                return deserializer.readU64();
            case "u128":
                deserializer.readUleb128();
                return deserializer.readU128();
            case "u256":
                deserializer.readUleb128();
                return deserializer.readU256();
            case "bool":
                deserializer.readUleb128();
                return deserializer.readBool();
            case "string":
                return deserializer.readString();
            case "id":
            case "address":
                deserializer.readUleb128();
                return deserializer.readString(); // Process address as a string.
            case "vector_u8":
                deserializer.readUleb128();
                return deserializer.readVector();
        }
        
        // vector type
        Matcher vectorMatch = VECTOR_PATTERN.matcher(typeName);
        if (vectorMatch.matches()) {
            String elementType = vectorMatch.group(1);
            return deserializer.readVector(d -> {
                try {
                    return deserializeValue(d, elementType);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to deserialize vector element", e);
                }
            });
        }
        
        // option type
        Matcher optionMatch = OPTION_PATTERN.matcher(typeName);
        if (optionMatch.matches()) {
            String elementType = optionMatch.group(1);
            return deserializer.readOption(d -> {
                try {
                    return deserializeValue(d, elementType);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to deserialize option element", e);
                }
            });
        }
        
        throw new IllegalArgumentException("Invalid Pure type name: " + typeName);
    }
} 