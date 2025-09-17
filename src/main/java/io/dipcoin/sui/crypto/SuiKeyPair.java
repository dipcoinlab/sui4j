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

import io.dipcoin.sui.bcs.BcsRegistry;
import io.dipcoin.sui.bcs.types.intent.IntentScope;
import io.dipcoin.sui.crypto.exceptions.SignatureSchemeNotSupportedException;
import io.dipcoin.sui.crypto.exceptions.SigningException;
import io.dipcoin.sui.crypto.signature.SignatureScheme;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.jcajce.provider.digest.Blake2b;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.security.Security;

/**
 * @author : Same
 * @datetime : 2025/6/26 10:23
 * @Description : Sui handles key pair abstract class.
 */
public abstract class SuiKeyPair<T> {

    protected final static String ADDRESS_PREFIX = "0x";
    protected static final SecureRandom SECURE_RANDOM = new SecureRandom();

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /** The Key pair. */
    protected T keyPair;

    /**
     * Gets key pair.
     *
     * @return the key pair
     */
    public T getKeyPair() {
        return keyPair;
    }

    @Override
    public String toString() {
        return "SuiKeyPair{" + "keyPair=" + keyPair + '}';
    }

    /**
     * Address string.
     *
     * @return the string
     */
    public String address() {
        byte[] pubKey = this.publicKeyBytes();
        // 1. Concatenate flag + pubkey
        byte[] flagAndPubkey = new byte[1 + pubKey.length];
        flagAndPubkey[0] = this.signatureScheme().getScheme(); // flag
        System.arraycopy(pubKey, 0, flagAndPubkey, 1, pubKey.length);

        // 2. Blake2b-256 generate address
        Blake2b.Blake2b256 blake2b256 = new Blake2b.Blake2b256();
        blake2b256.update(flagAndPubkey, 0, flagAndPubkey.length);
        byte[] addressBytes = blake2b256.digest();

        return ADDRESS_PREFIX + Hex.toHexString(addressBytes);
    }

    /**
     * Public key string.
     *
     * @return the string
     */
    public String publicKey() {
        return Hex.toHexString(this.publicKeyBytes());
    }

    /**
     * Public key byte [ ].
     *
     * @return the byte [ ]
     */
    public abstract byte[] publicKeyBytes();

    /**
     * Signature scheme.
     *
     * @return the signature scheme
     */
    public abstract SignatureScheme signatureScheme();

    /**
     * sui signWithIntent.
     * format:flag || signature || pubkey
     * @param msgBytes the msg bytes
     * @param intentScope intentScope
     * @return the bytes
     * @throws SigningException the signing exception
     */
    public byte[] signWithIntent(byte[] msgBytes, IntentScope intentScope) throws SigningException {
        // 1. Concatenate intent
        int msgLength = msgBytes.length;
        byte[] intentBytes = intentScope.getScope();
        int intentLength = intentBytes.length;
        byte[] intentMessage = new byte[intentLength + msgLength];
        System.arraycopy(intentBytes, 0, intentMessage, 0, intentLength);
        System.arraycopy(msgBytes, 0, intentMessage, intentLength, msgLength);

        // 2. calculate digest(Blake2b-256)
        byte[] digest = blake2b256(intentMessage);
        // 3. sign
        byte[] signature = this.sign(digest);
        byte[] pubkey = this.publicKeyBytes();
        ByteBuffer buffer = ByteBuffer.allocate(1 + signature.length + pubkey.length);
        buffer.put(this.signatureScheme().getScheme());
        buffer.put(signature);
        buffer.put(pubkey);
        return buffer.array();
    }

    /**
     * sui signWithIntent.
     * format:signature
     * @param msgBytes the msg bytes
     * @param intentScope intentScope
     * @return the bytes
     * @throws SigningException the signing exception
     */
    public byte[] signSignatureWithIntent(byte[] msgBytes, IntentScope intentScope) throws SigningException {
        // 1. Concatenate intent
        int msgLength = msgBytes.length;
        byte[] intentBytes = intentScope.getScope();
        int intentLength = intentBytes.length;
        byte[] intentMessage = new byte[intentLength + msgLength];
        System.arraycopy(intentBytes, 0, intentMessage, 0, intentLength);
        System.arraycopy(msgBytes, 0, intentMessage, intentLength, msgLength);
        // 2. calculate digest(Blake2b-256)
        byte[] digest = blake2b256(intentMessage);
        // 3. sign
        return this.sign(digest);
    }

