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
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : Same
 * @datetime : 2025/7/11 17:45
 * @Description : Programmable transaction, corresponding to TypeScript's `ProgrammableTransaction` type.
 */
@Data
public class ProgrammableTransaction {
    
    private final LinkedHashMap<CallArg, Integer> inputs;
    private final List<Command> commands;

    public ProgrammableTransaction() {
        this.inputs = new LinkedHashMap<>();
        this.commands = new ArrayList<>();
    }

    public ProgrammableTransaction(LinkedHashMap<CallArg, Integer> inputs, List<Command> commands) {
        this.inputs = inputs;
        this.commands = commands;
    }

    public ProgrammableTransaction(List<CallArg> inputs, List<Command> commands) {
        this.inputs = new LinkedHashMap<>(inputs.size());
        this.addInputs(inputs);
        this.commands = commands;
    }

    public int addInput(CallArg callArg) {
        return this.inputs.computeIfAbsent(callArg, k -> inputs.size());
    }

    public void addInputs(LinkedHashMap<CallArg, Integer> callArgs) {
        callArgs.forEach((callArg, integer) -> this.inputs.computeIfAbsent(callArg, k -> inputs.size()));
    }

    public void updateInputs(LinkedHashMap<CallArg, Integer> callArgs) {
        this.inputs.clear();
        this.addInputs(callArgs);
    }

    public void addInputs(List<CallArg> callArgs) {
        callArgs.forEach(callArg -> this.inputs.computeIfAbsent(callArg, k -> inputs.size()));
    }

    public ProgrammableTransaction addCommand(Command command) {
        this.commands.add(command);
        return this;
    }

    public void addCommands(List<Command> commands) {
        this.commands.addAll(commands);
    }

    public List<CallArg> getInputList() {
        return new ArrayList<>(inputs.keySet());
    }

    public LinkedHashMap<CallArg, Integer> getInputs() {
        return inputs;
    }

    /**
     * Get the input at the given BCS positional index (extension point / Template Method).
     *
     * <p>The default implementation reverse-looks-up the value in the build-time
     * {@code Map<CallArg, Integer>}, serving the "build transaction" semantics (identical Pure
     * inputs are de-duplicated by value and share a single slot).
     *
     * <p>Deserialization semantics are the opposite: inputs form a positional array and commands
     * reference inputs by their original position, so de-duplication is not allowed. That case is
     * overridden by {@link DecodedProgrammableTransaction} with a positional O(1) implementation.
     *
     * @param index the positional index of the input in BCS
     * @return the corresponding {@link CallArg}, or {@code null} if none exists
     */
    public CallArg getInputByIndex(int index) {
        for (Map.Entry<CallArg, Integer> entry : inputs.entrySet()) {
            if (entry.getValue() != null && entry.getValue() == index) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    public List<Command> getCommands() {
        return this.commands;
    }

    public int getCommandsSize() {
        return this.commands.size();
    }

} 