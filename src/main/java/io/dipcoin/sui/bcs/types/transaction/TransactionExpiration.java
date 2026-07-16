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

    /**
     * Validity-window expiration (SIP-58 / Address Balances).
     *
     * <p>When paying gas from an address balance, {@code GasData.payment} must be empty and
     * {@code expiration} must be a {@code ValidDuring} with both {@code minEpoch} and
     * {@code maxEpoch} set ({@code maxEpoch <= minEpoch + 1}). In this mode transaction
     * construction is fully stateless: no gas coin version/digest lookup is required, which
     * naturally supports concurrent submission.</p>
     *
     * <p>BCS layout (aligned with the Mysten SDK {@code ValidDuring} struct and Rust
     * {@code sui-types}):
     * <pre>
     * Option&lt;u64&gt; minEpoch
     * Option&lt;u64&gt; maxEpoch
     * Option&lt;u64&gt; minTimestamp
     * Option&lt;u64&gt; maxTimestamp
     * ObjectDigest  chain     (32-byte chain identifier = genesis checkpoint digest, encoded as a uleb length prefix + 32 bytes)
     * u32           nonce     (disambiguates two transactions with an otherwise identical digest; no ordering semantics, may be random or incremented)
     * </pre>
     * Sui does not yet support timestamp-based expiration, so {@code minTimestamp} /
     * {@code maxTimestamp} should be {@code null}.</p>
     */
    public static class ValidDuring extends TransactionExpiration {

        private final Long minEpoch;
        private final Long maxEpoch;
        private final Long minTimestamp;
        private final Long maxTimestamp;
        /** Chain identifier: the base58-encoded genesis checkpoint digest (32 bytes). */
        private final String chain;
        /** u32 nonce, used only to disambiguate transactions with an otherwise identical digest. */
        private final int nonce;

        public ValidDuring(Long minEpoch, Long maxEpoch, Long minTimestamp, Long maxTimestamp, String chain, int nonce) {
            this.minEpoch = minEpoch;
            this.maxEpoch = maxEpoch;
            this.minTimestamp = minTimestamp;
            this.maxTimestamp = maxTimestamp;
            this.chain = chain;
            this.nonce = nonce;
        }

        /**
         * Builds a single-epoch window {@code ValidDuring} ({@code [epoch, epoch + 1]}), the
         * most common form when paying gas from an address balance.
         *
         * @param epoch the current epoch
         * @param chain the genesis checkpoint digest (base58, 32 bytes)
         * @param nonce u32 nonce
         */
        public static ValidDuring ofEpochWindow(long epoch, String chain, int nonce) {
            return new ValidDuring(epoch, epoch + 1, null, null, chain, nonce);
        }

        public Long getMinEpoch() {
            return minEpoch;
        }

        public Long getMaxEpoch() {
            return maxEpoch;
        }

        public Long getMinTimestamp() {
            return minTimestamp;
        }

        public Long getMaxTimestamp() {
            return maxTimestamp;
        }

        public String getChain() {
            return chain;
        }

        public int getNonce() {
            return nonce;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ValidDuring that = (ValidDuring) obj;
            return nonce == that.nonce
                    && Objects.equals(minEpoch, that.minEpoch)
                    && Objects.equals(maxEpoch, that.maxEpoch)
                    && Objects.equals(minTimestamp, that.minTimestamp)
                    && Objects.equals(maxTimestamp, that.maxTimestamp)
                    && Objects.equals(chain, that.chain);
        }

        @Override
        public int hashCode() {
            return Objects.hash(minEpoch, maxEpoch, minTimestamp, maxTimestamp, chain, nonce);
        }

        @Override
        public String toString() {
            return "ValidDuring{minEpoch=" + minEpoch + ", maxEpoch=" + maxEpoch
                    + ", minTimestamp=" + minTimestamp + ", maxTimestamp=" + maxTimestamp
                    + ", chain=" + chain + ", nonce=" + nonce + "}";
        }
    }
} 