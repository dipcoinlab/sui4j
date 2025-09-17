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

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * @author : Same
 * @datetime : 2025/6/27 11:28
 * @Description : transaction request
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transaction {

    /**
     * BCS serialized transaction data (Base64 encoded)
     */
    private String txBytes;

    /**
     * sign list(Base64encode)
     */
    private List<String> signatures;

    /**
     * Response options configuration (default all enabled)
     */
    private TransactionBlockResponseOptions options = TransactionBlockResponseOptions.allTrue();

    /**
     * @see ExecuteTransactionRequestType
     * Request type (default WaitForEffectsCert for quick transaction submission confirmation)
     */
    private String requestType = ExecuteTransactionRequestType.WAIT_FOR_EFFECTS_CERT.getType();

}
