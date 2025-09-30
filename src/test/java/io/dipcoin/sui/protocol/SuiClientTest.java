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

package io.dipcoin.sui.protocol;

import io.dipcoin.sui.client.TransactionBuilder;
import io.dipcoin.sui.constant.PerpPackage;
import io.dipcoin.sui.constant.TxConfig;
import io.dipcoin.sui.constant.WalletKeyPair;
import io.dipcoin.sui.constant.model.SuiWallet;
import io.dipcoin.sui.constant.perp.PackageConfig;
import io.dipcoin.sui.crypto.Ed25519KeyPair;
import io.dipcoin.sui.crypto.SuiKeyPair;
import io.dipcoin.sui.crypto.signature.SignatureScheme;
import io.dipcoin.sui.model.Request;
import io.dipcoin.sui.model.extended.DynamicFieldName;
import io.dipcoin.sui.model.filter.MoveModuleFilter;
import io.dipcoin.sui.model.filter.SuiObjectDataFilter;
import io.dipcoin.sui.model.governance.SuiSystemStateSummary;
import io.dipcoin.sui.model.move.SuiMoveNormalizedFunction;
import io.dipcoin.sui.model.move.SuiMoveNormalizedModule;
import io.dipcoin.sui.model.move.SuiMoveNormalizedStruct;
import io.dipcoin.sui.model.move.kind.MoveFunctionArgType;
import io.dipcoin.sui.model.move.kind.MoveValue;
import io.dipcoin.sui.model.move.kind.data.MoveObject;
import io.dipcoin.sui.model.move.kind.struct.MoveStructMap;
import io.dipcoin.sui.model.move.kind.struct.MoveStructObject;
import io.dipcoin.sui.model.object.ObjectDataOptions;
import io.dipcoin.sui.model.object.ObjectResponseQuery;
import io.dipcoin.sui.model.object.PageForSuiObjectResponseAndObjectId;
import io.dipcoin.sui.model.object.SuiObjectResponse;
import io.dipcoin.sui.model.read.ChainIdentifier;
import io.dipcoin.sui.model.transaction.Transaction;
import io.dipcoin.sui.model.transaction.TransactionBlockBytes;
import io.dipcoin.sui.model.zk.ZkLoginIntentScope;
import io.dipcoin.sui.protocol.constant.SuiSystem;
import io.dipcoin.sui.protocol.http.HttpService;
import io.dipcoin.sui.protocol.http.request.*;
import io.dipcoin.sui.protocol.http.response.*;
import io.dipcoin.sui.pyth.constant.PythConfig;
import io.dipcoin.sui.pyth.constant.PythNetworkConfigs;
import io.dipcoin.sui.pyth.core.PythClient;
import io.dipcoin.sui.pyth.exception.PythException;
import io.dipcoin.sui.util.ObjectIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author : Same
 * @datetime : 2025/6/25 22:38
 * @Description : Sui client unit test test network data
 */
@Slf4j
@ExtendWith(IntervalExtension.class)
public class SuiClientTest extends DeserializerTest{

        public static final String TEST_URL = "https://fullnode.testnet.sui.io:443";
//        public static final String TEST_URL = "https://fullnode.mainnet.sui.io:443";
//    public static final String TEST_URL = "https://rpc-testnet.suiscan.xyz/";
//    public static final String TEST_URL = "https://rpc-mainnet.suiscan.xyz/";

    protected SuiService suiService;

    protected SuiClient suiClient;

    protected PythClient pythClient;

    /**
     * Whether write operations are on-chain.
     */
    protected static final boolean ENABLE_SEND = false;

    @BeforeEach
    protected void setUp() {
        this.suiService = new HttpService(TEST_URL);
        this.suiClient = SuiClient.build(suiService);
        this.pythClient = new PythClient(suiClient);
    }

    // --------------------- Extended API ---------------------

