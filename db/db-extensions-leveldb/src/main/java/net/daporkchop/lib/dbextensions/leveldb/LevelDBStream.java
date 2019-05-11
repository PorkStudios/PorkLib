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
import net.daporkchop.lib.collections.PMap;
import net.daporkchop.lib.collections.stream.PStream;
import net.daporkchop.lib.collections.util.ConcurrencyHelper;
import net.daporkchop.lib.common.function.io.IOConsumer;
import net.daporkchop.lib.common.function.io.IORunnable;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.db.DBStream;
import net.daporkchop.lib.db.util.exception.DBNotOpenException;
import net.daporkchop.lib.db.util.exception.DBReadException;
import net.daporkchop.lib.dbextensions.leveldb.util.LevelDBCollection;
import net.daporkchop.lib.dbextensions.leveldb.util.LevelDBConfiguration;
import net.daporkchop.lib.unsafe.PCleaner;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.ReadOptions;
import org.iq80.leveldb.Snapshot;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author DaPorkchop_
 */
public class LevelDBStream<V> extends LevelDBCollection implements DBStream<V> {
    protected final DB delegate;

    protected DB sourceDb;
    protected Serializer<V> sourceSerializer;

    protected boolean concurrent = false;
    protected final UUID id = UUID.randomUUID();

    protected final Lock lock = new ReentrantLock();
    protected final PCleaner cleaner;

    public LevelDBStream(@NonNull LevelDBConfiguration configuration, @NonNull DB sourceDb, @NonNull Serializer<V> sourceSerializer) {
        super(configuration);

        if (configuration.getSerialization() == null) {
            throw new IllegalArgumentException("serialization must be set!");
        }

        this.sourceDb = sourceDb;
        this.sourceSerializer = sourceSerializer;

        DB delegate = this.delegate = this.configuration.openDB(this.id.toString());
        UUID id = this.id;
        this.cleaner = PCleaner.cleaner(this, () -> new Thread((IORunnable) () -> {
            delegate.close();
            PFiles.rm(new File(configuration.getPath(), String.format("children/%s", id.toString())));
        }).start());
    }

    @Override
    public long size() {
        return -1L;
    }

    @Override
    public boolean isOrdered() {
        return true;
    }

    @Override
    public PStream<V> ordered() {
        return this;
    }

    @Override
    public PStream<V> unordered() {
        return this;
    }

    @Override
    public boolean isConcurrent() {
        return this.concurrent;
    }

    @Override
    public PStream<V> concurrent() {
        this.concurrent = true;
        return this;
    }

    @Override
    public PStream<V> singleThreaded() {
        this.concurrent = false;
        return this;
    }

    @Override
    public void forEach(@NonNull Consumer<V> consumer) {
        this.lock.lock();
        try {
            this.run(entry -> consumer.accept(this.read(entry.getValue())));
            this.close();
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> PStream<T> map(@NonNull Function<V, T> mappingFunction) {
        this.run(entry -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (DataOut out = DataOut.wrap(baos)) {
                this.configuration.getSerialization().write(mappingFunction.apply(this.read(entry.getValue())), out);
            }
            this.delegate.put(entry.getKey(), baos.toByteArray());
        });
        return (PStream<T>) this;
    }

    @Override
    public PStream<V> filter(@NonNull Predicate<V> condition) {
        this.run(entry -> {
            if (condition.test(this.read(entry.getValue()))) {
                this.delegate.delete(entry.getKey());
            }
        });
        return this;
    }

    @Override
    public PStream<V> distinct() {
        return null; //TODO
    }

    @Override
    public <Key, Value, T extends PMap<Key, Value>> T toMap(@NonNull Function<V, Key> keyExtractor, @NonNull Function<V, Value> valueExtractor, @NonNull Supplier<T> mapCreator) {
        T map = mapCreator.get();
        this.run(entry -> {
            V v = this.read(entry.getValue());
            map.put(keyExtractor.apply(v), valueExtractor.apply(v));
        });
        return map;
    }

    @Override
    public V[] toArray(@NonNull IntFunction<V[]> arrayCreator) {
        return arrayCreator.apply(-1);
    }

    @Override
    public void close() {
        this.ensureOpen();
        this.lock.lock();
        try {
            this.ensureOpen();
            this.cleaner.clean();
        } finally {
            this.lock.unlock();
        }
    }

    protected void ensureOpen() {
        if (this.cleaner.isCleaned()) {
            throw new DBNotOpenException();
        }
    }

    protected void run(@NonNull IOConsumer<Map.Entry<byte[], byte[]>> func) {
        this.lock.lock();
        try {
            this.ensureOpen();
            try (Snapshot snapshot = this.sourceDb == null ? this.delegate.getSnapshot() : this.sourceDb.getSnapshot();
                 DBIterator it = this.sourceDb == null ? this.delegate.iterator(new ReadOptions().snapshot(snapshot)) : this.sourceDb.iterator(new ReadOptions().snapshot(snapshot))) {
                if (this.concurrent) {
                    ConcurrencyHelper.runConcurrent(it, func);
                } else {
                    while (it.hasNext()) {
                        func.accept(it.next());
                    }
                }
            } catch (IOException e) {
                throw new DBReadException(e);
            } finally {
                if (this.sourceDb != null) {
                    this.sourceDb = null;
                    this.sourceSerializer = null;
                }
            }
        } finally {
            this.lock.unlock();
        }
    }

    protected V read(@NonNull byte[] b) throws IOException {
        DataIn in = DataIn.wrap(ByteBuffer.wrap(b));
        if (this.sourceDb != null) {
            return this.sourceSerializer.read(in);
        } else {
            return this.configuration.getSerialization().read(in);
        }
    }
}
