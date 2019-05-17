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

package net.daporkchop.lib.network.pipeline;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.network.pipeline.event.SendingListener;
import net.daporkchop.lib.network.pipeline.util.EventContext;
import net.daporkchop.lib.network.pipeline.util.FireEvents;
import net.daporkchop.lib.network.pipeline.util.PipelineListener;
import net.daporkchop.lib.network.session.AbstractUserSession;

import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A simplified and optimized version of Netty's ChannelPipeline.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public class Pipeline<S extends AbstractUserSession<S>> {
    @Getter
    protected final S session;

    protected final Object mutex = new Object[0];

    protected final List<Node<S>> nodes = new ArrayList<>();
    protected Node<S> head;
    protected Node<S> tail;

    protected final PipelineEdgeListener<S> listener;

    protected List<Object> sendQueue;
    protected final SendingListener.Fire<S> actualSender = (s, msg, channel) -> Pipeline.this.sendQueue.add(msg);

    protected int fallbackIdCounter = 0;

    public Pipeline(@NonNull S session, @NonNull PipelineEdgeListener<S> listener) {
        this.session = session;
        this.listener = listener;
    }

    public void fireOpened() {
        synchronized (this.mutex)   {
            this.head.fireOpened(this.session);
        }
    }

    public void fireClosed() {
        synchronized (this.mutex)   {
            this.head.fireClosed(this.session);

            this.nodes.forEach(n -> n.listener.removed(this, this.session));
            this.nodes.clear();
            this.rebuild();
        }
    }

    public void fireReceived(@NonNull Object msg, int channel) {
        synchronized (this.mutex)   {
            this.head.fireReceived(this.session, msg, channel);
        }
    }

    public void fireSending(@NonNull Object msg, int channel, @NonNull List<Object> sendQueue) {
        synchronized (this.mutex)   {
            this.sendQueue = sendQueue;
            this.tail.fireSending(this.session, msg, channel);
            this.sendQueue = null;
        }
    }

    public void fireExceptionCaught(@NonNull Throwable t) {
        synchronized (this.mutex)   {
            this.head.fireExceptionCaught(this.session, t);
        }
    }

    public Pipeline<S> addFirst(@NonNull PipelineListener<S> listener) {
        return this.addFirst(Integer.toHexString(this.fallbackIdCounter++), listener);
    }

    public Pipeline<S> addFirst(@NonNull String name, @NonNull PipelineListener<S> listener) {
        synchronized (this.mutex) {
            this.assertNotContains(name);
            this.nodes.add(0, new Node<>(this, name, listener));
            this.rebuild();
            listener.added(this, this.session);
            return this;
        }
    }

    public Pipeline<S> addLast(@NonNull PipelineListener<S> listener) {
        return this.addLast(Integer.toHexString(this.fallbackIdCounter++), listener);
    }

    public Pipeline<S> addLast(@NonNull String name, @NonNull PipelineListener<S> listener) {
        synchronized (this.mutex) {
            this.assertNotContains(name);
            this.nodes.add(new Node<>(this, name, listener));
            this.rebuild();
            listener.added(this, this.session);
            return this;
        }
    }

    public Pipeline<S> remove(@NonNull String name) {
        synchronized (this.mutex) {
            for (Iterator<Node<S>> itr = this.nodes.iterator(); itr.hasNext();) {
                Node<S> node = itr.next();
                if (name.equals(node.name))   {
                    itr.remove();
                    this.rebuild();
                    node.listener.removed(this, this.session);
                    return this;
                }
            }
            throw new IllegalStateException(String.format("No listener with name \"%s\"!", name));
        }
    }

    public Pipeline<S> replace(@NonNull String name, @NonNull PipelineListener<S> listener) {
        synchronized (this.mutex) {
            for (ListIterator<Node<S>> itr = this.nodes.listIterator(); itr.hasNext();) {
                Node<S> node = itr.next();
                if (name.equals(node.name))   {
                    itr.set(new Node<>(this, name, listener));
                    this.rebuild();
                    node.listener.removed(this, this.session);
                    listener.added(this, this.session);
                    return this;
                }
            }
            throw new IllegalStateException(String.format("No listener with name \"%s\"!", name));
        }
    }

    protected void assertNotContains(@NonNull String name) {
        synchronized (this.mutex) {
            this.nodes.forEach(node -> {
                if (name.equals(node.name)) {
                    throw new IllegalStateException(String.format("Listener with name \"%s\" already present!", name));
                }
            });
        }
    }

    protected void rebuild() {
        synchronized (this.mutex) {
            this.nodes.forEach(node -> {
                node.next = null;
                node.prev = null;
            });
            int i = this.nodes.size();
            for (int j = i - 2; j > 0; j--) {
                Node<S> node = this.nodes.get(j);
                node.next = this.nodes.get(j + 1);
                node.prev = this.nodes.get(j - 1);
            }
            this.nodes.get(0).prev = null;
            this.nodes.get(0).next = null;
            this.nodes.get(i - 1).prev = null;
            this.nodes.get(i - 1).next = null;
            if (i >= 2) {
                this.nodes.get(0).next = this.nodes.get(1);
                this.nodes.get(i - 1).prev = this.nodes.get(i - 2);
            }
            this.nodes.forEach(Node::rebuild);
        }
    }
}
