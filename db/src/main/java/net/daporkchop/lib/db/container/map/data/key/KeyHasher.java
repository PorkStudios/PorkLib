/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

package net.daporkchop.lib.db.container.map.data.key;

import lombok.Getter;
import lombok.NonNull;

/**
 * @author DaPorkchop_
 */
public interface KeyHasher<K> {
    byte[] hash(@NonNull K key);

    int getHashLength();

    default boolean canReconstructFromHash()    {
        return false;
    }

    default K reconstructFromHash(@NonNull byte[] hash) {
        throw new UnsupportedOperationException("reconstruct from hash");
    }

    abstract class BaseKeyHasher<K> implements KeyHasher<K> {
        @Getter
        private final int hashLength;
        private final ThreadLocal<byte[]> hashCache;

        public BaseKeyHasher(int hashLength) {
            if (hashLength <= 0) {
                throw new IllegalArgumentException(String.format("Invalid hash length: %d", hashLength));
            }
            this.hashLength = hashLength;
            this.hashCache = ThreadLocal.withInitial(() -> new byte[hashLength]);
        }

        @Override
        public byte[] hash(@NonNull K key) {
            byte[] b = this.hashCache.get();
            this.doHash(key, b);
            return b;
        }

        protected abstract void doHash(@NonNull K key, @NonNull byte[] b);
    }
}
