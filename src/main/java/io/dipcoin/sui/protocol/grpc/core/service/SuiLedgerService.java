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

package io.dipcoin.sui.protocol.grpc.core.service;

import sui.rpc.v2.LedgerServiceOuterClass.*;

import java.util.concurrent.CompletableFuture;

/**
 * @author : Same
 * @datetime : 2026/3/3
 * @Description : Service interface for the Sui LedgerService gRPC API.
 *
 * <p>The LedgerService provides read-only access to immutable, finalized on-chain data:
 * objects, executed transactions, checkpoints, and epoch information. Because the data
 * is immutable once written, all methods are idempotent and safe to retry.
 *
 * <p>Each method is available in two forms:
 * <ul>
 *   <li><strong>Synchronous</strong> – blocks the calling thread until the response arrives.</li>
 *   <li><strong>Asynchronous</strong> – returns a {@link CompletableFuture} immediately and
 *       resolves on a background thread, allowing non-blocking composition.</li>
 * </ul>
 *
 * <p>Use {@link com.google.protobuf.FieldMask} in requests to limit the fields returned,
 * reducing both network bandwidth and response parse time.
 */
public interface SuiLedgerService {

    // --------------------- Service Info ---------------------

    /**
     * Queries the Full Node for general service information, including the current
     * chain ID, human-readable chain name, epoch, checkpoint height, wall-clock
     * timestamp, data availability range, and software version string.
     *
     * @param request the service info request (no required fields)
     * @return the service info response
     */
    GetServiceInfoResponse getServiceInfo(GetServiceInfoRequest request);

    /**
     * @see #getServiceInfo(GetServiceInfoRequest)
     * @return a future completing with the service info response
     */
    CompletableFuture<GetServiceInfoResponse> getServiceInfoAsync(GetServiceInfoRequest request);

    // --------------------- Objects ---------------------

    /**
     * Fetches a single Sui object by its {@code object_id}.
     *
     * <p>An optional {@code version} may be specified to retrieve a historic version
     * of the object. If no version is given and the object is live, the current
     * (latest) version is returned.
     *
     * <p>Use the {@code read_mask} to request only specific fields (e.g. {@code object_id,version,digest}).
     * If no mask is specified, defaults to {@code object_id,version,digest}.
     *
     * @param request the object request containing the object ID and optional version/mask
     * @return the object response; the {@code object} field is absent if the object does not exist
     */
    GetObjectResponse getObject(GetObjectRequest request);

    /**
     * @see #getObject(GetObjectRequest)
     * @return a future completing with the object response
     */
    CompletableFuture<GetObjectResponse> getObjectAsync(GetObjectRequest request);

    /**
     * Fetches multiple objects in a single batch RPC call, reducing round-trip overhead
     * compared to issuing individual {@link #getObject} requests.
     *
     * <p>Each element in the response corresponds to one input request, in the same order.
     * Per-object errors (e.g. NOT_FOUND) are embedded inline as a {@code google.rpc.Status}
     * within the result rather than failing the entire batch.
     *
     * <p>A {@code read_mask} at the batch level overrides the per-request masks.
     *
     * @param request the batch request containing one or more per-object requests
     * @return the batch response with one result per input request
     */
    BatchGetObjectsResponse batchGetObjects(BatchGetObjectsRequest request);

    /**
     * @see #batchGetObjects(BatchGetObjectsRequest)
     * @return a future completing with the batch objects response
     */
    CompletableFuture<BatchGetObjectsResponse> batchGetObjectsAsync(BatchGetObjectsRequest request);

    // --------------------- Transactions ---------------------

    /**
     * Fetches a single executed transaction by its digest.
     *
     * <p>Use the {@code read_mask} to control which sub-fields of the
     * {@link sui.rpc.v2.ExecutedTransactionOuterClass.ExecutedTransaction ExecutedTransaction}
     * are populated. Defaults to returning only the {@code digest} if no mask is specified.
     *
     * @param request the transaction request containing the transaction digest
     * @return the transaction response; the {@code transaction} field is absent if not found
     */
    GetTransactionResponse getTransaction(GetTransactionRequest request);

    /**
     * @see #getTransaction(GetTransactionRequest)
     * @return a future completing with the transaction response
     */
    CompletableFuture<GetTransactionResponse> getTransactionAsync(GetTransactionRequest request);

    /**
     * Fetches multiple executed transactions in a single batch RPC call.
     *
     * <p>Each result may contain either the full {@code ExecutedTransaction} or an inline
     * {@code google.rpc.Status} error for transactions that were not found or could not be read.
     *
     * @param request the batch request containing a list of transaction digests
     * @return the batch response with per-transaction results in the same order as the input
     */
    BatchGetTransactionsResponse batchGetTransactions(BatchGetTransactionsRequest request);

    /**
     * @see #batchGetTransactions(BatchGetTransactionsRequest)
     * @return a future completing with the batch transactions response
     */
    CompletableFuture<BatchGetTransactionsResponse> batchGetTransactionsAsync(BatchGetTransactionsRequest request);

    // --------------------- Checkpoints ---------------------

    /**
     * Fetches a checkpoint by its sequence number or digest.
     *
     * <p>Exactly one of {@code sequence_number} or {@code digest} should be set.
     * If neither is provided, the latest executed checkpoint is returned.
     *
     * @param request the checkpoint request
     * @return the checkpoint response
     */
    GetCheckpointResponse getCheckpoint(GetCheckpointRequest request);

    /**
     * @see #getCheckpoint(GetCheckpointRequest)
     * @return a future completing with the checkpoint response
     */
    CompletableFuture<GetCheckpointResponse> getCheckpointAsync(GetCheckpointRequest request);

    // --------------------- Epochs ---------------------

    /**
     * Fetches epoch information by epoch number.
     *
     * <p>If no epoch is specified in the request, the current (most recent) epoch is returned.
     * Epoch data includes protocol configuration, gas prices, validator committee, and timing.
     *
     * @param request the epoch request; set {@code epoch} to query a specific epoch
     * @return the epoch response
     */
    GetEpochResponse getEpoch(GetEpochRequest request);

    /**
     * @see #getEpoch(GetEpochRequest)
     * @return a future completing with the epoch response
     */
    CompletableFuture<GetEpochResponse> getEpochAsync(GetEpochRequest request);
}
