/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
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

package net.daporkchop.lib.binary.stream.netty;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.common.annotation.param.Positive;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * An implementation of {@link DataIn} that can read from any direct {@link ByteBuf} which {@link ByteBuf#hasMemoryAddress() has a single backing memory address}.
 *
 * @author DaPorkchop_
 */
public class AddressDirectByteBufIn extends GenericDirectByteBufIn {
    public AddressDirectByteBufIn(@NonNull ByteBuf delegate, boolean autoRelease) {
        super(delegate, autoRelease);
        checkArg(delegate.hasMemoryAddress(), "delegate must have a memory address!");

        //we can't keep a constant reference to the memory address, as it could be changed when the buffer's capacity changes
    }

    @Override
    protected long read0(long addr, @Positive long length) throws IOException {
        //same implementation as super, but without calling PlatformDependent.directBuffer() (which would allocate a new object)

        //IndexOutOfBoundsException can't be thrown because we never read more than readableBytes()
        int count = toInt(min(this.delegate.readableBytes(), length));
        if (count <= 0) {
            return RESULT_EOF;
        }

        PUnsafe.copyMemory(this.delegate.memoryAddress() + this.delegate.readerIndex(), addr, count);
        this.delegate.readerIndex(this.delegate.readerIndex() + count); //don't use skipBytes() because it calls ensureAccessible(), causing a volatile load
        return count;
    }
}
