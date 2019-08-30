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

package net.daporkchop.lib.binary.io.source;

import lombok.NonNull;
import net.daporkchop.lib.binary.util.exception.EndOfStreamException;
import net.daporkchop.lib.common.util.PorkUtil;

import java.io.IOException;

/**
 * A source from which to read an indefinitely long sequence of bytes.
 *
 * @author DaPorkchop_
 */
public interface ByteSource {
    /**
     * Gets the next byte from this source.
     * <p>
     * If no further bytes are available, this will throw an {@link EndOfStreamException}.
     *
     * @return the next byte (unsigned)
     * @throws EndOfStreamException if EOS is reached
     * @throws IOException          if any exception occurs while reading
     */
    int next() throws IOException;

    /**
     * @return the next byte (signed)
     * @see #next()
     */
    default byte nextByte() throws IOException {
        return (byte) this.next();
    }

    /**
     * @see #nextBytes(byte[], int, int)
     */
    default void nextBytes(@NonNull byte[] arr) throws IOException {
        this.nextBytes(arr, 0, arr.length);
    }

    /**
     * Fills the given byte array with data from this source.
     *
     * @param arr   the array to fill
     * @param start the index in the array to start filling at
     * @param count the number of bytes to read
     * @see #next()
     */
    default void nextBytes(@NonNull byte[] arr, int start, int count) throws IOException {
        PorkUtil.assertValidArrayIndex(arr.length, start, count);
        for (int i = start; i < start + count; i++) {
            arr[i] = this.nextByte();
        }
    }

    /**
     * Skips (discards) the given number of bytes.
     *
     * @param count the number of bytes to skip
     * @see #next()
     */
    default void skip(long count) throws IOException {
        while (count-- > 0) {
            this.next();
        }
    }
}
