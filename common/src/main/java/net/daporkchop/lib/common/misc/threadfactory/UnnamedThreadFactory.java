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

import lombok.NonNull;

import java.util.concurrent.ThreadFactory;

/**
 * A {@link ThreadFactory} that does not apply any names to created threads.
 *
 * @author DaPorkchop_
 */
public class UnnamedThreadFactory extends AbstractThreadFactory {
    public UnnamedThreadFactory(ThreadFactory delegate, ClassLoader contextClassLoader, Thread.UncaughtExceptionHandler uncaughtExceptionHandler, int priority, boolean daemon) {
        super(delegate, contextClassLoader, uncaughtExceptionHandler, priority, daemon);
    }

    @Override
    protected String getName(@NonNull Runnable task, @NonNull Runnable wrappedTask, @NonNull Thread thread) {
        return null;
    }
}
