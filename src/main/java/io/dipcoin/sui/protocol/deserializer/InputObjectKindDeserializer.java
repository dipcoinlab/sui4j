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
import io.dipcoin.sui.model.object.kind.InputObjectKind;
import io.dipcoin.sui.model.object.kind.input.ImmOrOwnedMoveObject;
import io.dipcoin.sui.model.object.kind.input.MovePackage;
import io.dipcoin.sui.model.object.kind.input.SharedMoveObject;

import java.io.IOException;
import java.util.Iterator;

/**
 * @author : Same
 * @datetime : 2025/6/26 21:41
 * @Description : InputObjectKind special deserializer.
 */
public class InputObjectKindDeserializer  extends JsonDeserializer<InputObjectKind> {

    @Override
    public InputObjectKind deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        ObjectNode node = mapper.readTree(p);

        // get all field names
        Iterator<String> fieldNames = node.fieldNames();

        if (fieldNames.hasNext()) {
            String typeField = fieldNames.next();

            switch (typeField) {
                case "MovePackage":
                    MovePackage movePackage = new MovePackage();
                    movePackage.setMovePackage(node.get(typeField).asText());
                    return movePackage;
                case "ImmOrOwnedMoveObject":
                    return mapper.treeToValue(node.get(typeField), ImmOrOwnedMoveObject.class);
                case "SharedMoveObject":
                    return mapper.treeToValue(node.get(typeField), SharedMoveObject.class);
                default:
                    throw ctxt.instantiationException(InputObjectKind.class,
                            "Unknown InputObjectKind type: " + typeField);
            }
        }

        throw ctxt.instantiationException(InputObjectKind.class,
                "Empty InputObjectKind object");
    }

}