    @Test
    @Tag("suite")
    void testGetDynamicFieldObject() throws IOException {
        String suiFeedId = "0x50c67b3fd225db8912a424dd4baed60ffdde625ed2feaaf283724f9608fea266";

        // 1. get tableId
        GetDynamicFieldObject data = new GetDynamicFieldObject();
        data.setParentObjectId(PythNetworkConfigs.TESTNET_CONFIG.pythStateId());
        data.setName(new DynamicFieldName("vector<u8>",
                "price_info"));

        // execution test
        Request<?, SuiObjectResponseWrapper> request = suiClient.getDynamicFieldObject(data);
        SuiObjectResponseWrapper response = request.send();
        SuiObjectResponse result = response.getResult();
        log.info("SuiObjectResponse tableId result: {}", result);

        String tableId = result.getData().getObjectId();
        String tableType = ObjectIdUtil.getFeedObjectId(result.getData().getType());
        log.info("tableId tableId: {}", tableId);
        log.info("tableId tableType: {}", tableType);

        // 2. get feedObjectId
        GetDynamicFieldObject data2 = new GetDynamicFieldObject();
        data2.setParentObjectId(tableId);
        data2.setName(new DynamicFieldName(tableType + PythConfig.TABLE_ID_TYPE,
                suiFeedId));

        // execution test
        Request<?, SuiObjectResponseWrapper> request2 = suiClient.getDynamicFieldObject(data2);
        SuiObjectResponseWrapper response2 = request2.send();
        SuiObjectResponse result2 = response2.getResult();
        log.info("SuiObjectResponse feedObjectId result2: {}", result2);
        MoveObject content = (MoveObject) result2.getData().getContent();
        MoveStructMap fields = (MoveStructMap) content.getFields();
        MoveValue value = fields.getValues().get("value");
        log.info("SuiObjectResponse feedObjectId result2 value : {}", value.getValue());

        // verify result type
        assertThat(result)
                .isInstanceOf(SuiObjectResponse.class);
    }

    @Test
    @Tag("suite")
    void testGetOwnedObjects() throws IOException {
//        String address = "0xa8553f863775fc47eebfafa42fca1b72590859ead0fae650b6bae2e56e485e63";
        String address = "0xe4758f87e8c2601ad25a1e7f6e2711ef36359c4f2ccc2986e7d8444af4d491cf";

        String suiType = "0x2::coin::Coin<0x2::sui::SUI>";
        String capType = "0x1f2788918b609959c9052a1f00c49765752acb24d99997a102903be7da18dd0d::roles::DeleveragingCap";
        MoveModuleFilter moveModuleFilter = new MoveModuleFilter("trade", "0x1f2788918b609959c9052a1f00c49765752acb24d99997a102903be7da18dd0d");

        List<SuiObjectDataFilter> filters = List.of(
                new SuiObjectDataFilter().setStructTypeFilter(suiType)
                , new SuiObjectDataFilter().setStructTypeFilter(capType)
                , new SuiObjectDataFilter().setMoveModuleFilter(moveModuleFilter)
        );

        SuiObjectDataFilter suiObjectDataFilter = new SuiObjectDataFilter();
        suiObjectDataFilter.setMatchAny(filters);

        ObjectResponseQuery objectResponseQuery = new ObjectResponseQuery();
        objectResponseQuery.setFilter(suiObjectDataFilter);

        // 1. get tableId
        GetOwnedObjects data = new GetOwnedObjects();
        data.setAddress(address);
        data.setQuery(objectResponseQuery);
        data.setCursor(null);
        data.setLimit(null);

        // execution test
        Request<?, PageForSuiObjectResponseAndObjectIdWrapper> request = suiClient.getOwnedObjects(data);
        PageForSuiObjectResponseAndObjectIdWrapper response = request.send();
        PageForSuiObjectResponseAndObjectId result = response.getResult();
        log.info("result: {}", result);

        // verify result type
        assertThat(result)
                .isInstanceOf(PageForSuiObjectResponseAndObjectId.class);
    }

    @Test
    @Tag("suite")
    void testGetOwnedObject() throws IOException {
        String address = "0x9a52b85c81775a9571d1f963131cf3ee82f06e3c2dc84f9d5b643c698ec838d4";

//        String capType = "0x527fd3ce8ad58865ad72083352a19f73d6fb5fa114bd5b8a1451a78fedb21b6f::roles::SettlementCap";
        String capType = "0x2::coin::Coin<0x2::sui::SUI>";

        SuiObjectDataFilter suiObjectDataFilter = new SuiObjectDataFilter();
        suiObjectDataFilter.setStructTypeFilter(capType);

        ObjectResponseQuery objectResponseQuery = new ObjectResponseQuery();
        objectResponseQuery.setFilter(suiObjectDataFilter);

        // 1. get tableId
        GetOwnedObjects data = new GetOwnedObjects();
        data.setAddress(address);
        data.setQuery(objectResponseQuery);
        data.setCursor(null);
        data.setLimit(null);

        // execution test
        Request<?, PageForSuiObjectResponseAndObjectIdWrapper> request = suiClient.getOwnedObjects(data);
        PageForSuiObjectResponseAndObjectIdWrapper response = request.send();
        PageForSuiObjectResponseAndObjectId result = response.getResult();
        log.info("result: {}", result);

        // verify result type
        assertThat(result)
                .isInstanceOf(PageForSuiObjectResponseAndObjectId.class);
    }

