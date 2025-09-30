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
import io.dipcoin.sui.model.event.Event;
import io.dipcoin.sui.model.read.ChainIdentifier;
import io.dipcoin.sui.model.transaction.Transaction;
import io.dipcoin.sui.protocol.SuiClient;
import io.dipcoin.sui.protocol.SuiService;
import io.dipcoin.sui.protocol.http.request.*;
import io.dipcoin.sui.protocol.http.response.*;
import io.dipcoin.sui.protocol.rx.AdaptivePollingManager;
import io.dipcoin.sui.protocol.rx.Callback;
import io.dipcoin.sui.protocol.rx.EventQueryTask;
import io.dipcoin.sui.protocol.rx.JsonRpcSuiPolling;
import io.dipcoin.sui.protocol.rx.model.AdaptiveConfig;
import io.dipcoin.sui.util.Async;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author : Same
 * @datetime : 2025/6/25 21:48
 * @Description : JSON-RPC 2.0 factory implementation.
 */
public class JsonRpcSui implements SuiClient {

    public static final int DEFAULT_BLOCK_TIME = 5 * 100;
//    private static final BigInteger MIN_BLOB_BASE_FEE = new BigInteger("1");
//    private static final BigInteger BLOB_BASE_FEE_UPDATE_FRACTION = new BigInteger("3338477");

    protected final SuiService suiService;
    private final JsonRpcSuiPolling jsonRpcSuiPolling;
    private final AdaptivePollingManager adaptivePollingManager;
    private final long blockTime;
    private final ScheduledExecutorService scheduledExecutorService;

    public JsonRpcSui(SuiService suiService) {
        this(suiService, DEFAULT_BLOCK_TIME, Async.defaultExecutorService());
    }

