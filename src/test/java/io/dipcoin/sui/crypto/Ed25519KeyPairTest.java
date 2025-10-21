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

import org.bitcoinj.core.ECKey;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author : Same
 * @datetime : 2025/6/26 17:40
 * @Description : Ed25519 key pair test
 */
public class Ed25519KeyPairTest {

    // Test with a fixed key (Base64-encoded 32-byte private key)
    private static final String VALID_BASE64_KEY =
            "f60a65eb1a75838674ecc613a0c8d28ab49635cc2bd7697f1b0019193dc2f148";
    // Replace with the actual computed address
    private static final String EXPECTED_ADDRESS =
            "0xe63f4d12bcf145c510f2788f0bf8cf81a7cd5035d5a502d846b9517457973c1d";
    private static final String TEST_MESSAGE = "hello_sui";

    @Test
    void createSuiKeyPair() {
        // When
        SuiKeyPair<AsymmetricCipherKeyPair> keyPair = Ed25519KeyPair.generate();
        String address = keyPair.address();
        String key = keyPair.encodePrivateKey();
        System.out.println("Ed25519     key : " + key);
        System.out.println("Ed25519     key length : " + Hex.decode(key).length);
        System.out.println("Ed25519     public key : " + Base64.toBase64String(keyPair.publicKeyBytes()));
        System.out.println("Ed25519     public publicKeyBytes length : " + Base64.toBase64String(keyPair.publicKeyBytes()).length());
        System.out.println("Ed25519     public key length : " + keyPair.publicKeyBytes().length);
        System.out.println("Ed25519 address : " + address);
        System.out.println(" ---------------------------------------------------");

        // When
        SuiKeyPair<ECKey> key256Pair = Secp256k1KeyPair.generate();
        String address256 = key256Pair.address();
        String key256 = key256Pair.encodePrivateKey();
        System.out.println("Secp256k1     key : " + key256);
        System.out.println("Secp256k1     key length : " + Hex.decode(key256).length);
        System.out.println("Secp256k1     public key : " + Base64.toBase64String(key256Pair.publicKeyBytes()));
        System.out.println("Secp256k1     public publicKeyBytes length : " + Base64.toBase64String(key256Pair.publicKeyBytes()).length());
        System.out.println("Secp256k1     public key length : " + key256Pair.publicKeyBytes().length);
        System.out.println("Secp256k1 address : " + address256);

        // Then
        assertThat(keyPair).isNotNull();
        assertThat(keyPair.getKeyPair()).isNotNull();
        assertThat(Hex.decode(key))
                .isNotNull()
                .hasSize(32);
        assertThat(address).isNotNull()
                .startsWith("0x")
                .hasSize(66);
    }

    @Test
    void createSuiKeyDerive() {
        // When
        String seed = Mnemonics.generateMnemonics();
        Ed25519KeyPair keyPair = Ed25519KeyPair.deriveKeypair(seed, null);
        String address = keyPair.address();
        System.out.println("Ed25519    seed : " + seed);
        System.out.println("Ed25519 address : " + address);
        System.out.println(" ---------------------------------------------------");

        // Then
        assertThat(keyPair).isNotNull();
        assertThat(keyPair.getKeyPair()).isNotNull();
        assertThat(address).isNotNull()
                .startsWith("0x")
                .hasSize(66);
    }

    @Test
    void importSuiKeyDerive() {
        // 0xe32e147fe5cd982f8f251de32ded6d8f1ecc173eb8efa445191bce2b1e63046b
        // When
        String suiKeyPair = "suiprivkey1qzagu7s3jtl44n9j7rrsn2595fcudya0q9pxmupv9v8eaxvcjrtpzs893s5";
        SuiKeyPair keyPair = Ed25519KeyPair.decodeSuiPrivateKey(suiKeyPair);
        String address = keyPair.address();
        System.out.println("Ed25519 address : " + address);
        System.out.println(" ---------------------------------------------------");

        // Then
        assertThat(keyPair).isNotNull();
        assertThat(keyPair.getKeyPair()).isNotNull();
        assertThat(address).isNotNull()
                .startsWith("0x")
                .hasSize(66);
        assertThat(address).isEqualTo("0xe32e147fe5cd982f8f251de32ded6d8f1ecc173eb8efa445191bce2b1e63046b");
    }

