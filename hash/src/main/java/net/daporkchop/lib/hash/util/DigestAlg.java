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
