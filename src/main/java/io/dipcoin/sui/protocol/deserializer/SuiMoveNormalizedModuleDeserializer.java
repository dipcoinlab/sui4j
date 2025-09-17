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
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.dipcoin.sui.model.move.*;
import io.dipcoin.sui.model.move.kind.SuiMoveNormalizedType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : Same
 * @datetime : 2025/7/1 02:28
 * @Description : SuiMoveNormalizedModule deserialize special handling
 */
public class SuiMoveNormalizedModuleDeserializer extends JsonDeserializer<SuiMoveNormalizedModule> {

    @Override
    public SuiMoveNormalizedModule deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        ObjectNode rootNode = mapper.readTree(p);
        SuiMoveNormalizedModule module = new SuiMoveNormalizedModule();

        // 1. Parse required fields
        module.setFileFormatVersion(getLongField(rootNode, "fileFormatVersion", ctxt));
        module.setAddress(getTextField(rootNode, "address", ctxt));
        module.setName(getTextField(rootNode, "name", ctxt));

        // 2. Parse optional fields
        module.setFriends(parseFriends(rootNode.get("friends"), ctxt));
        module.setStructs(parseStructs(rootNode.get("structs"), mapper, ctxt));
        module.setEnums(parseEnums(rootNode.get("enums"), mapper, ctxt));
        module.setExposedFunctions(parseFunctions(rootNode.get("exposedFunctions"), mapper, ctxt));

