/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2025 DaPorkchop_
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

package net.daporkchop.lib.collections.collectors;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Additional methods that should have been added to {@link Collectors} but weren't for some reason.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PCollectors {
    //
    // Map utility lambdas
    //

    /**
     * Public version of {@link Collectors#throwingMerger()}.
     *
     * @see Collectors#throwingMerger()
     */
    public static <T> BinaryOperator<T> throwingMerger() {
        return (u, v) -> {
            throw new IllegalStateException("Duplicate key <unknown> (attempted merging values " + u + " and " + v + ')');
        };
    }

    /**
     * @return a {@link Function} which returns the key of a {@link Map.Entry map entry}
     */
    public static <K> Function<? extends Map.Entry<K, ?>, ? super K> mapEntryKey() {
        return Map.Entry::getKey;
    }

    /**
     * @return a {@link Function} which returns the value of a {@link Map.Entry map entry}
     */
    public static <V> Function<? extends Map.Entry<?, V>, ? super V> mapEntryValue() {
        return Map.Entry::getValue;
    }

    //
    // Collection factory lambdas
    //

    /**
     * @return a {@link Supplier} which returns new instances of {@link HashMap}
     */
    public static <K, V> Supplier<HashMap<K, V>> hashMapFactory() {
        return HashMap::new;
    }

    /**
     * @return a {@link Supplier} which returns new instances of {@link IdentityHashMap}
     */
    public static <K, V> Supplier<IdentityHashMap<K, V>> identityHashMapFactory() {
        return IdentityHashMap::new;
    }

    /**
     * @return a {@link Supplier} which returns new instances of {@link LinkedHashMap}
     */
    public static <K, V> Supplier<LinkedHashMap<K, V>> linkedHashMapFactory() {
        return LinkedHashMap::new;
    }

    /**
     * @return a {@link Supplier} which returns new instances of {@link TreeMap}
     */
    public static <K, V> Supplier<TreeMap<K, V>> treeMapFactory() {
        return TreeMap::new;
    }

    /**
     * @param keyClass the key class
     * @return a {@link Supplier} which returns new instances of {@link EnumMap}
     */
    public static <K extends Enum<K>, V> Supplier<EnumMap<K, V>> enumMapFactory(Class<K> keyClass) {
        assert keyClass.isEnum() : "not an enum: " + keyClass;
        return () -> new EnumMap<>(keyClass);
    }

    /**
     * @return a {@link Supplier} which returns new instances of {@link HashSet}
     */
    public static <E> Supplier<HashSet<E>> hashSetFactory() {
        return HashSet::new;
    }

    /**
     * @return a {@link Supplier} which returns new instances of {@link LinkedHashSet}
     */
    public static <E> Supplier<LinkedHashSet<E>> linkedHashSetFactory() {
        return LinkedHashSet::new;
    }

    /**
     * @return a {@link Supplier} which returns new instances of {@link TreeSet}
     */
    public static <E> Supplier<TreeSet<E>> treeSetFactory() {
        return TreeSet::new;
    }

    /**
     * @param elementClass the element class
     * @return a {@link Supplier} which returns new instances of {@link EnumSet}
     */
    public static <E extends Enum<E>> Supplier<EnumSet<E>> enumSetFactory(Class<E> elementClass) {
        assert elementClass.isEnum() : "not an enum: " + elementClass;
        return () -> EnumSet.noneOf(elementClass);
    }

    /**
     * @return a {@link Supplier} which returns new instances of {@link ArrayList}
     */
    public static <E> Supplier<ArrayList<E>> arrayListFactory() {
        return ArrayList::new;
    }

    /**
     * @return a {@link Supplier} which returns new instances of {@link LinkedList}
     */
    public static <E> Supplier<LinkedList<E>> linkedListFactory() {
        return LinkedList::new;
    }

    //
    // Collectors
    //

    /**
     * Variant of {@link Collectors#toMap(Function, Function)} which allows using a custom {@link Map} factory.
     *
     * @see Collectors#toMap(Function, Function)
     */
    public static <T, K, U, M extends Map<K, U>> Collector<T, ?, M> toMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper, Supplier<M> mapFactory) {
        return Collectors.toMap(keyMapper, valueMapper, throwingMerger(), mapFactory);
    }

    /**
     * Variant of {@link Collectors#toMap(Function, Function)} which always returns a {@link HashMap}.
     *
     * @see Collectors#toMap(Function, Function)
     */
    public static <T, K, U> Collector<T, ?, HashMap<K, U>> toHashMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
        return toMap(keyMapper, valueMapper, hashMapFactory());
    }

    /**
     * Variant of {@link Collectors#toMap(Function, Function)} which always returns a {@link IdentityHashMap}.
     *
     * @see Collectors#toMap(Function, Function)
     */
    public static <T, K, U> Collector<T, ?, IdentityHashMap<K, U>> toIdentityHashMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
        return toMap(keyMapper, valueMapper, identityHashMapFactory());
    }

    /**
     * Variant of {@link Collectors#toMap(Function, Function)} which always returns a {@link LinkedHashMap} with entries added in encounter order.
     *
     * @see Collectors#toMap(Function, Function)
     */
    public static <T, K, U> Collector<T, ?, LinkedHashMap<K, U>> toLinkedHashMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
        return toMap(keyMapper, valueMapper, linkedHashMapFactory());
    }

    /**
     * Variant of {@link Collectors#toMap(Function, Function)} which always returns a {@link TreeMap}.
     *
     * @see Collectors#toMap(Function, Function)
     */
    public static <T, K, U> Collector<T, ?, TreeMap<K, U>> toTreeMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
        return toMap(keyMapper, valueMapper, treeMapFactory());
    }

    /**
     * Variant of {@link Collectors#toMap(Function, Function)} which always returns an {@link EnumMap}.
     *
     * @see Collectors#toMap(Function, Function)
     */
    public static <T, K extends Enum<K>, U> Collector<T, ?, EnumMap<K, U>> toEnumMap(Class<K> keyClass, Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
        return toMap(keyMapper, valueMapper, enumMapFactory(keyClass));
    }

    /**
     * Variant of {@link Collectors#toSet()} which allows using a custom {@link Set} factory.
     *
     * @param setFactory a {@link Supplier} which provides new instances of the target {@link Set} implementation
     * @see Collectors#toSet()
     */
    public static <E, S extends Set<E>> Collector<E, ?, S> toSet(Supplier<S> setFactory) {
        return Collectors.toCollection(setFactory);
    }

    /**
     * Variant of {@link Collectors#toSet()} which always returns a {@link HashSet}.
     *
     * @see Collectors#toSet()
     */
    public static <E> Collector<E, ?, HashSet<E>> toHashSet() {
        return toSet(hashSetFactory());
    }

    /**
     * Variant of {@link Collectors#toSet()} which always returns a {@link LinkedHashSet} with elements added in encounter order.
     *
     * @see Collectors#toSet()
     */
    public static <E> Collector<E, ?, LinkedHashSet<E>> toLinkedHashSet() {
        return toSet(linkedHashSetFactory());
    }

    /**
     * Variant of {@link Collectors#toSet()} which always returns a {@link TreeSet}.
     *
     * @see Collectors#toSet()
     */
    public static <E> Collector<E, ?, TreeSet<E>> toTreeSet() {
        return toSet(treeSetFactory());
    }

    /**
     * Variant of {@link Collectors#toSet()} which always returns an {@link EnumSet}.
     *
     * @see Collectors#toSet()
     */
    public static <E extends Enum<E>> Collector<E, ?, EnumSet<E>> toEnumSet(Class<E> elementClass) {
        return toSet(enumSetFactory(elementClass));
    }

    /**
     * Variant of {@link Collectors#toList()} which allows using a custom {@link List} factory.
     *
     * @param listFactory a {@link Supplier} which provides new instances of the target {@link List} implementation
     * @see Collectors#toList()
     */
    public static <E, L extends List<E>> Collector<E, ?, L> toList(Supplier<L> listFactory) {
        return Collectors.toCollection(listFactory);
    }

    /**
     * Variant of {@link Collectors#toList()} which always returns an {@link ArrayList}.
     *
     * @see Collectors#toList()
     */
    public static <E> Collector<E, ?, ArrayList<E>> toArrayList() {
        return toList(arrayListFactory());
    }

    /**
     * Variant of {@link Collectors#toList()} which always returns a {@link LinkedList}.
     *
     * @see Collectors#toList()
     */
    public static <E> Collector<E, ?, LinkedList<E>> toLinkedList() {
        return toList(linkedListFactory());
    }
}
