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

package net.daporkchop.lib.binary.buf.file;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.binary.buf.AbstractCloseablePorkBuf;
import net.daporkchop.lib.binary.buf.PorkBuf;
import net.daporkchop.lib.binary.buf.exception.PorkBufIOException;
import net.daporkchop.lib.binary.buf.exception.PorkBufReadOutOfBoundsException;
import net.daporkchop.lib.binary.buf.exception.PorkBufWriteOutOfBoundsException;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * @author DaPorkchop_
 */
//TODO: file locking
public class PFileChannel extends AbstractCloseablePorkBuf {
    protected static FileChannel doOpenChannel(@NonNull Path path, @NonNull StandardOpenOption... options) {
        try {
            return FileChannel.open(path, options);
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        }
    }

    protected final FileChannel channel;
    protected final Object readMutex = new byte[0];
    protected final Object writeMutex = new byte[0];

    public PFileChannel(@NonNull String path, @NonNull StandardOpenOption... options) {
        this(new File(path), -1L, options);
    }

    public PFileChannel(@NonNull String path, long maxCapacity, @NonNull StandardOpenOption... options) {
        this(new File(path), maxCapacity, options);
    }

    public PFileChannel(@NonNull File file, @NonNull StandardOpenOption... options) {
        this(file.toPath(), -1L, options);
    }

    public PFileChannel(@NonNull File file, long maxCapacity, @NonNull StandardOpenOption... options) {
        this(file.toPath(), maxCapacity, options);
    }

    public PFileChannel(@NonNull Path path, @NonNull StandardOpenOption... options) {
        this(path, -1L, options);
    }

    public PFileChannel(@NonNull Path path, long maxCapacity, @NonNull StandardOpenOption... options) {
        this(doOpenChannel(path, options), maxCapacity);
    }

    public PFileChannel(@NonNull FileChannel channel, long maxCapacity) {
        super(maxCapacity);

        if (!(this.channel = channel).isOpen()) {
            throw new IllegalArgumentException("Channel must be open!");
        }
    }

    //
    //
    // relative write methods
    //
    //
    @Override
    public PorkBuf putByte(byte b) throws PorkBufWriteOutOfBoundsException {
        synchronized (this.writeMutex) {
            this.ensureWriteInBounds(1);
            try {
                this.channel.write((ByteBuffer) ByteBuffer.allocate(1).put(b).flip(), this.writerIndex++);
                return this;
            } catch (IOException e) {
                throw new PorkBufIOException(e);
            }
        }
    }

    @Override
    public PorkBuf putShort(short s) throws PorkBufWriteOutOfBoundsException {
        synchronized (this.writeMutex) {
            this.ensureWriteInBounds(2);
            try {
                this.channel.write((ByteBuffer) ByteBuffer.allocate(2).putShort(s).flip(), this.writerIndex);
                this.writerIndex += 2;
                return this;
            } catch (IOException e) {
                throw new PorkBufIOException(e);
            }
        }
    }

    @Override
    public PorkBuf putMedium(int i) throws PorkBufWriteOutOfBoundsException {
        synchronized (this.writeMutex) {
            this.ensureWriteInBounds(3);
            try {
                this.channel.write((ByteBuffer) ByteBuffer.allocate(3)
                        .put((byte) (i & 0xFF))
                        .put((byte) ((i >>> 8) & 0xFF))
                        .put((byte) ((i >>> 16) & 0xFF)).flip(), this.writerIndex);
                this.writerIndex += 3;
                return this;
            } catch (IOException e) {
                throw new PorkBufIOException(e);
            }
        }
    }

    @Override
    public PorkBuf putInt(int i) throws PorkBufWriteOutOfBoundsException {
        synchronized (this.writeMutex) {
            this.ensureWriteInBounds(4);
            try {
                this.channel.write((ByteBuffer) ByteBuffer.allocate(4).putInt(i).flip(), this.writerIndex);
                this.writerIndex += 4;
                return this;
            } catch (IOException e) {
                throw new PorkBufIOException(e);
            }
        }
    }

