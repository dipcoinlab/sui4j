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
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.dipcoin.sui.model.transaction.kind.TransactionBlockKind;
import io.dipcoin.sui.model.transaction.kind.block.*;

import java.io.IOException;
import java.util.Map;

/**
 * @author : Same
 * @datetime : 2025/6/27 14:37
 * @Description : TransactionBlockKind deserialize special handling
 */
public class TransactionBlockKindDeserializer extends StdDeserializer<TransactionBlockKind> {

    private static final String FIELD = "kind";

    private static final Map<String, Class<? extends TransactionBlockKind>> KIND_MAPPING =
            Map.ofEntries(
                    Map.entry("ChangeEpoch", ChangeEpoch.class),
                    Map.entry("Genesis", Genesis.class),
                    Map.entry("ConsensusCommitPrologue", ConsensusCommitPrologue.class),
                    Map.entry("ProgrammableTransaction", ProgrammableTransaction.class),
                    Map.entry("AuthenticatorStateUpdate", AuthenticatorStateUpdate.class),
                    Map.entry("RandomnessStateUpdate", RandomnessStateUpdate.class),
                    Map.entry("EndOfEpochTransaction", EndOfEpochTransaction.class),
                    Map.entry("ConsensusCommitPrologueV2", ConsensusCommitPrologueV2.class),
                    Map.entry("ConsensusCommitPrologueV3", ConsensusCommitPrologueV3.class),
                    Map.entry("ConsensusCommitPrologueV4", ConsensusCommitPrologueV4.class)
            );

    public TransactionBlockKindDeserializer() {
        super(TransactionBlockKind.class);
    }

    @Override
    public TransactionBlockKind deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {

        JsonNode root = p.getCodec().readTree(p);
        JsonNode kindNode = root.get(FIELD);

        if (kindNode == null) {
            throw ctxt.instantiationException(
                    TransactionBlockKind.class,
                    "Missing required field 'kind'"
            );
        }

        String kind = kindNode.asText();
        Class<? extends TransactionBlockKind> targetClass = KIND_MAPPING.get(kind);

        if (targetClass == null) {
            throw ctxt.instantiationException(
                    TransactionBlockKind.class,
                    "Unknown transaction kind: " + kind
            );
        }

        try {
            return p.getCodec().treeToValue(root, targetClass);
        } catch (JsonProcessingException e) {
            throw ctxt.instantiationException(
                    TransactionBlockKind.class,
                    "Failed to deserialize " + kind + ": " + e.getMessage()
            );
        }
    }
}
