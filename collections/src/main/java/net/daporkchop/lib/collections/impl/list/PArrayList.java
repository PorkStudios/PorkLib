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
import net.daporkchop.lib.collections.PIterator;
import net.daporkchop.lib.collections.PList;
import net.daporkchop.lib.collections.PStack;
import net.daporkchop.lib.collections.util.exception.IterationCompleteException;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A simple array-based implementation of {@link PList}.
 * <p>
 * Only allows storing up to {@link Integer#MAX_VALUE} values.
 *
 * @author DaPorkchop_
 */
public class PArrayList<V> implements PList<V>, PStack<V> {
    protected Object[] data;
    protected int size;

    public PArrayList() {
        this(16);
    }

    public PArrayList(int initialCapacity) {
        this.data = new Object[initialCapacity];
    }

    @Override
    public void add(long pos, V value) {
        if (pos < 0L || pos > this.size) {
            throw new IndexOutOfBoundsException(String.valueOf(pos));
        } else if ((int) pos >= this.size) {
            //grow array
            final Object[] oldArray = this.data;
            final Object[] newArray = new Object[oldArray.length << 1];
            if (pos == 0L) {
                System.arraycopy(oldArray, 0, newArray, 1, this.size);
            } else if (pos == this.size) {
                System.arraycopy(oldArray, 0, newArray, 0, this.size);
            } else {
                System.arraycopy(oldArray, 0, newArray, 0, (int) pos);
                System.arraycopy(oldArray, (int) pos, newArray, (int) pos + 1, this.size - (int) pos);
            }
            this.data = newArray;
        } else if (pos != this.size) {
            //move data forwards
            System.arraycopy(this.data, (int) pos, this.data, (int) pos + 1, this.size - (int) pos);
        }
        this.data[(int) pos] = value;
        this.size++;
    }

    @Override
    public void set(long pos, V value) {
        if (pos < 0L || pos >= this.size) {
            throw new IndexOutOfBoundsException(String.valueOf(pos));
        } else {
            this.data[(int) pos] = value;
        }
    }

    @Override
    public V replace(long pos, V value) {
        if (pos < 0L || pos >= this.size) {
            throw new IndexOutOfBoundsException(String.valueOf(pos));
        } else {
            @SuppressWarnings("unchecked")
            V old = (V) this.data[(int) pos];
            this.data[(int) pos] = value;
            return old;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(long pos) {
        if (pos < 0L || pos >= this.size) {
            throw new IndexOutOfBoundsException(String.valueOf(pos));
        } else {
            return (V) this.data[(int) pos];
        }
    }

    @Override
    public void remove(long pos) {
        this.getAndRemove(pos);
    }

    @Override
    public V getAndRemove(long pos) {
        if (pos < 0L || pos >= this.size) {
            throw new IndexOutOfBoundsException(String.valueOf(pos));
        } else {
            @SuppressWarnings("unchecked")
            V value = (V) this.data[(int) pos];
            System.arraycopy(this.data, (int) pos + 1, this.data, (int) pos, --this.size - (int) pos);
            return value;
        }
    }

    @Override
    public long indexOf(V value) {
        final Object[] data = this.data;
        for (int i = 0, size = this.size; i < size; i++) {
            if (Objects.equals(value, data[i])) {
                return i;
            }
        }
        return -1L;
    }

    @Override
    public void add(V value) {
        this.add(this.size, value);
    }

    @Override
    public void remove(V value) {
        this.checkAndRemove(value);
    }

    @Override
    public boolean checkAndRemove(V value) {
        final Object[] data = this.data;
        for (int i = 0, size = this.size; i < size; i++) {
            if (Objects.equals(value, data[i])) {
                this.remove(value);
                return true;
            }
        }
        return false;
    }

    @Override
    public void forEach(@NonNull Consumer<V> consumer) {
        final Object[] data = this.data;
        for (int i = 0, size = this.size; i < size; i++) {
            @SuppressWarnings("unchecked")
            V value = (V) data[i];
            consumer.accept(value);
        }
    }

    @Override
    public PIterator<V> iterator() {
        return new PIterator<V>() {
            private final Object[] data = PArrayList.this.data;
            private int index = 0;

            @Override
            public boolean hasNext() {
                return this.index + 1 < PArrayList.this.size;
            }

            @Override
            @SuppressWarnings("unchecked")
            public V next() {
                if (this.hasNext()) {
                    return (V) this.data[++this.index];
                } else {
                    throw new IterationCompleteException();
                }
            }

            @Override
            @SuppressWarnings("unchecked")
            public V peek() {
                if (this.hasNext()) {
                    return (V) this.data[this.index + 1];
                } else {
                    throw new IterationCompleteException();
                }
            }

            @Override
            public void remove() {
                PArrayList.this.remove(this.index--);
            }
        };
    }

    @Override
    public void push(V value) {
        this.add(value);
    }

    @Override
    public V pop() {
        return this.getAndRemove(this.size - 1);
    }

    @Override
    public long size() {
        return this.size;
    }

    @Override
    public void clear() {
        Arrays.fill(this.data, 0, this.size, null);
        this.size = 0;
    }

    @Override
    public boolean isConcurrent() {
        return false;
    }
}
