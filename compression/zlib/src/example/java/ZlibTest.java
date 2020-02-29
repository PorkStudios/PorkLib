/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
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

            try (PDeflater deflater = Zlib.PROVIDER.deflater()) {
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
            try (PInflater inflater = Zlib.PROVIDER.inflater()) {
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
            ByteBuf compressedNative = Unpooled.directBuffer(16, SIZE >>> 4);

            try (PDeflater deflater = Zlib.PROVIDER.deflater()) {
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
                } else if (true) {
                    deflater.fullDeflateGrowing(original.slice(), compressedNative);
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
            try (PInflater inflater = Zlib.PROVIDER.inflater()) {
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

            try (PDeflater deflater = Zlib.PROVIDER.deflater()) {
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
            try (PInflater inflater = Zlib.PROVIDER.inflater()) {
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
