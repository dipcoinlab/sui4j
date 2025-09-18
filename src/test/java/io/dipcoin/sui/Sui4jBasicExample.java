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

package io.dipcoin.sui;

import io.dipcoin.sui.bcs.types.gas.GasData;
import io.dipcoin.sui.bcs.types.gas.SuiObjectRef;
import io.dipcoin.sui.bcs.types.transaction.Argument;
import io.dipcoin.sui.bcs.types.transaction.Command;
import io.dipcoin.sui.bcs.types.transaction.ProgrammableMoveCall;
import io.dipcoin.sui.bcs.types.transaction.ProgrammableTransaction;
import io.dipcoin.sui.client.TransactionBuilder;
import io.dipcoin.sui.crypto.Ed25519KeyPair;
import io.dipcoin.sui.crypto.SuiKeyPair;
import io.dipcoin.sui.protocol.SuiClient;
import io.dipcoin.sui.protocol.http.HttpService;
import io.dipcoin.sui.pyth.core.PythClient;
import io.dipcoin.sui.pyth.model.PythNetwork;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author : Same
 * @datetime : 2025/9/18 16:37
 * @Description : Sui4jBasicExample
 */
public class Sui4jBasicExample {

    public static void main(String[] args) throws IOException {
        // 1. Create HTTP service instance
        HttpService httpService = new HttpService("https://fullnode.testnet.sui.io:443");

        // 2. Build Sui client
        SuiClient suiClient = SuiClient.build(httpService);

        // 3. Create Pyth client
        PythClient pythClient = new PythClient(suiClient);

        // 4. Generate Ed25519 key pair
        SuiKeyPair keyPair = Ed25519KeyPair.generate();
        System.out.println("Generated address: " + keyPair.address());
        System.out.println("Private key: " + keyPair.encodePrivateKey());

        // 5. Prepare Pyth price update parameters
        String feedId = "0xf9c0172ba10dfa4d19088d94f5bf61d3b54d5bd7483a322a982e1373ee8ea31b";

        // 6. Build PTB update price transaction
        ProgrammableTransaction programmableTx = pythClient.updatePrice(feedId, PythNetwork.TESTNET);

        // 7. Add custom Move call (optional)
        ProgrammableMoveCall moveCall = new ProgrammableMoveCall(
                "0xb5f4bf7d29a8e82eb5de521cf556f1953c9fdd3d73e7f371a156c6e1ee64f24b",
                "main",
                "use_pyth_price",
                new ArrayList<>(),
                Arrays.asList(Argument.ofInput(2), Argument.ofInput(6))
        );

        Command useUpdateMoveCall = new Command.MoveCall(moveCall);
        programmableTx.addCommand(useUpdateMoveCall);

        // 8. Prepare Gas data (actual Gas object needed here)
        SuiObjectRef gasObject = new SuiObjectRef(
                "0x0b50fe6d7b86730f0f8d2e389d22d63c30a73a1034720a8a43bb5e322a9588e1",
                500452832L,
                "FZSJfo8uZMLjeppm4XSd1peiEnox8FYxMxPbZVmuUjqw"
        );

        GasData gasData = new GasData(
                Arrays.asList(gasObject),
                keyPair.address(),
                1000L,
                BigInteger.valueOf(10000000L)
        );

        // 9. Serialize transaction (using local BCS encoding)
        String transactionDataBase64 = TransactionBuilder.serializeTransactionBytes(
                programmableTx,
                keyPair.address(),
                gasData
        );

        System.out.println("Serialized transaction data: " + transactionDataBase64);

        // 10. Execute transaction (optional, requires actual Gas object)
        // suiClient.executeTransactionBlock(transactionDataBase64, keyPair);

        System.out.println("Pyth price update PTB transaction construction completed!");
    }
}
