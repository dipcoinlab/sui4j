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

package io.dipcoin.sui.model.object.kind.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dipcoin.sui.model.object.kind.InputObjectKind;
import io.dipcoin.sui.model.object.kind.input.subtypes.SharedMoveObjectDetail;
import lombok.Data;

/**
 * @author : Same
 * @datetime : 2025/6/28 12:50
 * @Description : SharedMoveObject response
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SharedMoveObject implements InputObjectKind {

    @JsonProperty("SharedMoveObject")
    private SharedMoveObjectDetail sharedMoveObject;

    @Override
    public String getType() {
        return "SharedMoveObject";
    }

}