    @Override
    public PorkBuf putLong(long l) throws PorkBufWriteOutOfBoundsException {
        synchronized (this.writeMutex) {
            this.ensureWriteInBounds(8);
            try {
                this.channel.write((ByteBuffer) ByteBuffer.allocate(8).putLong(l).flip(), this.writerIndex);
                this.writerIndex += 8;
                return this;
            } catch (IOException e) {
                throw new PorkBufIOException(e);
            }
        }
    }

    @Override
    public PorkBuf putFloat(float f) throws PorkBufWriteOutOfBoundsException {
        synchronized (this.writeMutex) {
            this.ensureWriteInBounds(4);
            try {
                this.channel.write((ByteBuffer) ByteBuffer.allocate(4).putFloat(f).flip(), this.writerIndex);
                this.writerIndex += 4;
                return this;
            } catch (IOException e) {
                throw new PorkBufIOException(e);
            }
        }
    }

    @Override
    public PorkBuf putDouble(double d) throws PorkBufWriteOutOfBoundsException {
        synchronized (this.writeMutex) {
            this.ensureWriteInBounds(8);
            try {
                this.channel.write((ByteBuffer) ByteBuffer.allocate(8).putDouble(d).flip(), this.writerIndex);
                this.writerIndex += 8;
                return this;
            } catch (IOException e) {
                throw new PorkBufIOException(e);
            }
        }
    }

    @Override
    public PorkBuf putBytes(@NonNull byte[] arr) throws PorkBufWriteOutOfBoundsException {
        synchronized (this.writeMutex) {
            this.ensureWriteInBounds(arr.length);
            try {
                this.channel.write(ByteBuffer.wrap(arr), this.writerIndex);
                this.writerIndex += arr.length;
                return this;
            } catch (IOException e) {
                throw new PorkBufIOException(e);
            }
        }
    }

