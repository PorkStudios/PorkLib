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
import net.daporkchop.lib.compression.zstd.Zstd;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author DaPorkchop_
 */
public class ZstdTest {
    private static final int SIZE      = 1 << 26; // 64 MiB

    private static final int BOUND_SIZE = (int) Zstd.PROVIDER.compressBound(SIZE);

    public static void main(String... args) {
        System.out.printf("original: %d, worst-case compressed: %d\n", 1 << 16L, Zstd.PROVIDER.compressBound(1 << 16L));

        ByteBuf original = Unpooled.directBuffer(SIZE, SIZE).clear().ensureWritable(SIZE).writerIndex(SIZE);
        for (int i = 0; i < SIZE; i++) {
            original.setByte(i, i & 0xFF);
        }
        for (int i = 0; i < 256; i += 8) {
            original.setLongLE(i, ThreadLocalRandom.current().nextLong());
        }

        ByteBuf compressed = Unpooled.directBuffer(BOUND_SIZE, BOUND_SIZE);

        Zstd.PROVIDER.compress(original.slice(), compressed, Zstd.LEVEL_DEFAULT);
        System.out.printf("original: %d, compressed: %d\n", original.readableBytes(), compressed.readableBytes());

        int uncompressedSize = (int) Zstd.PROVIDER.frameContentSize(compressed);
        System.out.printf("original size: %d, frame content size: %d\n", SIZE, uncompressedSize);
        ByteBuf uncompressed = Unpooled.directBuffer(uncompressedSize, uncompressedSize);

        Zstd.PROVIDER.decompress(compressed, uncompressed);

        validateEqual(original, uncompressed, 0, SIZE);
    }

    private static void validateEqual(@NonNull ByteBuf a, @NonNull ByteBuf b, int start, int size) {
        for (int i = 0; i < size; i++) {
            if (a.getByte(start + i) != b.getByte(start + i)) {
                throw new IllegalStateException();
            }
        }
    }
}
