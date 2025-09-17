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
import io.dipcoin.sui.model.coin.BalanceChange;
import io.dipcoin.sui.model.event.Event;
import io.dipcoin.sui.model.object.kind.ObjectChange;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

/**
 * @author : Same
 * @datetime : 2025/6/27 11:14
 * @Description : SuiTransactionBlockResponse response
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SuiTransactionBlockResponse {

    private List<BalanceChange> balanceChanges;

    /**
     * The checkpoint number when this transaction was included and hence finalized. This is only returned in the read api, not in the transaction execution api.
     */
    private BigInteger checkpoint;

    private Boolean confirmedLocalExecution;

    private String digest;

    private TransactionBlockEffects effects;

    private List<String> errors;

    private List<Event> events;

    private List<ObjectChange> objectChanges;

    /**
     * BCS encoded [SenderSignedData] that includes input object references returns empty array if show_raw_transaction is false
     */
    private String rawTransaction;

    private BigInteger timestampMs;

    /**
     * Transaction input data
     */
    private TransactionBlock transaction;

}
