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

package net.daporkchop.lib.common.reference;

import lombok.NonNull;

import java.lang.ref.ReferenceQueue;
import java.util.function.Consumer;

/**
 * Various types of reference.
 *
 * @author DaPorkchop_
 */
public enum ReferenceStrength {
    STRONG {
        @Override
        public <T> Reference<T> createReference(@NonNull T referent) {
            return new StrongReference<>(referent);
        }

        @Override
        public <T> Reference<T> createReference(@NonNull T referent, @NonNull Consumer<? super Reference<T>> handler) {
            //the handler would never be able to be called, since the referent could never be collected before the reference. therefore we can just
            //  silently discard the handler
            return new StrongReference<>(referent);
        }
    },
    SOFT {
        @Override
        public <T> Reference<T> createReference(@NonNull T referent) {
            return new SoftReference<>(referent);
        }

        @Override
        public <T> Reference<T> createReference(@NonNull T referent, @NonNull Consumer<? super Reference<T>> handler) {
            return PReferenceHandler.createReference(referent, (referentIn, queue) -> new SoftReferenceWithConsumerHandler<>(referentIn, queue, handler));
        }
    },
    WEAK {
        @Override
        public <T> Reference<T> createReference(@NonNull T referent) {
            return new WeakReference<>(referent);
        }

        @Override
        public <T> Reference<T> createReference(@NonNull T referent, @NonNull Consumer<? super Reference<T>> handler) {
            return PReferenceHandler.createReference(referent, (referentIn, queue) -> new WeakReferenceWithConsumerHandler<>(referentIn, queue, handler));
        }
    };

    /**
     * Creates a reference to the given value (the referent).
     *
     * @param referent the value
     * @return the reference
     */
    public abstract <T> Reference<T> createReference(@NonNull T referent);

    /**
     * Creates a reference to the given value (the referent).
     *
     * @param referent the referent
     * @param handler  a {@link Consumer} which will be called at some point after the referent is garbage collected, assuming the {@link Ref} is not garbage
     *                 collected first. See {@link HandleableReference} for more details.
     * @return the reference
     */
    public abstract <T> Reference<T> createReference(@NonNull T referent, @NonNull Consumer<? super Reference<T>> handler);

    /**
     * @author DaPorkchop_
     */
    private static class SoftReferenceWithConsumerHandler<T> extends SoftReference<T> implements HandleableReference {
        private final Consumer<? super Reference<T>> handler;

        public SoftReferenceWithConsumerHandler(@NonNull T referent, @NonNull ReferenceQueue<? super T> queue, @NonNull Consumer<? super Reference<T>> handler) {
            super(referent, queue);

            this.handler = handler;
        }

        @Override
        public void handle() {
            this.handler.accept(this);
        }
    }

    /**
     * @author DaPorkchop_
     */
    private static class WeakReferenceWithConsumerHandler<T> extends WeakReference<T> implements HandleableReference {
        private final Consumer<? super Reference<T>> handler;

        public WeakReferenceWithConsumerHandler(@NonNull T referent, @NonNull ReferenceQueue<? super T> queue, @NonNull Consumer<? super Reference<T>> handler) {
            super(referent, queue);

            this.handler = handler;
        }

        @Override
        public void handle() {
            this.handler.accept(this);
        }
    }
}