    @Test
    void decodeBase64_shouldWorkWithValidKey() {
        // When
        Ed25519KeyPair keyPair = (Ed25519KeyPair) Ed25519KeyPair.decodeHex(VALID_BASE64_KEY);

        // Then
        assertThat(keyPair).isNotNull();
        assertThat(keyPair.getKeyPair()).isInstanceOf(AsymmetricCipherKeyPair.class);
    }

    @Test
    void address_shouldGenerateCorrectFormat() {
        // Given
        Ed25519KeyPair keyPair = (Ed25519KeyPair) Ed25519KeyPair.decodeHex(VALID_BASE64_KEY);

        // When
        String address = keyPair.address();

        // Then
        assertThat(address)
                .startsWith("0x")
                .hasSize(66) // 0x + 32-byte hex
                .isEqualTo(EXPECTED_ADDRESS); // Precomputed verification values are required.
    }

    @Test
    void encodePrivateKey_shouldReturnOriginalHex() {
        // Given
        Ed25519KeyPair keyPair = (Ed25519KeyPair) Ed25519KeyPair.decodeHex(VALID_BASE64_KEY);

        // When
        String encoded = keyPair.encodePrivateKey();

        // Then
        assertThat(encoded)
                .isEqualTo(VALID_BASE64_KEY) // The Base64 decoded output should match the original input.
                .hasSize(64); // 32-byte hexadecimal representation.
    }

    @Test
    void publicKeyBytes_shouldReturn32Bytes() {
        // Given
        Ed25519KeyPair keyPair = (Ed25519KeyPair) Ed25519KeyPair.decodeHex(VALID_BASE64_KEY);

        // When
        byte[] pubKey = keyPair.publicKeyBytes();

        // Then
        assertThat(pubKey)
                .hasSize(32)
                .isEqualTo(((Ed25519PublicKeyParameters)keyPair.getKeyPair().getPublic()).getEncoded());
    }

    @Test
    void sign_shouldGenerateValidSignature() {
        // Given
        Ed25519KeyPair keyPair = (Ed25519KeyPair) Ed25519KeyPair.decodeHex(VALID_BASE64_KEY);
        byte[] message = TEST_MESSAGE.getBytes();

        // When
        byte[] signature = keyPair.sign(message);

        // Then
        assertThat(signature)
                .hasSize(64) // Ed25519 signature is fixed at 64 bytes.
                .isNotEqualTo(message); // The signature should differ from the original message.
    }

//    @Test
//    void sign_shouldThrowOnEmptyMessage() {
//        // Given
//        Ed25519KeyPair keyPair = (Ed25519KeyPair) Ed25519KeyPair.decodeHex(VALID_BASE64_KEY);
//
//        // Then
//        assertThrows(SigningException.class, () -> keyPair.sign(new byte[0]));
//    }

//    @Test
//    void decodeBase64_shouldThrowOnInvalidKey() {
//        // Invalid key cases
//        String[] invalidKeys = {
//                "",                          // null string
//                "123",                       // too short
//                VALID_BASE64_KEY + "00",     // too long
//                "6f1c5cc83e7fdb63a163a82eb9a25d1fb8eb72873904ecf0b838fb1fd2f629xx" // Non-hex characters
//        };
//
//        for (String invalidKey : invalidKeys) {
//            assertThatThrownBy(() -> Ed25519KeyPair.decodeHex(invalidKey))
//                    .isInstanceOf(IllegalArgumentException.class);
//        }
//    }

    // Boundary testing: All-zero and all-F keys
    @Test
    void boundaryKeyTests() {
        String[] boundaryKeys = {
                "0000000000000000000000000000000000000000000000000000000000000000",
                "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
        };

        for (String key : boundaryKeys) {
            Ed25519KeyPair keyPair = (Ed25519KeyPair) Ed25519KeyPair.decodeHex(key);
            assertThat(keyPair.address()).isNotNull();
            assertThat(keyPair.sign(TEST_MESSAGE.getBytes())).hasSize(64);
        }
    }
}
