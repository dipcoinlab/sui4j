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

package io.dipcoin.sui.model.transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.dipcoin.sui.model.gas.GasCostSummary;
import io.dipcoin.sui.model.move.SuiMoveAbort;
import io.dipcoin.sui.model.object.ObjectRef;
import io.dipcoin.sui.model.object.OwnedObjectRef;
import io.dipcoin.sui.model.transaction.kind.ExecutionStatus;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

/**
 * @author : Same
 * @datetime : 2025/6/28 11:21
 * @Description : TransactionBlockEffects transaction block effects - response after handling transaction or authenticated transaction
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionBlockEffects {

    /**
     * If transaction fails due to abort code, then filled abort error
     */
    private SuiMoveAbort abortError;

    /**
     * Created new object references and owners
     */
    private List<OwnedObjectRef> created;

    /**
     * Currently deleted object references (old references)
     */
    private List<ObjectRef> deleted;

    /**
     * Set of transaction summaries that this transaction depends on
     */
    private List<String> dependencies;

    /**
     * Summary of events emitted during execution
     */
    private String eventsDigest;

    /**
     * Epoch when executing this transaction
     */
    private BigInteger executedEpoch;

    /**
     * Updated gas object reference
     */
    private OwnedObjectRef gasObject;

    /**
     * Gas usage summary
     */
    private GasCostSummary gasUsed;

    /**
     * Message version
     */
    private final String messageVersion = "v1";

    /**
     * Version that each modified (mutated or deleted) object had before being modified by this transaction
     */
    private List<TransactionBlockEffectsModifiedAtVersions> modifiedAtVersions;

    /**
     * Object references and owners of mutated objects, including gas object
     */
    private List<OwnedObjectRef> mutated;

    /**
     * Object references of shared objects used in this transaction
     */
    private List<ObjectRef> sharedObjects;

    /**
     * executionstatus
     */
    private ExecutionStatus status;

    /**
     * transaction summary
     */
    private String transactionDigest;

    /**
     * Object references and owners unwrapped in this transaction
     */
    private List<OwnedObjectRef> unwrapped;

    /**
     * Object references that were previously wrapped in other objects but are now deleted
     */
    private List<ObjectRef> unwrappedThenDeleted;

    /**
     * Object references that are now wrapped in other objects
     */
    private List<ObjectRef> wrapped;

}
