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
import net.daporkchop.lib.db.container.bitset.PersistentSparseBitSet;
import net.daporkchop.lib.db.container.map.DBMap;
import net.daporkchop.lib.db.data.value.ConstantLengthSerializer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * An implementation of {@link DataLookup} that stores everything in a single file with
 * minimal overhead.
 * <p>
 * This requires that a subclass of {@link net.daporkchop.lib.db.data.value.ConstantLengthSerializer} is used.
 *
 * @author DaPorkchop_
 */
public class ConstantLengthLookup implements DataLookup {
    private PersistentSparseBitSet ids;
    @Getter
    private File file;
    private RandomAccessFile raf;
    private FileChannel channel;
    private int dataLength;

    @Override
    public void init(@NonNull DBMap<?, ?> map, @NonNull File file) throws IOException {
        if (this.file != null) {
            throw new IllegalStateException("already initialized");
        }
        if (!(map.getValueSerializer() instanceof ConstantLengthSerializer)) {
            throw new IllegalStateException(String.format("ConstantLengthLookup requires a subclass of %s, but found %s", ConstantLengthSerializer.class.getCanonicalName(), map.getValueSerializer().getClass().getCanonicalName()));
        } else {
            this.dataLength = ((ConstantLengthSerializer) map.getValueSerializer()).getSize();
        }
        this.ids = new PersistentSparseBitSet(new File(file, "occupied.bitmap"));
        this.file = file = new File(file, "data");
        if (!file.exists()) {
            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                throw new IllegalStateException(String.format("Couldn't create directory: %s", file.getParentFile().getAbsolutePath()));
            } else if (!file.createNewFile()) {
                throw new IllegalStateException(String.format("Couldn't create file: %s", file.getAbsolutePath()));
            }
        }
        this.raf = new RandomAccessFile(file, "rw");
        this.channel = this.raf.getChannel();
        DataLookup.super.init(map, file);
    }

    @Override
    public void load() throws IOException {
        this.ids.load();
    }

    @Override
    public void clear() throws IOException {
        synchronized (this) {
            this.ids.clear();
            this.channel.truncate(0L);
        }
    }

    @Override
    public void save() throws IOException {
        synchronized (this) {
            this.ids.save();
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (this) {
            this.channel.close();
            this.raf.close();
            this.channel = null;
            this.raf = null;
            this.file = null;
            this.ids.close();
            this.ids = null;
        }
    }

    @Override
    public DataIn read(long id) throws IOException {
        synchronized (this) {
            if (this.ids.get((int) id)) {
                ByteBuffer buffer = ByteBuffer.allocateDirect(this.dataLength);
                this.channel.read(buffer, id * this.dataLength);
                buffer.rewind();
                return DataIn.wrap(buffer);
            } else {
                return null;
            }
        }
    }

    @Override
    public long write(long id, @NonNull IOConsumer<DataOut> writer) throws IOException {
        synchronized (this) {
            if (id == -1L || !this.ids.get((int) id)) {
                id = this.ids.getBitSet().nextClearBit(0);
                this.ids.set((int) id);
            }
            ByteBuffer buffer = ByteBuffer.allocateDirect(this.dataLength);
            try (DataOut out = DataOut.wrap(buffer)) {
                writer.accept(out);
            }
            buffer.flip();
            this.channel.write(buffer, id * this.dataLength);
            return id;
        }
    }

    @Override
    public void remove(long id) throws IOException {
        synchronized (this) {
            this.ids.getBitSet().clear((int) id);
        }
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
