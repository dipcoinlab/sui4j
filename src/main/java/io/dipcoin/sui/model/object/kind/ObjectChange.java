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

/**
 * @author : Same
 * @datetime : 2025/6/28 11:00
 * @Description : ObjectChange response
 */

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dipcoin.sui.model.object.kind.change.*;

/**
 * SuiObjectChange base class
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Published.class, name = "published"),
        @JsonSubTypes.Type(value = Transferred.class, name = "transferred"),
        @JsonSubTypes.Type(value = Mutated.class, name = "mutated"),
        @JsonSubTypes.Type(value = Deleted.class, name = "deleted"),
        @JsonSubTypes.Type(value = Wrapped.class, name = "wrapped"),
        @JsonSubTypes.Type(value = Created.class, name = "created")
})
public interface ObjectChange {

    String getType();

    String getObjectId(); // public method, implemented by some subclasses

    Long getVersion();

}
