/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.collections.impl.list;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.collections.PIterator;
import net.daporkchop.lib.collections.PList;
import net.daporkchop.lib.collections.util.exception.AlreadyRemovedException;
import net.daporkchop.lib.collections.util.exception.IterationCompleteException;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author DaPorkchop_
 */
public class BigLinkedList<V> implements PList<V> {
    protected long size = 0L;
    protected Node<V> root;
    protected Node<V> end;

    @Override
    public void add(long pos, V value) {
        if (pos == this.size) {
            this.add(value);
            return;
        }
        Node<V> node = new Node<>(value);
        if (this.root == null) {
            this.root = this.end = node;
        } else {
            Node<V> next = this.root;
            Node<V> prev = null;
            while (--pos >= 0L && next != null) {
                next = (prev = next).next;
            }
            if (next == null) {
                throw new IndexOutOfBoundsException();
            } else {
                if (next == this.end) {
                    this.end = node;
                }
                if (prev != null) {
                    prev.next = node;
                }
                node.next = next;
            }
        }
        this.size++;
    }

    @Override
    public void set(long pos, V value) {
        Node<V> node = this.root;
        while (--pos >= 0L && node != null) {
            node = node.next;
        }
        if (node == null) {
            throw new IndexOutOfBoundsException();
        } else {
            node.value = value;
        }
    }

    @Override
    public V replace(long pos, V value) {
        Node<V> node = this.root;
        while (--pos >= 0L && node != null) {
            node = node.next;
        }
        if (node == null) {
            throw new IndexOutOfBoundsException();
        } else {
            V old = node.value;
            node.value = value;
            return old;
        }
    }

    @Override
    public V get(long pos) {
        Node<V> node = this.root;
        while (--pos >= 0L && node != null) {
            node = node.next;
        }
        if (node == null) {
            throw new IndexOutOfBoundsException();
        } else {
            return node.value;
        }
    }

    @Override
    public void remove(long pos) {
        Node<V> node = this.root;
        Node<V> prev = null;
        while (--pos >= 0L && node != null) {
            node = (prev = node).next;
        }
        if (node == null) {
            throw new IndexOutOfBoundsException();
        } else if (prev == null || prev == this.root) {
            this.clear();
        } else {
            prev.next = node.next;
            this.size--;
        }
    }

    @Override
    public V getAndRemove(long pos) {
        Node<V> node = this.root;
        Node<V> prev = null;
        while (--pos >= 0L && node != null) {
            node = (prev = node).next;
        }
        if (node == null) {
            throw new IndexOutOfBoundsException();
        } else if (prev == null || prev == this.root) {
            this.clear();
            return node.value;
        } else {
            prev.next = node.next;
            this.size--;
            return node.value;
        }
    }

    @Override
    public long indexOf(V value) {
        Node<V> node = this.root;
        long l = 0L;
        while (node != null) {
            if (value.equals(node.value)) {
                return l;
            } else {
                l++;
            }
            node = node.next;
        }
        return -1L;
    }

    @Override
    public void add(V value) {
        Node<V> node = new Node<>(value);
        if (this.end == null) {
            this.end = this.root = node;
        } else {
            this.end.next = node;
            this.end = node;
        }
        this.size++;
    }

    @Override
    public void remove(V value) {
        Node<V> node = this.root;
        Node<V> prev = null;
        while (node != null) {
            if (value.equals(node.value)) {
                if (prev == null) {
                    this.clear();
                } else {
                    prev.next = node.next;
                    this.size--;
                }
                return;
            }
            node = (prev = node).next;
        }
    }

    @Override
    public boolean checkAndRemove(V value) {
        Node<V> node = this.root;
        Node<V> prev = null;
        while (node != null) {
            if (value.equals(node.value)) {
                if (prev == null) {
                    this.clear();
                } else {
                    prev.next = node.next;
                    this.size--;
                }
                return true;
            }
            node = (prev = node).next;
        }
        return false;
    }

    @Override
    public void forEach(@NonNull Consumer<V> consumer) {
        Node<V> node = this.root;
        while (node != null) {
            consumer.accept(node.value);
            node = node.next;
        }
    }

    @Override
    public PIterator<V> iterator() {
        return new PIterator<V>() {
            protected Node<V> next = BigLinkedList.this.root;
            protected Node<V> curr;
            protected Node<V> prev;
            protected boolean removed = false;

            @Override
            public boolean hasNext() {
                return this.next != null;
            }

            @Override
            public V next() {
                if (!this.hasNext()) {
                    throw new IterationCompleteException();
                } else {
                    this.removed = false;
                    this.prev = this.curr;
                    this.next = (this.curr = this.next).next;
                    return this.curr.value;
                }
            }

            @Override
            public V peek() {
                if (this.curr == null) {
                    throw new IterationCompleteException();
                } else if (this.removed) {
                    throw new AlreadyRemovedException();
                } else {
                    return this.curr.value;
                }
            }

            @Override
            public void remove() {
                if (this.curr == null) {
                    throw new IterationCompleteException();
                } else if (this.removed) {
                    throw new AlreadyRemovedException();
                } else if (this.prev == null) {
                    if (this.next == null) {
                        this.curr = null;
                        BigLinkedList.this.clear();
                    } else {
                        BigLinkedList.this.root = this.next;
                        BigLinkedList.this.size--;
                        this.curr = null;
                        this.removed = true;
                    }
                } else {
                    if (this.next == null) {
                        this.prev.next = null;
                        this.curr = null;
                        BigLinkedList.this.end = this.prev;
                    } else {
                        this.prev.next = this.next;
                        this.curr = this.prev;
                    }
                    this.removed = true;
                    BigLinkedList.this.size--;
                }
            }

            @Override
            public void set(V value) {
                if (this.curr == null) {
                    throw new IterationCompleteException();
                } else if (this.removed) {
                    throw new AlreadyRemovedException();
                } else {
                    this.curr.value = value;
                }
            }

            @Override
            public void recompute(@NonNull Function<V, V> mappingFunction) {
                if (this.curr == null) {
                    throw new IterationCompleteException();
                } else if (this.removed) {
                    throw new AlreadyRemovedException();
                } else {
                    this.curr.value = mappingFunction.apply(this.curr.value);
                }
            }

            @Override
            public boolean setSupported() {
                return true;
            }
        };
    }

    @Override
    public long size() {
        return this.size;
    }

    @Override
    public void clear() {
        this.root = null;
        this.size = 0L;
    }

    @Override
    public boolean isConcurrent() {
        return false;
    }

    @RequiredArgsConstructor
    protected static final class Node<V> {
        @NonNull
        protected V value;
        protected Node<V> next;
    }
}
