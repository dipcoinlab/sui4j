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
import io.dipcoin.sui.model.move.kind.MoveValue;
import io.dipcoin.sui.model.move.kind.struct.MoveStructObject;
import io.dipcoin.sui.model.move.kind.value.MoveVariant;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author : Same
 * @datetime : 2025/7/4 17:49
 * @Description : MoveValue special deserializer.
 */
public class MoveValueDeserializer extends JsonDeserializer<MoveValue> {

    @Override
    public MoveValue deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);
        MoveValue mv = new MoveValue();

        if (node.isInt()) {
            mv.setValueType(MoveValue.MoveValueType.UINT32);
            mv.setValue(node.intValue());
        } else if (node.isBoolean()) {
            mv.setValueType(MoveValue.MoveValueType.BOOLEAN);
            mv.setValue(node.booleanValue());
        } else if (node.isTextual()) {
            mv.setValueType(MoveValue.MoveValueType.STRING);
            mv.setValue(node.textValue());
        } else if (node.isNull()) {
            mv.setValueType(MoveValue.MoveValueType.NULL);
            mv.setValue(null);
        } else if (node.isArray()) {
            mv.setValueType(MoveValue.MoveValueType.ARRAY);
            mv.setValue(mapper.convertValue(node, new TypeReference<List<MoveValue>>() {}));
        } else if (node.has("id")) {
            mv.setValueType(MoveValue.MoveValueType.ID_OBJECT);
            mv.setValue(mapper.convertValue(node, Map.class));
        } else if (node.has("fields") && node.has("type") && node.has("variant")) {
            mv.setValueType(MoveValue.MoveValueType.VARIANT);
            mv.setValue(mapper.convertValue(node, MoveVariant.class));
        } else if (node.has("type") && node.has("fields")) {
            mv.setValueType(MoveValue.MoveValueType.STRUCT_OBJECT);
            mv.setValue(mapper.convertValue(node, MoveStructObject.class));
        } else {
            throw new IllegalArgumentException("Unknown MoveValue structure: " + node.toPrettyString());
        }

        return mv;
    }
}
