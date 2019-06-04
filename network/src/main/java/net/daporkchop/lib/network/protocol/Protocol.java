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

package net.daporkchop.lib.network.protocol;

import lombok.NonNull;
import net.daporkchop.lib.network.session.pipeline.Pipeline;
import net.daporkchop.lib.network.session.pipeline.util.PipelineListener;
import net.daporkchop.lib.network.session.AbstractUserSession;

/**
 * The simplest possible representation of a protocol.
 *
 * @author DaPorkchop_
 */
public interface Protocol<S extends AbstractUserSession<S>> {
    /**
     * @return this protocol's session factory
     */
    SessionFactory<S> sessionFactory();

    /**
     * @return this protocol's pipeline initializer
     */
    PipelineInitializer<S> pipelineInitializer();

    /**
     * @author DaPorkchop_
     */
    @FunctionalInterface
    interface PipelineInitializer<S extends AbstractUserSession<S>> {
        /**
         * Initializes the event pipeline for a session.
         * <p>
         * The pipeline will always have been initialized first according to the transport engine, however unless you
         * know what you're doing you should always add your handlers last (using {@link Pipeline#addLast(PipelineListener)}
         * or {@link Pipeline#addLast(String, PipelineListener)}).
         *
         * @param pipeline the pipeline to be initialized
         * @param session  the session to which the pipeline belongs
         */
        void initPipeline(@NonNull Pipeline<S> pipeline, @NonNull S session);
    }

    /**
     * @author DaPorkchop_
     */
    @FunctionalInterface
    interface SessionFactory<S extends AbstractUserSession<S>> {
        S newSession();
    }
}
