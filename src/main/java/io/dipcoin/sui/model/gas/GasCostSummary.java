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

package io.dipcoin.sui.model.gas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author : Same
 * @datetime : 2025/6/28 11:34
 * @Description : GasCostSummary response
 * Transaction fee summary. Storage fees are independent of computation fees. Storage fees are divided into three parts:
 * storage_cost is the storage fee during transaction execution. Storage cost equals the number of bytes of modified objects multiplied by the storage cost per byte for mutable
 * storage_rebate: This is the rebate users receive when operating objects. It is the object value minus fees. storage_rebate.
 * storage_cost: non_refundable_storage_fee object storage cost is not fully refunded to users, only a small portion is retained by the system. This value is used to track this fee.
 *
 * When viewing gas cost summary, the amount charged to users is computation_cost + storage_cost - storage_rebate, which is the amount deducted from gas coins.
 * non_refundable_storage_fee is collected from mutated/deleted objects and tracked by the system in the storage fund.
 *
 * Deleted objects (including old versions of modified objects) will add the storage fields on the objects to the "potential rebate" pool.
 * The rebate will then be deducted according to the "non-refundable rate" as follows:
 * potential_rebate(storage cost of deleted/mutated objects) = storage_rebate + non_refundable_storage_fee
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GasCostSummary {

    /**
     * calculate/execution cost
     */
    private BigInteger computationCost;

    /**
     * Rebate fee. The portion of storage rebate retained by the system.
     */
    private BigInteger nonRefundableStorageFee;

    /**
     * Storage cost, which is the sum of all storage costs of all created or mutated objects.
     */
    private BigInteger storageCost;

    /**
     * Amount of storage cost refunded to users for all objects deleted or changed in the transaction.
     */
    private BigInteger storageRebate;

}
