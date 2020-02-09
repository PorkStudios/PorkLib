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

package net.daporkchop.lib.compression.zstd;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.compression.util.BufferTyped;
import net.daporkchop.lib.compression.util.exception.InvalidBufferTypeException;
import net.daporkchop.lib.compression.zstd.util.exception.ContentSizeUnknownException;

/**
 * Representation of a Zstd implementation.
 *
 * @author DaPorkchop_
 */
public interface ZstdProvider extends BufferTyped {
    @Override
    boolean direct();

    /**
     * Compresses the given source data into a single Zstd frame into the given destination buffer.
     * <p>
     * If the destination buffer does not have enough space writable for the compressed data, the operation will fail and both buffer's indices will remain
     * unchanged, however the destination buffer's contents may be modified.
     *
     * @param src              the {@link ByteBuf} to read source data from
     * @param dst              the {@link ByteBuf} to write compressed data to
     * @param compressionLevel the Zstd level to compress at
     * @return whether or not compression was successful. If {@code false}, the destination buffer was too small for the compressed data
     */
    boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst, int compressionLevel) throws InvalidBufferTypeException;

    /**
     * Decompresses the given Zstd-compressed into the given destination buffer.
     * <p>
     * If the destination buffer does not have enough space writable for the decompressed data, the operation will fail and both buffer's indices will remain
     * unchanged, however the destination buffer's contents may be modified.
     *
     * @param src the {@link ByteBuf} to read compressed data from
     * @param dst the {@link ByteBuf} to write decompressed data to
     * @return whether or not decompression was successful. If {@code false}, the destination buffer was too small for the decompressed data
     */
    boolean decompress(@NonNull ByteBuf src, @NonNull ByteBuf dst) throws InvalidBufferTypeException;

    /**
     * Gets the decompressed size of the given Zstd-compressed data.
     *
     * @param src the {@link ByteBuf} containing the compressed data. This {@link ByteBuf}'s indices will not be modified by this method
     * @return the size (in bytes) of the decompressed data
     * @throws ContentSizeUnknownException if the decompressed size cannot be determined
     */
    long frameContentSize(@NonNull ByteBuf src) throws InvalidBufferTypeException, ContentSizeUnknownException;

    /**
     * Gets the maximum (worst-case) compressed size for input data of the given length.
     *
     * @param srcSize the size (in bytes) of the source data
     * @return the worst-case size of the compressed data
     */
    long compressBound(long srcSize);
}
