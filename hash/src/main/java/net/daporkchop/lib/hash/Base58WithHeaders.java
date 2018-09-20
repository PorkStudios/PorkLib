/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.hash;

import net.daporkchop.lib.binary.UTF8;
import net.daporkchop.lib.encoding.basen.Base58;
import net.daporkchop.lib.hash.helper.sha.Sha512Helper;

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
    public static String encode(byte version, String prefix, byte[] content) {
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
        for (int i = 0; i < content.length; i++) {
            newData[i + 1] = content[i];
        }
        for (int i = newData.length - 4; i < newData.length; i++) {
            newData[i] = version; //fill with random data for hash
        }

        byte[] hash = Sha512Helper.sha512(Sha512Helper.sha512(new byte[]{version}, prefix.getBytes(UTF8.utf8), content));
        System.arraycopy(hash, 0, newData, (newData.length - 4), 4);
        return Base58.INSTANCE.alphabet[prefix.length()] + prefix + Base58.encodeBase58(newData);
    }

    /**
     * Decodes a Base58 string and validates its checksum
     *
     * @param pork58 Input string, Pork58-encoded
     * @return Decoded data!
     */
    public static Decoded decode(String pork58) {
        List<Character> chars = pork58.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
        char prefixLengthChar = chars.remove(0);
        int prefixLength = -1;
        for (int i = 0; i < Base58.INSTANCE.length; i++) {
            if (Base58.INSTANCE.alphabet[i] == prefixLengthChar) {
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
        byte[] data = new byte[rawData.length - 5],
                hash = new byte[4];
        for (int i = 1; i < rawData.length - 4; i++) {
            data[i - 1] = rawData[i];
        }
        for (int i = rawData.length - 4; i < rawData.length; i++) {
            hash[i - (rawData.length - 4)] = rawData[i];
            rawData[i] = version;
        }
        byte[] newHash = Sha512Helper.sha512(Sha512Helper.sha512(new byte[]{version}, prefix.toString().getBytes(UTF8.utf8), data));
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
