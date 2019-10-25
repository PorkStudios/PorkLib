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

package net.daporkchop.lib.http;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * An HTTP request.
 *
 * @author DaPorkchop_
 */
public interface Request {
    /**
     * @return the type of request.
     */
    RequestType type();

    /**
     * Gets the query line of the request.
     * <p>
     * All characters must be valid ASCII glyphs, otherwise the behavior is undefined.
     *
     * @return the query line of the request
     */
    String query();

    /**
     * Runs a callback function across each header on the request.
     *
     * @param callback the callback function to run
     */
    void forEachHeader(@NonNull BiConsumer<String, String> callback);

    /**
     * A simple implementation of {@link Request}.
     *
     * @author DaPorkchop_
     */
    @RequiredArgsConstructor
    @Getter
    @Accessors(fluent = true)
    final class Simple implements Request {
        @NonNull
        protected final RequestType type;
        @NonNull
        protected final String query;
        @NonNull
        protected final Map<String, String> headers;

        @Override
        public void forEachHeader(@NonNull BiConsumer<String, String> callback) {
            this.headers.forEach(callback);
        }
    }
}
