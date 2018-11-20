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
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.db.container.bitset.PersistentSparseBitSet;
import net.daporkchop.lib.db.container.map.DBMap;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
public class IndividualFileLookup implements DataLookup {
    private PersistentSparseBitSet ids;
    @Getter
    private File file;
    private File dataFile;

    @Override
    public void init(@NonNull DBMap<?, ?> map, @NonNull File file) throws IOException {
        this.ids = new PersistentSparseBitSet(new File(file, "ids.bitmap"));
        this.dataFile = new File(file, "data");
        if (!this.dataFile.exists() && !this.dataFile.mkdirs()) {
            throw new IllegalStateException(String.format("Couldn't create directory: %s", this.dataFile.getAbsolutePath()));
        } else if (!this.dataFile.isDirectory()) {
            throw new IllegalStateException(String.format("Not a directory: %s", this.dataFile.getAbsolutePath()));
        }
        this.file = file;
    }

    @Override
    public void load() throws IOException {
        this.ids.load();
    }

    @Override
    public void clear() throws IOException {
        this.ids.clear();
        PorkUtil.rm(this.dataFile);
        if (!this.dataFile.mkdirs()) {
            throw new IllegalStateException(String.format("Couldn't create directory: %s", this.dataFile.getAbsolutePath()));
        }
    }

    @Override
    public DataIn read(long id) throws IOException {
        File file = new File(this.dataFile, String.format("%d/%d.dat", id >>> 8L, id & 0xFFL));
        if (!file.exists() || !file.isFile()) {
            return null;
        } else {
            return DataIn.wrap(file);
        }
    }

    @Override
    public long write(long id, @NonNull Consumer<DataOut> writer) throws IOException {
        if (id == -1L)  {
            synchronized (this.ids) {
                id = this.ids.getBitSet().nextClearBit(0);
                this.ids.set((int) id);
            }
        }
        File file = new File(this.dataFile, String.format("%d/%d.dat", id >>> 8L, id & 0xFFL));
        if (!file.exists()) {
            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                throw new IllegalStateException(String.format("Couldn't create directory: %s", file.getParentFile().getAbsolutePath()));
            } else if (!file.createNewFile()) {
                throw new IllegalStateException(String.format("Couldn't create new file: %s", file.getAbsolutePath()));
            }
        }
        try (DataOut out = DataOut.wrap(file, 4096))  {
            writer.accept(out);
        }
        return id;
    }

    @Override
    public void remove(long id) throws IOException {
        File file = new File(this.dataFile, String.format("%d/%d.dat", id >>> 8L, id & 0xFFL));
        if (file.exists() && !file.delete())    {
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
}
