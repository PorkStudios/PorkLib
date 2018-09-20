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

package net.daporkchop.lib.binary.util;

import lombok.NonNull;

/**
 * @author DaPorkchop_
 */
public class ByteArrayShift {
    public static byte[] shiftLeft(byte[] bytes, int bits) {
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

    public static byte[] shiftRight(byte[] bytes, int bits) {
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
