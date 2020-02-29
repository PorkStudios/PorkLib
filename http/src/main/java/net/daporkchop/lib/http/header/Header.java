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

package net.daporkchop.lib.http.header;

import lombok.NonNull;

import java.util.List;

/**
 * A single HTTP header.
 * <p>
 * Implementations of this class are expected to have both {@link #hashCode()} and {@link #equals(Object)} check for case-insensitive equality between
 * both keys and values.
 *
 * @author DaPorkchop_
 */
public interface Header {
    /**
     * Creates a new {@link Header} with the given key and value.
     *
     * @param key   the header's key
     * @param value the header's value
     * @return a new {@link Header} with the given key and value
     */
    static Header of(@NonNull String key, @NonNull String value) {
        return new SingletonHeaderImpl(key, value);
    }

    /**
     * Creates a new {@link Header} with the given key and values.
     *
     * @param key    the header's key
     * @param values the header's values
     * @return a new {@link Header} with the given key and values
     */
    static Header of(@NonNull String key, @NonNull List<String> values) {
        switch (values.size()) {
            case 0:
                throw new IllegalArgumentException("values list is empty!");
            case 1:
                return of(key, values.get(0));
            default:
                return new MultiHeaderImpl(key, values);
        }
    }

    /**
     * Gets a {@link Header} instance with the same value which is guaranteed to be immutable.
     *
     * @param header the header to make immutable
     * @return a {@link Header} instance with the same value which is guaranteed to be immutable
     */
    static Header immutable(@NonNull Header header) {
        if (header instanceof SingletonHeaderImpl || header instanceof MultiHeaderImpl) {
            return header;
        } else {
            return of(header.key(), header.values());
        }
    }

    /**
     * @return the key (name) of the HTTP header
     */
    String key();

    /**
     * @return the raw value of the HTTP header
     */
    String value();

    /**
     * @return a {@link List} containing all the values of the HTTP header
     */
    List<String> values();

    /**
     * @return whether or not this {@link Header} is a singleton (contains only one value)
     */
    default boolean singleton() {
        return this.values().size() == 1;
    }
}
