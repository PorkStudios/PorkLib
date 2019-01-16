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

package net.daporkchop.lib.common.util;

import lombok.NonNull;
import sun.misc.Cleaner;
import sun.misc.Unsafe;

import java.io.File;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Arrays;
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
    private static long string_valueOffset;
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
            long l = -1L;
            try {
                Field f = String.class.getDeclaredField("value");
                l = unsafe.objectFieldOffset(f);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } finally {
                string_valueOffset = l;
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
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                };
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                GET_STACK_TRACE_WRAPPER = func;
            }
        }
    }

    /**
     * Wraps a char array into a {@link String} without copying the array.
     *
     * @param chars the char array to copy
     * @return a new string
     */
    public static String wrap(@NonNull char[] chars) {
        String s = (String) PUnsafe.allocateInstance(String.class);
        PUnsafe.putObject(s, string_valueOffset, chars);
        return s;
    }

    public static StackTraceElement[] getStackTrace(@NonNull Throwable t) {
        return GET_STACK_TRACE_WRAPPER.apply(t);
    }

    public static void rm(@NonNull File file) {
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

    public static void release(@NonNull ByteBuffer buffer) {
        Cleaner cleaner = ((sun.nio.ch.DirectBuffer) buffer).cleaner();
        if (cleaner != null) {
            cleaner.clean();
        }
    }

    public static Class<?> classForName(@NonNull String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw PConstants.p_exception(e);
        }
    }

    public static Method getMethod(@NonNull Class<?> clazz, @NonNull String name, @NonNull Class<?>... params) {
        try {
            return clazz.getDeclaredMethod(name, params);
        } catch (NoSuchMethodException e) {
            try {
                return clazz.getMethod(name, params);
            } catch (NoSuchMethodException e1) {
                throw PConstants.p_exception(e);
            }
        }
    }

    //TODO: include this in PorkLib reflection, it's a bit of a mess because of generics
    @SuppressWarnings("unchecked")
    public static <T> T getLambdaReflection(@NonNull Class<T> interfaz, @NonNull Class<?> methodHolder, boolean isStatic, boolean isGeneric, @NonNull Class<?> returnType, @NonNull String methodName, @NonNull Class<?>... params) {
        try {
            Method reflected = getMethod(methodHolder, methodName, params);

            Method real;
            A:
            try {
                if (false)  {
                    throw new NoSuchMethodException();
                }
                real = getMethod(interfaz, methodName, params);
            } catch (NoSuchMethodException e) {
                //not to worry, we'll try and find one ourselves!
                for (Method method : interfaz.getMethods()) {
                    if (Arrays.equals(method.getParameterTypes(), params)) {
                        real = method;
                        break A;
                    }
                }
                throw new IllegalArgumentException(String.format("Unable to locate method matching %s in %s!", reflected, interfaz.getCanonicalName()));
            }

            MethodHandles.Lookup lookup;
            {
                //TODO: cache lookups per-class using something or other
                Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
                constructor.setAccessible(true);
                lookup = constructor.newInstance(methodHolder, -1);
                lookup = constructor.newInstance(interfaz, -1);
            }
            MethodType type = MethodType.methodType(returnType, params);
            MethodType actualType;
            if (isGeneric)  {
                Class<?> rClass;
                if (returnType == boolean.class
                        || returnType == byte.class
                        || returnType == short.class
                        || returnType == int.class
                        || returnType == long.class
                        || returnType == float.class
                        || returnType == double.class
                        || returnType == char.class
                        || returnType == void.class)    {
                    rClass = returnType;
                } else {
                    rClass = Object.class;
                }
                actualType = MethodType.methodType(rClass, params);
            } else {
                actualType = type;
            }
            MethodHandle handle = isStatic ?
                    lookup.findStatic(methodHolder, methodName, type) :
                    lookup.findVirtual(methodHolder, methodName, type);
            PUnsafe.ensureClassInitialized(interfaz);
            CallSite site = LambdaMetafactory.metafactory(
                    lookup,
                    real.getName(),
                    MethodType.methodType(interfaz),
                    actualType,
                    handle,
                    actualType
            );
            MethodHandle target = site.getTarget();
            Object o = target.invoke();
            return (T) o;
        } catch (Throwable e) {
            throw PConstants.p_exception(e);
        }
    }
}
