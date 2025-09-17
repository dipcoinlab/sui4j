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

package io.dipcoin.sui.model.transaction.kind;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dipcoin.sui.model.transaction.kind.block.*;
import lombok.Data;

/**
 * @author : Same
 * @datetime : 2025/6/27 12:59
 * @Description : TransactionBlockKind response
 */
@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "kind",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ChangeEpoch.class, name = "ChangeEpoch"),
        @JsonSubTypes.Type(value = Genesis.class, name = "Genesis"),
        @JsonSubTypes.Type(value = ConsensusCommitPrologue.class, name = "ConsensusCommitPrologue"),
        @JsonSubTypes.Type(value = ProgrammableTransaction.class, name = "ProgrammableTransaction"),
        @JsonSubTypes.Type(value = AuthenticatorStateUpdate.class, name = "AuthenticatorStateUpdate"),
        @JsonSubTypes.Type(value = RandomnessStateUpdate.class, name = "RandomnessStateUpdate"),
        @JsonSubTypes.Type(value = EndOfEpochTransaction.class, name = "EndOfEpochTransaction"),
        @JsonSubTypes.Type(value = ConsensusCommitPrologueV2.class, name = "ConsensusCommitPrologueV2"),
        @JsonSubTypes.Type(value = ConsensusCommitPrologueV3.class, name = "ConsensusCommitPrologueV3"),
        @JsonSubTypes.Type(value = ConsensusCommitPrologueV4.class, name = "ConsensusCommitPrologueV4")
})
public abstract class TransactionBlockKind {

    /**
     * kind field determines which object
     */
    protected String kind;

}
