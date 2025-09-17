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
 * @datetime : 2025/7/11 17:55
 * @Description : Transaction expiration type, corresponding to TypeScript's `TransactionExpiration` enum.
 */
public abstract class TransactionExpiration {
    
    /**
     * none
     */
    public static class None extends TransactionExpiration {
        public static final None INSTANCE = new None();
        
        private None() {}
        
        @Override
        public String toString() {
            return "None";
        }
    }
    
    /**
     * epoch
     */
    public static class Epoch extends TransactionExpiration {
        private final long epoch;
        
        public Epoch(long epoch) {
            this.epoch = epoch;
        }
        
        public long getEpoch() {
            return epoch;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Epoch epoch1 = (Epoch) obj;
            return epoch == epoch1.epoch;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(epoch);
        }
        
        @Override
        public String toString() {
            return "Epoch{" + epoch + "}";
        }
    }
} 