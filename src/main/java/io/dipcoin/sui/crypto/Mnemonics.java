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
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bouncycastle.util.encoders.Hex;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author : Same
 * @datetime : 2025/10/21 14:11
 * @Description : mnemonics
 */
public class Mnemonics {

    private static final Pattern HARDENED_PATH_PATTERN =
            Pattern.compile("^m/44'/784'/[0-9]+'/[0-9]+'/[0-9]+'+$");

    private static final Pattern BIP32_PATH_PATTERN =
            Pattern.compile("^m/(54|74)'/784'/[0-9]+'/[0-9]+/[0-9]+$");

    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * Parse and validate a path that is compliant to SLIP-0010 in form m/44'/784'/{account_index}'/{change_index}'/{address_index}'.
     *
     * @return path path string (e.g. `m/44'/784'/0'/0'/0'`).
     */
    public static boolean isValidHardenedPath(String path) {
        return HARDENED_PATH_PATTERN.matcher(path).matches();
    }

    /**
     * Parse and validate a path that is compliant to BIP-32 in form m/54'/784'/{account_index}'/{change_index}/{address_index}
     * for Secp256k1 and m/74'/784'/{account_index}'/{change_index}/{address_index} for Secp256r1.
     *
     * Note that the purpose for Secp256k1 is registered as 54, to differentiate from Ed25519 with purpose 44.
     *
     * @param path path string (e.g. `m/54'/784'/0'/0/0`).
     */
    public static boolean isValidBIP32Path(String path) {
        return BIP32_PATH_PATTERN.matcher(path).matches();
    }

    /**
     * Uses KDF to derive 64 bytes of key data from mnemonic with empty password.
     *
     * @param mnemonics 12 words string split by spaces.
     */
    public static byte[] mnemonicToSeed(String mnemonics) {
        try {
            DeterministicSeed seed = new DeterministicSeed(mnemonics, null, "", 0L);
            return seed.getSeedBytes();
        } catch (UnreadableWalletException e) {
            throw new SigningException("Invalid mnemonic!", e);
        }
    }

    /**
     * Derive the seed in hex format from a 12-word mnemonic string.
     *
     * @param mnemonics 12 words string split by spaces.
     */
    public static String mnemonicToSeedHex(String mnemonics) {
        return Hex.toHexString(mnemonicToSeed(mnemonics));
    }

    /**
     * parse path
     * @param path
     * @return
     */
    public static List<ChildNumber> parsePath(String path) {
        String[] segments = path.split("/");
        List<ChildNumber> result = new ArrayList<>();
        for (int i = 1; i < segments.length; i++) {
            String seg = segments[i];
            boolean hardened = seg.endsWith("'");
            int index = Integer.parseInt(seg.replace("'", ""));
            result.add(new ChildNumber(index, hardened));
        }
        return result;
    }

    /**
     * Generate 12 mnemonic words (default 128-bit entropy)
     * @return
     */
    public static String generateMnemonics() {
        try {
            byte[] entropy = new byte[16]; // 128 bits = 12 words
            secureRandom.nextBytes(entropy);

            List<String> mnemonicWords = MnemonicCode.INSTANCE.toMnemonic(entropy);

            return String.join(" ", mnemonicWords);
        } catch (Exception e) {
            throw new MnemonicsException("Failed to generate mnemonics", e);
        }
    }

    /**
     * Generate mnemonic phrases of specified strength (12/15/18/21/24 words)
     * @param entropyBits Entropy length, values: 128 / 160 / 192 / 224 / 256
     * @return
     */
    public static String generateMnemonics(int entropyBits) {
        if (entropyBits % 32 != 0 || entropyBits < 128 || entropyBits > 256) {
            throw new MnemonicsException("Entropy must be one of 128, 160, 192, 224, 256 bits");
        }

        try {
            byte[] entropy = new byte[entropyBits / 8];
            secureRandom.nextBytes(entropy);
            List<String> mnemonicWords = MnemonicCode.INSTANCE.toMnemonic(entropy);
            return String.join(" ", mnemonicWords);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate mnemonics", e);
        }
    }

}
