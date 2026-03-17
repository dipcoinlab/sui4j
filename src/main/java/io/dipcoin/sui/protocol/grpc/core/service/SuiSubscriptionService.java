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

import io.grpc.stub.StreamObserver;
import sui.rpc.v2.SubscriptionServiceOuterClass.SubscribeCheckpointsRequest;
import sui.rpc.v2.SubscriptionServiceOuterClass.SubscribeCheckpointsResponse;

/**
 * @author : Same
 * @datetime : 2026/3/3
 * @Description : Service interface for the Sui SubscriptionService gRPC API.
 *
 * <p>Provides a server-side streaming subscription to the Sui checkpoint stream.
 * Unlike the unary services, this interface is inherently asynchronous and callback-based.
 *
 * <p><strong>Stream semantics:</strong>
 * <ul>
 *   <li>The stream starts from the <em>latest executed checkpoint</em> as seen by the server
 *       at subscription time.</li>
 *   <li>Checkpoints are delivered in strictly ascending, gapless sequence number order.</li>
 *   <li>If the subscription is interrupted (network error, server restart), the client can
 *       reinitialize the subscription and use other APIs (e.g. {@link SuiLedgerService#getCheckpoint})
 *       to back-fill the gap.</li>
 * </ul>
 *
 * <p><strong>Backpressure:</strong> The underlying gRPC stream uses HTTP/2 flow control.
 * If the observer's {@code onNext} handler is slow, the stream will be flow-controlled
 * automatically. Avoid blocking inside {@code onNext} to keep throughput high.
 *
 * <p><strong>Cancellation:</strong> To stop the stream, cancel the {@link io.grpc.Context}
 * or call {@link io.grpc.ClientCall#cancel} on the underlying call.
 *
 * <p>Example usage:
 * <pre>{@code
 * client.subscribeCheckpoints(
 *     SubscribeCheckpointsRequest.getDefaultInstance(),
 *     new StreamObserver<>() {
 *         public void onNext(SubscribeCheckpointsResponse response) {
 *             long seq = response.getCursor();
 *             processCheckpoint(seq, response.getCheckpoint());
 *         }
 *         public void onError(Throwable t) {
 *             log.error("Checkpoint stream error", t);
 *         }
 *         public void onCompleted() {
 *             log.info("Checkpoint stream ended");
 *         }
 *     });
 * }</pre>
 */
public interface SuiSubscriptionService {

    /**
     * Opens a server-streaming subscription to the Sui checkpoint stream.
     *
     * <p>Use the {@code read_mask} in the request to control which fields of each
     * {@link sui.rpc.v2.CheckpointOuterClass.Checkpoint Checkpoint} are populated in the responses.
     * If no mask is specified, all checkpoint fields are returned.
     *
     * @param request          the subscription request, optionally containing a {@code read_mask}
     * @param responseObserver the observer receiving checkpoint responses, errors, and the
     *                         terminal {@code onCompleted} signal. Must not be {@code null}.
     */
    void subscribeCheckpoints(
            SubscribeCheckpointsRequest request,
            StreamObserver<SubscribeCheckpointsResponse> responseObserver);
}
