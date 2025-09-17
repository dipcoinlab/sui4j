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

package io.dipcoin.sui.pyth.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dipcoin.sui.bcs.PureBcs;
import io.dipcoin.sui.bcs.TypeTagSerializer;
import io.dipcoin.sui.bcs.types.arg.call.CallArgObjectArg;
import io.dipcoin.sui.bcs.types.arg.call.CallArgPure;
import io.dipcoin.sui.bcs.types.transaction.Argument;
import io.dipcoin.sui.bcs.types.transaction.Command;
import io.dipcoin.sui.bcs.types.transaction.ProgrammableMoveCall;
import io.dipcoin.sui.bcs.types.transaction.ProgrammableTransaction;
import io.dipcoin.sui.client.CommandBuilder;
import io.dipcoin.sui.client.TransactionBuilder;
import io.dipcoin.sui.model.Request;
import io.dipcoin.sui.model.extended.DynamicFieldName;
import io.dipcoin.sui.model.move.kind.MoveValue;
import io.dipcoin.sui.model.move.kind.data.MoveObject;
import io.dipcoin.sui.model.move.kind.struct.MoveStructMap;
import io.dipcoin.sui.model.move.kind.struct.MoveStructObject;
import io.dipcoin.sui.model.object.ObjectDataOptions;
import io.dipcoin.sui.model.object.SuiObjectResponse;
import io.dipcoin.sui.protocol.SuiClient;
import io.dipcoin.sui.protocol.constant.SuiSystem;
import io.dipcoin.sui.protocol.http.request.GetDynamicFieldObject;
import io.dipcoin.sui.protocol.http.request.GetObject;
import io.dipcoin.sui.protocol.http.response.SuiObjectResponseWrapper;
import io.dipcoin.sui.pyth.constant.PythConfig;
import io.dipcoin.sui.pyth.constant.PythNetworkConfigs;
import io.dipcoin.sui.pyth.exception.PythException;
import io.dipcoin.sui.pyth.model.PythNetwork;
import io.dipcoin.sui.pyth.model.PythNetworkConfig;
import io.dipcoin.sui.pyth.model.PythResponse;
import io.dipcoin.sui.util.ObjectIdUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.bouncycastle.util.encoders.Base64;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : Same
 * @datetime : 2025/7/16 14:27
 * @Description : Pyth client
 */
@Slf4j
public class PythClient {

    private final static Map<String, CallArgObjectArg> PYTH_SHARED = new ConcurrentHashMap<>();
    private final static Map<String, String> FEED_OBJECT_IDS = new ConcurrentHashMap<>();
    private final static Map<String, String> PACKAGE_IDS = new ConcurrentHashMap<>();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final SuiClient suiClient;
    private final OkHttpClient pythOkHttpClient;

    public PythClient(SuiClient suiClient) {
        this.suiClient = suiClient;
        this.pythOkHttpClient = new OkHttpClient();
    }

    public PythClient(SuiClient suiClient, OkHttpClient pythOkHttpClient) {
        this.suiClient = suiClient;
        this.pythOkHttpClient = pythOkHttpClient;
    }

    /**
     * Adds the commands for calling wormhole and verifying the vaas and returns the verified vaas.
     * @param feedId
     * @param pythNetwork
     * @return
     */
    public ProgrammableTransaction updatePrice(String feedId, PythNetwork pythNetwork) {
        PythNetworkConfig pythNetworkConfig = pythNetwork.getConfig();
        PythResponse lastPrice = this.getLastPrice(feedId, pythNetworkConfig);
        byte[] data = Base64.decode(lastPrice.getBinary().getData().get(0));

        ProgrammableTransaction programmableTx = new ProgrammableTransaction();
        // 0. parse_and_verify
        programmableTx.addCommand(this.verifyVaas(data, pythNetworkConfig, programmableTx))

        // 1. create_authenticated_price_infos_using_accumulator
        .addCommand(this.createAuthenticatedPrice(data, pythNetworkConfig, programmableTx))

        // 2. SplitCoin
        .addCommand(CommandBuilder.splitCoins(List.of(Argument.ofInput(programmableTx.addInput(new CallArgPure(
                        this.getBaseUpdateFee(pythNetworkConfig), PureBcs.BasePureType.U64))))))

        // 3. update_single_price_feed
        .addCommand(this.updateSinglePriceFeed(feedId, pythNetworkConfig, programmableTx))

        // 4. destroy
        .addCommand(this.destroy(pythNetworkConfig, programmableTx));

        return programmableTx;
    }

    /**
     * get objectId of feedId
     * @param feedId
     * @param pythStateId
     * @return
     */
    public String getFeedObjectId(String feedId, String pythStateId) {
        String objectId = FEED_OBJECT_IDS.get(feedId);
        if (objectId != null) {
            return objectId;
        }
        return this.getPriceFeedObjectId(feedId, pythStateId);
    }

