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

import io.dipcoin.sui.bcs.BcsIndexTest;
import io.dipcoin.sui.bcs.types.gas.GasData;
import io.dipcoin.sui.bcs.types.transaction.Argument;
import io.dipcoin.sui.bcs.types.transaction.Command;
import io.dipcoin.sui.bcs.types.transaction.ProgrammableMoveCall;
import io.dipcoin.sui.bcs.types.transaction.ProgrammableTransaction;
import io.dipcoin.sui.client.TransactionBuilder;
import io.dipcoin.sui.constant.TxConfig;
import io.dipcoin.sui.constant.WalletKeyPair;
import io.dipcoin.sui.constant.model.SuiWallet;
import io.dipcoin.sui.crypto.SuiKeyPair;
import io.dipcoin.sui.model.Request;
import io.dipcoin.sui.model.object.ObjectData;
import io.dipcoin.sui.model.object.ObjectDataOptions;
import io.dipcoin.sui.model.object.SuiObjectResponse;
import io.dipcoin.sui.protocol.http.request.GetObject;
import io.dipcoin.sui.protocol.http.response.SuiObjectResponseWrapper;
import io.dipcoin.sui.pyth.model.PythNetwork;
import io.dipcoin.sui.pyth.model.PythResponse;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author : Same
 * @datetime : 2025/7/17 18:16
 * @Description : Pyth client unit tests for testnet data.
 */
@Slf4j
public class PythClientTest extends BcsIndexTest {

    @Test
    void testGetDynamicFieldObject() throws IOException {
        String feedId = "0x50c67b3fd225db8912a424dd4baed60ffdde625ed2feaaf283724f9608fea266";
        PythResponse lastPrice = pythClient.getLastPrice(feedId, PythNetwork.TESTNET.getConfig());
        log.info("response: {}", lastPrice);

        // verify result type
        assertThat(lastPrice)
                .isInstanceOf(PythResponse.class);
    }

    @Test
    void testPtbUpdatePrice() throws IOException {
        log.info("test updatePrice PTB on chain");

        // ----------------------- operator trade -----------------------
        SuiWallet operator = WalletKeyPair.WALLETS.get(WalletKeyPair.OPERATOR);
        SuiKeyPair operatorKeyPair = operator.getKeyPair();
        String feedId = "0xf9c0172ba10dfa4d19088d94f5bf61d3b54d5bd7483a322a982e1373ee8ea31b";

        // ProgrammableMoveCall
        ProgrammableMoveCall moveCall = new ProgrammableMoveCall(
                "0xb5f4bf7d29a8e82eb5de521cf556f1953c9fdd3d73e7f371a156c6e1ee64f24b",
                "main",
                "use_pyth_price",
                new ArrayList<>(),
                Arrays.asList(Argument.ofInput(2),
                        Argument.ofInput(6)
                ));

        // Command
        Command useUpdateMoveCall = new Command.MoveCall(moveCall);

        // ProgrammableTransaction
        ProgrammableTransaction programmableTx = pythClient.updatePrice(feedId, PythNetwork.TESTNET);
        programmableTx.addCommand(useUpdateMoveCall);

        // TransactionData
        String transactionDataBase64 = TransactionBuilder.serializeTransactionBytes(programmableTx, operatorKeyPair.address(), this.getGasData(operator));
        log.info("\ntransactionDataBase64: {}", transactionDataBase64);
        log.info("\nbinary:\n{}", Arrays.toString(super.trans(Base64.decode(transactionDataBase64))));

        assertNotNull(transactionDataBase64);
        assertTrue(transactionDataBase64.length() > 0);

        // 4. on chain
        if (ENABLE_SEND) {
//        if (true) {
            this.testExecuteTransactionBlock(transactionDataBase64, operatorKeyPair);
        }
    }

    /**
     * get gasData
     * @param suiWallet
     * @return
     * @throws IOException
     */
    private GasData getGasData(SuiWallet suiWallet) throws IOException {
        SuiKeyPair operatorKeyPair = suiWallet.getKeyPair();

        // get gas
        GetObject data = new GetObject();
        data.setObjectId(suiWallet.getGasObjectId());
        data.setOptions(ObjectDataOptions.allFalse());

        Request<?, SuiObjectResponseWrapper> request = suiClient.getObject(data);
        SuiObjectResponseWrapper response = request.send();
        SuiObjectResponse result = response.getResult();
        ObjectData resultData = result.getData();
        // Create test gas data.
        return TransactionBuilder.buildGasData(
                suiWallet.getGasObjectId(),
                resultData.getVersion().longValue(),
                resultData.getDigest(),
                operatorKeyPair.address(),
                1000L,
                TxConfig.GAS_BUDGET
                );
    }
}
