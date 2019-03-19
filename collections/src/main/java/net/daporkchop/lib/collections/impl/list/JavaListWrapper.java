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

import lombok.NonNull;
import net.daporkchop.lib.collections.PList;
import net.daporkchop.lib.collections.impl.collection.AbstractJavaCollectionWrapper;
import net.daporkchop.lib.collections.stream.PStream;
import net.daporkchop.lib.collections.stream.impl.list.ConcurrentListStream;
import net.daporkchop.lib.collections.stream.impl.list.UncheckedListStream;

import java.util.List;

/**
 * @author DaPorkchop_
 */
public class JavaListWrapper<V> extends AbstractJavaCollectionWrapper<V, List<V>> implements PList<V> {
    public JavaListWrapper(List<V> delegate) {
        super(delegate);
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
}