    /**
     * sui signWithIntent.
     * return sign format: flag || signature || pubkey
     * @param msgBytes the msg bytes
     * @param intentScope intentScope
     * @return the base64 string
     * @throws SigningException the signing exception
     */
    public String signWithIntentBase64(byte[] msgBytes, IntentScope intentScope) throws SigningException {
        return Base64.toBase64String(this.signWithIntent(msgBytes, intentScope));
    }

    /**
     * sui signWithIntent. Personal
     * return sign format: flag || signature || pubkey
     * @param msg the msg bytes
     * @return the base64 bytes
     * @throws SigningException the signing exception
     */
    public byte[] signPersonalMessage(byte[] msg) throws SigningException, IOException {
        byte[] bytes = BcsRegistry.serializeToBytes(msg, BcsRegistry.BYTE_ARRAY_SERIALIZER);
        return this.signWithIntent(bytes, IntentScope.PersonalMessage.INSTANCE);
    }

    /**
     * sui signWithIntent. Personal
     * return sign format: flag || signature || pubkey
     * @param msg the msg
     * @return the base64 bytes
     * @throws SigningException the signing exception
     */
    public byte[] signPersonalMessage(String msg) throws SigningException, IOException {
        byte[] bytes = BcsRegistry.serializeToBytes(msg, BcsRegistry.STRING_SERIALIZER);
        return this.signWithIntent(bytes, IntentScope.PersonalMessage.INSTANCE);
    }

    /**
     * sui signWithIntent. Personal
     * return sign format: flag || signature || pubkey
     * @param msg the msg byte
     * @return the base64 string
     * @throws SigningException the signing exception
     */
    public String signPersonalMessageBase64(byte[] msg) throws SigningException, IOException {
        return Base64.toBase64String(this.signPersonalMessage(msg));
    }

    /**
     * sui signWithIntent. Personal
     * return sign format: flag || signature || pubkey
     * @param msg the msg
     * @return the base64 string
     * @throws SigningException the signing exception
     */
    public String signPersonalMessageBase64(String msg) throws SigningException, IOException {
        return Base64.toBase64String(this.signPersonalMessage(msg));
    }

    /**
     * sui signWithIntent. TransactionData
     * return sign format: flag || signature || pubkey
     * @param msg the msg String
     * @return the bytes
     * @throws SigningException the signing exception
     */
    public byte[] signTransactionData(String msg) throws SigningException {
        return this.signWithIntent(Base64.decode(msg), IntentScope.TransactionData.INSTANCE);
    }

    /**
     * sui signWithIntent. TransactionData
     * return sign format: flag || signature || pubkey
     * @param msgBytes the msg base64 bytes
     * @return the bytes
     * @throws SigningException the signing exception
     */
    public byte[] signTransactionData(byte[] msgBytes) throws SigningException {
        return this.signWithIntent(msgBytes, IntentScope.TransactionData.INSTANCE);
    }

    /**
     * sui signWithIntent. TransactionData
     * return sign format: flag || signature || pubkey
     * @param msg the msg String
     * @return the base64 string
     * @throws SigningException the signing exception
     */
    public String signTransactionDataBase64(String msg) throws SigningException {
        return Base64.toBase64String(this.signWithIntent(Base64.decode(msg), IntentScope.TransactionData.INSTANCE));
    }

    /**
     * sui signWithIntent. TransactionData
     * return sign format: flag || signature || pubkey
     * @param msgBytes the msg base64 bytes
     * @return the base64 string
     * @throws SigningException the signing exception
     */
    public String signTransactionDataBase64(byte[] msgBytes) throws SigningException {
        return Base64.toBase64String(this.signWithIntent(msgBytes, IntentScope.TransactionData.INSTANCE));
    }

