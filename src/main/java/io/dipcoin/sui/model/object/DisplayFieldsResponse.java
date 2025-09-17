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

package io.dipcoin.sui.model.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.dipcoin.sui.model.object.kind.ObjectResponseError;
import lombok.Data;

import java.util.Map;

/**
 * @author : Same
 * @datetime : 2025/7/4 18:07
 * @Description : DisplayFieldsResponse response
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DisplayFieldsResponse {

    private Map<String, String> data;

    private ObjectResponseError error;

}
