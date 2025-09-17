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
 * @datetime : 2025/7/11 18:40
 * @Description : Compressed signature type, corresponding to TypeScript's CompressedSignature type.
 */
public abstract class CompressedSignature {
    
    /**
     * ED25519 sign
     */
    public static class ED25519 extends CompressedSignature {
        private final byte[] signature;
        
        public ED25519(byte[] signature) {
            if (signature.length != 64) {
                throw new IllegalArgumentException("ED25519 signature must be 64 bytes");
            }
            this.signature = Arrays.copyOf(signature, signature.length);
        }
        
        public byte[] getSignature() {
            return Arrays.copyOf(signature, signature.length);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ED25519 ed25519 = (ED25519) obj;
            return Arrays.equals(signature, ed25519.signature);
        }
        
        @Override
        public int hashCode() {
            return Arrays.hashCode(signature);
        }
        
        @Override
        public String toString() {
            return "ED25519{signature=" + Arrays.toString(signature) + "}";
        }
    }
    
    /**
     * Secp256k1 sign
     */
    public static class Secp256k1 extends CompressedSignature {
        private final byte[] signature;
        
        public Secp256k1(byte[] signature) {
            if (signature.length != 64) {
                throw new IllegalArgumentException("Secp256k1 signature must be 64 bytes");
            }
            this.signature = Arrays.copyOf(signature, signature.length);
        }
        
        public byte[] getSignature() {
            return Arrays.copyOf(signature, signature.length);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Secp256k1 secp256k1 = (Secp256k1) obj;
            return Arrays.equals(signature, secp256k1.signature);
        }
        
        @Override
        public int hashCode() {
            return Arrays.hashCode(signature);
        }
        
        @Override
        public String toString() {
            return "Secp256k1{signature=" + Arrays.toString(signature) + "}";
        }
    }
    
    /**
     * Secp256r1 sign
     */
    public static class Secp256r1 extends CompressedSignature {
        private final byte[] signature;
        
        public Secp256r1(byte[] signature) {
            if (signature.length != 64) {
                throw new IllegalArgumentException("Secp256r1 signature must be 64 bytes");
            }
            this.signature = Arrays.copyOf(signature, signature.length);
        }
        
        public byte[] getSignature() {
            return Arrays.copyOf(signature, signature.length);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Secp256r1 secp256r1 = (Secp256r1) obj;
            return Arrays.equals(signature, secp256r1.signature);
        }
        
        @Override
        public int hashCode() {
            return Arrays.hashCode(signature);
        }
        
        @Override
        public String toString() {
            return "Secp256r1{signature=" + Arrays.toString(signature) + "}";
        }
    }
    
    /**
     * ZkLogin sign
     */
    public static class ZkLogin extends CompressedSignature {
        private final byte[] signature;
        
        public ZkLogin(byte[] signature) {
            this.signature = Arrays.copyOf(signature, signature.length);
        }
        
        public byte[] getSignature() {
            return Arrays.copyOf(signature, signature.length);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ZkLogin zkLogin = (ZkLogin) obj;
            return Arrays.equals(signature, zkLogin.signature);
        }
        
        @Override
        public int hashCode() {
            return Arrays.hashCode(signature);
        }
        
        @Override
        public String toString() {
            return "ZkLogin{signature=" + Arrays.toString(signature) + "}";
        }
    }
} 