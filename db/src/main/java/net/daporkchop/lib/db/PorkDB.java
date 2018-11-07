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

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.db.io.FileManager;
import net.daporkchop.lib.db.object.key.KeyHasher;
import net.daporkchop.lib.db.object.serializer.ValueSerializer;
import net.daporkchop.lib.db.util.LoadedEntry;
import net.daporkchop.lib.db.util.exception.DatabaseClosedException;
import net.daporkchop.lib.db.util.exception.WrappedException;
import net.daporkchop.lib.encoding.compression.EnumCompression;
import net.daporkchop.lib.nbt.NBTIO;
import net.daporkchop.lib.nbt.tag.impl.notch.CompoundTag;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * Common methods for all database format implementations
 *
 * @author DaPorkchop_
 */
public abstract class PorkDB<K, V> {
    /**
     * The file name extension for entry files.
     * <p>
     * dbe stands for Database Entry
     */
    public static final String ENTRY_ENDING = ".dbe";

    /**
     * The file name extension for the root database file.
     */
    public static final String ROOT_ENDING = ".porkdb";

    /**
     * The manager for open files on the file system.
     */
    @Getter
    private final FileManager fileManager;

    /**
     * See respective javadoc in {@link DBBuilder}
     */
    @Getter
    private final KeyHasher<K> keyHasher;

    /**
     * See respective javadoc in {@link DBBuilder}
     */
    @Getter
    private final ValueSerializer<V> valueSerializer;

    /**
     * See respective javadoc in {@link DBBuilder}
     */
    @Getter
    private final File rootFolder;

    /**
     * The database's main settings file
     */
    private final File rootFile;

    /**
     * Used to prevent many redundant byte array allocations for key hashes. The
     * byte array containing the key hash is perpetually recycled from this {@link ThreadLocal}
     */
    private final ThreadLocal<byte[]> keyHashBuf;
    /**
     * All entries that are currently loaded.
     * <p>
     * If the {@link KeyHasher} is incapable of deserializing key hashes and no key serializer is set, the
     * key parameter of the {@link LoadedEntry} instances will always be null.
     */
    private final Map<Long, LoadedEntry<K, V>> loadedEntries = new ConcurrentHashMap<>();
    /**
     * Whether or not this database instance is currently open.
     */
    @Getter
    private volatile boolean open = true;

    /**
     * Constructs a PorkDB instance from a database builder.
     *
     * @param builder the database builder to grab settings from.
     */
    public PorkDB(@NonNull DBBuilder<K, V> builder, @NonNull FileManager manager) {
        if (builder.getRootFolder() == null
                || builder.getKeyHasher() == null
                || builder.getValueSerializer() == null) {
            throw new IllegalArgumentException("Builder not initialized!");
        }

        this.valueSerializer = builder.getValueSerializer();
        this.rootFolder = builder.getRootFolder();
        if (!this.rootFolder.exists() && !this.rootFolder.mkdirs()) {
            throw new IllegalStateException("Unable to create folder: " + this.rootFolder.getAbsoluteFile().getAbsolutePath());
        }
        this.fileManager = manager;
        this.keyHasher = builder.getKeyHasher();
        this.keyHashBuf = ThreadLocal.withInitial(() -> new byte[this.keyHasher.getKeyLength()]);

        this.rootFile = new File(this.rootFolder, "root" + ROOT_ENDING);
        if (this.rootFile.exists()) {
            CompoundTag tag = NBTIO.read(this.rootFile);
            if (!builder.isForceOpen()) {
                if (!tag.getBoolean("closed")) {
                    throw new IllegalStateException("Database was not closed safely! Use DBBuilder#setForceOpen to force opening this database.");
                }
            }
            tag.putBoolean("closed", false);

            NBTIO.write(tag, this.rootFile);
        } else {
            CompoundTag tag = new CompoundTag();
            tag.putBoolean("closed", false);
            NBTIO.write(tag, this.rootFile);
        }
        this.fileManager.setDb(this);
        this.fileManager.init();
    }

    public static Long hash(@NonNull byte[] bytes) {
        long l = 524287L;
        for (byte b : bytes) {
            l *= (b == 0 ? 31 : b) * 63L;
        }
        return l;
    }

    /**
     * Gets a value from the database.
     *
     * @param key the key to get
     * @return an instance of V, or null if no entry was found.
     */
    public V get(@NonNull K key) {
        this.ensureDatabaseOpen();

        byte[] keyHash = this.keyHashBuf.get();
        this.keyHasher.hash(key, keyHash);

        Long hash = hash(keyHash);
        if (this.loadedEntries.containsKey(hash)) {
            return this.loadedEntries.get(hash).getValue();
        }

        try {
            InputStream in = this.fileManager.getStream(keyHash, hash);
            if (in == null) {
                return null;
            }
            EnumCompression compression = EnumCompression.values()[in.read()];
            DataIn dataIn = DataIn.wrap(compression.inflateStream(in));
            V val = this.valueSerializer.read(dataIn);
            dataIn.close();
            return val;
        } catch (IOException e) {
            e.printStackTrace();
            throw new WrappedException(e);
        }
    }

    /**
     * Puts a value to the database, overwriting existing values if already present.
     *
     * @param key   the key to put at
     * @param value the value to put
     */
    public void put(@NonNull K key, @NonNull V value) {
        this.put(key, value, EnumCompression.NONE);
    }

