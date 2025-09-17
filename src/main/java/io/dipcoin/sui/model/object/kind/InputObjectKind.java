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

package io.dipcoin.sui.model.object.kind;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dipcoin.sui.model.object.kind.input.ImmOrOwnedMoveObject;
import io.dipcoin.sui.model.object.kind.input.MovePackage;
import io.dipcoin.sui.model.object.kind.input.SharedMoveObject;

/**
 * @author : Same
 * @datetime : 2025/6/26 19:31
 * @Description : InputObjectKind input object type base class response
 * | {
 * 			MovePackage: string;
 * 	        }
 * 	| {
 * 			ImmOrOwnedMoveObject: SuiObjectRef;
 *      }
 * 	| {
 * 			SharedMoveObject: {
 * 				id: string;
 * 				initial_shared_version: string;
 * 				mutable?: boolean;
 *            };
 *      }
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.DEDUCTION,
        include = JsonTypeInfo.As.WRAPPER_OBJECT,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MovePackage.class, name = "MovePackage"),
        @JsonSubTypes.Type(value = ImmOrOwnedMoveObject.class, name = "ImmOrOwnedMoveObject"),
        @JsonSubTypes.Type(value = SharedMoveObject.class, name = "SharedMoveObject")
})
public interface InputObjectKind {

    String getType();
}
