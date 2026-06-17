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

import sui.rpc.v2.TransactionExecutionServiceOuterClass.ExecuteTransactionRequest;
import sui.rpc.v2.TransactionExecutionServiceOuterClass.ExecuteTransactionResponse;
import sui.rpc.v2.TransactionExecutionServiceOuterClass.SimulateTransactionRequest;
import sui.rpc.v2.TransactionExecutionServiceOuterClass.SimulateTransactionResponse;

import java.util.concurrent.CompletableFuture;

/**
 * @author : Same
 * @datetime : 2026/3/3
 * @Description : Service interface for the Sui TransactionExecutionService gRPC API.
 *
 * <p>Provides two core operations for submitting and inspecting programmable transactions:
 * <ul>
 *   <li>{@link #executeTransaction} – submits a signed, serialized transaction to the network
 *       and waits for it to be included in a checkpoint.</li>
 *   <li>{@link #simulateTransaction} – dry-runs a transaction locally without broadcasting it,
 *       returning execution effects, command outputs, and gas estimates.</li>
 * </ul>
 *
 * <p>Both methods accept a {@link com.google.protobuf.FieldMask} to control which fields
 * are populated in the returned {@link sui.rpc.v2.ExecutedTransactionOuterClass.ExecutedTransaction}.
 */
public interface SuiTransactionExecutionService {

    // --------------------- Execute ---------------------

    /**
     * Executes a signed transaction and returns the resulting executed transaction.
     *
     * <p>The request must include:
     * <ul>
     *   <li>A BCS-serialized {@code Transaction} object</li>
     *   <li>One or more {@code UserSignature}s authorizing the transaction (multi-sig is supported)</li>
     * </ul>
     *
     * <p>If no {@code read_mask} is specified, the response defaults to
     * {@code effects.status} and {@code checkpoint}.
     *
     * @param request the execute transaction request containing the transaction bytes and signatures
     * @return the response containing the executed transaction with its effects
     */
    ExecuteTransactionResponse executeTransaction(ExecuteTransactionRequest request);

    /**
     * @see #executeTransaction(ExecuteTransactionRequest)
     * @return a future completing with the execute transaction response
     */
    CompletableFuture<ExecuteTransactionResponse> executeTransactionAsync(ExecuteTransactionRequest request);

    // --------------------- Simulate ---------------------

    /**
     * Simulates the execution of a transaction without submitting it to the network.
     *
     * <p>Useful for:
     * <ul>
     *   <li>Gas cost estimation before actual execution</li>
     *   <li>Inspecting the return values and intermediate outputs of Move calls
     *       (via {@code command_outputs} in the response)</li>
     *   <li>Validating that the transaction can be executed successfully</li>
     *   <li>Receiving an automatic gas selection suggestion by setting
     *       {@code do_gas_selection = true} in the request</li>
     * </ul>
     *
     * <p>To bypass all transaction validation checks (e.g. for testing malformed
     * transactions), set {@code checks = DISABLED} in the request.
     *
     * @param request the simulate transaction request
     * @return the response containing the simulated execution effects, per-command outputs,
     *         and an optional suggested gas price
     */
    SimulateTransactionResponse simulateTransaction(SimulateTransactionRequest request);

    /**
     * @see #simulateTransaction(SimulateTransactionRequest)
     * @return a future completing with the simulate transaction response
     */
    CompletableFuture<SimulateTransactionResponse> simulateTransactionAsync(SimulateTransactionRequest request);
}
