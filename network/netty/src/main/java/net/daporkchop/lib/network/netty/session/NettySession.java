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

package net.daporkchop.lib.network.netty.session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelOutboundInvoker;
import lombok.NonNull;
import net.daporkchop.lib.network.netty.util.future.NettyChannelPromise;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.transport.NetSession;
import net.daporkchop.lib.network.util.Priority;
import net.daporkchop.lib.network.util.reliability.Reliability;

/**
 * A {@link NetSession} used by all Netty-based transports.
 *
 * @author DaPorkchop_
 */
public interface NettySession<S extends AbstractUserSession<S>> extends NetSession<S> {
    /**
     * @see Channel#id()
     */
    ChannelId id();

    /**
     * @see Channel#closeFuture()
     */
    ChannelFuture closeFuture();

    /**
     * @see Channel#flush()
     */
    ChannelOutboundInvoker flush();

    /**
     * @see Channel#close()
     */
    ChannelFuture close();

    @Override
    NettyChannelPromise send(@NonNull Object message, int channel, Reliability reliability, Priority priority, int flags);
}
