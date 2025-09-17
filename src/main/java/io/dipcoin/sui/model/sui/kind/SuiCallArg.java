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

import io.dipcoin.sui.model.sui.kind.call.ImmOrOwnedObject;
import io.dipcoin.sui.model.sui.kind.call.PureCall;
import io.dipcoin.sui.model.sui.kind.call.ReceivingObject;
import io.dipcoin.sui.model.sui.kind.call.SharedObject;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

/**
 * @author : Same
 * @datetime : 2025/6/27 19:09
 * @Description : SuiCallArg response
 */
@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ImmOrOwnedObject.class, name = "object"),
        @JsonSubTypes.Type(value = SharedObject.class, name = "object"),
        @JsonSubTypes.Type(value = ReceivingObject.class, name = "object"),
        @JsonSubTypes.Type(value = PureCall.class, name = "pure")
})
public abstract class SuiCallArg {

    protected String type;

}
