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

package io.dipcoin.sui.model.transaction;

import lombok.Data;

/**
 * @author : Same
 * @datetime : 2025/6/27 11:19
 * @Description : Return response field options request
 */
@Data
public class TransactionBlockResponseOptions {

    /**
     * Whether to show balance_changes. Default to be False
     */
    private Boolean showBalanceChanges;

    /**
     * Whether to show transaction effects. Default to be False
     */
    private Boolean showEffects;

    /**
     * Whether to show transaction events. Default to be False
     */
    private Boolean showEvents;

    /**
     * Whether to show transaction input data. Default to be False
     */
    private Boolean showInput;

    /**
     * Whether to show object_changes. Default to be False
     */
    private Boolean showObjectChanges;

    /**
     * Whether to show raw transaction effects. Default to be False
     */
    private Boolean showRawEffects;

    /**
     * Whether to show bcs-encoded transaction input data
     */
    private Boolean showRawInput;

    /**
     * default all to true
     * @return
     */
    public static TransactionBlockResponseOptions allTrue() {
        TransactionBlockResponseOptions options = new TransactionBlockResponseOptions();
        options.setShowBalanceChanges(true);
        options.setShowEffects(true);
        options.setShowEvents(true);
        options.setShowInput(true);
        options.setShowObjectChanges(true);
        options.setShowRawEffects(true);
        options.setShowRawInput(true);
        return options;
    }

    /**
     * bcs raw to false
     * @return
     */
    public static TransactionBlockResponseOptions rawFalse() {
        TransactionBlockResponseOptions options = new TransactionBlockResponseOptions();
        options.setShowBalanceChanges(true);
        options.setShowEffects(true);
        options.setShowEvents(true);
        options.setShowInput(true);
        options.setShowObjectChanges(true);
        options.setShowRawEffects(false);
        options.setShowRawInput(false);
        return options;
    }

    /**
     * changes and effects to true
     * @return
     */
    public static TransactionBlockResponseOptions changesAndEffectsTrue() {
        TransactionBlockResponseOptions options = new TransactionBlockResponseOptions();
        options.setShowBalanceChanges(true);
        options.setShowEffects(true);
        options.setShowEvents(false);
        options.setShowInput(false);
        options.setShowObjectChanges(true);
        options.setShowRawEffects(false);
        options.setShowRawInput(false);
        return options;
    }

    /**
     * changes, events and effects to true
     * @return
     */
    public static TransactionBlockResponseOptions changesAndEventsAndEffectsTrue() {
        TransactionBlockResponseOptions options = new TransactionBlockResponseOptions();
        options.setShowBalanceChanges(true);
        options.setShowEffects(true);
        options.setShowEvents(true);
        options.setShowInput(false);
        options.setShowObjectChanges(true);
        options.setShowRawEffects(false);
        options.setShowRawInput(false);
        return options;
    }

}
