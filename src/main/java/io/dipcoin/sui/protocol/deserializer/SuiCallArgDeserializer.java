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

import io.dipcoin.sui.model.sui.kind.SuiCallArg;
import io.dipcoin.sui.model.sui.kind.call.ImmOrOwnedObject;
import io.dipcoin.sui.model.sui.kind.call.PureCall;
import io.dipcoin.sui.model.sui.kind.call.ReceivingObject;
import io.dipcoin.sui.model.sui.kind.call.SharedObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.math.BigInteger;

/**
 * @author : Same
 * @datetime : 2025/6/27 19:35
 * @Description : SuiCallArg special deserializer.
 */
public class SuiCallArgDeserializer extends StdDeserializer<SuiCallArg> {

    public SuiCallArgDeserializer() {
        super(SuiCallArg.class);
    }

    @Override
    public SuiCallArg deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        JsonNode root = p.getCodec().readTree(p);

        // 1. Check required fields.
        if (!root.has("type")) {
            throw new JsonParseException(p, "Missing required field 'type'");
        }

        String type = root.get("type").asText();
        if (!"object".equals(type) && !"pure".equals(type)) {
            throw new JsonParseException(p, "Invalid type. Must be 'object' or 'pure'");
        }

        // 2. handle object type
        if ("object".equals(type)) {
            return handleObjectType(root, p);
        }

        // 3. handle pure type
        return handlePureType(root, p);
    }

    private SuiCallArg handleObjectType(JsonNode root, JsonParser p)
            throws JsonParseException {
        if (!root.has("objectType")) {
            throw new JsonParseException(p, "Missing required field 'objectType' for object call arg");
        }

        String objectType = root.get("objectType").asText();
        return switch (objectType) {
            case "immOrOwnedObject" -> parseImmOrOwnedObject(root, p);
            case "sharedObject" -> parseSharedObject(root, p);
            case "receiving" -> parseReceivingObject(root, p);
            default -> throw new JsonParseException(
                    p,
                    "Unknown objectType: " + objectType +
                            ". Expected: immOrOwnedObject/sharedObject/receiving"
            );
        };
    }

    private ImmOrOwnedObject parseImmOrOwnedObject(JsonNode root, JsonParser p)
            throws JsonParseException {
        requireFields(root, p, "digest", "objectId", "version");

        ImmOrOwnedObject arg = new ImmOrOwnedObject();
        arg.setDigest(root.get("digest").asText());
        arg.setObjectId(root.get("objectId").asText());
        arg.setVersion(new BigInteger(root.get("version").asText()));
        return arg;
    }

    private SharedObject parseSharedObject(JsonNode root, JsonParser p)
            throws JsonParseException {
        requireFields(root, p, "initialSharedVersion", "objectId", "mutable");

        SharedObject arg = new SharedObject();
        arg.setInitialSharedVersion(new BigInteger(root.get("initialSharedVersion").asText()));
        arg.setObjectId(root.get("objectId").asText());
        arg.setMutable(root.get("mutable").asBoolean());
        return arg;
    }

    private ReceivingObject parseReceivingObject(JsonNode root, JsonParser p)
            throws JsonParseException {
        requireFields(root, p, "digest", "objectId", "version");

        ReceivingObject arg = new ReceivingObject();
        arg.setDigest(root.get("digest").asText());
        arg.setObjectId(root.get("objectId").asText());
        arg.setVersion(new BigInteger(root.get("version").asText()));
        return arg;
    }

    private SuiCallArg handlePureType(JsonNode root, JsonParser p)
            throws JsonProcessingException {
        if (!root.has("value")) {
            throw new JsonParseException(
                    p,
                    "Missing required field 'value' for pure call arg"
            );
        }

        PureCall arg = new PureCall();
        arg.setValue(p.getCodec().treeToValue(root.get("value"), Object.class));
        if (root.has("valueType")) {
            arg.setValueType(root.get("valueType").asText());
        }
        return arg;
    }

    private void requireFields(JsonNode root, JsonParser p, String... fields)
            throws JsonParseException {
        for (String field : fields) {
            if (!root.has(field)) {
                throw new JsonParseException(p, "Missing required field: " + field);
            }
        }
    }
}
