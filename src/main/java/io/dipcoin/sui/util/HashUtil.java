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

package io.dipcoin.sui.util;

import org.bitcoinj.core.Base58;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author : Same
 * @datetime : 2025/7/8 15:54
 * @Description : hash utility class
 */
public class HashUtil {

    private static final MessageDigest digest;
    private static final Charset UTF8 = StandardCharsets.UTF_8;

    static {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get sha256 result
     * @param message
     * @return
     */
    public static byte[] sha256(byte[] message) {
        return digest.digest(message);
    }

    /**
     * Get sha256 Base58 result
     * @param message
     * @return
     */
    public static String sha256Base58(byte[] message) {
        return Base58.encode(sha256(message));
    }

    /**
     * Get sha256 Base58 result
     * @param message
     * @return
     */
    public static String sha256Base58(String message) {
        return Base58.encode(sha256(message.getBytes(UTF8)));
    }
}
