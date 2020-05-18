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

package net.daporkchop.lib.common.ref;

import net.daporkchop.lib.common.ref.attachment.AttachedRef;
import net.daporkchop.lib.common.ref.attachment.SoftAttachedRef;
import net.daporkchop.lib.common.ref.attachment.StrongAttachedRef;
import net.daporkchop.lib.common.ref.attachment.WeakAttachedRef;
import net.daporkchop.lib.common.ref.impl.SoftRef;
import net.daporkchop.lib.common.ref.impl.StrongRef;
import net.daporkchop.lib.common.ref.impl.WeakRef;

import java.lang.ref.ReferenceQueue;

/**
 * Various types of reference.
 *
 * @author DaPorkchop_
 */
public enum ReferenceType {
    STRONG {
        @Override
        public <V> Ref<V> create(V value, ReferenceQueue<? super V> queue) {
            if (queue != null)  {
                throw new UnsupportedOperationException("Strong references cannot use a ReferenceQueue!");
            }
            return new StrongRef<>(value);
        }

        @Override
        public <V, A> AttachedRef<V, A> createAttached(V value, A attachment, ReferenceQueue<? super V> queue) {
            if (queue != null)  {
                throw new UnsupportedOperationException("Strong references cannot use a ReferenceQueue!");
            }
            return new StrongAttachedRef<>(value, attachment);
        }
    },
    SOFT {
        @Override
        public <V> Ref<V> create(V value, ReferenceQueue<? super V> queue) {
            return new SoftRef<>(value, queue);
        }

        @Override
        public <V, A> AttachedRef<V, A> createAttached(V value, A attachment, ReferenceQueue<? super V> queue) {
            return new SoftAttachedRef<>(value, attachment, queue);
        }
    },
    WEAK {
        @Override
        public <V> Ref<V> create(V value, ReferenceQueue<? super V> queue) {
            return new WeakRef<>(value, queue);
        }

        @Override
        public <V, A> AttachedRef<V, A> createAttached(V value, A attachment, ReferenceQueue<? super V> queue) {
            return new WeakAttachedRef<>(value, attachment, queue);
        }
    };

    public <V> Ref<V> create(V value) {
        return this.create(value, null);
    }

    public abstract <V> Ref<V> create(V value, ReferenceQueue<? super V> queue);

    public <V, A> AttachedRef<V, A> createAttached(V value) {
        return this.createAttached(value, null, null);
    }

    public <V, A> AttachedRef<V, A> createAttached(V value, A attachment) {
        return this.createAttached(value, attachment, null);
    }

    public <V, A> AttachedRef<V, A> createAttached(V value, ReferenceQueue<? super V> queue) {
        return this.createAttached(value, null, queue);
    }

    public abstract <V, A> AttachedRef<V, A> createAttached(V value, A attachment, ReferenceQueue<? super V> queue);
}
