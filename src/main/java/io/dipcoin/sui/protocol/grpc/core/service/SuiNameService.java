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

import sui.rpc.v2.NameServiceOuterClass.LookupNameRequest;
import sui.rpc.v2.NameServiceOuterClass.LookupNameResponse;
import sui.rpc.v2.NameServiceOuterClass.ReverseLookupNameRequest;
import sui.rpc.v2.NameServiceOuterClass.ReverseLookupNameResponse;

import java.util.concurrent.CompletableFuture;

/**
 * @author : Same
 * @datetime : 2026/3/3
 * @Description : Service interface for the Sui NameService (SuiNS) gRPC API.
 *
 * <p>SuiNS maps human-readable domain names (e.g. {@code alice.sui}) to Sui addresses
 * and allows performing the reverse lookup. This is the on-chain equivalent of DNS.
 *
 * <p>Name format: both {@code @alice} and {@code alice.sui} are accepted as input
 * to {@link #lookupName}.
 *
 * <p>Reverse lookup returns the "default name" the owner has set for their address.
 * An address may hold multiple SuiNS names but can designate only one as the default.
 */
public interface SuiNameService {

    // --------------------- Forward Lookup ---------------------

    /**
     * Resolves a SuiNS name to its linked Sui address and returns the full {@code NameRecord}.
     *
     * <p>The record includes the target address, the registration NFT ID, the expiration
     * timestamp, and any additional key-value data stored in the record.
     *
     * <p>Supports both formats:
     * <ul>
     *   <li>{@code alice.sui}</li>
     *   <li>{@code @alice}</li>
     * </ul>
     *
     * @param request the lookup request containing the SuiNS name to resolve
     * @return the response containing the name record, or an empty record if the name is not found
     */
    LookupNameResponse lookupName(LookupNameRequest request);

    /**
     * @see #lookupName(LookupNameRequest)
     * @return a future completing with the lookup name response
     */
    CompletableFuture<LookupNameResponse> lookupNameAsync(LookupNameRequest request);

    // --------------------- Reverse Lookup ---------------------

    /**
     * Performs a reverse lookup: given a Sui address, returns the SuiNS name record
     * that the address has designated as its default name, if any.
     *
     * <p>Not all addresses have a default SuiNS name. If the address has not set a
     * default name, the response record will be absent or empty.
     *
     * @param request the reverse lookup request containing the Sui address
     * @return the response containing the name record for the default name of the address,
     *         or an empty record if no default name is set
     */
    ReverseLookupNameResponse reverseLookupName(ReverseLookupNameRequest request);

    /**
     * @see #reverseLookupName(ReverseLookupNameRequest)
     * @return a future completing with the reverse lookup name response
     */
    CompletableFuture<ReverseLookupNameResponse> reverseLookupNameAsync(ReverseLookupNameRequest request);
}
