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
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.collections.stream.PStream;
import net.daporkchop.lib.common.function.io.IOConsumer;
import net.daporkchop.lib.db.DBMap;
import net.daporkchop.lib.db.util.KeyHasher;
import net.daporkchop.lib.db.util.exception.DBNotOpenException;
import net.daporkchop.lib.db.util.exception.DBOpenException;
import net.daporkchop.lib.db.util.exception.DBReadException;
import net.daporkchop.lib.dbextensions.leveldb.builder.LevelDBMapBuilder;
import net.daporkchop.lib.encoding.ToBytes;
import net.daporkchop.lib.hash.util.Digest;
import net.daporkchop.lib.hash.util.Digester;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


/**
 * @author DaPorkchop_
 */
public class LevelDBMap<K, V> implements DBMap<K, V> {
    protected final DB delegate;

    protected final KeyHasher<K> keyHasher;
    protected final Serializer<K> keySerializer;
    protected final boolean serializeKeys;
    protected final Serializer<V> valueSerializer;

    protected final ReadWriteLock closeLock = new ReentrantReadWriteLock();
    protected final AtomicBoolean open = new AtomicBoolean(true);

    public LevelDBMap(@NonNull LevelDBMapBuilder<K, V> builder) {
        if ((this.valueSerializer = builder.valueSerializer()) == null) {
            throw new NullPointerException("valueSerializer");
        }

        Serializer<K> keySerializer = builder.keySerializer();
        KeyHasher<K> keyHasher = builder.keyHasher();
        boolean serializeKeys = builder.serializeKeys();
        if (serializeKeys) {
            if (keySerializer == null) {
                throw new IllegalArgumentException("serializeKeys is set but keySerializer is not!");
            } else if (keyHasher == null) {
                keyHasher = obj -> {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try (DataOut out = DataOut.wrap(baos)) {
                        keySerializer.write(obj, out);
                    }
                    return baos.toByteArray();
                };
            } else {
                throw new IllegalArgumentException("serializeKeys and keyHasher are set!");
            }
        } else {
            if (keyHasher == null) {
                if (keySerializer == null) {
                    keyHasher = obj -> ToBytes.toBytes(obj.hashCode());
                } else {
                    keyHasher = obj -> {
                        Digester digest = Digest.SHA_256.start();
                        keySerializer.write(obj, digest.appendStream());
                        return digest.hash().getHash();
                    };
                }
            } else {
                if (keySerializer != null)   {
                    throw new IllegalStateException("keySerializer is set but will never be used!");
                }
            }
        }
        this.keyHasher = keyHasher;
        this.keySerializer = keySerializer;
        this.serializeKeys = serializeKeys;

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
        this.ensureOpen();
        this.closeLock.readLock().lock();
        try {
            this.ensureOpen();
            for (DBIterator iterator = this.delegate.iterator(); iterator.hasNext(); ) {
                this.delegate.delete(iterator.next().getKey());
            }
        } finally {
            this.closeLock.readLock().unlock();
        }
    }

    @Override
    public V get(@NonNull K key) {
        this.ensureOpen();
        this.closeLock.readLock().lock();
        try {
            this.ensureOpen();
            byte[] data = this.delegate.get(this.keyHasher.hash(key));
            if (data == null) {
                return null;
            }
            return this.valueSerializer.read(DataIn.wrap(ByteBuffer.wrap(data)));
        } catch (IOException e) {
            throw new DBReadException(e);
        } finally {
            this.closeLock.readLock().unlock();
        }
    }

    @Override
    public void put(@NonNull K key, @NonNull V value) {
        this.ensureOpen();
        this.closeLock.readLock().lock();
        try {
            this.ensureOpen();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (DataOut out = DataOut.wrap(baos)) {
                this.valueSerializer.write(value, out);
            }
            this.delegate.put(this.keyHasher.hash(key), baos.toByteArray());
        } catch (IOException e) {
            throw new DBReadException(e);
        } finally {
            this.closeLock.readLock().unlock();
        }
    }

    @Override
    public boolean checkAndPut(@NonNull K key, @NonNull V value) {
        this.ensureOpen();
        this.closeLock.readLock().lock();
        try {
            this.ensureOpen();
            byte[] hash = this.keyHasher.hash(key);
            boolean wasPresent = this.delegate.get(hash) != null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (DataOut out = DataOut.wrap(baos)) {
                this.valueSerializer.write(value, out);
            }
            this.delegate.put(hash, baos.toByteArray());
            return wasPresent;
        } catch (IOException e) {
            throw new DBReadException(e);
        } finally {
            this.closeLock.readLock().unlock();
        }
    }

