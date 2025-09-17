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

/**
 * @author : Same
 * @datetime : 2025/8/5 11:52
 * @Description : signutility class
 */
public class SignatureUtil {

    /**
     * get public key by signature
     * @param signature
     * @return
     */
    public static String getPublicKey(String signature) {
        if (signature == null || signature.isEmpty()) {
            return "";
        }
        int lastIndex = signature.length() - 44;
        return signature.substring(lastIndex);
    }

    /**
     * get single signature by signature
     * @param signature
     * @return
     */
    public static String getSignature(String signature) {
        if (signature == null || signature.isEmpty()) {
            return "";
        }
        int lastIndex = signature.length() - 44;
        return signature.substring(0, lastIndex);
    }
}
