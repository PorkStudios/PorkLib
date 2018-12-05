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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.db.container.AbstractContainer;
import net.daporkchop.lib.db.local.LocalDB;
import net.daporkchop.lib.db.remote.RemoteDB;
import net.daporkchop.lib.logging.Logging;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the various {@link AbstractContainer}s for a database
 *
 * @author DaPorkchop_
 */
public abstract class PorkDB<DB extends PorkDB<DB, CC>, CC extends Container<?, ?, DB>> implements Logging {
    @Getter(AccessLevel.PACKAGE)
    protected final Map<String, CC> loadedContainers = new ConcurrentHashMap<>();
    @Getter
    protected volatile boolean open = true;

    protected PorkDB(@NonNull Builder builder) {
    }

    public static LocalDB.Builder local() {
        return new LocalDB.Builder();
    }

    public static LocalDB.Builder local(@NonNull File root) {
        return new LocalDB.Builder(root);
    }

    public static RemoteDB.Builder remote() {
        return new RemoteDB.Builder();
    }

    public static RemoteDB.Builder remote(@NonNull InetSocketAddress remoteAddress) {
        return new RemoteDB.Builder(remoteAddress);
    }

    /**
     * Get a currently loaded container
     *
     * @param name the name of the container to load
     * @param <C>  the type of the container to get (for convenience)
     * @return a container with the given name, or null if none was currently loaded.
     */
    @SuppressWarnings("unchecked")
    public <C extends CC> C get(@NonNull String name) {
        this.ensureOpen();
        return (C) this.loadedContainers.get(name);
    }

    /**
     * Saves the content of every loaded container to disk. The exact behavior of this may vary across
     * various {@link AbstractContainer} implementations.
     *
     * @throws IOException if a IO exception occurs you dummy
     */
    public abstract void save() throws IOException;

    /**
     * Closes the database, unloading all currently loaded containers.
     *
     * @throws IOException if a IO exception occurs you dummy
     */
    public abstract void close() throws IOException;

    protected void ensureOpen() {
        if (!this.isOpen()) {
            throw new IllegalStateException("Database already closed!");
        }
    }

    /**
     * Checks if this database is remote
     *
     * @return whether or not this is a remote database
     */
    public abstract boolean isRemote();

    @Accessors(chain = true)
    @Getter
    @Setter
    public static abstract class Builder<B extends Builder<B, DB>, DB extends PorkDB> {
        /**
         * Constructs a new {@link PorkDB} using the settings from this builder
         *
         * @return a new instance of {@link PorkDB} based on this builder
         */
        public abstract DB build();
    }
}
