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

package net.daporkchop.lib.network.packet.handler.codec;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.binary.NettyByteBufUtil;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;

/**
 * A {@link Codec} that uses a {@link DataIn} and {@link DataOut} instead of {@link ByteBuf} to read and write data
 *
 * @author DaPorkchop_
 */
public interface DataCodec<V> extends Codec<V> {
    @Override
    default void encode(@NonNull V value, @NonNull ByteBuf buf) throws Exception {
        try (DataOut out = NettyByteBufUtil.wrapOut(buf)) {
            this.encode(value, out);
        }
    }

    @Override
    default V decode(@NonNull ByteBuf buf) throws Exception {
        try (DataIn in = NettyByteBufUtil.wrapIn(buf)) {
            return this.decode(in);
        }
    }

    /**
     * Encodes a value
     *
     * @param value the value to encode
     * @param out   the stream to write data to
     * @throws Exception if an exception occurs
     */
    void encode(@NonNull V value, @NonNull DataOut out) throws Exception;

    /**
     * Decodes a value
     *
     * @param in the stream to read data from
     * @return the decoded value
     * @throws Exception if an exception occurs
     */
    V decode(@NonNull DataIn in) throws Exception;
}
