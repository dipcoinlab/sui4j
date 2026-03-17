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

package io.dipcoin.sui.protocol.grpc.interceptor;

import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author : Same
 * @datetime : 2026/3/3
 * @Description : gRPC {@link ClientInterceptor} that retries UNARY calls on transient errors
 *                using truncated exponential backoff with random jitter.
 *
 * <p>Only the following status codes trigger a retry:
 * <ul>
 *   <li>{@link Status.Code#UNAVAILABLE} – server temporarily unreachable or restarting</li>
 *   <li>{@link Status.Code#DEADLINE_EXCEEDED} – call timed out before completion</li>
 *   <li>{@link Status.Code#RESOURCE_EXHAUSTED} – server-side rate limit or quota exceeded</li>
 * </ul>
 *
 * <p>Server-streaming, client-streaming, and bidirectional-streaming calls are
 * intentionally <strong>not</strong> retried because partial stream delivery makes
 * automatic retry unsafe without application-level coordination.
 *
 * <p>Backoff formula: {@code delay = min(BASE_DELAY * 2^attempt, MAX_DELAY) * jitter}
 * where {@code jitter ∈ [0.75, 1.25]}.
 */
public final class GrpcRetryInterceptor implements ClientInterceptor {

    private static final Logger log = LoggerFactory.getLogger(GrpcRetryInterceptor.class);

    /** Status codes that warrant an automatic retry attempt. */
    private static final Set<Status.Code> RETRYABLE_CODES = EnumSet.of(
            Status.Code.UNAVAILABLE,
            Status.Code.DEADLINE_EXCEEDED,
            Status.Code.RESOURCE_EXHAUSTED
    );

    /** Initial backoff delay in milliseconds before the first retry. */
    private static final long BASE_DELAY_MS = 100L;

    /** Upper bound on backoff delay to prevent extremely long waits. */
    private static final long MAX_DELAY_MS = 5_000L;

    /** Jitter magnitude: delays are multiplied by a factor in [1-JITTER, 1+JITTER]. */
    private static final double JITTER = 0.25;

    private final int maxRetries;

    /**
     * Creates a new retry interceptor.
     *
     * @param maxRetries maximum number of retry attempts per call (0 = no retries)
     */
    public GrpcRetryInterceptor(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {

        // Only UNARY calls are safe to retry automatically
        if (method.getType() != MethodDescriptor.MethodType.UNARY || maxRetries <= 0) {
            return next.newCall(method, callOptions);
        }
        return new RetryingClientCall<>(method, callOptions, next, maxRetries);
    }

    // --------------------- Inner RetryingClientCall ---------------------

    /**
     * A delegating {@link ClientCall} that re-issues the request on retryable failures.
     * Each attempt re-creates the underlying call from the channel, ensuring that a
     * fresh TCP stream is used after a connection-level failure.
     */
    private static final class RetryingClientCall<ReqT, RespT> extends ClientCall<ReqT, RespT> {

        private final MethodDescriptor<ReqT, RespT> method;
        private final CallOptions callOptions;
        private final Channel channel;
        private final int maxRetries;

        private ClientCall<ReqT, RespT> delegate;
        private ClientCall.Listener<RespT> responseListener;
        private Metadata requestHeaders;
        private ReqT sentMessage;
        private int attempt = 0;

        private RetryingClientCall(
                MethodDescriptor<ReqT, RespT> method,
                CallOptions callOptions,
                Channel channel,
                int maxRetries) {
            this.method    = method;
            this.callOptions = callOptions;
            this.channel   = channel;
            this.maxRetries = maxRetries;
            this.delegate  = channel.newCall(method, callOptions);
        }

        @Override
        public void start(ClientCall.Listener<RespT> responseListener, Metadata headers) {
            this.responseListener = responseListener;
            this.requestHeaders   = headers;
            delegate.start(new RetryListener(responseListener), headers);
        }

        @Override
        public void request(int numMessages) {
            delegate.request(numMessages);
        }

        @Override
        public void cancel(String message, Throwable cause) {
            delegate.cancel(message, cause);
        }

        @Override
        public void halfClose() {
            delegate.halfClose();
        }

        @Override
        public void sendMessage(ReqT message) {
            this.sentMessage = message;
            delegate.sendMessage(message);
        }

        /**
         * Schedules and executes a retry attempt after a computed backoff period.
         * Creates a completely new underlying {@link ClientCall} for each attempt.
         */
        private void retry() {
            long delayMs = computeBackoffMs(attempt);
            log.warn("[gRPC] Retrying {} (attempt {}/{}) after {}ms delay",
                    method.getFullMethodName(), attempt + 1, maxRetries, delayMs);
            try {
                TimeUnit.MILLISECONDS.sleep(delayMs);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return;
            }
            attempt++;
            delegate = channel.newCall(method, callOptions);
            delegate.start(new RetryListener(responseListener), requestHeaders);
            delegate.request(1);
            if (sentMessage != null) {
                delegate.sendMessage(sentMessage);
            }
            delegate.halfClose();
        }

        /**
         * Computes the backoff interval for a given attempt index using truncated
         * exponential backoff: {@code min(BASE * 2^attempt, MAX) * jitter}.
         *
         * @param attemptIndex zero-based attempt index
         * @return the sleep duration in milliseconds
         */
        private static long computeBackoffMs(int attemptIndex) {
            long base  = Math.min(BASE_DELAY_MS * (1L << attemptIndex), MAX_DELAY_MS);
            double factor = 1.0 + ThreadLocalRandom.current().nextDouble(-JITTER, JITTER);
            return Math.max(0L, (long) (base * factor));
        }

        // --------------------- Inner RetryListener ---------------------

        /**
         * Intercepts {@link #onClose} to decide whether a retry is warranted.
         * On retryable errors within the retry budget, delegates to {@link RetryingClientCall#retry()}.
         * On success or non-retryable failure, forwards the event to the outer listener.
         */
        private final class RetryListener extends ClientCall.Listener<RespT> {

            private final ClientCall.Listener<RespT> outer;

            RetryListener(ClientCall.Listener<RespT> outer) {
                this.outer = outer;
            }

            @Override
            public void onHeaders(Metadata headers) {
                outer.onHeaders(headers);
            }

            @Override
            public void onMessage(RespT message) {
                outer.onMessage(message);
            }

            @Override
            public void onClose(Status status, Metadata trailers) {
                if (!status.isOk()
                        && RETRYABLE_CODES.contains(status.getCode())
                        && attempt < maxRetries) {
                    retry();
                } else {
                    outer.onClose(status, trailers);
                }
            }

            @Override
            public void onReady() {
                outer.onReady();
            }
        }
    }
}
