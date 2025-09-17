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

package io.dipcoin.sui.constant;

import io.dipcoin.sui.constant.model.SuiWallet;
import io.dipcoin.sui.crypto.Ed25519KeyPair;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : Same
 * @datetime : 2025/7/5 16:39
 * @Description : wallet constants
 */
public interface WalletKeyPair {

    String OPERATOR = "OPERATOR";

    Map<String, SuiWallet> WALLETS = new HashMap<>(
            Map.ofEntries(
                    // address : 0xe63f4d12bcf145c510f2788f0bf8cf81a7cd5035d5a502d846b9517457973c1d
                    Map.entry(OPERATOR, new SuiWallet()
                            .setKeyPair(Ed25519KeyPair.decodeHex("f60a65eb1a75838674ecc613a0c8d28ab49635cc2bd7697f1b0019193dc2f148"))
                            .setGasObjectId("0xc2a8cb3c544667522fe345f6eac84b14c59f13a017dcbda8f4a56c00ec9da680")
                            .setUsdcObjectId("0xfd7eac164be11c43153f310a807678841a40afa451ce931a48088069c885e65d")
                    )
    ));
}
