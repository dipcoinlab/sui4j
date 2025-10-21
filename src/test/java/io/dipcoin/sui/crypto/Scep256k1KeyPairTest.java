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
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author : Same
 * @datetime : 2025/6/26 18:42
 * @Description : Scep256k1key pair test
 */
public class Scep256k1KeyPairTest {


    // Test with a fixed key (Base64-encoded 32-byte private key)
    private static final String VALID_HEX_KEY =
            "232582e509d23b19f93d70ebe0e6190f592832281569755b7e41604a94dd5286";
    // Replace with the actual computed address
    private static final String EXPECTED_ADDRESS =
            "0x7fba051ae3dcfd21414b0d98063fa4403f3de46bbb8219ab3dfd2ee12d60ddfa";
    private static final String TEST_MESSAGE = "hello_sui_secp256k1";

    @Test
    void createSuiKeyPair() {
        // When
        SuiKeyPair<ECKey> keyPair = Secp256k1KeyPair.generate();
        String address = keyPair.address();
        String key = keyPair.encodePrivateKey();
        System.out.println("Scep256k1     key length : " + Hex.decode(key).length);
        System.out.println("Scep256k1     key : " + key);
        System.out.println("Scep256k1 address : " + address);

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
        Secp256k1KeyPair keyPair = Secp256k1KeyPair.deriveKeypair(seed, null);
        String address = keyPair.address();
        System.out.println("Scep256k1    seed : " + seed);
        System.out.println("Scep256k1 address : " + address);
        System.out.println(" ---------------------------------------------------");

        // Then
        assertThat(keyPair).isNotNull();
        assertThat(keyPair.getKeyPair()).isNotNull();
        assertThat(address).isNotNull()
                .startsWith("0x")
                .hasSize(66);
    }

    @Test
    void constructor_shouldWorkWithValidKey() {
        // When
        Secp256k1KeyPair keyPair = new Secp256k1KeyPair(VALID_HEX_KEY);

        // Then
        assertThat(keyPair).isNotNull();
        assertThat(keyPair.getKeyPair()).isNotNull();
    }

    @Test
    void address_shouldGenerateCorrectFormat() {
        // Given
        Secp256k1KeyPair keyPair = new Secp256k1KeyPair(VALID_HEX_KEY);

        // When
        String address = keyPair.address();

        // Then
        assertThat(address)
                .startsWith("0x")
                .hasSize(66) // 0x + 20-byte address (ETH style).
                .isEqualTo(EXPECTED_ADDRESS); // Precomputed verification values are required.
    }

    @Test
    void encodePrivateKey_shouldReturnOriginalHex() {
        // Given
        Secp256k1KeyPair keyPair = new Secp256k1KeyPair(VALID_HEX_KEY);

        // When
        String encoded = keyPair.encodePrivateKey();

        // Then
        assertThat(encoded)
                .isEqualTo(VALID_HEX_KEY.toLowerCase()) // Uniform lowercase.
                .hasSize(64); // 32-byte hexadecimal representation.
    }

//    @Test
//    void publicKeyBytes_shouldReturnCompressedFormat() {
//        // Given
//        Secp256k1KeyPair keyPair = new Secp256k1KeyPair(VALID_HEX_KEY);
//
//        // When
//        byte[] pubKey = keyPair.publicKeyBytes();
//
//        // Then
//        assertThat(pubKey)
//                .hasSize(33) // Compressed public key is 33 bytes (0x02 prefix)
//                .startsWith((byte) 0x02); // Valid compression prefix.
//    }

    @Test
    void sign_shouldGenerateValidSignature() {
        // Given
        Secp256k1KeyPair keyPair = new Secp256k1KeyPair(VALID_HEX_KEY);
        byte[] message = TEST_MESSAGE.getBytes();

        // When
        byte[] signature = keyPair.sign(message);

        // Then
        assertThat(signature)
                .hasSize(64) // R(32) + S(32)
                .isNotEqualTo(message); // The signature should differ from the original message.
    }

//    @Test
//    void sign_shouldBeDeterministic() {
//        // Given
//        Secp256k1KeyPair keyPair = new Secp256k1KeyPair(VALID_HEX_KEY);
//        byte[] message = TEST_MESSAGE.getBytes();
//
//        // When
//        byte[] sig1 = keyPair.sign(message);
//        byte[] sig2 = keyPair.sign(message);
//
//        // Then (SECP256K1 signatures are non-deterministic by default; RFC6979 must be enabled).
//        assertThat(sig1).isNotEqualTo(sig2); // Unless explicitly using deterministic signatures.
//    }

//    @Test
//    void signatureShouldBeVerifiable() {
//        Secp256k1KeyPair keyPair = new Secp256k1KeyPair(VALID_HEX_KEY);
//        byte[] message = TEST_MESSAGE.getBytes();
//        byte[] signature = keyPair.sign(message);
//
//        // Use the BC library to verify the signature.
//        ECDSASigner verifier = new ECDSASigner();
//        verifier.init(false, );
//        BigInteger r = new BigInteger(1, Arrays.copyOfRange(signature, 0, 32));
//        BigInteger s = new BigInteger(1, Arrays.copyOfRange(signature, 32, 64));
//        assertThat(verifier.verifySignature(message, r, s)).isTrue();
//    }

//    @Test
//    void decodeBase64_shouldHandleKeyWithPrefix() {
//        // Given (Base64-encoded key with scheme prefix).
//        String encoded = "AQ" + Base64.toBase64String(Hex.decode(VALID_HEX_KEY));
//
//        // When
//        SuiKeyPair<?> keyPair = Secp256k1KeyPair.decodeBase64(encoded);
//
//        // Then
//        assertThat(keyPair)
//                .isInstanceOf(Secp256k1KeyPair.class)
//                .extracting(SuiKeyPair::signatureScheme)
//                .isEqualTo(SignatureScheme.SECP256K1);
//    }

//    @Test
//    void constructor_shouldThrowOnInvalidKey() {
//        // Invalid key cases
//        String[] invalidKeys = {
//                "",                          // null string
//                "123",                       // too short
//                VALID_HEX_KEY + "00",        // too long
//                VALID_HEX_KEY.replace('a', 'x') // Non-hex characters.
//        };
//
//        for (String invalidKey : invalidKeys) {
//            assertThatThrownBy(() -> new Secp256k1KeyPair(invalidKey))
//                    .isInstanceOf(IllegalArgumentException.class);
//        }
//    }

    // Boundary testing: Private keys set to 1 and n-1 (where n is the curve order).
//    @Test
//    void boundaryKeyTests() {
//        String[] boundaryKeys = {
//                "0000000000000000000000000000000000000000000000000000000000000001",
//                "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364140" // n-1
//        };
//
//        for (String key : boundaryKeys) {
//            Secp256k1KeyPair keyPair = new Secp256k1KeyPair(key);
//            assertThat(keyPair.address()).isNotNull();
//            assertThat(keyPair.sign(TEST_MESSAGE.getBytes())).hasSize(64);
//        }
//    }
}
