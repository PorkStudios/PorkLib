/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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
     * Puts a new header into this map, or updates the value of an existing header if one with the given key already exists.
     *
     * @param key   the key of the header
     * @param value the value of the header
     * @return the previous value, or {@code null} if no previous value for the given key existed
     */
    default Header put(@NonNull String key, @NonNull String value)  {
        return this.put(Header.of(key, value));
    }

    /**
     * Puts a new header into this map, or updates the value of an existing header if one with the given key already exists.
     *
     * @param key   the key of the header
     * @param values the values of the header
     * @return the previous value, or {@code null} if no previous value for the given key existed
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
