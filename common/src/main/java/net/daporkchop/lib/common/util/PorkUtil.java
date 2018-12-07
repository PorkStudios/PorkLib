package net.daporkchop.lib.common.util;

import lombok.NonNull;
import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
    private static final Function<char[], String> CHAR_ARRAY_WRAPPER;
    private static final Function<Throwable, StackTraceElement[]> GET_STACK_TRACE_WRAPPER;
    private static final AtomicInteger DEFAULT_EXECUTOR_THREAD_COUNTER = new AtomicInteger(0);
    public static final Executor DEFAULT_EXECUTOR = new ThreadPoolExecutor(
            0, Integer.MAX_VALUE,
            2, TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            runnable -> new Thread(runnable, String.format("PorkLib executor #%d", DEFAULT_EXECUTOR_THREAD_COUNTER.getAndIncrement()))
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
}
