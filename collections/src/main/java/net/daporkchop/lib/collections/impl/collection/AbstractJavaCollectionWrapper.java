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

package net.daporkchop.lib.collections.impl.collection;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.collections.PCollection;
import net.daporkchop.lib.collections.PIterator;
import net.daporkchop.lib.collections.impl.iterator.JavaIteratorWrapper;
import net.daporkchop.lib.collections.stream.PStream;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public abstract class AbstractJavaCollectionWrapper<V, C extends Collection<V>> implements PCollection<V> {
    @NonNull
    protected final C delegate;

    @Override
    public void add(@NonNull V value) {
        this.delegate.add(value);
    }

    @Override
    public boolean contains(@NonNull V value) {
        return this.delegate.contains(value);
    }

    @Override
    public boolean remove(@NonNull V value) {
        return this.delegate.remove(value);
    }

    @Override
    public void removeIf(@NonNull Predicate<V> condition) {
        this.delegate.removeIf(condition);
    }

    @Override
    public void forEach(@NonNull Consumer<V> consumer) {
        this.delegate.forEach(consumer);
    }

    @Override
    public PIterator<V> iterator() {
        return new JavaIteratorWrapper<>(this.delegate.iterator());
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
