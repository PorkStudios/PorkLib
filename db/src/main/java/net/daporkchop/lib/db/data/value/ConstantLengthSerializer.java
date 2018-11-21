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

package net.daporkchop.lib.db.data.value;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.binary.data.Serializer;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.binary.stream.optimizations.NonExpandingByteArrayOutputStream;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Implementations of this serializer will always write data that is the same number of bytes
 *
 * @author DaPorkchop_
 */
public abstract class ConstantLengthSerializer<T> implements Serializer<T> {
    @Getter
    private final int size;
    private final ThreadLocal<byte[]> bufferCache;

    public ConstantLengthSerializer(int size)   {
        if (size <= 0)  {
            throw new IllegalArgumentException(String.format("Illegal size: %d (must be more than 0)", size));
        }
        this.size = size;
        this.bufferCache = ThreadLocal.withInitial(() -> new byte[this.size]);
    }

    @Override
    public void write(@NonNull T val, @NonNull DataOut out) throws IOException {
        NonExpandingByteArrayOutputStream baos = NonExpandingByteArrayOutputStream.wrap(this.bufferCache.get());
        try (DataOut theOut = DataOut.wrap(baos))   {
            this.doWrite(val, theOut);
        }
        out.write(baos.getBuf());
    }

    @Override
    public T read(@NonNull DataIn in) throws IOException {
        byte[] buf = this.bufferCache.get();
        in.readFully(buf, 0, this.size);
        try (DataIn theIn = DataIn.wrap(ByteBuffer.wrap(buf))) {
            return this.doRead(theIn);
        }
    }

    protected abstract void doWrite(@NonNull T val, @NonNull DataOut out) throws IOException;

    protected abstract T doRead(@NonNull DataIn in) throws IOException;
}
