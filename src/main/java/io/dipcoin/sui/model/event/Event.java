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

package io.dipcoin.sui.model.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author : Same
 * @datetime : 2025/6/28 11:22
 * @Description : Event response
 * TODO Future parsing of parsedJson can be done with generics
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {

    /**
     * Event ID (transaction sequence number, event sequence number)
     */
    private EventId id;

    /**
     * Move package ID that emitted this event
     */
    private String packageId;

    /**
     * JSON value after event parsing
     */
    private Object parsedJson;

    /**
     * Sender Sui address
     */
    private String sender;

    /**
     * Timestamp (milliseconds since 1970)
     */
    private BigInteger timestampMs;

    /**
     * Move module that emitted this event
     */
    private String transactionModule;

    /**
     * Move event type
     */
    private String type;

    /**
     * Binary encoding of event data
     */
    private String bcs;

    /**
     * @see BcsEncodingEnum
     * BCS encoding format (base64 or base58)
     */
    private String bcsEncoding;

}
