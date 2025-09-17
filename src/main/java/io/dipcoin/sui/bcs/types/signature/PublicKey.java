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

package io.dipcoin.sui.bcs.types.signature;

import java.util.Arrays;

/**
 * @author : Same
 * @datetime : 2025/7/11 18:45
 * @Description : Public key type, corresponding to TypeScript's `PublicKey` enum.
 */
public abstract class PublicKey {
    
    /**
     * ED25519 public key
     */
    public static class ED25519 extends PublicKey {
        private final byte[] publicKey;
        
        public ED25519(byte[] publicKey) {
            if (publicKey.length != 32) {
                throw new IllegalArgumentException("ED25519 public key must be 32 bytes");
            }
            this.publicKey = Arrays.copyOf(publicKey, publicKey.length);
        }
        
        public byte[] getPublicKey() {
            return Arrays.copyOf(publicKey, publicKey.length);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ED25519 ed25519 = (ED25519) obj;
            return Arrays.equals(publicKey, ed25519.publicKey);
        }
        
        @Override
        public int hashCode() {
            return Arrays.hashCode(publicKey);
        }
        
        @Override
        public String toString() {
            return "ED25519{publicKey=" + Arrays.toString(publicKey) + "}";
        }
    }
    
    /**
     * Secp256k1 public key
     */
    public static class Secp256k1 extends PublicKey {
        private final byte[] publicKey;
        
        public Secp256k1(byte[] publicKey) {
            if (publicKey.length != 33) {
                throw new IllegalArgumentException("Secp256k1 public key must be 33 bytes");
            }
            this.publicKey = Arrays.copyOf(publicKey, publicKey.length);
        }
        
        public byte[] getPublicKey() {
            return Arrays.copyOf(publicKey, publicKey.length);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Secp256k1 secp256k1 = (Secp256k1) obj;
            return Arrays.equals(publicKey, secp256k1.publicKey);
        }
        
        @Override
        public int hashCode() {
            return Arrays.hashCode(publicKey);
        }
        
        @Override
        public String toString() {
            return "Secp256k1{publicKey=" + Arrays.toString(publicKey) + "}";
        }
    }
    
    /**
     * Secp256r1 public key
     */
    public static class Secp256r1 extends PublicKey {
        private final byte[] publicKey;
        
        public Secp256r1(byte[] publicKey) {
            if (publicKey.length != 33) {
                throw new IllegalArgumentException("Secp256r1 public key must be 33 bytes");
            }
            this.publicKey = Arrays.copyOf(publicKey, publicKey.length);
        }
        
        public byte[] getPublicKey() {
            return Arrays.copyOf(publicKey, publicKey.length);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Secp256r1 secp256r1 = (Secp256r1) obj;
            return Arrays.equals(publicKey, secp256r1.publicKey);
        }
        
        @Override
        public int hashCode() {
            return Arrays.hashCode(publicKey);
        }
        
        @Override
        public String toString() {
            return "Secp256r1{publicKey=" + Arrays.toString(publicKey) + "}";
        }
    }
    
    /**
     * ZkLogin public key
     */
    public static class ZkLogin extends PublicKey {
        private final byte[] publicKey;
        
        public ZkLogin(byte[] publicKey) {
            this.publicKey = Arrays.copyOf(publicKey, publicKey.length);
        }
        
        public byte[] getPublicKey() {
            return Arrays.copyOf(publicKey, publicKey.length);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ZkLogin zkLogin = (ZkLogin) obj;
            return Arrays.equals(publicKey, zkLogin.publicKey);
        }
        
        @Override
        public int hashCode() {
            return Arrays.hashCode(publicKey);
        }
        
        @Override
        public String toString() {
            return "ZkLogin{publicKey=" + Arrays.toString(publicKey) + "}";
        }
    }
} 