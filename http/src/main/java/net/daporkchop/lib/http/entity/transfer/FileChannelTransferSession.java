/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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
