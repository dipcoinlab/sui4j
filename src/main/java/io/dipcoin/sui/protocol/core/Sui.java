/*
 * Copyright 2019 Web3 Labs Ltd.
 * Copyright 2025 Dipcoin LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modifications:
 * - Adapted for Sui RPC calls by Dipcoin LLC, 2025
 */

package io.dipcoin.sui.protocol.core;

import io.dipcoin.sui.model.Request;
import io.dipcoin.sui.model.read.ChainIdentifier;
import io.dipcoin.sui.model.transaction.Transaction;
import io.dipcoin.sui.protocol.http.request.*;
import io.dipcoin.sui.protocol.http.response.*;

/**
 * @author : Same
 * @datetime : 2025/6/25 21:39
 * @Description : Core Sui JSON-RPC API.
 */
public interface Sui {

    // --------------------- Extended API start ---------------------

    /**
     * Return dynamic field object information for specified object
     * @param request
     * @return
     */
    Request<?, SuiObjectResponseWrapper> getDynamicFieldObject(GetDynamicFieldObject request);

    /**
     * Return object list owned by an address
     * @param request
     * @return
     */
    Request<?, PageForSuiObjectResponseAndObjectIdWrapper> getOwnedObjects(GetOwnedObjects request);

    /**
     * Return list of events for a specified query criteria
     * @param request
     * @return
     */
    Request<?, PageForEventAndEventIdWrapper> queryEvents(QueryEvents request);

    // --------------------- Extended API end ---------------------

    // --------------------- Governance Read API start ---------------------

    /**
     * Return latest Sui system status
     * @return
     */
    Request<?, SuiSystemStateWrapper> getLatestSuiSystemState();

    /**
     * Return network reference gas price
     * @return
     */
    Request<?, GasPriceWrapper> getReferenceGasPrice();

    // --------------------- Governance Read API start ---------------------

    // --------------------- Move Utils API start ---------------------

    /**
     * According to normalized type return Move function parameter type
     * @param request
     * @return
     */
    Request<?, MoveFunctionArgTypesWrapper> getMoveFunctionArgTypes(GetMoveFunctionArgTypes request);

    /**
     * Return structured representation of Move function
     * @param request
     * @return
     */
    Request<?, SuiMoveNormalizedFunctionWrapper> getNormalizedMoveFunction(GetNormalizedMoveFunction request);

    /**
     * Return structured representation of Move module
     * @param request
     * @return
     */
    Request<?, SuiMoveNormalizedModuleWrapper> getNormalizedMoveModule(GetNormalizedMoveModule request);

    /**
     * Return structured representation of modules in given package
     * @param request
     * @return
     */
    Request<?, SuiMoveNormalizedModuleByPackageWrapper> getNormalizedMoveModulesByPackage(GetNormalizedMoveModuleByPackage request);

    /**
     * Return structured representation of Move struct
     * @param request
     * @return
     */
    Request<?, SuiMoveNormalizedStructWrapper> getNormalizedMoveStruct(GetNormalizedMoveStruct request);

    // --------------------- Move Utils API end ---------------------

    // --------------------- READ API start ---------------------

    /**
     * Return first four bytes of chain genesis checkpoint summary
     * @return
     */
    Request<?, ChainIdentifier> getChainIdentifier();

    /**
     * Return object information for specified object
     * @param request
     * @return
     */
    Request<?, SuiObjectResponseWrapper> getObject(GetObject request);

    /**
     * Return object data of object list
     * @param request
     * @return
     */
    Request<?, SuiMultiObjectResponseWrapper> multiGetObjects(MultiGetObjects request);

    /**
     * Verify zklogin signature for given bytes, intent graph range and author
     * @param request
     * @return
     */
    Request<?, ZkLoginVerifyResultWrapper> verifyZkLoginSignature(VerifyZkLoginSignature request);

    // --------------------- READ API end ---------------------

    // --------------------- Transaction Builder API start ---------------------

    /**
     * PTB(ProgramTransactionBlock) interface
     * @param request
     * @return
     */
    Request<?, TransactionBlockBytesWrapper> batchTransaction(UnsafeBatchTransaction request);

    /**
     * Call move interface
     * @param request
     * @return
     */
    Request<?, TransactionBlockBytesWrapper> moveCall(UnsafeMoveCall request);

    /**
     * Transfer token
     * @param request
     * @return
     */
    Request<?, TransactionBlockBytesWrapper> pay(UnsafePay request);

    /**
     * Transfer sui
     * @param request
     * @return
     */
    Request<?, TransactionBlockBytesWrapper> paySui(UnsafePaySui request);

    /**
     * Transfer object
     * @param request
     * @return
     */
    Request<?, TransactionBlockBytesWrapper> transferObject(UnsafeTransferObject request);

    // --------------------- Transaction Builder API end ---------------------

    // --------------------- Write API start ---------------------

    /**
     * Execute transaction and wait for result (if needed), request type:
     * 1. WaitForEffectsCert: Wait for TransactionEffectsCert then return to client. This mode is a proxy for transaction finality.
     * 2. WaitForLocalExecution: Wait for TransactionEffectsCert and ensure node has executed transaction locally before returning to client. Local execution ensures that when client triggers subsequent queries, the node can perceive this transaction. However, if node fails to execute transaction locally in time, the boolean type in response will be set to false to indicate this situation. WaitForEffectsCert unless options.show_events or options.show_effects is true, else request_type defaults to true.
     * @param request
     * @return
     */
    Request<?, SuiTransactionBlockResponseWrapper> executeTransactionBlock(Transaction request);

    // --------------------- Write API end ---------------------
}
