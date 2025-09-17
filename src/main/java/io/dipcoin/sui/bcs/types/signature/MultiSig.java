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
 * @datetime : 2025/7/11 19:00
 * @Description : Multi-signature, corresponding to TypeScript's MultiSig type.
 */
public class MultiSig {
    
    private final List<CompressedSignature> sigs;
    private final int bitmap;
    private final MultiSigPublicKey multisigPk;
    
    public MultiSig(List<CompressedSignature> sigs, int bitmap, MultiSigPublicKey multisigPk) {
        this.sigs = Objects.requireNonNull(sigs);
        this.bitmap = bitmap;
        this.multisigPk = Objects.requireNonNull(multisigPk);
    }
    
    public List<CompressedSignature> getSigs() {
        return sigs;
    }
    
    public int getBitmap() {
        return bitmap;
    }
    
    public MultiSigPublicKey getMultisigPk() {
        return multisigPk;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MultiSig multiSig = (MultiSig) obj;
        return bitmap == multiSig.bitmap &&
               Objects.equals(sigs, multiSig.sigs) &&
               Objects.equals(multisigPk, multiSig.multisigPk);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(sigs, bitmap, multisigPk);
    }
    
    @Override
    public String toString() {
        return "MultiSig{" +
               "sigs=" + sigs +
               ", bitmap=" + bitmap +
               ", multisigPk=" + multisigPk +
               '}';
    }
} 