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
import net.daporkchop.lib.collections.PIterator;
import net.daporkchop.lib.collections.stream.PStream;
import net.daporkchop.lib.db.DBSet;
import net.daporkchop.lib.db.util.KeyHasher;
import net.daporkchop.lib.db.util.exception.DBCloseException;
import net.daporkchop.lib.db.util.exception.DBNotOpenException;
import net.daporkchop.lib.db.util.exception.DBOpenException;
import net.daporkchop.lib.db.util.exception.DBReadException;
import net.daporkchop.lib.db.util.exception.DBWriteException;
import net.daporkchop.lib.dbextensions.leveldb.builder.LevelDBSetBuilder;
import net.daporkchop.lib.dbextensions.leveldb.util.LevelDBCollection;
import net.daporkchop.lib.dbextensions.leveldb.util.LevelDBConfiguration;
import net.daporkchop.lib.hash.util.Digest;
import net.daporkchop.lib.hash.util.Digester;
import net.daporkchop.lib.unsafe.PCleaner;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
public class LevelDBSet<V> extends LevelDBCollection implements DBSet<V> {
    protected final DB delegate;

    protected final KeyHasher<V> hasher;
    protected final Serializer<V> valueSerializer;

    protected final ReadWriteLock closeLock = new ReentrantReadWriteLock();
    protected final AtomicBoolean open = new AtomicBoolean(true);

    public LevelDBSet(@NonNull LevelDBSetBuilder<V> builder) {
        super(builder);

        if ((this.valueSerializer = builder.valueSerializer()) == null) {
            throw new NullPointerException("valueSerializer");
        }

        KeyHasher<V> hasher = builder.hasher();
        if (hasher == null) {
            hasher = obj -> {
                Digester digest = Digest.SHA_256.start();
                this.valueSerializer.write(obj, digest.appendStream());
                return digest.hash().getHash();
            };
        }
        this.hasher = hasher;

        this.delegate = this.configuration.openDB();
    }

    @Override
    public void add(@NonNull V value) {
        this.ensureOpen();
        this.closeLock.readLock().lock();
        try {
            this.ensureOpen();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (DataOut out = DataOut.wrap(baos)) {
                this.valueSerializer.write(value, out);
            }
            this.delegate.put(this.hasher.hash(value), baos.toByteArray());
        } catch (IOException e) {
            throw new DBWriteException(e);
        } finally {
            this.closeLock.readLock().unlock();
        }
    }

    @Override
    public boolean contains(@NonNull V value) {
        this.ensureOpen();
        this.closeLock.readLock().lock();
        try {
            this.ensureOpen();
            return this.delegate.get(this.hasher.hash(value)) != null;
        } catch (IOException e) {
            throw new DBReadException(e);
        } finally {
            this.closeLock.readLock().unlock();
        }
    }

    @Override
    public void remove(@NonNull V value) {
        this.ensureOpen();
        this.closeLock.readLock().lock();
        try {
            this.ensureOpen();
            this.delegate.delete(this.hasher.hash(value));
        } catch (IOException e) {
            throw new DBReadException(e);
        } finally {
            this.closeLock.readLock().unlock();
        }
    }

    @Override
    public boolean checkAndRemove(@NonNull V value) {
        this.ensureOpen();
        this.closeLock.readLock().lock();
        try {
            this.ensureOpen();
            byte[] hash = this.hasher.hash(value);
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
    public void forEach(@NonNull Consumer<V> consumer) {
        this.ensureOpen();
        this.closeLock.readLock().lock();
        try {
            this.ensureOpen();
            try (DBIterator iterator = this.delegate.iterator())    {
                while (iterator.hasNext())  {
                    consumer.accept(this.valueSerializer.read(DataIn.wrap(ByteBuffer.wrap(iterator.next().getValue()))));
                }
            }
        } catch (IOException e) {
            throw new DBReadException(e);
        } finally {
            this.closeLock.readLock().unlock();
        }
    }

    @Override
    public PIterator<V> iterator() {
        return new PIterator<V>() {
            DBIterator iterator = LevelDBSet.this.delegate.iterator();
            PCleaner cleaner = PCleaner.cleaner(this, () -> this.iterator.close());

            Map.Entry<byte[], byte[]> curr = null;

            @Override
            public boolean hasNext() {
                if (this.iterator.hasNext())    {
                    return true;
                } else {
                    this.cleaner.clean();
                    return false;
                }
            }

            @Override
            public V next() {
                try {
                    this.curr = this.iterator.next();
                    return LevelDBSet.this.valueSerializer.read(DataIn.wrap(ByteBuffer.wrap(this.curr.getValue())));
                } catch (IOException e) {
                    throw new DBReadException(e);
                }
            }

            @Override
            public V peek() {
                try {
                    Map.Entry<byte[], byte[]> entry = this.iterator.peekNext();
                    return LevelDBSet.this.valueSerializer.read(DataIn.wrap(ByteBuffer.wrap(entry.getValue())));
                } catch (IOException e) {
                    throw new DBReadException(e);
                }
            }

            @Override
            public void remove() {
                LevelDBSet.this.ensureOpen();
                LevelDBSet.this.closeLock.readLock().lock();
                try {
                    LevelDBSet.this.ensureOpen();
                    LevelDBSet.this.delegate.delete(this.curr.getKey());
                } finally {
                    LevelDBSet.this.closeLock.readLock().unlock();
                }
            }
        };
    }

    @Override
    public PStream<V> stream() {
        this.ensureOpen();
        this.closeLock.readLock().lock();
        try {
            this.ensureOpen();
            return new LevelDBStream<>(this.configuration, this.delegate, this.valueSerializer);
        } finally {
            this.closeLock.readLock().unlock();
        }
    }

    @Override
    public PStream<V> mutableStream() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long size() {
        return -1L;
    }

    @Override
    public void clear() {
        this.ensureOpen();
        this.closeLock.readLock().lock();
        try {
            this.ensureOpen();
            try (DBIterator iterator = this.delegate.iterator())    {
                while (iterator.hasNext())  {
                    this.delegate.delete(iterator.next().getKey());
                }
            }
        } finally {
            this.closeLock.readLock().unlock();
        }
    }

    @Override
    public boolean isConcurrent() {
        return false;
    }

    @Override
    public void close() {
        this.ensureOpen();
        this.closeLock.writeLock().lock();
        try {
            this.ensureOpen();
            this.open.set(false);
            this.delegate.close();
        } catch (IOException e) {
            throw new DBCloseException(e);
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
