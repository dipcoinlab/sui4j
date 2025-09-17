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
import io.dipcoin.sui.model.move.kind.SuiMoveNormalizedType;
import io.dipcoin.sui.model.move.kind.type.*;
import io.dipcoin.sui.model.move.kind.type.enums.PrimitiveEnum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author : Same
 * @datetime : 2025/6/30 14:43
 * @Description : SuiMoveNormalizedType deserialize special handling
 */
public class SuiMoveNormalizedTypeDeserializer extends JsonDeserializer<SuiMoveNormalizedType> {

    @Override
    public SuiMoveNormalizedType deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        ObjectMapper mapper = (ObjectMapper) p.getCodec();

        // 1. Handle basic types (string form)
        if (node.isTextual()) {
            return handlePrimitiveType(node, ctxt);
        }

        // 2. Handle object form
        if (node.isObject()) {
            // Check all possible type fields
            if (node.has("Struct")) {
                return handleStructType(node, mapper, ctxt);
            } else if (node.has("Vector")) {
                return handleVectorType(node, mapper, ctxt);
            } else if (node.has("TypeParameter")) {
                return handleTypeParameterType(node, ctxt);
            } else if (node.has("Reference")) {
                return handleReferenceType(node, mapper, ctxt);
            } else if (node.has("MutableReference")) {
                return handleMutableReferenceType(node, mapper, ctxt);
            }
        }

        throw ctxt.instantiationException(
                SuiMoveNormalizedType.class,
                "Invalid SuiMoveNormalizedType format. Expected one of: " +
                        "String enum, Struct, Vector, TypeParameter, Reference or MutableReference. " +
                        "Got: " + node.getNodeType()
        );
    }

    private PrimitiveType handlePrimitiveType(JsonNode node, DeserializationContext ctxt)
            throws IOException {
        String typeStr = node.asText();
        if (PrimitiveEnum.find(typeStr) == null) {
            throw ctxt.instantiationException(
                    PrimitiveType.class,
                    "Invalid primitive type: " + typeStr +
                            ". Valid values are: " + Arrays.toString(PrimitiveEnum.values())
            );
        }
        return new PrimitiveType(typeStr);
    }

    private StructType handleStructType(JsonNode node, ObjectMapper mapper, DeserializationContext ctxt)
            throws IOException {
        JsonNode structNode = node.get("Struct");

        // Verify required fields
        validateRequiredFields(structNode, ctxt, "Struct",
                "address", "module", "name", "typeArguments");

        try {
            return new StructType(
                    structNode.get("address").asText(),
                    structNode.get("module").asText(),
                    structNode.get("name").asText(),
                    parseTypeArguments(structNode.get("typeArguments"), mapper, ctxt)
            );
        } catch (Exception e) {
            throw ctxt.instantiationException(
                    StructType.class,
                    "Failed to parse Struct: " + e.getMessage()
            );
        }
    }

    private VectorType handleVectorType(JsonNode node, ObjectMapper mapper, DeserializationContext ctxt)
            throws IOException {
        try {
            SuiMoveNormalizedType elementType = mapper.readValue(
                    node.get("Vector").toString(),
                    SuiMoveNormalizedType.class
            );
            VectorType vectorType = new VectorType();
            vectorType.setVector(elementType);
            return vectorType;
        } catch (Exception e) {
            throw ctxt.instantiationException(
                    VectorType.class,
                    "Failed to parse Vector: " + e.getMessage()
            );
        }
    }

    private TypeParameterType handleTypeParameterType(JsonNode node, DeserializationContext ctxt)
            throws IOException {
        int index = node.get("TypeParameter").asInt();
        if (index < 0) {
            throw ctxt.instantiationException(
                    TypeParameterType.class,
                    "TypeParameter index must be >= 0, got: " + index
            );
        }
        TypeParameterType typeParam = new TypeParameterType();
        typeParam.setTypeParameter(index); // Note: adjust according to actual class definition
        return typeParam;
    }

    private ReferenceType handleReferenceType(JsonNode node, ObjectMapper mapper, DeserializationContext ctxt)
            throws IOException {
        try {
            SuiMoveNormalizedType referencedType = mapper.readValue(
                    node.get("Reference").toString(),
                    SuiMoveNormalizedType.class
            );
            ReferenceType refType = new ReferenceType();
            refType.setReference(referencedType);
            return refType;
        } catch (Exception e) {
            throw ctxt.instantiationException(
                    ReferenceType.class,
                    "Failed to parse Reference: " + e.getMessage()
            );
        }
    }

    private MutableReferenceType handleMutableReferenceType(JsonNode node, ObjectMapper mapper, DeserializationContext ctxt)
            throws IOException {
        try {
            SuiMoveNormalizedType referencedType = mapper.readValue(
                    node.get("MutableReference").toString(),
                    SuiMoveNormalizedType.class
            );
            MutableReferenceType mutRefType = new MutableReferenceType();
            mutRefType.setMutableReference(referencedType);
            return mutRefType;
        } catch (Exception e) {
            throw ctxt.instantiationException(
                    MutableReferenceType.class,
                    "Failed to parse MutableReference: " + e.getMessage()
            );
        }
    }

    private List<SuiMoveNormalizedType> parseTypeArguments(JsonNode node, ObjectMapper mapper, DeserializationContext ctxt)
            throws IOException {
        if (!node.isArray()) {
            throw ctxt.instantiationException(
                    StructType.class,
                    "typeArguments must be an array, got: " + node.getNodeType()
            );
        }

        List<SuiMoveNormalizedType> typeArgs = new ArrayList<>();
        for (JsonNode argNode : node) {
            typeArgs.add(mapper.readValue(argNode.toString(), SuiMoveNormalizedType.class));
        }
        return typeArgs;
    }

    private void validateRequiredFields(JsonNode node, DeserializationContext ctxt,
                                        String parentType, String... fields)
            throws IOException {
        for (String field : fields) {
            if (!node.has(field)) {
                throw ctxt.instantiationException(
                        SuiMoveNormalizedType.class,
                        "Missing required field '" + field + "' in " + parentType
                );
            }
        }
    }
}
