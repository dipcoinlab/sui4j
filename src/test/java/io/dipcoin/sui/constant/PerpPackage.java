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


import io.dipcoin.sui.constant.perp.PackageConfig;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : Same
 * @datetime : 2025/7/5 17:16
 * @Description : perp package constants
 */
public interface PerpPackage {

    String DEPOSIT = "DEPOSIT";

    String TRADE = "TRADE";

    /**
     * packageObjectId
     */
    String OBJECT_ID = "0x0114b1d4656ac42a9523da1c7241f0291918f9517fd30f3e6e84b9fd5b3e3730";

    /**
     * typeTag
     */
    List<String> TYPE_TAG = Arrays.asList("0x1f2788918b609959c9052a1f00c49765752acb24d99997a102903be7da18dd0d::coin::COIN");

    /**
     * protocol
     */
    String TRADE_PROJECT_CONFIG_ID = "0x05a630c36e8a6cb9ff99e2d2595e55ec70d002a8069a90c2d1bac0bfa12271fa";

    /**
     * tx_indexer
     */
    String TX_INDEXER_ID = "0xaed1352c3f6f2a44fd521350f53a98f675d4b07cc36916607eae24c2650a9cb9";

    /**
     * bank
     */
    String BANK_ID = "0x16be93006a3ced6fa2dde428c9b8418b4986efd4abc6980d7f4367bbfd638353";

    // ----------------- Object List -----------------

    /**
     * ETH trading pair
     */
    String ETH_PERP = "ETH-PERP";

    /**
     * BTC trading pair
     */
    String BTC_PERP = "BTC-PERP";

    /**
     * package configuration
     */
    Map<String, PackageConfig> MOVE_CALL = new HashMap<>(Map.ofEntries(
            Map.entry(DEPOSIT, new PackageConfig()
                    .setModule("bank")
                    .setFunction("deposit")),
            Map.entry(TRADE, new PackageConfig()
                    .setModule("exchange")
                    .setFunction("trade"))

    ));
}
