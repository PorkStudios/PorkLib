/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.http.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.util.ReferenceCountedFileChannel;
import net.daporkchop.lib.http.entity.content.type.ContentType;
import net.daporkchop.lib.http.entity.transfer.TransferSession;

import java.io.File;
import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.nio.file.StandardOpenOption;

/**
 * An {@link HttpEntity} that reads from a fixed region of an already opened {@link ReferenceCountedFileChannel}.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public final class FileRegionHttpEntity implements HttpEntity, TransferSession {
    @NonNull
    protected final ContentType type;

    @Getter(AccessLevel.NONE)
    protected final ReferenceCountedFileChannel channel;

    protected final long position;
    protected final long length;

    protected final boolean singleUseOnly;

    /**
     * @see #FileRegionHttpEntity(ContentType, ReferenceCountedFileChannel, long, long, boolean)
     */
    public FileRegionHttpEntity(@NonNull ContentType type, @NonNull File file) throws IOException {
        this(type, ReferenceCountedFileChannel.open(file.toPath(), StandardOpenOption.READ), 0L, file.length(), true);
    }

    /**
     * @see #FileRegionHttpEntity(ContentType, ReferenceCountedFileChannel, long, long, boolean)
     */
    public FileRegionHttpEntity(@NonNull ContentType type, @NonNull File file, long position, long length) throws IOException {
        this(type, ReferenceCountedFileChannel.open(file.toPath(), StandardOpenOption.READ), position, length, true);
    }

    /**
     * @see #FileRegionHttpEntity(ContentType, ReferenceCountedFileChannel, long, long, boolean)
     */
    public FileRegionHttpEntity(@NonNull ContentType type, @NonNull ReferenceCountedFileChannel channel, boolean singleUseOnly) throws IOException {
        this(type, channel, 0L, channel.size(), singleUseOnly);
    }

    /**
     * Creates a new {@link FileRegionHttpEntity} instance.
     *
     * @param type          the data's {@link ContentType}
     * @param channel       the {@link ReferenceCountedFileChannel} that the data will be read from
     * @param position      the start position (inclusive) in the file to start reading from
     * @param length        the size (in bytes) of the region to read from the file
     * @param singleUseOnly whether or not the {@link FileRegionHttpEntity} will be able to be reused. If {@code true}, the {@link ReferenceCountedFileChannel}
     *                      will be only retained once (inside this constructor), if {@code false}, the {@link ReferenceCountedFileChannel} will be retained
     *                      once inside this constructor and once more for every invocation of {@link #newSession()}. In either case the assumption is made
     *                      that the user will manually release the {@link ReferenceCountedFileChannel} once at some point after calling this constructor,
     *                      however if {@code true} the user must release this {@link FileRegionHttpEntity} a second time once it should no longer be used.
     * @throws IOException if an IO exception occurs you dummy
     */
    public FileRegionHttpEntity(@NonNull ContentType type, @NonNull ReferenceCountedFileChannel channel, long position, long length, boolean singleUseOnly) throws IOException {
        this.type = type;
        this.channel = channel.retain();
        this.position = position;
        this.length = length;
        this.singleUseOnly = singleUseOnly;

        long size = this.channel.size();
        if (this.position < 0L || this.position + this.length > size) {
            throw new IndexOutOfBoundsException(String.format("position=%d, length=%d, size=%d", this.position, this.length, size));
        }
    }

    @Override
    public TransferSession newSession() throws Exception {
        if (!this.singleUseOnly) {
            this.channel.retain(); //retain every time so that it will be released every time it's sent
        }
        return this;
    }

    @Override
    public long transfer(long position, @NonNull WritableByteChannel out) throws Exception {
        if (position >= this.position + this.length || position < this.position) {
            throw new IndexOutOfBoundsException(String.format("position=%d, length=%d, requested=%d", this.position, this.length, position));
        }
        return this.channel.transferTo(position, this.length - (position - this.position), out);
    }

    @Override
    public long transferAllBlocking(long position, @NonNull WritableByteChannel out) throws Exception {
        if (position >= this.position + this.length || position < this.position) {
            throw new IndexOutOfBoundsException(String.format("position=%d, length=%d, requested=%d", this.position, this.length, position));
        }
        long required = this.length - (position - this.position);
        for (long transferred = 0L; transferred < required; ) {
            this.channel.transferTo(position + transferred, required - transferred, out);
        }
        return required;
    }

    @Override
    public boolean reusable() {
        return !this.singleUseOnly;
    }

    @Override
    public void retain() {
        this.channel.retain();
    }

    @Override
    public boolean release() {
        return this.channel.release();
    }
}
