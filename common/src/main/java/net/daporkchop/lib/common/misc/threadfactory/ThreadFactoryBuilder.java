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

package net.daporkchop.lib.common.misc.threadfactory;

import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.concurrent.ThreadFactory;

/**
 * Helper for creating customized {@link ThreadFactory} instances.
 *
 * @author DaPorkchop_
 */
@Setter
@Accessors(fluent = true, chain = true)
public class ThreadFactoryBuilder {
    /**
     * @return a default thread factory
     */
    public static ThreadFactory defaultThreadFactory() {
        return Thread::new;
    }

    protected String name;

    protected ClassLoader                     contextClassLoader;
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
     * Builds a {@link ThreadFactory} using the currently configured settings.
     *
     * @return a new {@link ThreadFactory}
     */
    public ThreadFactory build() {
        if (!this.formatId) {
            if (this.name == null) {
                if (this.contextClassLoader == null && this.uncaughtExceptionHandler == null && this.priority == Thread.NORM_PRIORITY && !this.daemon) {
                    return defaultThreadFactory();
                } else {
                    return new UnnamedThreadFactory(this.contextClassLoader, this.uncaughtExceptionHandler, this.priority, this.daemon);
                }
            } else {
                return new FixedNamedThreadFactory(this.name, this.contextClassLoader, this.uncaughtExceptionHandler, this.priority, this.daemon);
            }
        } else if (this.name == null) {
            throw new IllegalStateException("formatId is set, but no name is given!");
        } else if (this.collapsingId) {
            return new CollapsingIncrementingNamedThreadFactory(this.name, this.contextClassLoader, this.uncaughtExceptionHandler, this.priority, this.daemon);
        } else {
            return new IncrementingNamedThreadFactory(this.name, this.contextClassLoader, this.uncaughtExceptionHandler, this.priority, this.daemon);
        }
    }
}
