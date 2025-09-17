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

package io.dipcoin.sui.bcs.types.gas;

import lombok.ToString;

import java.util.Objects;

/**
 * @author : Same
 * @datetime : 2025/7/10 13:45
 * @Description : Sui object reference, corresponding to TypeScript's SuiObjectRef type.
 */
@ToString
public class SuiObjectRef {

    private final String objectId;
    private final long version;
    private final String digest;
    
    public SuiObjectRef(String objectId, long version, String digest) {
        this.objectId = Objects.requireNonNull(objectId);
        this.version = version;
        this.digest = Objects.requireNonNull(digest);
    }
    
    public String getObjectId() {
        return objectId;
    }
    
    public long getVersion() {
        return version;
    }
    
    public String getDigest() {
        return digest;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SuiObjectRef that = (SuiObjectRef) obj;
        return version == that.version &&
               Objects.equals(objectId, that.objectId) &&
               Objects.equals(digest, that.digest);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(objectId, version, digest);
    }
    
    @Override
    public String toString() {
        return "SuiObjectRef{" +
               "objectId='" + objectId + '\'' +
               ", version=" + version +
               ", digest='" + digest + '\'' +
               '}';
    }
} 