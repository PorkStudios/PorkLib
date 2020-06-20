/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
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

package net.daporkchop.lib.minecraft.util.property;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.minecraft.item.ItemMeta;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A key used to identify an {@link ItemMeta} value.
 *
 * @param <T> the type of value referenced by this key
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public abstract class PropertyKey<T> implements Comparable<PropertyKey<?>> {
    @NonNull
    protected final String name;
    protected final T defaultValue;

    @Override
    public int compareTo(PropertyKey<?> o) {
        return this.name.compareTo(o.name);
    }

    /**
     * Checks whether or not the given value is considered to be unset.
     *
     * @param value the value to check
     * @return whether or not the given value is considered to be unset
     */
    public abstract boolean isSet(T value);

    /**
     * Pre-processes a value that is about to be set.
     * <p>
     * This allows a key to e.g. clamp a value to a specified range.
     *
     * @param value the value to process
     * @return the processed value. If {@code null}, the key will be removed
     */
    public T process(T value) {
        return this.isSet(value) ? value : null;
    }

    /**
     * Appends the given value as an entry to the given {@link StringBuilder}.
     * <p>
     * The value is guaranteed to be considered "set" as defined by {@link #isSet(Object)}.
     *
     * @param builder the {@link StringBuilder} to append to
     * @param value   the value to append
     */
    public void append(@NonNull StringBuilder builder, @NonNull T value) {
        builder.append(this.name).append('=').append(value).append(',').append(' ');
    }
}
