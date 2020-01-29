/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.common.misc.string;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.PUnsafe;

/**
 * Unsafe operations for {@link String}.
 * <p>
 * These mostly modify the contents of the string! This is very likely to break things unless used correctly.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PUnsafeStrings {
    protected final long STRING_HASH_OFFSET = PUnsafe.pork_getOffset(String.class, "hash");
    protected final long ENUM_NAME_OFFSET   = PUnsafe.pork_getOffset(Enum.class, "name");

    /**
     * Sets the value of {@link Enum#name()} for an {@link Enum} value.
     *
     * @param value the {@link Enum} value to set the name of
     * @param name  the new name to use
     * @param <E>   the enum type
     */
    public <E extends Enum<E>> void setEnumName(@NonNull E value, @NonNull String name) {
        PUnsafe.putObject(value, ENUM_NAME_OFFSET, name);
    }

    /**
     * Replaces all occurrences of the given {@code char} in the given {@link String} with another {@code char}.
     *
     * @see #replace(char[], char, char)
     */
    public void replace(@NonNull String text, char find, char replace) {
        replace(PorkUtil.unwrap(text), find, replace);
        PUnsafe.putInt(text, STRING_HASH_OFFSET, 0);
    }

    /**
     * Replaces all occurrences of the given {@code char} in the given {@code char[]} with another {@code char}.
     *
     * @param text    the {@code char[]} to modify
     * @param find    the {@code char} to find
     * @param replace the {@code char} to use as a replacement
     */
    public void replace(@NonNull char[] text, char find, char replace) {
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
    public void titleFormat(@NonNull String text) {
        titleFormat(PorkUtil.unwrap(text));
        PUnsafe.putInt(text, STRING_HASH_OFFSET, 0);
    }

    /**
     * Applies title formatting to the given {@code char[]}.
     * <p>
     * A "title formatted" string starts with one upper-case letter, all following letters are lower-case.
     *
     * @param text the {@code char[]} to apply title formatting to
     */
    public void titleFormat(@NonNull char[] text) {
        final int length = text.length;

        if (length > 0) {
            text[0] = Character.toUpperCase(text[0]);

            for (int i = 1; i < length; i++) {
                text[i] = Character.toLowerCase(text[i]);
            }
        }
    }
}
