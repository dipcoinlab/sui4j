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

import io.dipcoin.sui.protocol.SuiClient;
import io.dipcoin.sui.protocol.rx.model.TaskStats;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author : Same
 * @datetime : 2025/9/26 18:29
 * @Description : Generic task interface
 */
public abstract class PollingTask<T> {

    protected volatile ScheduledFuture<?> future;
    protected final SuiClient suiClient;
    protected final Callback<T> callback;

    // Statistical information.
    protected final AtomicLong successCount = new AtomicLong();
    protected final AtomicLong errorCount = new AtomicLong();
    protected volatile long lastExecutionTime;

    public PollingTask(SuiClient suiClient, Callback<T> callback) {
        this.suiClient = suiClient;
        this.callback = callback;
    }

    /**
     * Execute polling logic.
     */
    public abstract void execute();

    /**
     * Error handling.
     */
    public void onError(Exception e) {
        errorCount.incrementAndGet();
        // Custom error handling logic can be added.
    }

    /**
     * Get task statistics.
     */
    public TaskStats getStats() {
        return new TaskStats(
                successCount.get(),
                errorCount.get(),
                lastExecutionTime
        );
    }

    public void setFuture(ScheduledFuture<?> future) {
        this.future = future;
    }

    public ScheduledFuture<?> getFuture() {
        return future;
    }

}
