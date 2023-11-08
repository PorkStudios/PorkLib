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

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.concurrent.ThreadFactory;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Helper for creating customized {@link ThreadFactory} instances.
 *
 * @author DaPorkchop_
 * @see PThreadFactories#builder()
 */
@NoArgsConstructor(onConstructor_ = { @Deprecated })
@Setter
@Accessors(fluent = true, chain = true)
public class ThreadFactoryBuilder {
    protected String name;

    @NonNull
    protected ThreadFactory delegate = PThreadFactories.DEFAULT_THREAD_FACTORY;
    protected ClassLoader contextClassLoader;
    protected Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    protected int priority = Thread.NORM_PRIORITY;
    protected boolean daemon;

    protected boolean formatId;
    protected boolean collapsingId;

    /**
     * Alias for {@link #daemon(boolean)} with {@code true}.
     */
    public ThreadFactoryBuilder daemon() {
        return this.daemon(true);
    }

    /**
     * Alias for {@link #formatId(boolean)} with {@code true}.
     */
    public ThreadFactoryBuilder formatId() {
        return this.formatId(true);
    }

    /**
     * Alias for {@link #formatId(boolean)} with {@code true} and {@link #collapsingId(boolean)} with {@code true}.
     */
    public ThreadFactoryBuilder collapsingId() {
        return this.formatId(true).collapsingId(true);
    }

    /**
     * Sets the priority of threads created by the {@link ThreadFactory}.
     *
     * @param priority the new priority
     * @return this {@link ThreadFactoryBuilder}
     */
    public ThreadFactoryBuilder priority(int priority) {
        checkArg(priority >= Thread.MIN_PRIORITY && priority <= Thread.MAX_PRIORITY, "invalid priority: %d (must be in range [%d-%d])", priority, Thread.MIN_PRIORITY, Thread.MAX_PRIORITY);

        this.priority = priority;
        return this;
    }

    /**
     * Alias for {@link #priority(int)} with {@link Thread#MIN_PRIORITY}.
     */
    public ThreadFactoryBuilder minPriority() {
        return this.priority(Thread.MIN_PRIORITY);
    }

    /**
     * Alias for {@link #priority(int)} with {@link Thread#MAX_PRIORITY}.
     */
    public ThreadFactoryBuilder maxPriority() {
        return this.priority(Thread.MAX_PRIORITY);
    }

    /**
     * Builds a {@link ThreadFactory} using the currently configured settings.
     *
     * @return a new {@link ThreadFactory}
     */
    public ThreadFactory build() {
        if (!this.formatId) {
            if (this.name == null) {
                if (this.contextClassLoader == null && this.uncaughtExceptionHandler == null && this.priority == Thread.NORM_PRIORITY && !this.daemon) {
                    return this.delegate;
                } else {
                    return new UnnamedThreadFactory(this.delegate, this.contextClassLoader, this.uncaughtExceptionHandler, this.priority, this.daemon);
                }
            } else {
                return new FixedNamedThreadFactory(this.name, this.delegate, this.contextClassLoader, this.uncaughtExceptionHandler, this.priority, this.daemon);
            }
        } else if (this.name == null) {
            throw new IllegalStateException("formatId is set, but no name is given!");
        } else if (this.collapsingId) {
            return new CollapsingIncrementingNamedThreadFactory(this.name, this.delegate, this.contextClassLoader, this.uncaughtExceptionHandler, this.priority, this.daemon);
        } else {
            return new IncrementingNamedThreadFactory(this.name, this.delegate, this.contextClassLoader, this.uncaughtExceptionHandler, this.priority, this.daemon);
        }
    }
}
