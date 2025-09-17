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

package io.dipcoin.sui.bcs.types.tag;

import java.util.Objects;

/**
 * @author : Same
 * @datetime : 2025/7/11 12:33
 * @Description : struct type
 */
public class TypeTagStruct extends TypeTag {

    private final TypeTagStructTag structTag;
    
    public TypeTagStruct(TypeTagStructTag structTag) {
        this.structTag = Objects.requireNonNull(structTag);
    }
    
    public TypeTagStructTag getStructTag() {
        return structTag;
    }
    
    @Override
    public String toString() {
        return structTag.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TypeTagStruct struct = (TypeTagStruct) obj;
        return Objects.equals(structTag, struct.structTag);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(structTag);
    }
} 