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

package net.daporkchop.lib.common.misc.string;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.system.PlatformInfo;
import net.daporkchop.lib.common.util.PArrays;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * Questionably safe methods for working with {@link String} and {@link CharSequence}.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PStrings {
    private final long J8_STRING_VALUE_OFFSET = PlatformInfo.JAVA_VERSION > 8 ? -1L : PUnsafe.pork_getOffset(String.class, "value");
    private final long J8_ABSTRACTSTRINGBUILDER_VALUE_OFFSET = PlatformInfo.JAVA_VERSION > 8 ? -1L : PUnsafe.pork_getOffset(classForName("java.lang.AbstractStringBuilder"), "value");

    /**
     * Wraps the given {@code char[]} into a {@link String}.
     * <p>
     * The {@code char[]}'s contents are expected to be immutable. If they are modified after this method returns, the behavior is undefined.
     *
     * @param text the {@code char[]} to be wrapped
     * @return a {@link String}
     */
    public static String immutableArrayToString(@NonNull char[] text) {
        if (J8_STRING_VALUE_OFFSET >= 0L) { //unsafe constructor is supported
            String string = PUnsafe.allocateInstance(String.class);
            PUnsafe.putObject(string, J8_STRING_VALUE_OFFSET, text);
            return string;
        } else { //unsafe constructor isn't supported, fall back to regular constructor
            return String.valueOf(text);
        }
    }

    /**
     * Wraps the given {@code char[]} into a {@link CharSequence}.
     * <p>
     * The {@code char[]}'s contents are expected to be immutable. If they are modified after this method returns, the behavior is undefined.
     *
     * @param text the {@code char[]} to be wrapped
     * @return a {@link CharSequence}
     */
    public static CharSequence immutableArrayToCharSequence(@NonNull char[] text) {
        if (J8_STRING_VALUE_OFFSET >= 0L) { //unsafe constructor is supported, we can immediately make it a string with no overhead
            String string = PUnsafe.allocateInstance(String.class);
            PUnsafe.putObject(string, J8_STRING_VALUE_OFFSET, text);
            return string;
        } else { //unsafe constructor isn't supported, fall back to wrapping it in a CharBuffer (which will avoid creating any copies)
            return CharBuffer.wrap(text);
        }
    }

    /**
     * Unwraps the given {@link String} into an immutable {@code char[]}.
     * <p>
     * If the {@code char[]}'s contents are modified at any time, the behavior is undefined.
     *
     * @param text the {@link String} to be unwrapped
     * @return an immutable {@code char[]} containing the {@link String}'s contents. Note that the array may be longer than the original {@link String} was.
     */
    public static char[] stringToImmutableArray(@NonNull String text) {
        if (J8_STRING_VALUE_OFFSET >= 0L) { //we can unsafely access the array without copying using unsafe
            return PUnsafe.getObject(text, J8_STRING_VALUE_OFFSET);
        } else { //we can't unwrap using unsafe, fall back to regular java
            return text.toCharArray();
        }
    }

    /**
     * Unwraps the given {@link StringBuilder} into an immutable {@code char[]}.
     * <p>
     * If the {@code char[]}'s contents are modified at any time, the behavior is undefined. Subsequent modifications to the {@link StringBuilder} <i>may</i> be visible
     * in the {@code char[]}.
     *
     * @param builder the {@link StringBuilder} to be unwrapped
     * @return an immutable {@code char[]} containing the {@link StringBuilder}'s contents. Note that the array may be longer than the original {@link StringBuilder} was.
     */
    public static char[] stringBuilderToImmutableArray(@NonNull StringBuilder builder) {
        if (J8_ABSTRACTSTRINGBUILDER_VALUE_OFFSET >= 0L) { //we can unsafely access the array without copying using unsafe
            return PUnsafe.getObject(builder, J8_ABSTRACTSTRINGBUILDER_VALUE_OFFSET);
        } else { //we can't unwrap using unsafe, fall back to regular java
            char[] arr = new char[builder.length()];
            for (int i = 0; i < builder.length(); i++) {
                arr[i] = builder.charAt(i);
            }
            return arr;
        }
    }

    /**
     * Unwraps the given {@link StringBuffer} into an immutable {@code char[]}.
     * <p>
     * If the {@code char[]}'s contents are modified at any time, the behavior is undefined. Subsequent modifications to the {@link StringBuffer} <i>may</i> be visible
     * in the {@code char[]}.
     *
     * @param buffer the {@link StringBuffer} to be unwrapped
     * @return an immutable {@code char[]} containing the {@link StringBuffer}'s contents. Note that the array may be longer than the original {@link StringBuffer} was.
     */
    public static char[] stringBufferToImmutableArray(@NonNull StringBuffer buffer) {
        if (J8_ABSTRACTSTRINGBUILDER_VALUE_OFFSET >= 0L) { //we can unsafely access the array without copying using unsafe
            return PUnsafe.getObject(buffer, J8_ABSTRACTSTRINGBUILDER_VALUE_OFFSET);
        } else { //we can't unwrap using unsafe, fall back to regular java
            //StringBuffer synchronizes on EVERY method call, so converting to an array like this is probably substantially faster even if it creates
            // an unnecessary extra copy
            return stringToImmutableArray(buffer.toString());
        }
    }

    /**
     * Tries to unwrap the given {@link CharSequence} into an immutable {@code char[]}, if possible and there would be any performance advantage from doing so.
     * <p>
     * If the {@code char[]}'s contents are modified at any time, the behavior is undefined. Subsequent modifications to the {@link CharSequence} <i>may</i> be visible
     * in the {@code char[]}.
     *
     * @param sequence the {@link CharSequence} to be unwrapped
     * @return an immutable {@code char[]} containing the {@link CharSequence}'s contents. Note that the array may be longer than the original {@link CharSequence} was.
     */
    public static Optional<char[]> tryCharSequenceToImmutableArray(CharSequence sequence) {
        if (PlatformInfo.JAVA_VERSION >= 9) { //all of the fast extraction operations are impossible on newer Java versions
            return Optional.empty();
        }

        if (sequence instanceof String) {
            return Optional.of(stringToImmutableArray((String) sequence));
        } else if (sequence instanceof StringBuilder) {
            return Optional.of(stringBuilderToImmutableArray((StringBuilder) sequence));
        } else if (sequence instanceof StringBuffer) {
            return Optional.of(stringBufferToImmutableArray((StringBuffer) sequence));
        } else {
            //no fast unwrapping operations are supported for the given implementation, and we can't assume that converting to a String
            // and then unwrapping would be any faster as we don't know what the implementation is
            return Optional.empty();
        }
    }

    public static StringGroup split(@NonNull String src, char delimiter) {
        return split(stringToImmutableArray(src), delimiter);
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

    /**
     * @deprecated this is horrible and should never have existed
     */
    @Deprecated
    public static String clone(@NonNull String src) {
        return immutableArrayToString(stringToImmutableArray(src).clone());
    }

    /**
     * Drop-in alternative to {@link CharSequence#subSequence(int, int)} that can be faster for certain {@link CharSequence} implementations.
     *
     * @param sequence the {@link CharSequence} to get a subsequence of
     * @param start    the first index, inclusive
     * @param end      the last index, exclusive
     * @return a subsequence of the given range of the given {@link CharSequence}
     * @see CharSequence#subSequence(int, int)
     */
    public static CharSequence subSequence(@NonNull CharSequence sequence, int start, int end) {
        if (start == 0 && end == sequence.length()) {
            return sequence;
        }

        char[] unwrapped = tryCharSequenceToImmutableArray(sequence).orElse(null);
        if (unwrapped != null) { //we were able to unwrap the sequence, wrap it in a CharBuffer (which will avoid creating any copies)
            return CharBuffer.wrap(unwrapped, start, end - start);
        } else { //fall back to regular subSequence method
            return sequence.subSequence(start, end);
        }
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

        builder.ensureCapacity(builder.length() + count);
        for (int i = 0; i < count; i++) {
            builder.append(c);
        }
    }

    /**
     * A much faster alternative to {@link String#format(String, Object...)}, by simply replacing all occurrences of {@code %s}
     * with the {@link Objects#toString(Object)} value of the object.
     *
     * @param template the {@link String} to apply the formatting to
     * @param args     the arguments to the formatter
     * @return a {@link String} containing the formatted text
     */
    public static String lightFormat(@NonNull CharSequence template, Object... args) {
        if (args == null) {
            args = PorkUtil.EMPTY_OBJECT_ARRAY;
        }

        try (Handle<StringBuilder> handle = PorkUtil.STRINGBUILDER_POOL.get()) {
            StringBuilder builder = handle.get();
            builder.setLength(0);

            for (int i = 0, length = template.length(), j = 0; i < length; i++) {
                char c = template.charAt(i);
                if (c == '%' && i + 1 < length && template.charAt(i + 1) == 's') {
                    builder.append(j < args.length ? args[j++] : null);
                    i++;
                } else {
                    builder.append(c);
                }
            }

            return builder.toString();
        }
    }

    /**
     * A much faster alternative to {@link String#format(String, Object...)}, by simply replacing all occurrences of {@code %s}
     * with the {@link Objects#toString(Object)} value of the object.
     *
     * @param template the {@code char[]} to apply the formatting to
     * @param args     the arguments to the formatter
     * @return a {@code char[]} containing the formatted text
     * @deprecated this adds pretty much no value whatsoever
     */
    @Deprecated
    public static char[] lightFormat(@NonNull char[] template, Object... args) {
        return lightFormat(immutableArrayToCharSequence(template), args).toCharArray();
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

    /**
     * Applies title formatting to the given {@code char[]}, under the assumption that it is a single word.
     * <p>
     * A "title formatted" string starts with one upper-case letter, all following letters are lower-case.
     *
     * @param text the {@code char[]} to apply title formatting to
     */
    public static void titleFormatWord(@NonNull char[] text) {
        titleFormatWord(text, 0, text.length);
    }

    /**
     * Applies title formatting to the given {@code char[]}, under the assumption that it is a single word.
     * <p>
     * A "title formatted" string starts with one upper-case letter, all following letters are lower-case.
     *
     * @param text the {@code char[]} containing the word to apply title formatting to
     * @param off  the offset of the sub-region of the array containing the word
     * @param len  the length of the sub-region of the array containing the word
     */
    public static void titleFormatWord(@NonNull char[] text, int off, int len) {
        checkRangeLen(text.length, off, len);

        if (len == 0) { //do nothing
            return;
        }

        //make first letter upper-case
        text[off + 0] = Character.toUpperCase(text[off + 0]);

        //make all subsequent letters lower-case
        for (int i = 1; i < len; i++) {
            text[off + i] = Character.toLowerCase(text[off + i]);
        }
    }
}
