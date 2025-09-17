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

import io.dipcoin.sui.bcs.types.gas.GasData;

import java.util.Objects;

/**
 * @author : Same
 * @datetime : 2025/7/11 18:00
 * @Description : Transaction data V1 version, corresponding to TypeScript's `TransactionDataV1` type.
 */
public class TransactionDataV1 {
    
    private final TransactionKind kind;
    private final String sender;
    private final GasData gasData;
    private final TransactionExpiration expiration;
    
    public TransactionDataV1(TransactionKind kind, String sender, GasData gasData, TransactionExpiration expiration) {
        this.kind = Objects.requireNonNull(kind);
        this.sender = Objects.requireNonNull(sender);
        this.gasData = Objects.requireNonNull(gasData);
        this.expiration = Objects.requireNonNull(expiration);
    }
    
    public TransactionKind getKind() {
        return kind;
    }
    
    public String getSender() {
        return sender;
    }
    
    public GasData getGasData() {
        return gasData;
    }
    
    public TransactionExpiration getExpiration() {
        return expiration;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TransactionDataV1 that = (TransactionDataV1) obj;
        return Objects.equals(kind, that.kind) &&
               Objects.equals(sender, that.sender) &&
               Objects.equals(gasData, that.gasData) &&
               Objects.equals(expiration, that.expiration);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(kind, sender, gasData, expiration);
    }
    
    @Override
    public String toString() {
        return "TransactionDataV1{" +
               "kind=" + kind +
               ", sender='" + sender + '\'' +
               ", gasData=" + gasData +
               ", expiration=" + expiration +
               '}';
    }
} 