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
import net.daporkchop.lib.common.function.IOConsumer;
import net.daporkchop.lib.db.Container;
import net.daporkchop.lib.db.PorkDB;
import net.daporkchop.lib.db.container.map.data.DataLookup;
import net.daporkchop.lib.db.container.map.data.IndividualFileLookup;
import net.daporkchop.lib.db.container.map.data.key.DefaultKeyHasher;
import net.daporkchop.lib.db.container.map.data.key.KeyHasher;
import net.daporkchop.lib.db.container.map.index.IndexLookup;
import net.daporkchop.lib.db.container.map.index.SlowAndInefficientTreeIndexLookup;
import net.daporkchop.lib.encoding.compression.Compression;
import net.daporkchop.lib.encoding.compression.CompressionHelper;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A filesystem-based implementation of {@link Map}.
 * <p>
 * How exactly this will behave depends on the settings defined in the builder. Configurable
 * options are:
 * {@link KeyHasher}
 * {@link IndexLookup}
 * {@link DataLookup}
 *
 * @author DaPorkchop_
 */
public class DBMap<K, V> extends Container<Map<K, V>, DBMap.Builder<K, V>> implements Map<K, V> {
    private final AtomicLong size = new AtomicLong(0L);
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    @Getter
    private final CompressionHelper compression;
    //@Getter
    //private final Serializer<K> keySerializer;
    @Getter
    private final KeyHasher<K> keyHasher;
    @Getter
    private final Serializer<V> valueSerializer;
    @Getter //TODO: remove this debug thing
    private final IndexLookup<K> indexLookup;
    private final DataLookup dataLookup;
    private volatile boolean dirty = false;

    public DBMap(@NonNull Builder<K, V> builder) throws IOException {
        super(builder);

        this.compression = builder.compression;
        //this.keySerializer = builder.keySerializer;
        this.keyHasher = builder.keyHasher;
        this.valueSerializer = builder.valueSerializer;
        this.indexLookup = builder.indexLookup;
        this.dataLookup = builder.dataLookup;

        try (DataIn in = this.getIn("headers.dat", out -> {
            out.writeLong(0L); //size
        })) {
            this.size.set(in.readLong());
        }

        this.indexLookup.init(this, this.getFile("index", false));
        this.dataLookup.init(this, this.getFile("data", false));
    }

