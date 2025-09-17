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

package io.dipcoin.sui.model.transaction.kind.block;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dipcoin.sui.model.transaction.kind.TransactionBlockKind;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author : Same
 * @datetime : 2025/6/27 15:32
 * @Description : TransactionBlockKind.ChangeEpoch response
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangeEpoch extends TransactionBlockKind {

    @JsonProperty("computation_charge")
    private BigInteger computationCharge;

    private BigInteger epoch;

    @JsonProperty("epoch_start_timestamp_ms")
    private BigInteger epochStartTimestampMs;

    @JsonProperty("storage_charge")
    private BigInteger storageCharge;

    @JsonProperty("storage_rebate")
    private BigInteger storageRebate;

    public ChangeEpoch() {
        this.kind = "ChangeEpoch";
    }
}
