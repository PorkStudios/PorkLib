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
import net.daporkchop.lib.network.pipeline.util.EventContext;
import net.daporkchop.lib.network.pipeline.util.PipelineListener;
import net.daporkchop.lib.network.session.AbstractUserSession;

/**
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
class Node<S extends AbstractUserSession<S>> implements EventContext<S> {
    @Getter
    protected final Pipeline<S> pipeline;
    protected final PipelineListener<S> listener;

    protected Node<S> next;
    protected Node<S> prev;

    public Node(@NonNull Pipeline<S> pipeline, @NonNull PipelineListener<S> listener)   {
        this.pipeline = pipeline;
        this.listener = listener;
    }

    @Override
    public void fireOpened(@NonNull S session) {
    }

    @Override
    public void fireClosed(@NonNull S session) {
    }

    @Override
    public void fireReceived(@NonNull S session, @NonNull Object msg, int channel) {
    }

    @Override
    public void fireSending(@NonNull S session, @NonNull Object msg, int channel) {
    }

    @Override
    public void fireExceptionCaught(@NonNull S session, @NonNull Throwable t) {
    }

    protected void rebuild()    {
    }
}
