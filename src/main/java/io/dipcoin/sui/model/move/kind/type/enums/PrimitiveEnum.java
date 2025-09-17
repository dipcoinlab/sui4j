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

package io.dipcoin.sui.model.move.kind.type.enums;

import lombok.Getter;

/**
 * @author : Same
 * @datetime : 2025/6/30 15:51
 * @Description : Primitive enum
 */
@Getter
public enum PrimitiveEnum {

    BOOL("Bool"),
    U8("U8"),
    U16("U16"),
    U32("U32"),
    U64("U64"),
    U128("U128"),
    U256("U256"),
    ADDRESS("Address"),
    SIGNER("Signer"),
    ;

    PrimitiveEnum(String value) {
        this.value = value;
    }

    private final String value;

    public static PrimitiveEnum find(String value) {
        for (PrimitiveEnum e : values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        return null;
    }
}
