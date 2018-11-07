package net.daporkchop.lib.db;

import lombok.*;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.function.IOEFunction;
import net.daporkchop.lib.db.container.Container;
import net.daporkchop.lib.db.container.impl.DBAtomicLong;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Manages the various {@link Container}s for a database
 *
 * @author DaPorkchop_
 */
public class PorkDB {
    public static Builder builder()    {
        return new Builder();
    }

    @Getter
    private final File root;

    private final Map<String, Container> loadedContainers = new ConcurrentHashMap<>();

    private final Object saveLock = new Object();
    @Getter
    private volatile boolean open = true;

    private PorkDB(@NonNull Builder builder) {
        this.root = builder.root;
    }

    /**
     * Get a currently loaded container
     *
     * @param name the name of the container to load
     * @param <C>  the type of the container to get (for convenience)
     * @return a container with the given name, or null if none was currently loaded.
     */
    @SuppressWarnings("unchecked")
    public <C extends Container> C get(@NonNull String name) {
        this.ensureOpen();
        return (C) this.loadedContainers.get(name);
    }

    /**
     * Get a {@link DBAtomicLong}, loading/creating it if required.
     *
     * @param name       the container name
     * @param populators functions to be run on the builder to initialize the container
     *                   as needed
     * @return an instance of {@link DBAtomicLong}
     */
    public DBAtomicLong loadAtomicLong(@NonNull String name, @NonNull Consumer<DBAtomicLong.Builder>... populators) {
        this.ensureOpen();
        return this.computeIfAbsent(name, n -> this.load(new DBAtomicLong.Builder(this, name), populators));
    }

    /**
     * Saves the content of every loaded container to disk. The exact behavior of this may vary across
     * various {@link Container} implementations.
     *
     * @throws IOException if an io exception occurs? duh
     */
    public void save() throws IOException {
        this.ensureOpen();
        synchronized (this.saveLock) {
            for (Container container : this.loadedContainers.values()) {
                container.save();
            }
        }
    }

    /**
     * Closes the database, unloading all currently loaded containers.
     *
     * @throws IOException if an io exception occurs? duh
     */
    public void close() throws IOException {
        this.ensureOpen();
        synchronized (this.saveLock) { //TODO: read-write locking implementation
            this.open = false;
            try {
                for (Iterator<Container> iter = this.loadedContainers.values().iterator(); iter.hasNext(); iter.next().save()) {
                }
            } finally {
                this.loadedContainers.clear();
            }
        }
    }

    private void ensureOpen() {
        if (!this.isOpen()) {
            throw new IllegalStateException("Database already closed!");
        }
    }

    @SuppressWarnings("unchecked")
    private <V, C extends Container<V, ? extends Container.Builder<V, C>>> C computeIfAbsent(@NonNull String name, @NonNull IOEFunction<String, C> creator) {
        return (C) this.loadedContainers.computeIfAbsent(name, creator);
    }

    @SuppressWarnings("unchecked")
    private <V, C extends Container<V, B>, B extends Container.Builder<V, C>> C load(@NonNull B builder, @NonNull Consumer<B>... populators) throws IOException {
        for (Consumer<B> p : populators) {
            if (p == null) {
                throw new NullPointerException();
            }
            p.accept(builder);
        }
        return builder.build();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Accessors(chain = true)
    @Getter
    @Setter
    public static final class Builder {
        @NonNull
        private File root;

        public PorkDB build() {
            if (this.root == null) {
                throw new IllegalStateException("root must be set!");
            }

            return new PorkDB(this);
        }
    }
}
