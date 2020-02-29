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
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.compression.zstd.Zstd;
import net.daporkchop.lib.compression.zstd.ZstdCCtx;
import net.daporkchop.lib.compression.zstd.ZstdDCtx;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author DaPorkchop_
 */
public class ZstdTest {
    private static final int SIZE = 1 << 26; // 64 MiB

    private static final int BOUND_SIZE = PValidation.toPositiveIntSafe(Zstd.PROVIDER.compressBound(SIZE));

    public static void main(String... args) {
        System.out.printf("original: %d, worst-case compressed: %d\n", 1 << 16L, Zstd.PROVIDER.compressBound(1 << 16L));

        ByteBuf original = Unpooled.directBuffer(SIZE, SIZE).clear().ensureWritable(SIZE).writerIndex(SIZE).markWriterIndex();
        for (int i = 0; i < SIZE; i++) {
            original.setByte(i, i & 0xFF);
        }
        for (int i = 0; i < 256; i += 8) {
            original.setLongLE(i, ThreadLocalRandom.current().nextLong());
        }

        ByteBuf compressed = Unpooled.directBuffer(BOUND_SIZE, BOUND_SIZE);

        if (false) { //benchmark simple vs context compression
            long start = System.currentTimeMillis();
            for (int i = 0; i < 256; i++) {
                if (!Zstd.PROVIDER.compress(original.clear().writerIndex(SIZE), compressed.clear(), Zstd.LEVEL_MAX)) {
                    throw new IllegalStateException();
                }
            }
            System.out.printf("simple: %.2fs\n", (System.currentTimeMillis() - start) / 1000.0d);

            start = System.currentTimeMillis();
            try (ZstdCCtx ctx = Zstd.PROVIDER.compressionContext()) {
                for (int i = 0; i < 256; i++) {
                    if (!ctx.compress(original.clear().writerIndex(SIZE), compressed.clear(), Zstd.LEVEL_MAX)) {
                        throw new IllegalStateException();
                    }
                }
            }
            System.out.printf("context: %.2fs\n", (System.currentTimeMillis() - start) / 1000.0d);
        } else if (true) { //benchmark simple vs context decompression
            Zstd.PROVIDER.compress(original.clear().writerIndex(SIZE), compressed.clear(), Zstd.LEVEL_MAX);
            compressed.markWriterIndex();

            int uncompressedSize = Zstd.PROVIDER.frameContentSize(compressed);
            ByteBuf uncompressed = Unpooled.directBuffer(uncompressedSize, uncompressedSize);

            long start = System.currentTimeMillis();
            for (int i = 0; i < 256; i++) {
                if (!Zstd.PROVIDER.decompress(compressed.clear().resetWriterIndex(), uncompressed.clear())) {
                    throw new IllegalStateException();
                }
            }
            System.out.printf("simple: %.2fs\n", (System.currentTimeMillis() - start) / 1000.0d);

            start = System.currentTimeMillis();
            try (ZstdDCtx ctx = Zstd.PROVIDER.decompressionContext()) {
                for (int i = 0; i < 256; i++) {
                    if (!ctx.decompress(compressed.clear().resetWriterIndex(), uncompressed.clear())) {
                        throw new IllegalStateException();
                    }
                }
            }
            System.out.printf("context: %.2fs\n", (System.currentTimeMillis() - start) / 1000.0d);

            try (ZstdDCtx ctx = Zstd.PROVIDER.decompressionContext()) {
                if (!ctx.decompress(compressed.clear().resetWriterIndex(), uncompressed.clear())) {
                    throw new IllegalStateException();
                }
            }
            validateEqual(original, uncompressed, 0, SIZE);
        } else if (false) {
            Zstd.PROVIDER.compress(original.slice(), compressed, Zstd.LEVEL_DEFAULT);
            System.out.printf("original: %d, compressed: %d\n", original.readableBytes(), compressed.readableBytes());

            int uncompressedSize = PValidation.toPositiveIntSafe(Zstd.PROVIDER.frameContentSizeLong(compressed));
            System.out.printf("original size: %d, frame content size: %d\n", SIZE, uncompressedSize);
            ByteBuf uncompressed = Unpooled.directBuffer(uncompressedSize, uncompressedSize);

            Zstd.PROVIDER.decompress(compressed, uncompressed);

            validateEqual(original, uncompressed, 0, SIZE);
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
