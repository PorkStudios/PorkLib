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

package net.daporkchop.lib.binary.stream;

import lombok.NonNull;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * @author DaPorkchop_
 */
public class StreamUtil {
    public static int read(@NonNull InputStream input, @NonNull byte[] buffer, int offset, int length) throws IOException {
        if (length < 0) {
            throw new IllegalArgumentException("Length must not be negative: " + length);
        }
        int remaining = length;
        while (remaining > 0) {
            int location = length - remaining;
            int count = input.read(buffer, offset + location, remaining);
            if (count == -1) { // EOF
                break;
            }
            remaining -= count;
        }
        return length - remaining;
    }

    /**
     * Reads the total content of an {@link InputStream} into a byte array
     *
     * @param in          the {@link InputStream} to read from
     * @param maxSize     the maximum number of bytes to read. set to -1 to remove limt
     * @param requireSize whether or not the entire maxSize must be filled
     *
     * @return the complete content of an {@link InputStream}
     * @throws IOException if an IO exception occurs you dummy
     */
    public static byte[] readFully(@NonNull InputStream in, int maxSize, boolean requireSize) throws IOException {
        byte[] var3 = new byte[0];
        if (maxSize == -1) {
            maxSize = 2147483647;
        }

        int var6;
        for (int var4 = 0; var4 < maxSize; var4 += var6) {
            int var5;
            if (var4 >= var3.length) {
                var5 = Math.min(maxSize - var4, var3.length + 1024);
                if (var3.length < var4 + var5) {
                    var3 = Arrays.copyOf(var3, var4 + var5);
                }
            } else {
                var5 = var3.length - var4;
            }

            var6 = in.read(var3, var4, var5);
            if (var6 < 0) {
                if (requireSize && maxSize != 2147483647) {
                    throw new EOFException("Detect premature EOF");
                }

                if (var3.length != var4) {
                    var3 = Arrays.copyOf(var3, var4);
                }
                break;
            }
        }

        return var3;
    }
}
