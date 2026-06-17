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

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : Same
 * @datetime : 2026/3/3
 * @Description : Immutable configuration object for the Sui gRPC channel and per-call settings.
 *                Constructed via the nested {@link Builder} using a fluent API.
 *
 * <p>All time-based values default to production-sensible settings. Override them as needed:
 * <pre>{@code
 * GrpcOptions options = GrpcOptions.mainnet()
 *     .callTimeout(Duration.ofSeconds(15))
 *     .keepAliveTime(Duration.ofSeconds(20))
 *     .maxRetries(5)
 *     .build();
 * GrpcSuiClient client = GrpcSuiClient.build(options);
 * }</pre>
 */
public final class GrpcOptions {

    /** Default gRPC/TLS endpoint for the Sui Mainnet Full Node. */
    public static final String DEFAULT_MAINNET_HOST = "fullnode.mainnet.sui.io";

    /** Default gRPC/TLS endpoint for the Sui Testnet Full Node. */
    public static final String DEFAULT_TESTNET_HOST = "fullnode.testnet.sui.io";

    /** Default gRPC/TLS endpoint for the Sui Devnet Full Node. */
    public static final String DEFAULT_DEVNET_HOST = "fullnode.devnet.sui.io";

    /** Standard TLS port used by all Sui Full Node gRPC endpoints. */
    public static final int DEFAULT_PORT = 443;

    private final String host;
    private final int port;
    private final boolean useTls;
    private final Duration connectTimeout;
    private final Duration callTimeout;
    private final Duration keepAliveTime;
    private final Duration keepAliveTimeout;
    private final boolean keepAliveWithoutCalls;
    private final int maxInboundMessageSize;
    private final int maxRetries;
    private final Map<String, String> metadata;

    private GrpcOptions(Builder builder) {
        this.host = builder.host;
        this.port = builder.port;
        this.useTls = builder.useTls;
        this.connectTimeout = builder.connectTimeout;
        this.callTimeout = builder.callTimeout;
        this.keepAliveTime = builder.keepAliveTime;
        this.keepAliveTimeout = builder.keepAliveTimeout;
        this.keepAliveWithoutCalls = builder.keepAliveWithoutCalls;
        this.maxInboundMessageSize = builder.maxInboundMessageSize;
        this.maxRetries = builder.maxRetries;
        this.metadata = Collections.unmodifiableMap(new HashMap<>(builder.metadata));
    }

    // --------------------- Static Factory Methods ---------------------

    /**
     * Returns a {@link Builder} pre-configured to connect to the Sui Mainnet
     * gRPC endpoint with TLS enabled.
     *
     * @return a new builder targeting mainnet
     */
    public static Builder mainnet() {
        return new Builder(DEFAULT_MAINNET_HOST, DEFAULT_PORT, true);
    }

    /**
     * Returns a {@link Builder} pre-configured to connect to the Sui Testnet
     * gRPC endpoint with TLS enabled.
     *
     * @return a new builder targeting testnet
     */
    public static Builder testnet() {
        return new Builder(DEFAULT_TESTNET_HOST, DEFAULT_PORT, true);
    }

    /**
     * Returns a {@link Builder} pre-configured to connect to the Sui Devnet
     * gRPC endpoint with TLS enabled.
     *
     * @return a new builder targeting devnet
     */
    public static Builder devnet() {
        return new Builder(DEFAULT_DEVNET_HOST, DEFAULT_PORT, true);
    }

    /**
     * Returns a {@link Builder} targeting a custom host and port.
     * Set {@code useTls = false} for local/plaintext connections (e.g. integration tests).
     *
     * @param host   the gRPC server hostname or IP
     * @param port   the gRPC server port
     * @param useTls whether to use TLS transport security
     * @return a new builder targeting the specified endpoint
     */
    public static Builder custom(String host, int port, boolean useTls) {
        return new Builder(host, port, useTls);
    }

    // --------------------- Accessors ---------------------

    /** @return the target gRPC server hostname */
    public String getHost() { return host; }

    /** @return the target gRPC server port */
    public int getPort() { return port; }

    /** @return {@code true} if TLS is enabled for the channel */
    public boolean isUseTls() { return useTls; }

    /** @return the initial TCP connection timeout */
    public Duration getConnectTimeout() { return connectTimeout; }

    /** @return the per-call deadline applied to each gRPC invocation */
    public Duration getCallTimeout() { return callTimeout; }

    /** @return the HTTP/2 keepalive ping interval */
    public Duration getKeepAliveTime() { return keepAliveTime; }

    /** @return the time to wait for a keepalive ping ACK before closing */
    public Duration getKeepAliveTimeout() { return keepAliveTimeout; }

    /** @return whether keepalive pings are sent even with no active RPCs */
    public boolean isKeepAliveWithoutCalls() { return keepAliveWithoutCalls; }

