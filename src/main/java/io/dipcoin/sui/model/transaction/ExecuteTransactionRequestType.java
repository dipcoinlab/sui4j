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

import lombok.Getter;

/**
 * @author : Same
 * @datetime : 2025/6/27 11:48
 * @Description : ExecuteTransactionRequestType enum request
 */
@Getter
public enum ExecuteTransactionRequestType {

    /**
     * WaitForEffectsCert return including:
     * transaction summary
     * effects certificate
     * event summary
     * but local status may not be updated yet
     * Timeout recommendation: 5 seconds
     * Suitable scenarios:
     *  - Rapid transaction submission confirmation (fastest transaction confirmation)
     *  - General transfer operations (medium confirmation sufficient for security)
     */
    WAIT_FOR_EFFECTS_CERT("WaitForEffectsCert"),

    /**
     * Scenarios requiring strong consistency.
     * Additional guarantees when `WaitForLocalExecution` returns:
     * All object states have been updated.
     * Events are immediately queryable.
     * Transaction results are 100% deterministic.
     * Timeout recommendation: 15 seconds.
     * Suitable scenarios:
     *  - Requires immediate querying of transaction results (ensures local status is updated)
     *  - Critical contract calls (requires strongest execution guarantee)
     */
    WAIT_FOR_LOCAL_EXECUTION("WaitForLocalExecution");

    ExecuteTransactionRequestType(String type) {
        this.type = type;
    }

    private final String type;
}
