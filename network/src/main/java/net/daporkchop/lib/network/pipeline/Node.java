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
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.daporkchop.lib.network.pipeline.event.ExceptionCaught;
import net.daporkchop.lib.network.pipeline.event.MessageReceived;
import net.daporkchop.lib.network.pipeline.event.MessageSent;
import net.daporkchop.lib.network.pipeline.event.SessionClosed;
import net.daporkchop.lib.network.pipeline.event.SessionOpened;
import net.daporkchop.lib.network.session.AbstractUserSession;

/**
 * Holds all the filters in place.
 * <p>
 * Basically just a glorified node from a doubly-linked list.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Setter
public abstract class Node<S extends AbstractUserSession<S>> {
    protected final String name;
    protected final Filter<S> filter;

    protected Node<S> next;
    protected Node<S> prev;

    protected SessionOpened.Callback<S> sessionOpened;
    protected SessionClosed.Callback<S> sessionClosed;
    protected ExceptionCaught.Callback<S> exceptionCaught;
    protected MessageReceived.Callback<S, Object> messageReceived;
    protected MessageSent.Callback<S, Object> messageSent;

    protected void fireSessionOpened(@NonNull S session) {
        this.filter.sessionOpened(session, this.sessionOpened);
    }

    protected void fireSessionClosed(@NonNull S session) {
        this.filter.sessionClosed(session, this.sessionClosed);
    }

    protected void fireExceptionCaught(@NonNull S session, @NonNull Throwable t) {
        this.filter.exceptionCaught(session, t, this.exceptionCaught);
    }

    protected void fireMessageReceived(@NonNull S session, @NonNull Object msg, int channel) {
        this.filter.messageReceived(session, msg, channel, this.messageReceived);
    }

    protected void fireMessageSent(@NonNull S session, @NonNull Object msg, int channel) {
        this.filter.messageSent(session, msg, channel, this.messageSent);
    }

    protected void updateRelations()  {
        this.next.prev = this;
    }

    protected void updateSelf()  {
        this.sessionOpened = this.next::fireSessionOpened;
        this.sessionClosed = this.next::fireSessionClosed;
        this.exceptionCaught = this.next::fireExceptionCaught;

        this.messageReceived = this.next::fireMessageReceived; //TODO: have these two events find the first compatible filter in the pipeline
        this.messageSent = this.next::fireMessageSent;
    }
}
