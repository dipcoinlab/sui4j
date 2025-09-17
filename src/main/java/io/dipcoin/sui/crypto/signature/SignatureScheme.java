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

package io.dipcoin.sui.crypto.signature;

/**
 * @author : Same
 * @datetime : 2025/6/26 10:45
 * @Description : Sui signature scheme.
 */
public enum SignatureScheme {

    /** Ed25519 signature scheme. */
    ED25519((byte) 0x00),
    /** Secp256k1 signature scheme. */
    SECP256K1((byte) 0x01),
    /** Secp256r1 signature scheme. */
    SECP256R1((byte) 0x02),
    /** multisig signature scheme. */
    MULTISIG((byte) 0x03),
    /** zkLogin signature scheme. */
    ZKLOGIN((byte) 0x05),
    /** passkey signature scheme. */
    PASSKEY((byte) 0x06);


    private final byte scheme;

    SignatureScheme(byte scheme) {
        this.scheme = scheme;
    }

    public byte getScheme() {
        return scheme;
    }

    public static SignatureScheme findByScheme(byte scheme) {
        for (SignatureScheme signatureScheme : values()) {
            if (signatureScheme.getScheme() == (scheme)) {
                return signatureScheme;
            }
        }
        return null;
    }

}
