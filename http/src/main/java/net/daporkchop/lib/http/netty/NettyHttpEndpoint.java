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

package net.daporkchop.lib.http.netty;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.ChannelGroupFutureListener;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.Future;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.http.util.HttpEndpoint;
import net.daporkchop.lib.network.nettycommon.transport.Transport;
import net.daporkchop.lib.unsafe.PUnsafe;

/**
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
abstract class NettyHttpEndpoint<C extends Channel> implements HttpEndpoint {
    protected static final long CLOSEFUTURE_OFFSET = PUnsafe.pork_getOffset(NettyHttpEndpoint.class, "closeFuture");

    protected final Transport transport;
    protected final EventLoopGroup group;
    protected final ChannelGroup channels;
    private volatile ChannelGroupFuture closeFuture;

    public NettyHttpEndpoint(@NonNull Transport transport) {
        this.transport = transport;
        this.group = transport.eventLoopGroupPool().get();

        this.channels = new DefaultChannelGroup(this.group.next(), true);
    }

    @Override
    public Future<Void> close() {
        if (this.closeFuture != null) {
            return this.closeFuture;
        } else {
            if (PUnsafe.compareAndSwapObject(this, CLOSEFUTURE_OFFSET, null, this.channels.close())) {
                this.transport.eventLoopGroupPool().release(this.group);
            }
            return this.closeFuture;
        }
    }

    public void assertOpen() {
        if (this.closeFuture != null) throw new IllegalStateException("closed");
    }
}
