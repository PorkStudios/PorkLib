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

package net.daporkchop.lib.collections.impl.ordered.concurrent;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.collections.PCollection;
import net.daporkchop.lib.collections.POrderedCollection;
import net.daporkchop.lib.collections.stream.PStream;
import net.daporkchop.lib.collections.util.exception.AlreadyRemovedException;
import net.daporkchop.lib.common.util.PUnsafe;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
public class ConcurrentBigLinkedCollection<V> implements POrderedCollection<V> {
    protected static final long BASE_OFFSET = PUnsafe.pork_getOffset(ConcurrentBigLinkedCollection.class, "base");
    protected static final long SIZE_OFFSET = PUnsafe.pork_getOffset(ConcurrentBigLinkedCollection.ListBase.class, "size");
    protected static final long ROOT_OFFSET = PUnsafe.pork_getOffset(ConcurrentBigLinkedCollection.ListBase.class, "root");
    protected static final long NODE_VALUE_OFFSET = PUnsafe.pork_getOffset(ConcurrentBigLinkedCollection.Node.class, "value");
    protected static final long NODE_PREV_OFFSET = PUnsafe.pork_getOffset(ConcurrentBigLinkedCollection.Node.class, "prev");
    protected static final long NODE_PRESENT_OFFSET = PUnsafe.pork_getOffset(ConcurrentBigLinkedCollection.Node.class, "present");
    protected static final long ITERATOR_NODE_OFFSET = PUnsafe.pork_getOffset(ConcurrentBigLinkedCollection.ConcurrentIterator.class, "node");

    protected volatile ListBase base = new ListBase();

    public ConcurrentBigLinkedCollection()    {
    }

    public ConcurrentBigLinkedCollection(@NonNull Collection<V> collection)    {
        collection.forEach(this::add);
    }

    public ConcurrentBigLinkedCollection(@NonNull PCollection<V> collection)    {
        collection.forEach(this::add);
    }

    @Override
    public void add(@NonNull V value) {
        ListBase base = this.base;
        Node node = new Node(base, value);
        Node root;
        do {
            if (base.closed) {
                node = new Node(base = this.base, value);
            }
            root = node.prev = base.getRoot();
        } while (!PUnsafe.compareAndSwapObject(base, ROOT_OFFSET, root, node) && !base.closed);
        PUnsafe.getAndAddLong(base, SIZE_OFFSET, 1L);
    }

    @Override
    public boolean remove(@NonNull V value) {
        ListBase base = this.base;
        Node node = base.getRoot();
        while (!base.closed && node != null && node.present) {
            if (value.equals(node.value)) {
                return node.tryRemove();
            }
            node = node.getPrev();
        }
        return false;
    }

    @Override
    public boolean contains(@NonNull V value) {
        ListBase base = this.base;
        Node node = base.getRoot();
        while (!base.closed && node != null && node.present) {
            if (value.equals(node.value)) {
                return true;
            }
            node = node.getPrev();
        }
        return false;
    }

    @Override
    public void forEach(@NonNull Consumer<V> consumer) {
        ListBase base = this.base;
        Node node = base.getRoot();
        V value;
        while (!base.closed && node != null && (value = node.value) != null) {
            consumer.accept(value);
            node = node.getPrev();
        }
    }

    @Override
    public boolean replace(@NonNull V oldValue, @NonNull V newValue) {
        ListBase base = this.base;
        Node node = base.getRoot();
        while (!base.closed && node != null && node.present) {
            if (oldValue.equals(node.value)) {
                return node.trySet(newValue);
            }
            node = node.getPrev();
        }
        return false;
    }

    @Override
    public OrderedIterator<V> orderedIterator() {
        return new ConcurrentIterator();
    }

    @Override
    public PStream<V> stream() {
        return null;
    }

    @Override
    public long size() {
        return this.base.size;
    }

    @Override
    public void clear() {
        PUnsafe.<ListBase>pork_swapObject(this, BASE_OFFSET, new ListBase()).closed = true;
    }

    @Override
    public boolean isConcurrent() {
        return true;
    }

    @Override
    public String toString() {
        return String.format("ConcurrentBigLinkedCollection(size=%d)", this.base.size);
    }

    protected class ListBase {
        protected volatile long size = 0L;
        protected volatile Node root = null;
        protected volatile boolean closed = false;

        protected Node getRoot() {
            Node root = this.root;
            while (root != null && !root.present) {
                if (!PUnsafe.compareAndSwapObject(this, ROOT_OFFSET, root, root = root.prev)) {
                    root = this.root;
                }
            }
            return root;
        }

        @Override
        public String toString() {
            return String.format("ConcurrentBigLinkedCollection.ListBase(size=%d,closed=%b)", this.size, this.closed);
        }
    }

    @RequiredArgsConstructor
    protected class Node implements Entry<V> {
        @NonNull
        protected final ListBase base;
        @NonNull
        protected volatile V value;
        protected volatile Node prev = null;
        protected volatile boolean present = true;

        protected Node getPrev() {
            Node prev = this.prev;
            while (prev != null && !prev.present) {
                if (!PUnsafe.compareAndSwapObject(this, NODE_PREV_OFFSET, prev, prev = prev.prev)) {
                    prev = this.prev;
                }
            }
            return prev;
        }

        @Override
        public V get() {
            return this.value;
        }

        @Override
        public void set(@NonNull V value) {
            if (!this.present || !PUnsafe.pork_checkSwapIfNonNull(this, NODE_VALUE_OFFSET, value)) {
                throw new AlreadyRemovedException();
            }
        }

        @Override
        public boolean trySet(V value) {
            if (!this.present || !PUnsafe.pork_checkSwapIfNonNull(this, NODE_VALUE_OFFSET, value)) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public V replace(@NonNull V value) {
            if (!this.present || (value = PUnsafe.pork_swapIfNonNull(this, NODE_VALUE_OFFSET, value)) == null) {
                throw new AlreadyRemovedException();
            } else {
                return value;
            }
        }

        @Override
        public void remove() throws AlreadyRemovedException {
            if (!this.present || PUnsafe.getAndSetObject(this, NODE_VALUE_OFFSET, null) == null) {
                throw new AlreadyRemovedException();
            } else {
                this.doRemove();
            }
        }

        @Override
        public boolean tryRemove() {
            if (this.present || PUnsafe.getAndSetObject(this, NODE_VALUE_OFFSET, null) != null) {
                this.doRemove();
                return true;
            } else {
                return false;
            }
        }

        protected void doRemove() {
            this.present = false;
            PUnsafe.getAndAddLong(this.base, SIZE_OFFSET, -1L);
        }
    }

    protected class ConcurrentIterator implements OrderedIterator<V> {
        protected final ListBase base = ConcurrentBigLinkedCollection.this.base;
        protected volatile Node node = this.base.getRoot();

        @Override
        public Entry<V> next() {
            Node curr;
            do {
                curr = this.node;
            } while (!this.base.closed && curr != null && !PUnsafe.compareAndSwapObject(this, ITERATOR_NODE_OFFSET, curr, curr.getPrev()));
            return this.base.closed ? null : curr;
        }
    }
}
