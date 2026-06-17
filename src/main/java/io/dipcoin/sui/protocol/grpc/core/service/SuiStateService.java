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

import sui.rpc.v2.StateServiceOuterClass.*;

import java.util.concurrent.CompletableFuture;

/**
 * @author : Same
 * @datetime : 2026/3/3
 * @Description : Service interface for the Sui StateService gRPC API.
 *
 * <p>The StateService provides access to live, mutable on-chain state: account balances,
 * owned objects, dynamic fields, and coin metadata. Unlike the {@link SuiLedgerService}
 * which operates on finalized immutable data, the data returned here reflects the
 * current state and may change between calls.
 *
 * <p>Pagination is supported by all "List" methods via {@code page_size} and {@code page_token}
 * fields in the request. Pass the {@code next_page_token} from a response into the following
 * request to retrieve the next page.
 */
public interface SuiStateService {

    // --------------------- Dynamic Fields ---------------------

    /**
     * Returns a paginated list of dynamic fields attached to the specified parent object.
     *
     * <p>Dynamic fields are key-value pairs that can be added to any Sui object at runtime
     * without modifying the object's type. This method enumerates them by their parent object ID.
     *
     * @param request the request containing the parent object ID, optional page size, and page token
     * @return the paginated response containing the list of dynamic field objects
     */
    ListDynamicFieldsResponse listDynamicFields(ListDynamicFieldsRequest request);

    /**
     * @see #listDynamicFields(ListDynamicFieldsRequest)
     * @return a future completing with the dynamic fields response
     */
    CompletableFuture<ListDynamicFieldsResponse> listDynamicFieldsAsync(ListDynamicFieldsRequest request);

    // --------------------- Owned Objects ---------------------

    /**
     * Returns a paginated list of objects currently owned by the specified address.
     *
     * <p>Supports optional type-based filtering via the request to narrow the result
     * to a specific Move type (e.g. {@code 0x2::coin::Coin<0x2::sui::SUI>}).
     * Use a {@code read_mask} to limit the fields returned for each object.
     *
     * @param request the request containing the owner address, optional type filter,
     *                optional page size, and page token
     * @return the paginated response containing the owned objects
     */
    ListOwnedObjectsResponse listOwnedObjects(ListOwnedObjectsRequest request);

    /**
     * @see #listOwnedObjects(ListOwnedObjectsRequest)
     * @return a future completing with the owned objects response
     */
    CompletableFuture<ListOwnedObjectsResponse> listOwnedObjectsAsync(ListOwnedObjectsRequest request);

    // --------------------- Coin Info ---------------------

    /**
     * Returns metadata and treasury cap information for the specified fully-qualified coin type.
     *
     * <p>The response may include:
     * <ul>
     *   <li>{@code metadata} – {@code CoinMetadata} object fields (name, symbol, decimals, icon URL)</li>
     *   <li>{@code treasury} – {@code TreasuryCap} info (total supply, supply state)</li>
     *   <li>{@code regulated_metadata} – regulation state and deny-cap ID if the coin is regulated</li>
     * </ul>
     *
     * @param request the request containing the fully-qualified coin type string
     *                (e.g. {@code 0x2::sui::SUI})
     * @return the coin info response
     */
    GetCoinInfoResponse getCoinInfo(GetCoinInfoRequest request);

    /**
     * @see #getCoinInfo(GetCoinInfoRequest)
     * @return a future completing with the coin info response
     */
    CompletableFuture<GetCoinInfoResponse> getCoinInfoAsync(GetCoinInfoRequest request);

    // --------------------- Balances ---------------------

    /**
     * Returns the aggregated balance for a single coin type held by the given address.
     *
     * <p>The balance is the sum of all {@code Coin<T>} objects owned by the address
     * for the specified coin type. This is a scalar value; for an itemized list of
     * individual coin objects, use {@link SuiLedgerService#getObject} / listOwnedObjects.
     *
     * @param request the request containing the owner address and the coin type
     * @return the balance response containing the total balance amount
     */
    GetBalanceResponse getBalance(GetBalanceRequest request);

    /**
     * @see #getBalance(GetBalanceRequest)
     * @return a future completing with the balance response
     */
    CompletableFuture<GetBalanceResponse> getBalanceAsync(GetBalanceRequest request);

    /**
     * Returns all coin type balances held by the given address, one entry per distinct coin type.
     *
     * <p>This is equivalent to calling {@link #getBalance} for each coin type the address holds,
     * but in a single round trip.
     *
     * @param request the request containing the owner address
     * @return the response containing a list of (coin type, balance) pairs
     */
    ListBalancesResponse listBalances(ListBalancesRequest request);

    /**
     * @see #listBalances(ListBalancesRequest)
     * @return a future completing with the list balances response
     */
    CompletableFuture<ListBalancesResponse> listBalancesAsync(ListBalancesRequest request);
}
