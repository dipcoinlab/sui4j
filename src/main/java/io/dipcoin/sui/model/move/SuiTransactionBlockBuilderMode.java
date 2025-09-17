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

package io.dipcoin.sui.model.move;

import lombok.Getter;

/**
 * @author : Same
 * @datetime : 2025/6/30 00:08
 * @Description : SuiTransactionBlockBuilderMode enum request
 */
@Getter
public enum SuiTransactionBlockBuilderMode {

    COMMIT("Commit"),   // default, regular Sui transaction committed on-chain
    DEV_INSPECT("DevInspect"),  // Allows calling any Move function with arbitrary values in simulated transaction.
    ;

    SuiTransactionBlockBuilderMode(String mode) {
        this.mode = mode;
    }

    private final String mode;
}
