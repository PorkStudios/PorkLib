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

package net.daporkchop.lib.nbt.util;

import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.binary.stream.order.ReverseOrderedDataOut;
import net.daporkchop.lib.binary.util.NoMoreSpaceException;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;

/**
 * @author DaPorkchop_
 */
public class VarIntReverseOrderedDataOut extends ReverseOrderedDataOut {
    public VarIntReverseOrderedDataOut(DataOut delegate) {
        super(delegate);
    }

    @Override
    public void writeInt(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.writeVarIntZigZag(v);
    }

    @Override
    public void writeLong(long v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.writeVarLongZigZag(v);
    }

    @Override
    public void writeUTF(@NonNull String text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.writeVarUTF(text);
    }
}
