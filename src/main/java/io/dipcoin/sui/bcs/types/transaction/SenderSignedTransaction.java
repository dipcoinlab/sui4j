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

import io.dipcoin.sui.bcs.types.intent.IntentMessage;

import java.util.List;
import java.util.Objects;

/**
 * @author : Same
 * @datetime : 2025/7/11 19:05
 * @Description : Sender signed transaction, corresponding to TypeScript's SenderSignedTransaction type.
 */
public class SenderSignedTransaction {
    
    private final IntentMessage<TransactionData> intentMessage;
    private final List<byte[]> txSignatures;
    
    public SenderSignedTransaction(IntentMessage<TransactionData> intentMessage, List<byte[]> txSignatures) {
        this.intentMessage = Objects.requireNonNull(intentMessage);
        this.txSignatures = Objects.requireNonNull(txSignatures);
    }
    
    public IntentMessage<TransactionData> getIntentMessage() {
        return intentMessage;
    }
    
    public List<byte[]> getTxSignatures() {
        return txSignatures;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SenderSignedTransaction that = (SenderSignedTransaction) obj;
        return Objects.equals(intentMessage, that.intentMessage) &&
               Objects.equals(txSignatures, that.txSignatures);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(intentMessage, txSignatures);
    }
    
    @Override
    public String toString() {
        return "SenderSignedTransaction{" +
               "intentMessage=" + intentMessage +
               ", txSignatures=" + txSignatures.size() + " signatures" +
               '}';
    }
} 