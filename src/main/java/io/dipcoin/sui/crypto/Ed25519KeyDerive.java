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

import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;

import java.nio.ByteBuffer;
import java.util.regex.Pattern;

/**
 * @author : Same
 * @datetime : 2025/10/21 16:26
 * @Description : Ed25519 key derive
 */
public class Ed25519KeyDerive {

    private static final String ED25519_CURVE = "ed25519 seed";
    private static final int HARDENED_OFFSET = 0x80000000;
    private static final Pattern PATH_REGEX = Pattern.compile("^m(/[0-9]+')+$");

    private byte[] key;
    private byte[] chainCode;

    public Ed25519KeyDerive(byte[] key, byte[] chainCode) {
        this.key = key;
        this.chainCode = chainCode;
    }

    public static Ed25519KeyDerive getMasterKeyFromSeed(String seedHex) {
        byte[] seed = Hex.decode(seedHex);
        byte[] I = hmacSha512(ED25519_CURVE.getBytes(), seed);
        byte[] IL = new byte[32];
        byte[] IR = new byte[32];
        System.arraycopy(I, 0, IL, 0, 32);
        System.arraycopy(I, 32, IR, 0, 32);
        return new Ed25519KeyDerive(IL, IR);
    }

    public static Ed25519KeyDerive CKDPriv(Ed25519KeyDerive parent, int index) {
        byte[] indexBytes = ByteBuffer.allocate(4).putInt(index).array();
        int length = parent.key.length;
        byte[] data = new byte[1 + length + 4];
        data[0] = 0;
        System.arraycopy(parent.key, 0, data, 1, length);
        System.arraycopy(indexBytes, 0, data, 1 + length, 4);

        byte[] I = hmacSha512(parent.chainCode, data);
        byte[] IL = new byte[32];
        byte[] IR = new byte[32];
        System.arraycopy(I, 0, IL, 0, 32);
        System.arraycopy(I, 32, IR, 0, 32);
        return new Ed25519KeyDerive(IL, IR);
    }

    public static Ed25519KeyDerive derivePath(String path, String seedHex) {
        if (!isValidPath(path)) {
            throw new IllegalArgumentException("Invalid derivation path: " + path);
        }

        Ed25519KeyDerive master = getMasterKeyFromSeed(seedHex);
        String[] segments = path.split("/");
        Ed25519KeyDerive current = master;

        for (int i = 1; i < segments.length; i++) {
            String seg = segments[i].replace("'", "");
            int index = Integer.parseInt(seg);
            current = CKDPriv(current, index + HARDENED_OFFSET);
        }

        return current;
    }

    public static boolean isValidPath(String path) {
        if (!PATH_REGEX.matcher(path).matches()) return false;
        String[] parts = path.split("/");
        for (int i = 1; i < parts.length; i++) {
            try {
                Integer.parseInt(parts[i].replace("'", ""));
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    private static byte[] hmacSha512(byte[] key, byte[] data) {
        HMac hmac = new HMac(new SHA512Digest());
        hmac.init(new KeyParameter(key));
        hmac.update(data, 0, data.length);
        byte[] out = new byte[64];
        hmac.doFinal(out, 0);
        return out;
    }

    public byte[] getKey() {
        return key;
    }

    public byte[] getChainCode() {
        return chainCode;
    }
}
