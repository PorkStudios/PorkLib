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

package net.daporkchop.lib.network.tcp.frame;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.util.PacketMetadata;

import java.util.List;

/**
 * A {@link Framer} that doesn't use any sort of length prefixing or encode any metadata, and simply sends binary messages as they are.
 *
 * @author DaPorkchop_
 */
public class NoopFramer<S extends AbstractUserSession<S>> implements Framer<S> {
    @Override
    public void received(@NonNull S session, @NonNull ByteBuf msg, @NonNull UnpackCallback callback) {
        callback.add(msg);
    }

    @Override
    public void sending(@NonNull S session, @NonNull ByteBuf msg, @NonNull PacketMetadata metadata, @NonNull List<ByteBuf> frames) {
        if (metadata.checkAnySet(~PacketMetadata.ORIGINAL_MASK))    {
            throw new IllegalStateException();
        } else {
            frames.add(msg);
        }
    }

    @Override
    public void init(@NonNull S session) {
    }

    @Override
    public void release(@NonNull S session) {
    }
}
