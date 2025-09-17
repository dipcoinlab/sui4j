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

package io.dipcoin.sui.bcs.types.owner;

import java.util.Objects;

/**
 * @author : Same
 * @datetime : 2025/7/11 18:35
 * @Description : Object owner type, corresponding to TypeScript's Owner enum.
 */
public abstract class Owner {
    
    /**
     * address owner
     */
    public static class AddressOwner extends Owner {
        private final String address;
        
        public AddressOwner(String address) {
            this.address = Objects.requireNonNull(address);
        }
        
        public String getAddress() {
            return address;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            AddressOwner that = (AddressOwner) obj;
            return Objects.equals(address, that.address);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(address);
        }
        
        @Override
        public String toString() {
            return "AddressOwner{" + address + "}";
        }
    }
    
    /**
     * object owner
     */
    public static class ObjectOwner extends Owner {
        private final String address;
        
        public ObjectOwner(String address) {
            this.address = Objects.requireNonNull(address);
        }
        
        public String getAddress() {
            return address;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ObjectOwner that = (ObjectOwner) obj;
            return Objects.equals(address, that.address);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(address);
        }
        
        @Override
        public String toString() {
            return "ObjectOwner{" + address + "}";
        }
    }
    
    /**
     * shared object
     */
    public static class Shared extends Owner {
        private final long initialSharedVersion;
        
        public Shared(long initialSharedVersion) {
            this.initialSharedVersion = initialSharedVersion;
        }
        
        public long getInitialSharedVersion() {
            return initialSharedVersion;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Shared shared = (Shared) obj;
            return initialSharedVersion == shared.initialSharedVersion;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(initialSharedVersion);
        }
        
        @Override
        public String toString() {
            return "Shared{" + initialSharedVersion + "}";
        }
    }
    
    /**
     * immutable object
     */
    public static class Immutable extends Owner {
        public static final Immutable INSTANCE = new Immutable();
        
        private Immutable() {}
        
        @Override
        public String toString() {
            return "Immutable";
        }
    }
    
    /**
     * consensus V2
     */
    public static class ConsensusV2 extends Owner {
        private final Authenticator authenticator;
        private final long startVersion;
        
        public ConsensusV2(Authenticator authenticator, long startVersion) {
            this.authenticator = Objects.requireNonNull(authenticator);
            this.startVersion = startVersion;
        }
        
        public Authenticator getAuthenticator() {
            return authenticator;
        }
        
        public long getStartVersion() {
            return startVersion;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ConsensusV2 that = (ConsensusV2) obj;
            return startVersion == that.startVersion &&
                   Objects.equals(authenticator, that.authenticator);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(authenticator, startVersion);
        }
        
        @Override
        public String toString() {
            return "ConsensusV2{" +
                   "authenticator=" + authenticator +
                   ", startVersion=" + startVersion +
                   '}';
        }
    }
    
    /**
     * authenticator
     */
    public static class Authenticator {
        private final String singleOwner;
        
        public Authenticator(String singleOwner) {
            this.singleOwner = Objects.requireNonNull(singleOwner);
        }
        
        public String getSingleOwner() {
            return singleOwner;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Authenticator that = (Authenticator) obj;
            return Objects.equals(singleOwner, that.singleOwner);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(singleOwner);
        }
        
        @Override
        public String toString() {
            return "Authenticator{" + singleOwner + "}";
        }
    }
} 