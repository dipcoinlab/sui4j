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

package io.dipcoin.sui.model.sui.kind.arg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dipcoin.sui.model.sui.kind.SuiArgument;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author : Same
 * @datetime : 2025/6/28 00:27
 * @Description : NestedResult response
 */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NestedResult extends SuiArgument {

    @JsonProperty("NestedResult")
    private List<Integer> indices; // [uint16, uint16]

    public NestedResult() {
        this.type = "NestedResult";
    }
}