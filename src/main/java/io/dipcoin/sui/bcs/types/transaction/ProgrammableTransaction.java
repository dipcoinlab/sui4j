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
    
    public int addInput(CallArg callArg) {
        return this.inputs.computeIfAbsent(callArg, k -> inputs.size());
    }

    public void addInputs(LinkedHashMap<CallArg, Integer> callArgs) {
        callArgs.forEach((callArg, integer) -> this.inputs.computeIfAbsent(callArg, k -> inputs.size()));
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
    
    public List<Command> getCommands() {
        return this.commands;
    }

    public int getCommandsSize() {
        return this.commands.size();
    }

} 