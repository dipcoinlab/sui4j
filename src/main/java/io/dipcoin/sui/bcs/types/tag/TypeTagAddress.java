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

/**
 * @author : Same
 * @datetime : 2025/7/11 12:45
 * @Description : address type
 */
public class TypeTagAddress extends TypeTag {

    public static final TypeTagAddress INSTANCE = new TypeTagAddress();
    
    private TypeTagAddress() {}
    
    @Override
    public String toString() {
        return "address";
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof TypeTagAddress;
    }
    
    @Override
    public int hashCode() {
        return TypeTagAddress.class.hashCode();
    }
} 