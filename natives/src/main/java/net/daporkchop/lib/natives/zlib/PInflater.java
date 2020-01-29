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
 * Used for inflating a Zlib-compressed data stream.
 *
 * @author DaPorkchop_
 */
public interface PInflater extends Releasable {
    /**
     * Does the entire decompression process in one go.
     * <p>
     * This method ignores (and overwrites) any values set by {@link #input(long, int)} or {@link #output(long, int)}.
     * <p>
     * Calling this method will update the values of {@link #readBytes()} and {@link #writtenBytes()}, however the values will (probably) be garbage.
     * <p>
     * Attempting to use this method in combination with any of the following methods between resets may result in undefined behavior:
     * - {@link #inflate()}
     * - {@link #finished()}
     *
     * @param input  a {@link ByteBuf} containing the input data to be decompressed
     * @param output a {@link ByteBuf} that the output data will be written to
     */
    default void inflate(@NonNull ByteBuf input, @NonNull ByteBuf output) {
        if (!input.isReadable())    {
            throw new IllegalStateException("Input not readable!");
        }
        this.input(input.memoryAddress() + input.readerIndex(), input.readableBytes()); //we don't need to set this in a loop

        do {
            this.output(output.memoryAddress() + output.writerIndex(), output.writableBytes());

            this.inflate();

            input.skipBytes(this.readBytes());
            output.writerIndex(output.writerIndex() + this.writtenBytes());
        } while (!this.finished() && output.ensureWritable(8192).isWritable());
    }

    /**
     * Sets the input (source) data to be decompressed.
     *
     * @param addr the base address of the data to be decompressed
     * @param size the size of the data to be decompressed
     */
    void input(long addr, int size);

    /**
     * Sets the output (destination) where decompressed data will be written to.
     *
     * @param addr the base address of the destination
     * @param size the maximum size of the output data
     */
    void output(long addr, int size);

    /**
     * Does the actual data decompression, blocking until either the input buffer is empty or the output buffer is full.
     * <p>
     * This method requires {@link #input(long, int)} and {@link #output(long, int)} to be set.
     * <p>
     * Calling this method will update the values of {@link #readBytes()} and {@link #writtenBytes()}.
     * <p>
     * Attempting to use this method in combination with {@link #inflate(ByteBuf, ByteBuf)} between resets may result in undefined behavior.
     */
    void inflate();

    /**
     * @return whether or not the current deflation process is complete
     */
    boolean finished();

    /**
     * @return the number of bytes read from the input buffer during the last invocation of {@link #inflate()}
     */
    int readBytes();

    /**
     * @return the number of bytes written to the output buffer during the last invocation of {@link #inflate()}
     */
    int writtenBytes();

    /**
     * Resets this {@link PInflater} instance.
     * <p>
     * This must be called after the decompression process is completed if this instance should be re-used (but doesn't have to be, it can also be immediately
     * released or left to be garbage collected).
     */
    void reset();
}
