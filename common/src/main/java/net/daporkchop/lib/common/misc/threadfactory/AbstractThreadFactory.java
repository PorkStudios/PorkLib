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
    protected final ClassLoader                     contextClassLoader;
    protected final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    protected final int                             priority;
    protected final boolean                         daemon;

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = this.createThread(r, this.getName(r));

        if (this.contextClassLoader != null)    {
            thread.setContextClassLoader(this.contextClassLoader);
        }
        if (this.uncaughtExceptionHandler != null)  {
            thread.setUncaughtExceptionHandler(this.uncaughtExceptionHandler);
        }
        thread.setPriority(this.priority);
        thread.setDaemon(this.daemon);

        return thread;
    }

    protected Thread createThread(Runnable r, String name)   {
        return name == null ? new Thread(r) : new Thread(r, name);
    }

    protected abstract String getName(Runnable r);
}
