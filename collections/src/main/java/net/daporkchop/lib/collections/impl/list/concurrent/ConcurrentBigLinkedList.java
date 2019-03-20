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

package net.daporkchop.lib.collections.impl.list.concurrent;

import lombok.NonNull;
import net.daporkchop.lib.collections.PIterator;
import net.daporkchop.lib.collections.concurrent.ConcurrentPIterator;
import net.daporkchop.lib.collections.concurrent.ConcurrentPList;
import net.daporkchop.lib.collections.util.exception.AlreadyRemovedException;
import net.daporkchop.lib.common.util.PUnsafe;

import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
public class ConcurrentBigLinkedList<V> implements ConcurrentPList<V> {
    protected static final long SIZE_OFFSET = PUnsafe.pork_getOffset(ConcurrentBigLinkedList.class, "size");
    protected static final long NODE_VALUE_OFFSET = PUnsafe.pork_getOffset(ConcurrentBigLinkedList.Node.class, "value");

    protected volatile long size = 0L;

    @Override
    public void add(long pos, @NonNull V value) {
    }

    @Override
    public void set(long pos, @NonNull V value) {
    }

    @Override
    public V replace(long pos, @NonNull V value) {
        return null;
    }

    @Override
    public V get(long pos) {
        return null;
    }

    @Override
    public void remove(long pos) {
    }

    @Override
    public V getAndRemove(long pos) {
        return null;
    }

    @Override
    public long indexOf(@NonNull V value) {
        return 0;
    }

    @Override
    public void add(@NonNull V value) {
    }

    @Override
    public boolean remove(@NonNull V value) {
        return false;
    }

    @Override
    public void forEach(@NonNull Consumer<V> consumer) {
    }

    @Override
    public PIterator<V> iterator() {
        return null;
    }

    @Override
    public long size() {
        return this.size;
    }

    @Override
    public void clear() {
    }

    @Override
    public boolean isConcurrent() {
        return true;
    }

    protected class Node implements ConcurrentPIterator.Entry<V>  {
        protected volatile V value;

        @Override
        public V get() {
            return this.value;
        }

        @Override
        public void set(@NonNull V value) {
            if (!PUnsafe.pork_checkSwapIfNonNull(this, NODE_VALUE_OFFSET, value))    {
                throw new AlreadyRemovedException();
            }
        }

        @Override
        public V replace(@NonNull V value) {
            if ((value = PUnsafe.pork_swapIfNonNull(this, NODE_VALUE_OFFSET, value)) == null) {
                throw new AlreadyRemovedException();
            } else {
                return value;
            }
        }

        @Override
        public void remove() throws AlreadyRemovedException {
            if (PUnsafe.getAndSetObject(this, NODE_VALUE_OFFSET, null) == null) {
                throw new AlreadyRemovedException();
            } else {
                //TODO
            }
        }
    }
}
