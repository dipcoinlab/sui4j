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

package io.dipcoin.sui.client;

import io.dipcoin.sui.model.Request;
import io.dipcoin.sui.model.coin.Coin;
import io.dipcoin.sui.model.coin.PageForCoinAndString;
import io.dipcoin.sui.model.filter.SuiObjectDataFilter;
import io.dipcoin.sui.model.move.kind.MoveValue;
import io.dipcoin.sui.model.move.kind.data.MoveObject;
import io.dipcoin.sui.model.move.kind.struct.MoveStructMap;
import io.dipcoin.sui.model.object.*;
import io.dipcoin.sui.protocol.SuiClient;
import io.dipcoin.sui.protocol.exceptions.RpcRequestFailedException;
import io.dipcoin.sui.protocol.http.request.GetCoins;
import io.dipcoin.sui.protocol.http.request.GetObject;
import io.dipcoin.sui.protocol.http.request.GetOwnedObjects;
import io.dipcoin.sui.protocol.http.response.PageForCoinAndStringWrapper;
import io.dipcoin.sui.protocol.http.response.PageForSuiObjectResponseAndObjectIdWrapper;
import io.dipcoin.sui.protocol.http.response.SuiObjectResponseWrapper;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author : Same
 * @datetime : 2025/8/18 15:12
 * @Description :
 */
public class QueryBuilder {

    private static final String BALANCE = "balance";

    /**
     * Get list of objectData for address
     * @param suiClient
     * @param address
     * @param types
     * @return
     */
    public static List<ObjectData> getOwnerObjectData(SuiClient suiClient, String address, List<String> types) {
        List<SuiObjectDataFilter> filters = types.stream()
                .map(type -> {
                    // Set structTypeFilter field
                    return new SuiObjectDataFilter()
                            .setStructTypeFilter(type);
                })
                .collect(Collectors.toList());

        SuiObjectDataFilter suiObjectDataFilter = new SuiObjectDataFilter();
        suiObjectDataFilter.setMatchAny(filters);

        ObjectResponseQuery objectResponseQuery = new ObjectResponseQuery();
        objectResponseQuery.setFilter(suiObjectDataFilter);
        objectResponseQuery.setOptions(ObjectDataOptions.ownerAndTypeAndContentTrue());

        // 1. Get tableId
        GetOwnedObjects data = new GetOwnedObjects();
        data.setAddress(address);
        data.setQuery(objectResponseQuery);
        data.setCursor(null);
        data.setLimit(null);

        Request<?, PageForSuiObjectResponseAndObjectIdWrapper> request = suiClient.getOwnedObjects(data);
        PageForSuiObjectResponseAndObjectIdWrapper response;
        try {
            response = request.send();
        } catch (IOException e) {
            throw new RpcRequestFailedException("Get objectData failed! address = " + address, e);
        }
        PageForSuiObjectResponseAndObjectId result = response.getResult();
        List<SuiObjectResponse> resultData = result.getData();
        if (resultData == null || resultData.isEmpty()) {
            return null;
        }
        return resultData.stream()
                .map(SuiObjectResponse::getData)
                .collect(Collectors.toList());
    }

    /**
     * Get objectData for address
     * @param suiClient
     * @param address
     * @param type
     * @return
     */
    public static ObjectData getOwnerObjectData(SuiClient suiClient, String address, String type) {
        SuiObjectDataFilter suiObjectDataFilter = new SuiObjectDataFilter();
        suiObjectDataFilter.setStructTypeFilter(type);

        ObjectResponseQuery objectResponseQuery = new ObjectResponseQuery();
        objectResponseQuery.setFilter(suiObjectDataFilter);
        objectResponseQuery.setOptions(ObjectDataOptions.ownerAndTypeAndContentTrue());

        GetOwnedObjects data = new GetOwnedObjects();
        data.setAddress(address);
        data.setQuery(objectResponseQuery);
        data.setCursor(null);
        data.setLimit(null);

        Request<?, PageForSuiObjectResponseAndObjectIdWrapper> request = suiClient.getOwnedObjects(data);
        PageForSuiObjectResponseAndObjectIdWrapper response;
        try {
            response = request.send();
        } catch (IOException e) {
            throw new RpcRequestFailedException("Get objectData failed! address = " + address, e);
        }
        PageForSuiObjectResponseAndObjectId result = response.getResult();
        List<SuiObjectResponse> resultData = result.getData();
        if (resultData == null || resultData.isEmpty()) {
            return null;
        }
        return resultData.getFirst().getData();
    }

