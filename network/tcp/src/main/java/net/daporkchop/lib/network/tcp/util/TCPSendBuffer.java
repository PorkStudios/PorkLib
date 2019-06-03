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

package net.daporkchop.lib.network.tcp.util;

import io.netty.util.Recycler;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.concurrent.future.Promise;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Buffers messages that need to be sent on a TCP channel.
 *
 * @author DaPorkchop_
 */
public class TCPSendBuffer {
    protected boolean open = true;
    protected Node next;
    protected Node last;

    public synchronized void add(@NonNull ByteBuffer buffer, Promise promise)   {
        if (!this.open) {
            throw new IllegalStateException("closed");
        }
        Node node = Node.RECYCLER.get();
        node.buffer(buffer).promise(promise);
        if (this.last == null)  {
            this.next = this.last = node;
        } else {
            this.last.next(node);
            this.last = node;
        }
    }

    public synchronized void write(@NonNull SocketChannel channel) throws IOException {
        int written;
        do {
            Node next = this.next;
            if (next == null) {
                return;
            } else if (!next.buffer.hasRemaining()) {
                if ((this.next = next.next) == null)    {
                    this.last = null;
                }
                next.release(false);
                written = 1;
            } else {
                written = channel.write(next.buffer);
            }
        } while (written > 0);
    }

    /**
     * Releases this buffer and all messages in preparation for the channel to be closed.
     */
    public synchronized void release() {
        if (!this.open) {
            throw new IllegalStateException("closed");
        }
        this.open = false;
        Node node = this.next;
        while (node != null)    {
            Node next = node.next;
            node.release(true);
            node = next;
        }
    }

    @RequiredArgsConstructor
    @Setter
    @Accessors(fluent = true, chain = true)
    protected static class Node {
        protected static final Recycler<Node> RECYCLER = new Recycler<Node>() {
            @Override
            protected Node newObject(Handle<Node> handle) {
                return new Node(handle);
            }
        };

        @NonNull
        protected final Recycler.Handle<Node> handle;
        protected Node next;
        protected ByteBuffer buffer;
        protected Promise promise;

        public void release(boolean cancel)   {
            if (this.promise != null)   {
                if (cancel) {
                    this.promise.tryCancel();
                } else {
                    this.promise.tryCompleteSuccessfully();
                }
            }
            this.next(null).buffer(null).promise(null).handle.recycle(this);
        }
    }
}
