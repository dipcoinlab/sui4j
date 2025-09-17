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

import io.dipcoin.sui.bcs.types.gas.SuiObjectRef;
import lombok.ToString;

import java.util.List;

/**
 * @author : Same
 * @datetime : 2025/7/11 12:55
 * @Description : transaction effects V1
 */
@ToString
public class TransactionEffectsV1 {

    private final ExecutionStatus status;
    private final long executedEpoch;
    private final GasCostSummary gasUsed;
    private final List<SuiObjectRef> created;
    private final List<SuiObjectRef> mutated;
    private final List<SuiObjectRef> deleted;
    private final String transactionDigest;
    
    public TransactionEffectsV1(ExecutionStatus status, long executedEpoch, GasCostSummary gasUsed,
                              List<SuiObjectRef> created, List<SuiObjectRef> mutated, List<SuiObjectRef> deleted,
                              String transactionDigest) {
        this.status = status;
        this.executedEpoch = executedEpoch;
        this.gasUsed = gasUsed;
        this.created = created;
        this.mutated = mutated;
        this.deleted = deleted;
        this.transactionDigest = transactionDigest;
    }
    
    public ExecutionStatus getStatus() {
        return status;
    }
    
    public long getExecutedEpoch() {
        return executedEpoch;
    }
    
    public GasCostSummary getGasUsed() {
        return gasUsed;
    }
    
    public List<SuiObjectRef> getCreated() {
        return created;
    }
    
    public List<SuiObjectRef> getMutated() {
        return mutated;
    }
    
    public List<SuiObjectRef> getDeleted() {
        return deleted;
    }
    
    public String getTransactionDigest() {
        return transactionDigest;
    }
} 