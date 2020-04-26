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

package compression.zlib;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.daporkchop.lib.binary.stream.misc.SlashDevSlashNull;
import net.daporkchop.lib.compression.context.PDeflater;
import net.daporkchop.lib.compression.zlib.Zlib;
import net.daporkchop.lib.compression.zlib.ZlibStrategy;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
public class ZlibTest {
    @Before
    public void ensureNative() {
        checkState(Zlib.PROVIDER.isNative());
        checkState(Zlib.PROVIDER.compressBound(10) > 5);
    }

    @Test
    public void testOpenClose() {
        try (PDeflater deflater = Zlib.PROVIDER.deflater()) {
            System.out.println(deflater.getClass());
        }
    }

    @Test
    public void testCompression() throws IOException {
        try (PDeflater deflater = Zlib.PROVIDER.deflater()) {
            ByteBuf src = Unpooled.directBuffer(1024);
            ByteBuf dst = Unpooled.directBuffer(500);
            ByteBuf dict = Unpooled.directBuffer(1024);
            ByteBuf dictHeap = Unpooled.buffer(1024);

            dict.writeBytes(SlashDevSlashNull.INPUT_STREAM, 1024); //fill with zeroes
            dictHeap.writeBytes(SlashDevSlashNull.INPUT_STREAM, 1024);

            for (int i = 0; i < 32; i++) {
                System.out.println(i);

                src.readerIndex(0).writerIndex(1024);
                dst.readerIndex(0).writerIndex(0);
                //System.out.println("Attempting compression without dictionary");
                //System.out.printf("initial state: readable=%d, writable=%d\n", src.readableBytes(), dst.writableBytes());
                checkState(deflater.compress(src, dst), "compression failed");
                //System.out.printf("final state: readable=%d, compressed=%d\n", src.readableBytes(), dst.readableBytes());
                int withoutDictSize = dst.readableBytes();

                src.readerIndex(0).writerIndex(1024);
                dst.readerIndex(0).writerIndex(0);
                //System.out.println("Attempting compression with dictionary");
                //System.out.printf("initial state: readable=%d, writable=%d\n", src.readableBytes(), dst.writableBytes());
                checkState(deflater.compress(src, dst, dict), "compression failed");
                //System.out.printf("final state: readable=%d, compressed=%d\n", src.readableBytes(), dst.readableBytes());
                int withDictSize = dst.readableBytes();

                src.readerIndex(0).writerIndex(1024);
                dst.readerIndex(0).writerIndex(0);
                //System.out.println("Attempting compression with heap dictionary");
                //System.out.printf("initial state: readable=%d, writable=%d\n", src.readableBytes(), dst.writableBytes());
                checkState(deflater.compress(src, dst, dictHeap), "compression failed");
                //System.out.printf("final state: readable=%d, compressed=%d\n", src.readableBytes(), dst.readableBytes());
                int withHeapDictSize = dst.readableBytes();

                //checkState(withDictSize < withoutDictSize, "withDictSize (%d) >= withoutDictSize (%d)", withDictSize, withoutDictSize);
                checkState(withDictSize == withHeapDictSize, "withDictSize (%d) != withHeapDictSize (%d)", withDictSize, withHeapDictSize);
            }
        }
    }
}
