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

package net.daporkchop.lib.dbextensions.defaults.map.key;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.db.container.map.KeyHasher;

/**
 * Hashes a key using {@link Object#hashCode()}
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectHashCodeKeyHasher<K> implements KeyHasher<K> {
    private static final ObjectHashCodeKeyHasher INSTANCE = new ObjectHashCodeKeyHasher();

    @SuppressWarnings("unchecked")
    public static <K> ObjectHashCodeKeyHasher<K> getInstance() {
        return (ObjectHashCodeKeyHasher<K>) INSTANCE;
    }

    @Override
    public byte[] hash(@NonNull K key) {
        int hash = key.hashCode();
        return new byte[]{
                (byte) (hash & 0xFF),
                (byte) ((hash >>> 8) & 0xFF),
                (byte) ((hash >>> 16) & 0xFF),
                (byte) ((hash >>> 24) & 0xFF)
        };
    }

    @Override
    public int getHashLength() {
        return 4;
    }
}
