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
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.http.header.Header;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Like {@link java.util.Collections}, but for {@link HeaderMap}!
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class HeaderMaps {
    private final HeaderMap   EMPTY         = new Empty();
    private final Set<String> RESERVED_KEYS = new HashSet<>(Arrays.asList(
            "accept-encoding",
            "authorization",
            "content-encoding",
            "content-length",
            "content-type",
            "transfer-encoding"
    ));

    /**
     * @return an empty {@link HeaderMap}
     */
    public HeaderMap empty() {
        return EMPTY;
    }

    /**
     * Checks if a given header key is reserved (may not be manually set by the user for outgoing requests).
     *
     * @param key the header key to check
     * @return whether or not the given header key is reserved
     */
    public boolean isReserved(String key) {
        return key != null && RESERVED_KEYS.contains(key);
    }

    /**
     * An empty {@link HeaderMap}.
     *
     * @author DaPorkchop_
     */
    private final class Empty implements HeaderMap {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public Header get(int index) throws IndexOutOfBoundsException {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }

        @Override
        public String getKey(int index) throws IndexOutOfBoundsException {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }

        @Override
        public String getValue(int index) throws IndexOutOfBoundsException {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }

        @Override
        public List<String> getValues(int index) throws IndexOutOfBoundsException {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }

        @Override
        public Header get(@NonNull String key) {
            return null;
        }

        @Override
        public String getValue(@NonNull String key) {
            return null;
        }

        @Override
        public List<String> getValues(@NonNull String key) {
            return null;
        }

        @Override
        public boolean hasKey(@NonNull String key) {
            return false;
        }

        @Override
        public MutableHeaderMap mutableCopy() {
            return new MutableHeaderMapImpl();
        }

        @Override
        public void forEach(@NonNull Consumer<Header> callback) {
        }

        @Override
        public void forEach(@NonNull BiConsumer<String, String> callback) {
        }
    }
}
