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

package net.daporkchop.lib.db.container.map;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.data.Serializer;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.db.Container;
import net.daporkchop.lib.db.PorkDB;
import net.daporkchop.lib.db.container.map.index.IndexLookup;
import net.daporkchop.lib.db.container.map.index.TreeIndexLookup;
import net.daporkchop.lib.db.data.key.KeyHasher;
import net.daporkchop.lib.db.data.key.KeyHasherDefault;
import net.daporkchop.lib.encoding.compression.Compression;
import net.daporkchop.lib.encoding.compression.CompressionHelper;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author DaPorkchop_
 */
public class DBMap<K, V> extends Container<Map<K, V>, DBMap.Builder<K, V>> implements Map<K, V> {
    public static <K, V> Builder<K, V> builder(@NonNull PorkDB db, @NonNull String name) {
        return new Builder<>(db, name);
    }

    private final AtomicLong size = new AtomicLong(0L);
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    @Getter
    private final CompressionHelper compression;
    @Getter
    private final Serializer<K> keySerializer;
    @Getter
    private final KeyHasher<K> keyHasher;
    @Getter
    private final Serializer<V> valueSerializer;
    private final IndexLookup<K> indexLookup;
    private volatile boolean dirty = false;

    public DBMap(Builder<K, V> builder) throws IOException {
        super(builder);

        this.compression = builder.compression;
        this.keySerializer = builder.keySerializer;
        this.keyHasher = builder.keyHasher;
        this.valueSerializer = builder.valueSerializer;
        this.indexLookup = builder.indexLookup;

        try (DataIn in = this.getIn("headers.dat", out -> {
            out.writeLong(0L); //size
        })) {
            this.size.set(in.readLong());
        }

        this.indexLookup.init(this, this.getRAF("index"));
    }

    @Override
    public Map<K, V> getValue() {
        return this;
    }

    @Override
    public void save() throws IOException {
        this.lock.writeLock().lock();
        try {
            if (this.dirty) {
                try (DataOut out = this.getOut("headers.dat")) {
                    out.writeLong(this.size.get());
                }
                this.dirty = false;
            }
            this.indexLookup.save();
        } finally {
            this.lock.writeLock().unlock();
        }
        this.indexLookup.save();
        //TODO
    }

    /**
     * Use {@link #sizeLong()}
     */
    @Override
    public int size() {
        return this.size.intValue();
    }

    /**
     * Get the map's size as a 64-bit integer
     *
     * @return the map's size
     */
    public long sizeLong() {
        return this.size.get();
    }

    @Override
    public boolean isEmpty() {
        return this.size.get() == 0L;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean containsKey(@NonNull Object key) {
        try {
            return this.indexLookup.contains((K) key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean containsValue(@NonNull Object value) {
        //TODO
        return false;
    }

    @Override
    public V get(@NonNull Object key) {
        //TODO
        return null;
    }

    @Override
    public V put(@NonNull K key, @NonNull V value) {
        //TODO
        return null;
    }

    @Override
    public V remove(@NonNull Object key) {
        //TODO
        return null;
    }

    @Override
    public void putAll(@NonNull Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        //TODO
        this.size.set(0L);
        try {
            this.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<K> keySet() {
        //TODO
        return null;
    }

    @Override
    public Collection<V> values() {
        //TODO
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        //TODO
        return null;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Builder<K, V> extends Container.Builder<Map<K, V>, DBMap<K, V>> {
        private Serializer<K> keySerializer;

        @NonNull
        private Serializer<V> valueSerializer;

        @NonNull
        private KeyHasher<K> keyHasher = new KeyHasherDefault<>();

        @NonNull
        private IndexLookup<K> indexLookup = new TreeIndexLookup<>();

        @NonNull
        private CompressionHelper compression = Compression.NONE;

        private Builder(PorkDB db, String name) {
            super(db, name);
        }

        @Override
        protected DBMap<K, V> buildImpl() throws IOException {
            if (this.valueSerializer == null) {
                throw new IllegalStateException("Value serializer must be set!");
            }

            return new DBMap<>(this);
        }
    }
}
