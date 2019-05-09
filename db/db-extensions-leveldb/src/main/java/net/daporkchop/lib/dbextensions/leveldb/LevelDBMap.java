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

package net.daporkchop.lib.dbextensions.leveldb;

import lombok.NonNull;
import net.daporkchop.lib.binary.serialization.Serializer;
import net.daporkchop.lib.collections.PMap;
import net.daporkchop.lib.collections.stream.PStream;
import net.daporkchop.lib.db.DBMap;
import net.daporkchop.lib.db.util.KeyHasher;
import net.daporkchop.lib.db.util.exception.DBOpenException;
import net.daporkchop.lib.dbextensions.leveldb.builder.LevelDBMapBuilder;
import net.daporkchop.lib.hash.util.Digest;
import org.iq80.leveldb.DB;

import java.io.IOException;
import java.util.function.BiConsumer;


/**
 * @author DaPorkchop_
 */
public class LevelDBMap<K, V> implements DBMap<K, V> {
    protected final DB delegate;

    protected final KeyHasher<K> keyHasher;
    protected final Serializer<K> keySerializer;
    protected final Serializer<V> valueSerializer;

    public LevelDBMap(@NonNull LevelDBMapBuilder<K, V> builder) {
        if ((this.valueSerializer = builder.valueSerializer()) == null) {
            throw new NullPointerException("valueSerializer");
        }

        if ((this.keySerializer = builder.keySerializer()) == null) {
            if (builder.keyHasher() == null)    {
                this.keyHasher = (obj, out) -> out.writeInt(obj.hashCode());
            } else {
                this.keyHasher = builder.keyHasher();
            }
        } else {
            if (builder.keyHasher() == null)    {
                this.keyHasher = (obj, out) -> {
                }
            } else {
                this.keyHasher = builder.keyHasher();
            }
        }

        try {
            this.delegate = builder.openDB();
        } catch (IOException e) {
            throw new DBOpenException(e);
        }
    }

    @Override
    public long size() {
        //we can't really calculate the number of entries without iterating over (and, in the process, loading) every single key+value in
        //the db, or adding an additional get() call before every put and remove. since both of these options would hurt performance
        //significantly, we simply say that the size can't be computed.
        return -1L;
    }

    @Override
    public void clear() {
    }

    @Override
    public V get(@NonNull K key) {
        return null;
    }

    @Override
    public void put(@NonNull K key, @NonNull V value) {
    }

    @Override
    public boolean checkAndPut(@NonNull K key, @NonNull V value) {
        return false;
    }

    @Override
    public V getAndPut(@NonNull K key, @NonNull V value) {
        return null;
    }

    @Override
    public boolean contains(@NonNull K key) {
        return false;
    }

    @Override
    public void remove(@NonNull K key) {
    }

    @Override
    public boolean checkAndRemove(@NonNull K key) {
        return false;
    }

    @Override
    public V getAndRemove(@NonNull K key) {
        return null;
    }

    @Override
    public void forEach(@NonNull BiConsumer<K, V> consumer) {
    }

    @Override
    public PStream<K> keyStream() {
        return null;
    }

    @Override
    public PStream<V> valueStream() {
        return null;
    }

    @Override
    public PStream<Entry<K, V>> entryStream() {
        return null;
    }

    @Override
    public boolean isConcurrent() {
        return false;
    }

    @Override
    public void close() throws IOException {
    }
}
