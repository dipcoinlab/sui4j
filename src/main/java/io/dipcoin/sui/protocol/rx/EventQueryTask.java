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

import io.dipcoin.sui.model.Response;
import io.dipcoin.sui.model.event.Event;
import io.dipcoin.sui.model.event.EventId;
import io.dipcoin.sui.model.event.PageForEventAndEventId;
import io.dipcoin.sui.protocol.SuiClient;
import io.dipcoin.sui.protocol.exceptions.RpcRequestFailedException;
import io.dipcoin.sui.protocol.http.request.QueryEvents;
import io.dipcoin.sui.protocol.http.response.PageForEventAndEventIdWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author : Same
 * @datetime : 2025/9/26 18:34
 * @Description : Event query task
 */
@Slf4j
public class EventQueryTask extends PollingTask<List<Event>> {

    private final QueryEvents query;
    private final AtomicReference<EventId> lastCursor;

    public EventQueryTask(SuiClient suiClient,
                          QueryEvents query,
                          Callback<List<Event>> callback) {
        super(suiClient, callback);
        this.query = query;
        this.lastCursor = new AtomicReference<>(query.getCursor());
    }

    @Override
    public void execute() {
        long startTime = System.currentTimeMillis();

        try {
            // Set next query parameters.
            if (query.getCursor() != null) {
                query.setCursor(lastCursor.get());
            }

            // Execute query.
            PageForEventAndEventIdWrapper resultWrapper = suiClient.queryEvents(query)
                    .send();
            Response.Error error = resultWrapper.getError();

            if (error != null) {
                log.error(error.getMessage());
                throw new RpcRequestFailedException(error.getMessage());
            }
            PageForEventAndEventId result = resultWrapper.getResult();

            // Callback processing result.
            List<Event> data = result.getData();
            if (callback != null && data != null && !data.isEmpty()) {

                // Update cursor.
                if (query.getCursor() != null) {
                    callback.onEvent(data);
                    if (result.getHasNextPage()) {
                        // Query in ascending order by cursor.
                        lastCursor.set(result.getNextCursor());
                    } else {
                        // Query in descending order by cursor.
                        lastCursor.set(data.getLast().getId());
                    }
                } else {
                    // If the cursor is null, there is no need to set a cursor. Save the latest cursor and perform data deduplication.
                    EventId lastEventId = lastCursor.get();
                    if (lastEventId != null) {
                        // Find the position of the last processed event.
                        int lastProcessedIndex = this.findLastProcessedEventIndex(data, lastEventId);

                        if (lastProcessedIndex >= 0) {
                            // Identify duplicate events and extract the unprocessed portion.
                            List<Event> newEvents = data.subList(0, lastProcessedIndex);
                            if (!newEvents.isEmpty()) {
                                // Create a deduplicated result object.
                                callback.onEvent(newEvents);
                                log.debug("Deduplicated events: {} new events, {} duplicates",
                                        newEvents.size(), data.size() - newEvents.size());
                            } else {
                                // All events are duplicates, no processing required.
                                log.debug("All events are duplicates, skipping callback");
                            }
                        } else {
                            // No duplicate events found, process all data.
                            callback.onEvent(data);
                        }
                    } else {
                        // First query, no historical cursor available.
                        callback.onEvent(data);
                    }
                    lastCursor.set(data.getFirst().getId());
                }
            }

            // Update statistics.
            successCount.incrementAndGet();
            lastExecutionTime = System.currentTimeMillis() - startTime;

            log.debug("Event query completed: {} events, cursor: {}",
                    data != null ? data.size() : 0, lastCursor);

        } catch (Exception e) {
            errorCount.incrementAndGet();
            log.error("Event query failed", e);
            if (callback != null) {
                callback.onError(e);
            }
            throw new RuntimeException("Event query execution failed", e);
        }
    }

    /**
     * Reset cursor (start querying from the latest).
     */
    public void resetCursor() {
        this.lastCursor.set(null);
        log.info("Event query cursor reset");
    }

    /**
     * Get current cursor status.
     */
    public EventId getCursorState() {
        return lastCursor.get();
    }

    /**
     * Find the position of the last processed event in the list.
     * Since it is a reverse order query, the data is arranged from newest to oldest.
     *
     * @param events The current queried event list (in reverse order, with the newest events first).
     * @param lastEventId The ID of the last processed event.
     * @return The index of the last duplicate event; -1 indicates no duplicate events.
     */
    private int findLastProcessedEventIndex(List<Event> events, EventId lastEventId) {
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            if (this.isSameEvent(event.getId(), lastEventId)) {
                return i; // Return the index of duplicate events.
            }
        }
        return -1; // No duplicate events found.
    }

    /**
     * Determine if two event IDs are identical.
     */
    private boolean isSameEvent(EventId eventId1, EventId eventId2) {
        if (eventId1 == null || eventId2 == null) {
            return false;
        }

        // Compare transaction digest and event sequence number.
        return Objects.equals(eventId1.getTxDigest(), eventId2.getTxDigest()) &&
                Objects.equals(eventId1.getEventSeq(), eventId2.getEventSeq());
    }
}