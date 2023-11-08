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

package net.daporkchop.lib.common.reference.cache;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.reference.Reference;
import net.daporkchop.lib.common.reference.ReferenceStrength;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Accessors(fluent = true)
class GlobalReferencedCached<T> implements Cached<T>, Consumer<Reference<T>> {
    @Getter
    @NonNull
    protected final Supplier<T> factory;
    @NonNull
    protected final ReferenceStrength strength;

    protected Reference<T> ref;

    @Override
    public T get() {
        Reference<T> ref = this.ref;
        T value;
        if (ref == null || (value = ref.get()) == null) { //reference is unset or has been garbage-collected, (re-)compute it
            value = this.compute();
        }

        return value;
    }

    protected synchronized T compute() {
        Reference<T> ref = this.ref;
        T value;
        if (ref == null || (value = ref.get()) == null) {
            //compute value
            value = Objects.requireNonNull(this.factory.get());

            //create reference
            this.ref = this.strength.createReference(value, this);
        }

        return value;
    }

    /**
     * @deprecated internal API, do not touch!
     */
    @Override
    @Deprecated
    public void accept(@NonNull Reference<T> ref) { //fired when the referent is garbage-collected
        if (this.ref == ref) {
            synchronized (this) {
                if (this.ref == ref) {
                    //null out the reference to allow it to be garbage-collected as well
                    this.ref = null;
                }
            }
        }
    }
}
