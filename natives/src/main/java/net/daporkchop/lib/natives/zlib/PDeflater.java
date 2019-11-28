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

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.unsafe.capability.Releasable;

/**
 * Used for creating a Zlib-compressed data stream.
 *
 * @author DaPorkchop_
 */
public interface PDeflater extends Releasable {
    /**
     * Does the entire compression process in one go.
     * <p>
     * This method ignores (and overwrites) any values set by {@link #input(long, long)} or {@link #output(long, long)}.
     * <p>
     * Calling this method will update the values of {@link #readBytes()} and {@link #writtenBytes()}, however the values will (probably) be garbage.
     * <p>
     * Attempting to use this method in combination with any of the following methods between resets may result in undefined behavior:
     * - {@link #deflate(boolean)}
     * - {@link #finished()}
     *
     * @param input  a {@link ByteBuf} containing the input data to be compressed
     * @param output a {@link ByteBuf} that the output data will be written to
     */
    default void deflate(@NonNull ByteBuf input, @NonNull ByteBuf output) {
        input.memoryAddress();
        output.memoryAddress();

        do {
            //System.out.printf("readable: %d, writable: %d\n", input.readableBytes(), output.writableBytes());

            this.input(input.memoryAddress() + input.readerIndex(), input.readableBytes());
            this.output(output.memoryAddress() + output.writerIndex(), output.writableBytes());

            this.deflate(true);

            input.readerIndex(input.readerIndex() + (int) this.readBytes());
            output.writerIndex(output.writerIndex() + (int) this.writtenBytes());
        } while (!this.finished() && output.ensureWritable(8192).isWritable());

        //System.out.printf("Done! readable: %d, writable: %d\n", input.readableBytes(), output.writableBytes());
    }

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
     * Does the actual data compression, blocking until either the input buffer is empty or the output buffer is full.
     * <p>
     * This method requires {@link #input(long, long)} and {@link #output(long, long)} to be set.
     * <p>
     * Calling this method will update the values of {@link #readBytes()} and {@link #writtenBytes()}.
     * <p>
     * Attempting to use this method in combination with {@link #deflate(ByteBuf, ByteBuf)} between resets may result in undefined behavior.
     *
     * @param finish if {@code true}, this will attempt to read all bytes from the input buffer and then finish the compression process. Even if this
     *               parameter is set, {@link #finished()} will not be set to {@code true} if the output buffer fills up.
     */
    void deflate(boolean finish);

    /**
     * @return whether or not the current deflation process is complete
     */
    boolean finished();

    /**
     * @return the number of bytes read from the input buffer during the last invocation of {@link #deflate(boolean)} or {@link #deflate(ByteBuf, ByteBuf)}
     */
    long readBytes();

    /**
     * @return the number of bytes written to the output buffer during the last invocation of {@link #deflate(boolean)} or {@link #deflate(ByteBuf, ByteBuf)}
     */
    long writtenBytes();

    /**
     * Resets this {@link PDeflater} instance.
     * <p>
     * This must be called after the compression process is completed if this instance should be re-used (but doesn't have to be, it can also be immediately
     * released or left to be garbage collected).
     */
    void reset();
}
