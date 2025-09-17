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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dipcoin.sui.model.object.kind.error.*;

/**
 * @author : Same
 * @datetime : 2025/7/4 18:09
 * @Description : ObjectResponseError response
 * | {
 * 			code: 'notExists';
 * 			object_id: string;
 * 	        }
 * 	| {
 * 			code: 'dynamicFieldNotFound';
 * 			parent_object_id: string;
 *      }
 * 	| {
 * 			code: 'deleted';
 * 			digest: string;
 * 			object_id: string;
 * 			version: string;
 * 	        }
 * 	| {
 * 			code: 'unknown';
 *      }
 * 	| {
 * 			code: 'displayError';
 * 			error: string;
 *      }
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "code"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = NotExists.class, name = "notExists"),
        @JsonSubTypes.Type(value = DynamicFieldNotFound.class, name = "dynamicFieldNotFound"),
        @JsonSubTypes.Type(value = Deleted.class, name = "deleted"),
        @JsonSubTypes.Type(value = Unknown.class, name = "unknown"),
        @JsonSubTypes.Type(value = Display.class, name = "displayError")
})
public interface ObjectResponseError {

    String getCode();

}