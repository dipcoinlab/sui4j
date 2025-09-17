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

package io.dipcoin.sui.model.transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.dipcoin.sui.model.object.ObjectRef;
import io.dipcoin.sui.model.object.kind.InputObjectKind;
import lombok.Data;

import java.util.List;

/**
 * @author : Same
 * @datetime : 2025/6/26 19:25
 * @Description : TransactionBlockBytes
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionBlockBytes {

    /**
     * BCS serialized transaction data bytes without its type tag, as base-64 encoded string.
     */
    private String txBytes;

    /**
     * the gas object to be used
     */
    private List<ObjectRef> gas;

    /**
     * objects to be used in this transaction
     */
    private List<InputObjectKind> inputObjects;

}
