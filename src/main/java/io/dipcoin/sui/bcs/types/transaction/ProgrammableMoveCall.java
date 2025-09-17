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

import io.dipcoin.sui.bcs.types.tag.TypeTag;

import java.util.List;
import java.util.Objects;

/**
 * @author : Same
 * @datetime : 2025/7/11 17:35
 * @Description : Programmable Move call, corresponding to TypeScript's `ProgrammableMoveCall` type.
 */
public class ProgrammableMoveCall {
    
    private final String packageId;
    private final String module;
    private final String function;
    private final List<TypeTag> typeArguments;
    private final List<Argument> arguments;
    
    public ProgrammableMoveCall(String packageId, String module, String function, 
                               List<TypeTag> typeArguments, List<Argument> arguments) {
        this.packageId = Objects.requireNonNull(packageId);
        this.module = Objects.requireNonNull(module);
        this.function = Objects.requireNonNull(function);
        this.typeArguments = Objects.requireNonNull(typeArguments);
        this.arguments = Objects.requireNonNull(arguments);
    }
    
    public String getPackageId() {
        return packageId;
    }
    
    public String getModule() {
        return module;
    }
    
    public String getFunction() {
        return function;
    }
    
    public List<TypeTag> getTypeArguments() {
        return typeArguments;
    }
    
    public List<Argument> getArguments() {
        return arguments;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ProgrammableMoveCall that = (ProgrammableMoveCall) obj;
        return Objects.equals(packageId, that.packageId) &&
               Objects.equals(module, that.module) &&
               Objects.equals(function, that.function) &&
               Objects.equals(typeArguments, that.typeArguments) &&
               Objects.equals(arguments, that.arguments);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(packageId, module, function, typeArguments, arguments);
    }
    
    @Override
    public String toString() {
        return "ProgrammableMoveCall{" +
               "packageId='" + packageId + '\'' +
               ", module='" + module + '\'' +
               ", function='" + function + '\'' +
               ", typeArguments=" + typeArguments +
               ", arguments=" + arguments +
               '}';
    }
} 