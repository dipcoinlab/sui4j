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


import io.dipcoin.sui.bcs.types.gas.SuiObjectRef;
import io.dipcoin.sui.model.coin.Coin;
import io.dipcoin.sui.model.coin.PageForCoinAndString;
import io.dipcoin.sui.protocol.SuiClient;
import io.dipcoin.sui.protocol.constant.SuiSystem;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : Same
 * @datetime : 2025/10/8 12:01
 * @Description :
 */
public class CoinWithBalance {

    /**
     * Get the required amount of gas
     * @param suiClient
     * @param owner
     * @param requiredBalance
     * @return
     */
    public static List<SuiObjectRef> getMergeGas(SuiClient suiClient, String owner, BigInteger requiredBalance) {
        List<Coin> selectedCoins = getCoinsOfType(suiClient, owner, SuiSystem.SUI_TYPE, requiredBalance);
        List<SuiObjectRef> gasList = new ArrayList<>(selectedCoins.size());
        for (Coin coin : selectedCoins) {
            gasList.add(new SuiObjectRef(coin.getCoinObjectId(), coin.getVersion(), coin.getDigest()));
        }
        return gasList;
    }

    /**
     * Get the target amount of coins
     * @param suiClient
     * @param owner
     * @param type
     * @param requiredBalance
     * @return
     */
    public static List<Coin> getCoinsOfType(SuiClient suiClient, String owner, String type, BigInteger requiredBalance) {
        List<Coin> selectedCoins = new ArrayList<>();
        BigInteger remaining = requiredBalance;
        String cursor = null;

        while (true) {
            PageForCoinAndString response = QueryBuilder.getCoins(suiClient, owner, type, cursor);
            List<Coin> coinList = response.getData();
            for (Coin coin : coinList) {
                selectedCoins.add(coin);
                remaining = remaining.subtract(coin.getBalance());
                if (remaining.compareTo(BigInteger.ZERO) <= 0) {
                    return selectedCoins;
                }
            }
            if (!response.getHasNextPage()) break;
            cursor = response.getNextCursor();
        }

        throw new IllegalStateException("Not enough coins of type " + type + " to satisfy requested balance");
    }
}
