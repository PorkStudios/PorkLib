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
import net.daporkchop.lib.compression.PDeflater;
import net.daporkchop.lib.compression.zlib.Zlib;

/**
 * @author DaPorkchop_
 */
public class ZlibTest {
    private static final int SIZE = 1 << 26; // 64 MiB

    public static void main(String... args) {
        ByteBuf original = Unpooled.directBuffer(SIZE, SIZE).clear().ensureWritable(SIZE).writerIndex(SIZE);
        ByteBuf compressedNative = Unpooled.directBuffer(SIZE, SIZE).clear().ensureWritable(SIZE);

        try (PDeflater deflater = Zlib.PROVIDER.get().deflater(Zlib.LEVEL_DEFAULT, Zlib.STRATEGY_DEFAULT))  {
            if (!deflater.deflate(original, compressedNative))  {
                throw new IllegalStateException("Couldn't deflate data!");
            }
        }

        System.out.printf("original: %d, compressed: %d\n", SIZE, compressedNative.readableBytes());
    }
}
