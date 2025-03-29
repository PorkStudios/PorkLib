/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2025 DaPorkchop_
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

package net.daporkchop.lib.common.misc.classvalue;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.misc.mutable.MutableReference;
import net.daporkchop.lib.common.reference.HandleableReference;
import net.daporkchop.lib.common.reference.PReferenceHandler;
import net.daporkchop.lib.common.reference.Reference;
import net.daporkchop.lib.common.reference.ReferenceStrength;
import net.daporkchop.lib.common.util.PorkUtil;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SimpleCollectablePClassValue<T> extends ClassValue<Object> implements PClassValue<T> {
    //TODO: currently, this neither sets collected Reference objects to null, nor is it able to completely remove expired
    //      entries from the underlying ClassValue

    private final @NonNull Function<? super Class<?>, ? extends T> factory;
    private final @NonNull ReferenceStrength strength;

    @Override
    public T get(Class<?> type) {
        AtomicReference<Reference<T>> indirectReference = PorkUtil.uncheckedCast(super.get(type));
        Reference<T> reference = indirectReference.get();
        if (reference != null) {
            T value = reference.get();
            if (value != null) {
                return value;
            }
        }

        return this.getSlowPath(type, indirectReference);
    }

    private T getSlowPath(Class<?> type, AtomicReference<Reference<T>> indirectReference) {
        T nextValue = Objects.requireNonNull(this.factory.apply(type));
        Reference<T> nextReference = this.strength.createReference(nextValue);

        while (true) {
            Reference<T> prevReference = indirectReference.get();
            if (prevReference != null) {
                T value = prevReference.get();
                if (value != null) {
                    return value;
                }
            }

            if (indirectReference.compareAndSet(prevReference, nextReference)) {
                return nextValue;
            }
        }
    }

    @Override
    protected Object computeValue(Class<?> type) {
        return new AtomicReference<Reference<T>>(null);
    }
}
