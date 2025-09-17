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
 * @datetime : 2025/7/11 18:25
 * @Description : Intent, corresponding to TypeScript's Intent type.
 */
public class Intent {
    
    private final IntentScope scope;
    private final IntentVersion version;
    private final AppId appId;
    
    public Intent(IntentScope scope, IntentVersion version, AppId appId) {
        this.scope = Objects.requireNonNull(scope);
        this.version = Objects.requireNonNull(version);
        this.appId = Objects.requireNonNull(appId);
    }

    public Intent(IntentScope scope) {
        this.scope = scope;
        this.version = IntentVersion.V0.INSTANCE;
        this.appId = AppId.Sui.INSTANCE;
    }
    
    public IntentScope getScope() {
        return scope;
    }
    
    public IntentVersion getVersion() {
        return version;
    }
    
    public AppId getAppId() {
        return appId;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Intent intent = (Intent) obj;
        return Objects.equals(scope, intent.scope) &&
               Objects.equals(version, intent.version) &&
               Objects.equals(appId, intent.appId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(scope, version, appId);
    }
    
    @Override
    public String toString() {
        return "Intent{" +
               "scope=" + scope +
               ", version=" + version +
               ", appId=" + appId +
               '}';
    }
} 