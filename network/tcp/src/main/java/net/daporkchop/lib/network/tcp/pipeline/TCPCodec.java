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

package net.daporkchop.lib.network.tcp.pipeline;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.network.pipeline.handler.Codec;
import net.daporkchop.lib.network.pipeline.util.EventContext;
import net.daporkchop.lib.network.session.AbstractUserSession;

/**
 * @author DaPorkchop_
 */
public class TCPCodec<S extends AbstractUserSession<S>> implements Codec<S, ByteBuf, Object> {
    @Override
    public void received(@NonNull EventContext<S> context, @NonNull S session, @NonNull ByteBuf msg, int channel) {
    }

    @Override
    public void sending(@NonNull EventContext<S> context, @NonNull S session, @NonNull Object msg, int channel) {
    }
}
