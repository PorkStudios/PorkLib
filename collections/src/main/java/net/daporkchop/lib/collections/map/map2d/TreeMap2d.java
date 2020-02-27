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

package net.daporkchop.lib.collections.map.map2d;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.primitive.lambda.consumer.IntIntConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.IntIntObjConsumer;
import net.daporkchop.lib.primitive.lambda.function.IntIntObjFunction;

import java.util.function.Consumer;

import static net.daporkchop.lib.math.primitive.BinMath.*;

/**
 * A simple tree-based implementation of {@link Map2d}.
 * <p>
 * Positions are encoded into a single {@code long} using {@link net.daporkchop.lib.math.primitive.BinMath#packXY(int, int)},
 * and all other comparisons are done on the {@code long} value.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public class TreeMap2d<V> implements Map2d<V> {
    protected Node<V> root;

    @Getter
    protected int size;

    @Override
    public V put(int x, int y, @NonNull V value) {
        long pos = packXY(x, y);
        if (this.root == null) {
            (this.root = this.createNode(pos)).value = value;
            this.size = 1;
            return null;
        }
        Node<V> node = this.root.find0(this, pos, true);
        V old = node.value;
        if (old == null) {
            //node was newly created
            this.size++;
        }
        node.value = value;
        return old;
    }

    @Override
    public V putIfAbsent(int x, int y, @NonNull V value) {
        long pos = packXY(x, y);
        if (this.root == null) {
            (this.root = this.createNode(pos)).value = value;
            this.size = 1;
            return null;
        }
        Node<V> node = this.root.find0(this, pos, true);
        V old = node.value;
        if (old == null) {
            //node was newly created
            node.value = value;
            this.size++;
        }
        return old;
    }

    @Override
    public V get(int x, int y) {
        Node<V> node = this.root != null ? this.root.find0(this, packXY(x, y), false) : null;
        return node != null ? node.value : null;
    }

    @Override
    public V computeIfAbsent(int x, int y, @NonNull IntIntObjFunction<V> mappingFunction) {
        return null; //TODO
    }

    @Override
    public V remove(int x, int y) {
        return null; //TODO
    }

    @Override
    public boolean contains(int x, int y) {
        return this.root != null && this.root.find0(this, packXY(x, y), false) != null;
    }

    @Override
    public void forEach(@NonNull IntIntObjConsumer<V> consumer) {
        if (this.root != null) {
            this.root.forEach0(consumer);
        }
    }

    @Override
    public void forEachKey(@NonNull IntIntConsumer consumer) {
        if (this.root != null) {
            this.root.forEach0(consumer);
        }
    }

    @Override
    public void forEachValue(@NonNull Consumer<V> consumer) {
        if (this.root != null) {
            this.root.forEach0(consumer);
        }
    }

    @Override
    public void clear() {
        this.root = null;
        this.size = 0;
    }

    protected Node<V> createNode(long pos) {
        return new Node<>(pos);
    }

    @RequiredArgsConstructor
    protected static class Node<V> {
        protected final long    pos;
        protected       Node<V> low;
        protected       Node<V> high;

        protected V value;

        protected Node<V> find0(TreeMap2d<V> map, long pos, boolean create) {
            if (pos == this.pos) {
                return this;
            } else if (pos < this.pos) {
                if (this.low != null) {
                    return this.low.find0(map, pos, create);
                } else if (create) {
                    return this.low = map.createNode(pos);
                }
            } else if (pos > this.pos) {
                if (this.high != null) {
                    return this.high.find0(map, pos, create);
                } else if (create) {
                    return this.high = map.createNode(pos);
                }
            }
            return null;
        }

        protected void forEach0(IntIntObjConsumer<V> consumer) {
            if (this.low != null) {
                this.low.forEach0(consumer);
            }
            consumer.accept(unpackX(this.pos), unpackY(this.pos), this.value);
            if (this.high != null) {
                this.high.forEach0(consumer);
            }
        }

        protected void forEach0(IntIntConsumer consumer) {
            if (this.low != null) {
                this.low.forEach0(consumer);
            }
            consumer.accept(unpackX(this.pos), unpackY(this.pos));
            if (this.high != null) {
                this.high.forEach0(consumer);
            }
        }

        protected void forEach0(Consumer<V> consumer) {
            if (this.low != null) {
                this.low.forEach0(consumer);
            }
            consumer.accept(this.value);
            if (this.high != null) {
                this.high.forEach0(consumer);
            }
        }
    }
}
