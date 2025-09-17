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

package io.dipcoin.sui.model.object.kind.change;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dipcoin.sui.model.object.kind.ObjectChange;
import lombok.Data;

import java.util.List;

/**
 * @author : Same
 * @datetime : 2025/6/28 11:02
 * @Description : Published response
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Published implements ObjectChange {

    private String digest;

    private List<String> modules;

    @JsonProperty("packageId")
    private String packageId;

    private final String type = "published";

    private Long version;

    @Override
    public String getObjectId() {
        return packageId; // For published type, packageId is equivalent to objectId
    }
}
