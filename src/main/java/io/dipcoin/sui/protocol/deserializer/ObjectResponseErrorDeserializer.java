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
import io.dipcoin.sui.model.object.kind.ObjectResponseError;
import io.dipcoin.sui.model.object.kind.error.*;

import java.io.IOException;

/**
 * @author : Same
 * @datetime : 2025/7/4 18:18
 * @Description : ObjectResponseError special deserializer.
 */
public class ObjectResponseErrorDeserializer extends JsonDeserializer<ObjectResponseError> {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public ObjectResponseError deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        if (!node.has("code")) {
            throw new IllegalArgumentException("Missing code field in ObjectResponseError");
        }

        String code = node.get("code").asText();

        switch (code) {
            case "notExists":
                return mapper.treeToValue(node, NotExists.class);
            case "dynamicFieldNotFound":
                return mapper.treeToValue(node, DynamicFieldNotFound.class);
            case "deleted":
                return mapper.treeToValue(node, Deleted.class);
            case "unknown":
                return mapper.treeToValue(node, Unknown.class);
            case "displayError":
                return mapper.treeToValue(node, Display.class);
            default:
                throw new IllegalArgumentException("Unknown error code: " + code);
        }
    }
}
