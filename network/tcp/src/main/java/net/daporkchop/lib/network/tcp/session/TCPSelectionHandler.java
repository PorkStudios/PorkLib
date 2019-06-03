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

package net.daporkchop.lib.network.tcp.session;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.network.pork.SelectionHandler;
import net.daporkchop.lib.network.pork.pool.SelectionPool;
import net.daporkchop.lib.network.session.AbstractUserSession;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class TCPSelectionHandler<S extends AbstractUserSession<S>> implements SelectionHandler {
    protected final TCPNetSession<S> session;
    protected final ByteBufAllocator alloc;
    protected final SocketChannel channel;
    protected final SelectionKey key;

    public TCPSelectionHandler(@NonNull TCPNetSession<S> session) {
        this.session = session;
        this.alloc = session.alloc;
        this.channel = session.channel;
        this.key = session.endpoint.transportEngine().pool().register(session.channel, SelectionKey.OP_READ | SelectionKey.OP_WRITE, this)
    }

    @Override
    public void handle(int flags) throws Exception {
        if (!this.key.isValid()) {
            this.session.closePromise.tryCompleteSuccessfully();
            return;
        }
        if ((flags & SelectionKey.OP_READ) != 0) {
            ByteBuf buf = this.alloc.ioBuffer();
            try {
                if (buf.nioBufferCount() != 1) {
                    throw new IllegalStateException(String.format("Illegal number of NIO buffers: %d", buf.nioBufferCount()));
                }
                this.channel.read(buf.nioBuffer());
                //TODO: handle
            } finally {
                buf.release();
            }
        }
        if ((flags & SelectionKey.OP_WRITE) != 0)   {
            try {
                this.channel.write(this.session.sendBuffer.nioBuffer());
            } finally {
                this.session.sendBuffer.discardSomeReadBytes();
            }
            this.key.interestOps(this.key.interestOps() & ~SelectionKey.OP_WRITE); //TODO: this is worse than it was
        }
    }
}
