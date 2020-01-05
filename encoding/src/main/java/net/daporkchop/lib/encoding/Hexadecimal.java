/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.encoding;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.encoding.util.FastCharIntMap;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;
import java.util.Arrays;

/**
 * A highly optimized hexadecimal encoder.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class Hexadecimal {
    private final char[] ALPHABET = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    private final byte[] INDEX = new byte['f' + 1];

    static {
        Arrays.fill(INDEX, (byte) -1);
        for (byte i = 0; i < ALPHABET.length; i++) {
            INDEX[ALPHABET[i]] = i;
        }
    }

    public String encode(@NonNull byte[] data) {
        char[] newText = new char[data.length << 1];
        for (int i = 0; i < data.length; i++) {
            byte b = data[i];
            int a = i << 1;
            newText[a + 1] = ALPHABET[b & 0xF];
            newText[a] = ALPHABET[(b >> 4) & 0xF];
        }
        return PorkUtil.wrap(newText);
    }

    public String encode(@NonNull byte[] data, int from, int length) throws IndexOutOfBoundsException {
        PorkUtil.assertInRangeLen(data.length, from, length);
        char[] newText = new char[length << 1];
        for (int i = 0; i < length; i++) {
            byte b = data[i + from];
            int a = i << 1;
            newText[a] = ALPHABET[(b >>> 4) & 0xF];
            newText[a + 1] = ALPHABET[b & 0xF];
        }
        return PorkUtil.wrap(newText);
    }

    public void encode(@NonNull StringBuilder to, @NonNull byte[] data) {
        encode(to, data, 0, data.length);
    }

    public void encode(@NonNull StringBuilder to, @NonNull byte[] data, int from, int length) throws IndexOutOfBoundsException {
        PorkUtil.assertInRangeLen(data.length, from, length);
        to.ensureCapacity(length << 1);
        for (int i = 0; i < length; i++)    {
            byte b = data[i + from];
            to.append(ALPHABET[(b >>> 4) & 0xF]).append(ALPHABET[b & 0xF]);
        }
    }

    public void encode(@NonNull StringBuilder to, byte b)   {
        to.append(ALPHABET[(b >>> 4) & 0xF]).append(ALPHABET[b & 0xF]);
    }

    public void encode(@NonNull Appendable dst, @NonNull byte[] data) throws IOException {
        encode(dst, data, 0, data.length);
    }

    public void encode(@NonNull Appendable dst, @NonNull byte[] data, int from, int length) throws IOException, IndexOutOfBoundsException {
        PorkUtil.assertInRangeLen(data.length, from, length);
        for (int i = 0; i < length; i++)    {
            byte b = data[i + from];
            dst.append(ALPHABET[(b >>> 4) & 0xF]).append(ALPHABET[b & 0xF]);
        }
    }

    public void encode(@NonNull Appendable dst, byte b) throws IOException  {
        dst.append(ALPHABET[(b >>> 4) & 0xF]).append(ALPHABET[b & 0xF]);
    }

    public byte[] decode(@NonNull String input) {
        final char[] chars = PorkUtil.unwrap(input);
        final int length = chars.length;
        if ((length & 1) != 0)    {
            throw new IllegalArgumentException(String.format("Length not a multiple of 2: %d", length));
        }

        byte[] data = new byte[length >>> 1];
        for (int i = 0; i < length;) {
            byte b = INDEX[chars[i++]], a = INDEX[chars[i++]];
            if (b < 0 || a < 0) {
                throw new IllegalArgumentException("Illegal input text!");
            }
            data[i >> 1] = (byte) (a | (b << 4));
        }
        return data;
    }

    public byte decode(char c1, char c2)  {
        byte a = INDEX[c2];
        byte b = INDEX[c1];
        if (b < 0 || a < 0) {
            throw new IllegalArgumentException("Illegal input text!");
        }
        return (byte) (a | (b << 4));
    }

    public int decodeUnsigned(char c1, char c2)  {
        byte a = INDEX[c2];
        byte b = INDEX[c1];
        if (a >= 0 && b >= 0) {
            return (a | (b << 4));
        } else {
            return -1;
        }
    }
}
