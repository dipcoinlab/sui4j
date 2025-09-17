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
 * @datetime : 2025/7/11 17:50
 * @Description : Transaction type, corresponding to TypeScript's `TransactionKind` enum.
 */
public abstract class TransactionKind {
    
    /**
     * Programmable transaction
     */
    public static class ProgrammableTransaction extends TransactionKind {
        private final io.dipcoin.sui.bcs.types.transaction.ProgrammableTransaction programmableTransaction;
        
        public ProgrammableTransaction(io.dipcoin.sui.bcs.types.transaction.ProgrammableTransaction programmableTransaction) {
            this.programmableTransaction = Objects.requireNonNull(programmableTransaction);
        }
        
        public io.dipcoin.sui.bcs.types.transaction.ProgrammableTransaction getProgrammableTransaction() {
            return programmableTransaction;
        }
        
        @Override
        public String toString() {
            return "ProgrammableTransaction{" + programmableTransaction + "}";
        }
    }
    
    /**
     * ChangeEpoch
     */
    public static class ChangeEpoch extends TransactionKind {
        public static final ChangeEpoch INSTANCE = new ChangeEpoch();
        
        private ChangeEpoch() {}
        
        @Override
        public String toString() {
            return "ChangeEpoch";
        }
    }
    
    /**
     * Genesis
     */
    public static class Genesis extends TransactionKind {
        public static final Genesis INSTANCE = new Genesis();
        
        private Genesis() {}
        
        @Override
        public String toString() {
            return "Genesis";
        }
    }
    
    /**
     * Consensus commit prologue
     */
    public static class ConsensusCommitPrologue extends TransactionKind {
        public static final ConsensusCommitPrologue INSTANCE = new ConsensusCommitPrologue();
        
        private ConsensusCommitPrologue() {}
        
        @Override
        public String toString() {
            return "ConsensusCommitPrologue";
        }
    }
} 