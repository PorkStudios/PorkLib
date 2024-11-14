/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2024 DaPorkchop_
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

package net.daporkchop.lib.common.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Helper methods for working with {@link java.util.List Lists}.
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
                || Collections$UnmodifiableList.isInstance(list)
                || Collections$UnmodifiableRandomAccessList.isInstance(list);
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

    private static <E> ArrayList<E> concatArrayList(List<E> first, int firstSize, List<E> second, int secondSize) {
        ArrayList<E> result = new ArrayList<>(firstSize + secondSize);
        result.addAll(first);
        result.addAll(second);
        return result;
    }
}
