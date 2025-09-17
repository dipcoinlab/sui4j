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

import io.dipcoin.sui.pyth.constant.PythConfig;

/**
 * @author : Same
 * @datetime : 2025/7/17 10:48
 * @Description : ObjectId utility class
 */
public class ObjectIdUtil {

    private static final String HEX_PREFIX = "0x";

    /**
     * Get Pyth feedObjectId
     * Remove prefix and suffix
     * @param type
     * @return
     */
    public static String getFeedObjectId(String type) {
        String tmp = type.replace(PythConfig.TABLE_ID_PREFIX, "");
        return tmp.replace(PythConfig.TABLE_ID_SUFFIX, "");
    }


    /**
     * Normalize Sui address
     * @param address
     * @return
     */
    public static String normalizeSuiAddress(String address) {
        if (address == null || address.isEmpty()) {
            return address;
        }

        // Remove 0x prefix (if exists)
        if (address.startsWith(HEX_PREFIX)) {
            address = address.substring(2);
        }

        // Ensure address length is 64 characters (32 bytes)
        if (address.length() < 64) {
            address = "0".repeat(64 - address.length()) + address;
        }

        return HEX_PREFIX + address;
    }
}
