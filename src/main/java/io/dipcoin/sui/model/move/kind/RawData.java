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

package io.dipcoin.sui.model.move.kind;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dipcoin.sui.model.move.kind.rawdata.MoveObjectRaw;
import io.dipcoin.sui.model.move.kind.rawdata.PackageRaw;

/**
 * @author : Same
 * @datetime : 2025/7/4 17:59
 * @Description : RawData response
 * 	| {
 * 			bcsBytes: string;
 * 			dataType: 'moveObject';
 * 			hasPublicTransfer: boolean;
 * 			type: string;
 * 			version: string;
 * 	        }
 * 	| {
 * 			dataType: 'package';
 * 			id: string;
 * 			linkageTable: {
 * 				[key: string]: UpgradeInfo;
 *            };
 * 			moduleMap: {
 * 				[key: string]: string;
 *            };
 * 			typeOriginTable: TypeOrigin[];
 * 			version: string;
 *      }
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "dataType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MoveObjectRaw.class, name = "moveObject"),
        @JsonSubTypes.Type(value = PackageRaw.class, name = "package")
})
public interface RawData {

    String getDataType();

    String getVersion();
}
