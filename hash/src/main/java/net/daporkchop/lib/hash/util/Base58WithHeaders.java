/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.hash.util;

import lombok.NonNull;
import net.daporkchop.lib.encoding.basen.Base58;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Base58 with addtional version and checksum headers
 *
 * @author DaPorkchop_
 */
public class Base58WithHeaders {
    /**
     * Encodes the data to Base58, with some additional headers:
     * - Data is prefixed by a "version"
     * - Data is suffixed by a 4-byte hash of the content
     *
     * @param version The version of the address
     * @param prefix  A prefix string that will be shown before the content
     * @param content The actual data
     * @return A string encoded in Base58
     */
    public static String encode(byte version, @NonNull String prefix, @NonNull byte[] content) {
        if (prefix == null) {
            prefix = "";
        }
        if (prefix.length() > 57) {
            throw new IllegalArgumentException("Prefix too long! Should be max. 57 letters");
        }

        byte[] newData = new byte[
                1 //version
                        + content.length //actual data
                        + 4 //hash suffix
                ];
        newData[0] = version;
        System.arraycopy(content, 0, newData, 1, content.length);
        for (int i = newData.length - 4; i < newData.length; i++) {
            newData[i] = version; //fill with random data for hash
        }

        byte[] hash = Digest.SHA512.hash(Digest.SHA512.hash(new byte[]{version}, prefix.getBytes(StandardCharsets.UTF_8), content).getHash()).getHash();
        System.arraycopy(hash, 0, newData, (newData.length - 4), 4);
        return Base58.ALPHABET.charAt(prefix.length()) + prefix + Base58.encodeBase58(newData);
    }

    /**
     * Decodes a Base58 string and validates its checksum
     *
     * @param pork58 Input string, Pork58-encoded
     * @return Decoded data!
     */
    public static Decoded decode(@NonNull String pork58) {
        List<Character> chars = pork58.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
        char prefixLengthChar = chars.remove(0);
        int prefixLength = -1;
        for (int i = 0; i < 58; i++) {
            if (Base58.ALPHABET.charAt(i) == (int) prefixLengthChar) {
                prefixLength = i;
            }
        }
        if (prefixLength == -1) {
            return null;
        }
        StringBuilder prefix = new StringBuilder(prefixLength);
        for (int i = 0; i < prefixLength; i++) {
            prefix.append((char) chars.remove(0));
        }
        StringBuilder remainingData = new StringBuilder(chars.size());
        chars.forEach(character -> remainingData.append((char) character));
        byte[] rawData = Base58.decodeBase58(remainingData.toString());
        byte version = rawData[0];
        byte[] data = new byte[rawData.length - 5];
        byte[] hash = new byte[4];
        System.arraycopy(rawData, 1, data, 0, rawData.length - 4 - 1);
        for (int i = rawData.length - 4; i < rawData.length; i++) {
            hash[i - (rawData.length - 4)] = rawData[i];
            rawData[i] = version;
        }
        byte[] newHash = Digest.SHA512.hash(Digest.SHA512.hash(new byte[]{version}, prefix.toString().getBytes(StandardCharsets.UTF_8), data).getHash()).getHash();
        for (int i = 0; i < 4; i++) {
            if (hash[i] != newHash[i]) {
                throw new IllegalArgumentException("Invalid checksum!");
            }
        }
        return new Decoded(data, prefix.toString(), version);
    }

    public static final class Decoded {
        public final byte[] content;
        public final String prefix;
        public final byte version;

        private Decoded(byte[] content, String prefix, byte version) {
            this.content = content;
            this.prefix = prefix;
            this.version = version;
        }
    }
}
