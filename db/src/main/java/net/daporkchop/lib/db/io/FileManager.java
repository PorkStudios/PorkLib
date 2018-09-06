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

package net.daporkchop.lib.db.io;

import lombok.Data;
import lombok.NonNull;
import lombok.Setter;
import net.daporkchop.lib.db.PorkDB;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;

/**
 * Manages open files for use by a database instance
 *
 * @author DaPorkchop_
 */
@Data
public abstract class FileManager {
    /**
     * Used to prevent multiple threads potentially opening the same file at the same time.
     */
    protected final transient Lock lock = new ReentrantLock();
    /**
     * The maximum number of open files. See respective javadoc in {@link net.daporkchop.lib.db.DBBuilder}
     */
    protected final int maxOpenFiles;
    /**
     * The database instance that this is backing.
     */
    @Setter
    protected PorkDB db;

    /**
     * Initializes this file manager.
     * <p>
     * {@link #db} will be set when this is invoked.
     */
    public abstract void init();

    /**
     * Get a byte array from a full key hash
     *
     * @param fullHash the full key hash
     * @return the data at the given key, or null if not present
     */
    public byte[] get(@NonNull byte[] fullHash, long hashHash) {
        return null;
    }

    /**
     * Puts a value byte array to the given key hash
     *
     * @param fullHash the full key hash
     * @param value    the value to put
     */
    public void put(@NonNull byte[] fullHash, @NonNull byte[] value, long hashHash) {
    }

    public abstract InputStream getStream(@NonNull byte[] fullHash, long hashHash);

    public abstract OutputStream putStream(@NonNull byte[] fullHash, long hashHash);

    /**
     * Removes an entry at a given key hash
     *
     * @param fullHash the full key hash
     */
    public abstract void remove(@NonNull byte[] fullHash, long hashHash);

    /**
     * Checks if an entry is present in the database
     *
     * @param fullHash the full key hash
     * @return whether or not the entry is present
     */
    public abstract boolean contains(@NonNull byte[] fullHash, long hashHash);

    /**
     * Iterates through every value in the map and executes a given function on it
     *
     * @param consumer the function to run
     */
    public abstract void forEach(@NonNull BiConsumer<byte[], InputStream> consumer);

    /**
     * Blocks the invoking thread until all files can be safely closed.
     */
    public abstract void shutdown();
}
