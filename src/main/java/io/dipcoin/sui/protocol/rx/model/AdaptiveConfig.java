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

package io.dipcoin.sui.protocol.rx.model;

/**
 * @author : Same
 * @datetime : 2025/9/28 15:34
 * @Description : Adaptive configuration
 */
public record AdaptiveConfig(double minSuccessRate, double targetSuccessRate,
                             long maxExecutionTime) {
    public AdaptiveConfig {
        // Parameter validation
        if (minSuccessRate < 0 || minSuccessRate > 100) {
            throw new IllegalArgumentException("minSuccessRate must be between 0 and 100");
        }
    }

    /**
     * Default configuration: Minimum success rate 98%, Target success rate 98%, Maximum execution time 2 seconds
     * @return
     */
    public static AdaptiveConfig defaults() {
        return new AdaptiveConfig(98.0, 99.9, 2000L);
    }
}