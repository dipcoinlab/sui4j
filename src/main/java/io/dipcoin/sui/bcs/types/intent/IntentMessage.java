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

import java.util.Objects;

/**
 * @author : Same
 * @datetime : 2025/7/11 18:30
 * @Description : Intent message, corresponding to TypeScript's IntentMessage type.
 */
public class IntentMessage<T> {

    private final Intent intent;
    private final T value;
    
    public IntentMessage(Intent intent, T value) {
        this.intent = Objects.requireNonNull(intent);
        this.value = Objects.requireNonNull(value);
    }

    public IntentMessage(IntentScope intentScope, T value) {
        this.intent = new Intent(intentScope);
        this.value = Objects.requireNonNull(value);
    }
    
    public Intent getIntent() {
        return intent;
    }
    
    public T getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        IntentMessage<?> that = (IntentMessage<?>) obj;
        return Objects.equals(intent, that.intent) &&
               Objects.equals(value, that.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(intent, value);
    }
    
    @Override
    public String toString() {
        return "IntentMessage{" +
               "intent=" + intent +
               ", value=" + value +
               '}';
    }
} 