    // --------------------- Governance Read API ---------------------

    @Test
    @Tag("suite")
    void testGetLatestSuiSystemState() throws IOException {
        // execution test
        Request<?, SuiSystemStateWrapper> request = suiClient.getLatestSuiSystemState();
        SuiSystemStateWrapper response = request.send();

        SuiSystemStateSummary result = response.getResult();
        log.info("result: {}", result);
        long now = System.currentTimeMillis();
        log.info("Current epoch: {}", result.getEpoch());
        log.info("The current epoch has elapsed: {} ms", now - result.getEpochStartTimestampMs());
        log.info("Time remaining in the current epoch: {} ms", result.getEpochStartTimestampMs() + result.getEpochDurationMs() - now);
        log.info("Current epoch GasPrice: {}", result.getReferenceGasPrice());
    }

    @Test
    @Tag("suite")
    void testGetReferenceGasPrice() throws IOException {

        // execution test
        Request<?, GasPriceWrapper> request = suiClient.getReferenceGasPrice();
        GasPriceWrapper response = request.send();

        // Verify the result format (Hex string)
        assertThat(response.getResult())
                .as("GasPrice should be a string.")
                .matches("^[0-9]+$")
                .hasSizeGreaterThan(0); // Adjust length validation based on actual requirements.

        // Verify the result format (numeric)
        assertThat(response.getGasPrice())
                .as("Converted numerical value.")
                .isGreaterThan(BigInteger.ZERO) // Must be a positive number
                .isLessThan(new BigInteger("ffffffff", 16)); // Example upper limit.
    }

    @Test
    @Tag("suite")
    void testGetObject() throws IOException {
        GetObject data = new GetObject();
//        data.setObjectId("0xa7860ce29d33ca594d88251d11322358cc0b3c72d4a33821f8abe10d9d9ef8b4");
//        data.setObjectId("0x662be49e49772e9f7caddb6ddb414e30d5709075a292f6dd0649b206e63851b1");
//        data.setObjectId("0xa1b3e973d25dd12c467866814d6135890395a468e799794f1ee61b3cf09ad85a");
//        data.setObjectId("0xaff1a5fb513ee19be93d377fcd08bf9ffcea7d2f93a3693362b11dcd4b2baa3c");
//        data.setObjectId("0x0b50fe6d7b86730f0f8d2e389d22d63c30a73a1034720a8a43bb5e322a9588e1");
//        data.setObjectId("0xe530ce941d899d132ca82b11cd49ccc7d8828fbcd7e59ecf79ac8e233147a227");
//        data.setObjectId("0x655673a0878a510794d2b9141250f7251cf84414e33c1a9873b23198a62f5afc");
//        data.setObjectId("0x68c2c2fcb950006513499ceb920dd9532042dd748c23af578626fe198cb5a034");
//        data.setObjectId("0x97f373d23ebeefd13fa1249d8864e94c7a88648e838f9cfcfd4bc9d0ec415f81");
//        data.setObjectId("0x5ee0fbedc824e4bdbf555400b3aafaebd10df853adcebaaef9733b56c5ff35d8");
        data.setObjectId("0x88e5b7d9dac3ed2378084bc0cfdd52c3c9cd01257cfd3a78ede5cf2b62614468");
//        data.setObjectId("0x527fd3ce8ad58865ad72083352a19f73d6fb5fa114bd5b8a1451a78fedb21b6f");
        data.setOptions(ObjectDataOptions.ownerAndTypeTrue());

        // execution test
        Request<?, SuiObjectResponseWrapper> request = suiClient.getObject(data);
        SuiObjectResponseWrapper response = request.send();
        SuiObjectResponse result = response.getResult();
        log.info("result: {}", result);

        // verify
        assertThat(result).isInstanceOf(SuiObjectResponse.class);
    }

