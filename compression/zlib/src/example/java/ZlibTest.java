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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.NonNull;
import net.daporkchop.lib.compression.PDeflater;
import net.daporkchop.lib.compression.zlib.Zlib;

import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * @author DaPorkchop_
 */
public class ZlibTest {
    private static final int SIZE = 1 << 26; // 64 MiB

    public static void main(String... args) throws DataFormatException {
        ByteBuf original = Unpooled.directBuffer(SIZE, SIZE).clear().ensureWritable(SIZE).writerIndex(SIZE);
        for (int i = 0; i < SIZE; i++)  {
            original.setByte(i, i & 0xFF);
        }
        for (int i = 0; i < 512; i += 8) {
            original.setLongLE(i, ThreadLocalRandom.current().nextLong());
        }

        {
            ByteBuf compressedNative = Unpooled.directBuffer(SIZE >>> 4, SIZE >>> 4).clear().ensureWritable(SIZE >>> 4);

            try (PDeflater deflater = Zlib.PROVIDER.get().deflater(Zlib.LEVEL_DEFAULT, Zlib.STRATEGY_DEFAULT)) {
                if (!deflater.fullDeflate(original, compressedNative)) {
                    throw new IllegalStateException("Couldn't deflate data!");
                }
            }

            System.out.printf("original: %d, compressed: %d\n", SIZE, compressedNative.readableBytes());

            byte[] compressedHeap = new byte[compressedNative.readableBytes()];
            compressedNative.readBytes(compressedHeap);
            compressedNative.release();

            byte[] uncompressedHeap = new byte[SIZE];

            Inflater inflater = new Inflater();
            inflater.setInput(compressedHeap);
            int cnt = inflater.inflate(uncompressedHeap);
            if (cnt != SIZE) {
                throw new IllegalStateException(String.format("Only inflated %d/%d bytes!", cnt, SIZE));
            }

            validateEqual(original, Unpooled.wrappedBuffer(uncompressedHeap), 0, SIZE);
        }

        {
            ByteBuf compressedNative = Unpooled.directBuffer(SIZE >>> 4, SIZE >>> 4).clear().ensureWritable(SIZE >>> 4);

            try (PDeflater deflater = Zlib.PROVIDER.get().deflater(Zlib.LEVEL_DEFAULT, Zlib.STRATEGY_DEFAULT)) {
                deflater.dst(compressedNative);
                for (int i = 0; i < (1 << 4); i++) {
                    ByteBuf src = original.slice((SIZE >>> 4) * i, SIZE >>> 4);
                    deflater.src(src).update(true);
                    if (src.isReadable())   {
                        throw new IllegalStateException();
                    }
                }

                if (!deflater.finish()) {
                    throw new IllegalStateException("Couldn't deflate data!");
                }
            }

            System.out.printf("original: %d, compressed: %d\n", SIZE, compressedNative.readableBytes());

            byte[] compressedHeap = new byte[compressedNative.readableBytes()];
            compressedNative.readBytes(compressedHeap);
            compressedNative.release();

            byte[] uncompressedHeap = new byte[SIZE];

            Inflater inflater = new Inflater();
            inflater.setInput(compressedHeap);
            int cnt = inflater.inflate(uncompressedHeap);
            if (cnt != SIZE) {
                throw new IllegalStateException(String.format("Only inflated %d/%d bytes!", cnt, SIZE));
            }

            validateEqual(original, Unpooled.wrappedBuffer(uncompressedHeap), 0, SIZE);
        }
    }

    private static void validateEqual(@NonNull ByteBuf a, @NonNull ByteBuf b, int start, int size) {
        for (int i = 0; i < size; i++) {
            if (a.getByte(start + i) != b.getByte(start + i)) {
                throw new IllegalStateException();
            }
        }
    }
}
