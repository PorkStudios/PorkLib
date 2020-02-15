/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.pool.handle.DefaultThreadHandledPool;
import net.daporkchop.lib.common.pool.handle.HandledPool;
import net.daporkchop.lib.unsafe.PUnsafe;
import sun.misc.Cleaner;
import sun.misc.SoftCache;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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
    public final long STRING_VALUE_OFFSET   = PUnsafe.pork_getOffset(String.class, "value");
    public final long MATCHER_GROUPS_OFFSET = PUnsafe.pork_getOffset(Matcher.class, "groups");
    public final long MATCHER_TEXT_OFFSET   = PUnsafe.pork_getOffset(Matcher.class, "text");

    public final Class<?> ABSTRACTSTRINGBUILDER_CLASS        = classForName("java.lang.AbstractStringBuilder");
    public final long     ABSTRACTSTRINGBUILDER_VALUE_OFFSET = PUnsafe.pork_getOffset(ABSTRACTSTRINGBUILDER_CLASS, "value");

    private final Function<Throwable, StackTraceElement[]> GET_STACK_TRACE_WRAPPER;

    public final HandledPool<byte[]>        BUFFER_POOL        = new DefaultThreadHandledPool<>(() -> new byte[PUnsafe.PAGE_SIZE], 4);
    public final HandledPool<StringBuilder> STRINGBUILDER_POOL = new DefaultThreadHandledPool<>(StringBuilder::new, 4); //TODO: make this soft

    private final AtomicInteger DEFAULT_EXECUTOR_THREAD_COUNTER = new AtomicInteger(0);
    public final  Executor      DEFAULT_EXECUTOR                = new ThreadPoolExecutor(
            0, Integer.MAX_VALUE,
            2, TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            runnable -> new Thread(runnable, String.format("PorkLib executor #%d", DEFAULT_EXECUTOR_THREAD_COUNTER.getAndIncrement()))
    );

    public final DateFormat DATE_FORMAT     = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    public final String     PORKLIB_VERSION = "0.5.1-SNAPSHOT";
    public final int        CPU_COUNT       = Runtime.getRuntime().availableProcessors();

    static {
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
     * Wraps a {@code char[]} into a {@link String} without copying the array.
     *
     * @param chars the {@code char[]} to wrap
     * @return a new {@link String}
     */
    public String wrap(@NonNull char[] chars) {
        String s = PUnsafe.allocateInstance(String.class);
        PUnsafe.putObject(s, STRING_VALUE_OFFSET, chars);
        return s;
    }

    /**
     * Unwraps a {@link CharSequence} into a {@code char[]} without copying the array, if possible.
     * <p>
     * Be aware that the returned {@code char[]} may be larger than the actual size of the {@link CharSequence}. It is therefore strongly advised to use
     * {@link CharSequence#length()} instead of {@code char[]#length}.
     *
     * @param seq the {@link CharSequence} to unwrap
     * @return the value of the {@link CharSequence} as a {@code char[]}, or {@code null} if the given {@link CharSequence} cannot be unwrapped
     */
    public char[] tryUnwrap(@NonNull CharSequence seq) {
        if (seq instanceof String) {
            return PUnsafe.getObject(seq, STRING_VALUE_OFFSET);
        } else if (seq instanceof StringBuilder || seq instanceof StringBuffer) {
            return PUnsafe.getObject(seq, ABSTRACTSTRINGBUILDER_VALUE_OFFSET);
        } else {
            return null;
        }
    }

    /**
     * Unwraps a {@link String} into a {@code char[]} without copying the array.
     *
     * @param string the {@link String} to unwrap
     * @return the value of the {@link String} as a {@code char[]}
     */
    public char[] unwrap(@NonNull String string) {
        return PUnsafe.getObject(string, STRING_VALUE_OFFSET);
    }

    /**
     * Unwraps a {@link StringBuilder} into a {@code char[]} without copying the array.
     * <p>
     * Be aware that the returned {@code char[]} may be larger than the actual size of the {@link StringBuilder}. It is therefore strongly advised to use
     * {@link StringBuilder#length()} instead of {@code char[]#length}.
     *
     * @param builder the {@link StringBuilder} to unwrap
     * @return the value of the {@link StringBuilder} as a {@code char[]}
     */
    public char[] unwrap(@NonNull StringBuilder builder) {
        return PUnsafe.getObject(builder, ABSTRACTSTRINGBUILDER_VALUE_OFFSET);
    }

    /**
     * Unwraps a {@link StringBuffer} into a {@code char[]} without copying the array.
     * <p>
     * Be aware that the returned {@code char[]} may be larger than the actual size of the {@link StringBuffer}. It is therefore strongly advised to use
     * {@link StringBuffer#length()} instead of {@code char[]#length}.
     *
     * @param buffer the {@link StringBuffer} to unwrap
     * @return the value of the {@link StringBuffer} as a {@code char[]}
     */
    public char[] unwrap(@NonNull StringBuffer buffer) {
        return PUnsafe.getObject(buffer, ABSTRACTSTRINGBUILDER_VALUE_OFFSET);
    }

    /**
     * An alternative to {@link CharSequence#subSequence(int, int)} that can be faster for certain {@link CharSequence} implementations.
     *
     * @param seq   the {@link CharSequence} to get a subsequence of
     * @param start the first index, inclusive
     * @param end   the last index, exclusive
     * @return a subsequence of the given range of the given {@link CharSequence}
     * @see CharSequence#subSequence(int, int)
     */
    public CharSequence subSequence(@NonNull CharSequence seq, int start, int end) {
        if (start == 0 && end == seq.length()) {
            return seq;
        }
        char[] arr = null;
        if (seq instanceof String) {
            arr = PUnsafe.getObject(seq, STRING_VALUE_OFFSET);
        } else if (seq instanceof StringBuilder || seq instanceof StringBuffer) {
            arr = PUnsafe.getObject(seq, ABSTRACTSTRINGBUILDER_VALUE_OFFSET);
        }
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
    public <T> T fallbackIfNull(T value, T fallback) {
        return value != null ? value : fallback;
    }

    public StackTraceElement[] getStackTrace(@NonNull Throwable t) {
        return GET_STACK_TRACE_WRAPPER.apply(t);
    }

    public void release(@NonNull ByteBuffer buffer) {
        Cleaner cleaner = ((sun.nio.ch.DirectBuffer) buffer).cleaner();
        if (cleaner != null) {
            cleaner.clean();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Class<T> classForName(@NonNull String name) {
        try {
            return (Class<T>) Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Class<T> uninitializedClassForName(@NonNull String name) {
        try {
            return (Class<T>) Class.forName(name, false, null);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Class<T> uninitializedClassForName(@NonNull String name, ClassLoader loader) {
        try {
            return (Class<T>) Class.forName(name, false, loader);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean classExistsWithName(@NonNull String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public Method getMethod(@NonNull Class<?> clazz, @NonNull String name, @NonNull Class<?>... params) {
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
     * Creates a new instance of {@link SoftCache}.
     * <p>
     * This simply allows not showing compile-time warnings for using internal classes when creating
     * new instances.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @return a new {@link SoftCache}
     */
    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> newSoftCache() {
        return (Map<K, V>) new SoftCache();
    }

    public void simpleDisplayImage(@NonNull BufferedImage img) {
        simpleDisplayImage(false, img);
    }

    public void simpleDisplayImage(boolean block, @NonNull BufferedImage... imgs) {
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        for (BufferedImage img : imgs)  {
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

    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public String className(Object obj) {
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

    public Object getNull() {
        return null;
    }

    public CharSequence fastGroup(@NonNull Matcher matcher, int group) {
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

    public void assertInRange(int size, int start, int end) throws IndexOutOfBoundsException {
        if (start < 0) {
            throw new IndexOutOfBoundsException(String.format("start (%d) < 0", start));
        } else if (end > size) {
            throw new IndexOutOfBoundsException(String.format("end (%d) > size (%d)", end, size));
        } else if (end < start) {
            throw new IllegalArgumentException(String.format("end (%d) < start (%d)", end, start));
        }
    }

    public void assertInRangeLen(int size, int start, int length) throws IndexOutOfBoundsException {
        assertInRange(size, start, start + length);
    }
}
