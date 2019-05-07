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

package net.daporkchop.lib.dbextensions.leveldb.container;

import lombok.NonNull;
import net.daporkchop.lib.binary.serialization.Serializer;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.collections.PMap;
import net.daporkchop.lib.collections.stream.PStream;
import net.daporkchop.lib.common.function.io.IOBiConsumer;
import net.daporkchop.lib.common.setting.Settings;
import net.daporkchop.lib.concurrent.cache.Cache;
import net.daporkchop.lib.concurrent.cache.SoftThreadCache;
import net.daporkchop.lib.db.container.ContainerType;
import net.daporkchop.lib.db.container.map.AbstractDBMap;
import net.daporkchop.lib.db.util.exception.DBCloseException;
import net.daporkchop.lib.db.util.exception.DBOpenException;
import net.daporkchop.lib.db.util.exception.DBReadException;
import net.daporkchop.lib.db.util.exception.DBWriteException;
import net.daporkchop.lib.dbextensions.leveldb.LevelDB;
import net.daporkchop.lib.dbextensions.leveldb.builder.LevelDBMapBuilder;
import net.daporkchop.lib.encoding.ToBytes;
import net.daporkchop.lib.logging.Logging;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBException;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.WriteBatch;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


/**
 * @author DaPorkchop_
 */
public class LevelDBMap<K, V> extends AbstractDBMap<K, V, LevelDBMap<K, V>> {
    protected final LevelDB levelDb;
    protected final DB delegate;

    protected final byte[] prefix;
    protected final Serializer<K> fastKeySerializer;

    protected final Cache<ByteArrayOutputStream> baosCache = SoftThreadCache.of(/*Fast*/ByteArrayOutputStream::new);
    protected final ReadWriteLock lock = new ReentrantReadWriteLock();

    @SuppressWarnings("unchecked")
    public LevelDBMap(@NonNull LevelDBMapBuilder<K, V> builder) {
        super(builder.validate());

        try {
            this.levelDb = builder.getLevelDb();
            if (this.levelDb.getBuilder().isSharedDb()) {
                this.delegate = this.levelDb.getDb();
            } else {
                this.delegate = this.levelDb.getBuilder().getDbFactory().open(builder.getPath(), this.levelDb.getDbOptions());
            }

            if (builder.getContainerPrefix() == null) {
                if (this.levelDb.getBuilder().isSharedDb()) {
                    throw new IllegalStateException("Cannot have a null prefix in a shared DB!");
                } else {
                    this.prefix = null;
                }
            } else {
                this.prefix = builder.getContainerPrefix();
            }
            this.fastKeySerializer = builder.getFastKeySerializer();
        } catch (Exception e) {
            throw new DBOpenException(e);
        }
    }

    @Override
    public void close() {
        if (!this.closed.getAndSet(true))    {
            try {
                if (!this.levelDb.getBuilder().isSharedDb()) {
                    this.delegate.close();
                }
                this.levelDb.closeContainer(ContainerType.MAP, this.name);
            } catch (Exception e)   {
                throw new DBCloseException(e);
            }
        }
    }

    @Override
    public long size() {
        return 0L;
    }

