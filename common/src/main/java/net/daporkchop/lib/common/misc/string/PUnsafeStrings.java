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
import net.daporkchop.lib.common.system.PlatformInfo;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.nio.CharBuffer;
import java.util.regex.Matcher;

import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * Unsafe operations for {@link String}.
 * <p>
 * These mostly modify the contents of the string! This is very likely to break things unless used correctly.
 *
 * @author DaPorkchop_
 * @see PStrings
 * @deprecated this is horrible and should never have existed
 */
@UtilityClass
@Deprecated
public class PUnsafeStrings {
    static {
        if (PlatformInfo.JAVA_VERSION >= 9) {
            throw new AssertionError(PUnsafeStrings.class.getTypeName() + " is not supported on Java 9+ (and should not be used on any Java version for any reason anyway)");
        }

        new UnsupportedOperationException(PUnsafeStrings.class.getTypeName() + " is deprecated and will be removed in a future release").printStackTrace();
    }

    private final long STRING_VALUE_OFFSET = PUnsafe.pork_getOffset(String.class, "value");
    private final long STRING_HASH_OFFSET = PUnsafe.pork_getOffset(String.class, "hash");
    private final long ENUM_NAME_OFFSET = PUnsafe.pork_getOffset(Enum.class, "name");
    private final long STRINGBUILDER_VALUE_OFFSET = PUnsafe.pork_getOffset(classForName("java.lang.AbstractStringBuilder"), "value");

    private final long MATCHER_GROUPS_OFFSET = PUnsafe.pork_getOffset(Matcher.class, "groups");
    private final long MATCHER_TEXT_OFFSET = PUnsafe.pork_getOffset(Matcher.class, "text");

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
     * @see PStrings#titleFormatWord(char[])
     */
    public static void titleFormat(@NonNull String text) {
        titleFormat(unwrap(text));
        PUnsafe.putInt(text, STRING_HASH_OFFSET, 0);
    }

    /**
     * @deprecated replaced by {@link PStrings#titleFormatWord(char[])}
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
     * @deprecated replaced by {@link PStrings#immutableArrayToString(char[])}
     */
    public static String wrap(@NonNull char[] chars) {
        String s = PUnsafe.allocateInstance(String.class);
        PUnsafe.putObject(s, STRING_VALUE_OFFSET, chars);
        return s;
    }

    /**
     * @deprecated replaced by {@link PStrings#tryCharSequenceToImmutableArray(CharSequence)}
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
     * @deprecated replaced by {@link PStrings#stringToImmutableArray(String)}
     */
    public static char[] unwrap(@NonNull String string) {
        return PUnsafe.getObject(string, STRING_VALUE_OFFSET);
    }

    /**
     * @deprecated replaced by {@link PStrings#stringBuilderToImmutableArray(StringBuilder)}
     */
    public static char[] unwrap(@NonNull StringBuilder builder) {
        return PUnsafe.getObject(builder, STRINGBUILDER_VALUE_OFFSET);
    }

    /**
     * @deprecated replaced by {@link PStrings#stringBufferToImmutableArray(StringBuffer)}
     */
    public static char[] unwrap(@NonNull StringBuffer buffer) {
        return PUnsafe.getObject(buffer, STRINGBUILDER_VALUE_OFFSET);
    }

    /**
     * @deprecated replaced by {@link PStrings#subSequence(CharSequence, int, int)}
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
        int[] groups = PUnsafe.getObject(matcher, MATCHER_GROUPS_OFFSET);
        int start = groups[group << 1];
        int end = groups[(group << 1) + 1];
        if (start == -1 || end == -1) {
            return null;
        }
        return PUnsafe.<CharSequence>getObject(matcher, MATCHER_TEXT_OFFSET).subSequence(start, end);
    }
}
