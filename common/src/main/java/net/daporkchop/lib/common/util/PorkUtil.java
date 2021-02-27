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

package net.daporkchop.lib.common.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.misc.string.PUnsafeStrings;
import net.daporkchop.lib.common.pool.handle.HandledPool;
import net.daporkchop.lib.unsafe.PUnsafe;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.regex.Matcher;

/**
 * Some helper methods and values that I use all over the place
 *
 * @author DaPorkchop_
 */
//TODO: clean this up a bit
@UtilityClass
public class PorkUtil {
    protected final long MATCHER_GROUPS_OFFSET = PUnsafe.pork_getOffset(Matcher.class, "groups");
    protected final long MATCHER_TEXT_OFFSET = PUnsafe.pork_getOffset(Matcher.class, "text");

    public final int TINY_BUFFER_SIZE = 32;
    public final int BUFFER_SIZE = 65536;

    public final HandledPool<byte[]> TINY_BUFFER_POOL = HandledPool.threadLocal(() -> new byte[TINY_BUFFER_SIZE], 4);
    public final HandledPool<ByteBuffer> DIRECT_TINY_BUFFER_POOL = HandledPool.threadLocal(() -> ByteBuffer.allocateDirect(TINY_BUFFER_SIZE), 4);
    public final HandledPool<byte[]> BUFFER_POOL = HandledPool.threadLocal(() -> new byte[BUFFER_SIZE], 4);
    public final HandledPool<ByteBuffer> DIRECT_BUFFER_POOL = HandledPool.threadLocal(() -> ByteBuffer.allocateDirect(BUFFER_SIZE), 4);

    public final HandledPool<StringBuilder> STRINGBUILDER_POOL = HandledPool.threadLocal(StringBuilder::new, 4); //TODO: make this soft

    public final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    public final String PORKLIB_VERSION = "0.5.4-SNAPSHOT"; //TODO: set this dynamically
    public final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    public final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    public final boolean NETTY_PRESENT = classExistsWithName("io.netty.util.concurrent.FastThreadLocal");

    /**
     * An alternative to {@link CharSequence#subSequence(int, int)} that can be faster for certain {@link CharSequence} implementations.
     *
     * @param seq   the {@link CharSequence} to get a subsequence of
     * @param start the first index, inclusive
     * @param end   the last index, exclusive
     * @return a subsequence of the given range of the given {@link CharSequence}
     * @see CharSequence#subSequence(int, int)
     */
    public static CharSequence subSequence(@NonNull CharSequence seq, int start, int end) {
        if (start == 0 && end == seq.length()) {
            return seq;
        }
        char[] arr = PUnsafeStrings.tryUnwrap(seq);
        return arr != null ? CharBuffer.wrap(arr, start, end - start) : seq.subSequence(start, end);
    }

    /**
     * Gets a value, falling back to another value if {@code null}
     *
     * @param value    the value to get
     * @param fallback the fallback value to use
     * @param <T>      the value type
     * @return the value, or the fallback value if the value was {@code null}
     */
    public static <T> T fallbackIfNull(T value, Object fallback) {
        return value != null ? value : uncheckedCast(fallback);
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> classForName(@NonNull String name) {
        try {
            return (Class<T>) Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> classForName(@NonNull String name, ClassLoader loader) {
        try {
            return (Class<T>) Class.forName(name, true, loader);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> uninitializedClassForName(@NonNull String name) {
        try {
            return (Class<T>) Class.forName(name, false, null);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> uninitializedClassForName(@NonNull String name, ClassLoader loader) {
        try {
            return (Class<T>) Class.forName(name, false, loader);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean classExistsWithName(@NonNull String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static Method getMethod(@NonNull Class<?> clazz, @NonNull String name, @NonNull Class<?>... params) {
        try {
            return clazz.getDeclaredMethod(name, params);
        } catch (NoSuchMethodException e) {
            try {
                return clazz.getMethod(name, params);
            } catch (NoSuchMethodException e1) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Creates a new instance of {@code sun.misc.SoftCache}.
     * <p>
     * This simply allows not showing compile-time warnings for using internal classes when creating
     * new instances.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @return a new {@code sun.misc.SoftCache}
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> newSoftCache() {
        try {
            Class<?> clazz = Class.forName("sun.misc.SoftCache");
            return (Map<K, V>) clazz.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to find class: sun.misc.SoftCache", e);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e.getCause() == null ? e : e.getCause());
        }
    }

    public static void simpleDisplayImage(@NonNull BufferedImage img) {
        simpleDisplayImage(false, img);
    }

    public static void simpleDisplayImage(boolean block, @NonNull BufferedImage... imgs) {
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        for (BufferedImage img : imgs) {
            frame.getContentPane().add(new JLabel(new ImageIcon(img)));
        }
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        if (block) {
            CompletableFuture future = new CompletableFuture();
            frame.addWindowListener(new WindowAdapter() {
                @Override
                @SuppressWarnings("unchecked")
                public void windowClosed(WindowEvent e) {
                    future.complete(null);
                }
            });
            try {
                future.get();
            } catch (InterruptedException
                    | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static String className(Object obj) {
        return obj == null ? "null" : obj.getClass().getCanonicalName();
    }

    public void unsafe_forceGC() {
        Object obj = new Object();
        long oldMem = Runtime.getRuntime().freeMemory();
        obj = null;
        do {
            System.gc();
        } while (Runtime.getRuntime().freeMemory() <= oldMem);
    }

    public static Object getNull() {
        return null;
    }

    public static CharSequence fastGroup(@NonNull Matcher matcher, int group) {
        matcher.start(); //this does a < 0 check internally
        if (group < 0 || group > matcher.groupCount()) {
            throw new IndexOutOfBoundsException("No group " + group);
        }
        int[] groups = PUnsafe.getObject(matcher, MATCHER_GROUPS_OFFSET);
        int start = groups[group << 1];
        int end = groups[(group << 1) + 1];
        if (start == -1 || end == -1) {
            return null;
        }
        return PUnsafe.<CharSequence>getObject(matcher, MATCHER_TEXT_OFFSET).subSequence(start, end);
    }

    @SuppressWarnings("unchecked")
    public static <T> T uncheckedCast(Object value) {
        return (T) value;
    }

    public static <T> T newInstance(@NonNull Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            PUnsafe.throwException(e);
        } catch (InvocationTargetException e) {
            PUnsafe.throwException(e.getCause() != null ? e.getCause() : e);
        }
        throw new IllegalStateException();
    }
}
