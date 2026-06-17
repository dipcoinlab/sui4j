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

import io.dipcoin.sui.protocol.grpc.interceptor.GrpcDeadlineInterceptor;
import io.dipcoin.sui.protocol.grpc.interceptor.GrpcLoggingInterceptor;
import io.dipcoin.sui.protocol.grpc.interceptor.GrpcRetryInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author : Same
 * @datetime : 2026/3/3
 * @Description : Factory for creating and configuring gRPC {@link ManagedChannel} instances.
 *
 * <p>Applies all production settings in a well-defined order:
 * <ol>
 *   <li>TLS or plaintext transport, based on {@link GrpcOptions#isUseTls()}</li>
 *   <li>HTTP/2 keepalive parameters to prevent idle connection expiry</li>
 *   <li>Maximum inbound message size guard</li>
 *   <li>Custom metadata headers interceptor (e.g. API key, authorization token)</li>
 *   <li>{@link GrpcDeadlineInterceptor} – applies per-call timeout from {@link GrpcOptions#getCallTimeout()}</li>
 *   <li>{@link GrpcRetryInterceptor} – handles transient server-side failures</li>
 *   <li>{@link GrpcLoggingInterceptor} – logs method name, status, and latency</li>
 * </ol>
 *
 * <p>Interceptors are applied from outermost (first registered) to innermost.
 * The deadline interceptor is outermost so it wraps the full retry window.
 * The logging interceptor sits closest to the network layer, capturing
 * the actual final status including any retries.
 *
 * <p>This class is stateless and utility-style; it cannot be instantiated.
 */
public final class GrpcChannelFactory {

    private static final Logger log = LoggerFactory.getLogger(GrpcChannelFactory.class);

    private GrpcChannelFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Creates and returns a fully configured {@link ManagedChannel} based on the
     * provided {@link GrpcOptions}.
     *
     * <p>The returned channel is immediately usable for RPC calls. The caller is
     * responsible for invoking {@link #shutdown(ManagedChannel, long)} when done.
     *
     * @param options the channel and call configuration
     * @return a ready-to-use gRPC managed channel
     */
    public static ManagedChannel create(GrpcOptions options) {
        ManagedChannelBuilder<?> builder = ManagedChannelBuilder
                .forAddress(options.getHost(), options.getPort())
                .maxInboundMessageSize(options.getMaxInboundMessageSize())
                .keepAliveTime(options.getKeepAliveTime().toSeconds(), TimeUnit.SECONDS)
                .keepAliveTimeout(options.getKeepAliveTimeout().toSeconds(), TimeUnit.SECONDS)
                .keepAliveWithoutCalls(options.isKeepAliveWithoutCalls());

        if (!options.isUseTls()) {
            // Plaintext is only appropriate for local/test environments
            builder.usePlaintext();
            log.warn("[gRPC] Connecting to {}:{} WITHOUT TLS — not suitable for production",
                    options.getHost(), options.getPort());
        }

        // Attach custom headers (e.g. API key) to every outbound call
        if (!options.getMetadata().isEmpty()) {
            Metadata fixedHeaders = new Metadata();
            options.getMetadata().forEach((key, value) ->
                    fixedHeaders.put(
                            Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER),
                            value));
            builder.intercept(MetadataUtils.newAttachHeadersInterceptor(fixedHeaders));
        }

        // Interceptor registration order (outermost → innermost):
        //   1. Deadline — sets a per-call timeout if none is already present
        //   2. Retry   — drives retry attempts on transient failures
        //   3. Logging — captures the final status of each individual attempt
        builder.intercept(new GrpcDeadlineInterceptor(options.getCallTimeout().toMillis()));
        builder.intercept(new GrpcRetryInterceptor(options.getMaxRetries()));
        builder.intercept(new GrpcLoggingInterceptor());

        ManagedChannel channel = builder.build();
        if (log.isDebugEnabled()) {
            log.debug("[gRPC] Channel created → {}:{} (tls={}, maxRetries={}, callTimeout={}s)",
                    options.getHost(), options.getPort(),
                    options.isUseTls(),
                    options.getMaxRetries(),
                    options.getCallTimeout().toSeconds());
        }
        return channel;
    }

    /**
     * Gracefully shuts down a {@link ManagedChannel}, waiting up to {@code timeoutSeconds}
     * for in-flight RPCs to complete. Falls back to an immediate forceful shutdown if the
     * graceful window expires or if the waiting thread is interrupted.
     *
     * @param channel        the channel to shut down
     * @param timeoutSeconds maximum seconds to wait for graceful termination
     */
    public static void shutdown(ManagedChannel channel, long timeoutSeconds) {
        channel.shutdown();
        try {
            if (!channel.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) {
                log.warn("[gRPC] Channel did not terminate within {}s — forcing shutdown", timeoutSeconds);
                channel.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.warn("[gRPC] Interrupted while waiting for channel shutdown — forcing shutdown");
            channel.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
