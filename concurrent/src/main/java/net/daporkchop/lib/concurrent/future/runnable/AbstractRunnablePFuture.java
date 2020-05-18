/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.concurrent.future.runnable;

import io.netty.util.concurrent.EventExecutor;
import lombok.NonNull;
import net.daporkchop.lib.concurrent.PRunnableFuture;
import net.daporkchop.lib.concurrent.future.DefaultPFuture;

import java.util.concurrent.RunnableFuture;

import static net.daporkchop.lib.unsafe.PUnsafe.*;

/**
 * Base implementation of a {@link net.daporkchop.lib.concurrent.PFuture} which is also a {@link RunnableFuture} task.
 *
 * @author DaPorkchop_
 */
public abstract class AbstractRunnablePFuture<V> extends DefaultPFuture<V> implements PRunnableFuture<V> {
    protected static final long STARTED_OFFSET = pork_getOffset(AbstractRunnablePFuture.class, "started");

    protected volatile int started = 0;

    public AbstractRunnablePFuture(@NonNull EventExecutor executor) {
        super(executor);
    }

    @Override
    public void run() {
        if (!compareAndSwapInt(this, STARTED_OFFSET, 0, 1)) {
            throw new IllegalStateException("Already started!");
        }

        try {
            if (this.setUncancellable()) {
                this.setSuccess(this.run0());
            }
        } catch (Throwable t) {
            this.setFailure(t);
        } finally {
            this.cleanup();
        }
    }

    protected abstract V run0() throws Exception;

    protected abstract void cleanup();
}
