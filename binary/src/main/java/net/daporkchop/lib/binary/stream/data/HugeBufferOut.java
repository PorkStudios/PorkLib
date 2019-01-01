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

package net.daporkchop.lib.binary.stream.data;

import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.function.IOConsumer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows buffering a huge amount of data in RAM
 *
 * @author DaPorkchop_
 */
public class HugeBufferOut extends DataOut {
    private final int bufferSize;
    private final IOConsumer<List<ByteBuffer>> closer;
    private List<ByteBuffer> buffers = new ArrayList<>();
    private ByteBuffer currentBuffer;

    public HugeBufferOut(@NonNull IOConsumer<List<ByteBuffer>> closer) {
        this(closer, 4096);
    }

    public HugeBufferOut(@NonNull IOConsumer<List<ByteBuffer>> closer, int bufferSize) {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException(String.format("Invalid buffer size: %d", bufferSize));
        }
        this.bufferSize = bufferSize;
        this.closer = closer;
    }

    @Override
    public void write(int b) throws IOException {
        if (this.buffers == null) {
            throw new IOException("Stream closed!");
        } else if (this.currentBuffer == null) {
            this.currentBuffer = ByteBuffer.allocateDirect(this.bufferSize);
        }
        this.currentBuffer.put((byte) b);
        if (this.currentBuffer.position() == this.currentBuffer.limit()) {
            this.flush();
        }
    }

    @Override
    public void flush() throws IOException {
        synchronized (this) {
            if (this.currentBuffer != null) { //current buffer can be null if exactly the right number of bytes is written
                if (this.buffers == null) {
                    throw new IOException("Stream closed!");
                } else {
                    this.currentBuffer.flip();
                    this.buffers.add(this.currentBuffer);
                    this.currentBuffer = null;
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (this) {
            if (this.buffers != null) {
                this.flush();
                this.closer.accept(this.buffers);
                this.buffers = null;
            }
        }
    }
}
