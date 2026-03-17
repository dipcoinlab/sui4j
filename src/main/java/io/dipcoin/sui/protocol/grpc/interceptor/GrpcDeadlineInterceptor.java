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

import java.util.concurrent.TimeUnit;

/**
 * @author : Same
 * @datetime : 2026/3/3
 * @Description : gRPC {@link ClientInterceptor} that applies a per-call deadline to every outgoing RPC
 *                when no explicit deadline has already been set.
 *
 * <p>This interceptor ensures that all gRPC calls are bounded by a maximum duration,
 * preventing indefinite blocking caused by unresponsive servers, slow networks, or
 * stalled stream reads.
 *
 * <p>If the caller (or another interceptor) has already set a deadline on the
 * {@link CallOptions}, this interceptor does nothing — allowing explicit per-call
 * overrides to take precedence over the default.
 *
 * <p>Registered at the channel level by {@link io.dipcoin.sui.protocol.grpc.GrpcChannelFactory},
 * this interceptor uses the {@code callTimeout} value from
 * {@link io.dipcoin.sui.protocol.grpc.GrpcOptions}.
 */
public final class GrpcDeadlineInterceptor implements ClientInterceptor {

    private final long deadlineMs;

    /**
     * Creates a new deadline interceptor.
     *
     * @param deadlineMs the default deadline in milliseconds applied to calls lacking an explicit deadline
     */
    public GrpcDeadlineInterceptor(long deadlineMs) {
        this.deadlineMs = deadlineMs;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {

        if (method.getType() == MethodDescriptor.MethodType.UNARY
                && callOptions.getDeadline() == null) {
            callOptions = callOptions.withDeadlineAfter(deadlineMs, TimeUnit.MILLISECONDS);
        }
        return next.newCall(method, callOptions);
    }

}
