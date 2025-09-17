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

import io.dipcoin.sui.model.sui.kind.SuiArgument;
import io.dipcoin.sui.model.sui.kind.arg.GasCoin;
import io.dipcoin.sui.model.sui.kind.arg.Input;
import io.dipcoin.sui.model.sui.kind.arg.NestedResult;
import io.dipcoin.sui.model.sui.kind.arg.Result;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author : Same
 * @datetime : 2025/6/28 00:36
 * @Description : SuiArgument special deserializer.
 */
public class SuiArgumentDeserializer extends JsonDeserializer<SuiArgument> {

    @Override
    public SuiArgument deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        // Process the string form of "GasCoin".
        if (node.isTextual() && "GasCoin".equals(node.asText())) {
            return new GasCoin();
        }

        // Process parameters in object form.
        if (node.isObject()) {
            Iterator<String> fieldNames = node.fieldNames();
            if (fieldNames.hasNext()) {
                String type = fieldNames.next();

                switch (type) {
                    case "Input":
                        Input input = new Input();
                        input.setIndex(node.get("Input").asInt());
                        return input;
                    case "Result":
                        Result result = new Result();
                        result.setIndex(node.get("Result").asInt());
                        return result;
                    case "NestedResult":
                        NestedResult nestedResult = new NestedResult();
                        ArrayNode indices = (ArrayNode) node.get("NestedResult");
                        List<Integer> indexList = new ArrayList<>();
                        indices.forEach(n -> indexList.add(n.asInt()));
                        nestedResult.setIndices(indexList);
                        return nestedResult;
                    default:
                        throw ctxt.instantiationException(SuiArgument.class,
                                "Unknown SuiArgument type: " + type);
                }
            }
        }

        throw ctxt.instantiationException(SuiArgument.class,
                "Cannot deserialize SuiArgument from: " + node);
    }
}
