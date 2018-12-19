/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

package net.daporkchop.lib.common.util;

import lombok.NonNull;
import sun.misc.Cleaner;
import sun.misc.Unsafe;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * @author DaPorkchop_
 */
public class PorkUtil {
    public static final Unsafe unsafe;
    public static final ThreadLocal<byte[]> BUFFER_CACHE_SMALL = ThreadLocal.withInitial(() -> new byte[256]);
    private static final Function<char[], String> CHAR_ARRAY_WRAPPER;
    private static final Function<Throwable, StackTraceElement[]> GET_STACK_TRACE_WRAPPER;
    private static final AtomicInteger DEFAULT_EXECUTOR_THREAD_COUNTER = new AtomicInteger(0);
    public static final Executor DEFAULT_EXECUTOR = new ThreadPoolExecutor(
            0, Integer.MAX_VALUE,
            2, TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            runnable -> {
                Thread t = new Thread(runnable, String.format("PorkLib executor #%d", DEFAULT_EXECUTOR_THREAD_COUNTER.getAndIncrement()));
                //t.setDaemon(true);
                return t;
            }
    );

    static {
        {
            Unsafe u = null;
            try {
                Field f = Unsafe.class.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                u = (Unsafe) f.get(null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                unsafe = u;
            }
        }
        {
            Function<char[], String> func = chars -> {
                throw new UnsupportedOperationException();
            };
            try {
                Constructor<String> f = String.class.getDeclaredConstructor(char[].class, boolean.class);
                f.setAccessible(true);
                func = chars -> {
                    try {
                        return f.newInstance(chars, true);
                    } catch (Exception e)   {
                        throw new RuntimeException(e);
                    }
                };
            } catch (Exception e)   {
                throw new RuntimeException(e);
            } finally {
                CHAR_ARRAY_WRAPPER = func;
            }
        }
        {
            Function<Throwable, StackTraceElement[]> func = t -> {
                throw new UnsupportedOperationException();
            };
            try {
                Method m = Throwable.class.getDeclaredMethod("getOurStackTrace");
                m.setAccessible(true);
                func = t -> {
                    try {
                        return (StackTraceElement[]) m.invoke(t);
                    } catch (Exception e)   {
                        throw new RuntimeException(e);
                    }
                };
            } catch (Exception e)   {
                throw new RuntimeException(e);
            } finally {
                GET_STACK_TRACE_WRAPPER = func;
            }
        }
    }

    public static String wrap(@NonNull char[] chars)    {
        return CHAR_ARRAY_WRAPPER.apply(chars);
    }

    public static StackTraceElement[] getStackTrace(@NonNull Throwable t)   {
        return GET_STACK_TRACE_WRAPPER.apply(t);
    }

    public static void rm(@NonNull File file)   {
        while (file.exists()) {
            if (file.isDirectory()) {
                File[] files;
                while ((files = file.listFiles()) != null && files.length != 0) {
                    for (File f : files) {
                        rm(f);
                    }
                }
            }
            if (!file.delete()) {
                throw new IllegalStateException(String.format("Could not delete file: %s", file.getAbsolutePath()));
            }
        }
    }

    public static void release(@NonNull ByteBuffer buffer)  {
        Cleaner cleaner = ((sun.nio.ch.DirectBuffer) buffer).cleaner();
        if (cleaner != null) {
            cleaner.clean();
        }
    }
}