    @Test
    @Tag("suite")
    void testMultiGetObjects() throws IOException {
        MultiGetObjects data = new MultiGetObjects();
        data.setObjectIds(List.of(
                "0xa7860ce29d33ca594d88251d11322358cc0b3c72d4a33821f8abe10d9d9ef8b4",
                "0x662be49e49772e9f7caddb6ddb414e30d5709075a292f6dd0649b206e63851b1",
                "0xa1b3e973d25dd12c467866814d6135890395a468e799794f1ee61b3cf09ad85a",
                "0xaff1a5fb513ee19be93d377fcd08bf9ffcea7d2f93a3693362b11dcd4b2baa3c",
                "0x0b50fe6d7b86730f0f8d2e389d22d63c30a73a1034720a8a43bb5e322a9588e1"
        ));
        data.setOptions(ObjectDataOptions.allTrue());

        // execution test
        Request<?, SuiMultiObjectResponseWrapper> request = suiClient.multiGetObjects(data);
        SuiMultiObjectResponseWrapper response = request.send();
        List<SuiObjectResponse> result = response.getResult();
        log.info("result: {}", result);

        // verify
        assertThat(response).isInstanceOf(SuiMultiObjectResponseWrapper.class);
    }

    @Test
    @Tag("suite")
    void testGetObjectBaseUpdateFee() throws IOException {
        GetObject data = new GetObject();
        data.setObjectId("0x243759059f4c3111179da5878c12f68d612c21a8d54d85edc86164bb18be1c7c");
        data.setOptions(ObjectDataOptions.allTrue());

        // execution test
        Request<?, SuiObjectResponseWrapper> request = suiClient.getObject(data);
        SuiObjectResponseWrapper response = request.send();
        SuiObjectResponse result = response.getResult();
        MoveObject content = (MoveObject) result.getData().getContent();
        MoveStructMap fields = (MoveStructMap) content.getFields();
        MoveValue value = fields.getValues().get("base_update_fee");
        int baseUpdateFee = Integer.parseInt(value.getValue().toString());
        log.info("baseUpdateFee: {}", baseUpdateFee);

        // verify
        assertThat(result).isInstanceOf(SuiObjectResponse.class);
    }

    @Test
    @Tag("suite")
    void testGetPythPackageId() throws IOException {
        GetObject data = new GetObject();
        data.setObjectId("0x243759059f4c3111179da5878c12f68d612c21a8d54d85edc86164bb18be1c7c");
        data.setOptions(ObjectDataOptions.contentTrue());

        // execution test
        Request<?, SuiObjectResponseWrapper> request = suiClient.getObject(data);
        SuiObjectResponseWrapper response;
        try {
            response = request.send();
        } catch (IOException e) {
            throw new PythException("Get the packageId for the pyth package failed!", e);
        }
        SuiObjectResponse result = response.getResult();
        MoveObject content = (MoveObject) result.getData().getContent();
        MoveStructMap fields = (MoveStructMap) content.getFields();
        MoveValue value = fields.getValues().get("upgrade_cap");
        MoveStructObject fieldsStruct = (MoveStructObject) value.getValue();
        MoveValue moveValue = fieldsStruct.getFields().get("package");
        log.info("result : {}", moveValue.getValue().toString());

        // verify
        assertThat(result).isInstanceOf(SuiObjectResponse.class);
    }

