package net.daporkchop.lib.db.container.impl.map;

import lombok.NonNull;
import net.daporkchop.lib.binary.data.Serializer;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.db.PorkDB;
import net.daporkchop.lib.db.container.Container;

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
    private volatile boolean dirty = false;
    private final AtomicLong size = new AtomicLong(0L);

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final Serializer<K> keySerializer;
    private final Serializer<V> valueSerializer;

    public DBMap(Builder<K, V> builder) throws IOException {
        super(builder);

        this.keySerializer = builder.keySerializer;
        this.valueSerializer = builder.valueSerializer;

        try (DataIn in = this.getIn("headers.dat", out -> {
            out.writeLong(0L); //size
        })) {
            this.size.set(in.readLong());
        }
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
    public boolean containsKey(Object key) {
        //TODO
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        //TODO
        return false;
    }

    @Override
    public V get(Object key) {
        //TODO
        return null;
    }

    @Override
    public V put(K key, V value) {
        //TODO
        return null;
    }

    @Override
    public V remove(Object key) {
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

    public static class Builder<K, V> extends Container.Builder<Map<K, V>, DBMap<K, V>> {
        private Serializer<K> keySerializer;
        @NonNull
        private Serializer<V> valueSerializer;

        protected Builder(PorkDB db, String name) {
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