    /**
     * Fetch the latest information of feedId from the API.
     * @param feedId
     * @param pythNetworkConfig
     * @return
     */
    public PythResponse getLastPrice(String feedId, PythNetworkConfig pythNetworkConfig) {
        String url = pythNetworkConfig.priceServiceEndpoint() + PythNetworkConfigs.GET_LAST_PRICE_FEEDS + URLEncoder.encode(feedId, StandardCharsets.UTF_8);

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = pythOkHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new IOException("Unexpected code " + response);
            }
            String body = response.body().string();
            return objectMapper.readValue(body, PythResponse.class);
        } catch (IOException e) {
            throw new PythException("Failed to request Pyth price feed API", e);
        }
    }

    /**
     * get update price fee
     * @param pythNetworkConfig
     * @return
     */
    public long getBaseUpdateFee(PythNetworkConfig pythNetworkConfig) {
        GetObject data = new GetObject();
        data.setObjectId(pythNetworkConfig.pythStateId());
        data.setOptions(ObjectDataOptions.contentTrue());

        Request<?, SuiObjectResponseWrapper> request = suiClient.getObject(data);
        SuiObjectResponseWrapper response;
        try {
             response = request.send();
        } catch (IOException e) {
            throw new PythException("Get objectId by Pyth stateId failed!", e);
        }
        SuiObjectResponse result = response.getResult();
        MoveObject content = (MoveObject) result.getData().getContent();
        MoveStructMap fields = (MoveStructMap) content.getFields();
        MoveValue value = fields.getValues().get("base_update_fee");
        return Long.parseLong(value.getValue().toString());
    }

    /**
     * Get the packageId for the pyth package if not already cached
     * @param stateId
     * @return
     */
    public String getPackageId(String stateId) {
        String packageId = PACKAGE_IDS.get(stateId);
        if (packageId != null) {
            return packageId;
        }
        GetObject data = new GetObject();
        data.setObjectId(stateId);
        data.setOptions(ObjectDataOptions.contentTrue());

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
        String resultId = fieldsStruct.getFields().get("package").getValue().toString();
        PACKAGE_IDS.put(stateId, resultId);
        return resultId;
    }

    /**
     * Adds the commands for calling wormhole and verifying the vaas and returns the verified vaas.
     * @param data
     * @param pythNetworkConfig
     * @param programmableTx
     * @return
     */
    private Command verifyVaas(byte[] data, PythNetworkConfig pythNetworkConfig, ProgrammableTransaction programmableTx) {
        byte[] bytes = this.extractVaaBytesFromAccumulatorMessage(data);
        String wormholeStateId = pythNetworkConfig.wormholeStateId();
        // build moveCall
        ProgrammableMoveCall moveCall = new ProgrammableMoveCall(
                this.getPackageId(wormholeStateId),
                PythConfig.WORMHOLE_VERIFY_MODULE,
                PythConfig.WORMHOLE_VERIFY_FUNCTION,
                new ArrayList<>(),
                Arrays.asList(
                        Argument.ofInput(programmableTx.addInput(this.getSharedObject(wormholeStateId, false))),
                        Argument.ofInput(programmableTx.addInput(new CallArgPure(bytes, PureBcs.BasePureType.VECTOR_U8))),
                        Argument.ofInput(programmableTx.addInput(this.getSharedObject(SuiSystem.SUI_CLOCK_OBJECT_ID, false)))
                )
        );

        return new Command.MoveCall(moveCall);
    }

    /**
     * create_authenticated_price_infos_using_accumulator
     * @param data
     * @param pythNetworkConfig
     * @param programmableTx
     * @return
     */
    private Command createAuthenticatedPrice(byte[] data, PythNetworkConfig pythNetworkConfig, ProgrammableTransaction programmableTx) {
        String pythStateId = pythNetworkConfig.pythStateId();
        // build moveCall
        ProgrammableMoveCall moveCall = new ProgrammableMoveCall(
                this.getPackageId(pythStateId),
                PythConfig.PYTH_CREATE_MODULE,
                PythConfig.PYTH_CREATE_FUNCTION,
                new ArrayList<>(),
                Arrays.asList(
                        Argument.ofInput(programmableTx.addInput(this.getSharedObject(pythStateId, false))),
                        Argument.ofInput(programmableTx.addInput(new CallArgPure(data, PureBcs.BasePureType.VECTOR_U8))),
                        new Argument.NestedResult(0, 0),
                        Argument.ofInput(programmableTx.addInput(this.getSharedObject(SuiSystem.SUI_CLOCK_OBJECT_ID, false)))
                )
        );

        return new Command.MoveCall(moveCall);
    }

    /**
     * updateSinglePriceFeed
     * @param feedId
     * @param pythNetworkConfig
     * @param programmableTx
     * @return
     */
    private Command updateSinglePriceFeed(String feedId, PythNetworkConfig pythNetworkConfig, ProgrammableTransaction programmableTx) {
        String pythStateId = pythNetworkConfig.pythStateId();
        String feedObjectId = this.getFeedObjectId(feedId, pythStateId);

        // build moveCall
        ProgrammableMoveCall moveCall = new ProgrammableMoveCall(
                this.getPackageId(pythStateId),
                PythConfig.PYTH_UPDATE_MODULE,
                PythConfig.PYTH_UPDATE_FUNCTION,
                new ArrayList<>(),
                Arrays.asList(
                        Argument.ofInput(programmableTx.addInput(this.getSharedObject(pythStateId, false))),
                        new Argument.NestedResult(1, 0),
                        Argument.ofInput(programmableTx.addInput(this.getSharedObject(feedObjectId, true))),
                        new Argument.NestedResult(2, 0),
                        Argument.ofInput(programmableTx.addInput(this.getSharedObject(SuiSystem.SUI_CLOCK_OBJECT_ID, false)))
                )
        );

        return new Command.MoveCall(moveCall);
    }

    /**
     * destroy
     * @param pythNetworkConfig
     * @param programmableTx
     * @return
     */
    private Command destroy(PythNetworkConfig pythNetworkConfig, ProgrammableTransaction programmableTx) {
        String pythPackageId = this.getPackageId(pythNetworkConfig.pythStateId());

        // build moveCall
        ProgrammableMoveCall moveCall = new ProgrammableMoveCall(
                pythPackageId,
                PythConfig.PYTH_DESTROY_MODULE,
                PythConfig.PYTH_DESTROY_FUNCTION,
                TypeTagSerializer.parseStructTypeArgs(pythPackageId + PythConfig.TYPE_TAG, true),
                Arrays.asList(
                        new Argument.NestedResult(3, 0)
                )
        );

        return new Command.MoveCall(moveCall);
    }

    /**
     * Extract the actual VAA data.
     * @param data
     * @return
     */
    private byte[] extractVaaBytesFromAccumulatorMessage(byte[] data) {
        // Step 1: Wrap the byte array in a ByteBuffer
        ByteBuffer buffer = ByteBuffer.wrap(data);

        // Step 2: Skip the first 6 bytes (header, major, minor bytes, and trailing payload size)
        buffer.position(6);

        // Step 3: Read the trailing payload size (1 byte)
        int trailingPayloadSize = buffer.get() & 0xFF;

        // Step 4: Skip the trailing payload
        buffer.position(buffer.position() + trailingPayloadSize);

        // Step 5: Read the proof_type (1 byte, which we ignore here)
        buffer.get();

        // Step 6: Read the VAA size (2 bytes, big-endian)
        int vaaSize = Short.toUnsignedInt(buffer.getShort());

        // Step 7: Read the VAA bytes
        byte[] vaaBytes = new byte[vaaSize];
        buffer.get(vaaBytes);

        return vaaBytes;
    }

    /**
     * Get the objectId of feedId from the API.
     * @param feedId
     * @param pythStateId
     * @return
     */
    private String getPriceFeedObjectId(String feedId, String pythStateId) {
        // 1. get tableId
        GetDynamicFieldObject data = new GetDynamicFieldObject();
        data.setParentObjectId(pythStateId);
        data.setName(new DynamicFieldName("vector<u8>",
                "price_info"));

        Request<?, SuiObjectResponseWrapper> request = suiClient.getDynamicFieldObject(data);
        SuiObjectResponseWrapper response;
        try {
            response = request.send();
        } catch (IOException e) {
            throw new PythException("Get tableId by feedId failed!", e);
        }
        SuiObjectResponse result = response.getResult();


        String tableId = result.getData().getObjectId();
        String tableType = ObjectIdUtil.getFeedObjectId(result.getData().getType());

        // 2. get feedObjectId
        GetDynamicFieldObject data2 = new GetDynamicFieldObject();
        data2.setParentObjectId(tableId);
        data2.setName(new DynamicFieldName(tableType + PythConfig.TABLE_ID_TYPE,
                feedId));

        Request<?, SuiObjectResponseWrapper> request2 = suiClient.getDynamicFieldObject(data2);
        SuiObjectResponseWrapper response2;
        try {
            response2 = request2.send();
        } catch (IOException e) {
            throw new PythException("Get objectId by tableId failed!", e);
        }
        SuiObjectResponse result2 = response2.getResult();
        MoveObject content = (MoveObject) result2.getData().getContent();
        MoveStructMap fields = (MoveStructMap) content.getFields();
        MoveValue value = fields.getValues().get("value");
        String objectId = value.getValue().toString();
        FEED_OBJECT_IDS.put(feedId, objectId);
        return objectId;
    }

    /**
     * get Shared Object
     * @param objectId
     * @param mutable
     * @return
     */
    private CallArgObjectArg getSharedObject(String objectId, boolean mutable) {
        if (null == objectId || objectId.isEmpty()) {
            throw new PythException("objectId is null or empty!");
        }
        CallArgObjectArg objectArg = PYTH_SHARED.get(objectId);
        if (objectArg != null) {
            return objectArg;
        }

        CallArgObjectArg sharedObject = TransactionBuilder.buildSharedObject(suiClient, objectId, mutable);
        PYTH_SHARED.put(objectId, sharedObject);
        return sharedObject;
    }

}
