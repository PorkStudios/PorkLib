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

package net.daporkchop.lib.db.util;

import lombok.NonNull;

/**
 * @author DaPorkchop_
 */
public class FastHex {
    private static final char[] letters = "0123456789abcdef".toCharArray();

    public static String toHex(byte b) {
        return new String(new char[]{
                letters[b & 0xF],
                letters[(b >> 4) & 0xF]
        });
    }

    public static void toHex(byte b, StringBuilder builder) {
        builder.append(letters[b & 0xF]);
        builder.append(letters[(b >> 4) & 0xF]);
    }

    public static byte fromHex(@NonNull String s) {
        if (s.length() != 2) {
            throw new IllegalArgumentException("Invalid string length: " + s.length());
        }

        char c = s.charAt(0);
        byte b = 0;
        A:
        {
            for (int i = 0; i < 16; i++) {
                if (letters[i] == c) {
                    b = (byte) i;
                    break A;
                }
            }
            throw new IllegalArgumentException("Invalid char: " + c);
        }
        c = s.charAt(1);
        B:
        {
            for (int i = 0; i < 16; i++) {
                if (letters[i] == c) {
                    b |= (byte) (i << 4);
                    break B;
                }
            }
            throw new IllegalArgumentException("Invalid char: " + c);
        }
        return b;
    }
}
