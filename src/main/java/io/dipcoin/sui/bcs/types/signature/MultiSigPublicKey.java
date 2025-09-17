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

import java.util.List;
import java.util.Objects;

/**
 * @author : Same
 * @datetime : 2025/7/11 18:55
 * @Description : Multi-signature public key, corresponding to TypeScript's `MultiSigPublicKey` type.
 */
public class MultiSigPublicKey {
    
    private final List<MultiSigPkMap> pkMap;
    private final int threshold;
    
    public MultiSigPublicKey(List<MultiSigPkMap> pkMap, int threshold) {
        this.pkMap = Objects.requireNonNull(pkMap);
        this.threshold = threshold;
    }
    
    public List<MultiSigPkMap> getPkMap() {
        return pkMap;
    }
    
    public int getThreshold() {
        return threshold;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MultiSigPublicKey that = (MultiSigPublicKey) obj;
        return threshold == that.threshold &&
               Objects.equals(pkMap, that.pkMap);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(pkMap, threshold);
    }
    
    @Override
    public String toString() {
        return "MultiSigPublicKey{" +
               "pkMap=" + pkMap +
               ", threshold=" + threshold +
               '}';
    }
} 