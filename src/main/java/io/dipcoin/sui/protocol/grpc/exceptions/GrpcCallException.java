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

package io.dipcoin.sui.protocol.grpc.exceptions;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

/**
 * @author : Same
 * @datetime : 2026/3/3
 * @Description : Runtime exception that wraps a gRPC {@link StatusRuntimeException}, providing
 *                structured access to the gRPC status code and retry eligibility.
 *
 * <p>This is the standard exception thrown by all methods in the gRPC service layer
 * when a remote call fails. Callers can inspect {@link #getStatus()} to determine
 * the exact failure reason (e.g. NOT_FOUND, PERMISSION_DENIED) and use
 * {@link #isRetryable()} to decide whether to attempt a retry.
 */
public final class GrpcCallException extends RuntimeException {

    private final Status status;

    /**
     * Creates an exception with a custom message and an UNKNOWN status.
     *
     * @param message the detail message
     */
    public GrpcCallException(String message) {
        super(message);
        this.status = Status.UNKNOWN;
    }

    /**
     * Creates an exception from a generic cause, deriving the gRPC status
     * if the cause is a {@link StatusRuntimeException}.
     *
     * @param message the detail message
     * @param cause   the root cause
     */
    public GrpcCallException(String message, Throwable cause) {
        super(message, cause);
        if (cause instanceof StatusRuntimeException sre) {
            this.status = sre.getStatus();
        } else {
            this.status = Status.UNKNOWN;
        }
    }

    /**
     * Creates an exception directly from a {@link StatusRuntimeException}.
     * The message is derived from the status code and description.
     *
     * @param cause the originating gRPC status exception
     */
    public GrpcCallException(StatusRuntimeException cause) {
        super(cause.getStatus().getCode().name() + ": " + cause.getStatus().getDescription(), cause);
        this.status = cause.getStatus();
    }

    /**
     * Returns the gRPC {@link Status} associated with this exception.
     *
     * @return the gRPC status, never {@code null}
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Returns {@code true} if the failure is likely transient and the call
     * may be safely retried with exponential backoff.
     *
     * <p>Retryable status codes:
     * <ul>
     *   <li>{@link Status.Code#UNAVAILABLE} – server temporarily unreachable</li>
     *   <li>{@link Status.Code#DEADLINE_EXCEEDED} – call timed out</li>
     *   <li>{@link Status.Code#RESOURCE_EXHAUSTED} – rate-limited or quota exceeded</li>
     * </ul>
     *
     * @return {@code true} if the error is transient and retrying may succeed
     */
    public boolean isRetryable() {
        Status.Code code = status.getCode();
        return code == Status.Code.UNAVAILABLE
                || code == Status.Code.DEADLINE_EXCEEDED
                || code == Status.Code.RESOURCE_EXHAUSTED;
    }

}