    public JsonRpcSui(
            SuiService suiService,
            long pollingInterval,
            ScheduledExecutorService scheduledExecutorService) {
        this.suiService = suiService;
        this.jsonRpcSuiPolling = new JsonRpcSuiPolling(scheduledExecutorService);
        this.adaptivePollingManager = new AdaptivePollingManager(jsonRpcSuiPolling);
        this.blockTime = pollingInterval;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    // --------------------- Extended API start ---------------------

    @Override
    public Request<?, SuiObjectResponseWrapper> getDynamicFieldObject(GetDynamicFieldObject request) {
        return new Request<>(
                "suix_getDynamicFieldObject",
                List.of(request.getParentObjectId(), request.getName()),
                suiService,
                SuiObjectResponseWrapper.class);
    }

    @Override
    public Request<?, PageForSuiObjectResponseAndObjectIdWrapper> getOwnedObjects(GetOwnedObjects request) {
        return new Request<>(
                "suix_getOwnedObjects",
                Arrays.asList(request.getAddress(), request.getQuery(), request.getCursor(), request.getLimit()),
                suiService,
                PageForSuiObjectResponseAndObjectIdWrapper.class);
    }

    @Override
    public Request<?, PageForEventAndEventIdWrapper> queryEvents(QueryEvents request) {
        return new Request<>(
                "suix_queryEvents",
                Arrays.asList(request.getQuery(), request.getCursor(), request.getLimit(), request.getDescendingOrder()),
                suiService,
                PageForEventAndEventIdWrapper.class);
    }

    // --------------------- Extended API end ---------------------

    // --------------------- Governance Read API start ---------------------

    @Override
    public Request<?, SuiSystemStateWrapper> getLatestSuiSystemState() {
        return new Request<>(
                "suix_getLatestSuiSystemState",
                Collections.<String>emptyList(),
                suiService,
                SuiSystemStateWrapper.class);
    }

    @Override
    public Request<?, GasPriceWrapper> getReferenceGasPrice() {
        return new Request<>(
                "suix_getReferenceGasPrice",
                Collections.<String>emptyList(),
                suiService,
                GasPriceWrapper.class);
    }

    // --------------------- Governance Read API start ---------------------

    // --------------------- Move Utils API start ---------------------

    @Override
    public Request<?, MoveFunctionArgTypesWrapper> getMoveFunctionArgTypes(GetMoveFunctionArgTypes request) {

        return new Request<>(
                "sui_getMoveFunctionArgTypes",
                List.of(request.getObjectId(), request.getModule(), request.getFunction()),
                suiService,
                MoveFunctionArgTypesWrapper.class);
    }

    @Override
    public Request<?, SuiMoveNormalizedFunctionWrapper> getNormalizedMoveFunction(GetNormalizedMoveFunction request) {
        return new Request<>(
                "sui_getNormalizedMoveFunction",
                List.of(request.getObjectId(), request.getModuleName(), request.getFunctionName()),
                suiService,
                SuiMoveNormalizedFunctionWrapper.class);
    }

    @Override
    public Request<?, SuiMoveNormalizedModuleWrapper> getNormalizedMoveModule(GetNormalizedMoveModule request) {
        return new Request<>(
                "sui_getNormalizedMoveModule",
                List.of(request.getObjectId(), request.getModuleName()),
                suiService,
                SuiMoveNormalizedModuleWrapper.class);
    }

    @Override
    public Request<?, SuiMoveNormalizedModuleByPackageWrapper> getNormalizedMoveModulesByPackage(GetNormalizedMoveModuleByPackage request) {
        return new Request<>(
                "sui_getNormalizedMoveModulesByPackage",
                List.of(request.getObjectId()),
                suiService,
                SuiMoveNormalizedModuleByPackageWrapper.class);
    }

    @Override
    public Request<?, SuiMoveNormalizedStructWrapper> getNormalizedMoveStruct(GetNormalizedMoveStruct request) {
        return new Request<>(
                "sui_getNormalizedMoveStruct",
                List.of(request.getObjectId(), request.getModuleName(), request.getStructName()),
                suiService,
                SuiMoveNormalizedStructWrapper.class);
    }

    // --------------------- Move Utils API end ---------------------

    // --------------------- Read API start ---------------------

    @Override
    public Request<?, ChainIdentifier> getChainIdentifier() {
        return new Request<>(
                "sui_getChainIdentifier",
                Collections.<String>emptyList(),
                suiService,
                ChainIdentifier.class);
    }

    @Override
    public Request<?, SuiObjectResponseWrapper> getObject(GetObject request) {
        return new Request<>(
                "sui_getObject",
                List.of(request.getObjectId(), request.getOptions()),
                suiService,
                SuiObjectResponseWrapper.class);
    }

    @Override
    public Request<?, SuiMultiObjectResponseWrapper> multiGetObjects(MultiGetObjects request) {
        return new Request<>(
                "sui_multiGetObjects",
                List.of(request.getObjectIds(), request.getOptions()),
                suiService,
                SuiMultiObjectResponseWrapper.class);
    }

    @Override
    public Request<?, ZkLoginVerifyResultWrapper> verifyZkLoginSignature(VerifyZkLoginSignature request) {
        return new Request<>(
                "sui_verifyZkLoginSignature",
                List.of(request.getBytes(), request.getSignature(), request.getIntentScope(), request.getAuthor()),
                suiService,
                ZkLoginVerifyResultWrapper.class);
    }

    // --------------------- Read API end ---------------------

    // --------------------- Transaction Builder API start ---------------------

    @Override
    public Request<?, TransactionBlockBytesWrapper> batchTransaction(UnsafeBatchTransaction request) {
        return new Request<>(
                "unsafe_batchTransaction",
                List.of(request.getSigner(), request.getSingleTransactionParams(), request.getGas(), request.getGasBudget(), request.getExecutionMode()),
                suiService,
                TransactionBlockBytesWrapper.class);
    }

    @Override
    public Request<?, TransactionBlockBytesWrapper> moveCall(UnsafeMoveCall request) {
        return new Request<>(
                "unsafe_moveCall",
                List.of(request.getSigner(), request.getPackageObjectId(), request.getModule(), request.getFunction(), request.getTypeArguments(), request.getArguments(), request.getGas(), request.getGasBudget(), request.getExecutionMode()),
                suiService,
                TransactionBlockBytesWrapper.class);
    }

    @Override
    public Request<?, TransactionBlockBytesWrapper> pay(UnsafePay request) {
        return new Request<>(
                "unsafe_pay",
                List.of(request.getSigner(), request.getInputCoins(), request.getRecipients(), request.getAmounts(), request.getGas(), request.getGasBudget()),
                suiService,
                TransactionBlockBytesWrapper.class);
    }

    @Override
    public Request<?, TransactionBlockBytesWrapper> paySui(UnsafePaySui request) {
        return new Request<>(
                "unsafe_paySui",
                List.of(request.getSigner(), request.getInputCoins(), request.getRecipients(), request.getAmounts(), request.getGasBudget()),
                suiService,
                TransactionBlockBytesWrapper.class);
    }

    @Override
    public Request<?, TransactionBlockBytesWrapper> transferObject(UnsafeTransferObject request) {
        return new Request<>(
                "unsafe_transferObject",
                List.of(request.getSigner(), request.getObjectId(), request.getGas(), request.getGasBudget(), request.getRecipient()),
                suiService,
                TransactionBlockBytesWrapper.class);
    }

    // --------------------- Transaction Builder API end ---------------------

    // --------------------- Write API start ---------------------

    @Override
    public Request<?, SuiTransactionBlockResponseWrapper> executeTransactionBlock(Transaction request) {
        return new Request<>(
                "sui_executeTransactionBlock",
                List.of(request.getTxBytes(), request.getSignatures(), request.getOptions(), request.getRequestType()),
                suiService,
                SuiTransactionBlockResponseWrapper.class);
    }

    // --------------------- Write API end ---------------------

    // --------------------- polling API start ---------------------

    @Override
    public void shutdown() {
        scheduledExecutorService.shutdown();
        try {
            suiService.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to close web3j service", e);
        }
    }

    @Override
    public String suiEventSubscribe(QueryEvents request, Callback<List<Event>> callback) {
        return suiEventSubscribe(request, callback, blockTime);
    }

    @Override
    public String suiEventSubscribe(QueryEvents request, Callback<List<Event>> callback, long interval) {
        EventQueryTask eventTask = new EventQueryTask(this, request, callback);
        String taskId = "event-query" + eventTask.hashCode();
        adaptivePollingManager.registerAdaptiveTask(
                taskId,
                eventTask,
                interval,
                AdaptiveConfig.defaults()
        );
        return taskId;
    }

    @Override
    public boolean unSubscribe(String taskId) {
        return jsonRpcSuiPolling.stopTask(taskId);
    }

    // --------------------- polling API end ---------------------

    @Override
    public void close() throws Exception {
        this.shutdown();
    }

}
