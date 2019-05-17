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

import lombok.NonNull;
import net.daporkchop.lib.logging.Logger;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.pipeline.Pipeline;
import net.daporkchop.lib.network.pipeline.event.ClosedListener;
import net.daporkchop.lib.network.pipeline.event.ExceptionListener;
import net.daporkchop.lib.network.pipeline.event.OpenedListener;
import net.daporkchop.lib.network.pipeline.event.ReceivedListener;
import net.daporkchop.lib.network.pipeline.event.SendingListener;
import net.daporkchop.lib.network.pipeline.util.EventContext;
import net.daporkchop.lib.network.pipeline.util.FireEvents;
import net.daporkchop.lib.network.session.AbstractUserSession;

/**
 * @author DaPorkchop_
 */
public interface PipelineEdgeListener<S extends AbstractUserSession<S>> extends FireEvents<S>, Logging {
    @Override
    default void fireOpened(@NonNull S session) {
    }

    @Override
    default void fireClosed(@NonNull S session) {
    }

    @Override
    default void fireReceived(@NonNull S session, @NonNull Object msg, int channel) {
        logger.warn("Received message reached the end of the pipeline: %s", msg.getClass().getCanonicalName());
    }

    @Override
    default void fireSending(@NonNull S session, @NonNull Object msg, int channel) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void fireExceptionCaught(@NonNull S session, @NonNull Throwable t) {
        StringBuilder builder = new StringBuilder();
        Logger.getStackTrace(t, builder::append);
        logger.alert("Exception reached the end of the pipeline!\n\n%s", builder);
    }
}