    @Test
    @Tag("suite")
    void testVerifyZkLoginSignature() throws IOException {
        VerifyZkLoginSignature data = new VerifyZkLoginSignature();
        data.setBytes("aGVsbG8gd29ybGQ=");
        data.setSignature("BQNMMzI4ODA4OTM4MjA5MTM5MzI1MTg5NjY4MjYxNjQxNDAyMzE4ODIwMjYyNzI5ODE3NjQ2MTYxMDkzMjEwODEzMDQ3MDU4OTcyMjYxMk0xNDAyMjY2NjkzNDM5ODg4NjE3ODQ3NjA5NTg5OTkzMDQ3NTg2MDg5NTYxMTU3MDMxNDk0MTU5OTgyNzk5MTE4OTY5MjY5MTIyNjg5OAExAwJNMTc0ODAxNDUyMTI3OTc5Njg0NzAzOTM0OTgxODQ4NTQzMDkxODE1NTgxMTk4NDczOTA0ODUwNjc3NDEzMjM3NTg5MDkxNDA3NzAzOTdMNDM2MzM2MTY4NDIwNjU2NzAwMzUyMDE1MTI5MjM1NzcwNjI5ODAyMDIzNjk2OTg2NTY5MzQyMDAwNjA5ODU3MDAxNTkwNjk2ODY2OAJMODE3NDE0Mzk5OTMyOTQ0NDU0MTM0OTYwMjUzOTkxNzg5NjUwNDQwNjU5NTYwMDk0NjQ1NTE5MTIxMjc1MDMzNzYzMzc2ODExMDY4Nkw3MTgwOTY0NTQyNzMzMjk2NzU1MzIwOTU5ODUxNzY5MzA4NjYwMTM1ODIzNDk1ODU5ODQ5NDQyODk2NjE0NzY5ODYyODM5NTA0MzE3AgExATADTDE4MDkxMzA5NDI4MTkzNjI4ODU4MjY2MzgxMTY5MjAwMTE3ODA1MjYzOTA1MDMwNTk2MDYxNzk4OTk0MjkwOTU0MDA0MzE2ODU1MTBMMzU4NzgyMTI5MTY4NzYyODAyMDc4MjYyMDQzMjI0ODU4OTM2MTEwMjIwOTU0MTcxOTM1OTM2ODgyMzI2OTU1NDEzNzgzNTIwNDE1MgExMXlKcGMzTWlPaUpvZEhSd2N6b3ZMMkZqWTI5MWJuUnpMbWR2YjJkc1pTNWpiMjBpTEMBZmV5SmhiR2NpT2lKU1V6STFOaUlzSW10cFpDSTZJbUkxTURsak5URXpPRGMyT0dZM1kyWXlaVGd5TjJVd05HSXlOMlUzWlRSalltTTNZbUk1TVRraUxDSjBlWEFpT2lKS1YxUWlmUU0xNTk5NDEzMjQ4MTgyODYyNTY4OTgxMzUxNjQwMzM1NTg3NDgyNjMwODM2NzE5NjU5MTMxMzEyMDA2MjE4NDg1NDA5OTg5ODkwOTMxMSUDAAAAAAAAYgIvJTRy7BClgG5aPmIOkF5kGcjeu8pHXIijq9+Y6UFbk0jSjdIjf4qrmoE548Ko+x81q5RA3co0dvucLJR6BFGMAwz4oxA+xk6EgJNBnkJC70TCvgzkG+ZT3kjy3Ky/4V/R");
        data.setIntentScope(ZkLoginIntentScope.PERSONAL_MESSAGE.getScope());
        data.setAuthor("0x13d9ff6e14fb152fdb81997f99998842be93a4e56ef646ca646f45dab55d45b2");

        // Execute the test (only test when the signature is valid to avoid affecting installation).
        Request<?, ZkLoginVerifyResultWrapper> request = suiClient.verifyZkLoginSignature(data);
        ZkLoginVerifyResultWrapper response;
//        try {
//            response = request.send();
//        } catch (IOException e) {
//            throw new PythException("Get the packageId for the pyth package failed!", e);
//        }
//        ZkLoginVerifyResult result = response.getResult();
//        Boolean success = result.getSuccess();
//        if (success) {
//            log.info("verifyZkLoginSignature success! {}", success);
//        } else {
//            log.info("verifyZkLoginSignature failed : {}, cause : {}", success, result.getErrors().toString());
//        }
//
//        // verify
//        assertThat(result).isInstanceOf(ZkLoginVerifyResult.class);
    }

    // --------------------- Move Utils API ---------------------

    @Test
    @Tag("suite")
    void testGetMoveFunctionArgTypes() throws IOException {
        GetMoveFunctionArgTypes data = new GetMoveFunctionArgTypes();
//        data.setObjectId("0x0000000000000000000000000000000000000000000000000000000000000002");
//        data.setModule("pay");
//        data.setFunction("split");
        data.setObjectId("0x527fd3ce8ad58865ad72083352a19f73d6fb5fa114bd5b8a1451a78fedb21b6f");
        data.setModule("exchange");
        data.setFunction("trade");
//        data.setObjectId("0x21473617f3565d704aa67be73ea41243e9e34a42d434c31f8182c67ba01ccf49");
//        data.setModule("vaa");
//        data.setFunction("parse_and_verify");

        // execution test
        Request<?, MoveFunctionArgTypesWrapper> request = suiClient.getMoveFunctionArgTypes(data);
        MoveFunctionArgTypesWrapper response = request.send();
        List<MoveFunctionArgType> result = response.getResult();
        System.out.println(result);
        System.out.println(result.size());
        for (MoveFunctionArgType argType : result) {
            System.out.println(argType.getClass() + " : " + argType.getType());
        }
        // Verify the result format (Hex string).
//        assertThat(response.getResult())
//                .as("ChainIdentifier should be a hexadecimal string.")
//                .matches("^[0-9a-f]+$")
//                .hasSize(8); // Adjust length validation based on actual requirements.

    }

