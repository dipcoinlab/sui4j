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

package io.dipcoin.sui.bcs.types.auth;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author : Same
 * @datetime : 2025/7/11 19:10
 * @Description : Passkey authenticator, corresponding to TypeScript's PasskeyAuthenticator type.
 */
public class PasskeyAuthenticator {
    
    private final byte[] authenticatorData;
    private final String clientDataJson;
    private final byte[] userSignature;
    
    public PasskeyAuthenticator(byte[] authenticatorData, String clientDataJson, byte[] userSignature) {
        this.authenticatorData = Arrays.copyOf(authenticatorData, authenticatorData.length);
        this.clientDataJson = Objects.requireNonNull(clientDataJson);
        this.userSignature = Arrays.copyOf(userSignature, userSignature.length);
    }
    
    public byte[] getAuthenticatorData() {
        return Arrays.copyOf(authenticatorData, authenticatorData.length);
    }
    
    public String getClientDataJson() {
        return clientDataJson;
    }
    
    public byte[] getUserSignature() {
        return Arrays.copyOf(userSignature, userSignature.length);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PasskeyAuthenticator that = (PasskeyAuthenticator) obj;
        return Arrays.equals(authenticatorData, that.authenticatorData) &&
               Objects.equals(clientDataJson, that.clientDataJson) &&
               Arrays.equals(userSignature, that.userSignature);
    }
    
    @Override
    public int hashCode() {
        int result = Objects.hash(clientDataJson);
        result = 31 * result + Arrays.hashCode(authenticatorData);
        result = 31 * result + Arrays.hashCode(userSignature);
        return result;
    }
    
    @Override
    public String toString() {
        return "PasskeyAuthenticator{" +
               "authenticatorData=" + Arrays.toString(authenticatorData) +
               ", clientDataJson='" + clientDataJson + '\'' +
               ", userSignature=" + Arrays.toString(userSignature) +
               '}';
    }
} 