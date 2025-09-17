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

/**
 * @author : Same
 * @datetime : 2025/7/11 17:30
 * @Description : Transaction parameter type, corresponding to TypeScript's `Argument` enum.
 */
public abstract class Argument {

    /**
     * Gas coin parameter
     */
    public static class GasCoin extends Argument {
        public static final GasCoin INSTANCE = new GasCoin();
        
        private GasCoin() {}
        
        @Override
        public String toString() {
            return "GasCoin";
        }
    }
    
    /**
     * input parameter
     */
    public static class Input extends Argument {
        private final int index;
        
        public Input(int index) {
            this.index = index;
        }
        
        public int getIndex() {
            return index;
        }
        
        @Override
        public String toString() {
            return "Input{" + index + "}";
        }
    }
    
    /**
     * result parameter
     */
    public static class Result extends Argument {
        private final int index;
        
        public Result(int index) {
            this.index = index;
        }
        
        public int getIndex() {
            return index;
        }
        
        @Override
        public String toString() {
            return "Result{" + index + "}";
        }
    }
    
    /**
     * nested result parameter
     */
    public static class NestedResult extends Argument {
        private final int resultIndex;
        private final int nestedIndex;
        
        public NestedResult(int resultIndex, int nestedIndex) {
            this.resultIndex = resultIndex;
            this.nestedIndex = nestedIndex;
        }
        
        public int getResultIndex() {
            return resultIndex;
        }
        
        public int getNestedIndex() {
            return nestedIndex;
        }
        
        @Override
        public String toString() {
            return "NestedResult{" + resultIndex + ", " + nestedIndex + "}";
        }
    }

    /**
     * Get pre-cached Input
     * @param index
     * @return
     */
    public static Argument.Input ofInput(int index) {
        if (index >= 0 && index < ArgumentCache.INPUTS.length) {
            return ArgumentCache.INPUTS[index];
        }
        return new Argument.Input(index);
    }

    /**
     * Get pre-cached Result
     * @param index
     * @return
     */
    public static Argument.Result ofResult(int index) {
        if (index >= 0 && index < ArgumentCache.RESULT.length) {
            return ArgumentCache.RESULT[index];
        }
        return new Argument.Result(index);
    }

    private static class ArgumentCache {

        private static final Argument.Input[] INPUTS;
        private static final Argument.Result[] RESULT;

        static {
            INPUTS = new Argument.Input[100];
            for (int i = 0; i < INPUTS.length; i++) {
                INPUTS[i] = new Argument.Input(i);
            }
            RESULT = new Argument.Result[10];
            for (int i = 0; i < RESULT.length; i++) {
                RESULT[i] = new Argument.Result(i);
            }
        }
    }
} 