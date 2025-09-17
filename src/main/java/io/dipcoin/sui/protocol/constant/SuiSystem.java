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

package io.dipcoin.sui.protocol.constant;

import io.dipcoin.sui.util.ObjectIdUtil;

import java.math.BigInteger;

/**
 * @author : Same
 * @datetime : 2025/7/16 14:48
 * @Description : Sui system constants
 */
public interface SuiSystem {

    int SUI_DECIMALS = 9;
    BigInteger MIST_PER_SUI = BigInteger.valueOf(1000000000L);

    String SUI = "0x2::coin::Coin<0x2::sui::SUI>";

    String MOVE_STDLIB_ADDRESS = "0x1";
    String SUI_FRAMEWORK_ADDRESS = "0x2";
    String SUI_SYSTEM_ADDRESS = "0x3";
    String SUI_CLOCK_OBJECT_ID = ObjectIdUtil.normalizeSuiAddress("0x6");
    String SUI_SYSTEM_MODULE_NAME = "sui_system";
    String SUI_TYPE_ARG = SUI_FRAMEWORK_ADDRESS + "::sui::SUI";
    String SUI_SYSTEM_STATE_OBJECT_ID = ObjectIdUtil.normalizeSuiAddress("0x5");

}
