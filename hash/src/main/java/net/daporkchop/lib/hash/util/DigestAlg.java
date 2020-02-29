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

package net.daporkchop.lib.hash.util;

import lombok.NonNull;

/**
 * An actual implementation of a hash algorithm.
 * <p>
 * Not to be confused with {@link Digest}, which is a wrapper around this providing better usability
 *
 * @author DaPorkchop_
 */
public interface DigestAlg extends BaseDigest {
    /**
     * Add a byte
     *
     * @param in the byte to add
     */
    void update(byte in);

    /**
     * Add a lot of bytes
     *
     * @param in an array of bytes to add
     * @see #update(byte[], int, int)
     */
    default void update(@NonNull byte[] in) {
        this.update(in, 0, in.length);
    }

    /**
     * Add a lot of bytes
     *
     * @param in    an array of bytes
     * @param inOff the offset (starting position) in the array to start copying bytes from (inclusive)
     * @param len   the number of bytes to copy
     */
    void update(@NonNull byte[] in, int inOff, int len);

    /**
     * Complete calculation of the hash.
     * <p>
     * This method implicitly calls {@link #reset()}
     *
     * @param out    the byte array to write the hash to
     * @param outOff the offset in the array to write the hash to
     * @return the number of bytes copied into the array
     */
    int doFinal(byte[] out, int outOff);

    /**
     * Reset this digest to its default state
     */
    void reset();
}
