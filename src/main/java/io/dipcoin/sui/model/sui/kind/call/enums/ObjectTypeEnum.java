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

package io.dipcoin.sui.model.sui.kind.call.enums;

import lombok.Getter;

/**
 * @author : Same
 * @datetime : 2025/7/4 22:43
 * @Description : objectId object type enum
 */
@Getter
public enum ObjectTypeEnum {

    IMM_OR_OWNED_OBJECT("immOrOwnedObject"),
    RECEIVING("receiving"),
    SHARED_OBJECT("sharedObject"),
    U8("u8"),
    U64("u64"),
    U128("u128"),
    BOOL("bool"),
    STRING("string"),
    ADDRESS("address"),
    VECTOR_U8("vector<u8>"),
    VECTOR_U128("vector<u128>"),
    VECTOR_ADDRESS("vector<address>"),
    ;

    ObjectTypeEnum(String objectType) {
        this.objectType = objectType;
    }

    private final String objectType;
    
}
