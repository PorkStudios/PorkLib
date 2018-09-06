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

package net.daporkchop.lib.db.object.key;

import lombok.NonNull;

/**
 * @author DaPorkchop_
 */
public abstract class KeyHasher<K> {
    /**
     * Checks if this key hasher can reconstruct a key from a hash
     *
     * @return whether or not this key hasher can reconstruct a key from a hash
     */
    public abstract boolean canGetKeyFromHash();

    /**
     * Reconstructs a key from a given hash.
     *
     * @param hash the hash
     * @return an instance of a key based on the hash
     */
    public K getKeyFromHash(@NonNull byte[] hash) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the number of bytes used for a single key hash
     *
     * @return the number of bytes used for a single key hash
     */
    public abstract int getKeyLength();

    /**
     * Hashes a key, and writes it to a byte array
     *
     * @param key  the key to hash
     * @param hash the byte array to write the hash to. this array is as long as getKeyLength()
     */
    public abstract void hash(@NonNull K key, @NonNull byte[] hash);
}
