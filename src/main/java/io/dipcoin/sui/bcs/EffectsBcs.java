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

package io.dipcoin.sui.bcs;

import io.dipcoin.sui.bcs.types.effects.ExecutionStatus;
import io.dipcoin.sui.bcs.types.effects.GasCostSummary;
import io.dipcoin.sui.bcs.types.effects.TransactionEffects;
import io.dipcoin.sui.bcs.types.effects.TransactionEffectsV1;
import io.dipcoin.sui.bcs.types.gas.SuiObjectRef;
import io.dipcoin.sui.util.Numeric;
import org.bitcoinj.core.Base58;
import org.bouncycastle.util.encoders.Base64;

import java.io.IOException;
import java.util.List;

/**
 * @author : Same
 * @datetime : 2025/7/11 10:33
 * @Description : Transaction effects BCS serializer, provides BCS serialization functionality for transaction execution results, corresponding to TypeScript effects.ts
 */
public class EffectsBcs {
    
    /**
     * Gas cost summary serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<GasCostSummary> GAS_COST_SUMMARY_SERIALIZER = (serializer, summary) -> {
        serializer.writeU64(summary.getComputationCost());
        serializer.writeU64(summary.getStorageCost());
        serializer.writeU64(summary.getStorageRebate());
        serializer.writeU64(summary.getNonRefundableStorageFee());
    };
    
    /**
     * Transaction effects V1 serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<TransactionEffectsV1> TRANSACTION_EFFECTS_V1_SERIALIZER = (serializer, effects) -> {
        // Serialize execution status
        if (effects.getStatus() == ExecutionStatus.SUCCESS) {
            serializer.writeU32(0); // Success variant
        } else {
            serializer.writeU32(1); // Failed variant
            // Here can add failure status serialization logic
        }
        
        serializer.writeU64(effects.getExecutedEpoch());
        GAS_COST_SUMMARY_SERIALIZER.serialize(serializer, effects.getGasUsed());
        
        // Serialize created objects
        serializer.writeVector(effects.getCreated(), SuiBcs.SUI_OBJECT_REF_SERIALIZER);
        
        // Serialize modified objects
        serializer.writeVector(effects.getMutated(), SuiBcs.SUI_OBJECT_REF_SERIALIZER);
        
        // Serialize deleted objects
        serializer.writeVector(effects.getDeleted(), SuiBcs.SUI_OBJECT_REF_SERIALIZER);
        
        // Serialize transaction digest
        SuiBcs.OBJECT_DIGEST_SERIALIZER.serialize(serializer, effects.getTransactionDigest());
    };
    
    /**
     * Transaction effects serializer
     */
    public static final BcsSerializer.BcsTypeSerializer<TransactionEffects> TRANSACTION_EFFECTS_SERIALIZER = (serializer, effects) -> {
        serializer.writeU32(0); // V1 variant
        TRANSACTION_EFFECTS_V1_SERIALIZER.serialize(serializer, effects.getV1());
    };
    
    /**
     * Serialize transaction effects to Base64
     */
    public static String serializeToBase64(TransactionEffects effects) throws IOException {
        BcsSerializer bcsSerializer = new BcsSerializer();
        TRANSACTION_EFFECTS_SERIALIZER.serialize(bcsSerializer, effects);
        return bcsSerializer.toBase64();
    }
    
    /**
     * Deserialize transaction effects from Base64
     */
    public static TransactionEffects deserializeFromBase64(String base64) throws IOException {
        byte[] data = Base64.decode(base64);
        BcsDeserializer deserializer = new BcsDeserializer(data);
        
        int version = deserializer.readU32();
        if (version == 0) {
            TransactionEffectsV1 v1 = deserializeTransactionEffectsV1(deserializer);
            return new TransactionEffects(v1);
        } else {
            throw new IllegalArgumentException("Unsupported transaction effects version: " + version);
        }
    }
    
    /**
     * Deserialize transaction effects V1
     */
    private static TransactionEffectsV1 deserializeTransactionEffectsV1(BcsDeserializer deserializer) throws IOException {
        // Deserialize execution status
        int statusVariant = deserializer.readU32();
        ExecutionStatus status = statusVariant == 0 ? ExecutionStatus.SUCCESS : ExecutionStatus.FAILED;
        
        long executedEpoch = deserializer.readU64();
        
        // Deserialize gas cost summary
        GasCostSummary gasUsed = deserializeGasCostSummary(deserializer);
        
        // Deserialize created objects
        List<SuiObjectRef> created = deserializer.readVector(d -> {
            try {
                return deserializeSuiObjectRef(d);
            } catch (IOException e) {
                throw new RuntimeException("Failed to deserialize created object", e);
            }
        });
        
        // Deserialize modified objects
        List<SuiObjectRef> mutated = deserializer.readVector(d -> {
            try {
                return deserializeSuiObjectRef(d);
            } catch (IOException e) {
                throw new RuntimeException("Failed to deserialize mutated object", e);
            }
        });
        
        // Deserialize deleted objects
        List<SuiObjectRef> deleted = deserializer.readVector(d -> {
            try {
                return deserializeSuiObjectRef(d);
            } catch (IOException e) {
                throw new RuntimeException("Failed to deserialize deleted object", e);
            }
        });
        
        // Deserialize transaction digest
        String transactionDigest = Base58.encode(deserializer.readBytes());

        return new TransactionEffectsV1(status, executedEpoch, gasUsed, created, mutated, deleted, transactionDigest);
    }
    
    /**
     * Deserialize gas cost summary.
     */
    private static GasCostSummary deserializeGasCostSummary(BcsDeserializer deserializer) throws IOException {
        long computationCost = deserializer.readU64();
        long storageCost = deserializer.readU64();
        long storageRebate = deserializer.readU64();
        long nonRefundableStorageFee = deserializer.readU64();
        
        return new GasCostSummary(computationCost, storageCost, storageRebate, nonRefundableStorageFee);
    }
    
    /**
     * Deserialize SuiObjectRef.
     */
    private static SuiObjectRef deserializeSuiObjectRef(BcsDeserializer deserializer) throws IOException {
        String objectId = Numeric.toHexString(deserializer.readAddress());
        long version = deserializer.readU64();
        String digest = Base64.toBase64String(deserializer.readBytes());
        
        return new SuiObjectRef(objectId, version, digest);
    }
} 