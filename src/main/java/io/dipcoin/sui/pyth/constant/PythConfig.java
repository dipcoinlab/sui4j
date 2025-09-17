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

/**
 * @author : Same
 * @datetime : 2025/7/16 14:37
 * @Description : Pyth configuration constant
 */
public interface PythConfig {

    String TYPE_TAG = "::price_info::PriceInfo";

    String TABLE_ID_TYPE = "::price_identifier::PriceIdentifier";

    String TABLE_ID_PREFIX = "0x2::table::Table<";

    String TABLE_ID_SUFFIX = "::price_identifier::PriceIdentifier, 0x2::object::ID>";

    // ------------------ parse_and_verify ------------------

    String WORMHOLE_VERIFY_MODULE = "vaa";

    String WORMHOLE_VERIFY_FUNCTION = "parse_and_verify";


    // ------------------ create_authenticated_price_infos_using_accumulator ------------------
    String PYTH_CREATE_MODULE = "pyth";

    String PYTH_CREATE_FUNCTION = "create_authenticated_price_infos_using_accumulator";


    // ------------------ update_single_price_feed ------------------
    String PYTH_UPDATE_MODULE = PYTH_CREATE_MODULE;

    String PYTH_UPDATE_FUNCTION = "update_single_price_feed";


    // ------------------ destroy ------------------
    String PYTH_DESTROY_MODULE = "hot_potato_vector";

    String PYTH_DESTROY_FUNCTION = "destroy";


}
