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

package io.dipcoin.sui.protocol.http.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dipcoin.sui.model.move.SuiTransactionBlockBuilderMode;
import io.dipcoin.sui.model.transaction.kind.RPCTransactionRequestParams;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

/**
 * @author : Same
 * @datetime : 2025/7/15 12:24
 * @Description : unsafe_batchTransaction request
 */
@Data
public class UnsafeBatchTransaction {

    private String signer;

    @JsonProperty("single_transaction_params")
    private List<RPCTransactionRequestParams> singleTransactionParams;

    /**
     * required = false
     * ObjectId for paying gas
     */
    private String gas;

    @JsonProperty("gas_budget")
    private BigInteger gasBudget;

    /**
     * required = false
     * "Normal" transactions or "dev check" transactions. Default to normal on-chain submission.
     */
    @JsonProperty("execution_mode")
    private String executionMode = SuiTransactionBlockBuilderMode.COMMIT.getMode();


}
