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

package net.daporkchop.lib.encoding;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.misc.string.PUnsafeStrings;
import net.daporkchop.lib.common.util.PorkUtil;

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
    private final byte[] INDEX = new byte[128];

    static {
        Arrays.fill(INDEX, (byte) -1);
        for (byte i = 0; i < ALPHABET.length; i++) {
            INDEX[Character.toLowerCase(ALPHABET[i])] = i;
            INDEX[Character.toUpperCase(ALPHABET[i])] = i;
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
        return PUnsafeStrings.wrap(newText);
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
        return PUnsafeStrings.wrap(newText);
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
        final char[] chars = PUnsafeStrings.unwrap(input);
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
