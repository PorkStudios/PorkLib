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

package net.daporkchop.lib.db;

import lombok.*;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.function.IOEFunction;

import java.io.File;
import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Manages the various {@link Container}s for a database
 *
 * @author DaPorkchop_
 */
public class PorkDB {
    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(@NonNull File root) {
        return new Builder().setRoot(root);
    }

    private final Map<Class<? extends Container>, Function<String, ? extends Container.Builder>> builderCache = new IdentityHashMap<>();
    @Getter
    private final File root;

    final Map<String, Container> loadedContainers = new ConcurrentHashMap<>();

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
