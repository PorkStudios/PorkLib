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

package net.daporkchop.lib.db;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.db.container.DBAtomicLong;
import net.daporkchop.lib.db.container.map.DBMap;
import net.daporkchop.lib.logging.Logging;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Manages the various {@link Container}s for a database
 *
 * @author DaPorkchop_
 */
public class PorkDB implements Logging {
    final Map<String, Container> loadedContainers = new ConcurrentHashMap<>();
    private final Map<Class<? extends Container>, Function<String, ? extends Container.Builder>> builderCache = new IdentityHashMap<>();
    @Getter
    private final File root;
    private final Object saveLock = new Object();
    @Getter
    private volatile boolean open = true;

    private PorkDB(@NonNull Builder builder) {
        this.root = builder.root;
    }

    /**
     * Constructs a new {@link Builder}
     *
     * @return a blank instance of {@link Builder}
     */
    public static Builder builder() {
        return new Builder(null);
    }

    /**
     * Constructs a new {@link Builder}
     *
     * @param root the root folder of the {@link PorkDB} to be constructed
     * @return a new instance of {@link Builder} with the given root
     */
    public static Builder builder(@NonNull File root) {
        return new Builder(null).setRoot(root);
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
     * Get a new builder for a {@link DBAtomicLong}
     * <p>
     * Convenience wrapper around {@link DBAtomicLong#builder(PorkDB, String)}
     *
     * @param name the name of the new entry
     * @return a new builder
     */
    public DBAtomicLong.Builder atomicLong(@NonNull String name) {
        if (this.loadedContainers.containsKey(name)) {
            throw this.exception("Name \"${0}\" already taken!", name);
        } else {
            return DBAtomicLong.builder(this, name);
        }
    }

    /**
     * Get a new builder for a {@link DBMap}
     * <p>
     * Convenience wrapper around {@link DBMap#builder(PorkDB, String)}
     *
     * @param name the name of the new entry
     * @param <K>  the key type
     * @param <V>  the value type
     * @return a new builder
     */
    public <K, V> DBMap.Builder<K, V> map(@NonNull String name) {
        if (this.loadedContainers.containsKey(name)) {
            throw this.exception("Name \"${0}\" already taken!", name);
        } else {
            return DBMap.builder(this, name);
        }
    }

    /**
     * Saves the content of every loaded container to disk. The exact behavior of this may vary across
     * various {@link Container} implementations.
     *
     * @throws IOException if a IO exception occurs you dummy
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
     * @throws IOException if a IO exception occurs you dummy
     */
    public void close() throws IOException {
        this.ensureOpen();
        synchronized (this.saveLock) { //TODO: read-write locking implementation
            this.open = false;
            try {
                for (Iterator<Container> iter = this.loadedContainers.values().iterator(); iter.hasNext(); iter.next().close()) {
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

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Accessors(chain = true)
    @Getter
    @Setter
    public static final class Builder {
        /**
         * The remote address of this database.
         * <p>
         * If this is a local database, this will be {@code null}
         */
        private final InetSocketAddress remoteAddress;

        /**
         * The root directory of the database.
         */
        private File root;

        /**
         * Constructs a new {@link PorkDB} using the settings from this builder
         *
         * @return a new instance of {@link PorkDB} based on this builder
         */
        public PorkDB build() {
            if (this.root == null) {
                throw new IllegalStateException("root must be set!");
            }

            return new PorkDB(this);
        }
    }
}
