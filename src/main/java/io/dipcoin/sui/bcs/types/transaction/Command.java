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

import java.util.List;
import java.util.Objects;

/**
 * @author : Same
 * @datetime : 2025/7/11 17:40
 * @Description : Transaction command type, corresponding to TypeScript's `Command` enum.
 */
public abstract class Command {
    
    /**
     * Movecall command
     */
    public static class MoveCall extends Command {
        private final ProgrammableMoveCall moveCall;
        
        public MoveCall(ProgrammableMoveCall moveCall) {
            this.moveCall = Objects.requireNonNull(moveCall);
        }
        
        public ProgrammableMoveCall getMoveCall() {
            return moveCall;
        }
        
        @Override
        public String toString() {
            return "MoveCall{" + moveCall + "}";
        }
    }
    
    /**
     * TransferObjects command
     */
    public static class TransferObjects extends Command {
        private final List<Argument> objects;
        private final Argument address;
        
        public TransferObjects(List<Argument> objects, Argument address) {
            this.objects = Objects.requireNonNull(objects);
            this.address = Objects.requireNonNull(address);
        }
        
        public List<Argument> getObjects() {
            return objects;
        }
        
        public Argument getAddress() {
            return address;
        }
        
        @Override
        public String toString() {
            return "TransferObjects{" +
                   "objects=" + objects +
                   ", address=" + address +
                   '}';
        }
    }
    
    /**
     * SplitCoins command
     */
    public static class SplitCoins extends Command {
        private final Argument coin;
        private final List<Argument> amounts;
        
        public SplitCoins(Argument coin, List<Argument> amounts) {
            this.coin = Objects.requireNonNull(coin);
            this.amounts = Objects.requireNonNull(amounts);
        }
        
        public Argument getCoin() {
            return coin;
        }
        
        public List<Argument> getAmounts() {
            return amounts;
        }
        
        @Override
        public String toString() {
            return "SplitCoins{" +
                   "coin=" + coin +
                   ", amounts=" + amounts +
                   '}';
        }
    }
    
    /**
     * MergeCoins command
     */
    public static class MergeCoins extends Command {
        private final Argument destination;
        private final List<Argument> sources;
        
        public MergeCoins(Argument destination, List<Argument> sources) {
            this.destination = Objects.requireNonNull(destination);
            this.sources = Objects.requireNonNull(sources);
        }
        
        public Argument getDestination() {
            return destination;
        }
        
        public List<Argument> getSources() {
            return sources;
        }
        
        @Override
        public String toString() {
            return "MergeCoins{" +
                   "destination=" + destination +
                   ", sources=" + sources +
                   '}';
        }
    }
    
    /**
     * Publish command
     */
    public static class Publish extends Command {
        private final List<byte[]> modules;
        private final List<String> dependencies;
        
        public Publish(List<byte[]> modules, List<String> dependencies) {
            this.modules = Objects.requireNonNull(modules);
            this.dependencies = Objects.requireNonNull(dependencies);
        }
        
        public List<byte[]> getModules() {
            return modules;
        }
        
        public List<String> getDependencies() {
            return dependencies;
        }
        
        @Override
        public String toString() {
            return "Publish{" +
                   "modules=" + modules.size() + " modules" +
                   ", dependencies=" + dependencies +
                   '}';
        }
    }
    
    /**
     * MakeMoveVec command
     */
    public static class MakeMoveVec extends Command {
        private final static String NULL = "null";

        private final String type;
        private final List<Argument> elements;
        
        public MakeMoveVec(String type, List<Argument> elements) {
            this.type = type; // Can be null
            this.elements = Objects.requireNonNull(elements);
        }

        public MakeMoveVec(List<Argument> elements) {
            this(NULL, elements);
        }
        
        public String getType() {
            return type;
        }
        
        public List<Argument> getElements() {
            return elements;
        }
        
        @Override
        public String toString() {
            return "MakeMoveVec{" +
                   "type='" + type + '\'' +
                   ", elements=" + elements +
                   '}';
        }
    }
    
    /**
     * Upgrade command
     */
    public static class Upgrade extends Command {
        private final List<byte[]> modules;
        private final List<String> dependencies;
        private final String packageId;
        private final Argument ticket;
        
        public Upgrade(List<byte[]> modules, List<String> dependencies, String packageId, Argument ticket) {
            this.modules = Objects.requireNonNull(modules);
            this.dependencies = Objects.requireNonNull(dependencies);
            this.packageId = Objects.requireNonNull(packageId);
            this.ticket = Objects.requireNonNull(ticket);
        }
        
        public List<byte[]> getModules() {
            return modules;
        }
        
        public List<String> getDependencies() {
            return dependencies;
        }
        
        public String getPackageId() {
            return packageId;
        }
        
        public Argument getTicket() {
            return ticket;
        }
        
        @Override
        public String toString() {
            return "Upgrade{" +
                   "modules=" + modules.size() + " modules" +
                   ", dependencies=" + dependencies +
                   ", packageId='" + packageId + '\'' +
                   ", ticket=" + ticket +
                   '}';
        }
    }
} 