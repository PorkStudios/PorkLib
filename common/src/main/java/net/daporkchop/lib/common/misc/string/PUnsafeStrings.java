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
import net.daporkchop.lib.unsafe.PUnsafe;

import java.nio.CharBuffer;
import java.util.regex.Matcher;

import static net.daporkchop.lib.common.util.PorkUtil.*;
import static net.daporkchop.lib.unsafe.PUnsafe.*;

/**
 * Unsafe operations for {@link String}.
 * <p>
 * These mostly modify the contents of the string! This is very likely to break things unless used correctly.
 *
 * @author DaPorkchop_
 * @see PStrings
 */
@UtilityClass
public class PUnsafeStrings {
    protected final long STRING_VALUE_OFFSET = pork_getOffset(String.class, "value");
    protected final long STRING_HASH_OFFSET = pork_getOffset(String.class, "hash");
    protected final long ENUM_NAME_OFFSET = pork_getOffset(Enum.class, "name");
    protected final long STRINGBUILDER_VALUE_OFFSET = pork_getOffset(classForName("java.lang.AbstractStringBuilder"), "value");

    protected final long MATCHER_GROUPS_OFFSET = pork_getOffset(Matcher.class, "groups");
    protected final long MATCHER_TEXT_OFFSET = pork_getOffset(Matcher.class, "text");

    /**
     * Sets the value of {@link Enum#name()} for an {@link Enum} value.
     *
     * @param value the {@link Enum} value to set the name of
     * @param name  the new name to use
     * @param <E>   the enum type
     */
    public static <E extends Enum<E>> void setEnumName(@NonNull E value, @NonNull String name) {
        PUnsafe.putObject(value, ENUM_NAME_OFFSET, name);
    }

    /**
     * Replaces all occurrences of the given {@code char} in the given {@link String} with another {@code char}.
     *
     * @see #replace(char[], char, char)
     */
    public static void replace(@NonNull String text, char find, char replace) {
        replace(unwrap(text), find, replace);
        PUnsafe.putInt(text, STRING_HASH_OFFSET, 0);
    }

    /**
     * Replaces all occurrences of the given {@code char} in the given {@code char[]} with another {@code char}.
     *
     * @param text    the {@code char[]} to modify
     * @param find    the {@code char} to find
     * @param replace the {@code char} to use as a replacement
     */
    public static void replace(@NonNull char[] text, char find, char replace) {
        final int length = text.length;

        for (int i = 0; i < length; i++) {
            if (text[i] == find) {
                text[i] = replace;
            }
        }
    }

    /**
     * Applies title formatting to the given {@link String}.
     *
     * @see #titleFormat(char[])
     */
    public static void titleFormat(@NonNull String text) {
        titleFormat(unwrap(text));
        PUnsafe.putInt(text, STRING_HASH_OFFSET, 0);
    }

    /**
     * Applies title formatting to the given {@code char[]}.
     * <p>
     * A "title formatted" string starts with one upper-case letter, all following letters are lower-case.
     *
     * @param text the {@code char[]} to apply title formatting to
     */
    public static void titleFormat(@NonNull char[] text) {
        final int length = text.length;

        if (length > 0) {
            text[0] = Character.toUpperCase(text[0]);

            for (int i = 1; i < length; i++) {
                text[i] = Character.toLowerCase(text[i]);
            }
        }
    }

    /**
     * Wraps a {@code char[]} into a {@link String} without copying the array.
     *
     * @param chars the {@code char[]} to wrap
     * @return a new {@link String}
     */
    public static String wrap(@NonNull char[] chars) {
        String s = PUnsafe.allocateInstance(String.class);
        PUnsafe.putObject(s, STRING_VALUE_OFFSET, chars);
        return s;
    }

