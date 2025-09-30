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

package io.dipcoin.sui.model.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author : Same
 * @datetime : 2025/7/24 00:07
 * @Description : SuiObjectDataFilter request
 */
@Accessors(chain=true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SuiObjectDataFilter {

    @JsonProperty("MatchAll")
    private List<SuiObjectDataFilter> matchAll;

    @JsonProperty("MatchAny")
    private List<SuiObjectDataFilter> matchAny;

    @JsonProperty("MatchNone")
    private List<SuiObjectDataFilter> matchNone;

    @JsonProperty("Package")
    private String packageFilter;

    @JsonProperty("MoveModule")
    private MoveModuleFilter moveModuleFilter;

    @JsonProperty("StructType")
    private String structTypeFilter;

    @JsonProperty("AddressOwner")
    private String addressOwnerFilter;

    @JsonProperty("ObjectOwner")
    private String objectOwnerFilter;

    @JsonProperty("ObjectId")
    private String objectIdFilter;

    @JsonProperty("ObjectIds")
    private List<String> objectIdsFilter;

    @JsonProperty("Version")
    private Long versionFilter;

}
