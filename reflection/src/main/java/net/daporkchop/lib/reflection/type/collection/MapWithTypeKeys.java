/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.reflection.type.collection;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.util.PorkUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * A {@link Map} which uses {@link Type}s as keys.
 * <p>
 * Neither {@code null} keys nor {@code null} values are not allowed.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class MapWithTypeKeys<V> implements Map<Type, V> {
    @NonNull
    protected final Map<TypeWrapper, V> delegate;

    protected transient Set<Type> keySet;
    protected transient Set<Entry<Type, V>> entrySet;

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean containsValue(Object value) {
        return this.delegate.containsValue(value);
    }

    @Override
    public boolean containsKey(Object key) {
        return key instanceof Type && this.delegate.containsKey(new TypeWrapper((Type) key));
    }

    @Override
    public V get(Object key) {
        return key instanceof Type ? this.delegate.get(new TypeWrapper((Type) key)) : null;
    }

    @Override
    public V put(@NonNull Type key, @NonNull V value) {
        return this.delegate.put(new TypeWrapper(key), value);
    }

    @Override
    public V remove(Object key) {
        return key instanceof Type ? this.delegate.remove(new TypeWrapper((Type) key)) : null;
    }

    @Override
    public void putAll(@NonNull Map<? extends Type, ? extends V> m) {
        if (m instanceof MapWithTypeKeys) { //unwrap and do putAll on the delegates
            this.delegate.putAll(PorkUtil.<MapWithTypeKeys<? extends V>>uncheckedCast(m).delegate);
        } else { //insert every entry individually
            m.forEach(this::put);
        }
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof Map)) {
            return false;
        } else if (o instanceof MapWithTypeKeys) { //unwrap and do equals() on the delegates
            return this.delegate.equals(((MapWithTypeKeys) o).delegate);
        } else { //fall back to default equals() (adapted from AbstractMap)
            Map<?, ?> other = (Map<?, ?>) o;
            if (this.size() != other.size()) { //if the sizes are different then the maps are certainly not equal
                return false;
            }

            try {
                for (Map.Entry<TypeWrapper, V> entry : this.delegate.entrySet()) {
                    Type key = entry.getKey().unwrap();
                    V value = entry.getValue();

                    if (!value.equals(other.get(key))) {
                        return false;
                    }
                }
            } catch (ClassCastException | NullPointerException unused) { //no idea why NullPointerException is being caught here, but it's in AbstractMap and i want to preserve compatibility
                return false;
            }

            return true;
        }
    }

    @Override
    public int hashCode() {
        return this.delegate.hashCode(); //this is fine because TypeWrapper doesn't modify the return value of hashCode()
    }

    @Override
    public String toString() {
        return this.delegate.toString(); //this is fine because TypeWrapper doesn't modify the return value of toString()
    }

    @Override
    public Set<Type> keySet() {
        Set<Type> keySet = this.keySet;
        if (keySet == null) { //the key set hasn't been created yet
            this.keySet = keySet = this.createKeySet();
        }
        return keySet;
    }

    protected Set<Type> createKeySet() {
        return new KeySet(this.delegate);
    }

    @Override
    public Collection<V> values() {
        //we don't need to wrap the delegate's value collection
        return this.delegate.values();
    }

    @Override
    public Set<Entry<Type, V>> entrySet() {
        Set<Entry<Type, V>> entrySet = this.entrySet;
        if (entrySet == null) { //the entry set hasn't been created yet
            this.entrySet = entrySet = this.createEntrySet();
        }
        return entrySet;
    }

    protected Set<Entry<Type, V>> createEntrySet() {
        return new EntrySet<>(this.delegate);
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return key instanceof Type ? this.delegate.getOrDefault(new TypeWrapper((Type) key), defaultValue) : defaultValue;
    }

    @Override
    public void forEach(@NonNull BiConsumer<? super Type, ? super V> action) {
        this.delegate.forEach((key, value) -> action.accept(key.unwrap(), value));
    }

    @Override
    public void replaceAll(@NonNull BiFunction<? super Type, ? super V, ? extends V> function) {
        this.delegate.replaceAll((key, value) -> function.apply(key.unwrap(), value));
    }

    @Override
    public V putIfAbsent(@NonNull Type key, @NonNull V value) {
        return this.delegate.putIfAbsent(new TypeWrapper(key), value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return key instanceof Type && this.delegate.remove(new TypeWrapper((Type) key), value);
    }

    @Override
    public boolean replace(@NonNull Type key, V oldValue, @NonNull V newValue) {
        return this.delegate.replace(new TypeWrapper(key), oldValue, newValue);
    }

    @Override
    public V replace(@NonNull Type key, @NonNull V value) {
        return this.delegate.replace(new TypeWrapper(key), value);
    }

    @Override
    public V computeIfAbsent(@NonNull Type keyIn, @NonNull Function<? super Type, ? extends V> mappingFunction) {
        return this.delegate.computeIfAbsent(new TypeWrapper(keyIn), key -> Objects.requireNonNull(mappingFunction.apply(key.unwrap())));
    }

    @Override
    public V computeIfPresent(@NonNull Type keyIn, @NonNull BiFunction<? super Type, ? super V, ? extends V> remappingFunction) {
        return this.delegate.computeIfPresent(new TypeWrapper(keyIn), (key, value) -> Objects.requireNonNull(remappingFunction.apply(key.unwrap(), value)));
    }

    @Override
    public V compute(@NonNull Type keyIn, @NonNull BiFunction<? super Type, ? super V, ? extends V> remappingFunction) {
        return this.delegate.compute(new TypeWrapper(keyIn), (key, value) -> Objects.requireNonNull(remappingFunction.apply(key.unwrap(), value)));
    }

    @Override
    public V merge(@NonNull Type keyIn, @NonNull V valueIn, @NonNull BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return this.delegate.merge(new TypeWrapper(keyIn), valueIn, (v0, v1) -> Objects.requireNonNull(remappingFunction.apply(v0, v1)));
    }

    /**
     * Default implementation of {@link Set} for {@link MapWithTypeKeys#keySet()}.
     *
     * @author DaPorkchop_
     */
    @RequiredArgsConstructor
    protected static class KeySet implements Set<Type> {
        @NonNull
        protected final Map<TypeWrapper, ?> delegate;

        @Override
        public int size() {
            return this.delegate.size();
        }

        @Override
        public boolean isEmpty() {
            return this.delegate.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return o instanceof Type && this.delegate.containsKey(new TypeWrapper((Type) o));
        }

        @Override
        public Iterator<Type> iterator() {
            return new UnwrappingIterator(this.delegate.keySet().iterator());
        }

        @Override
        public void forEach(@NonNull Consumer<? super Type> action) {
            this.delegate.forEach((key, value) -> action.accept(key.unwrap()));
        }

        @Override
        public Object[] toArray() {
            //invoke delegate toArray(), then unwrap each element
            Object[] array = this.delegate.keySet().toArray();
            for (int i = 0; i < array.length; i++) {
                array[i] = ((TypeWrapper) array[i]).unwrap(); //this is safe because we don't allow null keys
            }
            return array;
        }

        @Override
        public <T> T[] toArray(T[] a) {
            @RequiredArgsConstructor
            class State implements BiConsumer<TypeWrapper, Object> {
                @NonNull
                T[] arr;
                int idx;

                @Override
                public void accept(TypeWrapper key, Object value) {
                    T[] arr = this.arr;
                    int idx = this.idx;

                    if (arr.length == idx) { //the array is full, we need to grow it
                        arr = this.grow();
                    }
                    arr[this.idx = idx + 1] = uncheckedCast(key.unwrap());
                }

                private T[] grow() {
                    //the grow factor here is 1.5
                    return this.arr = Arrays.copyOf(this.arr, max(addExact(this.arr.length, this.arr.length >> 1), 16));
                }
            }

            //estimate size of array; be prepared to see more or fewer elements
            int size = this.size();
            State state = new State(a.length >= size ? a : uncheckedCast(Array.newInstance(a.getClass().getComponentType(), size)));

            //iterate over all the elements and copy them into the array, growing the array as needed
            this.delegate.forEach(state);

            T[] out = state.arr;
            size = state.idx;

            if (out.length > size) { //the array is bigger than the number of elements that were added
                //noinspection ArrayEquality
                if (a == out) { //the array is unchanged from the original
                    out[size] = null; //null-terminate
                } else { //the array was grown
                    out = Arrays.copyOf(out, size); //truncate the array
                }
            }

            return out;
        }

        @Override
        public boolean add(@NonNull Type type) {
            //the delegate will almost certainly throw UnsupportedOperationException
            return this.delegate.keySet().add(new TypeWrapper(type));
        }

        @Override
        public boolean remove(Object o) {
            return o instanceof Type && this.delegate.remove(new TypeWrapper((Type) o)) != null;
        }

        @Override
        public boolean containsAll(@NonNull Collection<?> c) {
            return c instanceof KeySet
                    ? this.delegate.keySet().containsAll(((KeySet) c).delegate.keySet()) //unwrap and do containsAll() on the delegates' keySets
                    : this.defaultContainsAll(c); //fall back to default implementation
        }

        protected boolean defaultContainsAll(Collection<?> c) {
            for (Object element : c) {
                if (!this.contains(element)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean addAll(@NonNull Collection<? extends Type> c) {
            //the delegate will almost certainly throw UnsupportedOperationException
            return c instanceof KeySet
                    ? this.delegate.keySet().addAll(((KeySet) c).delegate.keySet()) //unwrap and do addAll() on the delegates' keySets
                    : this.defaultAddAll(c); //fall back to default implementation
        }

        protected boolean defaultAddAll(Collection<? extends Type> c) {
            class State implements Consumer<Type> {
                boolean modified = false;

                @Override
                public void accept(Type type) {
                    if (KeySet.this.add(type)) {
                        this.modified = true;
                    }
                }
            }

            State state = new State();
            c.forEach(state);
            return state.modified;
        }

        @Override
        public boolean retainAll(@NonNull Collection<?> c) {
            return c instanceof KeySet
                    ? this.delegate.keySet().retainAll(((KeySet) c).delegate.keySet()) //unwrap and do retainAll() on the delegates' keySets
                    : this.defaultRetainAll(c); //fall back to default implementation
        }

        protected boolean defaultRetainAll(Collection<?> c) {
            return this.delegate.keySet().removeIf(key -> !c.contains(key.unwrap()));
        }

        @Override
        public boolean removeAll(@NonNull Collection<?> c) {
            return c instanceof KeySet
                    ? this.delegate.keySet().removeAll(((KeySet) c).delegate.keySet()) //unwrap and do removeAll() on the delegates' keySets
                    : this.defaultRemoveAll(c); //fall back to default implementation
        }

        protected boolean defaultRemoveAll(Collection<?> c) { //adapted from AbstractSet
            if (this.size() > c.size()) { //the other collection is smaller, iterate through it and remove matching values from self
                class State implements Consumer<Object> {
                    boolean modified = false;

                    @Override
                    public void accept(Object o) {
                        if (KeySet.this.remove(o)) {
                            this.modified = true;
                        }
                    }
                }

                State state = new State();
                c.forEach(state);
                return state.modified;
            } else { //this set is smaller, use removeIf
                return this.delegate.keySet().removeIf(key -> c.contains(key.unwrap()));
            }
        }

        @Override
        public boolean removeIf(@NonNull Predicate<? super Type> filter) {
            return this.delegate.keySet().removeIf(key -> filter.test(key.unwrap()));
        }

        @Override
        public void clear() {
            this.delegate.clear();
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof KeySet
                    ? this.delegate.keySet().equals(((KeySet) o).delegate.keySet())  //unwrap and do equals() on the delegates' keySets
                    : super.equals(o); //fall back to default implementation
        }

        @Override
        public int hashCode() {
            return this.delegate.keySet().hashCode(); //this is fine because TypeWrapper doesn't modify the return value of hashCode()
        }

        @Override
        public String toString() {
            return this.delegate.keySet().toString(); //this is fine because TypeWrapper doesn't modify the return value of toString()
        }

        @Override
        public Spliterator<Type> spliterator() {
            return new UnwrappingSpliterator(this.delegate.keySet().spliterator());
        }

        @Override
        public Stream<Type> stream() {
            return this.delegate.keySet().stream().map(TypeWrapper::unwrap);
        }

        @Override
        public Stream<Type> parallelStream() {
            return this.delegate.keySet().parallelStream().map(TypeWrapper::unwrap);
        }

        /**
         * Default implementation of an {@link Iterator} for {@link KeySet}.
         *
         * @author DaPorkchop_
         */
        @RequiredArgsConstructor
        protected static class UnwrappingIterator implements Iterator<Type> {
            @NonNull
            protected final Iterator<TypeWrapper> delegate;

            @Override
            public boolean hasNext() {
                return this.delegate.hasNext();
            }

            @Override
            public Type next() {
                return this.delegate.next().unwrap();
            }

            @Override
            public void remove() {
                this.delegate.remove();
            }

            @Override
            public void forEachRemaining(@NonNull Consumer<? super Type> action) {
                this.delegate.forEachRemaining(key -> action.accept(key.unwrap()));
            }
        }

        /**
         * Default implementation of an {@link Spliterator} for {@link KeySet}.
         *
         * @author DaPorkchop_
         */
        @RequiredArgsConstructor
        protected static class UnwrappingSpliterator implements Spliterator<Type> {
            @NonNull
            protected final Spliterator<TypeWrapper> delegate;

            @Override
            public boolean tryAdvance(@NonNull Consumer<? super Type> action) {
                return this.delegate.tryAdvance(key -> action.accept(key.unwrap()));
            }

            @Override
            public void forEachRemaining(@NonNull Consumer<? super Type> action) {
                this.delegate.forEachRemaining(key -> action.accept(key.unwrap()));
            }

            @Override
            public Spliterator<Type> trySplit() {
                //try to split the delegate, wrap if successful
                Spliterator<TypeWrapper> split = this.delegate.trySplit();
                return split != null ? new UnwrappingSpliterator(split) : null;
            }

            @Override
            public long estimateSize() {
                return this.delegate.estimateSize();
            }

            @Override
            public long getExactSizeIfKnown() {
                return this.delegate.getExactSizeIfKnown();
            }

            @Override
            public int characteristics() {
                return this.delegate.characteristics();
            }

            @Override
            public boolean hasCharacteristics(int characteristics) {
                return this.delegate.hasCharacteristics(characteristics);
            }

            @Override
            public Comparator<? super Type> getComparator() {
                //try to get the delegate comparator, wrap if one exists
                Comparator<? super TypeWrapper> comparator = this.delegate.getComparator();
                return comparator != null ? Comparator.comparing(TypeWrapper::new, comparator) : null;
            }
        }
    }

    /**
     * Default implementation of {@link Set} for {@link MapWithTypeKeys#entrySet()}.
     *
     * @author DaPorkchop_
     */
    @RequiredArgsConstructor
    protected static class EntrySet<V> implements Set<Entry<Type, V>> {
        protected static <V> Entry<TypeWrapper, V> wrap(Entry<Type, V> unwrapped) {
            return new AbstractMap.SimpleImmutableEntry<>(new TypeWrapper(unwrapped.getKey()), unwrapped.getValue());
        }

        protected static <V> Entry<Type, V> unwrapInternal(Entry<TypeWrapper, V> wrapped) {
            return new AbstractMap.SimpleImmutableEntry<>(wrapped.getKey().unwrap(), wrapped.getValue());
        }

        protected static <V> Entry<Type, V> unwrapExternal(Entry<TypeWrapper, V> wrapped) {
            return new DelegateEntryWrapper<>(wrapped);
        }

        @NonNull
        protected final Map<TypeWrapper, V> delegate;

        @Override
        public int size() {
            return this.delegate.size();
        }

        @Override
        public boolean isEmpty() {
            return this.delegate.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return o instanceof Entry && this.delegate.entrySet().contains(wrap(uncheckedCast(o)));
        }

        @Override
        public Iterator<Entry<Type, V>> iterator() {
            return new UnwrappingIterator<>(this.delegate.entrySet().iterator());
        }

        @Override
        public void forEach(@NonNull Consumer<? super Entry<Type, V>> action) {
            this.delegate.entrySet().forEach(wrapped -> action.accept(unwrapExternal(wrapped)));
        }

        @Override
        public Object[] toArray() {
            //invoke delegate toArray(), then unwrap each element
            Object[] array = this.delegate.entrySet().toArray();
            for (int i = 0; i < array.length; i++) {
                array[i] = unwrapExternal(PorkUtil.<Map.Entry<TypeWrapper, V>>uncheckedCast(array[i]));
            }
            return array;
        }

        @Override
        public <T> T[] toArray(T[] a) {
            @RequiredArgsConstructor
            class State implements Consumer<Entry<TypeWrapper, V>> {
                @NonNull
                T[] arr;
                int idx;

                @Override
                public void accept(Entry<TypeWrapper, V> wrapped) {
                    T[] arr = this.arr;
                    int idx = this.idx;

                    if (arr.length == idx) { //the array is full, we need to grow it
                        arr = this.grow();
                    }
                    arr[this.idx = idx + 1] = uncheckedCast(unwrapExternal(wrapped));
                }

                private T[] grow() {
                    //the grow factor here is 1.5
                    return this.arr = Arrays.copyOf(this.arr, max(addExact(this.arr.length, this.arr.length >> 1), 16));
                }
            }

            //estimate size of array; be prepared to see more or fewer elements
            int size = this.size();
            State state = new State(a.length >= size ? a : uncheckedCast(Array.newInstance(a.getClass().getComponentType(), size)));

            //iterate over all the elements and copy them into the array, growing the array as needed
            this.delegate.entrySet().forEach(state);

            T[] out = state.arr;
            size = state.idx;

            if (out.length > size) { //the array is bigger than the number of elements that were added
                //noinspection ArrayEquality
                if (a == out) { //the array is unchanged from the original
                    out[size] = null; //null-terminate
                } else { //the array was grown
                    out = Arrays.copyOf(out, size); //truncate the array
                }
            }

            return out;
        }

        @Override
        public boolean add(@NonNull Entry<Type, V> unwrapped) {
            return this.delegate.entrySet().add(wrap(unwrapped));
        }

        @Override
        public boolean remove(Object o) {
            return o instanceof Entry && this.delegate.entrySet().contains(wrap(uncheckedCast(o)));
        }

        @Override
        public boolean containsAll(@NonNull Collection<?> c) {
            return c instanceof EntrySet
                    ? this.delegate.entrySet().containsAll(((EntrySet) c).delegate.entrySet()) //unwrap and do containsAll() on the delegates' entrySets
                    : this.defaultContainsAll(c); //fall back to default implementation
        }

        protected boolean defaultContainsAll(Collection<?> c) {
            for (Object element : c) {
                if (!this.contains(element)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean addAll(@NonNull Collection<? extends Entry<Type, V>> c) {
            return c instanceof EntrySet
                    ? this.delegate.entrySet().addAll(PorkUtil.<EntrySet<V>>uncheckedCast(c).delegate.entrySet()) //unwrap and do addAll() on the delegates' entrySets
                    : this.defaultAddAll(c); //fall back to default implementation
        }

        protected boolean defaultAddAll(Collection<? extends Entry<Type, V>> c) {
            class State implements Consumer<Entry<Type, V>> {
                boolean modified = false;

                @Override
                public void accept(Entry<Type, V> unwrapped) {
                    if (EntrySet.this.add(unwrapped)) {
                        this.modified = true;
                    }
                }
            }

            State state = new State();
            c.forEach(state);
            return state.modified;
        }

        @Override
        public boolean retainAll(@NonNull Collection<?> c) {
            return c instanceof EntrySet
                    ? this.delegate.entrySet().retainAll(((EntrySet) c).delegate.entrySet()) //unwrap and do retainAll() on the delegates' entrySets
                    : this.defaultRetainAll(c); //fall back to default implementation
        }

        protected boolean defaultRetainAll(Collection<?> c) {
            return this.delegate.entrySet().removeIf(wrapped -> !c.contains(unwrapInternal(wrapped)));
        }

        @Override
        public boolean removeAll(@NonNull Collection<?> c) {
            return c instanceof EntrySet
                    ? this.delegate.entrySet().removeAll(((EntrySet) c).delegate.entrySet()) //unwrap and do removeAll() on the delegates' entrySets
                    : this.defaultRemoveAll(c); //fall back to default implementation
        }

        protected boolean defaultRemoveAll(Collection<?> c) { //adapted from AbstractSet
            if (this.size() > c.size()) { //the other collection is smaller, iterate through it and remove matching values from self
                class State implements Consumer<Object> {
                    boolean modified = false;

                    @Override
                    public void accept(Object o) {
                        if (EntrySet.this.remove(o)) {
                            this.modified = true;
                        }
                    }
                }

                State state = new State();
                c.forEach(state);
                return state.modified;
            } else { //this set is smaller, use removeIf
                return this.delegate.entrySet().removeIf(wrapped -> c.contains(unwrapInternal(wrapped)));
            }
        }

        @Override
        public boolean removeIf(@NonNull Predicate<? super Entry<Type, V>> filter) {
            return this.delegate.entrySet().removeIf(wrapped -> filter.test(unwrapInternal(wrapped)));
        }

        @Override
        public void clear() {
            this.delegate.clear();
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof EntrySet
                    ? this.delegate.entrySet().equals(((EntrySet) o).delegate.entrySet())  //unwrap and do equals() on the delegates' entrySets
                    : super.equals(o); //fall back to default implementation
        }

        @Override
        public int hashCode() {
            return this.delegate.entrySet().hashCode(); //this is fine because TypeWrapper doesn't modify the return value of hashCode()
        }

        @Override
        public String toString() {
            return this.delegate.entrySet().toString(); //this is fine because TypeWrapper doesn't modify the return value of toString()
        }

        @Override
        public Spliterator<Entry<Type, V>> spliterator() {
            return new UnwrappingSpliterator<>(this.delegate.entrySet().spliterator());
        }

        @Override
        public Stream<Entry<Type, V>> stream() {
            return this.delegate.entrySet().stream().map(EntrySet::unwrapExternal);
        }

        @Override
        public Stream<Entry<Type, V>> parallelStream() {
            return this.delegate.entrySet().parallelStream().map(EntrySet::unwrapExternal);
        }

        /**
         * Unwraps a {@link Entry <code>Map.Entry&lt;TypeWrapper, V&gt;</code>} into a {@link Entry <code>Map.Entry&lt;Type, V&gt;</code>}.
         *
         * @author DaPorkchop_
         */
        @RequiredArgsConstructor
        protected static class DelegateEntryWrapper<V> implements Entry<Type, V> {
            @NonNull
            protected final Entry<TypeWrapper, V> delegate;

            @Override
            public Type getKey() {
                return this.delegate.getKey().unwrap();
            }

            @Override
            public V getValue() {
                return this.delegate.getValue();
            }

            @Override
            public V setValue(V value) {
                return this.delegate.setValue(value);
            }

            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
            @Override
            public boolean equals(Object obj) {
                //this will work even if obj is an Entry<Type, V> because it'll invoke this.getKey().equals(obj.getKey()), and TypeWrapper#equals() has special handling for Type
                return this.delegate.equals(obj);
            }

            @Override
            public int hashCode() {
                return this.delegate.hashCode(); //this is fine because TypeWrapper doesn't modify the return value of hashCode()
            }

            @Override
            public String toString() {
                return this.delegate.toString(); //this is fine because TypeWrapper doesn't modify the return value of toString()
            }
        }

        /**
         * Default implementation of an {@link Iterator} for {@link EntrySet}.
         *
         * @author DaPorkchop_
         */
        @RequiredArgsConstructor
        protected static class UnwrappingIterator<V> implements Iterator<Entry<Type, V>> {
            @NonNull
            protected final Iterator<Entry<TypeWrapper, V>> delegate;

            @Override
            public boolean hasNext() {
                return this.delegate.hasNext();
            }

            @Override
            public Entry<Type, V> next() {
                return unwrapExternal(this.delegate.next());
            }

            @Override
            public void remove() {
                this.delegate.remove();
            }

            @Override
            public void forEachRemaining(@NonNull Consumer<? super Entry<Type, V>> action) {
                this.delegate.forEachRemaining(wrapped -> action.accept(unwrapExternal(wrapped)));
            }
        }

        /**
         * Default implementation of an {@link Spliterator} for {@link EntrySet}.
         *
         * @author DaPorkchop_
         */
        @RequiredArgsConstructor
        protected static class UnwrappingSpliterator<V> implements Spliterator<Entry<Type, V>> {
            @NonNull
            protected final Spliterator<Entry<TypeWrapper, V>> delegate;

            @Override
            public boolean tryAdvance(@NonNull Consumer<? super Entry<Type, V>> action) {
                return this.delegate.tryAdvance(wrapped -> action.accept(unwrapExternal(wrapped)));
            }

            @Override
            public void forEachRemaining(@NonNull Consumer<? super Entry<Type, V>> action) {
                this.delegate.forEachRemaining(wrapped -> action.accept(unwrapExternal(wrapped)));
            }

            @Override
            public Spliterator<Entry<Type, V>> trySplit() {
                //try to split the delegate, wrap if successful
                Spliterator<Entry<TypeWrapper, V>> split = this.delegate.trySplit();
                return split != null ? new UnwrappingSpliterator<>(split) : null;
            }

            @Override
            public long estimateSize() {
                return this.delegate.estimateSize();
            }

            @Override
            public long getExactSizeIfKnown() {
                return this.delegate.getExactSizeIfKnown();
            }

            @Override
            public int characteristics() {
                return this.delegate.characteristics();
            }

            @Override
            public boolean hasCharacteristics(int characteristics) {
                return this.delegate.hasCharacteristics(characteristics);
            }

            @Override
            public Comparator<? super Entry<Type, V>> getComparator() {
                //try to get the delegate comparator, wrap if one exists
                Comparator<? super Entry<TypeWrapper, V>> comparator = this.delegate.getComparator();
                return comparator != null ? Comparator.comparing(EntrySet::wrap, comparator) : null;
            }
        }
    }
}