    /**
     * sui signWithIntent. Personal
     * return sign format: signature
     * @param msg the msg bytes
     * @return the base64 bytes
     * @throws SigningException the signing exception
     */
    public byte[] signSignaturePersonalMessage(byte[] msg) throws SigningException, IOException {
        byte[] bytes = BcsRegistry.serializeToBytes(msg, BcsRegistry.BYTE_ARRAY_SERIALIZER);
        return this.signSignatureWithIntent(bytes, IntentScope.PersonalMessage.INSTANCE);
    }

    /**
     *  base sign string.
     *
     * @param msg the msg
     * @return the string
     * @throws SigningException the signing exception
     */
    public abstract byte[] sign(byte[] msg) throws SigningException;


    /**
     * blake2b256 summary
     * @param message
     * @return
     */
    public static byte[] blake2b256(byte[] message) {
        Blake2bDigest digest = new Blake2bDigest(256);
        digest.update(message, 0, message.length);
        byte[] hash = new byte[32];
        digest.doFinal(hash, 0);
        return hash;
    }

    /**
     * Decode base64 sui key pair.
     *
     * @param encoded the encoded
     * @return the sui key pair
     * @throws SignatureSchemeNotSupportedException the signature scheme not supported exception
     */
    public static SuiKeyPair<?> decodeBase64(String encoded)
            throws SignatureSchemeNotSupportedException {
        final byte[] keyPairBytes = Base64.decode(encoded);

        final SignatureScheme scheme = SignatureScheme.findByScheme(keyPairBytes[0]);
        if (scheme == null) {
            throw new SignatureSchemeNotSupportedException();
        }
        return switch (scheme) {
            case ED25519 -> Ed25519KeyPair.decodeBase64(keyPairBytes);
            case SECP256K1 -> Secp256k1KeyPair.decodeBase64(keyPairBytes);
//            case SECP256R1 -> Secp256r1KeyPair.decodeBase64(keyPairBytes);
//            case MULTISIG -> MultisigKeyPair.decodeBase64(keyPairBytes);
//            case ZKLOGIN -> ZkLoginKeyPair.decodeBase64(keyPairBytes);
//            case PASSKEY -> PassKeyKeyPair.decodeBase64(keyPairBytes);
            default -> throw new SignatureSchemeNotSupportedException();
        };
    }

    /**
     * Decode hex sui key pair.
     *
     * @param encoded the encoded
     * @return the sui key pair
     * @throws SignatureSchemeNotSupportedException the signature scheme not supported exception
     */
    public static SuiKeyPair<?> decodeHex(String encoded, SignatureScheme scheme)
            throws SignatureSchemeNotSupportedException {
        return switch (scheme) {
            case ED25519 -> Ed25519KeyPair.decodeHex(encoded);
            case SECP256K1 -> Secp256k1KeyPair.decodeHex(encoded);
//            case SECP256R1 -> Secp256r1KeyPair.decodeHex(keyPairBytes);
//            case MULTISIG -> MultisigKeyPair.decodeHex(keyPairBytes);
//            case ZKLOGIN -> ZkLoginKeyPair.decodeHex(keyPairBytes);
//            case PASSKEY -> PassKeyKeyPair.decodeHex(keyPairBytes);
            default -> throw new SignatureSchemeNotSupportedException();
        };
    }

    /**
     * Decode bytes sui key pair.
     *
     * @param encoded the bytes
     * @return the sui key pair
     * @throws SignatureSchemeNotSupportedException the signature scheme not supported exception
     */
    public static SuiKeyPair<?> decode(byte[] encoded, SignatureScheme scheme)
            throws SignatureSchemeNotSupportedException {
        return switch (scheme) {
            case ED25519 -> Ed25519KeyPair.decode(encoded);
            case SECP256K1 -> Secp256k1KeyPair.decode(encoded);
//            case SECP256R1 -> Secp256r1KeyPair.decode(keyPairBytes);
//            case MULTISIG -> MultisigKeyPair.decode(keyPairBytes);
//            case ZKLOGIN -> ZkLoginKeyPair.decode(keyPairBytes);
//            case PASSKEY -> PassKeyKeyPair.decode(keyPairBytes);
            default -> throw new SignatureSchemeNotSupportedException();
        };
    }

    /**
     * encode hex sui key.
     *
     * @return the sui key
     */
    public abstract String encodePrivateKey();

    /**
     * byte[] sui key.
     *
     * @return the sui key
     */
    public abstract byte[] privateKey();
}
