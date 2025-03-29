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

package net.daporkchop.lib.common.reference;

import lombok.NonNull;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.function.Consumer;

/**
 * The different types of garbage-collectable reference strengths.
 *
 * @author DaPorkchop_
 */
public enum ReferenceStrength {
    SOFT,
    WEAK,
    ;

    /**
     * Creates a reference to the given value (the referent).
     *
     * @param referent the value
     * @return the reference
     */
    public <T> Reference<T> createReference(@NonNull T referent) {
        return this == SOFT
                ? new SoftReference<>(referent)
                : new WeakReference<>(referent);
    }

    /**
     * Creates a reference to the given value (the referent).
     *
     * @param referent the referent
     * @param handler  a {@link Consumer} which will be called at some point after the referent is garbage collected, assuming the {@link Reference} is not garbage
     *                 collected first. See {@link HandleableReference} for more details.
     * @return the reference
     */
    public <T> Reference<T> createReference(@NonNull T referent, @NonNull ReferenceHandler<T> handler) {
        return this == SOFT
                ? new SoftReferenceWithHandler<>(referent, handler)
                : new WeakReferenceWithHandler<>(referent, handler);
    }

    /**
     * Creates a reference to the given value (the referent).
     *
     * @param referent the referent
     * @param handler  a {@link Consumer} which will be called at some point after the referent is garbage collected, assuming the {@link Reference} is not garbage
     *                 collected first. See {@link HandleableReference} for more details.
     * @param param    an
     * @return the reference
     */
    public <T, P> Reference<T> createReference(@NonNull T referent, @NonNull ParameterizedReferenceHandler<T, ? super P> handler, P param) {
        return this == SOFT
                ? new SoftReferenceWithParameterizedHandler<>(referent, handler, param)
                : new WeakReferenceWithParameterizedHandler<>(referent, handler, param);
    }

    /**
     * @author DaPorkchop_
     */
    private static final class SoftReferenceWithHandler<T> extends SoftReference<T> implements HandleableReference {
        private final ReferenceHandler<T> handler;

        public SoftReferenceWithHandler(T referent, ReferenceHandler<T> handler) {
            super(referent, PReferenceQueues.getHandlingReferenceQueue());
            this.handler = handler;
        }

        @Override
        public void handle() {
            this.handler.handleReference(this);
        }
    }

    /**
     * @author DaPorkchop_
     */
    private static final class WeakReferenceWithHandler<T> extends WeakReference<T> implements HandleableReference {
        private final ReferenceHandler<T> handler;

        public WeakReferenceWithHandler(T referent, ReferenceHandler<T> handler) {
            super(referent, PReferenceQueues.getHandlingReferenceQueue());
            this.handler = handler;
        }

        @Override
        public void handle() {
            this.handler.handleReference(this);
        }
    }

    /**
     * @author DaPorkchop_
     */
    private static final class SoftReferenceWithParameterizedHandler<T, P> extends SoftReference<T> implements HandleableReference {
        private final ParameterizedReferenceHandler<T, ? super P> handler;
        private final P param;

        public SoftReferenceWithParameterizedHandler(T referent, ParameterizedReferenceHandler<T, ? super P> handler, P param) {
            super(referent, PReferenceQueues.getHandlingReferenceQueue());
            this.handler = handler;
            this.param = param;
        }

        @Override
        public void handle() {
            this.handler.handleReference(this, this.param);
        }
    }

    /**
     * @author DaPorkchop_
     */
    private static final class WeakReferenceWithParameterizedHandler<T, P> extends WeakReference<T> implements HandleableReference {
        private final ParameterizedReferenceHandler<T, ? super P> handler;
        private final P param;

        public WeakReferenceWithParameterizedHandler(T referent, ParameterizedReferenceHandler<T, ? super P> handler, P param) {
            super(referent, PReferenceQueues.getHandlingReferenceQueue());
            this.handler = handler;
            this.param = param;
        }

        @Override
        public void handle() {
            this.handler.handleReference(this, this.param);
        }
    }
}
