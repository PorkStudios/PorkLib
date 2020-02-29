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

package net.daporkchop.lib.http.entity.transfer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

/**
 * A simple {@link TransferSession} that reads data from a {@link FileChannel}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public final class FileChannelTransferSession implements TransferSession {
    protected final long position;
    protected final long length;

    @Getter(AccessLevel.NONE)
    @NonNull
    protected final FileChannel channel;

    protected int refCount = 1;

    public FileChannelTransferSession(@NonNull FileChannel channel) throws IOException {
        this(0L, channel.size(), channel);
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
        return true;
    }

    @Override
    public synchronized void retain() {
        if (this.refCount == 0) {
            throw new AlreadyReleasedException();
        } else {
            this.refCount++;
        }
    }

    @Override
    public synchronized boolean release() {
        if (this.refCount == 0) {
            throw new AlreadyReleasedException();
        } else if (--this.refCount == 0) {
            try {
                this.channel.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return true;
        } else {
            return false;
        }
    }
}
