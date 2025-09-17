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
 * @datetime : 2025/7/11 11:18
 * @Description : vector type
 */
public class TypeTagVector extends TypeTag {

    private final TypeTag elementType;
    
    public TypeTagVector(TypeTag elementType) {
        this.elementType = Objects.requireNonNull(elementType);
    }
    
    public TypeTag getElementType() {
        return elementType;
    }
    
    @Override
    public String toString() {
        return "vector<" + elementType + ">";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TypeTagVector vector = (TypeTagVector) obj;
        return Objects.equals(elementType, vector.elementType);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(elementType);
    }
} 