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
import com.fasterxml.jackson.databind.*;
import io.dipcoin.sui.model.object.kind.Owner;
import io.dipcoin.sui.model.object.kind.owner.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;

/**
 * @author : Same
 * @datetime : 2025/6/28 10:42
 * @Description : Owner special deserializer.
 */
public class OwnerDeserializer extends JsonDeserializer<Owner> {

    @Override
    public Owner deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        ObjectMapper mapper = (ObjectMapper) p.getCodec();

        // Process the string form of "Immutable".
        if (node.isTextual()) {
            if ("Immutable".equals(node.asText())) {
                return new Immutable();
            }
            throw ctxt.instantiationException(Owner.class,
                    "Unknown string Owner type: " + node.asText());
        }

        // Process the object form.
        if (node.isObject()) {
            Iterator<String> fieldNames = node.fieldNames();

            if (!fieldNames.hasNext()) {
                throw ctxt.instantiationException(Owner.class, "Owner object is empty");
            }

            String type = fieldNames.next();
            JsonNode valueNode = node.get(type);

            // Ensure only one field is present.
            if (fieldNames.hasNext()) {
                throw ctxt.instantiationException(Owner.class,
                        "Owner object should have exactly one field, found multiple");
            }

            switch (type) {
                case "AddressOwner":
                    return parseAddressOwner(type, valueNode, ctxt);

                case "ObjectOwner":
                    return parseObjectOwner(type, valueNode, ctxt);

                case "Shared":
                    return parseShared(type, valueNode, ctxt);

                case "ConsensusAddressOwner":
                    return parseConsensusAddressOwner(type, valueNode, mapper, ctxt);

                default:
                    throw ctxt.instantiationException(Owner.class,
                            "Unknown Owner type: " + type);
            }
        }

        throw ctxt.instantiationException(Owner.class,
                "Owner must be string or object, got: " + node.getNodeType());
    }

    private AddressOwner parseAddressOwner(String type, JsonNode valueNode, DeserializationContext ctxt) throws JsonMappingException {
        if (!valueNode.isTextual()) {
            throw ctxt.instantiationException(AddressOwner.class,
                    "AddressOwner value must be string, got: " + valueNode.getNodeType());
        }

        AddressOwner owner = new AddressOwner();
        owner.setType(type);
        owner.setAddressOwner(valueNode.asText());
        return owner;
    }

    private ObjectOwner parseObjectOwner(String type, JsonNode valueNode, DeserializationContext ctxt) throws JsonMappingException {
        if (!valueNode.isTextual()) {
            throw ctxt.instantiationException(ObjectOwner.class,
                    "ObjectOwner value must be string, got: " + valueNode.getNodeType());
        }

        ObjectOwner owner = new ObjectOwner();
        owner.setType(type);
        owner.setObjectId(valueNode.asText());
        return owner;
    }

    private Shared parseShared(String type, JsonNode valueNode, DeserializationContext ctxt) throws JsonMappingException {
        if (!valueNode.isObject()) {
            throw ctxt.instantiationException(Shared.class,
                    "Shared value must be object, got: " + valueNode.getNodeType());
        }

        JsonNode versionNode = valueNode.get("initial_shared_version");
        if (versionNode == null) {
            throw ctxt.instantiationException(Shared.class,
                    "Shared object missing 'initial_shared_version' field");
        }

        if (!versionNode.isNumber() && !versionNode.isTextual()) {
            throw ctxt.instantiationException(Shared.class,
                    "initial_shared_version must be number or string, got: " + versionNode.getNodeType());
        }

        Shared shared = new Shared();
        shared.setType(type);

        try {
            if (versionNode.isTextual()) {
                shared.setInitialSharedVersion(new BigInteger(versionNode.asText()));
            } else {
                shared.setInitialSharedVersion(BigInteger.valueOf(versionNode.asLong()));
            }
        } catch (NumberFormatException e) {
            throw ctxt.instantiationException(Shared.class,
                    "Invalid initial_shared_version format: " + versionNode.asText());
        }

        return shared;
    }

    private ConsensusAddressOwner parseConsensusAddressOwner(String type, JsonNode valueNode,
                                                             ObjectMapper mapper, DeserializationContext ctxt) throws JsonMappingException {
        try {
            ConsensusAddressOwner owner = new ConsensusAddressOwner();
            owner.setType(type);

            // Parse directly as an internal class.
            ConsensusAddressOwner.ConsensusData data = mapper.treeToValue(valueNode, ConsensusAddressOwner.ConsensusData.class);
            owner.setConsensus(data);

            return owner;
        } catch (IOException e) {
            throw ctxt.instantiationException(ConsensusAddressOwner.class,
                    "Failed to parse ConsensusAddressOwner: " + e.getMessage());
        }
    }
}