    @Override
    public PorkBuf putBytes(@NonNull byte[] arr, int off, int len) throws ArrayIndexOutOfBoundsException, PorkBufWriteOutOfBoundsException {
        if (off + len >= arr.length || off < 0 || len < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        synchronized (this.writeMutex) {
            this.ensureWriteInBounds(len);
            try {
                this.channel.write(ByteBuffer.wrap(arr, off, len), this.writerIndex);
                this.writerIndex += len;
                return this;
            } catch (IOException e) {
                throw new PorkBufIOException(e);
            }
        }
    }

    @Override
    public PorkBuf putBytes(@NonNull ByteBuf buf) throws PorkBufWriteOutOfBoundsException {
        synchronized (this.writeMutex) {
            int i = buf.readableBytes();
            this.ensureWriteInBounds(i);
            try {
                this.channel.write(buf.nioBuffer(), this.writerIndex);
                buf.readerIndex(buf.readerIndex() + i);
                this.writerIndex += i;
                return this;
            } catch (IOException e) {
                throw new PorkBufIOException(e);
            }
        }
    }

    @Override
    public PorkBuf putBytes(@NonNull ByteBuffer buf) throws PorkBufWriteOutOfBoundsException {
        synchronized (this.writeMutex) {
            int i = buf.remaining();
            this.ensureWriteInBounds(i);
            try {
                this.channel.write(buf, this.writerIndex);
                this.writerIndex += i;
                return this;
            } catch (IOException e) {
                throw new PorkBufIOException(e);
            }
        }
    }

    //
    //
    // absolute write methods
    //
    //
    @Override
    public PorkBuf putByte(long index, byte b) throws PorkBufWriteOutOfBoundsException {
        this.ensureInBounds(index, 1, false);
        try {
            this.channel.write((ByteBuffer) ByteBuffer.allocate(1).put(b).flip(), index);
            return this;
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        }
    }

    @Override
    public PorkBuf putShort(long index, short s) throws PorkBufWriteOutOfBoundsException {
        this.ensureInBounds(index, 2, false);
        try {
            this.channel.write((ByteBuffer) ByteBuffer.allocate(2).putShort(s).flip(), index);
            return this;
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        }
    }

    @Override
    public PorkBuf putMedium(long index, int i) throws PorkBufWriteOutOfBoundsException {
        this.ensureInBounds(index, 3, false);
        try {
            this.channel.write((ByteBuffer) ByteBuffer.allocate(3)
                    .put((byte) (i & 0xFF))
                    .put((byte) ((i >>> 8) & 0xFF))
                    .put((byte) ((i >>> 16) & 0xFF)).flip(), index);
            return this;
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        }
    }

    @Override
    public PorkBuf putInt(long index, int i) throws PorkBufWriteOutOfBoundsException {
        this.ensureInBounds(index, 4, false);
        try {
            this.channel.write((ByteBuffer) ByteBuffer.allocate(4).putInt(i).flip(), index);
            return this;
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        }
    }

    @Override
    public PorkBuf putLong(long index, long l) throws PorkBufWriteOutOfBoundsException {
        this.ensureInBounds(index, 8, false);
        try {
            this.channel.write((ByteBuffer) ByteBuffer.allocate(8).putLong(l).flip(), index);
            return this;
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        }
    }

    @Override
    public PorkBuf putFloat(long index, float f) throws PorkBufWriteOutOfBoundsException {
        this.ensureInBounds(index, 4, false);
        try {
            this.channel.write((ByteBuffer) ByteBuffer.allocate(4).putFloat(f).flip(), index);
            return this;
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        }
    }

    @Override
    public PorkBuf putDouble(long index, double d) throws PorkBufWriteOutOfBoundsException {
        this.ensureInBounds(index, 8, false);
        try {
            this.channel.write((ByteBuffer) ByteBuffer.allocate(8).putDouble(d).flip(), index);
            return this;
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        }
    }

    @Override
    public PorkBuf putBytes(long index, @NonNull byte[] arr) throws PorkBufWriteOutOfBoundsException {
        this.ensureInBounds(index, arr.length, false);
        try {
            this.channel.write(ByteBuffer.wrap(arr), index);
            return this;
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        }
    }

    @Override
    public PorkBuf putBytes(long index, @NonNull byte[] arr, int off, int len) throws ArrayIndexOutOfBoundsException, PorkBufWriteOutOfBoundsException {
        if (off + len >= arr.length || off < 0 || len < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        this.ensureInBounds(index, len, false);
        try {
            this.channel.write(ByteBuffer.wrap(arr, off, len), index);
            return this;
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        }
    }

    @Override
    public PorkBuf putBytes(long index, @NonNull ByteBuf buf) throws PorkBufWriteOutOfBoundsException {
        int i = buf.readableBytes();
        this.ensureInBounds(index, i, false);
        try {
            this.channel.write(buf.nioBuffer(), index);
            buf.readerIndex(buf.readerIndex() + i);
            return this;
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        }
    }

    @Override
    public PorkBuf putBytes(long index, @NonNull ByteBuffer buf) throws PorkBufWriteOutOfBoundsException {
        int i = buf.remaining();
        this.ensureInBounds(index, i, false);
        try {
            this.channel.write(buf, index);
            return this;
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        }
    }

    //
    //
    // relative read methods
    //
    //
    @Override
    public byte getByte() throws PorkBufReadOutOfBoundsException {
        synchronized (this.readMutex) {
            this.ensureReadInBounds(1);
            try {
                ByteBuffer buffer = ByteBuffer.allocate(1);
                this.channel.read(buffer, this.readerIndex++);
                return buffer.get(0);
            } catch (IOException e) {
                throw new PorkBufIOException(e);
            }
        }
    }

    @Override
    public short getShort() throws PorkBufReadOutOfBoundsException {
        synchronized (this.readMutex) {
            this.ensureReadInBounds(2);
            try {
                ByteBuffer buffer = ByteBuffer.allocate(2);
                this.channel.read(buffer, this.readerIndex);
                this.readerIndex += 2;
                return buffer.getShort(0);
            } catch (IOException e) {
                throw new PorkBufIOException(e);
            }
        }
    }

    @Override
    public int getMedium() throws PorkBufReadOutOfBoundsException {
        synchronized (this.readMutex) {
            this.ensureReadInBounds(3);
            try {
                ByteBuffer buffer = ByteBuffer.allocate(3);
                this.channel.read(buffer, this.readerIndex);
                this.readerIndex += 3;
                return (buffer.get(0) & 0xFF)
                        | ((buffer.get(1) & 0xFF) << 8)
                        | ((buffer.get(2) & 0xFF) << 16);
            } catch (IOException e) {
                throw new PorkBufIOException(e);
            }
        }
    }

    @Override
    public int getInt() throws PorkBufReadOutOfBoundsException {
        synchronized (this.readMutex) {
            this.ensureReadInBounds(4);
            try {
                ByteBuffer buffer = ByteBuffer.allocate(4);
                this.channel.read(buffer, this.readerIndex);
                this.readerIndex += 4;
                return buffer.getInt(0);
            } catch (IOException e) {
                throw new PorkBufIOException(e);
            }
        }
    }

    @Override
    public long getLong() throws PorkBufReadOutOfBoundsException {
        synchronized (this.readMutex) {
            this.ensureReadInBounds(8);
            try {
                ByteBuffer buffer = ByteBuffer.allocate(8);
                this.channel.read(buffer, this.readerIndex);
                this.readerIndex += 8;
                return buffer.getLong(0);
            } catch (IOException e) {
                throw new PorkBufIOException(e);
            }
        }
    }

    @Override
    public float getFloat() throws PorkBufReadOutOfBoundsException {
        synchronized (this.readMutex) {
            this.ensureReadInBounds(4);
            try {
                ByteBuffer buffer = ByteBuffer.allocate(4);
                this.channel.read(buffer, this.readerIndex);
                this.readerIndex += 4;
                return buffer.getFloat(0);
            } catch (IOException e) {
                throw new PorkBufIOException(e);
            }
        }
    }

    @Override
    public double getDouble() throws PorkBufReadOutOfBoundsException {
        synchronized (this.readMutex) {
            this.ensureReadInBounds(8);
            try {
                ByteBuffer buffer = ByteBuffer.allocate(8);
                this.channel.read(buffer, this.readerIndex);
                this.readerIndex += 8;
                return buffer.getDouble(0);
            } catch (IOException e) {
                throw new PorkBufIOException(e);
            }
        }
    }

    @Override
    public void getBytes(@NonNull byte[] arr) throws PorkBufReadOutOfBoundsException {
        synchronized (this.readMutex) {
            this.ensureReadInBounds(arr.length);
            try {
                this.channel.read(ByteBuffer.wrap(arr), this.readerIndex);
                this.readerIndex += arr.length;
            } catch (IOException e) {
                throw new PorkBufIOException(e);
            }
        }
    }

    @Override
    public void getBytes(@NonNull byte[] arr, int off, int len) throws ArrayIndexOutOfBoundsException, PorkBufReadOutOfBoundsException {
        if (off + len >= arr.length || off < 0 || len < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        synchronized (this.readMutex) {
            this.ensureReadInBounds(len);
            try {
                this.channel.read(ByteBuffer.wrap(arr, off, len), this.readerIndex);
                this.readerIndex += len;
            } catch (IOException e) {
                throw new PorkBufIOException(e);
            }
        }
    }

    @Override
    public void getBytes(@NonNull ByteBuf buf) throws PorkBufReadOutOfBoundsException {
        synchronized (this.readMutex) {
            int i = buf.writableBytes();
            this.ensureReadInBounds(i);
            try {
                this.channel.read(buf.nioBuffer(), this.readerIndex);
                this.readerIndex += i;
            } catch (IOException e) {
                throw new PorkBufIOException(e);
            }
        }
    }

    @Override
    public void getBytes(@NonNull ByteBuffer buf) throws PorkBufReadOutOfBoundsException {
        synchronized (this.readMutex) {
            int i = buf.remaining();
            this.ensureReadInBounds(i);
            try {
                this.channel.read(buf, this.readerIndex);
                this.readerIndex += i;
            } catch (IOException e) {
                throw new PorkBufIOException(e);
            }
        }
    }

    //
    //
    // absolute read methods
    //
    //
    @Override
    public byte getByte(long index) throws PorkBufReadOutOfBoundsException {
        this.ensureInBounds(index, 1, true);
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1);
            this.channel.read(buffer, index);
            return buffer.get(0);
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        }
    }

    @Override
    public short getShort(long index) throws PorkBufReadOutOfBoundsException {
        this.ensureInBounds(index, 2, true);
        try {
            ByteBuffer buffer = ByteBuffer.allocate(2);
            this.channel.read(buffer, index);
            return buffer.getShort(0);
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        }
    }

    @Override
    public int getMedium(long index) throws PorkBufReadOutOfBoundsException {
        this.ensureInBounds(index, 3, true);
        try {
            ByteBuffer buffer = ByteBuffer.allocate(3);
            this.channel.read(buffer, index);
            return (buffer.get(0) & 0xFF)
                    | ((buffer.get(1) & 0xFF) << 8)
                    | ((buffer.get(2) & 0xFF) << 16);
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        }
    }

    @Override
    public int getInt(long index) throws PorkBufReadOutOfBoundsException {
        this.ensureInBounds(index, 4, true);
        try {
            ByteBuffer buffer = ByteBuffer.allocate(4);
            this.channel.read(buffer, index);
            return buffer.getInt(0);
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        }
    }

    @Override
    public long getLong(long index) throws PorkBufReadOutOfBoundsException {
        this.ensureInBounds(index, 8, true);
        try {
            ByteBuffer buffer = ByteBuffer.allocate(8);
            this.channel.read(buffer, index);
            return buffer.getLong(0);
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        }
    }

    @Override
    public float getFloat(long index) throws PorkBufReadOutOfBoundsException {
        this.ensureInBounds(index, 4, true);
        try {
            ByteBuffer buffer = ByteBuffer.allocate(4);
            this.channel.read(buffer, index);
            return buffer.getFloat(0);
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        }
    }

    @Override
    public double getDouble(long index) throws PorkBufReadOutOfBoundsException {
        this.ensureInBounds(index, 8, true);
        try {
            ByteBuffer buffer = ByteBuffer.allocate(8);
            this.channel.read(buffer, index);
            return buffer.getDouble(0);
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        }
    }

    @Override
    public void getBytes(long index, @NonNull byte[] arr) throws PorkBufReadOutOfBoundsException {
        this.ensureInBounds(index, 8, true);
        try {
            this.channel.read(ByteBuffer.wrap(arr), index);
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        }
    }

    @Override
    public void getBytes(long index, @NonNull byte[] arr, int off, int len) throws ArrayIndexOutOfBoundsException, PorkBufReadOutOfBoundsException {
        if (off + len >= arr.length || off < 0 || len < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        this.ensureInBounds(index, 8, true);
        try {
            this.channel.read(ByteBuffer.wrap(arr, off, len), index);
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        }
    }

    @Override
    public void getBytes(long index, @NonNull ByteBuf buf) throws PorkBufReadOutOfBoundsException {
        this.ensureInBounds(index, buf.writableBytes(), true);
        try {
            this.channel.read(buf.nioBuffer(), index);
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        }
    }

    @Override
    public void getBytes(long index, @NonNull ByteBuffer buf) throws PorkBufReadOutOfBoundsException {
        this.ensureInBounds(index, buf.remaining(), true);
        try {
            this.channel.read(buf, index);
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        }
    }

    //
    //
    // other methods
    //
    //
    @Override
    public boolean isClosed() {
        return this.channel.isOpen();
    }

    @Override
    public synchronized void close() throws IOException {
        this.channel.close();
    }

    @Override
    public long capacity() {
        try {
            return this.channel.size();
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        }
    }

    @Override
    public PFileChannel capacity(long capacity) {
        try {
            this.channel.truncate(capacity);
            return this;
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        }
    }

    @Override
    public long memoryAddress() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long memorySize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object refObj() {
        throw new UnsupportedOperationException();
    }
}