    @Test
    @Tag("suite")
    void testGetNormalizedMoveFunction() throws IOException {
        GetNormalizedMoveFunction data = new GetNormalizedMoveFunction();
//        data.setObjectId("0x0000000000000000000000000000000000000000000000000000000000000002");
//        data.setModuleName("pay");
//        data.setFunctionName("split");
        data.setObjectId("0x527fd3ce8ad58865ad72083352a19f73d6fb5fa114bd5b8a1451a78fedb21b6f");
        data.setModuleName("exchange");
        data.setFunctionName("trade");

        // execution test
        Request<?, SuiMoveNormalizedFunctionWrapper> request = suiClient.getNormalizedMoveFunction(data);
        SuiMoveNormalizedFunctionWrapper response = request.send();
        SuiMoveNormalizedFunction result = response.getResult();
        System.out.println(result);
        // Verify the result format (Hex string).
//        assertThat(response.getResult())
//                .as("ChainIdentifier should be a hexadecimal string.")
//                .matches("^[0-9a-f]+$")
//                .hasSize(8); // Adjust length validation based on actual requirements.

    }

    @Test
    @Tag("suite")
    void testGetSuiMoveNormalizedModule() throws IOException {
        GetNormalizedMoveModule data = new GetNormalizedMoveModule();
        data.setObjectId("0x0000000000000000000000000000000000000000000000000000000000000002");
        data.setModuleName("pay");

        // execution test
        Request<?, SuiMoveNormalizedModuleWrapper> request = suiClient.getNormalizedMoveModule(data);
        SuiMoveNormalizedModuleWrapper response = request.send();
        SuiMoveNormalizedModule result = response.getResult();
        System.out.println(result);
        // Verify the result format (Hex string).
//        assertThat(response.getResult())
//                .as("ChainIdentifier should be a hexadecimal string.")
//                .matches("^[0-9a-f]+$")
//                .hasSize(8); // Adjust length validation based on actual requirements.

    }

    @Test
    @Tag("suite")
    void testGetSuiMoveNormalizedModuleByPackage() throws IOException {
        GetNormalizedMoveModuleByPackage data = new GetNormalizedMoveModuleByPackage();
        data.setObjectId("0x527fd3ce8ad58865ad72083352a19f73d6fb5fa114bd5b8a1451a78fedb21b6f");
//        data.setObjectId("0x0000000000000000000000000000000000000000000000000000000000000002");

        // execution test
        Request<?, SuiMoveNormalizedModuleByPackageWrapper> request = suiClient.getNormalizedMoveModulesByPackage(data);
        SuiMoveNormalizedModuleByPackageWrapper response = request.send();
        Map<String, SuiMoveNormalizedModule> result = response.getResult();
        System.out.println(result);
        System.out.println("result size : " + result.size());
        // Verify the result format (Hex string).
//        assertThat(response.getResult())
//                .as("ChainIdentifier should be a hexadecimal string.")
//                .matches("^[0-9a-f]+$")
//                .hasSize(8); // Adjust length validation based on actual requirements.

    }

    @Test
    @Tag("suite")
    void testGetNormalizedMoveStruct() throws IOException {
        GetNormalizedMoveStruct data = new GetNormalizedMoveStruct();
        data.setObjectId("0x527fd3ce8ad58865ad72083352a19f73d6fb5fa114bd5b8a1451a78fedb21b6f");
        data.setModuleName("bank");
        data.setStructName("Bank");

        // execution test
        Request<?, SuiMoveNormalizedStructWrapper> request = suiClient.getNormalizedMoveStruct(data);
        SuiMoveNormalizedStructWrapper response = request.send();
        SuiMoveNormalizedStruct result = response.getResult();
        System.out.println(result);
        // Verify the result format (Hex string).
//        assertThat(response.getResult())
//                .as("ChainIdentifier should be a hexadecimal string.")
//                .matches("^[0-9a-f]+$")
//                .hasSize(8); // Adjust length validation based on actual requirements.

    }

    // --------------------- Read API ---------------------

