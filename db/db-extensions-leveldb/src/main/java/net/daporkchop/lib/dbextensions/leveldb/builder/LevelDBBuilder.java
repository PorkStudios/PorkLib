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

package net.daporkchop.lib.dbextensions.leveldb.builder;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.db.builder.DBBuilder;
import net.daporkchop.lib.dbextensions.leveldb.util.LevelDBConfiguration;
import org.iq80.leveldb.DBFactory;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.File;

/**
 * A base builder class for all LevelDB-backed collections.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public abstract class LevelDBBuilder<Impl extends LevelDBBuilder<Impl, T>, T> implements DBBuilder<Impl, T> {
    /**
     * Additional LevelDB options to use when opening the database. If {@code null}, a default instance will be used.
     */
    protected Options options;

    /**
     * The path to store the database in. Must be set!
     */
    protected File path;

    /**
     * The LevelDB factory to use. If {@code null}, {@link Iq80DBFactory#factory} will be used.
     */
    protected DBFactory factory;

    @SuppressWarnings("unchecked")
    public Impl options(Options options) {
        this.options = options;
        return (Impl) this;
    }

    @SuppressWarnings("unchecked")
    public Impl path(File path) {
        this.path = path;
        return (Impl) this;
    }

    public LevelDBConfiguration configuration() {
        return new LevelDBConfiguration(
                this.options == null ? new Options() : this.options,
                this.factory == null ? Iq80DBFactory.factory : this.factory,
                this.path
        );
    }
}
