/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.natives.PNatives;
import net.daporkchop.lib.natives.zlib.PDeflater;
import net.daporkchop.lib.natives.zlib.PInflater;
import net.daporkchop.lib.natives.zlib.Zlib;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author DaPorkchop_
 */
public class NativeTests {
    private static final int SIZE = 67108864; //64 MiB

    public static void main(String... args) throws IOException {
        ByteBuf orig = Unpooled.directBuffer(SIZE).writerIndex(SIZE);
        for (int i = 0; i < SIZE; i++) {
            orig.setByte(i, ThreadLocalRandom.current().nextInt(8));
        }
        ByteBuf compressed = Unpooled.directBuffer(SIZE >>> 3, SIZE).clear();

        try (PDeflater deflater = PNatives.ZLIB.get().deflater(Zlib.ZLIB_LEVEL_BEST)) {
            System.out.printf("Deflating with %s...\nFinished: %b\n", PorkUtil.className(deflater), deflater.finished());
            deflater.deflate(orig, compressed);
            System.out.printf("Read: %d\nWritten: %d\nFinished: %b\n", deflater.readBytes(), deflater.writtenBytes(), deflater.finished());
        }

        ByteBuf decompressed = Unpooled.directBuffer(SIZE, SIZE).clear();

        try (PInflater inflater = PNatives.ZLIB.get().inflater()) {
            System.out.printf("Inflating with %s...\nFinished: %b\n", PorkUtil.className(inflater), inflater.finished());
            inflater.inflate(compressed, decompressed);
            System.out.printf("Read: %d\nWritten: %d\nFinished: %b\n", inflater.readBytes(), inflater.writtenBytes(), inflater.finished());
        }

        for (int i = 0; i < SIZE; i++) {
            if (orig.getByte(i) != decompressed.getByte(i)) {
                throw new IllegalStateException();
            }
        }
    }
}
