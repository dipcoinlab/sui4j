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

import io.dipcoin.sui.model.transaction.kind.SuiEndOfEpochTransactionKind;
import io.dipcoin.sui.model.transaction.kind.epoch.*;
import io.dipcoin.sui.model.transaction.kind.epoch.*;
import io.dipcoin.sui.model.transaction.kind.epoch.subtypes.SuiAuthenticatorStateExpire;
import io.dipcoin.sui.model.transaction.kind.epoch.subtypes.SuiChangeEpoch;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Set;

/**
 * @author : Same
 * @datetime : 2025/6/27 18:33
 * @Description : SuiEndOfEpochTransactionKind deserialize special handling
 */
public class SuiEndOfEpochTransactionKindDeserializer extends StdDeserializer<SuiEndOfEpochTransactionKind> {

    private static final Set<String> STRING_TYPES = Set.of(
            "AuthenticatorStateCreate",
            "RandomnessStateCreate",
            "CoinDenyListStateCreate",
            "StoreExecutionTimeObservations"
    );

    public SuiEndOfEpochTransactionKindDeserializer() {
        super(SuiEndOfEpochTransactionKind.class);
    }

    @Override
    public SuiEndOfEpochTransactionKind deserialize(
            JsonParser p,
            DeserializationContext ctxt
    ) throws IOException {
        JsonNode root = p.getCodec().readTree(p);

        // Case 1: String type
        if (root.isTextual()) {
            return handleStringType(root.asText(), ctxt);
        }

        // Case 2: Object type
        if (root.isObject()) {
            if (root.has("ChangeEpoch")) {
                return handleChangeEpoch(root, p.getCodec());
            }
            if (root.has("AuthenticatorStateExpire")) {
                return handleAuthenticatorStateExpire(root, p.getCodec());
            }
            if (root.has("BridgeStateCreate")) {
                return handleBridgeStateCreate(root, p);
            }
            if (root.has("BridgeCommitteeUpdate")) {
                return handleBridgeCommitteeUpdate(root, p);
            }
        }

        throw ctxt.instantiationException(
                handledType(),
                "Invalid SuiEndOfEpochTransactionKind format. Expected: " +
                        "(1) String enum: " + STRING_TYPES + " OR " +
                        "(2) Object with one of [ChangeEpoch, AuthenticatorStateExpire, BridgeStateCreate, BridgeCommitteeUpdate]"
        );
    }

    // ========== Specific type handling method ==========

    private SuiEndOfEpochTransactionKind handleStringType(
            String type,
            DeserializationContext ctxt
    ) throws JsonProcessingException {
        if (!STRING_TYPES.contains(type)) {
            throw ctxt.instantiationException(
                    handledType(),
                    "Invalid string type. Allowed: " + STRING_TYPES
            );
        }
        return StringEndOfEpoch.of(type);
    }

    private SuiEndOfEpochTransactionKind handleChangeEpoch(
            JsonNode root,
            ObjectCodec codec
    ) throws JsonProcessingException {
        return new ChangeEpoch()
                .setChangeEpoch(codec.treeToValue(
                        root.get("ChangeEpoch"),
                        SuiChangeEpoch.class
                ));
    }

    private SuiEndOfEpochTransactionKind handleAuthenticatorStateExpire(
            JsonNode root,
            ObjectCodec codec
    ) throws JsonProcessingException {
        return new AuthenticatorStateExpire()
                .setAuthStateExpire(codec.treeToValue(
                        root.get("AuthenticatorStateExpire"),
                        SuiAuthenticatorStateExpire.class
                ));
    }

    private SuiEndOfEpochTransactionKind handleBridgeStateCreate(
            JsonNode root,
            JsonParser p
    ) throws JsonProcessingException {
        JsonNode node = root.get("BridgeStateCreate");
        if (!node.isTextual()) {
            throw new JsonParseException(
                    p,
                    "BridgeStateCreate must be a string (CheckpointDigest)"
            );
        }
        return new BridgeStateCreate()
                .setCheckpointDigest(node.asText());
    }

    private SuiEndOfEpochTransactionKind handleBridgeCommitteeUpdate(
            JsonNode root,
            JsonParser p
    ) throws JsonProcessingException {
        JsonNode node = root.get("BridgeCommitteeUpdate");
        if (!node.isTextual()) {
            throw new JsonParseException(
                    p,
                    "BridgeCommitteeUpdate must be a string (SequenceNumber2)"
            );
        }
        return new BridgeCommitteeUpdate()
                .setSequenceNumber(new BigInteger(node.asText()));
    }

}
