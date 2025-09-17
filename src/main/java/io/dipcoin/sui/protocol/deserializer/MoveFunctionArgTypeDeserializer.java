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
import io.dipcoin.sui.model.move.kind.MoveFunctionArgType;
import io.dipcoin.sui.model.move.kind.arg.ObjectValueKind;
import io.dipcoin.sui.model.move.kind.arg.PureArgType;

import java.io.IOException;

/**
 * @author : Same
 * @datetime : 2025/6/30 10:54
 * @Description : MoveFunctionArgType special deserializer.
 */
public class MoveFunctionArgTypeDeserializer extends JsonDeserializer<MoveFunctionArgType> {


    @Override
    public MoveFunctionArgType deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        // Process the string form of "Pure".
        if (node.isTextual()) {
            if ("Pure".equals(node.asText())) {
                PureArgType pure = new PureArgType();
                pure.setType("Pure");
                return pure;
            }
            throw ctxt.instantiationException(MoveFunctionArgType.class,
                    "Unknown string type: " + node.asText());
        }

        // Process the object form of {"Object": "ByValue"}.
        if (node.isObject()) {
            JsonNode objectNode = node.get("Object");
            if (objectNode != null && objectNode.isTextual()) {
                try {
                    ObjectValueKind kind = new ObjectValueKind();
                    kind.setObject(objectNode.asText());
                    return kind;
                } catch (IllegalArgumentException e) {
                    throw ctxt.instantiationException(MoveFunctionArgType.class,
                            "Invalid ObjectValueKind: " + objectNode.asText());
                }
            }
        }

        throw ctxt.instantiationException(MoveFunctionArgType.class,
                "Invalid MoveFunctionArgType format: " + node);
    }

}
