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

package net.daporkchop.lib.binary.netty;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.util.PorkUtil;

/**
 * Some methods for dealing with Netty's {@link ByteBuf} class.
 *
 * @author DaPorkchop_
 */
public interface NettyUtil {
    /**
     * Wraps a {@link ByteBuf} into a {@link DataIn} for reading.
     * <p>
     * When the {@link DataIn} is closed (using {@link DataIn#close()}), the {@link ByteBuf} will not be released.
     *
     * @param buf the {@link ByteBuf} to read from
     * @return a {@link DataIn} that can read data from the {@link ByteBuf}
     */
    static DataIn wrapIn(@NonNull ByteBuf buf) {
        return wrapIn(buf, false);
    }

    /**
     * Wraps a {@link ByteBuf} into a {@link DataIn} for reading.
     * <p>
     * When the {@link DataIn} is closed (using {@link DataIn#close()}), the {@link ByteBuf} may or may not be released, depending on the value of the
     * {@code release} parameter.
     *
     * @param buf     the {@link ByteBuf} to read from
     * @param release whether or not to release the buffer when the {@link DataIn} is closed
     * @return a {@link DataIn} that can read data from the {@link ByteBuf}
     */
    static DataIn wrapIn(@NonNull ByteBuf buf, boolean release) {
        return release ? new NettyByteBufIn.Releasing(buf) : new NettyByteBufIn(buf);
    }

    /**
     * Wraps a {@link ByteBuf} into a {@link DataOut} for writing.
     * <p>
     * When the {@link DataOut} is closed (using {@link DataOut#close()}), the {@link ByteBuf} will not be released.
     *
     * @param buf the {@link ByteBuf} to write to
     * @return a {@link DataOut} that can write data to the {@link ByteBuf}
     */
    static DataOut wrapOut(@NonNull ByteBuf buf) {
        return new NettyByteBufOut.Default(buf);
    }
}
