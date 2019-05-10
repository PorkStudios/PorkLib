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

package net.daporkchop.lib.db.builder;

import lombok.NonNull;
import net.daporkchop.lib.binary.serialization.Serializer;
import net.daporkchop.lib.db.DBMap;
import net.daporkchop.lib.db.util.KeyHasher;

/**
 * @author DaPorkchop_
 */
public interface DBMapBuilder<Impl extends DBMapBuilder<Impl, K, V>, K, V> extends DBBuilder<Impl, DBMap<K, V>> {
    /**
     * A function to use for hashing keys. Using a custom key hashing function may provide better performance and/or reliability when using
     * a {@link DBMap}. If {@code null} and {@link #keySerializer} is set, keys will be serialized and (possibly) hashed using SHA-256. If {@code null}
     * and {@link #keySerializer} is not set, {@link Object#hashCode()} will be used.
     */
    KeyHasher<K> keyHasher();

    /**
     * A function that will be used to serialize keys. If {@code null}, keys will not be serialized at all.
     * <p>
     * Underlying implementations may choose to ignore this operation.
     */
    Serializer<K> keySerializer();

    /**
     * A function that will be used to serialize values. Must be set!
     */
    Serializer<V> valueSerializer();

    /**
     * A function to use for hashing keys. Using a custom key hashing function may provide better performance and/or reliability when using
     * a {@link DBMap}. If {@code null} and {@link #keySerializer} is set, keys will be serialized and (possibly) hashed using SHA-256. If {@code null}
     * and {@link #keySerializer} is not set, {@link Object#hashCode()} will be used.
     */
    <NEW_K> DBMapBuilder keyHasher(@NonNull KeyHasher<NEW_K> keyHasher);

    /**
     * A function that will be used to serialize keys. If {@code null}, keys will not be serialized at all.
     * <p>
     * Underlying implementations may choose to ignore this operation.
     */
    <NEW_K> DBMapBuilder keySerializer(@NonNull Serializer<NEW_K> keySerializer);

    /**
     * A function that will be used to serialize values. Must be set!
     */
    <NEW_V> DBMapBuilder valueSerializer(@NonNull Serializer<NEW_V> valueSerializer);
}
