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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.dipcoin.sui.bcs.BcsRegistry;
import io.dipcoin.sui.bcs.types.arg.call.CallArgObjectArg;
import io.dipcoin.sui.bcs.types.arg.object.ObjectArgImmOrOwnedObject;
import io.dipcoin.sui.bcs.types.arg.object.ObjectArgSharedObject;
import io.dipcoin.sui.bcs.types.arg.object.SharedObjectRef;
import io.dipcoin.sui.bcs.types.gas.GasData;
import io.dipcoin.sui.bcs.types.gas.SuiObjectRef;
import io.dipcoin.sui.bcs.types.transaction.*;
import io.dipcoin.sui.crypto.SuiKeyPair;
import io.dipcoin.sui.crypto.signature.SignatureScheme;
import io.dipcoin.sui.model.Request;
import io.dipcoin.sui.model.object.ObjectData;
import io.dipcoin.sui.model.object.kind.owner.Shared;
import io.dipcoin.sui.model.transaction.SuiTransactionBlockResponse;
import io.dipcoin.sui.model.transaction.Transaction;
import io.dipcoin.sui.model.transaction.TransactionBlockResponseOptions;
import io.dipcoin.sui.protocol.SuiClient;
import io.dipcoin.sui.protocol.exceptions.RpcRequestFailedException;
import io.dipcoin.sui.protocol.http.response.SuiTransactionBlockResponseWrapper;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

/**
 * @author : Same
 * @datetime : 2025/6/27 10:05
 * @Description : Transaction builder
 */
