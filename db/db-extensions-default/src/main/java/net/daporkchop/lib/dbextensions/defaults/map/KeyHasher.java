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

package net.daporkchop.lib.dbextensions.defaults.map;

import lombok.Getter;
import lombok.NonNull;

/**
 * A key hasher can take an instance of a key and hash it to a constant-length byte array.
 *
 * @author DaPorkchop_
 */
public interface KeyHasher<K> {
    /**
     * Hashes a key
     *
     * @param key the key to hash
     * @return the hash
     */
    byte[] hash(@NonNull K key);

    /**
     * Get the size of the hash (in bytes)
     *
     * @return the size of the hash (in bytes)
     */
    int getHashLength();

    /**
     * Checks if this key hasher can reconstruct a key instance from a hash.
     * <p>
     * If {@code true}, then {@link #reconstructFromHash(byte[])} must be implemented.
     *
     * @return whether or not this key hasher can reconstruct a key instance from a hash
     */
    default boolean canReconstructFromHash() {
        return false;
    }

    /**
     * Reconstructs a key from a hash
     *
     * @param hash the hash
     * @return a new instance of a key
     */
    default K reconstructFromHash(@NonNull byte[] hash) {
        throw new UnsupportedOperationException("reconstruct from hash");
    }

    /**
     * A key hasher that reuses its hash
     *
     * @param <K> the key type
     */
    abstract class ThreadLocalKeyHasher<K> implements KeyHasher<K> {
        @Getter
        private final int hashLength;
        private final ThreadLocal<byte[]> hashCache;

        public ThreadLocalKeyHasher(int hashLength) {
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

        /**
         * Hashes a key
         *
         * @param key the key to hash
         * @param b   the byte array that the hash is to be written to
         */
        protected abstract void doHash(@NonNull K key, @NonNull byte[] b);
    }
}