    @Override
    public void clear() {
        this.lock.writeLock().lock();
        try (DBIterator iterator = this.delegate.iterator()) {
            if (this.levelDb.getBuilder().isSharedDb()) {
                //TODO: this really needs to be optimized somehow (i think)
                ITERATOR_LOOP:
                for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                    byte[] key = iterator.peekNext().getKey();
                    for (int i = this.prefix.length - 1; i >= 0; i--) {
                        if (key[i] != this.prefix[i]) {
                            continue ITERATOR_LOOP;
                        }
                    }
                    iterator.remove();
                }
            } else {
                //TODO: consider closing the database, physically deleting the files, then creating a new one
                for (iterator.seekToFirst(); iterator.hasNext(); iterator.next())   {
                    iterator.remove();
                }
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public V get(@NonNull K key) {
        this.lock.readLock().lock();
        try {
            byte[] data = this.delegate.get(this.getKey(key));
            V val = null;
            if (data != null)   {
                val = this.valueSerializer.read(DataIn.wrap(this.valueCompression.inflate(DataIn.wrap(ByteBuffer.wrap(data)))));
            }
            return val;
        } catch (IOException | DBException e) {
            throw new DBReadException(e);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public void put(@NonNull K key, @NonNull V value) {
        this.lock.readLock().lock();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (DataOut out = DataOut.wrap(this.valueCompression.deflate(baos)))    {
                this.valueSerializer.write(value, out);
            }
            this.delegate.put(this.getKey(key), baos.toByteArray());
        } catch (IOException | DBException e) {
            throw new DBWriteException(e);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public boolean checkAndPut(@NonNull K key, @NonNull V value) {
        this.lock.readLock().lock();
        try {
            byte[] keyEncoded = this.getKey(key);
            boolean present = this.delegate.get(keyEncoded) != null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (DataOut out = DataOut.wrap(this.valueCompression.deflate(baos)))    {
                this.valueSerializer.write(value, out);
            }
            this.delegate.put(keyEncoded, baos.toByteArray());
            return present;
        } catch (IOException | DBException e) {
            throw new DBWriteException(e);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public V getAndPut(@NonNull K key, @NonNull V value) {
        this.lock.readLock().lock();
        try {
            byte[] keyEncoded = this.getKey(key);
            V val = null;
            {
                byte[] data = this.delegate.get(this.getKey(key));
                if (data != null) {
                    val = this.valueSerializer.read(DataIn.wrap(this.valueCompression.inflate(DataIn.wrap(ByteBuffer.wrap(data)))));
                }
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (DataOut out = DataOut.wrap(this.valueCompression.deflate(baos)))    {
                this.valueSerializer.write(value, out);
            }
            this.delegate.put(keyEncoded, baos.toByteArray());
            return val;
        } catch (IOException | DBException e) {
            throw new DBWriteException(e);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public void putAll(@NonNull PMap<K, V> other) {
        this.lock.readLock().lock();
        try (WriteBatch batch = this.delegate.createWriteBatch()) {
            other.forEach((IOBiConsumer<K, V>) (key, value) -> {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try (DataOut out = DataOut.wrap(this.valueCompression.deflate(baos)))    {
                    this.valueSerializer.write(value, out);
                }
                batch.put(this.getKey(key), baos.toByteArray());
            });

            this.delegate.write(batch);
        } catch (IOException | DBException e)   {
            throw new DBWriteException(e);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public boolean contains(@NonNull K key) {
        this.lock.readLock().lock();
        try {
            return this.delegate.get(this.getKey(key)) != null;
        } catch (IOException | DBException e) {
            throw new DBReadException(e);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public void remove(@NonNull K key) {
        this.lock.readLock().lock();
        try {
            this.delegate.delete(this.getKey(key));
        } catch (IOException | DBException e) {
            throw new DBWriteException(e);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public boolean checkAndRemove(@NonNull K key) {
        this.lock.readLock().lock();
        try {
            byte[] keyEncoded = this.getKey(key);
            boolean present = this.delegate.get(keyEncoded) != null;
            this.delegate.delete(keyEncoded);
            return present;
        } catch (IOException | DBException e) {
            throw new DBWriteException(e);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public V getAndRemove(@NonNull K key) {
        this.lock.readLock().lock();
        try {
            byte[] keyEncoded = this.getKey(key);
            V val = null;
            {
                byte[] data = this.delegate.get(this.getKey(key));
                if (data != null) {
                    val = this.valueSerializer.read(DataIn.wrap(this.valueCompression.inflate(DataIn.wrap(ByteBuffer.wrap(data)))));
                }
            }
            this.delegate.delete(keyEncoded);
            return val;
        } catch (IOException | DBException e) {
            throw new DBWriteException(e);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public void forEach(@NonNull BiConsumer<K, V> consumer) {
        this.lock.readLock().lock();
        try (DBIterator iterator = this.delegate.iterator()) {
            int i = 0;
            if (this.levelDb.getBuilder().isSharedDb()) {
                ITERATOR:
                for (iterator.seekToFirst(); iterator.hasNext(); iterator.next(), i++) {
                    K key = null;
                    if (this.keysReadable) {
                        try (DataIn in = DataIn.wrap(ByteBuffer.wrap(iterator.peekNext().getKey()))) {
                            for (int j = 0; j < this.prefix.length; j++)    {
                                if ((this.prefix[j] & 0xFF) != in.read())   {
                                    continue ITERATOR;
                                }
                            }
                            if (this.fastKeySerializer != null) {
                                key = this.fastKeySerializer.read(in);
                            } else if (this.keySerializer != null) {
                                key = this.keySerializer.read(in);
                            } else {
                                throw new IllegalStateException();
                            }
                        }
                    }
                    V val = this.valueSerializer.read(DataIn.wrap(this.valueCompression.inflate(DataIn.wrap(ByteBuffer.wrap(iterator.peekNext().getValue())))));
                    consumer.accept(key, val);
                }
            } else {
                for (iterator.seekToFirst(); iterator.hasNext(); iterator.next(), i++) {
                    K key = null;
                    if (this.keysReadable) {
                        try (DataIn in = DataIn.wrap(ByteBuffer.wrap(iterator.peekNext().getKey()))) {
                            if (this.fastKeySerializer != null) {
                                key = this.fastKeySerializer.read(in);
                            } else if (this.keySerializer != null) {
                                key = this.keySerializer.read(in);
                            } else {
                                throw new IllegalStateException();
                            }
                        }
                    }
                    V val = this.valueSerializer.read(DataIn.wrap(this.valueCompression.inflate(DataIn.wrap(ByteBuffer.wrap(iterator.peekNext().getValue())))));
                    consumer.accept(key, val);
                }
            }
            Logging.logger.debug("Iterated over %d entries", i);
        } catch (IOException | DBException e) {
            throw new DBReadException(e);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public PStream<K> keyStream() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PStream<V> valueStream() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PStream<Entry<K, V>> entryStream() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isConcurrent() {
        return true;
    }

    protected byte[] getKey(@NonNull K key) throws IOException {
        ByteArrayOutputStream baos = this.baosCache.get();
        baos.reset();
        if (this.prefix != null)    {
            baos.write(this.prefix);
        }
        if (this.keySerializer != null) {
            this.keySerializer.write(key, DataOut.wrap(baos));
        } else if (this.fastKeySerializer != null) {
            this.fastKeySerializer.write(key, DataOut.wrap(baos));
        } else {
            throw new IllegalStateException();
        }
        return baos.toByteArray();
    }
}
