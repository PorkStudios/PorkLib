/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.minecraft.world.format.anvil;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.nbt.alloc.DefaultNBTArrayAllocator;
import net.daporkchop.lib.nbt.alloc.NBTArrayAllocator;
import net.daporkchop.lib.nbt.alloc.NBTArrayHandle;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.lang.ref.SoftReference;

/**
 * Implementation of {@link net.daporkchop.lib.nbt.alloc.NBTArrayAllocator} which allocates 2KiB and 4KiB {@code byte[]}s using a simple soft referencing
 * stack-based pool, and creates new arrays for all other sizes.
 * <p>
 * This is beneficial for the Anvil save format since nearly all memory allocated while parsing a chunk's NBT will be 2- and 4KiB {@code byte[]}s.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
final class AnvilPooledNBTArrayAllocator extends DefaultNBTArrayAllocator {
    protected static final long ALLOCATED_OFFSET = PUnsafe.pork_getOffset(ThreadLocalData.PooledHandle.class, "allocated");

    @Getter(AccessLevel.NONE)
    protected final ThreadLocal<ThreadLocalData> threadLocal = ThreadLocal.withInitial(ThreadLocalData::new);

    protected final int max2kbCount;
    protected final int max4kbCount;

    @Override
    public NBTArrayHandle<byte[]> byteArray(int size) {
        switch (size)   {
            case 2048:
                return this.threadLocal.get().get2kb();
            case 4096:
                return this.threadLocal.get().get4kb();
            default:
                return super.byteArray(size);
        }
    }

    /**
     * Does the actual pooling of arrays in a thread-local manner.
     *
     * @author DaPorkchop_
     */
    private final class ThreadLocalData {
        @SuppressWarnings("unchecked")
        protected final SoftReference<PooledHandle>[] handles2kb = (SoftReference<PooledHandle>[]) new SoftReference[AnvilPooledNBTArrayAllocator.this.max2kbCount];
        @SuppressWarnings("unchecked")
        protected final SoftReference<PooledHandle>[] handles4kb = (SoftReference<PooledHandle>[]) new SoftReference[AnvilPooledNBTArrayAllocator.this.max4kbCount];

        protected int index2kb = 0;
        protected int index4kb = 0;

        protected synchronized PooledHandle get2kb() {
            while (this.index2kb > 0)   {
                PooledHandle handle = this.handles2kb[--this.index2kb].get();
                this.handles2kb[this.index2kb] = null;
                if (handle != null) {
                    return handle;
                }
            }
            System.out.println("Allocating 2KiB buffer");
            return new PooledHandle(new byte[2048]);
        }

        protected synchronized void put2kb(@NonNull PooledHandle handle)  {
            if (this.index2kb < this.handles2kb.length) {
                this.handles2kb[this.index2kb++] = new SoftReference<>(handle);
            }
        }

        protected synchronized PooledHandle get4kb() {
            while (this.index4kb > 0)   {
                PooledHandle handle = this.handles4kb[--this.index4kb].get();
                this.handles4kb[this.index4kb] = null;
                if (handle != null) {
                    return handle;
                }
            }
            System.out.println("Allocating 4KiB buffer");
            return new PooledHandle(new byte[4096]);
        }

        protected synchronized void put4kb(@NonNull PooledHandle handle)  {
            if (this.index4kb < this.handles4kb.length) {
                this.handles4kb[this.index4kb++] = new SoftReference<>(handle);
            }
        }

        /**
         * A {@link NBTArrayHandle} which contains a pooled {@code byte[]} for an {@link AnvilPooledNBTArrayAllocator}.
         *
         * @author DaPorkchop_
         */
        @RequiredArgsConstructor
        @Accessors(fluent = true)
        private final class PooledHandle implements NBTArrayHandle<byte[]> {
            @Getter
            protected final byte[] value;

            protected volatile int allocated = 1;

            @Override
            public void release() {
                if (PUnsafe.compareAndSwapInt(this, ALLOCATED_OFFSET, 1, 0))    {
                    if (this.value.length == 2048)  {
                        ThreadLocalData.this.put2kb(this);
                    } else if (this.value.length == 4096)   {
                        ThreadLocalData.this.put4kb(this);
                    } else {
                        throw new IllegalStateException(String.valueOf(this.value.length));
                    }
                }
            }

            @Override
            public NBTArrayAllocator alloc() {
                return AnvilPooledNBTArrayAllocator.this;
            }

            protected PooledHandle allocate()   {
                if (!PUnsafe.compareAndSwapInt(this, ALLOCATED_OFFSET, 0, 1))   {
                    throw new IllegalStateException("Handle already allocated!");
                }
                return this;
            }
        }
    }
}
