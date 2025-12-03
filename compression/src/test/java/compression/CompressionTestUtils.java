/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2025 DaPorkchop_
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
 */

package compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.function.exception.EConsumer;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.nio.ByteBuffer;

/**
 * @author DaPorkchop_
 */
@UtilityClass
public class CompressionTestUtils {
    public static void forEachNioBufferTypeInput(byte @NonNull [] input, @NonNull EConsumer<? super ByteBuffer> action) {
        // heap ByteBuffer, wrapping entire array
        action.accept(ByteBuffer.wrap(input.clone()));

        // heap ByteBuffer, wrapping start of oversized array
        byte[] bigArray = PUnsafe.allocateUninitializedByteArray(input.length * 2);
        System.arraycopy(input, 0, bigArray, 0, input.length);
        action.accept(ByteBuffer.wrap(bigArray, 0, input.length));

        // heap ByteBuffer, wrapping middle segment of oversized array
        System.arraycopy(input, 0, bigArray, input.length / 2, input.length);
        action.accept(ByteBuffer.wrap(bigArray, input.length / 2, input.length));

        ByteBuffer directBuffer = ByteBuffer.allocateDirect(input.length * 2);
        try {
            // direct ByteBuffer, wrapping start of oversized memory segment
            directBuffer.put(input).flip();
            action.accept(directBuffer);

            // direct ByteBuffer, wrapping middle segment of oversized memory segment
            directBuffer.clear().position(input.length / 2);
            directBuffer.put(input);
            directBuffer.clear().position(input.length / 2).limit(input.length / 2 + input.length);
            action.accept(directBuffer);
        } finally {
            PUnsafe.pork_releaseBuffer(directBuffer);
        }
    }

    public static void forEachNioBufferTypeOutput(int outputLength, @NonNull EConsumer<? super ByteBuffer> action) {
        // heap ByteBuffer, wrapping entire array
        action.accept(ByteBuffer.wrap(PUnsafe.allocateUninitializedByteArray(outputLength)));

        // heap ByteBuffer, wrapping start of oversized array
        byte[] bigArray = PUnsafe.allocateUninitializedByteArray(outputLength * 2);
        action.accept(ByteBuffer.wrap(bigArray, 0, outputLength));

        // heap ByteBuffer, wrapping middle segment of oversized array
        action.accept(ByteBuffer.wrap(bigArray, outputLength / 2, outputLength));

        ByteBuffer directBuffer = ByteBuffer.allocateDirect(outputLength * 2);
        try {
            // direct ByteBuffer, wrapping start of oversized memory segment
            directBuffer.clear().limit(outputLength);
            action.accept(directBuffer);

            // direct ByteBuffer, wrapping middle segment of oversized memory segment
            directBuffer.clear().position(outputLength / 2).limit(outputLength / 2 + outputLength);
            action.accept(directBuffer);
        } finally {
            PUnsafe.pork_releaseBuffer(directBuffer);
        }
    }

    public static void forEachNetty4BufferTypeInput(byte @NonNull [] input, @NonNull EConsumer<? super ByteBuf> action) {
        // heap ByteBuf, wrapping entire array
        action.accept(Unpooled.wrappedBuffer(input.clone()));

        // heap ByteBuf, wrapping start of oversized array
        byte[] bigArray = PUnsafe.allocateUninitializedByteArray(input.length * 2);
        System.arraycopy(input, 0, bigArray, 0, input.length);
        action.accept(Unpooled.wrappedBuffer(bigArray, 0, input.length));

        // heap ByteBuf, wrapping middle segment of oversized array
        System.arraycopy(input, 0, bigArray, input.length / 2, input.length);
        action.accept(Unpooled.wrappedBuffer(bigArray, input.length / 2, input.length));

        ByteBuf directBuffer = Unpooled.directBuffer(input.length * 2, input.length * 2);
        try {
            // direct ByteBuf, wrapping start of oversized memory segment
            directBuffer.writeBytes(input);
            action.accept(directBuffer);

            // direct ByteBuf, wrapping middle segment of oversized memory segment
            directBuffer.clear().writerIndex(input.length / 2).writeBytes(input);
            action.accept(directBuffer);
        } finally {
            directBuffer.release();
        }

        //TODO: add composite and read-only buffers
    }
}
