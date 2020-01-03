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

import io.netty.buffer.ByteBuf;
import lombok.NonNull;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * A simple {@link TransferSession} that simply returns data stored in a single {@link ByteBuf}.
 *
 * @author DaPorkchop_
 */
public final class ByteBufTransferSession implements TransferSession {
    protected final ByteBuffer   nioBuffer;
    protected final ByteBuffer[] nioBuffers;

    protected ByteBuf buf;

    public ByteBufTransferSession(@NonNull ByteBuf buf) {
        this.buf = buf;
        switch (buf.nioBufferCount())   {
            case -1:
                throw new IllegalArgumentException("Buffer does not have any NIO buffers!");
            case 1:
                this.nioBuffer = buf.nioBuffer();
                this.nioBuffers = null;
                break;
            default:
                this.nioBuffer = null;
                this.nioBuffers = buf.nioBuffers();
                break;
        }
    }

    @Override
    public long transfer(@NonNull WritableByteChannel out) throws Exception {
        if (this.nioBuffer != null) {
            return out.write(this.nioBuffer);
        } else {
            long count = 0L;
            for (ByteBuffer buf : this.nioBuffers)  {
                if (buf.hasRemaining()) {
                    int read = out.write(buf);
                    count += read;
                    if (read == 0)  {
                        break;
                    }
                }
            }
            return count;
        }
    }

    @Override
    public long transferAllBlocking(@NonNull WritableByteChannel out) throws Exception {
        long count = 0L;
        if (this.nioBuffer != null) {
            do {
                count += out.write(this.nioBuffer);
            } while (this.nioBuffer.hasRemaining());
        } else {
            for (ByteBuffer buf : this.nioBuffers)  {
                do {
                    count += out.write(buf);
                } while (buf.hasRemaining());
            }
        }
        return count;
    }

    @Override
    public boolean complete() {
        return !this.buf.isReadable();
    }

    @Override
    public void close() throws Exception {
        this.buf.release();
        this.buf = null;
    }
}
