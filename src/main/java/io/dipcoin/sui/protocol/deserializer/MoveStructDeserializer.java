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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dipcoin.sui.model.move.kind.MoveStruct;
import io.dipcoin.sui.model.move.kind.MoveValue;
import io.dipcoin.sui.model.move.kind.struct.MoveStructArray;
import io.dipcoin.sui.model.move.kind.struct.MoveStructMap;
import io.dipcoin.sui.model.move.kind.struct.MoveStructObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author : Same
 * @datetime : 2025/7/4 17:51
 * @Description : MoveStruct special deserializer.
 */
public class MoveStructDeserializer extends JsonDeserializer<MoveStruct> {

    @Override
    public MoveStruct deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);

        if (node.isArray()) {
            MoveStructArray array = new MoveStructArray();
            List<MoveValue> values = mapper.convertValue(node, new TypeReference<List<MoveValue>>() {});
            array.setValues(values);
            return array;
        } else if (node.has("type") && node.has("fields")) {
            MoveStructObject obj = new MoveStructObject();
            obj.setType(node.get("type").asText());
            obj.setFields(mapper.convertValue(node.get("fields"), new TypeReference<Map<String, MoveValue>>() {}));
            return obj;
        } else if (node.isObject()) {
            MoveStructMap map = new MoveStructMap();
            map.setValues(mapper.convertValue(node, new TypeReference<Map<String, MoveValue>>() {}));
            return map;
        } else {
            throw new IllegalArgumentException("Unrecognized MoveStruct format");
        }
    }
}