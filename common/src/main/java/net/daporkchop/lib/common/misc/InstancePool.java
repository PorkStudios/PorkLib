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

package net.daporkchop.lib.common.misc;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A pool for sharing instances of classes that only really need one instance to be around
 * at a time.
 * <p>
 * Having these instances pooled is better than creating new instances all the time (because
 * heap usage) and is also better than having a single static instance (because the class can
 * never be unloaded in that scenario). All references are soft, to allow the garbage collector to
 * free up the memory if needed.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class InstancePool {
    private final Map<Class<?>, SoftReference<?>> MAP = new ConcurrentHashMap<>();

    public static <T> T getInstance(@NonNull Class<T> clazz) {
        @SuppressWarnings("unchecked")
        SoftReference<T> ref = (SoftReference<T>) MAP.computeIfAbsent(clazz, c -> new SoftReference<>(createInstance(c)));
        T val;
        while ((val = ref.get()) == null)   {
            synchronized (clazz)    {
                @SuppressWarnings("unchecked")
                SoftReference<T> newRef = (SoftReference<T>) MAP.get(clazz);
                if (newRef == ref)  {
                    //reference in map has not been changed, create new reference
                    MAP.replace(clazz, ref, newRef = new SoftReference<>(createInstance(clazz)));
                }
                ref = newRef;
            }
        }
        return val;
    }

    private static <T> T createInstance(@NonNull Class<T> clazz)    {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(clazz.getCanonicalName(), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e); //not possible
        } catch (InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(clazz.getCanonicalName(), e);
        }
    }
}
