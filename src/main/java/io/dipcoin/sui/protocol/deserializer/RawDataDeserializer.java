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

package io.dipcoin.sui.protocol.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dipcoin.sui.model.move.kind.RawData;
import io.dipcoin.sui.model.move.kind.rawdata.MoveObjectRaw;
import io.dipcoin.sui.model.move.kind.rawdata.PackageRaw;

import java.io.IOException;

/**
 * @author : Same
 * @datetime : 2025/7/4 18:03
 * @Description : RawData special deserializer.
 */
public class RawDataDeserializer extends JsonDeserializer<RawData> {

    @Override
    public RawData deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = p.getCodec().readTree(p);

        if (!node.has("dataType")) {
            throw new IllegalArgumentException("Missing dataType field in RawData");
        }

        String dataType = node.get("dataType").asText();

        switch (dataType) {
            case "moveObject":
                return mapper.treeToValue(node, MoveObjectRaw.class);
            case "package":
                return mapper.treeToValue(node, PackageRaw.class);
            default:
                throw new IllegalArgumentException("Unknown RawData type: " + dataType);
        }
    }

}