    @Test
    @Tag("suite")
    void testGetChainIdentifier() throws IOException {

        // execution test
        Request<?, ChainIdentifier> request = suiClient.getChainIdentifier();
        ChainIdentifier response = request.send();

        // Verify the result format (Hex string).
        assertThat(response.getResult())
                .as("ChainIdentifier should be a hexadecimal string.")
                .matches("^[0-9a-f]+$")
                .hasSize(8); // Adjust length validation based on actual requirements.

        // Verify the result format (numeric).
        assertThat(response.getSuiChainIdentifier())
                .as("Converted numerical value.")
                .isGreaterThan(BigInteger.ZERO) // Must be a positive number.
                .isLessThan(new BigInteger("ffffffff", 16)); // Example upper limit.
    }

    // --------------------- Transaction Builder API ---------------------

    @Test
    @Tag("suite")
    void testPaySui() throws IOException {
        SuiWallet operator = WalletKeyPair.WALLETS.get(WalletKeyPair.OPERATOR);
        SuiKeyPair operatorKeyPair = operator.getKeyPair();

        // 1. Basic parameters
        String signer = operatorKeyPair.address();
        List<String> recipients = Arrays.asList("0x3b77dde7abbe2156df1751bb1880de3dbfcb7a90ae61640115b5225c0b8d4fb5");
        List<String> inputCoins = Arrays.asList(operator.getGasObjectId());
        List<BigInteger> amount = Arrays.asList(BigInteger.TEN.pow(6));
        BigInteger gasBudget = new BigInteger("10000000");

        UnsafePaySui paySui = new UnsafePaySui();
        paySui.setSigner(signer);
        paySui.setRecipients(recipients);
        paySui.setInputCoins(inputCoins);
        paySui.setAmounts(amount);
        paySui.setGasBudget(gasBudget);

        // 2. execution test
        Request<?, TransactionBlockBytesWrapper> request = suiClient.paySui(paySui);
        TransactionBlockBytesWrapper response = request.send();
        TransactionBlockBytes result = response.getResult();
        System.out.println("testPaySui : " + result.toString());

        // 3. Verify the result format (Hex string).
        assertThat(result)
                .isInstanceOf(TransactionBlockBytes.class);

        // 4. on chain
//        if (true) {
        if (ENABLE_SEND) {
            this.testExecuteTransactionBlock(result.getTxBytes(), operatorKeyPair);
        }
    }

    @Test
    @Tag("suite")
    void testMoveCallDeposit() throws IOException {
        // 1. Basic parameters
        SuiWallet suiWallet = WalletKeyPair.WALLETS.get(WalletKeyPair.OPERATOR);
        SuiKeyPair user = suiWallet.getKeyPair();

        PackageConfig depositConfig = PerpPackage.MOVE_CALL.get(PerpPackage.DEPOSIT);
        List<Object> arguments = Arrays.asList(
                PerpPackage.TRADE_PROJECT_CONFIG_ID,
                PerpPackage.BANK_ID,
                PerpPackage.TX_INDEXER_ID,
                Base64.encode(Ed25519KeyPair.generate().privateKey()), // tx_hash
//                new byte[]{73,83,57,104,113,85,68,73,76,78,43,56,74,51,110,103,107,122,69,50,69,75,88,43,103,88,122,120,78,65,99,105,75,97,73,109,112,79,75,119,77,55,99,61}, // tx_hash
                user.address(),
                new BigInteger("10").multiply(BigInteger.TEN.pow(6)), // amount
                suiWallet.getUsdcObjectId()  // coin objectId
        );

        // Assemble the request.
        UnsafeMoveCall moveCall = new UnsafeMoveCall();
        moveCall.setSigner(user.address());
        moveCall.setPackageObjectId(PerpPackage.OBJECT_ID);
        moveCall.setModule(depositConfig.getModule());
        moveCall.setFunction(depositConfig.getFunction());
        moveCall.setTypeArguments(PerpPackage.TYPE_TAG);
        moveCall.setArguments(arguments);
        moveCall.setGas(suiWallet.getGasObjectId());
        moveCall.setGasBudget(TxConfig.GAS_BUDGET);

        // 2. execution test
        Request<?, TransactionBlockBytesWrapper> request = suiClient.moveCall(moveCall);
        TransactionBlockBytesWrapper response = request.send();
        TransactionBlockBytes result = response.getResult();
        System.out.println("testMoveCallDeposit : " + result.toString());

        // 3. Verify the result format (Hex string).
        assertThat(result)
                .isInstanceOf(TransactionBlockBytes.class);

        // 4. on chain
//        if (true) {
        if (ENABLE_SEND) {
            this.testExecuteTransactionBlock(result.getTxBytes(), user);
        }
    }

