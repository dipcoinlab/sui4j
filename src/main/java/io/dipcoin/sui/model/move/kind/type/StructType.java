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

package io.dipcoin.sui.model.move.kind.type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.dipcoin.sui.model.move.kind.SuiMoveNormalizedType;
import lombok.*;

import java.util.List;

/**
 * @author : Same
 * @datetime : 2025/6/30 16:45
 * @Description : Move Struct type response
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StructType extends SuiMoveNormalizedType {

    private String address;

    private String module;

    private String name;

    private List<SuiMoveNormalizedType> typeArguments;

}
