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

package io.dipcoin.sui.model.sui.kind;

import io.dipcoin.sui.model.sui.kind.arg.GasCoin;
import io.dipcoin.sui.model.sui.kind.arg.Input;
import io.dipcoin.sui.model.sui.kind.arg.NestedResult;
import io.dipcoin.sui.model.sui.kind.arg.Result;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

/**
 * @author : Same
 * @datetime : 2025/6/27 22:36
 * @Description : SuiArgument response
 */
@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.DEDUCTION,
        include = JsonTypeInfo.As.WRAPPER_OBJECT,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = GasCoin.class, name = "GasCoin"),
        @JsonSubTypes.Type(value = Input.class, name = "Input"),
        @JsonSubTypes.Type(value = Result.class, name = "Result"),
        @JsonSubTypes.Type(value = NestedResult.class, name = "NestedResult")
})
public abstract class SuiArgument {

    protected String type;
}
