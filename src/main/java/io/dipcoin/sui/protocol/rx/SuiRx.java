/*
 * Copyright 2019 Web3 Labs Ltd.
 * Copyright 2025 Dipcoin LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modifications:
 * - Adapted for Sui RPC calls by Dipcoin LLC, 2025
 */

package io.dipcoin.sui.protocol.rx;

import io.dipcoin.sui.model.event.Event;
import io.dipcoin.sui.model.filter.SuiFilter;
import io.reactivex.Flowable;

/**
 * @author : Same
 * @datetime : 2025/6/25 21:44
 * @Description : The Flowable JSON-RPC client event API.
 */
public interface SuiRx {

    /**
     * Create a flowable to filter for specific events on the blockchain.
     *
     * @param suiFilter filter criteria
     * @return a {@link Flowable} instance that emits all Log events matching the filter
     */
    Flowable<Event> suiEventFlowable(SuiFilter suiFilter);
}
