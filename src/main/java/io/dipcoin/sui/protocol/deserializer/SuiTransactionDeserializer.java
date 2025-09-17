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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.dipcoin.sui.model.sui.MoveCallSuiTransaction;
import io.dipcoin.sui.model.sui.kind.SuiArgument;
import io.dipcoin.sui.model.sui.kind.arg.GasCoin;
import io.dipcoin.sui.model.transaction.kind.SuiTransaction;
import io.dipcoin.sui.model.transaction.kind.operate.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : Same
 * @datetime : 2025/6/28 00:29
 * @Description : SuiTransaction deserialize special handling
 */
public class SuiTransactionDeserializer extends StdDeserializer<SuiTransaction> {

    public SuiTransactionDeserializer() {
        super(SuiTransaction.class);
    }

    @Override
    public SuiTransaction deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        ObjectNode root = mapper.readTree(p);

        if (root.has("MoveCall")) {
            return deserializeMoveCall(root, mapper);
        } else if (root.has("TransferObjects")) {
            return deserializeTransferObjects(root, mapper);
        } else if (root.has("SplitCoins")) {
            return deserializeSplitCoins(root, mapper);
        } else if (root.has("MergeCoins")) {
            return deserializeMergeCoins(root, mapper);
        } else if (root.has("Publish")) {
            return deserializePublish(root, mapper);
        } else if (root.has("Upgrade")) {
            return deserializeUpgrade(root, mapper);
        } else if (root.has("MakeMoveVec")) {
            return deserializeMakeMoveVec(root, mapper);
        } else {
            throw ctxt.instantiationException(SuiTransaction.class,
                    "Unknown transaction type, must be one of: MoveCall, TransferObjects, SplitCoins, MergeCoins, Publish, Upgrade, MakeMoveVec");
        }
    }

    private MoveCall deserializeMoveCall(ObjectNode node, ObjectMapper mapper) {
        return new MoveCall(mapper.convertValue(node.get("MoveCall"), MoveCallSuiTransaction.class));
    }

    private TransferObjects deserializeTransferObjects(ObjectNode node, ObjectMapper mapper) {
        ArrayNode transferObjectsArray = (ArrayNode) node.get("TransferObjects");

        if (transferObjectsArray.size() != 2) {
            throw new IllegalArgumentException("TransferObjects must have exactly 2 elements");
        }

        TransferObjects.TransferObjectsData data = new TransferObjects.TransferObjectsData();
        // Handle first element (objects)
        data.setObjects(this.parseSuiArgumentList(transferObjectsArray.get(0), mapper));

        // Handle second element (address)
        data.setAddress(this.parseSuiArgument(transferObjectsArray.get(1), mapper));

        return new TransferObjects(data);
    }

    private SplitCoins deserializeSplitCoins(ObjectNode node, ObjectMapper mapper) {
        ArrayNode splitCoinsArray = (ArrayNode) node.get("SplitCoins");

        if (splitCoinsArray.size() != 2) {
            throw new IllegalArgumentException("SplitCoins must have exactly 2 elements");
        }

        SplitCoins.SplitCoinsData data = new SplitCoins.SplitCoinsData();

        // Handle first element (coin)
        data.setCoin(this.parseSuiArgument(splitCoinsArray.get(0), mapper));

        // Handle second element (amounts)
        data.setAmounts(this.parseSuiArgumentList(splitCoinsArray.get(1), mapper));

        return new SplitCoins(data);
    }

    private MergeCoins deserializeMergeCoins(ObjectNode node, ObjectMapper mapper) {
        ArrayNode mergeCoinsArray = (ArrayNode) node.get("MergeCoins");

        if (mergeCoinsArray.size() != 2) {
            throw new IllegalArgumentException("MergeCoins must have exactly 2 elements");
        }

        MergeCoins.MergeCoinsData data = new MergeCoins.MergeCoinsData();

        // handle first element (destination)
        data.setDestination(this.parseSuiArgument(mergeCoinsArray.get(0), mapper));

        // handle second element (sources)
        data.setSources(this.parseSuiArgumentList(mergeCoinsArray.get(1), mapper));

        return new MergeCoins(data);
    }

    private Publish deserializePublish(ObjectNode node, ObjectMapper mapper) {
        ArrayNode publishNode = (ArrayNode) node.get("Publish");

        List<String> packageBytes = new ArrayList<>();
        publishNode.forEach(n -> packageBytes.add(n.asText()));

        return new Publish(packageBytes);
    }

    private Upgrade deserializeUpgrade(ObjectNode node, ObjectMapper mapper) {
        ArrayNode upgradeNode = (ArrayNode) node.get("Upgrade");

        if (upgradeNode.size() != 3) {
            throw new IllegalArgumentException("Upgrade must have exactly 3 elements");
        }

        Upgrade.UpgradeData data = new Upgrade.UpgradeData();

        // First element: array of strings (package bytes)
        ArrayNode packageBytesNode = (ArrayNode) upgradeNode.get(0);
        List<String> packageBytes = new ArrayList<>();
        packageBytesNode.forEach(n -> packageBytes.add(n.asText()));
        data.setPackageBytes(packageBytes);

        // Second element: string (package ID)
        data.setPackageId(upgradeNode.get(1).asText());

        // Third element: SuiArgument (upgrade ticket)
        data.setUpgradeTicket(this.parseSuiArgument(upgradeNode.get(2), mapper));

        return new Upgrade(data);
    }

    private MakeMoveVec deserializeMakeMoveVec(ObjectNode node, ObjectMapper mapper) {
        ArrayNode makeMoveVecArray = (ArrayNode) node.get("MakeMoveVec");

        if (makeMoveVecArray.size() != 2) {
            throw new IllegalArgumentException("MakeMoveVec must have exactly 2 elements");
        }

        MakeMoveVec.MakeMoveVecData data = new MakeMoveVec.MakeMoveVecData();

        // First element: string or null (type tag)
        if (makeMoveVecArray.get(0).isNull()) {
            data.setTypeTag(null);
        } else {
            data.setTypeTag(makeMoveVecArray.get(0).asText());
        }

        // Second element: array of SuiArgument (elements)
        data.setElements(this.parseSuiArgumentList(makeMoveVecArray.get(1), mapper));

        return new MakeMoveVec(data);
    }

    /**
     * Helper method: parse SuiArgument list
     * @param node
     * @param mapper
     * @return
     * @throws IOException
     */
    private List<SuiArgument> parseSuiArgumentList(JsonNode node, ObjectMapper mapper) {
        List<SuiArgument> amounts = new ArrayList<>();

        if (!node.isArray()) {
            throw new IllegalArgumentException("Second element of SplitCoins must be an array");
        }
        for (JsonNode item : node) {
            amounts.add(this.parseSuiArgument(item, mapper));
        }
        return amounts;
    }

    /**
     * Helper method: parse SuiArgument
     * @param node
     * @param mapper
     * @return
     * @throws IOException
     */
    private SuiArgument parseSuiArgument(JsonNode node, ObjectMapper mapper) {
        try {
            if (node.isTextual() && "GasCoin".equals(node.asText())) {
                return new GasCoin();
            } else if (node.isObject()) {
                // Use the registered custom deserializer.
                return mapper.treeToValue(node, SuiArgument.class);
            }
            throw new IllegalArgumentException("Unsupported argument type: " + node.getNodeType());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "BridgeStateCreate must be a string or object or array (SuiArgument), cause: " + e.getMessage()
            );
        }
    }
}
