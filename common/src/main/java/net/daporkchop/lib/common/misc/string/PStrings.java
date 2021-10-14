/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2021 DaPorkchop_
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

package net.daporkchop.lib.common.misc.string;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.util.PArrays;
import net.daporkchop.lib.common.util.PorkUtil;

import java.util.Arrays;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static net.daporkchop.lib.common.misc.string.PUnsafeStrings.*;
import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Questionably safe methods for working with {@link String} and {@link CharSequence}.
 *
 * @author DaPorkchop_
 * @see PUnsafeStrings for some very unsafe methods
 */
@UtilityClass
public class PStrings {
    public static StringGroup split(@NonNull String src, char delimiter) {
        return split(PUnsafeStrings.unwrap(src), delimiter);
    }

    public static StringGroup split(@NonNull char[] src, char delimiter) {
        final int length = src.length;
        List<char[]> list = new LinkedList<>(); //probably better performance-wise (due to O(1) add time in all cases)? benchmarks needed

        int off = 0;
        int next;
        while ((next = PArrays.linearSearch(src, off, length, delimiter)) != -1) {
            list.add(Arrays.copyOfRange(src, off, next));
            off = next + 1;
        }
        list.add(Arrays.copyOfRange(src, off, length));

        return new StringGroup(list.toArray(new char[list.size()][]));
    }

    public static String clone(@NonNull String src) {
        return wrap(PUnsafeStrings.unwrap(src).clone());
    }

    /**
     * Quickly appends the same character to the given {@link StringBuilder} multiple times.
     *
     * @param builder the {@link StringBuilder} to append the characters to
     * @param c       the character to append
     * @param count   the number of times to append the character
     */
    public static void appendMany(@NonNull StringBuilder builder, char c, int count) {
        if (notNegative(count, "count") == 0) {
            return;
        }
        int initialLength = builder.length();
        builder.setLength(initialLength + count);
        Arrays.fill(PUnsafeStrings.unwrap(builder), initialLength, initialLength + count, c);
    }

    /**
     * A much faster alternative to {@link String#format(String, Object...)}, by simply replacing all occurrences of {@code %s}
     * with the {@link Objects#toString(Object)} value of the object.
     *
     * @param template the {@link String} to apply the formatting to
     * @param args     the arguments to the formatter
     * @return a {@link String} containing the formatted text
     */
    public static String lightFormat(@NonNull String template, Object... args) {
        return wrap(lightFormat(PUnsafeStrings.unwrap(template), args));
    }

    /**
     * A much faster alternative to {@link String#format(String, Object...)}, by simply replacing all occurrences of {@code %s}
     * with the {@link Objects#toString(Object)} value of the object.
     *
     * @param template the {@code char[]} to apply the formatting to
     * @param args     the arguments to the formatter
     * @return a {@code char[]} containing the formatted text
     */
    public static char[] lightFormat(@NonNull char[] template, Object... args) {
        if (args == null) {
            args = PorkUtil.EMPTY_OBJECT_ARRAY;
        }

        try (Handle<StringBuilder> handle = PorkUtil.STRINGBUILDER_POOL.get()) {
            StringBuilder builder = handle.get();
            builder.setLength(0);

            for (int i = 0, length = template.length, j = 0; i < length; i++) {
                char c = template[i];
                if (c == '%' && i + 1 < length && template[i + 1] == 's') {
                    builder.append(Objects.toString(j < args.length ? args[j++] : null));
                    i++;
                } else {
                    builder.append(c);
                }
            }

            return Arrays.copyOf(PUnsafeStrings.unwrap(builder), builder.length());
        }
    }

    /**
     * A faster alternative to {@link String#format(String, Object...)}, by caching the instance of {@link StringBuilder} used
     * internally.
     *
     * @param template the {@link String} to apply the formatting to
     * @param args     the arguments to the formatter
     * @return a {@link String} containing the formatted text
     */
    public static String fastFormat(@NonNull String template, Object... args) {
        try (Handle<StringBuilder> handle = PorkUtil.STRINGBUILDER_POOL.get()) {
            StringBuilder builder = handle.get();
            builder.setLength(0);

            new Formatter(builder, Locale.US).format(template, args);

            return builder.toString();
        }
    }
}
