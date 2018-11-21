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
import lombok.Setter;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.binary.stream.HugeBufferOut;
import net.daporkchop.lib.binary.stream.file.BufferingFileInput;
import net.daporkchop.lib.common.function.IOConsumer;
import net.daporkchop.lib.db.container.map.DBMap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * An implementation of {@link DataLookup} that only allows writing a certain value once (i.e. values
 * cannot be removed or modified).
 * <p>
 * Useful for highly compact storage, but not much else
 *
 * @author DaPorkchop_
 */
public class OneTimeWriteDataLookup implements DataLookup {
    private MappedByteBuffer lengthWriteBuffer;

    @Getter
    private File file;
    private RandomAccessFile raf;
    private FileChannel channel;

    @Getter
    @Setter
    private volatile boolean dirty;

    @Override
    public void init(@NonNull DBMap<?, ?> map, @NonNull File file) throws IOException {
        if (this.file != null) {
            throw new IllegalStateException("already initialized!");
        }
        boolean flag = false;
        if (!file.exists()) {
            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                throw new IllegalStateException(String.format("Couldn't create directory: %s", file.getParentFile().getAbsolutePath()));
            } else if (!file.createNewFile()) {
                throw new IllegalStateException(String.format("Couldn't create file: %s", file.getAbsolutePath()));
            }
            flag = true;
        }
        this.file = file;
        this.raf = new RandomAccessFile(file, "rw");
        this.channel = this.raf.getChannel();
        this.lengthWriteBuffer = this.channel.map(FileChannel.MapMode.READ_WRITE, 0L, 8L);
        if (flag) {
            this.clear();
        }
        DataLookup.super.init(map, file);
    }

    @Override
    public void clear() throws IOException {
        this.raf.setLength(8L);
        this.lengthWriteBuffer.putLong(0, 8L);
    }

    @Override
    public void load() throws IOException {
        if (this.channel.size() == 0L) {
            this.raf.seek(0L);
            this.raf.writeLong(8L);
        }
        this.lengthWriteBuffer = this.channel.map(FileChannel.MapMode.READ_WRITE, 0L, 8L);
    }

    @Override
    public void save() throws IOException {
        if (this.dirty) {
            this.lengthWriteBuffer.force();
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (this) {
            this.save();
            this.lengthWriteBuffer = null;
            this.channel.close();
            this.channel = null;
            this.raf.close();
            this.raf = null;
            this.file = null;
        }
    }

    @Override
    public DataIn read(long id) throws IOException {
        return new BufferingFileInput(this.channel, id, 4096);
    }

    @Override
    public long write(long id, @NonNull IOConsumer<DataOut> writer) throws IOException {
        synchronized (this) {
            long len = this.lengthWriteBuffer.getLong(0);
            if (id != -1L && id <= len) {
                throw new UnsupportedOperationException("overwrite");
            } else if (id == -1L) {
                id = this.lengthWriteBuffer.getLong(0);
            }
            long ree_java = id;
            try (DataOut out = new HugeBufferOut(buffers -> {
                long off = ree_java;
                for (ByteBuffer buffer : buffers) {
                    off += this.channel.write(buffer, off);
                }
                this.lengthWriteBuffer.putLong(0, off);
            }, 4096)) {
                writer.accept(out);
            }
            this.markDirty();
            return id;
        }
    }

    @Override
    public void remove(long id) throws IOException {
        throw new UnsupportedOperationException("remove");
    }

    @Override
    public boolean allowsCompression() {
        return true;
    }
}
