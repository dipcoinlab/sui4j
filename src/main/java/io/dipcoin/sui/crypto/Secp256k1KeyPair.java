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

import io.dipcoin.sui.crypto.exceptions.MnemonicsException;
import io.dipcoin.sui.crypto.exceptions.SigningException;
import io.dipcoin.sui.crypto.signature.SignatureScheme;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bouncycastle.util.encoders.Hex;

import java.util.Arrays;
import java.util.List;

/**
 * @author : Same
 * @datetime : 2025/6/26 16:30
 * @Description : Secp256k1 key pair
 */
public class Secp256k1KeyPair extends SuiKeyPair<ECKey> {

    private final static String DEFAULT_ED25519_DERIVATION_PATH = "m/54'/784'/0'/0/0";

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
     * Derive Secp256k1 keypair from mnemonics and path. The mnemonics must be normalized
     * and validated against the english wordlist.
     *
     * If path is none, it will default to m/54'/784'/0'/0/0, otherwise the path must
     * be compliant to BIP-32 in form m/54'/784'/{account_index}'/{change_index}/{address_index}.
     * @param mnemonics
     * @param path
     * @return
     */
    public static Secp256k1KeyPair deriveKeypair(String mnemonics, String path) {
        if (path == null) {
            path = DEFAULT_ED25519_DERIVATION_PATH;
        }

        if (!Mnemonics.isValidBIP32Path(path)) {
            throw new MnemonicsException("Invalid derivation path");
        }
        try {
            DeterministicSeed seed = new DeterministicSeed(mnemonics, null, "", 0L);
            DeterministicKeyChain chain = DeterministicKeyChain.builder().seed(seed).build();
            List<ChildNumber> pathList = Mnemonics.parsePath(path);
            DeterministicKey key = chain.getKeyByPath(pathList, true);

            return new Secp256k1KeyPair(key.getPrivKeyBytes());
        } catch (Exception e) {
            throw new MnemonicsException("Failed to derive Secp256k1 keypair", e);
        }
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
