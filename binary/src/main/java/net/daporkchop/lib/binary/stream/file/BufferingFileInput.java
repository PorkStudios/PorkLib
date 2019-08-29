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

package net.daporkchop.lib.binary.stream.file;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.stream.OldDataIn;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Allows reading from a {@link FileChannel} using a native byte buffer
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class BufferingFileInput extends OldDataIn {
    @NonNull
    private final FileChannel channel;
    private final int bufferSize;
    @NonNull
    private long offset;
    @Getter(AccessLevel.PRIVATE)
    private ByteBuffer buffer;

    @Override
    public int read() throws IOException {
        if (this.buffer == null || !this.buffer.hasRemaining()) {
            if (this.buffer == null) {
                this.buffer = ByteBuffer.allocateDirect(this.bufferSize);
            }
            this.buffer.clear();
            this.offset += this.channel.read(this.buffer, this.offset);
            this.buffer.flip();
        }
        return this.buffer.get() & 0xFF;
    }

    @Override
    public void close() throws IOException {
        this.buffer = null;
    }
}
