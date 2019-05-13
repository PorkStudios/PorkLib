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

package net.daporkchop.lib.collections.impl.ordered;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.collections.PCollection;
import net.daporkchop.lib.collections.POrderedCollection;
import net.daporkchop.lib.collections.util.exception.AlreadyRemovedException;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
public class BigLinkedCollection<V> implements POrderedCollection<V> {
    protected long size = 0L;
    protected Node root = null;

    public BigLinkedCollection() {
    }

    public BigLinkedCollection(@NonNull Collection<V> collection) {
        collection.forEach(this::add);
    }

    public BigLinkedCollection(@NonNull PCollection<V> collection) {
        collection.forEach(this::add);
    }

    @Override
    public void add(@NonNull V value) {
        Node node = new Node(value);
        if (this.root != null) {
            node.prev = this.root;
            this.root.next = node;
        }
        this.root = node;
        this.size++;
    }

    @Override
    public boolean contains(@NonNull V value) {
        Node node = this.root;
        while (node != null) {
            if (value.equals(node.value)) {
                return true;
            }
            node = node.prev;
        }
        return false;
    }

    @Override
    public void remove(@NonNull V value) {
        Node node = this.root;
        while (node != null) {
            if (value.equals(node.value)) {
                node.remove();
                return;
            }
            node = node.prev;
        }
    }

    @Override
    public boolean checkAndRemove(@NonNull V value) {
        Node node = this.root;
        while (node != null) {
            if (value.equals(node.value)) {
                node.remove();
                return true;
            }
            node = node.prev;
        }
        return false;
    }

    @Override
    public void forEach(@NonNull Consumer<V> consumer) {
        Node node = this.root;
        while (node != null) {
            consumer.accept(node.value);
            node = node.prev;
        }
    }

    @Override
    public boolean replace(@NonNull V oldValue, @NonNull V newValue) {
        Node node = this.root;
        while (node != null) {
            if (oldValue.equals(node.value)) {
                node.value = newValue;
                return true;
            }
            node = node.prev;
        }
        return false;
    }

    @Override
    public OrderedIterator<V> orderedIterator() {
        return new OrderedIterator<V>() {
            protected Node node = BigLinkedCollection.this.root;

            @Override
            public Entry<V> next() {
                if (this.node == null) {
                    return null;
                } else {
                    Node node = this.node;
                    this.node = node.prev;
                    return node;
                }
            }
        };
    }

    @Override
    public long size() {
        return this.size;
    }

    @Override
    public void clear() {
        this.size = 0L;
        this.root = null;
    }

    @Override
    public boolean isConcurrent() {
        return false;
    }

    @RequiredArgsConstructor
    @Getter
    protected class Node implements Entry<V> {
        @NonNull
        protected V value;

        protected Node prev;
        protected Node next;

        public boolean isHead() {
            return this.next == null;
        }

        public boolean isTail() {
            return this.prev == null;
        }

        @Override
        public V get() {
            return this.value;
        }

        @Override
        public void set(@NonNull V value) throws AlreadyRemovedException {
            if (this.value == null) {
                throw new AlreadyRemovedException();
            } else {
                this.value = value;
            }
        }

        @Override
        public boolean trySet(@NonNull V value) {
            if (this.value == null) {
                return false;
            } else {
                this.value = value;
                return true;
            }
        }

        @Override
        public V replace(@NonNull V value) throws AlreadyRemovedException {
            if (this.value == null) {
                throw new AlreadyRemovedException();
            } else {
                V oldValue = this.value;
                this.value = value;
                return oldValue;
            }
        }

        @Override
        public void remove() throws AlreadyRemovedException {
            if (this.value == null) {
                throw new AlreadyRemovedException();
            } else {
                this.doRemove();
            }
        }

        @Override
        public boolean tryRemove() {
            if (this.value == null) {
                return false;
            } else {
                this.doRemove();
                return true;
            }
        }

        protected void doRemove() {
            this.value = null;
            if (!this.isHead()) {
                this.next.prev = this.prev;
            }
            if (!this.isTail()) {
                this.prev.next = this.next;
            }
            BigLinkedCollection.this.size--;
        }
    }
}
