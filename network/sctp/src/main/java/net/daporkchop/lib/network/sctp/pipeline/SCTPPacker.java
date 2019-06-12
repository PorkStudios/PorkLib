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

package net.daporkchop.lib.network.sctp.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.sctp.SctpMessage;
import lombok.NonNull;
import net.daporkchop.lib.network.pipeline.event.SendingListener;
import net.daporkchop.lib.network.pipeline.util.EventContext;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.session.Reliability;

/**
 * Packs {@link ByteBuf}s into {@link SctpMessage}s.
 *
 * @author DaPorkchop_
 */
public class SCTPPacker<S extends AbstractUserSession<S>> implements SendingListener<S, ByteBuf> {
    @Override
    public void sending(@NonNull EventContext<S> context, @NonNull S session, @NonNull ByteBuf msg, Reliability reliability, int channel) {
        boolean unordered = reliability == null || reliability != Reliability.RELIABLE;
        context.sending(
                session,
                new SctpMessage(0, channel, unordered, msg),
                unordered ? Reliability.RELIABLE : Reliability.RELIABLE_ORDERED,
                channel
        );
    }
}