    /**
     * Unwraps a {@link CharSequence} into a {@code char[]} without copying the array, if possible.
     * <p>
     * Be aware that the returned {@code char[]} may be larger than the actual size of the {@link CharSequence}. It is therefore strongly advised to use
     * {@link CharSequence#length()} instead of {@code char[]#length}.
     *
     * @param seq the {@link CharSequence} to unwrap
     * @return the value of the {@link CharSequence} as a {@code char[]}, or {@code null} if the given {@link CharSequence} cannot be unwrapped
     */
    public static char[] tryUnwrap(@NonNull CharSequence seq) {
        if (seq instanceof String) {
            return PUnsafe.getObject(seq, STRING_VALUE_OFFSET);
        } else if (seq instanceof StringBuilder || seq instanceof StringBuffer) {
            return PUnsafe.getObject(seq, STRINGBUILDER_VALUE_OFFSET);
        } else {
            return null;
        }
    }

    /**
     * Unwraps a {@link String} into a {@code char[]} without copying the array.
     *
     * @param string the {@link String} to unwrap
     * @return the value of the {@link String} as a {@code char[]}
     */
    public static char[] unwrap(@NonNull String string) {
        return PUnsafe.getObject(string, STRING_VALUE_OFFSET);
    }

    /**
     * Unwraps a {@link StringBuilder} into a {@code char[]} without copying the array.
     * <p>
     * Be aware that the returned {@code char[]} may be larger than the actual size of the {@link StringBuilder}. It is therefore strongly advised to use
     * {@link StringBuilder#length()} instead of {@code char[]#length}.
     *
     * @param builder the {@link StringBuilder} to unwrap
     * @return the value of the {@link StringBuilder} as a {@code char[]}
     */
    public static char[] unwrap(@NonNull StringBuilder builder) {
        return PUnsafe.getObject(builder, STRINGBUILDER_VALUE_OFFSET);
    }

    /**
     * Unwraps a {@link StringBuffer} into a {@code char[]} without copying the array.
     * <p>
     * Be aware that the returned {@code char[]} may be larger than the actual size of the {@link StringBuffer}. It is therefore strongly advised to use
     * {@link StringBuffer#length()} instead of {@code char[]#length}.
     *
     * @param buffer the {@link StringBuffer} to unwrap
     * @return the value of the {@link StringBuffer} as a {@code char[]}
     */
    public static char[] unwrap(@NonNull StringBuffer buffer) {
        return PUnsafe.getObject(buffer, STRINGBUILDER_VALUE_OFFSET);
    }

    /**
     * An alternative to {@link CharSequence#subSequence(int, int)} that can be faster for certain {@link CharSequence} implementations.
     *
     * @param seq   the {@link CharSequence} to get a subsequence of
     * @param start the first index, inclusive
     * @param end   the last index, exclusive
     * @return a subsequence of the given range of the given {@link CharSequence}
     * @see CharSequence#subSequence(int, int)
     */
    public static CharSequence subSequence(@NonNull CharSequence seq, int start, int end) {
        if (start == 0 && end == seq.length()) {
            return seq;
        }
        char[] arr = tryUnwrap(seq);
        return arr != null ? CharBuffer.wrap(arr, start, end - start) : seq.subSequence(start, end);
    }

    /**
     * Faster alternative to {@link Matcher#group(int)}.
     *
     * @param matcher the {@link Matcher}
     * @param group   the group index
     * @return the group's text, or {@code null} if the group didn't match
     * @see Matcher#group(int)
     */
    public static CharSequence fastGroup(@NonNull Matcher matcher, int group) {
        matcher.start(); //this does a < 0 check internally
        if (group < 0 || group > matcher.groupCount()) {
            throw new IndexOutOfBoundsException("No group " + group);
        }
        int[] groups = getObject(matcher, MATCHER_GROUPS_OFFSET);
        int start = groups[group << 1];
        int end = groups[(group << 1) + 1];
        if (start == -1 || end == -1) {
            return null;
        }
        return PUnsafe.<CharSequence>getObject(matcher, MATCHER_TEXT_OFFSET).subSequence(start, end);
    }
}
