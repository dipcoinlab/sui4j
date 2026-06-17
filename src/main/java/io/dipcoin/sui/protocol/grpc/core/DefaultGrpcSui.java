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

package io.dipcoin.sui.protocol.grpc.core;

import io.dipcoin.sui.protocol.grpc.GrpcChannelFactory;
import io.dipcoin.sui.protocol.grpc.GrpcOptions;
import io.dipcoin.sui.protocol.grpc.GrpcSuiClient;
import io.dipcoin.sui.protocol.grpc.exceptions.GrpcCallException;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import sui.rpc.v2.*;
import sui.rpc.v2.LedgerServiceOuterClass.*;
import sui.rpc.v2.MovePackageServiceOuterClass.*;
import sui.rpc.v2.NameServiceOuterClass.LookupNameRequest;
import sui.rpc.v2.NameServiceOuterClass.LookupNameResponse;
import sui.rpc.v2.NameServiceOuterClass.ReverseLookupNameRequest;
import sui.rpc.v2.NameServiceOuterClass.ReverseLookupNameResponse;
import sui.rpc.v2.SignatureVerificationServiceOuterClass.VerifySignatureRequest;
import sui.rpc.v2.SignatureVerificationServiceOuterClass.VerifySignatureResponse;
import sui.rpc.v2.StateServiceOuterClass.*;
import sui.rpc.v2.SubscriptionServiceOuterClass.SubscribeCheckpointsRequest;
import sui.rpc.v2.SubscriptionServiceOuterClass.SubscribeCheckpointsResponse;
import sui.rpc.v2.TransactionExecutionServiceOuterClass.ExecuteTransactionRequest;
import sui.rpc.v2.TransactionExecutionServiceOuterClass.ExecuteTransactionResponse;
import sui.rpc.v2.TransactionExecutionServiceOuterClass.SimulateTransactionRequest;
import sui.rpc.v2.TransactionExecutionServiceOuterClass.SimulateTransactionResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/**
 * @author : Same
 * @datetime : 2026/3/3
 * @Description : Default implementation of {@link GrpcSuiClient}.
 *
 * <p>Owns the lifecycle of a single {@link ManagedChannel} and the seven gRPC service stubs
 * built on top of it. Each service exposes both blocking (synchronous) and async
 * ({@link CompletableFuture}-based) variants for all unary RPCs, plus a streaming variant
 * for the checkpoint subscription.
 *
 * <p>Thread safety: all stub instances are thread-safe by the gRPC contract.
 * JDK 21 virtual threads ({@link Executors#newVirtualThreadPerTaskExecutor()}) are used for
 * async calls by default, providing lightweight concurrency ideal for I/O-bound gRPC blocking
 * stubs. Supply a custom {@link Executor} via the constructor if finer control is needed.
 *
 * <p>Resource management: call {@link #close()} or wrap in a try-with-resources block
 * to release the underlying channel and all associated network connections.
 *
 * <p>Do not instantiate directly – use {@link GrpcSuiClient#build(io.dipcoin.sui.protocol.grpc.GrpcOptions)}.
 */
@Slf4j
public class DefaultGrpcSui implements GrpcSuiClient {

    /**
     * Default async executor using JDK 21 virtual threads — ideal for wrapping blocking
     * gRPC stubs since each virtual thread is extremely lightweight and I/O-wait efficient.
     */
    private static final Executor VIRTUAL_THREAD_EXECUTOR =
            Executors.newVirtualThreadPerTaskExecutor();

    private final ManagedChannel channel;
    private final Executor asyncExecutor;

    // --------------------- Blocking stubs (synchronous I/O) ---------------------

    private final LedgerServiceGrpc.LedgerServiceBlockingStub ledgerStub;
    private final StateServiceGrpc.StateServiceBlockingStub stateStub;
    private final TransactionExecutionServiceGrpc.TransactionExecutionServiceBlockingStub txStub;
    private final MovePackageServiceGrpc.MovePackageServiceBlockingStub movePackageStub;
    private final NameServiceGrpc.NameServiceBlockingStub nameStub;
    private final SignatureVerificationServiceGrpc.SignatureVerificationServiceBlockingStub sigStub;

