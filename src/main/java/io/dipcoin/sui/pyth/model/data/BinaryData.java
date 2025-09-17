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

package io.dipcoin.sui.pyth.model.data;

import lombok.Data;

import java.util.List;

/**
 * @author : Same
 * @datetime : 2025/7/17 17:38
 * @Description : Pyth raw binary data
 */
@Data
public class BinaryData {

    /**
     * Encoding format (usually hex)
     */
    private String encoding;

    /**
     * Encoded data array (Hex string)
     */
    private List<String> data;
}
