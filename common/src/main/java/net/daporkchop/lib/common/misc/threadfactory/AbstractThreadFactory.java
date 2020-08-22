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

package net.daporkchop.lib.common.misc.threadfactory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.concurrent.ThreadFactory;

/**
 * Base implementation of a customizable {@link ThreadFactory}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public abstract class AbstractThreadFactory implements ThreadFactory {
    protected final ClassLoader contextClassLoader;
    protected final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    protected final int priority;
    protected final boolean daemon;

    @Override
    public Thread newThread(Runnable task) {
        Runnable wrappedTask = this.wrapTask(task);
        Thread thread = PThreadFactories.DEFAULT_THREAD_FACTORY.newThread(wrappedTask);

        String name = this.getName(task, wrappedTask, thread);
        if (name != null) {
            thread.setName(name);
        }

        if (this.contextClassLoader != null) {
            thread.setContextClassLoader(this.contextClassLoader);
        }
        if (this.uncaughtExceptionHandler != null) {
            thread.setUncaughtExceptionHandler(this.uncaughtExceptionHandler);
        }
        thread.setPriority(this.priority);
        thread.setDaemon(this.daemon);

        return thread;
    }

    protected Runnable wrapTask(Runnable task) {
        return task;
    }

    protected abstract String getName(Runnable task, Runnable wrappedTask, Thread thread);
}
