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

package io.dipcoin.sui.protocol.grpc;

import io.dipcoin.sui.protocol.grpc.core.DefaultGrpcSui;
import io.dipcoin.sui.protocol.grpc.core.service.*;

import java.util.concurrent.Executor;

/**
 * @author : Same
 * @datetime : 2026/3/3
 * @Description : Top-level facade interface for the Sui gRPC client.
 *
 * <p>Aggregates all seven Sui Full Node gRPC service interfaces into a single,
 * convenient entry point. Mirrors the design of {@link io.dipcoin.sui.protocol.SuiClient}
 * for the JSON-RPC layer but targets the gRPC transport introduced as the replacement
 * for JSON-RPC (scheduled for full deprecation in July 2026).
 *
 * <p><strong>Service coverage:</strong>
 * <ul>
 *   <li>{@link SuiLedgerService} – objects, transactions, checkpoints, epochs (immutable reads)</li>
 *   <li>{@link SuiStateService} – balances, owned objects, dynamic fields, coin info (live state)</li>
 *   <li>{@link SuiTransactionExecutionService} – execute and simulate transactions</li>
 *   <li>{@link SuiMovePackageService} – Move package, datatype, and function descriptors</li>
 *   <li>{@link SuiNameService} – SuiNS forward and reverse name lookups</li>
 *   <li>{@link SuiSignatureVerificationService} – server-side cryptographic signature verification</li>
 *   <li>{@link SuiSubscriptionService} – server-streaming checkpoint subscription</li>
 * </ul>
 *
 * <p><strong>Usage examples:</strong>
 * <pre>{@code
 * // Quick mainnet client with all defaults
 * try (GrpcSuiClient client = GrpcSuiClient.mainnet()) {
 *     GetServiceInfoResponse info =
 *         client.getServiceInfo(GetServiceInfoRequest.getDefaultInstance());
 *     System.out.println("Chain: " + info.getChain() + ", epoch: " + info.getEpoch());
 * }
 *
 * // Custom options: testnet, 15s call timeout, 5 retries
 * GrpcOptions options = GrpcOptions.testnet()
 *     .callTimeout(Duration.ofSeconds(15))
 *     .maxRetries(5)
 *     .build();
 * GrpcSuiClient client = GrpcSuiClient.build(options);
 *
 * // Local devnet without TLS (e.g. integration tests)
 * GrpcSuiClient local = GrpcSuiClient.build(
 *     GrpcOptions.custom("localhost", 9000, false).build());
 * }</pre>
 *
 * <p><strong>Thread safety:</strong> All method implementations are thread-safe.
 * A single {@link GrpcSuiClient} instance may be shared freely across threads.
 *
 * <p><strong>Resource management:</strong> The client holds an open gRPC channel.
 * Always call {@link #close()} when finished, or use a try-with-resources block.
 */
public interface GrpcSuiClient
        extends SuiLedgerService,
                SuiStateService,
                SuiTransactionExecutionService,
                SuiMovePackageService,
                SuiNameService,
                SuiSignatureVerificationService,
                SuiSubscriptionService,
                AutoCloseable {

    // --------------------- Factory Methods ---------------------

    /**
     * Creates a {@link GrpcSuiClient} connected to the Sui Mainnet gRPC endpoint
     * ({@value GrpcOptions#DEFAULT_MAINNET_HOST}:{@value GrpcOptions#DEFAULT_PORT})
     * with default options (TLS, 30s call timeout, 3 retries).
     *
     * @return a new client targeting mainnet
     */
    static GrpcSuiClient mainnet() {
        return build(GrpcOptions.mainnet().build());
    }

    /**
     * Creates a {@link GrpcSuiClient} connected to the Sui Testnet gRPC endpoint
     * ({@value GrpcOptions#DEFAULT_TESTNET_HOST}:{@value GrpcOptions#DEFAULT_PORT})
     * with default options.
     *
     * @return a new client targeting testnet
     */
    static GrpcSuiClient testnet() {
        return build(GrpcOptions.testnet().build());
    }

    /**
     * Creates a {@link GrpcSuiClient} connected to the Sui Devnet gRPC endpoint
     * ({@value GrpcOptions#DEFAULT_DEVNET_HOST}:{@value GrpcOptions#DEFAULT_PORT})
     * with default options.
     *
     * @return a new client targeting devnet
     */
    static GrpcSuiClient devnet() {
        return build(GrpcOptions.devnet().build());
    }

    /**
     * Creates a {@link GrpcSuiClient} with fully customized channel and call settings.
     *
     * <p>Use {@link GrpcOptions#custom(String, int, boolean)} for local or non-standard endpoints,
     * or {@link GrpcOptions#mainnet()} / {@link GrpcOptions#testnet()} as a starting point
     * for overriding specific settings.
     *
     * @param options the channel and call configuration
     * @return a new client using the specified options
     */
    static GrpcSuiClient build(GrpcOptions options) {
        return new DefaultGrpcSui(GrpcChannelFactory.create(options));
    }

    /**
     * Creates a {@link GrpcSuiClient} with customized options and a specific executor
     * for async ({@link java.util.concurrent.CompletableFuture}) call completions.
     *
     * <p>Provide a dedicated thread pool when the default {@link java.util.concurrent.ForkJoinPool}
     * common pool is unsuitable (e.g. to isolate blocking gRPC threads from computation threads).
     *
     * @param options  the channel and call configuration
     * @param executor the executor used to dispatch async responses
     * @return a new client using the specified options and executor
     */
    static GrpcSuiClient build(GrpcOptions options, Executor executor) {
        return new DefaultGrpcSui(GrpcChannelFactory.create(options), executor);
    }

    // --------------------- Lifecycle ---------------------

    /**
     * Gracefully shuts down the underlying gRPC channel, waiting up to 5 seconds for
     * any in-flight RPCs to complete before forcing a hard shutdown.
     *
     * <p>Equivalent to calling {@link #close()} but without the checked exception.
     */
    void shutdown();

    /**
     * Implements {@link AutoCloseable}. Delegates to {@link #shutdown()}.
     * Suppresses the checked exception declaration for convenience in try-with-resources.
     */
    @Override
    void close();
}