    @Test
    @Tag("suite")
    void testMoveCallSplitCoin() throws IOException {
        SuiWallet suiWallet = WalletKeyPair.WALLETS.get(WalletKeyPair.OPERATOR);
        SuiKeyPair operatorKeyPair = suiWallet.getKeyPair();
        String amount = "1";
        // Must be another SUI coin !
        String suiObjectId = "0x04ae1f86935550a28faef0f61fbbbbb73d5b013264dd404f834c94f4d6e2c5ed";

        // 1. Basic parameters
        List<String> typeArguments = Arrays.asList(SuiSystem.SUI_TYPE_ARG);
        List<Object> arguments = Arrays.asList(
                suiObjectId, // sui
                amount // amount
        );

        UnsafeMoveCall moveCall = new UnsafeMoveCall();
        moveCall.setSigner(operatorKeyPair.address());
        moveCall.setPackageObjectId(SuiSystem.SUI_FRAMEWORK_ADDRESS);
        moveCall.setModule("coin");
        moveCall.setFunction("split");
        moveCall.setTypeArguments(typeArguments);
        moveCall.setArguments(arguments);
        moveCall.setGas(suiWallet.getGasObjectId());
        moveCall.setGasBudget(TxConfig.GAS_BUDGET);

        // 2. execution test
        Request<?, TransactionBlockBytesWrapper> request = suiClient.moveCall(moveCall);
        TransactionBlockBytesWrapper response = request.send();
        TransactionBlockBytes result = response.getResult();
        log.info("result : " + result.toString());

        // 3. Verify the result format (Hex string).
        assertThat(result)
                .isInstanceOf(TransactionBlockBytes.class);

        // 4. on chain
//        if (true) {
        if (ENABLE_SEND) {
            this.testExecuteTransactionBlock(result.getTxBytes(), operatorKeyPair);
        }
    }

    @Test
    @Tag("suite")
    void testTransferObject() throws IOException {
        SuiWallet suiWallet = WalletKeyPair.WALLETS.get(WalletKeyPair.OPERATOR);
        SuiKeyPair operatorKeyPair = suiWallet.getKeyPair();
        // 1. Basic parameters
        String signer = operatorKeyPair.address();
        String objectId = "0xe530ce941d899d132ca82b11cd49ccc7d8828fbcd7e59ecf79ac8e233147a227";
        String gas = suiWallet.getGasObjectId();
        String recipient = "0x474c79910cc1cb54f029eab7965575dbb47697876946d23062b635394d0729ed";
        BigInteger gasBudget = new BigInteger("10000000");

        UnsafeTransferObject transferObject = new UnsafeTransferObject();
        transferObject.setSigner(signer);
        transferObject.setObjectId(objectId);
        transferObject.setGas(gas);
        transferObject.setGasBudget(gasBudget);
        transferObject.setRecipient(recipient);

        // 2. execution test
        Request<?, TransactionBlockBytesWrapper> request = suiClient.transferObject(transferObject);
        TransactionBlockBytesWrapper response = request.send();
        TransactionBlockBytes result = response.getResult();
        System.out.println("transferObject : " + result.toString());

        // 3. Verify the result format (Hex string).
        assertThat(result)
                .isInstanceOf(TransactionBlockBytes.class);

        // 4. on chain
        if (ENABLE_SEND) {
            this.testExecuteTransactionBlock(result.getTxBytes(), operatorKeyPair);
        }
    }

    /**
     * on chain
     * @param txBytes
     * @throws IOException
     */
    private void testExecuteTransactionBlock(String txBytes, String key, SignatureScheme signatureScheme) throws IOException {
        Transaction transaction = TransactionBuilder.buildTransaction(txBytes, key, signatureScheme);

//        // execution on chain test
        Request<?, SuiTransactionBlockResponseWrapper> tx = suiClient.executeTransactionBlock(transaction);
        SuiTransactionBlockResponseWrapper send = tx.send();
        System.out.println("on chain success:" + send.getResult().toString());
    }

    /**
     * on chain
     * @param txBytes
     * @throws IOException
     */
    protected void testExecuteTransactionBlock(String txBytes, SuiKeyPair suiKeyPair) throws IOException {
        Transaction transaction = TransactionBuilder.buildTransaction(txBytes, suiKeyPair);

//        // execution on chain test
        Request<?, SuiTransactionBlockResponseWrapper> tx = suiClient.executeTransactionBlock(transaction);
        SuiTransactionBlockResponseWrapper send = tx.send();
        System.out.println("on chain success:" + send.getResult().toString());
    }

}
