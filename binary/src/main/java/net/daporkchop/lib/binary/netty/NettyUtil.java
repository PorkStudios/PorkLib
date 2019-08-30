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
import net.daporkchop.lib.binary.io.OldDataIn;
import net.daporkchop.lib.binary.io.OldDataOut;
import net.daporkchop.lib.common.util.PorkUtil;

/**
 * Some methods for dealing with Netty's {@link ByteBuf} class.
 *
 * @author DaPorkchop_
 */
//TODO: get rid of this
public interface NettyUtil {
    boolean NETTY_PRESENT = PorkUtil.classExistsWithName("io.netty.buffer.ByteBuf");

    /**
     * Ensures that Netty (or specifically netty-buffer) is present in the classpath by throwing an exception if it isn't.
     */
    static void ensureNettyPresent() {
        if (!NETTY_PRESENT) {
            throw new IllegalStateException("Netty not found in classpath!");
        }
    }

    /**
     * Wraps a {@link ByteBuf} into a {@link OldDataIn} for reading.
     * <p>
     * When the {@link OldDataIn} is closed (using {@link OldDataIn#close()}), the {@link ByteBuf} will not be released.
     *
     * @param buf the {@link ByteBuf} to read from
     * @return a {@link OldDataIn} that can read data from the {@link ByteBuf}
     */
    static OldDataIn wrapIn(@NonNull ByteBuf buf) {
        return wrapIn(buf, false);
    }

    /**
     * Wraps a {@link ByteBuf} into a {@link OldDataIn} for reading.
     * <p>
     * When the {@link OldDataIn} is closed (using {@link OldDataIn#close()}), the {@link ByteBuf} may or may not be released, depending on the value of the
     * {@code release} parameter.
     *
     * @param buf     the {@link ByteBuf} to read from
     * @param release whether or not to release the buffer when the {@link OldDataIn} is closed
     * @return a {@link OldDataIn} that can read data from the {@link ByteBuf}
     */
    static OldDataIn wrapIn(@NonNull ByteBuf buf, boolean release) {
        ensureNettyPresent();
        return release ? new NettyByteBufIn.Releasing(buf) : new NettyByteBufIn(buf);
    }

    /**
     * Wraps a {@link ByteBuf} into a {@link OldDataOut} for writing.
     * <p>
     * When the {@link OldDataOut} is closed (using {@link OldDataOut#close()}), the {@link ByteBuf} will not be released.
     *
     * @param buf the {@link ByteBuf} to write to
     * @return a {@link OldDataOut} that can write data to the {@link ByteBuf}
     */
    static OldDataOut wrapOut(@NonNull ByteBuf buf) {
        ensureNettyPresent();
        return new NettyByteBufOut.Default(buf);
    }
}
