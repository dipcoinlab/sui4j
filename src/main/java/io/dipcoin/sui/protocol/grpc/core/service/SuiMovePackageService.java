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

import sui.rpc.v2.MovePackageServiceOuterClass.*;

import java.util.concurrent.CompletableFuture;

/**
 * @author : Same
 * @datetime : 2026/3/3
 * @Description : Service interface for the Sui MovePackageService gRPC API.
 *
 * <p>Provides access to normalized Move package metadata: the full package descriptor,
 * individual datatype (struct/enum) definitions, function signatures, and the version
 * history of an upgradeable package.
 *
 * <p>All methods accept a package's {@code storage_id} (the on-chain object ID) as the
 * primary key. For upgraded packages, use any version's ID and {@link #listPackageVersions}
 * to enumerate all versions.
 */
public interface SuiMovePackageService {

    // --------------------- Package ---------------------

    /**
     * Fetches the full package descriptor for the specified package storage ID.
     *
     * <p>The response includes all modules, their dependencies, type origins,
     * and upgrade linkage information. For large packages this can be a heavy response;
     * prefer {@link #getDatatype} or {@link #getFunction} when only a specific member is needed.
     *
     * @param request the request containing the package's storage ID
     * @return the package descriptor response
     */
    GetPackageResponse getPackage(GetPackageRequest request);

    /**
     * @see #getPackage(GetPackageRequest)
     * @return a future completing with the package response
     */
    CompletableFuture<GetPackageResponse> getPackageAsync(GetPackageRequest request);

    // --------------------- Datatypes ---------------------

    /**
     * Fetches the descriptor for a specific datatype (struct or enum) within a Move module.
     *
     * <p>The response includes field names and types for structs, or variant definitions for enums,
     * as well as generic type parameters and abilities.
     *
     * @param request the request containing the package ID, module name, and datatype name
     * @return the datatype descriptor response
     */
    GetDatatypeResponse getDatatype(GetDatatypeRequest request);

    /**
     * @see #getDatatype(GetDatatypeRequest)
     * @return a future completing with the datatype response
     */
    CompletableFuture<GetDatatypeResponse> getDatatypeAsync(GetDatatypeRequest request);

    // --------------------- Functions ---------------------

    /**
     * Fetches the descriptor for a specific function within a Move module.
     *
     * <p>The response includes the function's parameter types, return types, type parameters,
     * visibility ({@code public}/{@code friend}/{@code private}), and whether it is an entry point.
     * This is useful for building or validating Move call arguments programmatically.
     *
     * @param request the request containing the package ID, module name, and function name
     * @return the function descriptor response
     */
    GetFunctionResponse getFunction(GetFunctionRequest request);

    /**
     * @see #getFunction(GetFunctionRequest)
     * @return a future completing with the function response
     */
    CompletableFuture<GetFunctionResponse> getFunctionAsync(GetFunctionRequest request);

    // --------------------- Package Versions ---------------------

    /**
     * Lists all known versions of an upgradeable package, ordered by ascending version number.
     *
     * <p>Any version's storage ID can be used as the {@code package_id} in the request.
     * The response is paginated; use the returned {@code next_page_token} in subsequent
     * calls to retrieve additional pages. Pagination parameters must remain consistent
     * across pages.
     *
     * <p>Useful for traversing the upgrade history of a package to find when a specific
     * module or function was introduced or changed.
     *
     * @param request the request containing the package ID and optional page size / page token
     * @return the paginated response containing (package_id, version) pairs
     */
    ListPackageVersionsResponse listPackageVersions(ListPackageVersionsRequest request);

    /**
     * @see #listPackageVersions(ListPackageVersionsRequest)
     * @return a future completing with the list package versions response
     */
    CompletableFuture<ListPackageVersionsResponse> listPackageVersionsAsync(ListPackageVersionsRequest request);
}
