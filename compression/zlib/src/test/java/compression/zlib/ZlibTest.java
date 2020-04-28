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
import net.daporkchop.lib.binary.oio.StreamUtil;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.binary.stream.misc.SlashDevSlashNull;
import net.daporkchop.lib.compression.context.PDeflater;
import net.daporkchop.lib.compression.context.PInflater;
import net.daporkchop.lib.compression.zlib.Zlib;
import net.daporkchop.lib.compression.zlib.ZlibMode;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
public class ZlibTest {
    protected byte[] text;
    protected byte[] gzipped;

    @Before
    public void loadText() throws IOException {
        try (InputStream in = new FileInputStream(new File("../../LICENSE"))) {
            this.text = StreamUtil.toByteArray(in);
        }
        try (InputStream in = new FileInputStream(new File("../../encoding/nbt/src/test/resources/bigtest.nbt"))) {
            this.gzipped = StreamUtil.toByteArray(in);
        }
    }

    @Test
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
    public void testBlockCompression() throws IOException {
        try (PDeflater deflater = Zlib.PROVIDER.deflater()) {
            ByteBuf src = Unpooled.directBuffer(this.text.length).writeBytes(this.text).markReaderIndex().markWriterIndex();
            ByteBuf dst = Unpooled.directBuffer(Zlib.PROVIDER.compressBound(src.readableBytes()));
            ByteBuf dict = Unpooled.directBuffer(1024);
            ByteBuf dictHeap = Unpooled.buffer(1024);

            dict.writeBytes(SlashDevSlashNull.INPUT_STREAM, 1024); //fill with zeroes
            dictHeap.writeBytes(SlashDevSlashNull.INPUT_STREAM, 1024);

            for (int i = 0; i < 32; i++) {
                System.out.printf(i + " ");
                System.out.flush();

                src.resetReaderIndex().resetWriterIndex();
                dst.clear();
                //System.out.println("Attempting compression without dictionary");
                //System.out.printf("initial state: readable=%d, writable=%d\n", src.readableBytes(), dst.writableBytes());
                checkState(deflater.compress(src, dst), "compression failed");
                //System.out.printf("final state: readable=%d, compressed=%d\n", src.readableBytes(), dst.readableBytes());
                int withoutDictSize = dst.readableBytes();

                src.resetReaderIndex().resetWriterIndex();
                dst.clear();
                //System.out.println("Attempting compression with dictionary");
                //System.out.printf("initial state: readable=%d, writable=%d\n", src.readableBytes(), dst.writableBytes());
                checkState(deflater.compress(src, dst, dict), "compression failed");
                //System.out.printf("final state: readable=%d, compressed=%d\n", src.readableBytes(), dst.readableBytes());
                int withDictSize = dst.readableBytes();

                src.resetReaderIndex().resetWriterIndex();
                dst.clear();
                //System.out.println("Attempting compression with heap dictionary");
                //System.out.printf("initial state: readable=%d, writable=%d\n", src.readableBytes(), dst.writableBytes());
                checkState(deflater.compress(src, dst, dictHeap), "compression failed");
                //System.out.printf("final state: readable=%d, compressed=%d\n", src.readableBytes(), dst.readableBytes());
                int withHeapDictSize = dst.readableBytes();

                //checkState(withDictSize < withoutDictSize, "withDictSize (%d) >= withoutDictSize (%d)", withDictSize, withoutDictSize);
                checkState(withDictSize == withHeapDictSize, "withDictSize (%d) != withHeapDictSize (%d)", withDictSize, withHeapDictSize);
            }
            System.out.println();
        }
    }

    @Test
    public void testStreamDecompression() throws IOException {
        try (PInflater inflater = Zlib.PROVIDER.inflater(Zlib.PROVIDER.defaultInflaterOptions().builder().mode(ZlibMode.AUTO).build())) {
            ByteBuf src = Unpooled.directBuffer(this.gzipped.length).writeBytes(this.gzipped).markReaderIndex().markWriterIndex();
            ByteBuf dst = Unpooled.directBuffer(1544);

            System.out.println("Gzipped size: " + src.readableBytes());
            try (DataIn in = inflater.decompressionStream(DataIn.wrap(src))) {
                System.out.println(in.read(dst, dst.writableBytes() - 4));
                System.out.println(in.read(dst));
            }
            System.out.println("Inflated size: " + dst.readableBytes());
        }
    }

    @Test
    public void testStreamCompression() throws IOException {
        try (PDeflater deflater = Zlib.PROVIDER.deflater()) {
            ByteBuf src = Unpooled.directBuffer(this.text.length).writeBytes(this.text).markReaderIndex().markWriterIndex();
            ByteBuf dst = Unpooled.directBuffer(500);
            ByteBuf dict = Unpooled.directBuffer(1024);
            ByteBuf dictHeap = Unpooled.buffer(1024);

            dict.writeBytes(SlashDevSlashNull.INPUT_STREAM, 1024); //fill with zeroes
            dictHeap.writeBytes(SlashDevSlashNull.INPUT_STREAM, 1024);

            for (int i = 0; i < 32; i++) {
                System.out.printf(i + " ");
                System.out.flush();

                src.resetReaderIndex().resetWriterIndex();
                dst.clear();
                //System.out.println("Attempting streaming compression without dictionary");
                //System.out.printf("initial state: readable=%d, writable=%d\n", src.readableBytes(), dst.writableBytes());
                try (DataOut out = deflater.compressionStream(DataOut.wrap(dst))) {
                    out.write(src);
                    //System.out.printf("intermediate (pre-flush) state: readable=%d, compressed=%d\n", src.readableBytes(), dst.readableBytes());
                    out.flush();
                    //System.out.printf("intermediate (post-flush) state: readable=%d, compressed=%d\n", src.readableBytes(), dst.readableBytes());
                }
                //System.out.printf("final state: readable=%d, compressed=%d\n", src.readableBytes(), dst.readableBytes());
            }
            System.out.println();
        }
    }

    @Test
    public void test() throws IOException {
        try (PDeflater deflater = Zlib.PROVIDER.deflater();
             PInflater inflater = Zlib.PROVIDER.inflater()) {
            ByteBuf origSrc = Unpooled.directBuffer().writeBytes(this.text);
            ByteBuf compressed = Unpooled.directBuffer();
            deflater.compressGrowing(origSrc, compressed);
            ByteBuf uncompressed = Unpooled.directBuffer();
            inflater.decompressGrowing(compressed, uncompressed);
            for (int i = 0; i < this.text.length; i++)  {
                checkState(this.text[i] == uncompressed.getByte(i));
            }
            origSrc.release();
            compressed.release();
            uncompressed.release();
        }
    }

    @Test
    public void testStream() throws IOException {
        try (PDeflater deflater = Zlib.PROVIDER.deflater();
             PInflater inflater = Zlib.PROVIDER.inflater()) {
        }
    }
}
