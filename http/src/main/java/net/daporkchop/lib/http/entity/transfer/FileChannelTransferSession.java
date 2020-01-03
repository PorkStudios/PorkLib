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

import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

/**
 * A simple {@link TransferSession} that reads data from a {@link FileChannel}.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
public final class FileChannelTransferSession implements TransferSession {
    protected final long size;
    protected       long position;

    @NonNull
    protected final FileChannel channel;

    public FileChannelTransferSession(@NonNull FileChannel channel) throws IOException {
        this(channel.size(), 0L, channel);
    }

    @Override
    public long transfer(@NonNull WritableByteChannel out) throws Exception {
        long position = this.position;
        long transferred = this.channel.transferTo(position, this.channel.size() - position, out);
        if (transferred > 0L) {
            this.position = position + transferred;
        }
        return transferred;
    }

    @Override
    public long transferAllBlocking(@NonNull WritableByteChannel out) throws Exception {
        long size = this.channel.size();
        long position = this.position;
        while (position < size) {
            long transferred = this.channel.transferTo(position, size - position, out);
            position += transferred;
        }
        return this.position = size;
    }

    @Override
    public boolean complete() {
        return this.position >= this.size;
    }

    @Override
    public void close() throws Exception {
        this.channel.close();
    }
}