    /** @return the maximum inbound message size in bytes */
    public int getMaxInboundMessageSize() { return maxInboundMessageSize; }

    /** @return the maximum number of retry attempts for transient errors */
    public int getMaxRetries() { return maxRetries; }

    /**
     * Returns an unmodifiable view of the custom gRPC metadata headers.
     * These are sent as HTTP/2 headers with every RPC call.
     *
     * @return immutable map of header name to value
     */
    public Map<String, String> getMetadata() { return metadata; }

    // --------------------- Builder ---------------------

    /**
     * Fluent builder for constructing immutable {@link GrpcOptions} instances.
     */
    public static final class Builder {

        private final String host;
        private final int port;
        private final boolean useTls;

        private Duration connectTimeout = Duration.ofSeconds(10);
        private Duration callTimeout    = Duration.ofSeconds(30);
        private Duration keepAliveTime  = Duration.ofSeconds(30);
        private Duration keepAliveTimeout = Duration.ofSeconds(10);
        private boolean  keepAliveWithoutCalls = false;
        private int      maxInboundMessageSize = 50 * 1024 * 1024; // 50 MB
        private int      maxRetries = 3;
        private Map<String, String> metadata = new HashMap<>();

        private Builder(String host, int port, boolean useTls) {
            this.host   = host;
            this.port   = port;
            this.useTls = useTls;
        }

        /**
         * Sets the per-call deadline. Each gRPC invocation will be cancelled
         * if it does not complete within this duration.
         * Defaults to {@code 30 seconds}.
         *
         * @param callTimeout the per-call timeout
         * @return this builder
         */
        public Builder callTimeout(Duration callTimeout) {
            this.callTimeout = callTimeout;
            return this;
        }

        /**
         * Sets the initial TCP connection timeout.
         * Defaults to {@code 10 seconds}.
         *
         * @param connectTimeout the connection timeout
         * @return this builder
         */
        public Builder connectTimeout(Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        /**
         * Sets the HTTP/2 keepalive ping interval. A ping is sent after
         * this duration of inactivity to keep the connection alive.
         * Defaults to {@code 30 seconds}.
         *
         * @param keepAliveTime the keepalive interval
         * @return this builder
         */
        public Builder keepAliveTime(Duration keepAliveTime) {
            this.keepAliveTime = keepAliveTime;
            return this;
        }

        /**
         * Sets the maximum time to wait for a keepalive ping ACK before
         * the connection is considered dead and closed.
         * Defaults to {@code 10 seconds}.
         *
         * @param keepAliveTimeout the keepalive timeout
         * @return this builder
         */
        public Builder keepAliveTimeout(Duration keepAliveTimeout) {
            this.keepAliveTimeout = keepAliveTimeout;
            return this;
        }

        /**
         * Whether to send keepalive HTTP/2 pings even when no RPCs are in-flight.
         * Useful for long-lived connections to detect broken links early.
         * Defaults to {@code false}.
         *
         * @param keepAliveWithoutCalls {@code true} to enable idle keepalive
         * @return this builder
         */
        public Builder keepAliveWithoutCalls(boolean keepAliveWithoutCalls) {
            this.keepAliveWithoutCalls = keepAliveWithoutCalls;
            return this;
        }

        /**
         * Sets the maximum allowed size in bytes for inbound gRPC messages.
         * Requests returning large objects (e.g. batch calls) may need a higher limit.
         * Defaults to {@code 50 MB}.
         *
         * @param maxInboundMessageSize the maximum message size in bytes
         * @return this builder
         */
        public Builder maxInboundMessageSize(int maxInboundMessageSize) {
            this.maxInboundMessageSize = maxInboundMessageSize;
            return this;
        }

        /**
         * Sets the maximum number of automatic retry attempts for transient errors.
         * Retried status codes: {@code UNAVAILABLE}, {@code DEADLINE_EXCEEDED},
         * {@code RESOURCE_EXHAUSTED}.
         * Defaults to {@code 3}.
         *
         * @param maxRetries the retry limit (0 disables retries)
         * @return this builder
         */
        public Builder maxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        /**
         * Adds a single custom gRPC metadata header. Useful for API keys or
         * authorization tokens.
         *
         * @param key   the header name (ASCII)
         * @param value the header value
         * @return this builder
         */
        public Builder metadata(String key, String value) {
            this.metadata.put(key, value);
            return this;
        }

        /**
         * Adds multiple custom gRPC metadata headers in bulk.
         *
         * @param metadata a map of header names to values
         * @return this builder
         */
        public Builder metadata(Map<String, String> metadata) {
            this.metadata.putAll(metadata);
            return this;
        }

        /**
         * Builds and returns an immutable {@link GrpcOptions} instance
         * containing the current builder state.
         *
         * @return the built options
         */
        public GrpcOptions build() {
            return new GrpcOptions(this);
        }
    }
}
