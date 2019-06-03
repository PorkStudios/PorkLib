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

import net.daporkchop.lib.network.session.Reliability;
import net.daporkchop.lib.network.util.GenericMatcher;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.network.pipeline.event.ClosedListener;
import net.daporkchop.lib.network.pipeline.event.ExceptionListener;
import net.daporkchop.lib.network.pipeline.event.OpenedListener;
import net.daporkchop.lib.network.pipeline.event.ReceivedListener;
import net.daporkchop.lib.network.pipeline.event.SendingListener;
import net.daporkchop.lib.network.pipeline.util.EventContext;
import net.daporkchop.lib.network.pipeline.util.FireEvents;
import net.daporkchop.lib.network.pipeline.util.PipelineListener;
import net.daporkchop.lib.network.session.AbstractUserSession;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
class Node<S extends AbstractUserSession<S>> implements FireEvents<S> {
    @Getter
    protected final Pipeline<S> pipeline;
    protected final PipelineListener<S> listener;

    protected final String name;

    protected Node<S> next;
    protected Node<S> prev;

    private final GenericMatcher canReceive;
    private final GenericMatcher canSend;

    protected final Context context = new Context();

    public Node(@NonNull Pipeline<S> pipeline, @NonNull String name, @NonNull PipelineListener<S> listener) {
        this.pipeline = pipeline;
        this.name = name;
        this.listener = listener;

        this.canReceive = listener instanceof ReceivedListener ? GenericMatcher.find(listener.getClass(), ReceivedListener.class, "I") : null;
        this.canSend = listener instanceof SendingListener ? GenericMatcher.find(listener.getClass(), SendingListener.class, "I") : null;
    }

    @Override
    public void fireOpened(@NonNull S session) {
        ((OpenedListener<S>) this.listener).opened(this.context, session);
    }

    @Override
    public void fireClosed(@NonNull S session) {
        ((ClosedListener<S>) this.listener).closed(this.context, session);
    }

    @Override
    public void fireReceived(@NonNull S session, @NonNull Object msg, int channel) {
        ((ReceivedListener<S, Object>) this.listener).received(this.context, session, msg, channel);
    }

    @Override
    public void fireSending(@NonNull S session, @NonNull Object msg, Reliability reliability, int channel) {
        ((SendingListener<S, Object>) this.listener).sending(this.context, session, msg, reliability, channel);
    }

    @Override
    public void fireException(@NonNull S session, @NonNull Throwable t) {
        ((ExceptionListener<S>) this.listener).exceptionCaught(this.context, session, t);
    }

    protected boolean canReceive(@NonNull Object o) {
        return this.canReceive != null && this.canReceive.getType().isInstance(o);
    }

    protected boolean canSend(@NonNull Object o) {
        return this.canSend != null && this.canSend.getType().isInstance(o);
    }

    protected void rebuild() {
        this.context.rebuild();
    }

    protected class Context implements EventContext<S> {
        protected OpenedListener.Fire<S> opened;
        protected ClosedListener.Fire<S> closed;
        protected ExceptionListener.Fire<S> exception;

        protected final Map<Class<?>, ReceivedListener.Fire<S>> received = new IdentityHashMap<>(); //TODO: optimized map for classes?
        protected final Map<Class<?>, SendingListener.Fire<S>> sending = new IdentityHashMap<>();

        @Override
        public Pipeline<S> pipeline() {
            return Node.this.pipeline;
        }

        @Override
        public void fireOpened(@NonNull S session) {
            this.opened.fireOpened(session);
        }

        @Override
        public void fireClosed(@NonNull S session) {
            this.closed.fireClosed(session);
        }

        @Override
        public void fireReceived(@NonNull S session, @NonNull Object msg, int channel) {
            ReceivedListener.Fire<S> callback = this.received.get(msg.getClass());
            if (callback == null) {
                //no computeIfAbsent due to lambda allocation
                Node<S> node = Node.this.next;
                while (node != null && !node.canReceive(msg)) {
                    node = node.next;
                }
                this.received.put(msg.getClass(), callback = node == null ? this.pipeline().listener : node);
            }
            callback.fireReceived(session, msg, channel);
        }

        @Override
        public void fireSending(@NonNull S session, @NonNull Object msg, Reliability reliability, int channel) {
            SendingListener.Fire<S> callback = this.sending.get(msg.getClass());
            if (callback == null) {
                Node<S> node = Node.this.prev;
                while (node != null && !node.canSend(msg)) {
                    node = node.prev;
                }
                this.sending.put(msg.getClass(), callback = node == null ? this.pipeline()::addCallback : node);
            }
            callback.fireSending(session, msg, reliability, channel);
        }

        @Override
        public void fireException(@NonNull S session, @NonNull Throwable t) {
            this.exception.fireException(session, t);
        }

        protected void rebuild() {
            {
                Node<S> node = Node.this.next;
                while (node != null && !(node.listener instanceof OpenedListener)) {
                    node = node.next;
                }
                this.opened = node == null ? this.pipeline().listener : node;
            }
            {
                Node<S> node = Node.this.next;
                while (node != null && !(node.listener instanceof ClosedListener)) {
                    node = node.next;
                }
                this.closed = node == null ? this.pipeline().listener : node;
            }
            {
                Node<S> node = Node.this.next;
                while (node != null && !(node.listener instanceof ExceptionListener)) {
                    node = node.next;
                }
                this.exception = node == null ? this.pipeline().listener : node;
            }

            this.received.clear();
            this.sending.clear();
        }
    }
}
