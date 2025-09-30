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

import io.dipcoin.sui.protocol.rx.model.AdaptiveConfig;
import io.dipcoin.sui.protocol.rx.model.TaskStats;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : Same
 * @datetime : 2025/9/28 15:31
 * @Description : Adaptive polling adjustment
 */
@Slf4j
public class AdaptivePollingManager {

    private final JsonRpcSuiPolling scheduler;
    private final Map<String, AdaptiveConfig> taskConfigs = new ConcurrentHashMap<>();
    private final boolean enableMonitor; // Whether to enable adaptive monitoring

    // Default configuration
    private static final long MIN_INTERVAL = 200L; // 200ms
    private static final long MAX_INTERVAL = 3000L; // 3s
    private static final double LOAD_THRESHOLD = 0.8; // 80% Load threshold

    public AdaptivePollingManager(JsonRpcSuiPolling scheduler) {
        this.scheduler = scheduler;
        this.enableMonitor = false;
    }

    public AdaptivePollingManager(JsonRpcSuiPolling scheduler, boolean enableMonitor) {
        this.scheduler = scheduler;
        this.enableMonitor = enableMonitor;
    }

    /**
     * Register adaptive task
     */
    public <T> void registerAdaptiveTask(String taskId, PollingTask<T> task,
                                         long baseInterval, AdaptiveConfig config) {
        scheduler.registerTask(taskId, task);
        taskConfigs.put(taskId, config);
        scheduler.startTask(taskId, task, baseInterval);

        // Start monitoring thread
        if (enableMonitor) {
            startMonitoring(taskId, task, baseInterval);
        }
    }

    /**
     * Start adaptive monitoring
     */
    private <T> void startMonitoring(String taskId, PollingTask<T> task, long baseInterval) {
        Thread.ofVirtual().start(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(30_000L); // Check every 30 seconds
                    adjustPollingInterval(taskId, task, baseInterval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    /**
     * Adjust polling interval
     */
    private <T> void adjustPollingInterval(String taskId, PollingTask<T> task, long baseInterval) {
        TaskStats stats = task.getStats();
        AdaptiveConfig config = taskConfigs.get(taskId);

        long currentInterval = baseInterval;
        double successRate = stats.getSuccessRate();
        double avgExecutionTime = stats.lastExecutionTime();

        // Adjust the interval based on success rate and execution time
        if (successRate < config.minSuccessRate()) {
            // Low success rate, increase interval to reduce load
            currentInterval = Math.min(currentInterval * 2, MAX_INTERVAL);
        } else if (successRate > config.targetSuccessRate() && avgExecutionTime < config.maxExecutionTime()) {
            // Good performance, can try to reduce the interval
            currentInterval = Math.max(currentInterval / 2, MIN_INTERVAL);
        }

        // Apply new interval
        scheduler.adjustInterval(taskId, currentInterval);

        log.debug("Adjusted polling interval for {}: {} ms (success rate: {}%)",
                taskId, currentInterval, String.format("%.2f", successRate));
    }
}
