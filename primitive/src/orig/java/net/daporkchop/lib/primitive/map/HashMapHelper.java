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

package net.daporkchop.lib.primitive.map;

import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.math.BinMath;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Shared constants used by HashMap implementations.
 *
 * @author DaPorkchop_
 */
@UtilityClass
class HashMapHelper {
    public static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
    public static final int MAXIMUM_CAPACITY = 1 << 30;
    public static final float DEFAULT_LOAD_FACTOR = 0.75f;
    public static final int TREEIFY_THRESHOLD = 8;
    public static final int UNTREEIFY_THRESHOLD = 6;
    public static final int MIN_TREEIFY_CAPACITY = 64;

    public static Class<?> comparableClassFor(Object x) {
        if (x instanceof Comparable) {
            Class<?> c = x.getClass();
            Type[] ts;
            if (c == String.class) {
                return c;
            } else if ((ts = c.getGenericInterfaces()) != null) {
                Type t;
                ParameterizedType p;
                Type[] as;
                for (int i = 0; i < ts.length; ++i) {
                    if ((t = ts[i]) instanceof ParameterizedType
                            && (p = (ParameterizedType) t).getRawType() == Comparable.class
                            && (as = p.getActualTypeArguments()) != null
                            && as.length == 1 && as[0] == c) {
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

    protected static int tableSizeFor(int capacity) {
        capacity = BinMath.roundToNearestPowerOf2(capacity);
        return capacity < 0 ? 1 : capacity >= MAXIMUM_CAPACITY ? MAXIMUM_CAPACITY : capacity + 1;
    }
}
