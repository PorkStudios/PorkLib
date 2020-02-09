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

package net.daporkchop.lib.binary.oio;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.common.util.PorkUtil;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Helper methods for dealing with OIO {@link InputStream}s and {@link OutputStream}s.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class StreamUtil {
    /**
     * Reads the entire contents of the given {@link InputStream} into a {@code byte[]}.
     *
     * @param in the {@link InputStream} to read
     * @return the contents of the given {@link InputStream} as a {@code byte[]}
     * @throws IOException if an IO exception occurs you dummy
     */
    public byte[] toByteArray(@NonNull InputStream in) throws IOException {
        if (in instanceof DataIn) {
            //DataIn implementations might apply their own optimizations here
            return ((DataIn) in).toByteArray();
        } else {
            byte[] arr = new byte[4096];
            int pos = 0;
            for (int i; (i = in.read(arr, pos, arr.length - pos)) != -1; pos += i) {
                if (pos + i == arr.length) {
                    //grow array
                    byte[] old = arr;
                    System.arraycopy(old, 0, arr = new byte[arr.length << 1], 0, old.length);
                }
            }
            return pos == arr.length ? arr : Arrays.copyOf(arr, pos); //don't copy if the size is exactly the size of the array already
        }
    }

    /**
     * Fills the given {@code byte[]} with data read from the given {@link InputStream}.
     *
     * @param in  the {@link InputStream} to read from
     * @param dst the {@code byte[]} to read to
     * @return the {@link byte[]}
     * @throws EOFException if the given {@link InputStream} reaches EOF before the given {@code byte[]} could be filled
     * @throws IOException  if an IO exception occurs you dummy
     */
    public byte[] readFully(@NonNull InputStream in, @NonNull byte[] dst) throws EOFException, IOException {
        return readFully(in, dst, 0, dst.length);
    }

    /**
     * Fills the given region of the given {@code byte[]} with data read from the given {@link InputStream}.
     *
     * @param in     the {@link InputStream} to read from
     * @param dst    the {@code byte[]} to read to
     * @param start  the first index (inclusive) in the {@code byte[]} to start writing to
     * @param length the number of bytes to read into the {@code byte[]}
     * @return the {@link byte[]}
     * @throws EOFException if the given {@link InputStream} reaches EOF before the given number of bytes could be read
     * @throws IOException  if an IO exception occurs you dummy
     */
    public byte[] readFully(@NonNull InputStream in, @NonNull byte[] dst, int start, int length) throws EOFException, IOException {
        if (in instanceof DataIn) {
            //DataIn implementations might apply their own optimizations here
            ((DataIn) in).readFully(dst, start, length);
        } else {
            PorkUtil.assertInRangeLen(dst.length, start, length);
            for (int i; length > 0 && (i = in.read(dst, start, length)) != -1; start += i, length -= i) ;
            if (length != 0) {
                throw new EOFException();
            }
        }
        return dst;
    }
}
