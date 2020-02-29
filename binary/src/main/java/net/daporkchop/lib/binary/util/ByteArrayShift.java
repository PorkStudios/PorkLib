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

package net.daporkchop.lib.binary.util;

import lombok.NonNull;

/**
 * @author DaPorkchop_
 */
public class ByteArrayShift {
    public static byte[] shiftLeft(@NonNull byte[] bytes, int bits) {
        bits %= 8 * bytes.length;
        int shiftMod = bits & 7;
        byte carryMask = (byte) ((1 << shiftMod) - 1);
        int offsetBytes = (bits >> 3);

        int sourceIndex;
        for (int i = 0; i < bytes.length; i++) {
            sourceIndex = i + offsetBytes;
            if (sourceIndex >= bytes.length) {
                bytes[i] = 0;
            } else {
                byte src = bytes[sourceIndex];
                byte dst = (byte) (src << shiftMod);
                if (sourceIndex + 1 < bytes.length) {
                    dst |= bytes[sourceIndex + 1] >>> (8 - shiftMod) & carryMask;
                }
                bytes[i] = dst;
            }
        }
        return bytes;
    }

    public static byte[] shiftRight(@NonNull byte[] bytes, int bits) {
        bits %= 8 * bytes.length;
        int shiftMod = bits & 7;
        byte carryMask = (byte) (0xFF << (8 - shiftMod));
        int offsetBytes = (bits >> 3);

        int sourceIndex;
        for (int i = bytes.length - 1; i >= 0; i--) {
            sourceIndex = i - offsetBytes;
            if (sourceIndex < 0) {
                bytes[i] = 0;
            } else {
                byte src = bytes[sourceIndex];
                byte dst = (byte) ((0xff & src) >>> shiftMod);
                if (sourceIndex - 1 >= 0) {
                    dst |= bytes[sourceIndex - 1] << (8 - shiftMod) & carryMask;
                }
                bytes[i] = dst;
            }
        }
        return bytes;
    }

    public static byte[] circularShiftRight(@NonNull byte[] bytes, int bits) {
        return circularShiftRight(bytes, new byte[bytes.length], bits);
    }

    public static byte[] circularShiftRight(@NonNull byte[] bytes, @NonNull byte[] rightBuffer, int bits) {
        if (bytes.length != rightBuffer.length) {
            throw new IllegalArgumentException("bytes and rightBuffer must be the same length!");
        } else if (bytes.length == 0) {
            return bytes;
        }
        bits %= bytes.length << 3;
        System.arraycopy(bytes, 0, rightBuffer, 0, bytes.length);
        shiftRight(rightBuffer, bits);
        shiftLeft(bytes, (bytes.length << 3) - bits);
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] |= rightBuffer[i];
        }
        return bytes;
    }

    public static byte[] circularShiftLeft(@NonNull byte[] bytes, int bits) {
        return circularShiftRight(bytes, new byte[bytes.length], bits);
    }

    public static byte[] circularShiftLeft(@NonNull byte[] bytes, @NonNull byte[] rightBuffer, int bits) {
        if (bytes.length != rightBuffer.length) {
            throw new IllegalArgumentException("bytes and rightBuffer must be the same length!");
        } else if (bytes.length == 0) {
            return bytes;
        }
        bits %= bytes.length << 3;
        System.arraycopy(bytes, 0, rightBuffer, 0, bytes.length);
        shiftLeft(rightBuffer, bits);
        shiftRight(bytes, (bytes.length << 3) - bits);
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] |= rightBuffer[i];
        }
        return bytes;
    }
}
