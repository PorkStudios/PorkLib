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

package net.daporkchop.lib.dbextensions.leveldb.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.serialization.Serialization;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.db.util.exception.DBOpenException;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBFactory;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.File;
import java.io.IOException;

/**
 * Configuration for a LevelDB-backed collection.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public class LevelDBConfiguration {
    @NonNull
    protected final Options options;
    @NonNull
    protected final DBFactory factory;
    @NonNull
    protected final File path;
    protected Serialization serialization = Serialization.DEFAULT_REGISTRY;

    public LevelDBConfiguration(@NonNull File path) {
        this(new Options(), Iq80DBFactory.factory, path);
    }

    public LevelDBConfiguration(@NonNull Options options, @NonNull File path) {
        this(options, Iq80DBFactory.factory, path);
    }

    public LevelDBConfiguration(@NonNull DBFactory factory, @NonNull File path) {
        this(new Options(), factory, path);
    }

    /**
     * Opens a LevelDB using this configuration.
     *
     * @return the newly opened LevelDB
     * @throws DBOpenException if an exception occurs while opening the database
     */
    public DB openDB() throws DBOpenException {
        PFiles.rm(new File(this.path, "children"));
        return this.openDB(this.path);
    }

    /**
     * Opens a LevelDB using this configuration, in a given subpath of this configuration's path.
     *
     * @param subpath the subpath to use
     * @return the newly opened LevelDB
     * @throws DBOpenException if an exception occurs while opening the database
     */
    public DB openDB(@NonNull String subpath) throws DBOpenException {
        return this.openDB(new File(this.path, String.format("children/%s", subpath)));
    }

    protected DB openDB(@NonNull File path) throws DBOpenException {
        try {
            return this.factory.open(PFiles.ensureDirectoryExists(this.path), this.options);
        } catch (IOException e) {
            throw new DBOpenException(e);
        }
    }
}