public class TransactionBuilder {

    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);

    /**
     * Build SharedObject
     * @param suiClient
     * @param objectId
     * @param mutable
     * @return
     */
    public static CallArgObjectArg buildSharedObject(SuiClient suiClient, String objectId, boolean mutable) {
        ObjectData objectData = QueryBuilder.getObjectData(suiClient, objectId);
        return new CallArgObjectArg(new ObjectArgSharedObject(new SharedObjectRef(
                objectId, ((Shared)objectData.getOwner()).getInitialSharedVersion().longValue(), mutable)));
    }

    /**
     * Build SharedObject
     * @param objectId
     * @param version
     * @param mutable
     * @return
     */
    public static CallArgObjectArg buildSharedObject(String objectId, long version, boolean mutable) {
        return new CallArgObjectArg(new ObjectArgSharedObject(new SharedObjectRef(
                objectId, version, mutable)));
    }

    /**
     * Build ImmOrOwnedObject
     * @param objectId
     * @param version
     * @param digest
     * @return
     */
    public static CallArgObjectArg buildImmOrOwnedObject(String objectId, long version, String digest) {
        return new CallArgObjectArg(new ObjectArgImmOrOwnedObject(new SuiObjectRef(
                objectId, version, digest)));
    }

    /**
     * Build ImmOrOwnedObject
     * @param suiClient
     * @param objectId
     * @return
     */
    public static CallArgObjectArg buildImmOrOwnedObject(SuiClient suiClient, String objectId) {
        ObjectData objectData = QueryBuilder.getObjectData(suiClient, objectId);
        return new CallArgObjectArg(new ObjectArgImmOrOwnedObject(new SuiObjectRef(
                objectId, objectData.getVersion().longValue(), objectData.getDigest())));
    }

    /**
     * Build GasData single gas
     * @param gasObjectId
     * @param sender
     * @param gasPrice
     * @param gasBudget
     * @return
     */
    public static GasData buildGasData(SuiClient suiClient, String gasObjectId, String sender, long gasPrice, BigInteger gasBudget) {
        ObjectData objectData = QueryBuilder.getObjectData(suiClient, gasObjectId);
        SuiObjectRef suiObjectRef = new SuiObjectRef(gasObjectId, objectData.getVersion().longValue(), objectData.getDigest());
        return new GasData(List.of(suiObjectRef), sender, gasPrice, gasBudget);
    }

    /**
     * Build GasData single gas
     * @param gasObjectId
     * @param version
     * @param digest
     * @param sender
     * @param gasPrice
     * @param gasBudget
     * @return
     */
    public static GasData buildGasData(String gasObjectId, long version, String digest, String sender, long gasPrice, BigInteger gasBudget) {
        SuiObjectRef suiObjectRef = new SuiObjectRef(gasObjectId, version, digest);
        return new GasData(List.of(suiObjectRef), sender, gasPrice, gasBudget);
    }

    /**
     * Build GasData single gas
     * @param suiObjectRef
     * @param sender
     * @param gasPrice
     * @param gasBudget
     * @return
     */
    public static GasData buildGasData(SuiObjectRef suiObjectRef, String sender, long gasPrice, BigInteger gasBudget) {
        return new GasData(List.of(suiObjectRef), sender, gasPrice, gasBudget);
    }

    /**
     * Build GasData multiple gas
     * @param gasObjectRefList
     * @param sender
     * @param gasPrice
     * @param gasBudget
     * @return
     */
    public static GasData buildGasData(List<SuiObjectRef> gasObjectRefList, String sender, long gasPrice, BigInteger gasBudget) {
        return new GasData(gasObjectRefList, sender, gasPrice, gasBudget);
    }

    /**
     * Build GasData multiple gas
     * @param suiClient
     * @param sender
     * @param gasPrice
     * @param gasBudget
     * @return
     */
    public static GasData buildGasData(SuiClient suiClient, String sender, long gasPrice, BigInteger gasBudget) {
        List<SuiObjectRef> suiObjectRefs = CoinWithBalance.getMergeGas(suiClient, sender, gasBudget);
        return new GasData(suiObjectRefs, sender, gasPrice, gasBudget);
    }

    /**
     * Build GasData multiple gas
     * @param suiClient
     * @param sender
     * @param gasPrice
     * @param gasBudget
     * @param txSuiUse
     * @return
     */
    public static GasData buildGasData(SuiClient suiClient, String sender, long gasPrice, BigInteger gasBudget, BigInteger txSuiUse) {
        List<SuiObjectRef> suiObjectRefs = CoinWithBalance.getMergeGas(suiClient, sender, gasBudget.add(txSuiUse));
        return new GasData(suiObjectRefs, sender, gasPrice, gasBudget);
    }

    /**
     * Build TransactionData V1 version
     * @param programmableTx
     * @param sender
     * @param gasData
     * @param transactionExpiration
     * @return
     */
    public static TransactionData buildTransactionDataV1(ProgrammableTransaction programmableTx, String sender, GasData gasData, TransactionExpiration transactionExpiration) {
        TransactionKind programmableKind = new TransactionKind.ProgrammableTransaction(programmableTx);
        TransactionDataV1 transactionDataV1 = new TransactionDataV1(
                programmableKind,
                sender,
                gasData,
                transactionExpiration
        );
        return new TransactionData.V1(transactionDataV1);
    }

    /**
     * Build txBytes (with expiration epoch)
     * @param programmableTx
     * @param sender
     * @param gasData
     * @param epoch
     * @return txBytes
     * @throws IOException
     */
    public static String serializeTransactionBytes(ProgrammableTransaction programmableTx, String sender, GasData gasData, long epoch) throws IOException {
        TransactionData transactionData = buildTransactionDataV1(programmableTx, sender, gasData, new TransactionExpiration.Epoch(epoch));
        return BcsRegistry.serializeToBase64(transactionData, BcsRegistry.TRANSACTION_DATA_SERIALIZER);
    }

    /**
     * Build txBytes
     * @param programmableTx
     * @param sender
     * @param gasData
     * @return txBytes
     * @throws IOException
     */
    public static String serializeTransactionBytes(ProgrammableTransaction programmableTx, String sender, GasData gasData) throws IOException {
        TransactionData transactionData = buildTransactionDataV1(programmableTx, sender, gasData, TransactionExpiration.None.INSTANCE);
        return BcsRegistry.serializeToBase64(transactionData, BcsRegistry.TRANSACTION_DATA_SERIALIZER);
    }

    /**
     * Build tx
     * @param programmableTx
     * @param sender
     * @param gasData
     * @return txBytes
     * @throws IOException
     */
    public static Transaction buildTransaction(ProgrammableTransaction programmableTx, String sender, GasData gasData, SuiKeyPair suiKeyPair) throws IOException {
        return buildTransaction(serializeTransactionBytes(programmableTx, sender, gasData), suiKeyPair);
    }

    /**
     * Build tx
     * @param txBytes
     * @param key
     * @param signatureScheme
     * @return
     */
    public static Transaction buildTransaction(String txBytes, String key, SignatureScheme signatureScheme) {
        return buildTransaction(txBytes, SuiKeyPair.decodeHex(key, signatureScheme));
    }

    /**
     * Build tx
     * @param txBytes
     * @param suiKeyPair
     * @return
     */
    public static Transaction buildTransaction(String txBytes, SuiKeyPair suiKeyPair) {
        String sign = suiKeyPair.signTransactionDataBase64(txBytes);
        return buildTransaction(txBytes, List.of(sign));
    }

    /**
     * Build tx
     * @param txBytes
     * @param suiKeyPair
     * @return
     */
    public static Transaction buildTransaction(String txBytes, SuiKeyPair suiKeyPair, TransactionBlockResponseOptions options) {
        String sign = suiKeyPair.signTransactionDataBase64(txBytes);
        return buildTransaction(txBytes, List.of(sign), options);
    }

    /**
     * Build tx
     * @param txBytes
     * @param signature
     * @return
     */
    public static Transaction buildTransaction(String txBytes, String signature) {
        return buildTransaction(txBytes, List.of(signature));
    }

    /**
     * Build tx
     * @param txBytes
     * @param signature
     * @param options
     * @return
     */
    public static Transaction buildTransaction(String txBytes, String signature, TransactionBlockResponseOptions options) {
        return buildTransaction(txBytes, List.of(signature), options);
    }

    /**
     * Build tx
     * @param txBytes
     * @param signatures
     * @return
     */
    public static Transaction buildTransaction(String txBytes, List<String> signatures) {
        // Build Transaction object
        Transaction transaction = new Transaction();
        transaction.setTxBytes(txBytes);
        transaction.setSignatures(signatures);
        return transaction;
    }

    /**
     * Build tx with custom return parameters
     * @param txBytes
     * @param signatures
     * @return
     */
    public static Transaction buildTransaction(String txBytes, List<String> signatures, TransactionBlockResponseOptions options) {
        // Build Transaction object
        Transaction transaction = new Transaction();
        transaction.setTxBytes(txBytes);
        transaction.setSignatures(signatures);
        transaction.setOptions(options);
        return transaction;
    }

    /**
     * Send tx with custom return parameters
     * @param txBytes
     * @param signatures
     * @return
     */
    public static SuiTransactionBlockResponse sendTransaction(SuiClient suiClient, String txBytes, List<String> signatures, TransactionBlockResponseOptions options) throws IOException {
        if (txBytes == null || txBytes.isEmpty()) {
            throw new IllegalArgumentException("Send tx error, txBytes is empty!");
        }

        Transaction transaction = TransactionBuilder.buildTransaction(txBytes, signatures, options);
        Request<?, SuiTransactionBlockResponseWrapper> tx = suiClient.executeTransactionBlock(transaction);
        SuiTransactionBlockResponseWrapper send = tx.send();
        return send.getResult();
    }

    /**
     * Send tx with custom return parameters
     * @param suiClient
     * @param programmableTx
     * @param suiKeyPair
     * @param gasData
     * @return
     * @throws IOException
     */
    public static SuiTransactionBlockResponse sendTransaction(SuiClient suiClient, ProgrammableTransaction programmableTx, SuiKeyPair suiKeyPair, GasData gasData) throws IOException {
        String address = suiKeyPair.address();
        String txBytes;
        try {
            txBytes = TransactionBuilder.serializeTransactionBytes(programmableTx, address, gasData);
        } catch (IOException e) {
            throw new RpcRequestFailedException("Send tx failed! Cause : " + e.getMessage());
        }
        Transaction transaction = TransactionBuilder.buildTransaction(txBytes, suiKeyPair);
        Request<?, SuiTransactionBlockResponseWrapper> tx = suiClient.executeTransactionBlock(transaction);
        SuiTransactionBlockResponseWrapper send = tx.send();
        return send.getResult();
    }

    /**
     * Send tx with custom return parameters
     * @param suiClient
     * @param programmableTx
     * @param suiKeyPair
     * @param gasData
     * @return
     * @throws IOException
     */
    public static SuiTransactionBlockResponse sendTransaction(SuiClient suiClient, ProgrammableTransaction programmableTx, SuiKeyPair suiKeyPair, GasData gasData, TransactionBlockResponseOptions options) throws IOException {
        String address = suiKeyPair.address();
        String txBytes;
        try {
            txBytes = TransactionBuilder.serializeTransactionBytes(programmableTx, address, gasData);
        } catch (IOException e) {
            throw new RpcRequestFailedException("Send tx failed! Cause : " + e.getMessage());
        }
        Transaction transaction = TransactionBuilder.buildTransaction(txBytes, suiKeyPair, options);
        Request<?, SuiTransactionBlockResponseWrapper> tx = suiClient.executeTransactionBlock(transaction);
        SuiTransactionBlockResponseWrapper send = tx.send();
        return send.getResult();
    }

    /**
     * Send tx with custom return parameters
     * @param suiClient
     * @param programmableTx
     * @param suiKeyPair
     * @param gasPrice
     * @param gasBudget
     * @return
     * @throws IOException
     */
    public static SuiTransactionBlockResponse sendTransaction(SuiClient suiClient, ProgrammableTransaction programmableTx, SuiKeyPair suiKeyPair, long gasPrice, BigInteger gasBudget) throws IOException {
        String address = suiKeyPair.address();
        String txBytes;
        try {
            txBytes = TransactionBuilder.serializeTransactionBytes(programmableTx, address, buildGasData(suiClient, address, gasPrice, gasBudget));
        } catch (IOException e) {
            throw new RpcRequestFailedException("Send tx failed! Cause : " + e.getMessage());
        }
        Transaction transaction = TransactionBuilder.buildTransaction(txBytes, suiKeyPair);
        Request<?, SuiTransactionBlockResponseWrapper> tx = suiClient.executeTransactionBlock(transaction);
        SuiTransactionBlockResponseWrapper send = tx.send();
        return send.getResult();
    }

    /**
     * Send tx with custom return parameters
     * @param suiClient
     * @param programmableTx
     * @param suiKeyPair
     * @param gasPrice
     * @param gasBudget
     * @param options
     * @return
     * @throws IOException
     */
    public static SuiTransactionBlockResponse sendTransaction(SuiClient suiClient, ProgrammableTransaction programmableTx, SuiKeyPair suiKeyPair, long gasPrice, BigInteger gasBudget, TransactionBlockResponseOptions options) throws IOException {
        String address = suiKeyPair.address();
        String txBytes;
        try {
            txBytes = TransactionBuilder.serializeTransactionBytes(programmableTx, address, buildGasData(suiClient, address, gasPrice, gasBudget));
        } catch (IOException e) {
            throw new RpcRequestFailedException("Send tx failed! Cause : " + e.getMessage());
        }
        Transaction transaction = TransactionBuilder.buildTransaction(txBytes, suiKeyPair, options);
        Request<?, SuiTransactionBlockResponseWrapper> tx = suiClient.executeTransactionBlock(transaction);
        SuiTransactionBlockResponseWrapper send = tx.send();
        return send.getResult();
    }

    /**
     * Send tx with custom return parameters
     * @param txBytes
     * @param signatures
     * @return
     */
    public static SuiTransactionBlockResponse sendTransaction(SuiClient suiClient, String txBytes, List<String> signatures) throws IOException {
        if (txBytes == null || txBytes.isEmpty()) {
            throw new IllegalArgumentException("Send tx error, txBytes is empty!");
        }

        Transaction transaction = TransactionBuilder.buildTransaction(txBytes, signatures);
        Request<?, SuiTransactionBlockResponseWrapper> tx = suiClient.executeTransactionBlock(transaction);
        SuiTransactionBlockResponseWrapper send = tx.send();
        return send.getResult();
    }
}
