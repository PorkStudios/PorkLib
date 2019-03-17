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
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.UTF8;
import net.daporkchop.lib.common.function.io.IOBiFunction;
import net.daporkchop.lib.common.setting.Option;
import net.daporkchop.lib.common.setting.Settings;
import net.daporkchop.lib.db.builder.AbstractDBBuilder;
import net.daporkchop.lib.dbextensions.leveldb.util.PrefixGenerator;
import net.daporkchop.lib.hash.util.Digest;
import net.daporkchop.lib.logging.Logger;
import org.iq80.leveldb.CompressionType;
import org.iq80.leveldb.DBFactory;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
@Getter
@Setter
@Accessors(chain = true)
public class LevelDBBuilder extends AbstractDBBuilder<LevelDB, LevelDBBuilder> {
    @NonNull
    protected File path;
    protected boolean createIfMissing = true;
    protected boolean errorIfExists = false;
    protected int writeBufferSize = 4194304;
    protected int maxOpenFiles = 1000;
    protected int blockRestartInterval = 16;
    protected int maxFileSize = 2097152;
    protected int blockSize = 4096;
    @NonNull
    protected CompressionType compressionType = CompressionType.NONE;
    protected boolean paraniodChecksumChecks = false;
    @NonNull
    protected org.iq80.leveldb.Logger logger = Logger.DEFAULT_LOG::info;
    protected long cacheSize = 0L;

    @NonNull
    protected PrefixGenerator prefixGenerator = (type, name) -> {
        byte[] b = new byte[8];
        b[0] = (byte) type.ordinal();
        System.arraycopy(Digest.WHIRLPOOL.hash(name.getBytes(UTF8.utf8), b).getHash(), 0, b, 1, 7);
        return b;
    };
    /**
     * @see org.iq80.leveldb.impl.Iq80DBFactory
     * @see org.fusesource.leveldbjni.JniDBFactory
     */
    protected DBFactory dbFactory;
    protected boolean sharedDb = true; //by default, share the single leveldb instance with all containers

    @Override
    public LevelDBBuilder validate() {
        if (this.path == null)  {
            throw new NullPointerException("path");
        }
        return super.validate();
    }

    @Override
    public LevelDB build() {
        return new LevelDB(this);
    }
}
