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

package io.dipcoin.sui.bcs.types.effects;

import lombok.ToString;

/**
 * @author : Same
 * @datetime : 2025/7/11 12:28
 * @Description : Gas cost summary.
 */
@ToString
public class GasCostSummary {

    private final long computationCost;
    private final long storageCost;
    private final long storageRebate;
    private final long nonRefundableStorageFee;
    
    public GasCostSummary(long computationCost, long storageCost, long storageRebate, long nonRefundableStorageFee) {
        this.computationCost = computationCost;
        this.storageCost = storageCost;
        this.storageRebate = storageRebate;
        this.nonRefundableStorageFee = nonRefundableStorageFee;
    }
    
    public long getComputationCost() {
        return computationCost;
    }
    
    public long getStorageCost() {
        return storageCost;
    }
    
    public long getStorageRebate() {
        return storageRebate;
    }
    
    public long getNonRefundableStorageFee() {
        return nonRefundableStorageFee;
    }
} 