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
}
