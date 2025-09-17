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

import java.util.List;
import java.util.Objects;

/**
 * @author : Same
 * @datetime : 2025/7/11 11:47
 * @Description : StructTag type
 */
public class TypeTagStructTag {

    private final String address;
    private final String module;
    private final String name;
    private final List<TypeTag> typeParams;
    
    public TypeTagStructTag(String address, String module, String name, List<TypeTag> typeParams) {
        this.address = Objects.requireNonNull(address);
        this.module = Objects.requireNonNull(module);
        this.name = Objects.requireNonNull(name);
        this.typeParams = Objects.requireNonNull(typeParams);
    }
    
    public String getAddress() {
        return address;
    }
    
    public String getModule() {
        return module;
    }
    
    public String getName() {
        return name;
    }
    
    public List<TypeTag> getTypeParams() {
        return typeParams;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(address).append("::").append(module).append("::").append(name);
        if (!typeParams.isEmpty()) {
            sb.append("<");
            for (int i = 0; i < typeParams.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(typeParams.get(i));
            }
            sb.append(">");
        }
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TypeTagStructTag structTag = (TypeTagStructTag) obj;
        return Objects.equals(address, structTag.address) &&
               Objects.equals(module, structTag.module) &&
               Objects.equals(name, structTag.name) &&
               Objects.equals(typeParams, structTag.typeParams);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(address, module, name, typeParams);
    }
} 