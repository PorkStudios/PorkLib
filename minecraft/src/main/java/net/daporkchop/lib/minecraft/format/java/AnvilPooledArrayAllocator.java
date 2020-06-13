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

package net.daporkchop.lib.minecraft.format.java;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.pool.array.AbstractArrayHandle;
import net.daporkchop.lib.common.pool.array.ArrayAllocator;
import net.daporkchop.lib.common.pool.array.ArrayHandle;
import net.daporkchop.lib.common.ref.ThreadRef;

/**
 * Implementation of {@link ArrayAllocator} which allocates 2KiB and 4KiB {@code byte[]}s using a simple soft referencing stack-based pool,
 * and falls back to a given alternate {@link ArrayAllocator} for all other array lengths.
 * <p>
 * This is beneficial for the Anvil save format since nearly all memory allocated while parsing a chunk's NBT will be 2- and 4KiB {@code byte[]}s.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public final class AnvilPooledArrayAllocator implements ArrayAllocator<byte[]> {
    @Getter(AccessLevel.NONE)
    protected final ThreadRef<ThreadLocalData> threadLocal = ThreadRef.late(ThreadLocalData::new);

    @NonNull
    protected final ArrayAllocator<byte[]> fallback;

    protected final int max2kbCount;
    protected final int max4kbCount;

    @Override
    public ArrayHandle<byte[]> atLeast(int length) {
        switch (length) {
            case 2048:
                return this.threadLocal.get().get2kb();
            case 4096:
                return this.threadLocal.get().get4kb();
            default:
                return this.fallback.atLeast(length);
        }
    }

    @Override
    public ArrayHandle<byte[]> exactly(int length) {
        switch (length) {
            case 2048:
                return this.threadLocal.get().get2kb();
            case 4096:
                return this.threadLocal.get().get4kb();
            default:
                return this.fallback.exactly(length);
        }
    }

    /**
     * Does the actual pooling of arrays in a thread-local manner.
     *
     * @author DaPorkchop_
     */
    private final class ThreadLocalData {
        @SuppressWarnings("unchecked")
        protected final byte[][] handles2kb = new byte[AnvilPooledArrayAllocator.this.max2kbCount][];
        @SuppressWarnings("unchecked")
        protected final byte[][] handles4kb = new byte[AnvilPooledArrayAllocator.this.max4kbCount][];

        protected int index2kb = 0;
        protected int index4kb = 0;

        protected synchronized PooledHandle get2kb() {
            byte[] arr = null;
            if (this.index2kb > 0) {
                arr = this.handles2kb[--this.index2kb];
                this.handles2kb[this.index2kb] = null;
            }
            return new PooledHandle(arr != null ? arr : new byte[2048], this);
        }

        protected synchronized void put2kb(@NonNull byte[] arr) {
            if (this.index2kb < this.handles2kb.length) {
                this.handles2kb[this.index2kb++] = arr;
            }
        }

        protected synchronized PooledHandle get4kb() {
            byte[] arr = null;
            if (this.index4kb > 0) {
                arr = this.handles4kb[--this.index4kb];
                this.handles4kb[this.index4kb] = null;
            }
            return new PooledHandle(arr != null ? arr : new byte[4096], this);
        }

        protected synchronized void put4kb(@NonNull byte[] arr) {
            if (this.index4kb < this.handles4kb.length) {
                this.handles4kb[this.index4kb++] = arr;
            }
        }
    }

    /**
     * An {@link ArrayHandle} which contains a pooled {@code byte[]} for an {@link AnvilPooledArrayAllocator}.
     *
     * @author DaPorkchop_
     */
    private static final class PooledHandle extends AbstractArrayHandle<byte[]> {
        protected final ThreadLocalData parent;

        public PooledHandle(@NonNull byte[] value, @NonNull ThreadLocalData parent) {
            super(value, value.length);

            this.parent = parent;
        }

        @Override
        protected void doRelease() {
            switch (this.value.length) {
                case 2048:
                    this.parent.put2kb(this.value);
                    break;
                case 4096:
                    this.parent.put4kb(this.value);
                    break;
                default:
                    throw new IllegalStateException(String.valueOf(this.value.length));
            }
        }
    }
}
