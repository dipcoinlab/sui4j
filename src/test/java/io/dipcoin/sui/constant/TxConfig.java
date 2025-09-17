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

import java.math.BigInteger;

/**
 * @author : Same
 * @datetime : 2025/7/5 17:12
 * @Description : transaction basic constants
 */
public interface TxConfig {

    /**
     * gas limit, default 0.1 SUI
     * prod environment configuration 2 SUI
     */
    BigInteger GAS_BUDGET = new BigInteger("100000000");

}
