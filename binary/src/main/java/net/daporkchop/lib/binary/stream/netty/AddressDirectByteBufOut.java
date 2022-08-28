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
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.binary.util.NoMoreSpaceException;
import net.daporkchop.lib.common.annotation.param.Positive;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * An implementation of {@link DataOut} that can write to any direct {@link ByteBuf} which {@link ByteBuf#hasMemoryAddress() has a single backing memory address}.
 *
 * @author DaPorkchop_
 */
public class AddressDirectByteBufOut extends GenericDirectByteBufOut {
    public AddressDirectByteBufOut(@NonNull ByteBuf delegate, boolean autoRelease) {
        super(delegate, autoRelease);
        checkArg(delegate.hasMemoryAddress(), "delegate must have a memory address!");

        //we can't keep a constant reference to the memory address, as it could be changed when the buffer's capacity changes
    }

    @Override
    protected void write0(long addr, @Positive long _length) throws NoMoreSpaceException, IOException {
        //same implementation as super, but without calling PlatformDependent.directBuffer() (which would allocate a new object)

        int length = toInt(_length, "length");
        if (length > this.delegate.maxWritableBytes()) {
            throw new NoMoreSpaceException();
        }

        if (length > this.delegate.writableBytes()) { //we need to increase the buffer's capacity
            this.delegate.ensureWritable(length);
        }

        PUnsafe.copyMemory(addr, this.delegate.memoryAddress() + this.delegate.writerIndex(), length);
        this.delegate.writerIndex(this.delegate.writerIndex() + length);
    }
}
