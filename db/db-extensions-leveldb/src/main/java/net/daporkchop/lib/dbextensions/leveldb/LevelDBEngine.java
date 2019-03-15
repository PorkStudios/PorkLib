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
import net.daporkchop.lib.common.setting.Settings;
import net.daporkchop.lib.db.PorkDB;
import net.daporkchop.lib.db.container.ContainerType;
import net.daporkchop.lib.db.engine.DBEngine;
import net.daporkchop.lib.db.engine.EngineContainerTypeInfo;
import net.daporkchop.lib.db.util.exception.DBCloseException;
import net.daporkchop.lib.dbextensions.leveldb.container.LevelDBMap;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBFactory;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static net.daporkchop.lib.dbextensions.leveldb.OptionsLevelDB.*;

/**
 * @author DaPorkchop_
 */
@Getter
public class LevelDBEngine implements DBEngine {
    protected static final EngineContainerTypeInfo TYPE_INFO = new EngineContainerTypeInfo<LevelDBEngine>()
            .configure(ContainerType.MAP, LEVELDB_MAP_OPTIONS, LevelDBMap::new, LevelDBMap.class);

    public static LevelDBBuilder builder()  {
        return new LevelDBBuilder();
    }

    protected PorkDB parent;

    protected final AtomicBoolean closed = new AtomicBoolean(false);

    protected final Settings settings;
    protected DB delegate;

    public LevelDBEngine(@NonNull Settings settings) {
        this.settings = settings.validateMatches(LEVELDB_INIT_OPTIONS);
    }

    @Override
    public synchronized void init(@NonNull PorkDB parent) throws IOException {
        if (this.delegate == null)  {
            this.parent = parent;

            Consumer<String> logger = this.settings.get(LOGGER);
            DBFactory factory = this.settings.get(DB_FACTORY);
            if (factory == null)    {
                factory = Iq80DBFactory.factory;
            }
            this.delegate = factory.open(
                    this.settings.get(PATH),
                    new Options()
                            .createIfMissing(this.settings.get(CREATE_IF_MISSING))
                            .errorIfExists(this.settings.get(ERROR_IF_EXISTS))
                            .writeBufferSize(this.settings.get(WRITE_BUFFER_SIZE))
                            .maxOpenFiles(this.settings.get(MAX_OPEN_FILES))
                            .blockRestartInterval(this.settings.get(BLOCK_RESTART_INTERVAL))
                            .blockSize(this.settings.get(BLOCK_SIZE))
                            .compressionType(this.settings.get(COMPRESSION_TYPE))
                            .verifyChecksums(this.settings.get(VERIFY_CHECKSUMS))
                            .paranoidChecks(this.settings.get(PARANIOD_CHECKS))
                            .logger(msg -> logger.accept(msg))
                            .cacheSize(this.settings.get(CACHE_SIZE))
            );
        } else {
            throw new IllegalStateException("Already initialized!");
        }
    }

    @Override
    public EngineContainerTypeInfo getTypeInfo() {
        return TYPE_INFO;
    }

    @Override
    public void close() {
        try {
            if (this.closed.getAndSet(true)) {
                throw new IllegalStateException("Already closed!");
            } else {
                this.delegate.close();
            }
        } catch (IOException e) {
            throw new DBCloseException(e);
        }
    }

    @Override
    public boolean isClosed() {
        return this.closed.get();
    }
}
