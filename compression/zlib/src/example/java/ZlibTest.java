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
import net.daporkchop.lib.compression.PInflater;
import net.daporkchop.lib.compression.zlib.Zlib;

import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * @author DaPorkchop_
 */
public class ZlibTest {
    private static final int SIZE      = 1 << 26; // 64 MiB
    private static final int DICT_SIZE = 256 * 8;//32768;

    public static void main(String... args) throws DataFormatException {
        ByteBuf original = Unpooled.directBuffer(SIZE, SIZE).clear().ensureWritable(SIZE).writerIndex(SIZE);
        for (int i = 0; i < SIZE; i++) {
            original.setByte(i, i & 0xFF);
        }
        for (int i = 0; i < 256; i += 8) {
            original.setLongLE(i, ThreadLocalRandom.current().nextLong());
        }

        {
            ByteBuf compressedNative = Unpooled.directBuffer(SIZE >>> 4, SIZE >>> 4);

            try (PDeflater deflater = Zlib.PROVIDER.get().deflater()) {
                if (!deflater.fullDeflate(original.slice(), compressedNative)) {
                    throw new IllegalStateException("Couldn't deflate data!");
                }
            }

            System.out.printf("original: %d, compressed: %d\n", SIZE, compressedNative.readableBytes());

            byte[] compressedHeap = new byte[compressedNative.readableBytes()];
            compressedNative.getBytes(0, compressedHeap);

            byte[] uncompressedHeap = new byte[SIZE];

            {
                Inflater inflater = new Inflater();
                inflater.setInput(compressedHeap);
                int cnt = inflater.inflate(uncompressedHeap);
                if (cnt != SIZE) {
                    throw new IllegalStateException(String.format("Only inflated %d/%d bytes!", cnt, SIZE));
                }
                inflater.end();
            }

            validateEqual(original, Unpooled.wrappedBuffer(uncompressedHeap), 0, SIZE);

            ByteBuf uncompressedNative = Unpooled.directBuffer(SIZE, SIZE);
            try (PInflater inflater = Zlib.PROVIDER.get().inflater()) {
                if (!inflater.fullInflate(compressedNative, uncompressedNative)) {
                    throw new IllegalStateException("Couldn't inflate data!");
                }
                validateEqual(original, uncompressedNative, 0, SIZE);

                uncompressedNative.clear();
                inflater.reset().src(compressedNative.resetReaderIndex());
                for (int i = 0; i < 16; i++) {
                    ByteBuf dst = uncompressedNative.slice((SIZE >>> 4) * i, SIZE >>> 4).clear();
                    inflater.dst(dst).update(false);
                    if (dst.isWritable()) {
                        throw new IllegalStateException();
                    }
                }
                validateEqual(original, uncompressedNative, 0, SIZE);
            }
        }

        {
            ByteBuf compressedNative = Unpooled.directBuffer(SIZE >>> 4, SIZE >>> 4);

            try (PDeflater deflater = Zlib.PROVIDER.get().deflater()) {
                deflater.dict(original.slice(0, DICT_SIZE));
                if (false) {
                    deflater.dst(compressedNative);
                    for (int i = 0; i < 16; i++) {
                        ByteBuf src = original.slice((SIZE >>> 4) * i, SIZE >>> 4);
                        deflater.src(src).update(false);
                        if (src.isReadable()) {
                            throw new IllegalStateException();
                        }
                    }

                    if (!deflater.finish()) {
                        throw new IllegalStateException("Couldn't deflate data!");
                    }
                } else {
                    if (!deflater.fullDeflate(original.slice(), compressedNative)) {
                        throw new IllegalStateException("Couldn't deflate data!");
                    }
                }
            }

            System.out.printf("original: %d, compressed: %d\n", SIZE, compressedNative.readableBytes());

            byte[] compressedHeap = new byte[compressedNative.readableBytes()];
            compressedNative.getBytes(0, compressedHeap);

            byte[] uncompressedHeap = new byte[SIZE];

            {
                Inflater inflater = new Inflater();

                inflater.setInput(compressedHeap);
                int cnt = inflater.inflate(uncompressedHeap);
                if (cnt != 0) {
                    throw new IllegalStateException(String.format("Inflated %d bytes without asking for a dictionary?!?", cnt));
                } else if (!inflater.needsDictionary()) {
                    throw new IllegalStateException("Not requiring dictionary!");
                }

                byte[] dict = new byte[DICT_SIZE];
                original.getBytes(0, dict);
                inflater.setDictionary(dict);

                cnt = inflater.inflate(uncompressedHeap);
                if (cnt != SIZE) {
                    throw new IllegalStateException(String.format("Only inflated %d/%d bytes!", cnt, SIZE));
                }
                inflater.end();
            }

            validateEqual(original, Unpooled.wrappedBuffer(uncompressedHeap), 0, SIZE);

            ByteBuf uncompressedNative = Unpooled.directBuffer(SIZE, SIZE);
            try (PInflater inflater = Zlib.PROVIDER.get().inflater()) {
                if (!inflater.dict(original.slice(0, DICT_SIZE))
                        .fullInflate(compressedNative, uncompressedNative)) {
                    throw new IllegalStateException("Couldn't inflate data!");
                }
                validateEqual(original, uncompressedNative, 0, SIZE);

                uncompressedNative.clear();
                inflater.reset()
                        .dict(original.slice(0, DICT_SIZE))
                        .src(compressedNative.resetReaderIndex());
                for (int i = 0; i < 16; i++) {
                    ByteBuf dst = uncompressedNative.slice((SIZE >>> 4) * i, SIZE >>> 4).clear();
                    inflater.dst(dst).update(false);
                    if (dst.isWritable()) {
                        throw new IllegalStateException();
                    }
                }
                validateEqual(original, uncompressedNative, 0, SIZE);
            }
        }

        {
            ByteBuf compressedNative = Unpooled.directBuffer(SIZE >>> 4, SIZE >>> 4);

            try (PDeflater deflater = Zlib.PROVIDER.get().deflater()) {
                deflater.dst(compressedNative);
                for (int i = 0; i < 16; i++) {
                    ByteBuf src = original.slice((SIZE >>> 4) * i, SIZE >>> 4);
                    deflater.src(src).update(true);
                    if (src.isReadable()) {
                        throw new IllegalStateException();
                    }
                }

                if (!deflater.finish()) {
                    throw new IllegalStateException("Couldn't deflate data!");
                }
            }

            System.out.printf("original: %d, compressed: %d\n", SIZE, compressedNative.readableBytes());

            byte[] compressedHeap = new byte[compressedNative.readableBytes()];
            compressedNative.getBytes(0, compressedHeap);

            byte[] uncompressedHeap = new byte[SIZE];

            {
                Inflater inflater = new Inflater();
                inflater.setInput(compressedHeap);
                int cnt = inflater.inflate(uncompressedHeap);
                if (cnt != SIZE) {
                    throw new IllegalStateException(String.format("Only inflated %d/%d bytes!", cnt, SIZE));
                }
                inflater.end();
            }

            validateEqual(original, Unpooled.wrappedBuffer(uncompressedHeap), 0, SIZE);

            ByteBuf uncompressedNative = Unpooled.directBuffer(SIZE, SIZE);
            try (PInflater inflater = Zlib.PROVIDER.get().inflater()) {
                if (!inflater.fullInflate(compressedNative, uncompressedNative)) {
                    throw new IllegalStateException("Couldn't inflate data!");
                }
                validateEqual(original, uncompressedNative, 0, SIZE);

                uncompressedNative.clear();
                inflater.reset().src(compressedNative.resetReaderIndex());
                for (int i = 0; i < 16; i++) {
                    ByteBuf dst = uncompressedNative.slice((SIZE >>> 4) * i, SIZE >>> 4).clear();
                    inflater.dst(dst).update(false);
                    if (dst.isWritable()) {
                        throw new IllegalStateException();
                    }
                }
                validateEqual(original, uncompressedNative, 0, SIZE);
            }
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
