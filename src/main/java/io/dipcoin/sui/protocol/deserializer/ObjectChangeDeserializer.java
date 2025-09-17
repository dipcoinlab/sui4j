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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.dipcoin.sui.model.object.kind.ObjectChange;
import io.dipcoin.sui.model.object.kind.change.*;

import java.io.IOException;

/**
 * @author : Same
 * @datetime : 2025/6/28 11:09
 * @Description : ObjectChange special deserializer.
 */
public class ObjectChangeDeserializer extends JsonDeserializer<ObjectChange> {

    @Override
    public ObjectChange deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        ObjectNode node = mapper.readTree(p);

        if (!node.has("type")) {
            throw ctxt.instantiationException(ObjectChange.class,
                    "Missing required field 'type' in SuiObjectChange");
        }

        String type = node.get("type").asText();

        try {
            switch (type) {
                case "published":
                    return mapper.convertValue(node, Published.class);
                case "transferred":
                    return mapper.convertValue(node, Transferred.class);
                case "mutated":
                    return mapper.convertValue(node, Mutated.class);
                case "deleted":
                    return mapper.convertValue(node, Deleted.class);
                case "wrapped":
                    return mapper.convertValue(node, Wrapped.class);
                case "created":
                    return mapper.convertValue(node, Created.class);
                default:
                    throw ctxt.instantiationException(ObjectChange.class,
                            "Unknown SuiObjectChange type: " + type);
            }
        } catch (Exception e) {
            throw ctxt.instantiationException(ObjectChange.class,
                    "Failed to deserialize SuiObjectChange of type " + type);
        }
    }
}
