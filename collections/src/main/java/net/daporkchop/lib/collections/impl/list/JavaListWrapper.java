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

package net.daporkchop.lib.collections.impl.list;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.collections.PList;
import net.daporkchop.lib.collections.stream.PStream;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class JavaListWrapper<V> implements PList<V> {
    @NonNull
    protected final List<V> delegate;

    @Override
    public void add(@NonNull V value) {
        this.delegate.add(value);
    }

    @Override
    public void add(long pos, @NonNull V value) {
        this.delegate.add((int) pos, value);
    }

    @Override
    public void set(long pos, @NonNull V value) {
        this.delegate.set((int) pos, value);
    }

    @Override
    public V getAndSet(long pos, @NonNull V value) {
        return this.delegate.set((int) pos, value);
    }

    @Override
    public V get(long pos) {
        return this.delegate.get((int) pos);
    }

    @Override
    public boolean remove(@NonNull V value) {
        return this.delegate.remove(value);
    }

    @Override
    public void remove(long pos) {
        this.delegate.remove((int) pos);
    }

    @Override
    public V getAndRemove(long pos) {
        return this.delegate.remove((int) pos);
    }

    @Override
    public long indexOf(@NonNull V value) {
        return this.delegate.indexOf(value);
    }

    @Override
    public boolean contains(@NonNull V value) {
        return this.delegate.contains(value);
    }

    @Override
    public void forEach(@NonNull Consumer<V> consumer) {
        this.delegate.forEach(consumer);
    }

    @Override
    public PStream<V> stream() {
        return null; //TODO
    }

    @Override
    public long size() {
        return this.delegate.size();
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }

    @Override
    public boolean isConcurrent() {
        return false;
    }
}
