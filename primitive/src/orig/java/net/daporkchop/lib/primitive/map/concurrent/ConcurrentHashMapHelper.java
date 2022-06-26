/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
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

package net.daporkchop.lib.primitive.map.concurrent;

import lombok.experimental.UtilityClass;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Various constants used by concurrent hash map implementations.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class ConcurrentHashMapHelper {
    public static final int MAXIMUM_CAPACITY = 1 << 30;
    public static final int DEFAULT_CAPACITY = 16;
    public static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    public static final int DEFAULT_CONCURRENCY_LEVEL = 16;
    public static final float LOAD_FACTOR = 0.75f;
    public static final int TREEIFY_THRESHOLD = 8;
    public static final int UNTREEIFY_THRESHOLD = 6;
    public static final int MIN_TREEIFY_CAPACITY = 64;
    public static final int MIN_TRANSFER_STRIDE = 16;
    public static final int RESIZE_STAMP_BITS = 16;
    public static final int MAX_RESIZERS = (1 << (32 - RESIZE_STAMP_BITS)) - 1;
    public static final int RESIZE_STAMP_SHIFT = 32 - RESIZE_STAMP_BITS;

    public static final int FORWARDING = -1;
    public static final int TREEBIN = -2;
    public static final int RESERVED = -3;
    public static final int HASH_BITS = 0x7FFFFFFF;

    public static final int NCPU = Runtime.getRuntime().availableProcessors();

    public static final long THREAD_PROBE_OFFSET = PUnsafe.pork_getOffset(Thread.class, "threadLocalRandomProbe");

    public static int tableSizeFor(int c) {
        int n = c - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    public static Class<?> comparableClassFor(Object x) {
        if (x instanceof Comparable) {
            Class<?> c = x.getClass();
            Type[] ts, as;
            ParameterizedType p;
            if (c == String.class) {
                return c;
            } else if ((ts = c.getGenericInterfaces()) != null) {
                for (Type t : ts) {
                    if (t instanceof ParameterizedType &&
                            (p = (ParameterizedType) t).getRawType() == Comparable.class &&
                            (as = p.getActualTypeArguments()) != null &&
                            as.length == 1 && as[0] == c) {
                        return c;
                    }
                }
            }
        }
        return null;
    }

    @SuppressWarnings({
            "rawtypes",
            "unchecked"
    })
    public static int compareComparables(Class<?> kc, Object k, Object x) {
        return x == null || x.getClass() != kc ? 0 : ((Comparable) k).compareTo(x);
    }

    public static int resizeStamp(int n) {
        return Integer.numberOfLeadingZeros(n) | (1 << (RESIZE_STAMP_BITS - 1));
    }

    public static <T> T getArrayVolatile(Object base, int index) {
        return PUnsafe.getObjectVolatile(base, PUnsafe.arrayObjectElementOffset(index));
    }

    public static void setArrayVolatile(Object base, int index, Object value) {
        PUnsafe.putObjectVolatile(base, PUnsafe.arrayObjectElementOffset(index), value);
    }

    public static boolean casArray(Object base, int index, Object expect, Object value) {
        return PUnsafe.compareAndSwapObject(base, PUnsafe.arrayObjectElementOffset(index), expect, value);
    }

    public static int getProbe() {
        return PUnsafe.getInt(Thread.currentThread(), THREAD_PROBE_OFFSET);
    }

    public static void initProbe() {
        ThreadLocalRandom.current();
    }

    public static int advanceProbe(int probe)   {
        probe ^= probe << 13;   // xorshift
        probe ^= probe >>> 17;
        probe ^= probe << 5;
        PUnsafe.putInt(Thread.currentThread(), THREAD_PROBE_OFFSET, probe);
        return probe;
    }
}
