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
import net.daporkchop.lib.network.pipeline.util.PipelineListener;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.session.Reliability;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * A simplified and optimized version of Netty's ChannelPipeline.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public class Pipeline<S extends AbstractUserSession<S>> {
    protected static final PipelineListener NOOP_LISTENER = new PipelineListener() {
    };

    @SuppressWarnings("unchecked")
    protected static <S extends AbstractUserSession<S>> PipelineListener<S> noopListener() {
        return (PipelineListener<S>) NOOP_LISTENER;
    }

    @Getter
    protected final S session;

    protected final Object mutex = new Object[0];

    protected final List<Node<S>> nodes = new ArrayList<>();
    protected final Node<S> head;
    protected final Node<S> tail;

    protected final PipelineEdgeListener<S> listener;

    protected int fallbackIdCounter = 0;

    public Pipeline(@NonNull S session, @NonNull PipelineEdgeListener<S> listener) {
        this.session = session;
        this.listener = listener;

        this.head = new Node<>(this, "head", noopListener());
        this.tail = new Node<>(this, "tail", noopListener());

        this.rebuild();
    }

    public void fireOpened() {
        synchronized (this.mutex) {
            this.head.context.fireOpened(this.session);
        }
    }

    public void fireClosed() {
        synchronized (this.mutex) {
            this.head.context.fireClosed(this.session);

            this.nodes.forEach(n -> n.listener.removed(this, this.session));
            this.nodes.clear();
            this.rebuild();
        }
    }

    public void fireReceived(@NonNull Object msg, int channel) {
        synchronized (this.mutex) {
            this.head.context.fireReceived(this.session, msg, channel);
        }
    }

    public void fireSending(@NonNull Object msg, Reliability reliability, int channel) {
        synchronized (this.mutex) {
            this.tail.context.fireSending(this.session, msg, reliability, channel);
        }
    }

    public void fireException(@NonNull Throwable t) {
        synchronized (this.mutex) {
            this.head.context.fireException(this.session, t);
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
            for (Iterator<Node<S>> itr = this.nodes.iterator(); itr.hasNext(); ) {
                Node<S> node = itr.next();
                if (name.equals(node.name)) {
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
            for (ListIterator<Node<S>> itr = this.nodes.listIterator(); itr.hasNext(); ) {
                Node<S> node = itr.next();
                if (name.equals(node.name)) {
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
            this.head.next = this.head.prev = null;
            this.tail.next = this.tail.prev = null;
            if (this.nodes.isEmpty()) {
            } else {
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

                this.head.next = this.nodes.get(0);
                this.nodes.get(0).prev = this.head;
                this.tail.prev = this.nodes.get(i - 1);
                this.nodes.get(i - 1).next = this.tail;

                if (i == 1) {
                    this.nodes.get(0).next = this.tail;
                    this.nodes.get(i - 1).prev = this.head;
                } else if (i >= 2) {
                    this.nodes.get(0).next = this.nodes.get(1);
                    this.nodes.get(i - 1).prev = this.nodes.get(i - 2);
                }
                this.nodes.forEach(Node::rebuild);
            }
            this.head.rebuild();
            this.tail.rebuild();
        }
    }

    protected void addCallback(@NonNull S session, @NonNull Object msg, Reliability reliability, int channel) {
        this.queueAdder.add(this.sendQueue, session, msg, reliability, channel);
    }
}
