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

import java.util.function.BiConsumer;

/**
 * A specialized map for storing HTTP headers.
 * <p>
 * Note that as specified in RFC2616§4.2, keys are case-insensitive. Implementations may choose to remember the capitalization of keys passed to any
 * methods in this interface, or may modify the capitalization in any way they desire. In other words: don't depend on the capitalization of keys to
 * be preserved!
 * <p>
 * Unless otherwise explicitly stated, implementations make no guarantees as to thread-safety or concurrency.
 *
 * @author DaPorkchop_
 * @see MutableHeaderMap
 * @see DefaultHeaderMap
 */
public interface HeaderMap {

    /**
     * @return the number of headers in this map
     */
    int size();

    /**
     * @return whether or not this map is empty
     */
    default boolean isEmpty() {
        return this.size() == 0;
    }

    /**
     * Gets the header value for the given key.
     *
     * @param key the key of the header to get
     * @return the header value for the given key, or {@code null} if the requested key does not exist
     */
    String get(@NonNull String key);

    /**
     * Checks if a header with the given key exists.
     *
     * @param key the key to check for
     * @return whether or not a header with the given key exists
     */
    boolean has(@NonNull String key);

    /**
     * Iterates over all the headers in this map, passing each of them to the given callback function.
     *
     * @param callback the function to run
     */
    void forEach(@NonNull BiConsumer<? super String, ? super String> callback);
}
