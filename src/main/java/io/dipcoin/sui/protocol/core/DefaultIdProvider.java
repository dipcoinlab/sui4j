/*
 * Copyright 2020 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.dipcoin.sui.protocol.core;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author : Same
 * @datetime : 2025/6/25 18:59
 * @Description : default id generator
 */
public class DefaultIdProvider {

    protected static final AtomicLong nextId = new AtomicLong(0);

    protected DefaultIdProvider() {}

    public static long getNextId() {
        return nextId.getAndIncrement();
    }

}
