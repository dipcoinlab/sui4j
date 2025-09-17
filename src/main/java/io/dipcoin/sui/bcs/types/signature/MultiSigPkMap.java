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

package io.dipcoin.sui.bcs.types.signature;

import java.util.Objects;

/**
 * @author : Same
 * @datetime : 2025/7/11 18:50
 * @Description : Multi-signature public key map, corresponding to TypeScript's `MultiSigPkMap` type.
 */
public class MultiSigPkMap {
    
    private final PublicKey pubKey;
    private final int weight;
    
    public MultiSigPkMap(PublicKey pubKey, int weight) {
        this.pubKey = Objects.requireNonNull(pubKey);
        this.weight = weight;
    }
    
    public PublicKey getPubKey() {
        return pubKey;
    }
    
    public int getWeight() {
        return weight;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MultiSigPkMap that = (MultiSigPkMap) obj;
        return weight == that.weight &&
               Objects.equals(pubKey, that.pubKey);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(pubKey, weight);
    }
    
    @Override
    public String toString() {
        return "MultiSigPkMap{" +
               "pubKey=" + pubKey +
               ", weight=" + weight +
               '}';
    }
} 