/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2021 DaPorkchop_
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

package net.daporkchop.lib.common.misc.threadfactory;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.BitSet;
import java.util.concurrent.ThreadFactory;

/**
 * A {@link ThreadFactory} that applies a name to all created threads, formatted with an ID which "collapses": if a thread exits, the thread's ID will
 * be freed and become usable by other threads that are created later.
 * <p>
 * Basically just an aesthetic thing, but shouldn't cause any issues since it's only minor overhead and only occurs once on thread creation.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public class CollapsingIncrementingNamedThreadFactory extends AbstractThreadFactory {
    @Getter
    protected final String format;
    protected final BitSet usedIds = new BitSet();

    public CollapsingIncrementingNamedThreadFactory(@NonNull String format, ThreadFactory delegate, ClassLoader contextClassLoader, Thread.UncaughtExceptionHandler uncaughtExceptionHandler, int priority, boolean daemon) {
        super(delegate, contextClassLoader, uncaughtExceptionHandler, priority, daemon);

        this.format = format;
    }

    @Override
    protected Runnable wrapTask(@NonNull Runnable task) {
        return new TaskWrapper(task);
    }

    @Override
    protected String getName(@NonNull Runnable task, @NonNull Runnable wrappedTask, @NonNull Thread thread) {
        return String.format(this.format, ((TaskWrapper) wrappedTask).id);
    }

    private final class TaskWrapper implements Runnable {
        @NonNull
        protected final Runnable delegate;
        protected final int id;

        public TaskWrapper(@NonNull Runnable delegate) {
            this.delegate = delegate;

            BitSet usedIds = CollapsingIncrementingNamedThreadFactory.this.usedIds;
            synchronized (usedIds)    {
                usedIds.set(this.id = usedIds.nextClearBit(0));
            }
        }

        @Override
        public void run() {
            try {
                this.delegate.run();
            } finally {
                BitSet usedIds = CollapsingIncrementingNamedThreadFactory.this.usedIds;
                synchronized (usedIds) {
                    usedIds.clear(this.id);
                }
            }
        }
    }
}
