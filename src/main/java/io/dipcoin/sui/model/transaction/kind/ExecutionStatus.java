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

package io.dipcoin.sui.model.transaction.kind;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dipcoin.sui.model.transaction.kind.status.Failure;
import io.dipcoin.sui.model.transaction.kind.status.Success;

/**
 * @author : Same
 * @datetime : 2025/6/28 11:40
 * @Description : ExecutionStatus execution status base class response
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "status",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Success.class, name = "success"),
        @JsonSubTypes.Type(value = Failure.class, name = "failure")
})
public interface ExecutionStatus {

    String getStatus();
}