        return module;
    }

    // ================ Basic field parsing ================
    private long getLongField(JsonNode node, String field, DeserializationContext ctxt)
            throws IOException {
        if (!node.has(field)) {
            throw ctxt.instantiationException(
                    SuiMoveNormalizedModule.class,
                    "Missing required field: " + field
            );
        }
        return node.get(field).asLong();
    }

    private String getTextField(JsonNode node, String field, DeserializationContext ctxt)
            throws IOException {
        if (!node.has(field)) {
            throw ctxt.instantiationException(
                    SuiMoveNormalizedModule.class,
                    "Missing required field: " + field
            );
        }
        return node.get(field).asText();
    }

    // ================ Nested type parsing ================
    private List<SuiMoveModuleId> parseFriends(JsonNode node, DeserializationContext ctxt)
            throws IOException {
        List<SuiMoveModuleId> friends = new ArrayList<>();
        if (node == null || node.isNull()) {
            return friends;
        }

        if (!node.isArray()) {
            throw ctxt.instantiationException(
                    SuiMoveModuleId.class,
                    "friends must be an array, got: " + node.getNodeType()
            );
        }

        for (JsonNode friendNode : node) {
            friends.add(parseModuleId(friendNode, ctxt));
        }
        return friends;
    }

    private SuiMoveModuleId parseModuleId(JsonNode node, DeserializationContext ctxt)
            throws IOException {
        if (!node.isObject()) {
            throw ctxt.instantiationException(
                    SuiMoveModuleId.class,
                    "ModuleId must be an object, got: " + node.getNodeType()
            );
        }

        SuiMoveModuleId moduleId = new SuiMoveModuleId();
        moduleId.setAddress(getTextField(node, "address", ctxt));
        moduleId.setName(getTextField(node, "name", ctxt));
        return moduleId;
    }

    private Map<String, SuiMoveNormalizedStruct> parseStructs(JsonNode node, ObjectMapper mapper, DeserializationContext ctxt)
            throws IOException {
        Map<String, SuiMoveNormalizedStruct> structs = new HashMap<>();
        if (node == null || node.isNull()) {
            return structs;
        }

        if (!node.isObject()) {
            throw ctxt.instantiationException(
                    SuiMoveNormalizedStruct.class,
                    "structs must be an object, got: " + node.getNodeType()
            );
        }

        node.fields().forEachRemaining(entry -> {
            try {
                structs.put(entry.getKey(), parseStruct(entry.getValue(), mapper, ctxt));
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse struct: " + entry.getKey(), e);
            }
        });
        return structs;
    }

    private SuiMoveNormalizedStruct parseStruct(JsonNode node, ObjectMapper mapper, DeserializationContext ctxt)
            throws IOException {
        SuiMoveNormalizedStruct struct = new SuiMoveNormalizedStruct();

        // Parse required fields
        struct.setAbilities(parseAbilitySet(node.get("abilities"), mapper, ctxt));
        struct.setFields(parseFields(node.get("fields"), mapper, ctxt));
        struct.setTypeParameters(parseTypeParameters(node.get("typeParameters"), mapper, ctxt));

        return struct;
    }

    private List<SuiMoveNormalizedField> parseFields(JsonNode node, ObjectMapper mapper, DeserializationContext ctxt)
            throws IOException {
        List<SuiMoveNormalizedField> fields = new ArrayList<>();
        if (!node.isArray()) {
            throw ctxt.instantiationException(
                    SuiMoveNormalizedField.class,
                    "fields must be an array, got: " + node.getNodeType()
            );
        }

        for (JsonNode fieldNode : node) {
            SuiMoveNormalizedField field = new SuiMoveNormalizedField();
            field.setName(getTextField(fieldNode, "name", ctxt));
            field.setType(mapper.readValue(fieldNode.get("type").toString(), SuiMoveNormalizedType.class));
            fields.add(field);
        }
        return fields;
    }

    private List<SuiMoveStructTypeParameter> parseTypeParameters(JsonNode node, ObjectMapper mapper, DeserializationContext ctxt)
            throws IOException {
        List<SuiMoveStructTypeParameter> params = new ArrayList<>();
        if (!node.isArray()) {
            throw ctxt.instantiationException(
                    SuiMoveStructTypeParameter.class,
                    "typeParameters must be an array, got: " + node.getNodeType()
            );
        }

        for (JsonNode paramNode : node) {
            SuiMoveStructTypeParameter param = new SuiMoveStructTypeParameter();
            param.setConstraints(parseAbilitySet(paramNode.get("constraints"), mapper, ctxt));
            param.setIsPhantom(paramNode.get("isPhantom").asBoolean());
            params.add(param);
        }
        return params;
    }

    private Map<String, SuiMoveNormalizedEnum> parseEnums(JsonNode node, ObjectMapper mapper, DeserializationContext ctxt)
            throws IOException {
        Map<String, SuiMoveNormalizedEnum> enums = new HashMap<>();
        if (node == null || node.isNull()) {
            return enums;
        }

        if (!node.isObject()) {
            throw ctxt.instantiationException(
                    SuiMoveNormalizedEnum.class,
                    "enums must be an object, got: " + node.getNodeType()
            );
        }

        node.fields().forEachRemaining(entry -> {
            try {
                enums.put(entry.getKey(), parseEnum(entry.getValue(), mapper, ctxt));
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse enum: " + entry.getKey(), e);
            }
        });
        return enums;
    }

    private SuiMoveNormalizedEnum parseEnum(JsonNode node, ObjectMapper mapper, DeserializationContext ctxt)
            throws IOException {
        SuiMoveNormalizedEnum enumType = new SuiMoveNormalizedEnum();

        // Parse required fields
        enumType.setAbilities(parseAbilitySet(node.get("abilities"), mapper, ctxt));
        enumType.setTypeParameters(parseTypeParameters(node.get("typeParameters"), mapper, ctxt));

        // Parse optional fields
        if (node.has("variantDeclarationOrder")) {
            enumType.setVariantDeclarationOrder(parseStringList(node.get("variantDeclarationOrder")));
        }

        // parsevariants
        JsonNode variantsNode = node.get("variants");
        if (!variantsNode.isObject()) {
            throw ctxt.instantiationException(
                    SuiMoveNormalizedEnum.class,
                    "variants must be an object, got: " + variantsNode.getNodeType()
            );
        }

        Map<String, List<SuiMoveNormalizedField>> variants = new HashMap<>();
        variantsNode.fields().forEachRemaining(entry -> {
            try {
                variants.put(entry.getKey(), parseFields(entry.getValue(), mapper, ctxt));
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse variant: " + entry.getKey(), e);
            }
        });
        enumType.setVariants(variants);

        return enumType;
    }

    private Map<String, SuiMoveNormalizedFunction> parseFunctions(JsonNode node, ObjectMapper mapper, DeserializationContext ctxt)
            throws IOException {
        Map<String, SuiMoveNormalizedFunction> functions = new HashMap<>();
        if (node == null || node.isNull()) {
            return functions;
        }

        if (!node.isObject()) {
            throw ctxt.instantiationException(
                    SuiMoveNormalizedFunction.class,
                    "exposedFunctions must be an object, got: " + node.getNodeType()
            );
        }

        node.fields().forEachRemaining(entry -> {
            try {
                functions.put(entry.getKey(), parseFunction(entry.getValue(), mapper, ctxt));
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse function: " + entry.getKey(), e);
            }
        });
        return functions;
    }

    private SuiMoveNormalizedFunction parseFunction(JsonNode node, ObjectMapper mapper, DeserializationContext ctxt)
            throws IOException {
        SuiMoveNormalizedFunction function = new SuiMoveNormalizedFunction();

        // Parse required fields
        function.setIsEntry(node.get("isEntry").asBoolean());
        function.setParameters(parseTypeList(node.get("parameters"), mapper, ctxt));
        function.setReturnTypes(parseTypeList(node.get("return"), mapper, ctxt));
        function.setTypeParameters(parseAbilitySetList(node.get("typeParameters"), mapper, ctxt));
        function.setVisibility(SuiMoveVisibility.findByValue(node.get("visibility").asText()).getValue());

        return function;
    }

    // ================ utility method ================

    private SuiMoveAbilitySet parseAbilitySet(JsonNode node, ObjectMapper mapper , DeserializationContext ctxt)
            throws IOException {
        if (node == null || node.isNull()) {
            throw ctxt.instantiationException(
                    SuiMoveAbilitySet.class,
                    "abilities cannot be null"
            );
        }
        return mapper.readValue(node.toString(), SuiMoveAbilitySet.class);
    }

    private List<SuiMoveAbilitySet> parseAbilitySetList(JsonNode node, ObjectMapper mapper, DeserializationContext ctxt)
            throws IOException {
        List<SuiMoveAbilitySet> list = new ArrayList<>();
        if (!node.isArray()) {
            throw ctxt.instantiationException(
                    SuiMoveAbilitySet.class,
                    "typeParameters must be an array, got: " + node.getNodeType()
            );
        }

        for (JsonNode item : node) {
            list.add(parseAbilitySet(item, mapper, ctxt));
        }
        return list;
    }

    private List<SuiMoveNormalizedType> parseTypeList(JsonNode node, ObjectMapper mapper, DeserializationContext ctxt)
            throws IOException {
        List<SuiMoveNormalizedType> types = new ArrayList<>();
        if (!node.isArray()) {
            throw ctxt.instantiationException(
                    SuiMoveNormalizedType.class,
                    "parameters must be an array, got: " + node.getNodeType()
            );
        }

        for (JsonNode typeNode : node) {
            types.add(mapper.readValue(typeNode.toString(), SuiMoveNormalizedType.class));
        }
        return types;
    }

    private List<String> parseStringList(JsonNode node) {
        List<String> list = new ArrayList<>();
        if (node != null && node.isArray()) {
            for (JsonNode item : node) {
                list.add(item.asText());
            }
        }
        return list;
    }
}
