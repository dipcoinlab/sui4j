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
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.util.encoders.Hex;

/**
 * @author : Same
 * @datetime : 2025/6/26 11:48
 * @Description : Ed25519 key pair
 */
public class Ed25519KeyPair extends SuiKeyPair<AsymmetricCipherKeyPair> {

    public Ed25519KeyPair(byte[] privateKey) {
        Ed25519PrivateKeyParameters privateKeyParameters = new Ed25519PrivateKeyParameters(privateKey, 0);
        Ed25519PublicKeyParameters publicKeyParameters = privateKeyParameters.generatePublicKey();
            this.keyPair = new AsymmetricCipherKeyPair(publicKeyParameters, privateKeyParameters);
    }

    public Ed25519KeyPair(
        Ed25519PrivateKeyParameters privateKeyParameters,
        Ed25519PublicKeyParameters publicKeyParameters) {
            this.keyPair = new AsymmetricCipherKeyPair(publicKeyParameters, privateKeyParameters);
    }

    @Override
    public byte[] publicKeyBytes() {
        return ((Ed25519PublicKeyParameters) keyPair.getPublic()).getEncoded();
    }

    @Override
    public SignatureScheme signatureScheme() {
        return SignatureScheme.ED25519;
    }

    @Override
    public byte[] sign(byte[] msg) throws SigningException {
        Signer signer = new Ed25519Signer();
        signer.init(true, keyPair.getPrivate());
        signer.update(msg, 0, msg.length);
        try {
            return signer.generateSignature();
        } catch (CryptoException e) {
            throw new SigningException("Sui wallet signature failed by Ed25519.");
        }
    }

    @Override
    public String encodePrivateKey() {
        Ed25519PrivateKeyParameters pair = (Ed25519PrivateKeyParameters) this.keyPair.getPrivate();
        return Hex.toHexString(pair.getEncoded());
    }

    @Override
    public byte[] privateKey() {
        Ed25519PrivateKeyParameters pair = (Ed25519PrivateKeyParameters) this.keyPair.getPrivate();
        return pair.getEncoded();
    }

    /**
     * Decode hex sui key pair.
     *
     * @param encoded the encoded
     * @return the sui key pair
     */
    public static Ed25519KeyPair decodeHex(String encoded) {
        return decode(Hex.decode(encoded));
    }

    /**
     * Decode bytes sui key pair.
     *
     * @param seed
     * @return
     */
    public static Ed25519KeyPair decode(byte[] seed) {
        Ed25519PrivateKeyParameters privateKeyParameters = new Ed25519PrivateKeyParameters(seed, 0);
        Ed25519PublicKeyParameters publicKeyParameters = privateKeyParameters.generatePublicKey();
        return new Ed25519KeyPair(privateKeyParameters, publicKeyParameters);
    }

    /**
     * Decode base 64 sui key pair.
     *
     * @param encoded the encoded
     * @return the sui key pair
     */
    public static Ed25519KeyPair decodeBase64(byte[] encoded) {
        Ed25519PrivateKeyParameters privateKeyParameters = new Ed25519PrivateKeyParameters(encoded, 1);
        Ed25519PublicKeyParameters publicKeyParameters = privateKeyParameters.generatePublicKey();
        return new Ed25519KeyPair(privateKeyParameters, publicKeyParameters);
    }

    /**
     * Generate Ed25519 sui key pair.
     *
     * @return the sui key pair
     */
    public static Ed25519KeyPair generate() {
        byte[] seed = new byte[32];
        SECURE_RANDOM.nextBytes(seed);
        return new Ed25519KeyPair(seed);
    }
}
