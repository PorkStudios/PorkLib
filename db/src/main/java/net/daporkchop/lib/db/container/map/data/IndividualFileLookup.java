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

package net.daporkchop.lib.db.container.map.data;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.function.IOConsumer;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.db.container.map.DBMap;
import net.daporkchop.lib.db.util.PersistentSparseBitSet;

import java.io.File;
import java.io.IOException;

/**
 * A simple implementation of {@link DataLookup} that stores everything in it's own file.
 * <p>
 * This doesn't really have many practical usages that I can think of, it's mainly intended
 * for debugging.
 *
 * @author DaPorkchop_
 */
public class IndividualFileLookup implements DataLookup {
    private PersistentSparseBitSet ids;
    @Getter
    private File file;

    @Override
    public void init(@NonNull DBMap<?, ?> map, @NonNull File file) throws IOException {
        this.file = file;
        if (!this.file.exists() && !this.file.mkdirs()) {
            throw new IllegalStateException(String.format("Couldn't create directory: %s", this.file.getAbsolutePath()));
        } else if (!this.file.isDirectory()) {
            throw new IllegalStateException(String.format("Not a directory: %s", this.file.getAbsolutePath()));
        }
        this.ids = new PersistentSparseBitSet(new File(this.file, "ids.bitmap"));
        DataLookup.super.init(map, file);
    }

    @Override
    public void load() throws IOException {
        this.ids.load();
    }

    @Override
    public void clear() throws IOException {
        this.ids.clear();
        PorkUtil.rm(this.file);
        if (!this.file.mkdirs()) {
            throw new IllegalStateException(String.format("Couldn't create directory: %s", this.file.getAbsolutePath()));
        }
    }

    @Override
    public DataIn read(long id) throws IOException {
        File file = this.getFile(id);
        if (!file.exists() || !file.isFile()) {
            return null;
        } else {
            return DataIn.wrap(file);
        }
    }

    @Override
    public long write(long id, @NonNull IOConsumer<DataOut> writer) throws IOException {
        if (id == -1L || !this.ids.get((int) id)) {
            synchronized (this.ids) {
                id = this.ids.getBitSet().nextClearBit(0);
                this.ids.set((int) id);
            }
        }
        File file = this.getFile(id);
        if (!file.exists()) {
            File parent = file.getParentFile();
            boolean parentExists = parent.exists();
            if (!parentExists && !parent.mkdirs()) {
                throw new IllegalStateException(String.format("Couldn't create directory: %s", parent.getAbsolutePath()));
            } else if (parentExists && !parent.isDirectory()) {
                throw new IllegalStateException(String.format("Not a directory: %s", parent.getAbsolutePath()));
            } else if (!file.createNewFile()) {
                throw new IllegalStateException(String.format("Couldn't create new file: %s", file.getAbsolutePath()));
            }
        }
        try (DataOut out = DataOut.wrap(file, 4096)) {
            writer.accept(out);
        }
        return id;
    }

    @Override
    public void remove(long id) throws IOException {
        File file = this.getFile(id);
        if (file.exists() && !file.delete()) {
            throw new IllegalStateException(String.format("Couldn't delete file: %s", file.getAbsolutePath()));
        }
    }

    @Override
    public void save() throws IOException {
        this.ids.save();
    }

    @Override
    public boolean isDirty() {
        return this.ids.isDirty();
    }

    @Override
    public void setDirty(boolean dirty) {
        this.ids.setDirty(dirty);
    }

    @Override
    public boolean allowsCompression() {
        return true;
    }

    protected File getFile(long id) {
        return new File(this.file, String.format(
                "%d/%d/%d/%d",
                (id >>> 24L) & 0xFFL,
                (id >>> 16L) & 0xFFL,
                (id >>> 8L) & 0xFFL,
                id & 0xFFL
        ));
    }
}
