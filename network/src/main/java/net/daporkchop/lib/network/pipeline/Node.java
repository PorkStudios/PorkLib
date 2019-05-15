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

import io.netty.util.internal.TypeParameterMatcher;
import lombok.NonNull;
import net.daporkchop.lib.network.pipeline.event.ExceptionCaught;
import net.daporkchop.lib.network.pipeline.event.MessageReceived;
import net.daporkchop.lib.network.pipeline.event.MessageSent;
import net.daporkchop.lib.network.pipeline.event.PipelineHandler;
import net.daporkchop.lib.network.pipeline.event.SessionClosed;
import net.daporkchop.lib.network.pipeline.event.SessionOpened;
import net.daporkchop.lib.network.session.AbstractUserSession;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Holds all the filters in place.
 * <p>
 * Basically just a glorified node from a doubly-linked list.
 * <p>
 * Tail -> prev -> prev -> prev -> Head
 * Tail <- next <- next <- next <- Head
 *
 * @author DaPorkchop_
 */
class Node<S extends AbstractUserSession<S>> implements PipelineHandler.Firing<S> {
    protected final String name;
    protected final PipelineHandler<S> handler;

    protected Node<S> next;
    protected Node<S> prev;

    protected final TypeParameterMatcher canReceive;
    protected final Map<Class<?>, Node<S>> postReceivedDelegates = new IdentityHashMap<>(); //TODO: optimized class map?
    protected final TypeParameterMatcher canSend;
    protected final Map<Class<?>, Node<S>> postSentDelegates = new IdentityHashMap<>();

    protected SessionOpened.Callback<S> sessionOpened;
    protected SessionClosed.Callback<S> sessionClosed;
    protected ExceptionCaught.Callback<S> exceptionCaught;
    protected final MessageReceived.Callback<S, Object> messageReceived;
    protected final MessageSent.Callback<S, Object> messageSent;

    public Node(String name, @NonNull PipelineHandler<S> handler) {
        this.name = name;
        this.handler = handler;

        if (handler instanceof MessageReceived) {
            this.canReceive = TypeParameterMatcher.find(handler, MessageReceived.class, "I");
            this.messageReceived = (session, msg, channel) -> {
                //not using computeIfAbsent due to lambda allocation
                Node<S> next = this.postReceivedDelegates.get(msg.getClass());
                if (next == null) {
                    this.postReceivedDelegates.put(msg.getClass(), next = this.findNextMatchingNode(node -> node.canReceive(msg)));
                }
                next.fireMessageReceived(session, msg, channel);
            };
        } else {
            this.canReceive = null;
            this.messageReceived = null;
        }
        if (handler instanceof MessageSent) {
            this.canSend = TypeParameterMatcher.find(handler, MessageSent.class, "I");
            this.messageSent = (session, msg, channel) -> {
                Node<S> next = this.postSentDelegates.get(msg.getClass());
                if (next == null) {
                    this.postSentDelegates.put(msg.getClass(), next = this.findPrevMatchingNode(node -> node.canSend(msg)));
                }
                next.fireMessageSent(session, msg, channel);
            };
        } else {
            this.canSend = null;
            this.messageSent = null;
        }
    }

    @Override
    public void fireSessionOpened(@NonNull S session) {
        ((SessionOpened<S>) this.handler).sessionOpened(session, this.sessionOpened);
    }

    @Override
    public void fireSessionClosed(@NonNull S session) {
        ((SessionClosed<S>) this.handler).sessionClosed(session, this.sessionClosed);
    }

    @Override
    public void fireExceptionCaught(@NonNull S session, @NonNull Throwable t) {
        ((ExceptionCaught<S>) this.handler).exceptionCaught(session, t, this.exceptionCaught);
    }

    @Override
    public void fireMessageReceived(@NonNull S session, @NonNull Object msg, int channel) {
        ((MessageReceived<S, Object, Object>) this.handler).messageReceived(session, msg, channel, this.messageReceived);
    }

    @Override
    public void fireMessageSent(@NonNull S session, @NonNull Object msg, int channel) {
        ((MessageSent<S, Object, Object>) this.handler).messageSent(session, msg, channel, this.messageSent);
    }

    protected void updateRelations() {
        this.next.prev = this;

        this.postReceivedDelegates.clear();
        this.postSentDelegates.clear();
    }

    protected void updateSelf() {
        this.sessionOpened = this.findNextMatchingEvent(SessionOpened.class::isInstance)::fireSessionOpened;
        this.sessionClosed = this.findNextMatchingEvent(SessionClosed.class::isInstance)::fireSessionClosed;
        this.exceptionCaught = this.findNextMatchingEvent(ExceptionCaught.class::isInstance)::fireExceptionCaught;
    }

    protected boolean canReceive(Object msg)    {
        return this.canReceive != null && this.canReceive.match(msg);
    }

    protected boolean canSend(Object msg)    {
        return this.canSend != null && this.canSend.match(msg);
    }

    protected Node<S> findNextMatchingEvent(@NonNull Predicate<PipelineHandler<S>> condition) {
        return this.findNextMatchingNode(node -> condition.test(node.handler));
    }

    protected Node<S> findNextMatchingNode(@NonNull Predicate<Node<S>> condition) {
        Node<S> curr = this.next;
        while (!(curr instanceof Tail) && !condition.test(curr)) {
            curr = curr.next;
        }
        return curr;
    }

    protected Node<S> findPrevMatchingEvent(@NonNull Predicate<PipelineHandler<S>> condition) {
        return this.findPrevMatchingNode(node -> condition.test(node.handler));
    }

    protected Node<S> findPrevMatchingNode(@NonNull Predicate<Node<S>> condition) {
        Node<S> curr = this.prev;
        while (!(curr instanceof Head) && !condition.test(curr)) {
            curr = curr.prev;
        }
        return curr;
    }
}