    /**
     * Puts a value to the database, overwriting existing values if already present.
     *
     * @param key         the key to put at
     * @param value       the value to put
     * @param compression the compression to use
     */
    public void put(@NonNull K key, @NonNull V value, @NonNull EnumCompression compression) {
        this.ensureDatabaseOpen();

        byte[] keyHash = this.keyHashBuf.get();
        this.keyHasher.hash(key, keyHash);

        Long hash = hash(keyHash);
        if (this.loadedEntries.containsKey(hash)) {
            this.loadedEntries.get(hash).setValue(value);
            return;
        }

        try {
            OutputStream out = this.fileManager.putStream(keyHash, hash);
            out.write(compression.ordinal());
            DataOut dataOut = DataOut.wrap(compression.compressStream(out));
            this.valueSerializer.write(value, dataOut);
            dataOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes an element from the database
     *
     * @param key the key to remove
     */
    public void remove(@NonNull K key) {
        this.ensureDatabaseOpen();

        byte[] keyHash = this.keyHashBuf.get();
        this.keyHasher.hash(key, keyHash);

        Long hash = hash(keyHash);
        this.loadedEntries.remove(hash);

        this.fileManager.remove(keyHash, hash);
    }

    /**
     * Checks if the database contains a given key
     *
     * @param key the key to check for
     * @return whether or not the database contains the given key
     */
    public boolean contains(@NonNull K key) {
        this.ensureDatabaseOpen();

        byte[] keyHash = this.keyHashBuf.get();
        this.keyHasher.hash(key, keyHash);

        Long hash = hash(keyHash);
        return this.loadedEntries.containsKey(hash) || this.fileManager.contains(keyHash, hash);
    }

    /**
     * Loads the given entry into memory.
     * <p>
     * Methods such as {@link #get(Object)} and {@link #contains(Object)} will use the cached entry
     * instead of the one on disk until the entry is unloaded.
     * <p>
     * When unloaded, the entry will be serialized and written to disk unless otherwise specified.
     * <p>
     * All loaded entries are unloaded and written when the database is shut down.
     *
     * @param key the key of the entry to load
     * @return the newly loaded entry
     */
    public LoadedEntry<K, V> load(@NonNull K key) {
        this.ensureDatabaseOpen();

        byte[] keyHash = this.keyHashBuf.get();
        this.keyHasher.hash(key, keyHash);

        Long hash = hash(keyHash);
        if (this.loadedEntries.containsKey(hash)) {
            return this.loadedEntries.get(hash);
        }
        LoadedEntry<K, V> entry = new LoadedEntry<>(key, this.get(key));
        this.loadedEntries.put(hash, entry);
        return entry;
    }

    /**
     * Unloads a loaded entry from memory.
     * <p>
     * If the entry with the given key is not loaded, nothing is done.
     *
     * @param key the key of the entry to unload
     */
    public void unload(@NonNull K key) {
        this.unload(key, true);
    }

    /**
     * Unloads a loaded entry from memory.
     * <p>
     * If the entry with the given key is not loaded, nothing is done.
     *
     * @param key   the key of the entry to unload
     * @param write whether or not to serialize and write the current in-memory value to disk
     */
    public void unload(@NonNull K key, boolean write) {
        this.ensureDatabaseOpen();

        byte[] keyHash = this.keyHashBuf.get();
        this.keyHasher.hash(key, keyHash);

        Long hash = hash(keyHash);
        if (this.loadedEntries.containsKey(hash)) {
            LoadedEntry<K, V> entry = this.loadedEntries.remove(hash);
            if (write) {
                this.put(key, entry.getValue());
            }
        }
    }

    /**
     * Checks if the given key is loaded
     *
     * @param key the key to check for
     * @return whether or not the key is loaded
     */
    public boolean isLoaded(@NonNull K key) {
        this.ensureDatabaseOpen();

        byte[] keyHash = this.keyHashBuf.get();
        this.keyHasher.hash(key, keyHash);

        Long hash = hash(keyHash);
        return this.loadedEntries.containsKey(hash);
    }

    /**
     * Iterates over every entry in the database, and executes a function on them
     *
     * @param consumer the function to run
     */
    public void forEach(@NonNull BiConsumer<K, V> consumer) {
        boolean keyDeserialize = this.keyHasher.canGetKeyFromHash();
        this.fileManager.forEach((keyBytes, stream) -> {
            try {
                stream = EnumCompression.values()[stream.read()].inflateStream(stream);
                V value = this.valueSerializer.read(DataIn.wrap(stream));
                stream.close();
                consumer.accept(keyDeserialize ? this.keyHasher.getKeyFromHash(keyBytes) : null, value);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Shuts down the database, blocking until all files have been closed.
     * <p>
     * The database is no longer usable after calling this method.
     */
    public void shutdown() {
        this.ensureDatabaseOpen();

        this.open = false;

        this.loadedEntries.values().stream().map(LoadedEntry::getKey).forEach(this::unload);
        this.loadedEntries.clear();
        this.fileManager.shutdown();
    }

    /**
     * Throws a {@link DatabaseClosedException} if the database is closed.
     */
    private void ensureDatabaseOpen() {
        if (!this.open) {
            throw new DatabaseClosedException();
        }
    }
}