    public static <K, V> Builder<K, V> builder(@NonNull PorkDB db, @NonNull String name) {
        return new Builder<>(db, name);
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
        } finally {
            this.lock.writeLock().unlock();
        }
        this.indexLookup.save();
        this.dataLookup.save();
        //TODO
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.indexLookup.close();
        this.dataLookup.close();
    }

    /**
     * Use {@link #sizeLong()}
     */
    @Override
    @Deprecated
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
    @Deprecated
    public boolean containsValue(@NonNull Object value) {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(@NonNull Object o) {
        try {
            K key = (K) o;
            AtomicReference<V> ref = new AtomicReference<>(null);
            this.indexLookup.runIfContains(key, id -> {
                try (DataIn in = this.wrap(this.dataLookup.read(id))) {
                    ref.set(this.valueSerializer.read(in));
                }
            });
            return ref.get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public V put(@NonNull K key, @NonNull V value) {
        return this.put(key, value, true);
    }

    /**
     * Puts a key/value pair into the map
     *
     * @param key          the key to add
     * @param value        the value corresponding to the key
     * @param loadOldValue whether or not the old value should be loaded and returned, if present
     * @return the old value, if present and loadOldValue is true
     */
    public V put(@NonNull K key, @NonNull V value, boolean loadOldValue) {
        try {
            AtomicReference<V> oldValue = loadOldValue ? new AtomicReference<>(null) : null;
            this.indexLookup.change(key, id -> {
                if (id != -1L && loadOldValue) {
                    try (DataIn in = this.wrap(this.dataLookup.read(id))) {
                        oldValue.set(this.valueSerializer.read(in));
                    }
                }
                return this.dataLookup.write(id, out -> {
                    try (DataOut theOut = this.wrap(out)) {
                        this.valueSerializer.write(value, theOut);
                    }
                });
            });
            return loadOldValue ? oldValue.get() : null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public V remove(@NonNull Object o) {
        return this.remove((K) o, true);
    }

    /**
     * Remove a mapping from the map
     *
     * @param key     the key that should be removed
     * @param loadOld whether or not the old value should be loaded and returned, if
     *                present
     * @return the old value, if present and loadOld is true
     */
    public V remove(@NonNull K key, boolean loadOld) {
        try {
            AtomicReference<V> ref = loadOld ? new AtomicReference<>(null) : null;
            this.indexLookup.runIfContains(key, id -> {
                if (loadOld) {
                    try (DataIn in = this.wrap(this.dataLookup.read(id))) {
                        ref.set(this.valueSerializer.read(in));
                    }
                }
            });
            return loadOld ? ref.get() : null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putAll(@NonNull Map<? extends K, ? extends V> m) {
        m.forEach((key, value) -> this.put(key, value, false));
    }

    @Override
    public void clear() {
        this.size.set(0L);
        try {
            this.indexLookup.clear();
            this.dataLookup.clear();
            this.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<K> keySet() {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<V> values() {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public File getFile(String name) throws IOException {
        return super.getFile(name);
    }

    @Override
    public File getFile(String name, IOConsumer<DataOut> initializer, boolean create) throws IOException {
        return super.getFile(name, initializer, create);
    }

    @Override
    public RandomAccessFile getRAF(String name) throws IOException {
        return super.getRAF(name);
    }

    private DataIn wrap(@NonNull DataIn in) throws IOException {
        if (this.dataLookup.allowsCompression() && this.compression != Compression.NONE) {
            return DataIn.wrap(this.compression.inflate(in));
        } else {
            return in;
        }
    }

    private DataOut wrap(@NonNull DataOut out) throws IOException {
        if (this.dataLookup.allowsCompression() && this.compression != Compression.NONE) {
            return DataOut.wrap(this.compression.deflate(out));
        } else {
            return out;
        }
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Builder<K, V> extends Container.Builder<Map<K, V>, DBMap<K, V>> {
        //TODO: private Serializer<K> keySerializer;

        /**
         * The {@link Serializer} used for writing values to disk.
         * <p>
         * If the {@link DataLookup} expects constant-length values, this must
         * be set to an instance of {@link net.daporkchop.lib.binary.data.impl.ConstantLengthSerializer}.
         * <p>
         * MUST BE SET! Unlike all other fields in this class, this one is NOT set to a
         * default value.
         *
         * @see net.daporkchop.lib.binary.data.impl.BasicSerializer
         * @see net.daporkchop.lib.binary.data.impl.ConstantLengthSerializer
         * @see net.daporkchop.lib.binary.data.impl.ByteArraySerializer
         */
        @NonNull
        private Serializer<V> valueSerializer;

        /**
         * The {@link KeyHasher} used for... hashing keys
         *
         * @see net.daporkchop.lib.db.container.map.data.key.DefaultKeyHasher
         * @see net.daporkchop.lib.db.container.map.data.key.ByteArrayKeyHasher
         * @see net.daporkchop.lib.db.container.map.data.key.PrimitiveKeyHasher
         */
        @NonNull
        private KeyHasher<K> keyHasher = new DefaultKeyHasher<>();

        /**
         * The {@link IndexLookup} used for mapping keys to long ids as used by
         * implementations of {@link DataLookup}
         *
         * @see net.daporkchop.lib.db.container.map.index.SlowAndInefficientTreeIndexLookup (don't use this, really)
         */
        @NonNull
        private IndexLookup<K> indexLookup = new SlowAndInefficientTreeIndexLookup<>();

        /**
         * The {@link DataLookup} used for reading values.
         *
         * @see net.daporkchop.lib.db.container.map.data.IndividualFileLookup
         * @see net.daporkchop.lib.db.container.map.data.StreamingDataLookup (wip)
         * @see net.daporkchop.lib.db.container.map.data.ConstantLengthLookup
         * @see net.daporkchop.lib.db.container.map.data.OneTimeWriteDataLookup
         */
        @NonNull
        private DataLookup dataLookup = new IndividualFileLookup();

        /**
         * The compression algorithm used for compression of values. If the {@link DataLookup}
         * doesn't allow compression of values (see {@link DataLookup#allowsCompression()}) then
         * this will not be used.
         * <p>
         * Some default {@link CompressionHelper} instances to use here can be found
         * as static fields in {@link Compression}.
         *
         * @see Compression
         * @see DataLookup#allowsCompression()
         */
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
            if (this.compression != Compression.NONE && !this.dataLookup.allowsCompression()) {
                System.err.printf("[Warning] DataLookup %s reports that it doesn't support compression, but compression is set to %s. Data will not be compressed.\n", this.dataLookup.getClass().getCanonicalName(), this.compression);
            }

            return new DBMap<>(this);
        }
    }
}
