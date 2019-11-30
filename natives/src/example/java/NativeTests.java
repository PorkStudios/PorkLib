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
import net.daporkchop.lib.natives.PNatives;
import net.daporkchop.lib.natives.zlib.JavaDeflater;
import net.daporkchop.lib.natives.zlib.PDeflater;
import net.daporkchop.lib.natives.zlib.PInflater;
import net.daporkchop.lib.natives.zlib.Zlib;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.InflaterOutputStream;

/**
 * @author DaPorkchop_
 */
public class NativeTests {
    private static final int SIZE = 67108864; //64 MiB

    public static void main(String... args) throws IOException {
        if (true) {
            ByteBuf orig = Unpooled.directBuffer(SIZE).writerIndex(SIZE);
            for (int i = 0; i < 128; i++) {
                orig.setByte(i, ThreadLocalRandom.current().nextInt(8));
            }
            ByteBuf compressed = Unpooled.directBuffer(SIZE >>> 3, SIZE).clear();

            try (PDeflater deflater = /*PNatives.ZLIB.get().deflater(Zlib.ZLIB_LEVEL_BEST)*/new JavaDeflater(Zlib.ZLIB_LEVEL_BEST, Zlib.ZLIB_MODE_ZLIB)) {
                System.out.printf("Deflating...\nFinished: %b\n", deflater.finished());
                deflater.deflate(orig, compressed);
                System.out.printf("Read: %d\nWritten: %d\nFinished: %b\n", deflater.readBytes(), deflater.writtenBytes(), deflater.finished());
            }

            ByteBuf decompressed = Unpooled.directBuffer(SIZE, SIZE).clear();

            try (PInflater inflater = PNatives.ZLIB.get().inflater()) {
                System.out.printf("Inflating...\nFinished: %b\n", inflater.finished());
                inflater.inflate(compressed, decompressed);
                System.out.printf("Read: %d\nWritten: %d\nFinished: %b\n", inflater.readBytes(), inflater.writtenBytes(), inflater.finished());
            }

            for (int i = 0; i < SIZE; i++) {
                if (orig.getByte(i) != decompressed.getByte(i)) {
                    throw new IllegalStateException();
                }
            }
        } else if (true)    {
            File srcFile = new File("/home/daporkchop/Desktop/java.tar.gz");
            File dstFile = new File("/home/daporkchop/Desktop/java.tar");

            ByteBuf src;
            try (FileChannel channel = FileChannel.open(srcFile.toPath(), StandardOpenOption.READ)) {
                int size = (int) channel.size();
                src = Unpooled.directBuffer(size, size).clear();
                src.writeBytes(channel, 0L, size);
                if (src.isWritable())   {
                    throw new IllegalStateException(String.format("Only read %d bytes!", src.writerIndex()));
                }
            }

            System.out.printf("Original size: %d bytes\n", src.readableBytes());

            ByteBuf dst = Unpooled.directBuffer(src.readableBytes(), SIZE);
            try (PInflater inflater = PNatives.ZLIB.get().inflater(Zlib.ZLIB_MODE_AUTO))   {
                inflater.inflate(src, dst);
            }

            System.out.printf("Inflated size: %d bytes\n", dst.readableBytes());

            try (FileChannel channel = FileChannel.open(dstFile.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))    {
                dst.readBytes(channel, 0L, dst.readableBytes());
                if (dst.isReadable())   {
                    throw new IllegalStateException(String.format("Only wrote %d bytes!", src.readableBytes()));
                }
            }
        }
    }
}
