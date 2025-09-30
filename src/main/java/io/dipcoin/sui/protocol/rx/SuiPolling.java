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

package io.dipcoin.sui.protocol.rx;

import io.dipcoin.sui.model.event.Event;
import io.dipcoin.sui.protocol.http.request.QueryEvents;

import java.util.List;

/**
 * @author : Same
 * @datetime : 2025/6/25 21:44
 * @Description : The polling JSON-RPC client event API.
 */
public interface SuiPolling {

    /**
     * Create a polling to filter for specific events on the blockchain.
     * @param request
     * @param callback
     * @return
     */
    String suiEventSubscribe(QueryEvents request, Callback<List<Event>> callback);

    /**
     * Create a polling to filter for specific events on the blockchain.
     * @param request
     * @param callback
     * @param interval
     * @return
     */
    String suiEventSubscribe(QueryEvents request, Callback<List<Event>> callback, long interval);

    /**
     * unSubscribe a polling to filter for specific events on the blockchain.
     * @param taskId
     * @return
     */
    boolean unSubscribe(String taskId);
}
