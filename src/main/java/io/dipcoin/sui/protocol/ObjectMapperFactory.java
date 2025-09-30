/*
 * Copyright 2019 Web3 Labs Ltd.
 * Copyright 2025 Dipcoin LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modifications:
 * - Adapted for Sui RPC calls by Dipcoin LLC, 2025
 */

package io.dipcoin.sui.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.dipcoin.sui.model.move.SuiMoveNormalizedModule;
import io.dipcoin.sui.model.move.kind.*;
import io.dipcoin.sui.model.object.kind.InputObjectKind;
import io.dipcoin.sui.model.object.kind.ObjectChange;
import io.dipcoin.sui.model.object.kind.ObjectResponseError;
import io.dipcoin.sui.model.object.kind.Owner;
import io.dipcoin.sui.model.sui.kind.SuiArgument;
import io.dipcoin.sui.model.sui.kind.SuiCallArg;
import io.dipcoin.sui.model.transaction.kind.ExecutionStatus;
import io.dipcoin.sui.model.transaction.kind.SuiEndOfEpochTransactionKind;
import io.dipcoin.sui.model.transaction.kind.SuiTransaction;
import io.dipcoin.sui.model.transaction.kind.TransactionBlockKind;
import io.dipcoin.sui.protocol.deserializer.*;
import io.dipcoin.sui.protocol.serializer.BigIntegerToStringSerializer;
import io.dipcoin.sui.protocol.serializer.ByteArrayToU8ListSerializer;

import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author : Same
 * @datetime : 2025/6/25 19:25
 * @Description : Factory for managing our ObjectMapper instances.
 */
public class ObjectMapperFactory {

    // Use double-checked locking to ensure thread safety
    private static volatile ObjectMapper DEFAULT_OBJECT_MAPPER;

    // Deserializer cache (Key: transaction type kind, Value: deserializer instance)
    private static final ConcurrentMap<String, JsonDeserializer<?>> DESERIALIZER_CACHE =
            new ConcurrentHashMap<>();

    static {
        initializeMappers();
    }

    private static void initializeMappers() {
        // Initialize defaultMapper (lazy loading)
        if (DEFAULT_OBJECT_MAPPER == null) {
            synchronized (ObjectMapperFactory.class) {
                if (DEFAULT_OBJECT_MAPPER == null) {
                    DEFAULT_OBJECT_MAPPER = createBaseMapper();
                }
            }
        }
    }

    public static ObjectMapper getObjectMapper() {
        return DEFAULT_OBJECT_MAPPER;
    }

    public static ObjectReader getObjectReader() {
        return DEFAULT_OBJECT_MAPPER.reader();
    }

    private static ObjectMapper createBaseMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Basic configuration
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                // handle [] to null
                .configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // Register Sui-specific modules (including cached deserializer)
        mapper.registerModule(new SimpleModule("SuiModule")
                // byte[]
                .addSerializer(byte[].class, new ByteArrayToU8ListSerializer())

                // BigInteger
                .addSerializer(BigInteger.class, new BigIntegerToStringSerializer())
                .addDeserializer(BigInteger.class, new StringToBigIntegerDeserializer())

                // Handle JSON parsing for various data types like field, object
                .addDeserializer(TransactionBlockKind.class, new TransactionBlockKindDeserializer())
                .addDeserializer(SuiEndOfEpochTransactionKind.class, new SuiEndOfEpochTransactionKindDeserializer())
                .addDeserializer(SuiArgument.class, new SuiArgumentDeserializer())
                .addDeserializer(SuiCallArg.class, new SuiCallArgDeserializer())
                .addDeserializer(ObjectChange.class, new ObjectChangeDeserializer())
                .addDeserializer(Owner.class, new OwnerDeserializer())
                .addDeserializer(ExecutionStatus.class, new ExecutionStatusDeserializer())
                .addDeserializer(InputObjectKind.class, new InputObjectKindDeserializer())
                .addDeserializer(MoveFunctionArgType.class, new MoveFunctionArgTypeDeserializer())
                .addDeserializer(SuiMoveNormalizedType.class, new SuiMoveNormalizedTypeDeserializer())
                .addDeserializer(SuiMoveNormalizedModule.class, new SuiMoveNormalizedModuleDeserializer())
                .addDeserializer(MoveStruct.class, new MoveStructDeserializer())
                .addDeserializer(Data.class, new DataDeserializer())
                .addDeserializer(RawData.class, new RawDataDeserializer())
                .addDeserializer(ObjectResponseError.class, new ObjectResponseErrorDeserializer())

                // Process special objects in various array formats such as `['', [obj]]`, `[[obj], '']`, or `[obj, [obj]]`.
                .addDeserializer(SuiTransaction.class, new SuiTransactionDeserializer())
                .addDeserializer(MoveValue.class, new MoveValueDeserializer())

                // Process the special single object in the format `{ objectName: "field" }`.
//                .addDeserializer(AddressOwner.class, new AddressOwnerDeserializer())
//                .addDeserializer(ObjectOwner.class, new ObjectOwnerDeserializer())
        );

        // Performance optimization configuration
        mapper.enable(JsonParser.Feature.USE_FAST_DOUBLE_PARSER)
                .enable(JsonParser.Feature.USE_FAST_BIG_NUMBER_PARSER)
                .disable(MapperFeature.AUTO_DETECT_CREATORS);

        return mapper;
    }

}
