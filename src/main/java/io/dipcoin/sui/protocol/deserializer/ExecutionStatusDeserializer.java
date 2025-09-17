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
import io.dipcoin.sui.model.transaction.kind.ExecutionStatus;
import io.dipcoin.sui.model.transaction.kind.status.Failure;
import io.dipcoin.sui.model.transaction.kind.status.Success;

import java.io.IOException;

/**
 * @author : Same
 * @datetime : 2025/6/28 11:48
 * @Description : ExecutionStatus deserialize special handling
 */
public class ExecutionStatusDeserializer extends JsonDeserializer<ExecutionStatus> {

    @Override
    public ExecutionStatus deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);

        if (!node.has("status")) {
            throw ctxt.instantiationException(ExecutionStatus.class,
                    "Missing required field 'status' in ExecutionStatus");
        }

        String status = node.get("status").asText();

        try {
            switch (status) {
                case "success":
                    return mapper.convertValue(node, Success.class);
                case "failure":
                    return mapper.convertValue(node, Failure.class);
                default:
                    throw ctxt.instantiationException(ExecutionStatus.class,
                            "Unknown ExecutionStatus: " + status);
            }
        } catch (Exception e) {
            throw ctxt.instantiationException(ExecutionStatus.class,
                    "Failed to deserialize ExecutionStatus");
        }
    }
}
