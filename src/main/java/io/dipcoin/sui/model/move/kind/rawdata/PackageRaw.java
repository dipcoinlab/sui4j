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

package io.dipcoin.sui.model.move.kind.rawdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.dipcoin.sui.model.move.TypeOrigin;
import io.dipcoin.sui.model.move.UpgradeInfo;
import io.dipcoin.sui.model.move.kind.RawData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * @author : Same
 * @datetime : 2025/7/4 18:01
 * @Description : PackageRaw response
 */
@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PackageRaw implements RawData {

    private final String dataType = "package";

    private String id;

    private Map<String, UpgradeInfo> linkageTable;

    private Map<String, String> moduleMap; // Base64 encoded module content

    private List<TypeOrigin> typeOriginTable;

    private String version;
}
