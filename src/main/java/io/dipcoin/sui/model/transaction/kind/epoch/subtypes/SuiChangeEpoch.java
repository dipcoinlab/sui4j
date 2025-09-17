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

package io.dipcoin.sui.model.transaction.kind.epoch.subtypes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author : Same
 * @datetime : 2025/6/27 18:17
 * @Description : SuiEndOfEpochTransactionKind.ChangeEpochEndOfEpoch.ChangeEpoch response
 */
@Data
public class SuiChangeEpoch {
    @JsonProperty("computation_charge")
    private BigInteger computationCharge; // BigInt_for_uint64

    private BigInteger epoch; // BigInt_for_uint64

    @JsonProperty("epoch_start_timestamp_ms")
    private BigInteger epochStartTimestampMs; // BigInt_for_uint64

    @JsonProperty("storage_charge")
    private BigInteger storageCharge; // BigInt_for_uint64

    @JsonProperty("storage_rebate")
    private BigInteger storageRebate; // BigInt_for_uint64
}