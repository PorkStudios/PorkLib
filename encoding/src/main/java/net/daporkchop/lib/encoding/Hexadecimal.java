/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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

import net.daporkchop.lib.encoding.util.FastCharIntMap;

/**
 * A really fast hexadecimal encoder
 *
 * @author DaPorkchop_
 */
public class Hexadecimal {
    private static final char[] ALPHABET = "0123456789abcdef".toCharArray();
    private static final FastCharIntMap INDEX = new FastCharIntMap();

    static {
        for (byte i = 0; i < ALPHABET.length; i++) {
            INDEX.put(ALPHABET[i], i);
        }
    }

    public static String encode(byte[] data) {
        char[] newText = new char[data.length << 1];
        for (int i = 0; i < data.length; i++) {
            byte b = data[i];
            int a = i << 1;
            newText[a + 1] = ALPHABET[b & 0xF];
            newText[a] = ALPHABET[(b >> 4) & 0xF];
        }
        return new String(newText);
    }

    public static String encode(byte[] data, int from, int length) {
        char[] newText = new char[length << 1];
        for (int i = 0; i < length; i++) {
            byte b = data[i + from];
            int a = i << 1;
            newText[a + 1] = ALPHABET[b & 0xF];
            newText[a] = ALPHABET[(b >> 4) & 0xF];
        }
        return new String(newText);
    }

    public static byte[] decode(String input) {
        char[] chars = input.toCharArray();
        byte[] data = new byte[chars.length >> 1];
        for (int i = 0; i < chars.length; i += 2) {
            char a = chars[i + 1], b = chars[i];
            data[i >> 1] = (byte) ((INDEX.get(a) & 0xF) | (INDEX.get(b) << 4));
        }
        return data;
    }
}
