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
 * @datetime : 2025/9/26 15:28
 * @Description : EventFilter request
 */
@Accessors(chain=true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventFilter {

    @JsonProperty("All")
    private List<EventFilter> all;

    @JsonProperty("Any")
    private List<EventFilter> any;

    @JsonProperty("Sender")
    private String sender;

    @JsonProperty("Transaction")
    private String transactionDigest;

    @JsonProperty("MoveModule")
    private MoveModuleFilter moveModuleFilter;

    @JsonProperty("MoveEventType")
    private String moveEventType;

    @JsonProperty("MoveEventModule")
    private MoveModuleFilter moveEventModuleFilter;

    @JsonProperty("TimeRange")
    private TimeRangeFilter timeRangeFilter;

}
