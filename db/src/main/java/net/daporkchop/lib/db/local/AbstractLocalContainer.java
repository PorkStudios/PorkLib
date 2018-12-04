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

package net.daporkchop.lib.db.local;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.function.IOConsumer;
import net.daporkchop.lib.db.Container;
import net.daporkchop.lib.db.PorkDB;
import net.daporkchop.lib.db.container.AbstractContainer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author DaPorkchop_
 */
@Getter
public abstract class AbstractLocalContainer<V, B extends AbstractLocalContainer.Builder<V, ? extends AbstractLocalContainer<V, B>>> extends AbstractContainer<V, B, LocalDB> implements LocalContainer<V, B> {
    protected final File file;

    public AbstractLocalContainer(@NonNull B builder) throws IOException {
        super(builder);

        this.file = new File(this.db.getRoot(), this.name);
        if (this.file.exists()) {
            if (this.usesDirectory()) {
                if (!this.file.isDirectory()) {
                    throw new IllegalStateException(String.format("Not a directory: %s", this.file.getAbsolutePath()));
                }
            } else if (!this.file.isFile()) {
                throw new IllegalStateException(String.format("Not a file: %s", this.file.getAbsolutePath()));
            }
        } else if (this.usesDirectory() && !this.file.mkdirs()) {
            throw new IllegalStateException(String.format("Could not create directory: %s", this.file.getAbsolutePath()));
        } else {
            File parent = this.file.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                throw this.exception("Couldn't create directory: ${0}", parent);
            } else if (!this.file.exists() && !this.file.createNewFile())   {
                throw this.exception("Couldn't create file: ${0}", this.file);
            }
        }
    }

    @Override
    public void save() throws IOException {
        this.lock.writeLock().lock();
        try {
            if (this.dirty) {
                this.doSave();
                this.dirty = false;
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    protected abstract void doSave() throws IOException;

    public static abstract class Builder<V, C extends LocalContainer<V, ? extends AbstractLocalContainer.Builder<V, C>>> extends Container.Builder<V, C, LocalDB>    {
        protected Builder(LocalDB db, String name) {
            super(db, name);
        }

        @Override
        public C buildIfPresent() throws IOException {
            if (new File(this.db.getRoot(), this.name).exists()) {
                return this.build();
            } else {
                return null;
            }
        }
    }
}
