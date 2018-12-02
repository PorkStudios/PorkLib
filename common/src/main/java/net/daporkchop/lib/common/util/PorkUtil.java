package net.daporkchop.lib.common.util;

import lombok.NonNull;
import sun.misc.Unsafe;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * @author DaPorkchop_
 */
public class PorkUtil {
    public static final Unsafe unsafe;
    private static final Function<char[], String> CHAR_ARRAY_WRAPPER;
    private static final Function<Throwable, StackTraceElement[]> GET_STACK_TRACE_WRAPPER;

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
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files == null) {
                    throw new NullPointerException(file.getAbsolutePath());
                }
                for (File f : files) {
                    rm(f);
                }
            }
            if (!file.delete()) {
                throw new IllegalStateException(String.format("Could not delete file: %s", file.getAbsolutePath()));
            }
        }
    }
}
