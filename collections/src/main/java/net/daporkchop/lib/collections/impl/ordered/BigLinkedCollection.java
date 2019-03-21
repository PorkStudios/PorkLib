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
import net.daporkchop.lib.collections.POrderedCollection;
import net.daporkchop.lib.collections.util.exception.AlreadyRemovedException;

import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
public class BigLinkedCollection<V> implements POrderedCollection<V> {
    protected long size;

    @Override
    public void add(@NonNull V value) {
    }

    @Override
    public boolean contains(@NonNull V value) {
        return false;
    }

    @Override
    public boolean remove(@NonNull V value) {
        return false;
    }

    @Override
    public void forEach(@NonNull Consumer<V> consumer) {
    }

    @Override
    public boolean replace(@NonNull V oldValue, @NonNull V newValue) {
        return false;
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public void clear() {
    }

    @Override
    public OrderedIterator<V> orderedIterator() {
        return null;
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
            //TODO
        }
    }
}
