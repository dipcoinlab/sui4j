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

package io.dipcoin.sui.grpc;

import com.google.protobuf.ByteString;
import com.google.protobuf.FieldMask;
import io.dipcoin.sui.bcs.BcsRegistry;
import io.dipcoin.sui.constant.WalletKeyPair;
import io.dipcoin.sui.constant.model.SuiWallet;
import io.dipcoin.sui.crypto.SuiKeyPair;
import io.dipcoin.sui.protocol.IntervalExtension;
import io.dipcoin.sui.protocol.grpc.GrpcOptions;
import io.dipcoin.sui.protocol.grpc.GrpcSuiClient;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import sui.rpc.v2.BcsOuterClass.Bcs;
import sui.rpc.v2.EventOuterClass;
import sui.rpc.v2.ExecutedTransactionOuterClass;
import sui.rpc.v2.LedgerServiceOuterClass.*;
import sui.rpc.v2.MovePackageServiceOuterClass.*;
import sui.rpc.v2.NameServiceOuterClass.LookupNameRequest;
import sui.rpc.v2.NameServiceOuterClass.LookupNameResponse;
import sui.rpc.v2.NameServiceOuterClass.ReverseLookupNameRequest;
import sui.rpc.v2.NameServiceOuterClass.ReverseLookupNameResponse;
import sui.rpc.v2.Signature.UserSignature;
import sui.rpc.v2.SignatureVerificationServiceOuterClass.VerifySignatureRequest;
import sui.rpc.v2.SignatureVerificationServiceOuterClass.VerifySignatureResponse;
import sui.rpc.v2.StateServiceOuterClass.*;
import sui.rpc.v2.SubscriptionServiceOuterClass.SubscribeCheckpointsRequest;
import sui.rpc.v2.SubscriptionServiceOuterClass.SubscribeCheckpointsResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author : Same
 * @datetime : 2026/3/3
 * @Description : Integration tests for {@link GrpcSuiClient} against the Sui Mainnet gRPC endpoint.
 *
 * <p>All tests make real network calls and require connectivity to
 * {@value GrpcOptions#DEFAULT_MAINNET_HOST}. They are tagged {@code "suite"} so that the
 * {@link IntervalExtension} inserts a 200ms cooldown between tests to avoid rate-limiting.
 *
 * <p>Write operations (execute transaction) are guarded by {@link #ENABLE_SEND} and are
 * disabled by default to prevent accidental on-chain submissions during CI.
 */
@Slf4j
@ExtendWith(IntervalExtension.class)
public class GrpcSuiClientTest {

    // --------------------- Test constants ---------------------

    /** Owner address used across read-only queries. */
    private static final String TEST_OWNER =
            "0x52d1c4be8b0d166c4e3e44fd32a8132f522e3d96f0497388d421b4795553ae4a";

    /** Owner address used across read-only queries. */
    private static final String TEST_NS_OWNER =
            "0x84eec5448c9787c1bb599955b413ea93be8932ddd2c047da795733635eec7682";

    /** Sui native coin type. */
    private static final String SUI_COIN_TYPE = "0x2::sui::SUI";

    /** Sui native coin type. */
    private static final String SUI_NATIVE_COIN_TYPE = "0x0000000000000000000000000000000000000000000000000000000000000002::sui::SUI";

    /** Sui framework package ID (always present on every network). */
    private static final String FRAMEWORK_PACKAGE_ID =
            "0x0000000000000000000000000000000000000000000000000000000000000002";

    /** A well-known immutable object ID (Sui Clock). */
    private static final String CLOCK_OBJECT_ID =
            "0x0000000000000000000000000000000000000000000000000000000000000006";

    /** Additional object IDs reused from SuiClientTest for batch calls. */
    private static final String OBJECT_ID_1 =
            "0xa7860ce29d33ca594d88251d11322358cc0b3c72d4a33821f8abe10d9d9ef8b4";
    private static final String OBJECT_ID_2 =
            "0x662be49e49772e9f7caddb6ddb414e30d5709075a292f6dd0649b206e63851b1";

    /**
     * A known mainnet transaction digest used for read-only transaction queries.
     * Replace with any valid digest found via Sui Explorer if this one expires.
     */
    private static final String KNOWN_TX_DIGEST =
            "7SHjm9ugNSAy8q2sr7A1jjCYqFzRXd1GC1Y99Qh9w5yL";

    /**
     * Guard for write operations. Set to {@code true} only in a controlled environment
     * with a funded test wallet to avoid unintended on-chain submissions.
     */
    protected static final boolean ENABLE_SEND = false;

    // --------------------- Fixtures ---------------------

    protected GrpcSuiClient grpcClient;

    @BeforeEach
    void setUp() {
//        grpcClient = GrpcSuiClient.build(
//                GrpcOptions.custom("rpc-mainnet.suiscan.xyz", 443, true)
//                        .callTimeout(Duration.ofSeconds(30))
//                        .maxRetries(2)
//                        .build());
//        grpcClient = GrpcSuiClient.build(
//                GrpcOptions.custom("sui-mainnet-endpoint.blockvision.org", 443, true)
//                        .callTimeout(Duration.ofSeconds(30))
//                        .maxRetries(2)
//                        .build());
        grpcClient = GrpcSuiClient.build(
                GrpcOptions.mainnet()
//                GrpcOptions.testnet()
                        .callTimeout(Duration.ofSeconds(30))
                        .maxRetries(2)
                        .build());
    }

    @AfterEach
    void tearDown() {
        if (grpcClient != null) {
            grpcClient.close();
        }
    }

    // =========================================================================
    // LedgerService
    // =========================================================================

    // --------------------- GetServiceInfo ---------------------

    @Test
    @Tag("suite")
    void testGetServiceInfo() {
        GetServiceInfoResponse result =
                grpcClient.getServiceInfo(GetServiceInfoRequest.getDefaultInstance());
        log.info("testGetServiceInfo chain={} epoch={} checkpoint={}",
                result.getChain(), result.getEpoch(), result.getCheckpointHeight());

        assertThat(result).isNotNull();
        assertThat(result.getChain())
                .as("Chain name should be 'mainnet'")
                .isEqualTo("mainnet");
        assertThat(result.getEpoch())
                .as("Epoch must be a positive number")
                .isGreaterThan(0L);
        assertThat(result.getCheckpointHeight())
                .as("Checkpoint height must be positive")
                .isGreaterThan(0L);
        assertThat(result.hasServer())
                .as("Server version should be present")
                .isTrue();
    }

    @Test
    @Tag("suite")
    void testGetServiceInfoAsync() throws Exception {
        CompletableFuture<GetServiceInfoResponse> future =
                grpcClient.getServiceInfoAsync(GetServiceInfoRequest.getDefaultInstance());

        GetServiceInfoResponse result = future.get(30, TimeUnit.SECONDS);
        log.info("testGetServiceInfoAsync chain={} epoch={}", result.getChain(), result.getEpoch());

        assertThat(result).isNotNull();
        assertThat(result.getChain()).isEqualTo("mainnet");
    }

    // --------------------- GetObject ---------------------

    @Test
    @Tag("suite")
    void testGetObject() {
        GetObjectRequest request = GetObjectRequest.newBuilder()
                .setObjectId(CLOCK_OBJECT_ID)
                .setReadMask(FieldMask.newBuilder()
                        .addPaths("object_id")
                        .addPaths("version")
                        .addPaths("digest")
                        .addPaths("object_type")
                        .addPaths("owner")
                        .build())
                .build();

        GetObjectResponse result = grpcClient.getObject(request);
        log.info("testGetObject object={}", result.getObject());

        assertThat(result).isNotNull();
        assertThat(result.hasObject())
                .as("Object should be present for the Clock object")
                .isTrue();
        assertThat(result.getObject().getObjectId())
                .as("Returned object ID should match the requested one")
                .isEqualTo(CLOCK_OBJECT_ID);
        assertThat(result.getObject().getVersion())
                .as("Clock version must be positive")
                .isGreaterThan(0L);
    }

    @Test
    @Tag("suite")
    void testGetObjectAsync() throws Exception {
        GetObjectRequest request = GetObjectRequest.newBuilder()
                .setObjectId(CLOCK_OBJECT_ID)
                .build();

        GetObjectResponse result = grpcClient.getObjectAsync(request).get(30, TimeUnit.SECONDS);
        log.info("testGetObjectAsync objectId={}", result.getObject().getObjectId());

        assertThat(result.hasObject()).isTrue();
        assertThat(result.getObject().getObjectId()).isEqualTo(CLOCK_OBJECT_ID);
    }

    // --------------------- BatchGetObjects ---------------------

    @Test
    @Tag("suite")
    void testBatchGetObjects() {
        GetObjectRequest req1 = GetObjectRequest.newBuilder().setObjectId(CLOCK_OBJECT_ID).build();
        GetObjectRequest req2 = GetObjectRequest.newBuilder().setObjectId(OBJECT_ID_1).build();
        GetObjectRequest req3 = GetObjectRequest.newBuilder().setObjectId(OBJECT_ID_2).build();

        BatchGetObjectsRequest batchRequest = BatchGetObjectsRequest.newBuilder()
                .addRequests(req1)
                .addRequests(req2)
                .addRequests(req3)
                .setReadMask(FieldMask.newBuilder()
                        .addPaths("object_id")
                        .addPaths("version")
                        .addPaths("digest")
                        .build())
                .build();

        BatchGetObjectsResponse result = grpcClient.batchGetObjects(batchRequest);
        log.info("testBatchGetObjects result count={}", result.getObjectsCount());

        assertThat(result).isNotNull();
        assertThat(result.getObjectsCount())
                .as("Batch response should return one result per input request")
                .isEqualTo(3);
        // Clock should always resolve successfully
        assertThat(result.getObjects(0).hasObject())
                .as("Clock object should be found")
                .isTrue();
    }

    @Test
    @Tag("suite")
    void testBatchGetObjectsAsync() throws Exception {
        BatchGetObjectsRequest batchRequest = BatchGetObjectsRequest.newBuilder()
                .addRequests(GetObjectRequest.newBuilder().setObjectId(CLOCK_OBJECT_ID).build())
                .build();

        BatchGetObjectsResponse result =
                grpcClient.batchGetObjectsAsync(batchRequest).get(30, TimeUnit.SECONDS);
        log.info("testBatchGetObjectsAsync count={}", result.getObjectsCount());

        assertThat(result.getObjectsCount()).isEqualTo(1);
    }

    // --------------------- GetTransaction ---------------------

    @Test
    @Tag("suite")
    void testGetTransaction() {
        GetTransactionRequest request = GetTransactionRequest.newBuilder()
                .setDigest(KNOWN_TX_DIGEST)
                .setReadMask(FieldMask.newBuilder()
                        .addPaths("digest")
                        .addPaths("effects")
                        .addPaths("checkpoint")
                        .build())
                .build();

        GetTransactionResponse result = grpcClient.getTransaction(request);
        log.info("testGetTransaction digest={} checkpoint={} effects={}",
                result.getTransaction().getDigest(), result.getTransaction().getCheckpoint(), result.getTransaction().getEffects());

        assertThat(result).isNotNull();
        assertThat(result.hasTransaction()).isTrue();
        assertThat(result.getTransaction().getDigest())
                .as("Returned digest must match the requested digest")
                .isEqualTo(KNOWN_TX_DIGEST);
        assertThat(result.getTransaction().getCheckpoint())
                .as("Transaction must be finalized in a checkpoint")
                .isGreaterThan(0L);
    }

    @Test
    @Tag("suite")
    void testGetTransactionAsync() throws Exception {
        GetTransactionRequest request = GetTransactionRequest.newBuilder()
                .setDigest(KNOWN_TX_DIGEST)
                .build();

        GetTransactionResponse result =
                grpcClient.getTransactionAsync(request).get(30, TimeUnit.SECONDS);
        log.info("testGetTransactionAsync digest={}", result.getTransaction().getDigest());

        assertThat(result.hasTransaction()).isTrue();
        assertThat(result.getTransaction().getDigest()).isEqualTo(KNOWN_TX_DIGEST);
    }

    // --------------------- BatchGetTransactions ---------------------

    @Test
    @Tag("suite")
    void testBatchGetTransactions() {
        BatchGetTransactionsRequest batchRequest = BatchGetTransactionsRequest.newBuilder()
                .addDigests(KNOWN_TX_DIGEST)
                .setReadMask(FieldMask.newBuilder()
                        .addPaths("digest")
                        .addPaths("checkpoint")
                        .build())
                .build();

        BatchGetTransactionsResponse result = grpcClient.batchGetTransactions(batchRequest);
        log.info("testBatchGetTransactions count={}", result.getTransactionsCount());

        assertThat(result).isNotNull();
        assertThat(result.getTransactionsCount())
                .as("Should return one result for one input digest")
                .isEqualTo(1);
        assertThat(result.getTransactions(0).hasTransaction())
                .as("Known transaction should resolve successfully")
                .isTrue();
    }

    @Test
    @Tag("suite")
    void testBatchGetTransactionsAsync() throws Exception {
        BatchGetTransactionsRequest batchRequest = BatchGetTransactionsRequest.newBuilder()
                .addDigests(KNOWN_TX_DIGEST)
                .setReadMask(FieldMask.newBuilder()
                        .addPaths("digest")
                        .addPaths("checkpoint")
                        .build())
                .build();

        BatchGetTransactionsResponse result =
                grpcClient.batchGetTransactionsAsync(batchRequest).get(30, TimeUnit.SECONDS);
        log.info("testBatchGetTransactionsAsync count={}", result.getTransactionsCount());

        assertThat(result.getTransactionsCount())
                .as("Async batch should return one result for one input digest")
                .isEqualTo(1);
        assertThat(result.getTransactions(0).hasTransaction()).isTrue();
    }

    // --------------------- GetCheckpoint ---------------------

    @Test
    @Tag("suite")
    void testGetLatestCheckpoint() {
        // Omit both sequence_number and digest to get the latest checkpoint
        GetCheckpointRequest request = GetCheckpointRequest.newBuilder()
                .setReadMask(FieldMask.newBuilder()
                        .addPaths("sequence_number")
                        .addPaths("digest")
                        .addPaths("timestamp")
                        .build())
                .build();

        GetCheckpointResponse result = grpcClient.getCheckpoint(request);
        log.info("testGetLatestCheckpoint seq={} digest={}",
                result.getCheckpoint().getSequenceNumber(),
                result.getCheckpoint().getDigest());

        assertThat(result).isNotNull();
        assertThat(result.hasCheckpoint()).isTrue();
        assertThat(result.getCheckpoint().getSequenceNumber())
                .as("Latest checkpoint sequence number must be positive")
                .isGreaterThan(0L);
        assertThat(result.getCheckpoint().getDigest())
                .as("Checkpoint digest must not be blank")
                .isNotBlank();
    }

    @Test
    @Tag("suite")
    void testGetCheckpointBySequenceNumber() {
        // First get the latest checkpoint sequence number to use as reference
        GetCheckpointResponse latest = grpcClient.getCheckpoint(
                GetCheckpointRequest.newBuilder()
                        .setReadMask(FieldMask.newBuilder().addPaths("sequence_number").build())
                        .build());
        long latestSeq = latest.getCheckpoint().getSequenceNumber();

        // Then request a slightly older checkpoint by explicit sequence number
        long targetSeq = Math.max(0L, latestSeq - 10);
        GetCheckpointRequest request = GetCheckpointRequest.newBuilder()
                .setSequenceNumber(targetSeq)
                .setReadMask(FieldMask.newBuilder()
                        .addPaths("sequence_number")
                        .addPaths("digest")
                        .build())
                .build();

        GetCheckpointResponse result = grpcClient.getCheckpoint(request);
        log.info("testGetCheckpointBySequenceNumber seq={} digest={}", result.getCheckpoint().getSequenceNumber(), result.getCheckpoint().getDigest());

        assertThat(result.hasCheckpoint()).isTrue();
        assertThat(result.getCheckpoint().getSequenceNumber())
                .as("Returned sequence number must match the requested one")
                .isEqualTo(targetSeq);
    }

    @Test
    @Tag("suite")
    void testGetCheckpointAsync() throws Exception {
        GetCheckpointResponse result =
                grpcClient.getCheckpointAsync(GetCheckpointRequest.getDefaultInstance())
                        .get(30, TimeUnit.SECONDS);
        log.info("testGetCheckpointAsync seq={}", result.getCheckpoint().getSequenceNumber());

        assertThat(result.hasCheckpoint()).isTrue();
        assertThat(result.getCheckpoint().getSequenceNumber()).isGreaterThan(0L);
    }

    // --------------------- GetEpoch ---------------------

    @Test
    @Tag("suite")
    void testGetCurrentEpoch() {
        GetEpochRequest request = GetEpochRequest.getDefaultInstance();

        GetEpochResponse result = grpcClient.getEpoch(request);
        log.info("testGetCurrentEpoch epoch={} gasPrice={}",
                result.getEpoch().getEpoch(), result.getEpoch().getReferenceGasPrice());

        assertThat(result).isNotNull();
        assertThat(result.hasEpoch()).isTrue();
        assertThat(result.getEpoch().getEpoch())
                .as("Current epoch number must be positive")
                .isGreaterThan(0L);
        assertThat(result.getEpoch().getReferenceGasPrice())
                .as("Reference gas price must be positive")
                .isGreaterThan(0L);
    }

    @Test
    @Tag("suite")
    void testGetEpochAsync() throws Exception {
        GetEpochResponse result =
                grpcClient.getEpochAsync(GetEpochRequest.getDefaultInstance())
                        .get(30, TimeUnit.SECONDS);
        log.info("testGetEpochAsync epoch={} gasPrice={}",
                result.getEpoch().getEpoch(), result.getEpoch().getReferenceGasPrice());

        assertThat(result.hasEpoch()).isTrue();
        assertThat(result.getEpoch().getEpoch()).isGreaterThan(0L);
    }

    // =========================================================================
    // StateService
    // =========================================================================

    // --------------------- ListDynamicFields ---------------------

    @Test
    @Tag("suite")
    void testListDynamicFields() {
        // 0x5 is the SuiSystemState object which always has dynamic fields
        ListDynamicFieldsRequest request = ListDynamicFieldsRequest.newBuilder()
                .setParent("0x0000000000000000000000000000000000000000000000000000000000000005")
                .build();

        ListDynamicFieldsResponse result = grpcClient.listDynamicFields(request);
        log.info("testListDynamicFields count={}", result.getDynamicFieldsCount());

        assertThat(result).isNotNull();
        assertThat(result.getDynamicFieldsCount())
                .as("SuiSystemState should have at least one dynamic field")
                .isGreaterThan(0);
    }

    @Test
    @Tag("suite")
    void testListDynamicFieldsAsync() throws Exception {
        ListDynamicFieldsRequest request = ListDynamicFieldsRequest.newBuilder()
                .setParent("0x0000000000000000000000000000000000000000000000000000000000000005")
                .build();

        ListDynamicFieldsResponse result =
                grpcClient.listDynamicFieldsAsync(request).get(30, TimeUnit.SECONDS);
        log.info("testListDynamicFieldsAsync count={}", result.getDynamicFieldsCount());

        assertThat(result.getDynamicFieldsCount()).isGreaterThan(0);
    }

    // --------------------- ListOwnedObjects ---------------------

    @Test
    @Tag("suite")
    void testListOwnedObjects() {
        ListOwnedObjectsRequest request = ListOwnedObjectsRequest.newBuilder()
//                .setOwner(TEST_OWNER)
                .setOwner("0x52d1c4be8b0d166c4e3e44fd32a8132f522e3d96f0497388d421b4795553ae4a")
//                .setObjectType("0x2::coin::Coin<0x2::sui::SUI>, 0x978fed071cca22dd26bec3cf4a5d5a00ab10f39cb8c659bbfdfbec4397241001::roles::FundingRateCap")
                .setReadMask(FieldMask.newBuilder()
                        .addPaths("object_id")
                        .addPaths("version")
                        .addPaths("object_type")
                        .addPaths("owner")
                        .addPaths("contents")
                        .build())
                .build();

        ListOwnedObjectsResponse result = grpcClient.listOwnedObjects(request);
        log.info("testListOwnedObjects owner={} count={} objects={}",
                TEST_OWNER, result.getObjectsCount(), result.getObjectsList());

        assertThat(result).isNotNull();
        assertThat(result.getObjectsList())
                .as("Test owner should own at least one object")
                .isNotEmpty();
    }

    @Test
    @Tag("suite")
    void testListOwnedObjectsAsync() throws Exception {
        ListOwnedObjectsRequest request = ListOwnedObjectsRequest.newBuilder()
                .setOwner(TEST_OWNER)
                .setReadMask(FieldMask.newBuilder()
                        .addPaths("object_id")
                        .addPaths("version")
                        .addPaths("object_type")
                        .build())
                .build();

        ListOwnedObjectsResponse result =
                grpcClient.listOwnedObjectsAsync(request).get(30, TimeUnit.SECONDS);
        log.info("testListOwnedObjectsAsync owner={} count={} objects={}",
                TEST_OWNER, result.getObjectsCount(), result.getObjectsList());

        assertThat(result.getObjectsList()).isNotEmpty();
    }

    // --------------------- GetCoinInfo ---------------------

    @Test
    @Tag("suite")
    void testGetCoinInfo() {
        GetCoinInfoRequest request = GetCoinInfoRequest.newBuilder()
                .setCoinType(SUI_COIN_TYPE)
                .build();

        GetCoinInfoResponse result = grpcClient.getCoinInfo(request);
        log.info("testGetCoinInfo coinType={} name={}",
                result.getCoinType(),
                result.hasMetadata() ? result.getMetadata().getName() : "N/A");

        assertThat(result).isNotNull();
        assertThat(result.getCoinType())
                .as("Returned coin type should match the request")
                .isIn(List.of(SUI_COIN_TYPE, SUI_NATIVE_COIN_TYPE));
    }

    @Test
    @Tag("suite")
    void testGetCoinInfoAsync() throws Exception {
        GetCoinInfoRequest request = GetCoinInfoRequest.newBuilder()
                .setCoinType(SUI_COIN_TYPE)
                .build();

        GetCoinInfoResponse result =
                grpcClient.getCoinInfoAsync(request).get(30, TimeUnit.SECONDS);
        log.info("testGetCoinInfoAsync coinType={} name={}", result.getCoinType(),
                result.hasMetadata() ? result.getMetadata().getName() : "N/A");

        assertThat(result.getCoinType())
                .isIn(List.of(SUI_COIN_TYPE, SUI_NATIVE_COIN_TYPE));
    }

    // --------------------- GetBalance ---------------------

    @Test
    @Tag("suite")
    void testGetBalance() {
        GetBalanceRequest request = GetBalanceRequest.newBuilder()
                .setOwner(TEST_OWNER)
                .setCoinType(SUI_COIN_TYPE)
                .build();

        GetBalanceResponse result = grpcClient.getBalance(request);
        log.info("testGetBalance owner={} coinType={} balance={}",
                TEST_OWNER, SUI_COIN_TYPE,
                result.hasBalance() ? result.getBalance().getBalance() : "N/A");

        assertThat(result).isNotNull();
        assertThat(result.hasBalance())
                .as("Balance response should be present for an account that holds SUI")
                .isTrue();
        assertThat(result.getBalance().getCoinType())
                .as("Balance coin type should match the request")
                .isIn(List.of(SUI_COIN_TYPE, SUI_NATIVE_COIN_TYPE));
    }

    @Test
    @Tag("suite")
    void testGetBalanceAsync() throws Exception {
        GetBalanceRequest request = GetBalanceRequest.newBuilder()
                .setOwner(TEST_OWNER)
                .setCoinType(SUI_COIN_TYPE)
                .build();

        GetBalanceResponse result =
                grpcClient.getBalanceAsync(request).get(30, TimeUnit.SECONDS);
        log.info("testGetBalanceAsync balance={}",
                result.hasBalance() ? result.getBalance().getBalance() : "N/A");

        assertThat(result.hasBalance()).isTrue();
    }

    // --------------------- ListBalances ---------------------

    @Test
    @Tag("suite")
    void testListBalances() {
        ListBalancesRequest request = ListBalancesRequest.newBuilder()
                .setOwner(TEST_OWNER)
                .build();

        ListBalancesResponse result = grpcClient.listBalances(request);
        log.info("testListBalances owner={} coinTypeCount={}",
                TEST_OWNER, result.getBalancesCount());

        assertThat(result).isNotNull();
        assertThat(result.getBalancesList())
                .as("Test owner should hold at least one coin type (SUI)")
                .isNotEmpty();

        // Verify at least one entry corresponds to SUI
        boolean hasSui = result.getBalancesList().stream()
                .anyMatch(b -> SUI_NATIVE_COIN_TYPE.equals(b.getCoinType()));
        assertThat(hasSui)
                .as("Owner should have a SUI balance entry")
                .isTrue();
    }

    @Test
    @Tag("suite")
    void testListBalancesAsync() throws Exception {
        ListBalancesRequest request = ListBalancesRequest.newBuilder()
                .setOwner(TEST_OWNER)
                .build();

        ListBalancesResponse result =
                grpcClient.listBalancesAsync(request).get(30, TimeUnit.SECONDS);
        log.info("testListBalancesAsync count={}", result.getBalancesCount());

        assertThat(result.getBalancesList()).isNotEmpty();
    }

    // =========================================================================
    // MovePackageService
    // =========================================================================

    // --------------------- GetPackage ---------------------

    @Test
    @Tag("suite")
    void testGetPackage() {
        GetPackageRequest request = GetPackageRequest.newBuilder()
                .setPackageId(FRAMEWORK_PACKAGE_ID)
                .build();

        GetPackageResponse result = grpcClient.getPackage(request);
        log.info("testGetPackage packageId={} modules={}",
                FRAMEWORK_PACKAGE_ID,
                result.hasPackage() ? result.getPackage().getModulesCount() : 0);

        assertThat(result).isNotNull();
        assertThat(result.hasPackage())
                .as("Framework package should always exist")
                .isTrue();
        assertThat(result.getPackage().getModulesCount())
                .as("Framework package must contain at least one module")
                .isGreaterThan(0);
    }

    @Test
    @Tag("suite")
    void testGetPackageAsync() throws Exception {
        GetPackageRequest request = GetPackageRequest.newBuilder()
                .setPackageId(FRAMEWORK_PACKAGE_ID)
                .build();

        GetPackageResponse result =
                grpcClient.getPackageAsync(request).get(30, TimeUnit.SECONDS);
        log.info("testGetPackageAsync modules={}", result.getPackage().getModulesCount());

        assertThat(result.hasPackage()).isTrue();
        assertThat(result.getPackage().getModulesCount()).isGreaterThan(0);
    }

    // --------------------- GetDatatype ---------------------

    @Test
    @Tag("suite")
    void testGetDatatype() {
        // 0x2::coin::Coin is a well-known struct in the framework package
        GetDatatypeRequest request = GetDatatypeRequest.newBuilder()
                .setPackageId(FRAMEWORK_PACKAGE_ID)
                .setModuleName("coin")
                .setName("Coin")
                .build();

        GetDatatypeResponse result = grpcClient.getDatatype(request);
        log.info("testGetDatatype package={} module=coin name=Coin",
                FRAMEWORK_PACKAGE_ID);

        assertThat(result).isNotNull();
        assertThat(result.hasDatatype())
                .as("Coin struct should exist in the framework package")
                .isTrue();
        assertThat(result.getDatatype().getName())
                .as("Returned datatype name should be 'Coin'")
                .isEqualTo("Coin");
    }

    @Test
    @Tag("suite")
    void testGetDatatypeAsync() throws Exception {
        GetDatatypeRequest request = GetDatatypeRequest.newBuilder()
                .setPackageId(FRAMEWORK_PACKAGE_ID)
                .setModuleName("coin")
                .setName("Coin")
                .build();

        GetDatatypeResponse result =
                grpcClient.getDatatypeAsync(request).get(30, TimeUnit.SECONDS);
        log.info("testGetDatatypeAsync name={}", result.getDatatype().getName());

        assertThat(result.hasDatatype()).isTrue();
        assertThat(result.getDatatype().getName()).isEqualTo("Coin");
    }

    // --------------------- GetFunction ---------------------

    @Test
    @Tag("suite")
    void testGetFunction() {
        // 0x2::coin::split is a well-known public function
        GetFunctionRequest request = GetFunctionRequest.newBuilder()
                .setPackageId(FRAMEWORK_PACKAGE_ID)
                .setModuleName("coin")
                .setName("split")
                .build();

        GetFunctionResponse result = grpcClient.getFunction(request);
        log.info("testGetFunction package={} module=coin function=split param={}", FRAMEWORK_PACKAGE_ID, result.getFunction());

        assertThat(result).isNotNull();
        assertThat(result.hasFunction())
                .as("coin::split should exist in the framework package")
                .isTrue();
        assertThat(result.getFunction().getName())
                .as("Returned function name should be 'split'")
                .isEqualTo("split");
    }

    @Test
    @Tag("suite")
    void testGetFunctionAsync() throws Exception {
        GetFunctionRequest request = GetFunctionRequest.newBuilder()
                .setPackageId(FRAMEWORK_PACKAGE_ID)
                .setModuleName("coin")
                .setName("split")
                .build();

        GetFunctionResponse result =
                grpcClient.getFunctionAsync(request).get(30, TimeUnit.SECONDS);
        log.info("testGetFunctionAsync package={} module=coin function=split param={}", FRAMEWORK_PACKAGE_ID, result.getFunction());

        assertThat(result.hasFunction()).isTrue();
        assertThat(result.getFunction().getName()).isEqualTo("split");
    }

    // --------------------- ListPackageVersions ---------------------

    @Test
    @Tag("suite")
    void testListPackageVersions() {
        ListPackageVersionsRequest request = ListPackageVersionsRequest.newBuilder()
                .setPackageId(FRAMEWORK_PACKAGE_ID)
                .build();

        ListPackageVersionsResponse result = grpcClient.listPackageVersions(request);
        log.info("testListPackageVersions count={} list={}", result.getVersionsCount(), result.getVersionsList());

        assertThat(result).isNotNull();
        assertThat(result.getVersionsCount())
                .as("Framework package should have at least one version")
                .isGreaterThan(0);
    }

    @Test
    @Tag("suite")
    void testListPackageVersionsAsync() throws Exception {
        ListPackageVersionsRequest request = ListPackageVersionsRequest.newBuilder()
                .setPackageId(FRAMEWORK_PACKAGE_ID)
                .build();

        ListPackageVersionsResponse result =
                grpcClient.listPackageVersionsAsync(request).get(30, TimeUnit.SECONDS);
        log.info("testListPackageVersionsAsync count={} list={}", result.getVersionsCount(), result.getVersionsList());

        assertThat(result.getVersionsCount()).isGreaterThan(0);
    }

    // =========================================================================
    // NameService
    // =========================================================================

    // --------------------- LookupName ---------------------

    @Test
    @Tag("suite")
    void testLookupName() {
        // "sui" is a well-known SuiNS name on mainnet
        LookupNameRequest request = LookupNameRequest.newBuilder()
                .setName("@clevermanalex")
                .build();

        LookupNameResponse result = grpcClient.lookupName(request);
        log.info("testLookupName name=@sui hasRecord={}", result.hasRecord());

        // The record may or may not exist; we just verify the call completes
        assertThat(result).isNotNull();
    }

    @Test
    @Tag("suite")
    void testLookupNameAsync() throws Exception {
        LookupNameRequest request = LookupNameRequest.newBuilder()
                .setName("@clevermanalex")
                .build();

        LookupNameResponse result =
                grpcClient.lookupNameAsync(request).get(30, TimeUnit.SECONDS);
        log.info("testLookupNameAsync hasRecord={}", result.hasRecord());

        assertThat(result).isNotNull();
    }

    // --------------------- ReverseLookupName ---------------------

    @Test
    @Tag("suite")
    void testReverseLookupName() {
        ReverseLookupNameRequest request = ReverseLookupNameRequest.newBuilder()
                .setAddress(TEST_NS_OWNER)
                .build();

        ReverseLookupNameResponse result = grpcClient.reverseLookupName(request);
        log.info("testReverseLookupName address={} hasRecord={} result={}", TEST_NS_OWNER, result.hasRecord(), result);

        // Address may or may not have a default SuiNS name; call should succeed
        assertThat(result).isNotNull();
    }

    @Test
    @Tag("suite")
    void testReverseLookupNameAsync() throws Exception {
        ReverseLookupNameRequest request = ReverseLookupNameRequest.newBuilder()
                .setAddress(TEST_NS_OWNER)
                .build();

        ReverseLookupNameResponse result =
                grpcClient.reverseLookupNameAsync(request).get(30, TimeUnit.SECONDS);
        log.info("testReverseLookupNameAsync address={} hasRecord={} result={}", TEST_NS_OWNER, result.hasRecord(), result);

        assertThat(result).isNotNull();
    }

    // =========================================================================
    // SuiSignatureVerificationService
    // =========================================================================

    // --------------------- VerifySignature ---------------------

    /**
     * Helper: builds a correctly BCS-encoded PersonalMessage {@link Bcs} for the given raw bytes.
     * The BCS format for {@code PersonalMessage} is {@code Vec<u8>}: ULEB128(length) + raw bytes.
     */
    private static Bcs bcsPersonalMessage(byte[] rawMsg) throws IOException {
        byte[] bcsValue = BcsRegistry.serializeToBytes(rawMsg, BcsRegistry.BYTE_ARRAY_SERIALIZER);
        return Bcs.newBuilder()
                .setName("PersonalMessage")
                .setValue(ByteString.copyFrom(bcsValue))
                .build();
    }

    /**
     * Helper: builds a {@link UserSignature} using the canonical Sui BCS format
     * (flag || signature || pubkey) via the {@code bcs} field.
     */
    private static UserSignature bcsUserSignature(byte[] flagSigPk) {
        return UserSignature.newBuilder()
                .setBcs(Bcs.newBuilder()
                        .setValue(ByteString.copyFrom(flagSigPk))
                        .build())
                .build();
    }

    @Test
    @Tag("suite")
    void testVerifySignatureValid() throws Exception {
        byte[] msgBytes = "hello sui".getBytes(StandardCharsets.UTF_8);

        SuiWallet maker = WalletKeyPair.WALLETS.get(WalletKeyPair.OPERATOR);
        SuiKeyPair<?> keyPair = maker.getKeyPair();

        // signPersonalMessage returns: flag(1) || signature(64) || pubkey(32)
        byte[] signedBytes = keyPair.signPersonalMessage(msgBytes);

        VerifySignatureRequest request = VerifySignatureRequest.newBuilder()
                .setMessage(bcsPersonalMessage(msgBytes))
                .setSignature(bcsUserSignature(signedBytes))
                .build();

        VerifySignatureResponse result = grpcClient.verifySignature(request);
        log.info("testVerifySignatureValid isValid={} reason={}",
                result.getIsValid(), result.getReason());

        assertThat(result).isNotNull();
        assertThat(result.getIsValid())
                .as("A correctly signed personal message must pass verification")
                .isTrue();
    }

    @Test
    @Tag("suite")
    void testVerifySignatureValidAsync() throws Exception {
        byte[] msgBytes = "hello sui async".getBytes(StandardCharsets.UTF_8);

        SuiWallet maker = WalletKeyPair.WALLETS.get(WalletKeyPair.OPERATOR);
        SuiKeyPair<?> keyPair = maker.getKeyPair();

        byte[] signedBytes = keyPair.signPersonalMessage(msgBytes);

        VerifySignatureRequest request = VerifySignatureRequest.newBuilder()
                .setMessage(bcsPersonalMessage(msgBytes))
                .setSignature(bcsUserSignature(signedBytes))
                .build();

        VerifySignatureResponse result =
                grpcClient.verifySignatureAsync(request).get(30, TimeUnit.SECONDS);
        log.info("testVerifySignatureValidAsync isValid={} reason={}",
                result.getIsValid(), result.getReason());

        assertThat(result).isNotNull();
        assertThat(result.getIsValid())
                .as("Async: a correctly signed personal message must pass verification")
                .isTrue();
    }

    @Test
    @Tag("suite")
    void testVerifySignatureWithInvalidSignature() throws Exception {
        // Sign message "A" but verify against BCS of message "B" — cryptographic mismatch
        byte[] signedMsg = "message A".getBytes(StandardCharsets.UTF_8);
        byte[] verifiedMsg = "message B".getBytes(StandardCharsets.UTF_8);

        SuiWallet maker = WalletKeyPair.WALLETS.get(WalletKeyPair.OPERATOR);
        SuiKeyPair<?> keyPair = maker.getKeyPair();

        byte[] signedBytes = keyPair.signPersonalMessage(signedMsg);

        VerifySignatureRequest request = VerifySignatureRequest.newBuilder()
                .setMessage(bcsPersonalMessage(verifiedMsg))
                .setSignature(bcsUserSignature(signedBytes))
                .build();

        VerifySignatureResponse result = grpcClient.verifySignature(request);
        log.info("testVerifySignatureWithInvalidSignature isValid={} reason={}",
                result.getIsValid(), result.getReason());

        assertThat(result).isNotNull();
        assertThat(result.getIsValid())
                .as("Signature over a different message must fail verification")
                .isFalse();
        assertThat(result.getReason())
                .as("Server should provide a failure reason for a mismatched signature")
                .isNotBlank();
    }

    @Test
    @Tag("suite")
    void testVerifySignatureWithAddressMismatch() throws Exception {
        byte[] msgBytes = "address mismatch test".getBytes(StandardCharsets.UTF_8);

        SuiWallet maker = WalletKeyPair.WALLETS.get(WalletKeyPair.OPERATOR);
        SuiKeyPair<?> keyPair = maker.getKeyPair();

        byte[] signedBytes = keyPair.signPersonalMessage(msgBytes);

        // Supply a completely unrelated address — the address derived from the
        // signature's public key will not match TEST_OWNER.
        VerifySignatureRequest request = VerifySignatureRequest.newBuilder()
                .setMessage(bcsPersonalMessage(msgBytes))
                .setSignature(bcsUserSignature(signedBytes))
                .setAddress(TEST_OWNER)
                .build();

        VerifySignatureResponse result = grpcClient.verifySignature(request);
        log.info("testVerifySignatureWithAddressMismatch isValid={} reason={}",
                result.getIsValid(), result.getReason());

        assertThat(result).isNotNull();
        assertThat(result.getIsValid())
                .as("Signature with mismatched address should fail verification")
                .isFalse();
    }

    // =========================================================================
    // SubscriptionService
    // =========================================================================

    @Test
    @Tag("suite")
    void testSubscribeCheckpoints() throws InterruptedException {
        // Receive exactly 3 checkpoints then verify ordering and completeness
        int targetCount = 3;
        CountDownLatch latch = new CountDownLatch(targetCount);
        AtomicInteger received = new AtomicInteger(0);
        AtomicReference<Throwable> streamError = new AtomicReference<>();
        AtomicReference<Long> prevCursor = new AtomicReference<>(-1L);

        SubscribeCheckpointsRequest request = SubscribeCheckpointsRequest.newBuilder()
                .setReadMask(FieldMask.newBuilder()
                        .addPaths("cursor")
                        .addPaths("checkpoint.sequence_number")
                        .addPaths("checkpoint.digest")
                        .build())
                .build();

        grpcClient.subscribeCheckpoints(request, new StreamObserver<>() {
            @Override
            public void onNext(SubscribeCheckpointsResponse response) {
                log.info(" >>>  response = {}", response);
                long cursor = response.getCursor();
                long prev = prevCursor.getAndSet(cursor);
                int count = received.incrementAndGet();
                log.info("testSubscribeCheckpoints checkpoint #{} cursor={}", count, cursor);

                // Checkpoints must arrive in strictly ascending order
                assertThat(cursor)
                        .as("Checkpoint cursor must be strictly increasing")
                        .isGreaterThan(prev);

                latch.countDown();
            }

            @Override
            public void onError(Throwable t) {
                log.warn("testSubscribeCheckpoints stream error: {}", t.getMessage());
                streamError.set(t);
                // Drain the latch to unblock the test
                while (latch.getCount() > 0) latch.countDown();
            }

            @Override
            public void onCompleted() {
                log.info("testSubscribeCheckpoints stream completed");
            }
        });

        // Wait up to 60 seconds for 3 checkpoints (~3-10s per checkpoint on mainnet)
        boolean completed = latch.await(60, TimeUnit.SECONDS);

        assertThat(streamError.get())
                .as("Checkpoint stream must not emit an error")
                .isNull();
        assertThat(completed)
                .as("Should receive at least " + targetCount + " checkpoints within 60s")
                .isTrue();
        assertThat(received.get())
                .as("Received count must be at least " + targetCount)
                .isGreaterThanOrEqualTo(targetCount);
    }

    /**
     * Subscribe to the checkpoint stream and scan for events emitted by a specific Move module.
     *
     * <p>Approach: {@code SubscribeCheckpoints} returns full {@link ExecutedTransactionOuterClass.ExecutedTransaction} objects
     * within each checkpoint when the {@code read_mask} includes {@code checkpoint.transactions}.
     * Each {@link ExecutedTransactionOuterClass.ExecutedTransaction} may carry {@link EventOuterClass.TransactionEvents} containing the
     * per-event metadata ({@code package_id}, {@code module}, {@code event_type}, {@code json}).
     *
     * <p>This test filters for events from
     * {@code 0x1191d09ee2b51346d8ba641ce924faed59a657182dac760112e0cb758600afa2::distributor}
     * on mainnet. Since the contract may be inactive during the test window, the test does
     * <b>not</b> assert that matching events are found — it only verifies the stream operates
     * correctly and logs any matches.
     */
    @Test
    @Tag("suite")
    void testSubscribeEvent() throws InterruptedException {
        String targetPackageId =
                "0x5306f64e312b581766351c07af79c72fcb1cd25147157fdc2f8ad76de9a3fb6a";
        String targetModule = "vaa";

        int maxCheckpoints = 5000;
        CountDownLatch latch = new CountDownLatch(maxCheckpoints);
        AtomicInteger checkpointCount = new AtomicInteger(0);
        AtomicInteger totalEventCount = new AtomicInteger(0);
        AtomicInteger matchedEventCount = new AtomicInteger(0);
        AtomicReference<Throwable> streamError = new AtomicReference<>();

        SubscribeCheckpointsRequest request = SubscribeCheckpointsRequest.newBuilder()
                .setReadMask(FieldMask.newBuilder()
                        .addPaths("cursor")
                        .addPaths("checkpoint.sequence_number")
                        .addPaths("checkpoint.transactions")
//                        .addPaths("checkpoint.transactions.digest")
//                        .addPaths("checkpoint.transactions.events")
                        .build())
                .build();

        grpcClient.subscribeCheckpoints(request, new StreamObserver<>() {
            @Override
            public void onNext(SubscribeCheckpointsResponse response) {
                int cpIdx = checkpointCount.incrementAndGet();
                log.info(" >>> response = {}", response);

                if (cpIdx == 1) {
                    log.info("testSubscribeEvent [DIAG] first response: cursor={} " +
                                    "hasCheckpoint={} hasSeqNum={} seqNum={} txCount={} unknownFields={}",
                            response.getCursor(),
                            response.hasCheckpoint(),
                            response.getCheckpoint().hasSequenceNumber(),
                            response.getCheckpoint().hasSequenceNumber()
                                    ? response.getCheckpoint().getSequenceNumber() : "N/A",
                            response.getCheckpoint().getTransactionsCount(),
                            response.getCheckpoint().getUnknownFields());
                }

                long seqNum = response.getCheckpoint().getSequenceNumber();

                for (ExecutedTransactionOuterClass.ExecutedTransaction tx : response.getCheckpoint().getTransactionsList()) {
                    if (!tx.hasEvents()) {
                        continue;
                    }
                    for (EventOuterClass.Event event : tx.getEvents().getEventsList()) {
                        totalEventCount.incrementAndGet();

                        if (targetPackageId.equals(event.getPackageId())
                                && targetModule.equals(event.getModule())) {
                            int matched = matchedEventCount.incrementAndGet();
                            log.info("testSubscribeEvent MATCH #{} | checkpoint={} tx={} " +
                                            "type={} sender={} json={}",
                                    matched, seqNum, tx.getDigest(),
                                    event.getEventType(), event.getSender(),
                                    event.hasJson() ? event.getJson() : "N/A");
                        }
                    }
                }

                if (cpIdx % 10 == 0) {
                    log.info("testSubscribeEvent progress: checkpoints={} totalEvents={} matched={}",
                            cpIdx, totalEventCount.get(), matchedEventCount.get());
                }

                latch.countDown();
            }

            @Override
            public void onError(Throwable t) {
                log.warn("testSubscribeEvent stream error: {}", t.getMessage());
                streamError.set(t);
                while (latch.getCount() > 0) {
                    latch.countDown();
                }
            }

            @Override
            public void onCompleted() {
                log.info("testSubscribeEvent stream completed");
            }
        });

        boolean completed = latch.await(180, TimeUnit.SECONDS);

        log.info("testSubscribeEvent finished: checkpoints={} totalEvents={} matched={} completed={}",
                checkpointCount.get(), totalEventCount.get(), matchedEventCount.get(), completed);

        assertThat(streamError.get())
                .as("Event subscription stream must not emit an error")
                .isNull();
        assertThat(completed)
                .as("Should scan at least %d checkpoints within 180s", maxCheckpoints)
                .isTrue();
        assertThat(checkpointCount.get())
                .as("Scanned checkpoint count")
                .isGreaterThanOrEqualTo(maxCheckpoints);
    }

    // =========================================================================
    // TransactionExecutionService
    // =========================================================================

    /**
     * Execute a real transaction.
     * Guarded by {@link #ENABLE_SEND}; disabled by default to prevent unintended on-chain writes.
     * Enable by setting {@code ENABLE_SEND = true} and providing a valid signed transaction.
     */
    @Test
    @Tag("suite")
    void testExecuteTransaction() {
        if (!ENABLE_SEND) {
            log.info("testExecuteTransaction skipped (ENABLE_SEND=false)");
            return;
        }
        // Provide valid BCS transaction bytes and signature(s) to test real execution.
        // See SuiClientTest.testBatchTransaction for reference on building transaction bytes.
        log.warn("testExecuteTransaction: ENABLE_SEND=true but no test transaction provided — skipping");
    }

    /**
     * Simulate a transaction.
     * Requires a valid BCS-serialized {@code Transaction}.
     * Guarded similarly to execute; enable only with a properly constructed transaction.
     */
    @Test
    @Tag("suite")
    void testSimulateTransaction() {
        if (!ENABLE_SEND) {
            log.info("testSimulateTransaction skipped (ENABLE_SEND=false)");
            return;
        }
        log.warn("testSimulateTransaction: ENABLE_SEND=true but no test transaction provided — skipping");
    }

    // =========================================================================
    // Cross-cutting: verify async and sync return consistent results
    // =========================================================================

    @Test
    @Tag("suite")
    void testSyncAndAsyncReturnConsistentResults() throws Exception {
        GetServiceInfoRequest req = GetServiceInfoRequest.getDefaultInstance();

        GetServiceInfoResponse sync  = grpcClient.getServiceInfo(req);
        GetServiceInfoResponse async = grpcClient.getServiceInfoAsync(req).get(30, TimeUnit.SECONDS);

        log.info("testSyncAndAsyncReturnConsistentResults syncChain={} asyncChain={}",
                sync.getChain(), async.getChain());

        assertThat(sync.getChain())
                .as("Sync and async must return the same chain name")
                .isEqualTo(async.getChain());
        assertThat(sync.getEpoch())
                .as("Epoch returned by sync and async should be equal or async ≥ sync")
                .isLessThanOrEqualTo(async.getEpoch());
    }

    // =========================================================================
    // GrpcOptions builder verification
    // =========================================================================

    @Test
    @Tag("suite")
    void testCustomGrpcOptions() {
        GrpcOptions options = GrpcOptions.mainnet()
                .callTimeout(Duration.ofSeconds(15))
                .maxRetries(1)
                .keepAliveTime(Duration.ofSeconds(20))
                .keepAliveTimeout(Duration.ofSeconds(5))
                .keepAliveWithoutCalls(true)
                .maxInboundMessageSize(10 * 1024 * 1024)
                .build();

        assertThat(options.getCallTimeout()).isEqualTo(Duration.ofSeconds(15));
        assertThat(options.getMaxRetries()).isEqualTo(1);
        assertThat(options.getKeepAliveTime()).isEqualTo(Duration.ofSeconds(20));
        assertThat(options.isKeepAliveWithoutCalls()).isTrue();
        assertThat(options.getMaxInboundMessageSize()).isEqualTo(10 * 1024 * 1024);
        assertThat(options.getHost()).isEqualTo(GrpcOptions.DEFAULT_MAINNET_HOST);
        assertThat(options.isUseTls()).isTrue();

        // Verify the client can be built and used with these options
        try (GrpcSuiClient customClient = GrpcSuiClient.build(options)) {
            GetServiceInfoResponse info =
                    customClient.getServiceInfo(GetServiceInfoRequest.getDefaultInstance());
            assertThat(info.getChain()).isEqualTo("mainnet");
            log.info("testCustomGrpcOptions chain={}", info.getChain());
        }
    }

    @Test
    @Tag("suite")
    void testMetadataHeadersInOptions() {
        GrpcOptions options = GrpcOptions.mainnet()
                .metadata("x-custom-header", "test-value")
                .build();

        assertThat(options.getMetadata())
                .as("Custom metadata should be retained in options")
                .containsEntry("x-custom-header", "test-value");

        // The metadata map returned must be unmodifiable
        assertThat(options.getMetadata())
                .isUnmodifiable();
    }
    
}
