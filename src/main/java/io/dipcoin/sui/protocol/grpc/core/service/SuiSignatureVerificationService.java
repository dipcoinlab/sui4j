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

import sui.rpc.v2.SignatureVerificationServiceOuterClass.VerifySignatureRequest;
import sui.rpc.v2.SignatureVerificationServiceOuterClass.VerifySignatureResponse;

import java.util.concurrent.CompletableFuture;

/**
 * @author : Same
 * @datetime : 2026/3/3
 * @Description : Service interface for the Sui SignatureVerificationService gRPC API.
 *
 * <p>Offloads cryptographic signature verification to the Full Node, supporting all
 * Sui-native signature schemes: Ed25519, Secp256k1, Secp256r1, MultiSig, and ZkLogin.
 *
 * <p>Supported message types (communicated via {@code Bcs.name} in the request):
 * <ul>
 *   <li>{@code "PersonalMessage"} – a user-signed personal message</li>
 *   <li>{@code "TransactionData"} – the BCS bytes of a transaction ready for signing</li>
 * </ul>
 *
 * <p>For ZkLogin signatures, the on-chain JWK (JSON Web Key) set is used by default.
 * A custom set of {@code ActiveJwk} entries can be supplied in the request to verify
 * against a known JWK snapshot, which is useful for unit-testing ZkLogin flows.
 */
public interface SuiSignatureVerificationService {

    /**
     * Verifies that the provided {@code UserSignature} is valid for the given BCS-encoded message.
     *
     * <p>The request must include:
     * <ul>
     *   <li>{@code message} – the BCS-encoded message bytes with {@code name} set to indicate
     *       its type ({@code "PersonalMessage"} or {@code "TransactionData"})</li>
     *   <li>{@code signature} – the {@code UserSignature} to verify</li>
     * </ul>
     *
     * <p>Optionally:
     * <ul>
     *   <li>{@code address} – if provided, the address derived from the signature must match
     *       this value for the verification to succeed</li>
     *   <li>{@code jwks} – a custom JWK set for verifying ZkLogin signatures offline</li>
     * </ul>
     *
     * <p>On success the response contains {@code is_valid = true}. On failure,
     * {@code is_valid = false} and the {@code reason} field explains why.
     *
     * @param request the verify signature request
     * @return the response indicating verification success or failure with an optional reason
     */
    VerifySignatureResponse verifySignature(VerifySignatureRequest request);

    /**
     * @see #verifySignature(VerifySignatureRequest)
     * @return a future completing with the verify signature response
     */
    CompletableFuture<VerifySignatureResponse> verifySignatureAsync(VerifySignatureRequest request);
}
