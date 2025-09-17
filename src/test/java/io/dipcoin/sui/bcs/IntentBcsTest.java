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

import io.dipcoin.sui.constant.WalletKeyPair;
import io.dipcoin.sui.constant.model.SuiWallet;
import io.dipcoin.sui.crypto.Ed25519KeyPair;
import io.dipcoin.sui.crypto.SuiKeyPair;
import io.dipcoin.sui.model.Request;
import io.dipcoin.sui.model.transaction.TransactionBlockBytes;
import io.dipcoin.sui.protocol.SuiClientTest;
import io.dipcoin.sui.protocol.http.request.UnsafePaySui;
import io.dipcoin.sui.protocol.http.response.TransactionBlockBytesWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * @author : Same
 * @datetime : 2025/7/12 16:55
 * @Description : Intent unit test, test.*related serialization functionality
 */
@Slf4j
public class IntentBcsTest extends SuiClientTest {

    // Test with a fixed key (Base64-encoded 32-byte private key).
    private static final String KEY =
            "f60a65eb1a75838674ecc613a0c8d28ab49635cc2bd7697f1b0019193dc2f148";

    @Test
    void testTransactionDataMessage() throws IOException {
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
        String txBytes = result.getTxBytes();

        String signTransactionDataMessage = operatorKeyPair.signTransactionDataBase64(txBytes);
        log.info("TransactionDataMessage: {}\n -> {}", txBytes, signTransactionDataMessage);
    }

    @Test
    void testPersonalMessage() throws IOException {
//        String message = "123";
        String message = "000000000000006c6b935b8bbd4000000000000000000000016345785d8a000000000000000000008ac7230489e80000000000000000000000000197e39b9d70000000000000000032c42fef490b188931789797feb1125034ec8118a5d15ef927b9b60e07e2ca1e3a19c6f9b313c4de503b498a3f519462710c2ff8783a727c6db984735c1dbc8518446970436f696e";
        Ed25519KeyPair keyPair = Ed25519KeyPair.decodeHex(KEY);
        String signPersonalMessage = keyPair.signPersonalMessageBase64(message);
        log.info("PersonalMessage: {}\n -> {}", message, signPersonalMessage);
    }
}
