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
 */

package net.daporkchop.lib.collections.list;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.collections.Immutable;
import net.daporkchop.lib.common.util.PorkUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * Helper methods for {@link List}.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PLists {
    private static final Class<?> Collections$SingletonList = Collections.singletonList(null).getClass();
    private static final Class<?> Collections$UnmodifiableList = Collections.unmodifiableList(new LinkedList<>()).getClass();
    private static final Class<?> Collections$UnmodifiableRandomAccessList = Collections.unmodifiableList(new ArrayList<>()).getClass();

    /**
     * Checks if the given {@link List} is immutable.
     * <p>
     * Note that although this function makes a best-effort guess, it may return false negatives, i.e. it may return {@code false} even if the given
     * {@link List} is actually immutable.
     *
     * @param list the {@link List} to check
     * @return {@code true} if the given {@link List} is definitely immutable
     */
    public static boolean isImmutable(@NonNull List<?> list) {
        return list == Collections.emptyList()
                || Collections$SingletonList.isInstance(list)
                || Collections$UnmodifiableList.isInstance(list)
                || Collections$UnmodifiableRandomAccessList.isInstance(list)
                || list instanceof Immutable;
    }

    /**
     * Obtains an immutable, empty {@link List} instance.
     * <p>
     * The returned object may not be unique.
     *
     * @return an immutable, empty {@link List}
     */
    public static <E> List<E> immutable() {
        return Collections.emptyList();
    }

    /**
     * Obtains an immutable {@link List} instance containing the given element.
     * <p>
     * The returned object may not be unique.
     *
     * @param v0 the element at index {@code 0}
     * @return an immutable {@link List} containing the given element
     */
    public static <E> List<E> immutable(E v0) {
        return Collections.singletonList(v0);
    }

    /**
     * Obtains an immutable {@link List} instance containing the given elements.
     * <p>
     * The returned object may not be unique.
     *
     * @param v0 the element at index {@code 0}
     * @param v1 the element at index {@code 1}
     * @return an immutable {@link List} containing the given elements
     */
    public static <E> List<E> immutable(E v0, E v1) {
        return immutableMove(new Object[]{ v0, v1 });
    }

    /**
     * Obtains an immutable {@link List} instance containing the given elements.
     * <p>
     * The returned object may not be unique.
     *
     * @param v0 the element at index {@code 0}
     * @param v1 the element at index {@code 1}
     * @param v2 the element at index {@code 2}
     * @return an immutable {@link List} containing the given elements
     */
    public static <E> List<E> immutable(E v0, E v1, E v2) {
        return immutableMove(new Object[]{ v0, v1, v2 });
    }

    /**
     * Obtains an immutable {@link List} instance containing the given elements.
     * <p>
     * The returned object may not be unique.
     *
     * @param v0 the element at index {@code 0}
     * @param v1 the element at index {@code 1}
     * @param v2 the element at index {@code 2}
     * @param v3 the element at index {@code 3}
     * @param elements the list elements
     * @return an immutable {@link List} containing the given elements
     */
    @SafeVarargs
    public static <E> List<E> immutable(E v0, E v1, E v2, E v3, E @NonNull ... elements) {
        ArrayList<E> list = new ArrayList<>(4 + elements.length);
        list.add(v0);
        list.add(v1);
        list.add(v2);
        list.add(v3);
        list.addAll(Arrays.asList(elements));
        return immutableMove(list);
    }

    /**
     * Obtains an immutable {@link List} instance containing the given elements.
     * <p>
     * The returned object may not be unique.
     *
     * @param elements the list elements
     * @return an immutable {@link List} containing the given elements
     */
    public static <E> List<E> immutableCopy(E @NonNull [] elements) {
        switch (elements.length) {
            case 0:
                return immutable();
            case 1:
                return immutable(elements[0]);
            default:
                return Collections.unmodifiableList(new ArrayList<>(Arrays.asList(elements)));
        }
    }

    /**
     * Obtains an immutable {@link List} instance containing the given elements.
     * <p>
     * The returned object may not be unique.
     *
     * @param elements the list elements
     * @return an immutable {@link List} containing the given elements
     */
    public static <E> List<E> immutableCopy(@NonNull List<E> elements) {
        if (isImmutable(elements)) {
            return elements;
        }

        switch (elements.size()) {
            case 0:
                return immutable();
            case 1:
                return immutable(elements.get(0));
            default:
                return Collections.unmodifiableList(new ArrayList<>(elements));
        }
    }

    private static <E> List<E> immutableMove(Object[] elements) {
        switch (elements.length) {
            case 0:
                return immutable();
            case 1:
                return immutable(PorkUtil.uncheckedCast(elements[0]));
            default:
                return Collections.unmodifiableList(new ArrayList<>(Arrays.asList(PorkUtil.uncheckedCast(elements))));
        }
    }

    private static <E> List<E> immutableMove(ArrayList<E> elements) {
        switch (elements.size()) {
            case 0:
                return immutable();
            case 1:
                return immutable(elements.get(0));
            default:
                elements.trimToSize();
                return Collections.unmodifiableList(elements);
        }
    }

    /**
     * Obtains a mutable, empty {@link List} instance.
     * <p>
     * The returned object may not be unique.
     *
     * @return an immutable, empty {@link List}
     */
    public static <E> List<E> mutable() {
        return new ArrayList<>();
    }

    /**
     * Obtains a mutable {@link List} instance initialized with the given element.
     * <p>
     * The returned {@link List} is guaranteed to be unique, mutable and random-access.
     *
     * @param v0 the element at index {@code 0}
     * @return an immutable {@link List} initialized with the given element
     */
    public static <E> List<E> mutable(E v0) {
        return new ArrayList<>(Collections.singletonList(v0));
    }

    /**
     * Obtains a mutable {@link List} instance initialized with the given elements.
     * <p>
     * The returned {@link List} is guaranteed to be unique, mutable and random-access.
     *
     * @param elements the list elements
     * @return an immutable {@link List} initialized with the given elements
     */
    @SafeVarargs
    public static <E> List<E> mutable(E @NonNull ... elements) {
        return mutableCopy(elements);
    }

    /**
     * Obtains a mutable {@link List} instance initialized with the given elements.
     * <p>
     * The returned {@link List} is guaranteed to be unique, mutable and random-access.
     *
     * @param elements the list elements
     * @return an immutable {@link List} initialized with the given elements
     */
    public static <E> List<E> mutableCopy(E @NonNull [] elements) {
        return new ArrayList<>(Arrays.asList(elements));
    }

    /**
     * Obtains a mutable {@link List} instance initialized with the given elements.
     * <p>
     * The returned {@link List} is guaranteed to be unique, mutable and random-access.
     *
     * @param elements the list elements
     * @return an immutable {@link List} initialized with the given elements
     */
    public static <E> List<E> mutableCopy(@NonNull List<E> elements) {
        return new ArrayList<>(elements);
    }

    /**
     * Concatenates the two given {@link List}s.
     * <p>
     * The returned {@link List} is an effective copy of the input lists. It <i>may</i> be immutable, and <i>may</i> be identical to one or both of
     * the input arguments.
     *
     * @param first  the first {@link List}
     * @param second the second {@link List}
     * @return a {@link List} containing the concatenated {@link List} elements
     */
    public static <E> List<E> concat(@NonNull List<E> first, @NonNull List<E> second) {
        int firstSize = first.size();
        int secondSize = second.size();

        if (firstSize == 0 && secondSize == 0) {
            return Collections.emptyList();
        } else if (secondSize == 0 && isImmutable(first)) {
            //we can return the first list directly: it's immutable, so no true copy is necessary to make an effective copy
            return first;
        } else if (firstSize == 0 && isImmutable(second)) {
            //we can return the second list directly: it's immutable, so no true copy is necessary to make an effective copy
            return second;
        } else if (secondSize == 0 && firstSize == 1) {
            //there is only one element in total, copy it into a singleton list
            return Collections.singletonList(first.get(0));
        } else if (firstSize == 0 && secondSize == 1) {
            //there is only one element in total, copy it into a singleton list
            return Collections.singletonList(second.get(0));
        }

        return concatArrayList(first, firstSize, second, secondSize);
    }

    /**
     * Concatenates the two given {@link List}s.
     * <p>
     * The returned {@link List} is a true copy of the input lists and is guaranteed to be mutable.
     *
     * @param first  the first {@link List}
     * @param second the second {@link List}
     * @return a new {@link List} containing the concatenated {@link List} elements
     */
    public static <E> List<E> concatCopy(@NonNull List<E> first, @NonNull List<E> second) {
        return concatArrayList(first, first.size(), second, second.size());
    }

    /**
     * Concatenates the two given {@link List}s.
     * <p>
     * The returned {@link List} is an effective copy of the input lists. It is guaranteed to be immutable, and <i>may</i> be identical to one
     * or both of the input arguments.
     *
     * @param first  the first {@link List}
     * @param second the second {@link List}
     * @return a {@link List} containing the concatenated {@link List} elements
     */
    public static <E> List<E> concatImmutable(@NonNull List<E> first, @NonNull List<E> second) {
        int firstSize = first.size();
        int secondSize = second.size();

        if (firstSize == 0 && secondSize == 0) {
            return Collections.emptyList();
        } else if (secondSize == 0 && isImmutable(first)) {
            //we can return the first list as-is: it's immutable, so no true copy is necessary to make an effective copy
            return first;
        } else if (firstSize == 0 && isImmutable(second)) {
            //we can return the second list as-is: it's immutable, so no true copy is necessary to make an effective copy
            return second;
        }

        return Collections.unmodifiableList(concatArrayList(first, firstSize, second, secondSize));
    }

    private static <E> ArrayList<E> concatArrayList(@NonNull List<E> first, int firstSize, @NonNull List<E> second, int secondSize) {
        ArrayList<E> result = new ArrayList<>(firstSize + secondSize);
        result.addAll(first);
        result.addAll(second);
        return result;
    }

    /**
     * Maps the elements in the given {@link List} to new values according to the given function, and returns a {@link List} containing the resulting values.
     * <p>
     * The returned {@link List} <i>may</i> be immutable.
     *
     * @param src    the {@link List} containing the original values
     * @param mapper the mapping function
     * @return the resulting {@link List}
     */
    public static <T, R> List<R> map(@NonNull List<? extends T> src, @NonNull Function<? super T, ? extends R> mapper) {
        return mapArrayList(src, mapper);
    }

    /**
     * Maps the elements in the given {@link List} to new values according to the given function, and returns a {@link List} containing the resulting values.
     * <p>
     * The returned {@link List} is guaranteed to be unique, mutable and random-access.
     *
     * @param src    the {@link List} containing the original values
     * @param mapper the mapping function
     * @return the resulting {@link List}
     */
    public static <T, R> List<R> mapMutable(@NonNull List<? extends T> src, @NonNull Function<? super T, ? extends R> mapper) {
        return mapArrayList(src, mapper);
    }

    /**
     * Maps the elements in the given {@link List} to new values according to the given function, and returns a {@link List} containing the resulting values.
     * <p>
     * The returned {@link List} is guaranteed to be immutable, and <i>may</i> be identical to the input list.
     *
     * @param src    the {@link List} containing the original values
     * @param mapper the mapping function
     * @return the resulting {@link List}
     */
    public static <T, R> List<R> mapImmutable(@NonNull List<? extends T> src, @NonNull Function<? super T, ? extends R> mapper) {
        return immutableMove(mapArrayList(src, mapper));
    }

    private static <T, R> ArrayList<R> mapArrayList(@NonNull List<? extends T> src, @NonNull Function<? super T, ? extends R> mapper) {
        ArrayList<R> result = new ArrayList<>(src.size());
        for (T value : src) {
            result.add(mapper.apply(value));
        }
        return result;
    }
}
