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

package io.dipcoin.sui.client;

import io.dipcoin.sui.bcs.types.transaction.Argument;
import io.dipcoin.sui.bcs.types.transaction.Command;
import io.dipcoin.sui.bcs.types.transaction.ProgrammableMoveCall;

import java.util.List;

/**
 * @author : Same
 * @datetime : 2025/7/21 10:44
 * @Description : Command method BCS serialization builder
 */
public class CommandBuilder {

    /**
     * moveCall
     * @param programmableMoveCall
     * @return
     */
    public static Command moveCall(ProgrammableMoveCall programmableMoveCall) {
        return new Command.MoveCall(programmableMoveCall);
    }

    /**
     * transferObjects
     * @param objects
     * @param address
     * @return
     */
    public static Command transferObjects(List<Argument> objects, Argument address) {
        return new Command.TransferObjects(objects, address);
    }

    /**
     * splitCoins
     * @param amounts
     * @return
     */
    public static Command splitCoins(List<Argument> amounts) {
        return new Command.SplitCoins(Argument.GasCoin.INSTANCE, amounts);
    }

    /**
     * splitCoins
     * @param coin
     * @param amounts
     * @return
     */
    public static Command splitCoins(Argument coin, List<Argument> amounts) {
        return new Command.SplitCoins(coin, amounts);
    }

    /**
     * mergeCoins
     * @param destination
     * @param sources
     * @return
     */
    public static Command mergeCoins(Argument destination, List<Argument> sources) {
        return new Command.MergeCoins(destination, sources);
    }

    /**
     * publish
     * @param modules
     * @param dependencies
     * @return
     */
    public static Command publish(List<byte[]> modules, List<String> dependencies) {
        return new Command.Publish(modules, dependencies);
    }

    /**
     * makeMoveVec
     * @param type
     * @param elements
     * @return
     */
    public static Command makeMoveVec(String type, List<Argument> elements) {
        return new Command.MakeMoveVec(type, elements);
    }

    /**
     * makeMoveVec
     * @param elements
     * @return
     */
    public static Command makeMoveVec(List<Argument> elements) {
        return new Command.MakeMoveVec(elements);
    }

    /**
     * upgrade
     * @param modules
     * @param dependencies
     * @param packageId
     * @param ticket
     * @return
     */
    public static Command upgrade(List<byte[]> modules, List<String> dependencies, String packageId, Argument ticket) {
        return new Command.Upgrade(modules, dependencies, packageId, ticket);
    }

}
