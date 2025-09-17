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
 * @datetime : 2025/7/10 17:35
 * @Description : u16 type
 */
public class TypeTagU16 extends TypeTag {

    public static final TypeTagU16 INSTANCE = new TypeTagU16();
    
    private TypeTagU16() {}
    
    @Override
    public String toString() {
        return "u16";
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof TypeTagU16;
    }
    
    @Override
    public int hashCode() {
        return TypeTagU16.class.hashCode();
    }
} 