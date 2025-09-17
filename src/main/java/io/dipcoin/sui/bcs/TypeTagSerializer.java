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

import io.dipcoin.sui.bcs.types.tag.*;
import io.dipcoin.sui.util.ObjectIdUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : Same
 * @datetime : 2025/7/10 18:25
 * @Description : TypeTag serializer, provides TypeTag string parsing and serialization functionality, corresponding to TypeScript's `TypeTagSerializer`.
 */
public class TypeTagSerializer {
    
    private static final Pattern VECTOR_REGEX = Pattern.compile("^vector<(.+)>$");
    private static final Pattern STRUCT_REGEX = Pattern.compile("^([^:]+)::([^:]+)::([^<]+)(<(.+)>)?");
    
    /**
     * Parse TypeTag from string.
     */
    public static TypeTag parseFromStr(String str) {
        return parseFromStr(str, false);
    }
    
    /**
     * Parse TypeTag from string.
     * @param str Type string
     * @param normalizeAddress Whether to normalize the address
     */
    public static TypeTag parseFromStr(String str, boolean normalizeAddress) {
        if (str == null || str.trim().isEmpty()) {
            throw new IllegalArgumentException("Type string cannot be null or empty");
        }
        
        str = str.trim();
        
        // primitive types
        switch (str) {
            case "address":
                return TypeTagAddress.INSTANCE;
            case "bool":
                return TypeTagBool.INSTANCE;
            case "u8":
                return TypeTagU8.INSTANCE;
            case "u16":
                return TypeTagU16.INSTANCE;
            case "u32":
                return TypeTagU32.INSTANCE;
            case "u64":
                return TypeTagU64.INSTANCE;
            case "u128":
                return TypeTagU128.INSTANCE;
            case "u256":
                return TypeTagU256.INSTANCE;
            case "signer":
                return TypeTagSigner.INSTANCE;
        }
        
        // vector type
        Matcher vectorMatch = VECTOR_REGEX.matcher(str);
        if (vectorMatch.matches()) {
            TypeTag elementType = parseFromStr(vectorMatch.group(1), normalizeAddress);
            return new TypeTagVector(elementType);
        }
        
        // struct type
        Matcher structMatch = STRUCT_REGEX.matcher(str);
        if (structMatch.matches()) {
            String address = structMatch.group(1);
            if (normalizeAddress) {
                address = ObjectIdUtil.normalizeSuiAddress(address);
            }
            
            String module = structMatch.group(2);
            String name = structMatch.group(3);
            String typeParamsStr = structMatch.group(5);
            
            List<TypeTag> typeParams = new ArrayList<>();
            if (typeParamsStr != null && !typeParamsStr.trim().isEmpty()) {
                typeParams = parseStructTypeArgs(typeParamsStr, normalizeAddress);
            }
            
            TypeTagStructTag structTag = new TypeTagStructTag(address, module, name, typeParams);
            return new TypeTagStruct(structTag);
        }
        
        throw new IllegalArgumentException("Encountered unexpected token when parsing type args for " + str);
    }
    
    /**
     * Parse struct type parameters.
     */
    public static List<TypeTag> parseStructTypeArgs(String str, boolean normalizeAddress) {
        List<TypeTag> result = new ArrayList<>();
        List<String> tokens = splitGenericParameters(str);
        
        for (String token : tokens) {
            result.add(parseFromStr(token, normalizeAddress));
        }
        
        return result;
    }
    
    /**
     * Convert TypeTag to string.
     */
    public static String tagToString(TypeTag tag) {
        if (tag instanceof TypeTagBool) {
            return "bool";
        }
        if (tag instanceof TypeTagU8) {
            return "u8";
        }
        if (tag instanceof TypeTagU16) {
            return "u16";
        }
        if (tag instanceof TypeTagU32) {
            return "u32";
        }
        if (tag instanceof TypeTagU64) {
            return "u64";
        }
        if (tag instanceof TypeTagU128) {
            return "u128";
        }
        if (tag instanceof TypeTagU256) {
            return "u256";
        }
        if (tag instanceof TypeTagAddress) {
            return "address";
        }
        if (tag instanceof TypeTagSigner) {
            return "signer";
        }
        if (tag instanceof TypeTagVector) {
            TypeTagVector vector = (TypeTagVector) tag;
            return "vector<" + tagToString(vector.getElementType()) + ">";
        }
        if (tag instanceof TypeTagStruct) {
            TypeTagStruct struct = (TypeTagStruct) tag;
            TypeTagStructTag structTag = struct.getStructTag();
            
            StringBuilder sb = new StringBuilder();
            sb.append(structTag.getAddress())
              .append("::")
              .append(structTag.getModule())
              .append("::")
              .append(structTag.getName());
            
            List<TypeTag> typeParams = structTag.getTypeParams();
            if (!typeParams.isEmpty()) {
                sb.append("<");
                for (int i = 0; i < typeParams.size(); i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(tagToString(typeParams.get(i)));
                }
                sb.append(">");
            }
            
            return sb.toString();
        }
        
        throw new IllegalArgumentException("Invalid TypeTag");
    }
    
    /**
     * Split generic parameters.
     */
    private static List<String> splitGenericParameters(String str) {
        List<String> result = new ArrayList<>();
        int depth = 0;
        int start = 0;
        
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            
            if (c == '<') {
                depth++;
            } else if (c == '>') {
                depth--;
            } else if (c == ',' && depth == 0) {
                result.add(str.substring(start, i).trim());
                start = i + 1;
            }
        }
        
        if (start < str.length()) {
            result.add(str.substring(start).trim());
        }
        
        return result;
    }
} 