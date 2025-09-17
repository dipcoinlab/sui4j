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

package io.dipcoin.sui.bcs.types.intent;

/**
 * @author : Same
 * @datetime : 2025/7/11 18:20
 * @Description : Application ID, corresponding to TypeScript's AppId enum.
 */
public abstract class AppId {
    
    /**
     * Sui application.
     */
    public static class Sui extends AppId {
        public static final Sui INSTANCE = new Sui();
        
        private Sui() {}
        
        @Override
        public String toString() {
            return "Sui";
        }
    }
} 