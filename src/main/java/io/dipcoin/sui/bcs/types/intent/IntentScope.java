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

package io.dipcoin.sui.bcs.types.intent;

/**
 * @author : Same
 * @datetime : 2025/7/11 18:10
 * @Description : Intent scope, corresponding to TypeScript's IntentScope enum.
 */
public abstract class IntentScope {

    /**
     * get IntentScope
     * @return
     */
    public abstract byte[] getScope();

    /**
     * transaction data
     */
    public static class TransactionData extends IntentScope {

        public static final TransactionData INSTANCE = new TransactionData();
        public static final byte[] INSTANCE_BYTES = new byte[]{0x00, 0x00, 0x00};
        
        private TransactionData() {}

        @Override
        public byte[] getScope() {
            return INSTANCE_BYTES;
        }

        @Override
        public String toString() {
            return "TransactionData";
        }
    }
    
    /**
     * transaction effects
     */
    public static class TransactionEffects extends IntentScope {

        public static final TransactionEffects INSTANCE = new TransactionEffects();
        public static final byte[] INSTANCE_BYTES = new byte[]{0x01, 0x00, 0x00};
        
        private TransactionEffects() {}

        @Override
        public byte[] getScope() {
            return INSTANCE_BYTES;
        }
        
        @Override
        public String toString() {
            return "TransactionEffects";
        }
    }
    
    /**
     * checkpoint summary
     */
    public static class CheckpointSummary extends IntentScope {

        public static final CheckpointSummary INSTANCE = new CheckpointSummary();
        public static final byte[] INSTANCE_BYTES = new byte[]{0x02, 0x00, 0x00};
        
        private CheckpointSummary() {}

        @Override
        public byte[] getScope() {
            return INSTANCE_BYTES;
        }
        
        @Override
        public String toString() {
            return "CheckpointSummary";
        }
    }
    
    /**
     * personal message
     */
    public static class PersonalMessage extends IntentScope {

        public static final PersonalMessage INSTANCE = new PersonalMessage();
        public static final byte[] INSTANCE_BYTES = new byte[]{0x03, 0x00, 0x00};
        
        private PersonalMessage() {}

        @Override
        public byte[] getScope() {
            return INSTANCE_BYTES;
        }
        
        @Override
        public String toString() {
            return "PersonalMessage";
        }
    }
} 