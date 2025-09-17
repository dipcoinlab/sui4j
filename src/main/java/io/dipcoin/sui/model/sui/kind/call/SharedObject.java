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

package io.dipcoin.sui.model.sui.kind.call;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dipcoin.sui.model.sui.kind.SuiCallArg;
import io.dipcoin.sui.model.sui.kind.call.enums.ObjectTypeEnum;
import io.dipcoin.sui.model.sui.kind.call.enums.TypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigInteger;

/**
 * @author : Same
 * @datetime : 2025/6/27 19:29
 * @Description : SharedObject response
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SharedObject extends SuiCallArg {

    @JsonProperty("objectType")
    private String objectType = ObjectTypeEnum.SHARED_OBJECT.getObjectType();

    @JsonProperty("objectId")
    private String objectId;

    @JsonProperty("initialSharedVersion")
    private BigInteger initialSharedVersion; // SequenceNumber -> BigInteger

    private boolean mutable;

    public SharedObject() {
        this.type = TypeEnum.OBJECT.getType();
    }
}