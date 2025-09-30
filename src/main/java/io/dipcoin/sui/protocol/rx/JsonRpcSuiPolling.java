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

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author : Same
 * @datetime : 2025/6/25 21:50
 * @Description : sui polling API implementation.
 */
@Slf4j
public class JsonRpcSuiPolling implements AutoCloseable{

    private final ScheduledExecutorService scheduler;
    private final Map<String, PollingTask<?>> tasks = new ConcurrentHashMap<>();
    private final boolean useVirtualThreads;

    public JsonRpcSuiPolling(boolean useVirtualThreads) {
        this.useVirtualThreads = useVirtualThreads;
        this.scheduler = createScheduler();
    }

    public JsonRpcSuiPolling(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
        this.useVirtualThreads = true;
    }

    public JsonRpcSuiPolling() {
        this(true); // Default to using virtual threads.
    }

    private ScheduledExecutorService createScheduler() {
        if (useVirtualThreads) {
            return Executors.newScheduledThreadPool(1,
                    Thread.ofVirtual().factory());
        } else {
            return Executors.newScheduledThreadPool(4); // Platform thread pool.
        }
    }

    /**
     * Register polling task.
     */
    public <T> void registerTask(String taskId, PollingTask<T> task) {
        tasks.put(taskId, task);
        log.info("Registered polling task: {}", taskId);
    }

    /**
     * Start all polling tasks.
     */
    public void startAll(long intervalMs) {
        tasks.forEach((taskId, task) -> {
            startTask(taskId, task, intervalMs);
        });
        log.info("Started {} polling tasks with interval {}ms", tasks.size(), intervalMs);
    }

    /**
     * Start a single task.
     */
    public <T> void startTask(String taskId, PollingTask<T> task, long intervalMs) {
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
                () -> executeTaskSafely(taskId, task),
                0, intervalMs, TimeUnit.MILLISECONDS
        );

        task.setFuture(future);
        log.debug("Started task: {} with interval {}ms", taskId, intervalMs);
    }

    /**
     * Execute task safely (with exception handling).
     */
    private void executeTaskSafely(String taskId, PollingTask<?> task) {
        try {
            task.execute();
        } catch (Exception e) {
            log.error("Polling task {} failed", taskId, e);
            // Retry or alert logic can be added.
            task.onError(e);
        }
    }

    /**
     * Stop specific task.
     */
    public boolean stopTask(String taskId) {
        PollingTask<?> task = tasks.get(taskId);
        if (task != null && task.getFuture() != null) {
            log.info("Stopped polling task: {}", taskId);
            return task.getFuture().cancel(false);
        }
        return true;
    }

    /**
     * Dynamically adjust polling interval.
     */
    public void adjustInterval(String taskId, long newIntervalMs) {
        stopTask(taskId);
        PollingTask<?> task = tasks.get(taskId);
        if (task != null) {
            startTask(taskId, task, newIntervalMs);
            log.info("Adjusted interval for task {} to {}ms", taskId, newIntervalMs);
        }
    }

    @Override
    public void close() {
        // Stop all tasks.
        tasks.keySet().forEach(this::stopTask);

        // Shut down the scheduler.
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("Polling scheduler closed");
    }
}
