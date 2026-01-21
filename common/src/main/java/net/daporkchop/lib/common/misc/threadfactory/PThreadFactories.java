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

import io.netty.util.concurrent.FastThreadLocalThread;
import lombok.experimental.UtilityClass;

import java.util.concurrent.ThreadFactory;

/**
 * @author DaPorkchop_
 */
@UtilityClass
public class PThreadFactories {
    /**
     * The default {@link ThreadFactory} to use.
     * <p>
     * This makes a best-effort attempt to use {@link FastThreadLocalThread} instead of {@link Thread}.
     */
    public static final ThreadFactory DEFAULT_THREAD_FACTORY;

    static {
        ThreadFactory defaultThreadFactory = null;
        try {
            Class.forName("io.netty.util.concurrent.FastThreadLocalThread"); //check if class exists

            defaultThreadFactory = FastThreadLocalThread::new;
        } catch (ClassNotFoundException e) {
            defaultThreadFactory = Thread::new;
        } finally {
            DEFAULT_THREAD_FACTORY = defaultThreadFactory;
        }
    }

    /**
     * @return a new {@link ThreadFactoryBuilder}
     */
    public ThreadFactoryBuilder builder() {
        return new ThreadFactoryBuilder();
    }
}