    @Override
    public V getAndPut(@NonNull K key, @NonNull V value) {
        this.ensureOpen();
        this.closeLock.readLock().lock();
        try {
            this.ensureOpen();
            byte[] hash = this.keyHasher.hash(key);
            byte[] old = this.delegate.get(hash);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (DataOut out = DataOut.wrap(baos)) {
                this.valueSerializer.write(value, out);
            }
            this.delegate.put(hash, baos.toByteArray());
            return this.valueSerializer.read(DataIn.wrap(ByteBuffer.wrap(old)));
        } catch (IOException e) {
            throw new DBReadException(e);
        } finally {
            this.closeLock.readLock().unlock();
        }
    }

    @Override
    public boolean contains(@NonNull K key) {
        this.ensureOpen();
        this.closeLock.readLock().lock();
        try {
            this.ensureOpen();
            return this.delegate.get(this.keyHasher.hash(key)) != null;
        } catch (IOException e) {
            throw new DBReadException(e);
        } finally {
            this.closeLock.readLock().unlock();
        }
    }

    @Override
    public void remove(@NonNull K key) {
        this.ensureOpen();
        this.closeLock.readLock().lock();
        try {
            this.ensureOpen();
            this.delegate.delete(this.keyHasher.hash(key));
        } catch (IOException e) {
            throw new DBReadException(e);
        } finally {
            this.closeLock.readLock().unlock();
        }
    }

    @Override
    public boolean checkAndRemove(@NonNull K key) {
        this.ensureOpen();
        this.closeLock.readLock().lock();
        try {
            this.ensureOpen();
            byte[] hash = this.keyHasher.hash(key);
            boolean wasPresent = this.delegate.get(hash) != null;
            this.delegate.delete(hash);
            return wasPresent;
        } catch (IOException e) {
            throw new DBReadException(e);
        } finally {
            this.closeLock.readLock().unlock();
        }
    }

    @Override
    public V getAndRemove(@NonNull K key) {
        this.ensureOpen();
        this.closeLock.readLock().lock();
        try {
            this.ensureOpen();
            byte[] hash = this.keyHasher.hash(key);
            byte[] old = this.delegate.get(hash);
            this.delegate.delete(hash);
            return this.valueSerializer.read(DataIn.wrap(ByteBuffer.wrap(old)));
        } catch (IOException e) {
            throw new DBReadException(e);
        } finally {
            this.closeLock.readLock().unlock();
        }
    }

    @Override
    public void forEach(@NonNull BiConsumer<K, V> consumer) {
        this.ensureOpen();
        this.closeLock.readLock().lock();
        try {
            this.ensureOpen();
            this.delegate.forEach((IOConsumer<Map.Entry<byte[], byte[]>>) e -> {
                K key = null;
                V val = this.valueSerializer.read(DataIn.wrap(ByteBuffer.wrap(e.getValue())));
                if (this.serializeKeys) {
                    key = this.keySerializer.read(DataIn.wrap(ByteBuffer.wrap(e.getKey())));
                }
                consumer.accept(key, val);
            });
        } finally {
            this.closeLock.readLock().unlock();
        }
    }

    @Override
    public void forEachKey(@NonNull Consumer<K> consumer) {
        if (this.serializeKeys) {
            this.ensureOpen();
            this.closeLock.readLock().lock();
            try {
                this.ensureOpen();
                this.delegate.forEach((IOConsumer<Map.Entry<byte[], byte[]>>) e -> consumer.accept(this.keySerializer.read(DataIn.wrap(ByteBuffer.wrap(e.getKey())))));
            } finally {
                this.closeLock.readLock().unlock();
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void forEachValue(@NonNull Consumer<V> consumer) {
        this.ensureOpen();
        this.closeLock.readLock().lock();
        try {
            this.ensureOpen();
            this.delegate.forEach((IOConsumer<Map.Entry<byte[], byte[]>>) e -> consumer.accept(this.valueSerializer.read(DataIn.wrap(ByteBuffer.wrap(e.getValue())))));
        } finally {
            this.closeLock.readLock().unlock();
        }
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
        this.ensureOpen();
        this.closeLock.writeLock().lock();
        try {
            this.ensureOpen();
            this.open.set(false);
            this.delegate.close();
        } finally {
            this.closeLock.writeLock().unlock();
        }
    }

    protected void ensureOpen() {
        if (!this.open.get()) {
            throw new DBNotOpenException();
        }
    }
}