    // --------------------- Async stub (server-streaming) ---------------------

    private final SubscriptionServiceGrpc.SubscriptionServiceStub subscriptionStub;

    /**
     * Creates a new instance using JDK 21 virtual threads for async operations.
     * Prefer {@link GrpcSuiClient#build(GrpcOptions)} over direct construction.
     *
     * @param channel the fully configured channel produced by {@link GrpcChannelFactory}
     */
    public DefaultGrpcSui(ManagedChannel channel) {
        this(channel, VIRTUAL_THREAD_EXECUTOR);
    }

    /**
     * Creates a new instance with a custom executor for async operations.
     * Prefer {@link GrpcSuiClient#build(GrpcOptions, Executor)} over direct construction.
     *
     * @param channel       the fully configured channel produced by {@link GrpcChannelFactory}
     * @param asyncExecutor the executor used to run blocking stubs inside async wrappers
     */
    public DefaultGrpcSui(ManagedChannel channel, Executor asyncExecutor) {
        this.channel       = channel;
        this.asyncExecutor = asyncExecutor;

        this.ledgerStub       = LedgerServiceGrpc.newBlockingStub(channel);
        this.stateStub        = StateServiceGrpc.newBlockingStub(channel);
        this.txStub           = TransactionExecutionServiceGrpc.newBlockingStub(channel);
        this.movePackageStub  = MovePackageServiceGrpc.newBlockingStub(channel);
        this.nameStub         = NameServiceGrpc.newBlockingStub(channel);
        this.sigStub          = SignatureVerificationServiceGrpc.newBlockingStub(channel);
        this.subscriptionStub = SubscriptionServiceGrpc.newStub(channel);
    }

    // =========================================================================
    // SuiLedgerService
    // =========================================================================

