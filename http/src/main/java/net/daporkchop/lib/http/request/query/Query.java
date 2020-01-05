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

package net.daporkchop.lib.http.request.query;

import lombok.NonNull;
import net.daporkchop.lib.http.HttpMethod;

import java.io.IOException;
import java.util.Map;

/**
 * Representation of an HTTP query.
 *
 * @author DaPorkchop_
 */
public interface Query {
    /**
     * @return the {@link HttpMethod} of the query
     */
    HttpMethod method();

    /**
     * @return the path of the query
     */
    String path();

    /**
     * @return the fragment of the query
     */
    String fragment();

    /**
     * Gets the value of a URL parameter with the given name.
     *
     * @param name the name of the URL parameter
     * @return the value of a URL parameter with the given name, or {@code null} if none was found
     */
    default String param(@NonNull String name) {
        return this.params().get(name);
    }

    /**
     * Gets the value of a URL parameter with the given name.
     *
     * @param name     the name of the URL parameter
     * @param fallback the value to return in case no parameter with the given name could be found
     * @return the value of a URL parameter with the given name, or the given fallback value if none was found
     */
    default String param(@NonNull String name, String fallback) {
        return this.params().getOrDefault(name, fallback);
    }

    /**
     * @return a {@link Map} containing the additional URL parameters
     */
    Map<String, String> params();

    /**
     * Identical to {@code encoded(true);}.
     *
     * @see #encoded(boolean)
     */
    default CharSequence encoded() {
        return this.encoded(true);
    }

    /**
     * @param computeIfAbsent whether or not to compute (and cache) the encoded path if not already cached
     * @return the encoded path, including URL parameters and fragment ID (if present), or {@code null} if computeIfAbsent is {@code false} and the encoded path is not cached
     */
    CharSequence encoded(boolean computeIfAbsent);

    /**
     * Appends the encoded path, including URL parameters and fragment ID (if present) to the given {@link Appendable}.
     * <p>
     * If the encoded path is cached, then it will be appended directly from the cache. Otherwise, this method will NOT cache it (it will be encoded directly
     * to the given {@link Appendable}).
     *
     * @param dst the {@link Appendable} to append the encoded path to
     * @throws IOException if an IO exception occurs you dummy
     */
    void appendEncoded(@NonNull Appendable dst) throws IOException;
}
