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

package net.daporkchop.lib.nbt.tag.notch;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.daporkchop.lib.nbt.NBTInputStream;
import net.daporkchop.lib.nbt.NBTOutputStream;
import net.daporkchop.lib.nbt.tag.Tag;
import net.daporkchop.lib.nbt.tag.TagRegistry;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * A tag that contains a list of unnamed {@link Tag}s required the same type.
 * <p>
 * These are contained in a single {@link List} while in memory.
 *
 * @author DaPorkchop_
 */
@Getter
@Setter
public class ListTag<T extends Tag> extends Tag implements List<T> {
    @NonNull
    protected List<T> value;

    public ListTag(String name) {
        super(name);
    }

    public ListTag(String name, @NonNull List<T> value) {
        super(name);
        this.value = value;
    }

    @Override
    public void read(@NonNull NBTInputStream in, @NonNull TagRegistry registry) throws IOException {
        this.value = new ArrayList<>();
        byte type = in.readByte();
        int len = in.readInt();
        for (int i = 0; i < len; i++) {
            T tag = registry.create(type, null);
            tag.read(in, registry);
            this.value.add(tag);
        }
    }

    @Override
    public void write(@NonNull NBTOutputStream out, @NonNull TagRegistry registry) throws IOException {
        if (this.value.isEmpty()) {
            out.writeByte((byte) 0);
            out.writeInt(0);
        } else {
            byte id = registry.getId(this.value.get(0).getClass());
            out.writeByte(id);
            out.writeInt(this.value.size());
            for (T tag : this.value) {
                tag.write(out, registry);
            }
        }
    }

    @Override
    public synchronized void release() throws AlreadyReleasedException {
        if (this.value != Collections.<T>emptyList()) {
            this.value.forEach(Tag::release);
            this.value = Collections.emptyList();
        } else {
            throw new AlreadyReleasedException();
        }
    }

    @Override
    public String toString() {
        return String.format("ListTag(\"%s\"): %d tags", this.getName(), this.value.size());
    }

    //list implementations
    @Override
    public int size() {
        return this.value.size();
    }

    @Override
    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.value.contains(o);
    }

    @Override
    public Object[] toArray() {
        return this.value.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return this.value.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return this.value.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return this.value.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.value.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return this.value.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return this.value.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.value.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.value.retainAll(c);
    }

    @Override
    public void clear() {
        this.value.clear();
    }

    @Override
    public T get(int index) {
        return this.value.get(index);
    }

    @Override
    public T set(int index, T element) {
        return this.value.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        this.value.add(index, element);
    }

    @Override
    public T remove(int index) {
        return this.value.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.value.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.value.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return this.value.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return this.value.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return this.value.subList(fromIndex, toIndex);
    }

    @Override
    public void replaceAll(UnaryOperator<T> operator) {
        this.value.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super T> c) {
        this.value.sort(c);
    }

    @Override
    public Spliterator<T> spliterator() {
        return this.value.spliterator();
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        return this.value.removeIf(filter);
    }

    @Override
    public Stream<T> stream() {
        return this.value.stream();
    }

    @Override
    public Stream<T> parallelStream() {
        return this.value.parallelStream();
    }

    @Override
    public void forEach(@NonNull Consumer<? super T> consumer) {
        this.value.forEach(consumer);
    }

    @Override
    public Iterator<T> iterator() {
        return this.value.iterator();
    }
}