    /** {@inheritDoc} */
    @Override
    public GetServiceInfoResponse getServiceInfo(GetServiceInfoRequest request) {
        return call(() -> ledgerStub.getServiceInfo(request));
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<GetServiceInfoResponse> getServiceInfoAsync(GetServiceInfoRequest request) {
        return callAsync(() -> ledgerStub.getServiceInfo(request));
    }

    /** {@inheritDoc} */
    @Override
    public GetObjectResponse getObject(GetObjectRequest request) {
        return call(() -> ledgerStub.getObject(request));
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<GetObjectResponse> getObjectAsync(GetObjectRequest request) {
        return callAsync(() -> ledgerStub.getObject(request));
    }

    /** {@inheritDoc} */
    @Override
    public BatchGetObjectsResponse batchGetObjects(BatchGetObjectsRequest request) {
        return call(() -> ledgerStub.batchGetObjects(request));
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<BatchGetObjectsResponse> batchGetObjectsAsync(BatchGetObjectsRequest request) {
        return callAsync(() -> ledgerStub.batchGetObjects(request));
    }

    /** {@inheritDoc} */
    @Override
    public GetTransactionResponse getTransaction(GetTransactionRequest request) {
        return call(() -> ledgerStub.getTransaction(request));
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<GetTransactionResponse> getTransactionAsync(GetTransactionRequest request) {
        return callAsync(() -> ledgerStub.getTransaction(request));
    }

    /** {@inheritDoc} */
    @Override
    public BatchGetTransactionsResponse batchGetTransactions(BatchGetTransactionsRequest request) {
        return call(() -> ledgerStub.batchGetTransactions(request));
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<BatchGetTransactionsResponse> batchGetTransactionsAsync(BatchGetTransactionsRequest request) {
        return callAsync(() -> ledgerStub.batchGetTransactions(request));
    }

    /** {@inheritDoc} */
    @Override
    public GetCheckpointResponse getCheckpoint(GetCheckpointRequest request) {
        return call(() -> ledgerStub.getCheckpoint(request));
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<GetCheckpointResponse> getCheckpointAsync(GetCheckpointRequest request) {
        return callAsync(() -> ledgerStub.getCheckpoint(request));
    }

    /** {@inheritDoc} */
    @Override
    public GetEpochResponse getEpoch(GetEpochRequest request) {
        return call(() -> ledgerStub.getEpoch(request));
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<GetEpochResponse> getEpochAsync(GetEpochRequest request) {
        return callAsync(() -> ledgerStub.getEpoch(request));
    }

    // =========================================================================
    // SuiStateService
    // =========================================================================

    /** {@inheritDoc} */
    @Override
    public ListDynamicFieldsResponse listDynamicFields(ListDynamicFieldsRequest request) {
        return call(() -> stateStub.listDynamicFields(request));
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<ListDynamicFieldsResponse> listDynamicFieldsAsync(ListDynamicFieldsRequest request) {
        return callAsync(() -> stateStub.listDynamicFields(request));
    }

    /** {@inheritDoc} */
    @Override
    public ListOwnedObjectsResponse listOwnedObjects(ListOwnedObjectsRequest request) {
        return call(() -> stateStub.listOwnedObjects(request));
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<ListOwnedObjectsResponse> listOwnedObjectsAsync(ListOwnedObjectsRequest request) {
        return callAsync(() -> stateStub.listOwnedObjects(request));
    }

    /** {@inheritDoc} */
    @Override
    public GetCoinInfoResponse getCoinInfo(GetCoinInfoRequest request) {
        return call(() -> stateStub.getCoinInfo(request));
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<GetCoinInfoResponse> getCoinInfoAsync(GetCoinInfoRequest request) {
        return callAsync(() -> stateStub.getCoinInfo(request));
    }

    /** {@inheritDoc} */
    @Override
    public GetBalanceResponse getBalance(GetBalanceRequest request) {
        return call(() -> stateStub.getBalance(request));
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<GetBalanceResponse> getBalanceAsync(GetBalanceRequest request) {
        return callAsync(() -> stateStub.getBalance(request));
    }

    /** {@inheritDoc} */
    @Override
    public ListBalancesResponse listBalances(ListBalancesRequest request) {
        return call(() -> stateStub.listBalances(request));
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<ListBalancesResponse> listBalancesAsync(ListBalancesRequest request) {
        return callAsync(() -> stateStub.listBalances(request));
    }

    // =========================================================================
    // SuiTransactionExecutionService
    // =========================================================================

    /** {@inheritDoc} */
    @Override
    public ExecuteTransactionResponse executeTransaction(ExecuteTransactionRequest request) {
        return call(() -> txStub.executeTransaction(request));
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<ExecuteTransactionResponse> executeTransactionAsync(ExecuteTransactionRequest request) {
        return callAsync(() -> txStub.executeTransaction(request));
    }

    /** {@inheritDoc} */
    @Override
    public SimulateTransactionResponse simulateTransaction(SimulateTransactionRequest request) {
        return call(() -> txStub.simulateTransaction(request));
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<SimulateTransactionResponse> simulateTransactionAsync(SimulateTransactionRequest request) {
        return callAsync(() -> txStub.simulateTransaction(request));
    }

    // =========================================================================
    // SuiMovePackageService
    // =========================================================================

    /** {@inheritDoc} */
    @Override
    public GetPackageResponse getPackage(GetPackageRequest request) {
        return call(() -> movePackageStub.getPackage(request));
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<GetPackageResponse> getPackageAsync(GetPackageRequest request) {
        return callAsync(() -> movePackageStub.getPackage(request));
    }

    /** {@inheritDoc} */
    @Override
    public GetDatatypeResponse getDatatype(GetDatatypeRequest request) {
        return call(() -> movePackageStub.getDatatype(request));
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<GetDatatypeResponse> getDatatypeAsync(GetDatatypeRequest request) {
        return callAsync(() -> movePackageStub.getDatatype(request));
    }

    /** {@inheritDoc} */
    @Override
    public GetFunctionResponse getFunction(GetFunctionRequest request) {
        return call(() -> movePackageStub.getFunction(request));
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<GetFunctionResponse> getFunctionAsync(GetFunctionRequest request) {
        return callAsync(() -> movePackageStub.getFunction(request));
    }

    /** {@inheritDoc} */
    @Override
    public ListPackageVersionsResponse listPackageVersions(ListPackageVersionsRequest request) {
        return call(() -> movePackageStub.listPackageVersions(request));
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<ListPackageVersionsResponse> listPackageVersionsAsync(ListPackageVersionsRequest request) {
        return callAsync(() -> movePackageStub.listPackageVersions(request));
    }

    // =========================================================================
    // SuiNameService
    // =========================================================================

    /** {@inheritDoc} */
    @Override
    public LookupNameResponse lookupName(LookupNameRequest request) {
        return call(() -> nameStub.lookupName(request));
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<LookupNameResponse> lookupNameAsync(LookupNameRequest request) {
        return callAsync(() -> nameStub.lookupName(request));
    }

    /** {@inheritDoc} */
    @Override
    public ReverseLookupNameResponse reverseLookupName(ReverseLookupNameRequest request) {
        return call(() -> nameStub.reverseLookupName(request));
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<ReverseLookupNameResponse> reverseLookupNameAsync(ReverseLookupNameRequest request) {
        return callAsync(() -> nameStub.reverseLookupName(request));
    }

    // =========================================================================
    // SuiSignatureVerificationService
    // =========================================================================

    /** {@inheritDoc} */
    @Override
    public VerifySignatureResponse verifySignature(VerifySignatureRequest request) {
        return call(() -> sigStub.verifySignature(request));
    }

    /** {@inheritDoc} */
    @Override
    public CompletableFuture<VerifySignatureResponse> verifySignatureAsync(VerifySignatureRequest request) {
        return callAsync(() -> sigStub.verifySignature(request));
    }

    // =========================================================================
    // SuiSubscriptionService
    // =========================================================================

    /**
     * {@inheritDoc}
     *
     * <p>Uses the async (non-blocking) gRPC stub, so this call returns immediately.
     * The {@code responseObserver} receives checkpoint events on the gRPC executor thread.
     */
    @Override
    public void subscribeCheckpoints(
            SubscribeCheckpointsRequest request,
            StreamObserver<SubscribeCheckpointsResponse> responseObserver) {
        subscriptionStub.subscribeCheckpoints(request, responseObserver);
    }

    // =========================================================================
    // Lifecycle
    // =========================================================================

    /**
     * {@inheritDoc}
     *
     * <p>Initiates graceful shutdown, waiting up to 5 seconds for in-flight RPCs
     * to complete before forcing a hard shutdown.
     */
    @Override
    public void shutdown() {
        log.info("[gRPC] Shutting down channel ...");
        GrpcChannelFactory.shutdown(channel, 5);
        log.info("[gRPC] Channel shut down");
    }

    /** Delegates to {@link #shutdown()}. */
    @Override
    public void close() {
        shutdown();
    }

    // =========================================================================
    // Private helpers
    // =========================================================================

    /**
     * Executes a blocking gRPC call and translates any {@link StatusRuntimeException}
     * into the project-standard {@link GrpcCallException}.
     *
     * @param supplier the blocking stub invocation
     * @param <T>      the response type
     * @return the response
     * @throws GrpcCallException if the gRPC call fails
     */
    private <T> T call(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (StatusRuntimeException e) {
            throw new GrpcCallException(e);
        }
    }

    /**
     * Wraps a blocking gRPC call inside a {@link CompletableFuture}, executing it
     * on the configured {@link #asyncExecutor} to avoid blocking the caller's thread.
     *
     * <p>Any {@link GrpcCallException} thrown inside the future will be wrapped in a
     * {@link java.util.concurrent.ExecutionException} when the future is joined.
     *
     * @param supplier the blocking stub invocation
     * @param <T>      the response type
     * @return a future that resolves with the response or completes exceptionally on failure
     */
    private <T> CompletableFuture<T> callAsync(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(() -> call(supplier), asyncExecutor);
    }
}
