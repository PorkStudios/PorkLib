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

package net.daporkchop.lib.binary.stream;

import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows buffering a huge amount of data from a {@link FileChannel} into RAM
 *
 * @author DaPorkchop_
 */
public class HugeBufferIn extends DataIn {
    private List<ByteBuffer> buffers = new ArrayList<>();

    public HugeBufferIn(@NonNull FileChannel channel, long offset, long size) throws IOException {
        if (size < 0L) {
            throw new IllegalArgumentException(String.format("Invalid size: %d", size));
        } else if (offset < 0L) {
            throw new IllegalArgumentException(String.format("Invalid offset: %d", offset));
        }
        while (size > 0L) {
            int decr = size > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) size;
            size -= decr;
            this.buffers.add(ByteBuffer.allocateDirect(decr));
        }
        for (int i = 0; i < this.buffers.size(); i++) {
            ByteBuffer buffer = this.buffers.get(i);
            channel.read(buffer, offset);
            offset += buffer.capacity();
            buffer.flip();
        }
    }

    @Override
    public int read() throws IOException {
        if (this.buffers == null || this.buffers.isEmpty()) {
            return -1;
        } else {
            ByteBuffer buffer = this.buffers.get(0);
            if (!buffer.hasRemaining()) {
                this.buffers.remove(0);
                if (this.buffers.isEmpty()) {
                    this.close();
                    return -1;
                } else {
                    buffer = this.buffers.get(0);
                }
            }
            return buffer.get() & 0xFF;
        }
    }

    @Override
    public void close() throws IOException {
        this.buffers = null;
    }
}
