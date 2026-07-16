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

package io.dipcoin.sui.bcs.types.transaction;

import io.dipcoin.sui.bcs.types.arg.call.CallArg;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author : Same
 * @datetime : 2025/7/6
 * @Description : A deserialization-only {@link ProgrammableTransaction} (open for extension, closed for modification).
 *
 * <p>In BCS a PTB's inputs form a positional array, and commands reference inputs by their
 * original positional index. The base class stores them in a {@code LinkedHashMap<CallArg, Integer>}
 * and de-duplicates by value at build time (reusing the slot of an identical Pure input) — a
 * deliberate optimization for building transactions.
 *
 * <p>Applying that de-duplication semantics to deserialization is incorrect: when a transaction
 * contains duplicate inputs (typically a multi-hop swap with {@code sqrt_price_limit = u64::MAX},
 * direction booleans, etc., which collapse because {@code CallArgPure} is compared by value),
 * the {@code Map} collapses entries and reindexes them, so {@code getInputByIndex} can no longer
 * find the input at its original position.
 *
 * <p>This subclass instead stores inputs in a positional {@code List} without any de-duplication,
 * guaranteeing that indices match BCS positions exactly, and overrides {@link #getInputList()} so
 * that re-serialization is faithful. The build path is left completely unaffected.
 */
public class DecodedProgrammableTransaction extends ProgrammableTransaction {

    /** Positional, non-deduplicated input list preserving BCS order; the index is the position. */
    private final List<CallArg> orderedInputs;

    public DecodedProgrammableTransaction(List<CallArg> inputs, List<Command> commands) {
        super(new LinkedHashMap<>(), commands);
        this.orderedInputs = inputs != null ? inputs : new ArrayList<>();
    }

    @Override
    public CallArg getInputByIndex(int index) {
        if (index < 0 || index >= orderedInputs.size()) {
            return null;
        }
        return orderedInputs.get(index);
    }

    @Override
    public List<CallArg> getInputList() {
        return orderedInputs;
    }
}
