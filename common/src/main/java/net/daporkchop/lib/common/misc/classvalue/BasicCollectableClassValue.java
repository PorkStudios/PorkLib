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
import net.daporkchop.lib.common.reference.ParameterizedReferenceHandler;
import net.daporkchop.lib.common.reference.ReferenceStrength;
import net.daporkchop.lib.common.util.PorkUtil;

import java.lang.ref.Reference;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class BasicCollectableClassValue<T> extends ClassValue<Object> implements PClassValue<T>, ParameterizedReferenceHandler<T, AtomicReference<Reference<T>>> {
    //TODO: currently, this isn't able to completely remove collected entries from the underlying ClassValue

    private final @NonNull Function<? super Class<?>, ? extends T> factory;
    private final @NonNull ReferenceStrength strength;

    @Override
    public T get(Class<?> type) {
        AtomicReference<Reference<T>> indirectReference = PorkUtil.uncheckedCast(super.get(type));
        Reference<T> reference = indirectReference.get();
        T value;
        if (reference != null && (value = reference.get()) != null) {
            return value;
        }

        return this.compute(type, indirectReference);
    }

    private T compute(Class<?> type, AtomicReference<Reference<T>> indirectReference) {
        T nextValue = Objects.requireNonNull(this.factory.apply(type));
        Reference<T> nextReference = this.strength.createReference(nextValue, this, indirectReference);

        while (true) {
            Reference<T> prevReference = indirectReference.get();
            T value;
            if (prevReference != null && (value = prevReference.get()) != null) {
                return value;
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

    @Override
    public void handleReference(Reference<T> reference, AtomicReference<Reference<T>> param) {
        //null out the reference to allow it to be garbage-collected as well
        param.compareAndSet(reference, null);
    }
}
