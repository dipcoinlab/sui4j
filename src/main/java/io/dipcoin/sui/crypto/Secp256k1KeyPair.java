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

package io.dipcoin.sui.crypto;

import io.dipcoin.sui.crypto.exceptions.SigningException;
import io.dipcoin.sui.crypto.signature.SignatureScheme;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Utils;
import org.bouncycastle.util.encoders.Hex;

import java.util.Arrays;

/**
 * @author : Same
 * @datetime : 2025/6/26 16:30
 * @Description : Secp256k1 key pair
 */
public class Secp256k1KeyPair extends SuiKeyPair<ECKey> {

    public Secp256k1KeyPair(byte[] privateKey) {
        this.keyPair = ECKey.fromPrivate(privateKey);
    }

    public Secp256k1KeyPair(String privateKey) {
        this(Hex.decode(privateKey));
    }

    @Override
    public byte[] publicKeyBytes() {
        return keyPair.getPubKey();
    }

    @Override
    public SignatureScheme signatureScheme() {
        return SignatureScheme.SECP256K1;
    }

    @Override
    public byte[] sign(byte[] msg) throws SigningException {
        try {
            Sha256Hash sha256Hash = Sha256Hash.of(msg);
            ECKey.ECDSASignature signature = keyPair.sign(sha256Hash);

            byte[] sigData = new byte[64]; // 32 bytes for R + 32 bytes for S
            System.arraycopy(Utils.bigIntegerToBytes(signature.r, 32), 0, sigData, 0, 32);
            System.arraycopy(Utils.bigIntegerToBytes(signature.s, 32), 0, sigData, 32, 32);
            return sigData;
        } catch (RuntimeException e) {
            throw new SigningException("Sui wallet signature failed by Secp256k1.");
        }
    }

    @Override
    public String encodePrivateKey() {
        return Hex.toHexString(this.keyPair.getPrivKeyBytes());
    }

    @Override
    public byte[] privateKey() {
        return this.keyPair.getPrivKeyBytes();
    }

    /**
     * Decode hex sui key pair.
     *
     * @param encoded the encoded
     * @return the sui key pair
     */
    public static Secp256k1KeyPair decodeHex(String encoded) {
        return decode(Hex.decode(encoded));
    }

    /**
     * Decode bytes sui key pair.
     *
     * @param seed the bytes
     * @return the sui key pair
     */
    public static Secp256k1KeyPair decode(byte[] seed) {
        return new Secp256k1KeyPair(Arrays.copyOfRange(seed, 0, seed.length));
    }

    /**
     * Decode base 64 sui key pair.
     *
     * @param encoded the encoded
     * @return the sui key pair
     */
    public static Secp256k1KeyPair decodeBase64(byte[] encoded) {
        return new Secp256k1KeyPair(Arrays.copyOfRange(encoded, 1, encoded.length));
    }

    /**
     * Generate Secp256k1 sui key pair.
     *
     * @return the sui key pair
     */
    public static Secp256k1KeyPair generate() {
        byte[] seed = new byte[32];
        SECURE_RANDOM.nextBytes(seed);
        return new Secp256k1KeyPair(seed);
    }

}
