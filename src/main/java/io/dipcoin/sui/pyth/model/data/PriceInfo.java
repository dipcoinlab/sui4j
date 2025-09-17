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

package io.dipcoin.sui.pyth.model.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author : Same
 * @datetime : 2025/7/17 17:44
 * @Description : Pyth price information
 */
@Data
public class PriceInfo {

    /**
     * Price value (with precision coefficient)
     */
    private String price;

    /**
     * Confidence interval (Confidence Interval)
     */
    private String conf;

    /**
     * Exponent (expo -8 means decimal point shifts left 8 places)
     */
    private Integer expo;

    /**
     * Publish timestamp (Unix timestamp)
     */
    @JsonProperty("publish_time")
    private Long publishTime;

}