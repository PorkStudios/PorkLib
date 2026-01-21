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

package net.daporkchop.lib.common.reference.cache;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.reference.ReferenceHandler;
import net.daporkchop.lib.common.reference.ReferenceStrength;

import java.lang.ref.Reference;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Supplier;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Accessors(fluent = true)
final class GlobalCollectableCached<T> implements Cached<T>, ReferenceHandler<T> {
    private static final AtomicReferenceFieldUpdater<GlobalCollectableCached, Reference> REFERENCE_UPDATER = AtomicReferenceFieldUpdater.newUpdater(GlobalCollectableCached.class, Reference.class, "reference");

    @Getter
    private final @NonNull Supplier<T> factory;
    private final @NonNull ReferenceStrength strength;

    //TODO: if we had VarHandle, both reads from this could use Acquire semantics: e.g. https://en.wikipedia.org/wiki/Double-checked_locking#Usage_in_Java
    private volatile Reference<T> reference;

    @Override
    public T get() {
        Reference<T> ref = this.reference;
        T value;
        if (ref != null && (value = ref.get()) != null) {
            return value;
        }

        return this.compute();
    }

    private synchronized T compute() {
        Reference<T> ref = this.reference;
        T value;
        if (ref != null && (value = ref.get()) != null) {
            return value;
        }

        //reference is unset or has been garbage-collected, (re-)compute it
        value = Objects.requireNonNull(this.factory.get());
        this.reference = this.strength.createReference(value, this);
        return value;
    }

    @Override
    public void handleReference(Reference<T> reference) {
        //null out the reference to allow it to be garbage-collected as well
        REFERENCE_UPDATER.compareAndSet(this, reference, null);
    }
}
