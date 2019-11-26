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

package net.daporkchop.lib.natives.zlib;

import net.daporkchop.lib.unsafe.capability.Releasable;

/**
 * Used for creating a Zlib-compressed data stream.
 *
 * @author DaPorkchop_
 */
public interface PDeflater extends Releasable {
    /**
     * Sets the input (source) data to be compressed.
     *
     * @param addr the base address of the data to be compressed
     * @param size the size of the data to be compressed
     */
    void input(long addr, long size);

    /**
     * Sets the output (destination) where compressed data will be written to.
     *
     * @param addr the base address of the destination
     * @param size the maximum size of the output data
     */
    void output(long addr, long size);

    /**
     * Does the entire compression process in one go.
     * <p>
     * Will fail if the output buffer is not large enough to fit the compressed data.
     * <p>
     * Calling this method will update the values of {@link #readBytes()} and {@link #writtenBytes()}.
     * <p>
     * May not be used in combination with {@link #deflate()}!
     */
    void deflateFinish();

    /**
     * Does the actual data compression, blocking until either the input or output buffers are full.
     * <p>
     * Calling this method will update the values of {@link #readBytes()} and {@link #writtenBytes()}.
     * <p>
     * May not be used in combination with {@link #deflateFinish()}!
     */
    void deflate();

    /**
     * @return the number of bytes read from the input buffer during the last invocation of {@link #deflate()} or {@link #deflateFinish()}
     */
    long readBytes();

    /**
     * @return the number of bytes written to the output buffer during the last invocation of {@link #deflate()} or {@link #deflateFinish()}
     */
    long writtenBytes();

    /**
     * Informs this deflater that it should no longer read any data.
     * <p>
     * After this method has been called, {@link #deflate()} will no longer cause any data to be read from the input buffer. {@link #deflate()} should
     * be called repeatedly until {@link #finished()} returns {@code true} (the output buffer should be expanded as needed).
     */
    void finish();

    /**
     * @return whether or not the current deflation process is complete
     */
    boolean finished();

    /**
     * Resets this {@link PDeflater} instance.
     * <p>
     * This must be called after the compression process is completed if this instance should be re-used (but doesn't have to be, it can also be immediately
     * released or left to be garbage collected).
     */
    void reset();
}
