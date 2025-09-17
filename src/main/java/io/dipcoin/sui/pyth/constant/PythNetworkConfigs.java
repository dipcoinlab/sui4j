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

package io.dipcoin.sui.pyth.constant;

import io.dipcoin.sui.pyth.model.PythNetworkConfig;

/**
 * @author : Same
 * @datetime : 2025/7/16 14:13
 * @Description : Pyth network configuration constant
 */
public final class PythNetworkConfigs {

    public final static String GET_LAST_PRICE_FEEDS = "/v2/updates/price/latest?encoding=base64&parsed=true&ids[]=";

    private PythNetworkConfigs() {}

    public static final PythNetworkConfig MAINNET_CONFIG = new PythNetworkConfig(
            "https://sui-mainnet-endpoint.blockvision.org",
            "0x04e20ddf36af412a4096f9014f4a565af9e812db9a05cc40254846cf6ed0ad91", // pythPackageId
            "0x1f9310238ee9298fb703c3419030b35b22bb1cc37113e3bb5007c99aec79e5b8", // pythStateId
            "0x5306f64e312b581766351c07af79c72fcb1cd25147157fdc2f8ad76de9a3fb6a", // wormholePackageId
            "0xaeab97f96cf9877fee2883315d459552b2b921edc16d7ceac6eab944dd88919c", // wormholeStateId
            "0x14b4697477d24c30c8eecc31dd1bd49a3115a9fe0db6bd4fd570cf14640b79a0", // priceFeedTableId
            "https://hermes.pyth.network",
            "./mnemonic",
            "./price-config.stable.sample.yaml"
    );

    public static final PythNetworkConfig TESTNET_CONFIG = new PythNetworkConfig(
            "https://sui-testnet-rpc.allthatnode.com",
            "0xabf837e98c26087cba0883c0a7a28326b1fa3c5e1e2c5abdb486f9e8f594c837", // pythPackageId
            "0x243759059f4c3111179da5878c12f68d612c21a8d54d85edc86164bb18be1c7c", // pythStateId
            "0x21473617f3565d704aa67be73ea41243e9e34a42d434c31f8182c67ba01ccf49", // wormholePackageId
            "0x31358d198147da50db32eda2562951d53973a0c0ad5ed738e9b17d88b213d790", // wormholeStateId
            "0xf8929174008c662266a1adde78e1e8e33016eb7ad37d379481e860b911e40ed5", // priceFeedTableId
            "https://hermes-beta.pyth.network",
            "./mnemonic",
            "./price-config.beta.sample.yaml"
    );
}
