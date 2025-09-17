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

import io.dipcoin.sui.protocol.SuiClient;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.ScheduledExecutorService;

/**
 * @author : Same
 * @datetime : 2025/6/25 21:50
 * @Description : sui reactive API implementation.
 */
public class JsonRpcSuiRx{

    private final SuiClient suiClient;
    private final ScheduledExecutorService scheduledExecutorService;
    private final Scheduler scheduler;

    public JsonRpcSuiRx(SuiClient suiClient, ScheduledExecutorService scheduledExecutorService) {
        this.suiClient = suiClient;
        this.scheduledExecutorService = scheduledExecutorService;
        this.scheduler = Schedulers.from(scheduledExecutorService);
    }

//    public Flowable<String> suiEventFlowable(long pollingInterval) {
//        return Flowable.create(
//                subscriber -> {
//                    BlockFilter blockFilter = new BlockFilter(suiClient, subscriber::onNext);
//                    this.run(blockFilter, subscriber, pollingInterval);
//                },
//                BackpressureStrategy.BUFFER);
//    }


//    private <T> void run(
//            org.web3j.protocol.core.filters.Filter<T> filter,
//            FlowableEmitter<? super T> emitter,
//            long pollingInterval) {
//
//        filter.run(scheduledExecutorService, pollingInterval);
//        emitter.setCancellable(filter::cancel);
//    }
}