    /**
     * Get objectData for address token
     * @param suiClient
     * @param address
     * @param type
     * @return
     */
    public static List<ObjectData> getTokenObjectData(SuiClient suiClient, String address, String type) {
        SuiObjectDataFilter suiObjectDataFilter = new SuiObjectDataFilter();
        suiObjectDataFilter.setStructTypeFilter(type);

        ObjectResponseQuery objectResponseQuery = new ObjectResponseQuery();
        objectResponseQuery.setFilter(suiObjectDataFilter);
        objectResponseQuery.setOptions(ObjectDataOptions.ownerAndTypeAndContentTrue());

        GetOwnedObjects data = new GetOwnedObjects();
        data.setAddress(address);
        data.setQuery(objectResponseQuery);
        data.setCursor(null);
        data.setLimit(null);

        Request<?, PageForSuiObjectResponseAndObjectIdWrapper> request = suiClient.getOwnedObjects(data);
        PageForSuiObjectResponseAndObjectIdWrapper response;
        try {
            response = request.send();
        } catch (IOException e) {
            throw new RpcRequestFailedException("Get objectData failed! address = " + address, e);
        }
        PageForSuiObjectResponseAndObjectId result = response.getResult();
        List<SuiObjectResponse> resultData = result.getData();
        if (resultData == null || resultData.isEmpty()) {
            return null;
        }
        return resultData.stream()
                .map(SuiObjectResponse::getData)
                .collect(Collectors.toList());
    }

    /**
     * Get objectData
     * @param suiClient
     * @param objectId
     * @param options
     * @return
     */
    public static ObjectData getObjectData(SuiClient suiClient, String objectId, ObjectDataOptions options) {
        // Get object
        GetObject data = new GetObject();
        data.setObjectId(objectId);
        data.setOptions(options);

        Request<?, SuiObjectResponseWrapper> request = suiClient.getObject(data);
        SuiObjectResponseWrapper response;
        try {
            response = request.send();
        } catch (IOException e) {
            throw new RpcRequestFailedException("Get objectData failed! objectId = " + objectId, e);
        }
        SuiObjectResponse result = response.getResult();
        return result.getData();
    }

    /**
     * Get objectData
     * @param suiClient
     * @param objectId
     * @return
     */
    public static ObjectData getObjectData(SuiClient suiClient, String objectId) {
        // Get object
        GetObject data = new GetObject();
        data.setObjectId(objectId);
        data.setOptions(ObjectDataOptions.ownerAndTypeTrue());

        Request<?, SuiObjectResponseWrapper> request = suiClient.getObject(data);
        SuiObjectResponseWrapper response;
        try {
            response = request.send();
        } catch (IOException e) {
            throw new RpcRequestFailedException("Get objectData failed! objectId = " + objectId, e);
        }
        SuiObjectResponse result = response.getResult();
        return result.getData();
    }

    /**
     * Get coins
     * @param suiClient
     * @param address
     * @param type
     * @return
     */
    public static List<Coin> getCoins(SuiClient suiClient, String address, String type) {
        // Get object
        GetCoins data = new GetCoins();
        data.setOwner(address);
        data.setCoinType(type);

        Request<?, PageForCoinAndStringWrapper> request = suiClient.getCoins(data);
        PageForCoinAndStringWrapper response;
        try {
            response = request.send();
        } catch (IOException e) {
            throw new RpcRequestFailedException("Get coins failed! address = " + address + ", type = " + type, e);
        }
        PageForCoinAndString result = response.getResult();
        return result.getData();
    }

    /**
     * get max balance from ObjectData
     * @param suiClient
     * @param address
     * @param type
     * @return
     */
    public static ObjectData getMaxBalanceObjectData(SuiClient suiClient, String address, String type) {
        List<ObjectData> objectDataList = getTokenObjectData(suiClient, address, type);
        if (objectDataList == null || objectDataList.isEmpty()) {
            return null;
        }

        AtomicReference<BigInteger> max = new AtomicReference<>(BigInteger.ZERO);
        AtomicReference<ObjectData> maxObject = new AtomicReference<>();
        objectDataList.forEach(objectData -> {
            // find ObjectData which has max balance
            BigInteger balance = getBalance(objectData);
            BigInteger maxBalance = max.get() == null ? BigInteger.ZERO : max.get();
            if (balance.compareTo(maxBalance) > 0) {
                max.set(balance);
                maxObject.set(objectData);
            }
        });
        return maxObject.get();
    }

    /**
     * get balance from objectData
     * @param objectData
     * @return
     */
    public static BigInteger getBalance(ObjectData objectData) {
        if (objectData == null ||
                objectData.getContent() == null ||
                !(objectData.getContent() instanceof MoveObject moveObject)) {
            return BigInteger.ZERO;
        }
        if (moveObject.getFields() instanceof MoveStructMap moveStructMap) {
            MoveValue balanceValue = moveStructMap.getValues().get(BALANCE);

            if (balanceValue != null && balanceValue.getValue() != null) {
                try {
                    String balanceStr = balanceValue.getValue().toString();
                    return new BigInteger(balanceStr);
                } catch (NumberFormatException e) {
                    return BigInteger.ZERO;
                }
            }
        }
        return BigInteger.ZERO;
    }

}
