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

package io.dipcoin.sui.bcs.types.transaction;

import java.util.Objects;

/**
 * @author : Same
 * @datetime : 2025/7/11 18:05
 * @Description : Transaction data, corresponding to TypeScript's `TransactionData` enum.
 */
public abstract class TransactionData {
    
    /**
     * V1 version transaction data
     */
    public static class V1 extends TransactionData {
        private final TransactionDataV1 transactionDataV1;
        
        public V1(TransactionDataV1 transactionDataV1) {
            this.transactionDataV1 = Objects.requireNonNull(transactionDataV1);
        }
        
        public TransactionDataV1 getTransactionDataV1() {
            return transactionDataV1;
        }
        
        @Override
        public String toString() {
            return "V1{" + transactionDataV1 + "}";
        }
    }
} 