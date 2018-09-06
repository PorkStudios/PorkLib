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
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.db.object.key.KeyHasher;
import net.daporkchop.lib.db.object.serializer.ValueSerializer;

import java.io.File;

/**
 * Used for constructing an instance of {@link PorkDB}
 *
 * @author DaPorkchop_
 */
@Accessors(chain = true)
@Getter
@Setter
public class DBBuilder<K, V> {
    /**
     * The key hasher to use for hashing keys.
     * <p>
     * If the key hasher cannot reconstruct a key from its hash, then all functions that
     * supply a key for an entry will simply supply null.
     * <p>
     * Must be set!
     */
    @NonNull
    private KeyHasher<K> keyHasher;

    /**
     * The serializer to use for serializing entries to be written.
     * <p>
     * Must be set!
     */
    @NonNull
    private ValueSerializer<V> valueSerializer;

    /**
     * The root folder for this database.
     * <p>
     * Must be set!
     */
    @NonNull
    private File rootFolder;

    /**
     * The maximum number of files to keep open at a time. Files that contain an entry that is
     * loaded are never closed, and therefore don't count towards this total.
     * <p>
     * Of course, if an entry needs to be modified immediately, and the maximum open file count is reached,
     * and no currently open files can be unloaded, a new file will be opened and
     * then closed directly after completing the operation.
     */
    private int maxOpenFiles = 32;

    /**
     * Whether or not the database should be force-opened or not.
     * <p>
     * This can be to recover from a crash, power loss or another situation where
     * the database wasn't shut down correctly. This also can be to open a DB that was saved
     * using different settings than the currently set ones.
     */
    private boolean forceOpen;

    /**
     * The format used for this database for storing data.
     * <p>
     * May not be changed, or the database will fail to load!
     */
    @NonNull
    private DatabaseFormat format = DatabaseFormat.TREE;

    public PorkDB<K, V> build() {
        switch (this.format) {
            case TREE:
                return new TreeDBImpl<>(this);
            case ZIP_TREE:
                return new ArchiveDBImpl<>(this, "zip");
            case TAR_TREE:
                return new ArchiveDBImpl<>(this, "tar");
            case TAR_GZ_TREE:
                return new ArchiveDBImpl<>(this, "tar.gz");
            case TAR_XZ_TREE:
                return new ArchiveDBImpl<>(this, "tar.xz");
        }

        throw new IllegalStateException("Unknown database format: " + this.format.name());
    }
}
