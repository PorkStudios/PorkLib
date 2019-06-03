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

package net.daporkchop.lib.network.pipeline.util;

import lombok.NonNull;
import net.daporkchop.lib.network.pipeline.event.ClosedListener;
import net.daporkchop.lib.network.pipeline.event.ExceptionListener;
import net.daporkchop.lib.network.pipeline.event.OpenedListener;
import net.daporkchop.lib.network.pipeline.event.ReceivedListener;
import net.daporkchop.lib.network.pipeline.event.SendingListener;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.session.Reliability;

/**
 * A type capable of firing events.
 *
 * @author DaPorkchop_
 * @see net.daporkchop.lib.network.pipeline.Pipeline
 * @see EventContext
 */
public interface FireEvents<S extends AbstractUserSession<S>> extends OpenedListener.Fire<S>, ClosedListener.Fire<S>, ReceivedListener.Fire<S>, SendingListener.Fire<S>, ExceptionListener.Fire<S> {
    @Override
    void fireOpened(@NonNull S session);

    @Override
    void fireClosed(@NonNull S session);

    @Override
    void fireReceived(@NonNull S session, @NonNull Object msg, int channel);

    @Override
    void fireSending(@NonNull S session, @NonNull Object msg, Reliability reliability, int channel);

    @Override
    void fireException(@NonNull S session, @NonNull Throwable t);
}
