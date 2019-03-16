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

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.db.PorkDB;
import net.daporkchop.lib.db.container.Container;
import net.daporkchop.lib.db.container.ContainerType;
import net.daporkchop.lib.db.util.AbstractPorkDB;
import net.daporkchop.lib.db.util.exception.DBCloseException;
import net.daporkchop.lib.db.util.exception.DBOpenException;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
@Getter
public class LevelDB extends AbstractPorkDB<LevelDBContainerFactory, LevelDB> {
    protected final LevelDBBuilder builder;
    protected final Options dbOptions;
    protected final DB db;

    public LevelDB(@NonNull LevelDBBuilder builder) {
        super(builder.validate(), new LevelDBContainerFactory());

        try {
            this.builder = builder;
            this.factory.levelDb = this;

            this.dbOptions = new Options()
                    .createIfMissing(builder.createIfMissing)
                    .errorIfExists(builder.errorIfExists)
                    .writeBufferSize(builder.writeBufferSize)
                    .maxOpenFiles(builder.maxOpenFiles)
                    .blockRestartInterval(builder.blockRestartInterval)
                    .maxFileSize(builder.maxFileSize)
                    .blockSize(builder.blockSize)
                    .compressionType(builder.compressionType)
                    .paranoidChecks(builder.paraniodChecksumChecks)
                    .logger(builder.logger)
                    .cacheSize(builder.cacheSize);

            if (builder.sharedDb) {
                this.db = builder.dbFactory.open(builder.path, this.dbOptions);
            } else {
                this.db = null;
            }
        } catch (Exception e) {
            throw new DBOpenException(e);
        }
    }

    @Override
    protected void doPreClose() throws IOException {
        if (this.db != null)    {
            this.db.close();
        }
    }

    @Override
    protected void doPostClose() throws IOException {
    }
}
