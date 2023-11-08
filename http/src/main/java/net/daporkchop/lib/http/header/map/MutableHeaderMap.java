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

package net.daporkchop.lib.http.header.map;

import lombok.NonNull;
import net.daporkchop.lib.http.header.Header;

import java.util.List;
import java.util.function.Consumer;

/**
 * A {@link HeaderMap} that is mutable (i.e. headers may be added and removed as needed).
 *
 * @author DaPorkchop_
 */
//TODO: more methods? such as replace or add
public interface MutableHeaderMap extends HeaderMap {
    @Override
    default HeaderMap snapshot() {
        return new HeaderSnapshot(this);
    }

    /**
     * Puts a new header into this map, or replaces an existing header if one with the given key already exists.
     *
     * @param key   the key of the header
     * @param value the value of the header
     * @return the previous value, or {@code null} if no previous body for the given key existed
     */
    default Header put(@NonNull String key, @NonNull String value)  {
        return this.put(Header.of(key, value));
    }

    /**
     * Puts a new header into this map, or replaces an existing header if one with the given key already exists.
     *
     * @param key   the key of the header
     * @param values the values of the header
     * @return the previous value, or {@code null} if no previous body for the given key existed
     */
    default Header put(@NonNull String key, @NonNull List<String> values)  {
        return this.put(Header.of(key, values));
    }

    /**
     * Puts a new header into this map, or replaces an existing header if one with the given key already exists.
     *
     * @param header the header
     * @return the previous header with the given key, or {@code null} if no previous header with the given key existed
     */
    Header put(@NonNull Header header);

    /**
     * Puts all headers from the source map into this map.
     *
     * @param source the source to copy the headers from
     */
    default void putAll(@NonNull HeaderMap source) {
        source.forEach((Consumer<Header>) this::put);
    }

    /**
     * Adds a new header to this map, or adds the value to an existing header if one with the given key already exists.
     *
     * @param key   the key of the header
     * @param value the value to add to the header
     */
    default void add(@NonNull String key, @NonNull String value)  {
        this.add(Header.of(key, value));
    }

    /**
     * Puts a new header into this map, or adds the value to an existing header if one with the given key already exists.
     *
     * @param key   the key of the header
     * @param values the values to add to the header
     */
    default void add(@NonNull String key, @NonNull List<String> values)  {
        this.add(Header.of(key, values));
    }

    /**
     * Puts a new header into this map, or adds the value to an existing header if one with the given key already exists.
     *
     * @param header the header whose values to add
     */
    void add(@NonNull Header header);

    /**
     * Removes a header from this map.
     *
     * @param key the key of the header
     * @return the removed value, or {@code null} if no header with the given key existed
     */
    Header remove(@NonNull String key);

    /**
     * Removes the header at the given index from this map.
     *
     * @param index the index of the header to remove
     * @return the removed header
     * @throws IndexOutOfBoundsException if the given index is out of bounds
     */
    Header remove(int index) throws IndexOutOfBoundsException;
}
