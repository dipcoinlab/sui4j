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

/**
 * @author : Same
 * @datetime : 2026/3/3
 * @Description : gRPC {@link ClientInterceptor} that emits structured log entries for each RPC.
 *
 * <p>At {@code DEBUG} level, logs the outbound method name and type before the call,
 * and the response status code plus elapsed time (in milliseconds) after the call closes.
 * No log entries are produced at {@code INFO} or higher level, ensuring zero overhead
 * in production environments where DEBUG is disabled.
 *
 * <p>Example output:
 * <pre>
 * [gRPC] --> UNARY  sui.rpc.v2.LedgerService/GetObject
 * [gRPC] <-- sui.rpc.v2.LedgerService/GetObject  OK  12ms
 * [gRPC] <-- sui.rpc.v2.LedgerService/GetObject  NOT_FOUND  8ms
 * </pre>
 */
public final class GrpcLoggingInterceptor implements ClientInterceptor {

    private static final Logger log = LoggerFactory.getLogger(GrpcLoggingInterceptor.class);

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {

        return new ForwardingClientCall.SimpleForwardingClientCall<>(next.newCall(method, callOptions)) {

            private final long startNanos = System.nanoTime();

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                if (log.isDebugEnabled()) {
                    log.debug("[gRPC] --> {}  {}", method.getType(), method.getFullMethodName());
                }
                super.start(
                        new ForwardingClientCallListener.SimpleForwardingClientCallListener<>(responseListener) {
                            @Override
                            public void onClose(Status status, Metadata trailers) {
                                if (log.isDebugEnabled()) {
                                    long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000L;
                                    log.debug("[gRPC] <-- {}  {}  {}ms",
                                            method.getFullMethodName(),
                                            status.getCode(),
                                            elapsedMs);
                                }
                                super.onClose(status, trailers);
                            }
                        },
                        headers);
            }
        };
    }
}
