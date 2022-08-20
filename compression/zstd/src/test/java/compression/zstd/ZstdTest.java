/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
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

package compression.zstd;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.NonNull;
import net.daporkchop.lib.binary.oio.StreamUtil;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.binary.stream.misc.SlashDevSlashNull;
import net.daporkchop.lib.common.function.io.IOConsumer;
import net.daporkchop.lib.compression.context.PDeflater;
import net.daporkchop.lib.compression.context.PInflater;
import net.daporkchop.lib.compression.zstd.Zstd;
import net.daporkchop.lib.compression.zstd.ZstdDeflateDictionary;
import net.daporkchop.lib.compression.zstd.ZstdInflateDictionary;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
public class ZstdTest {
    protected byte[] text;
    protected byte[] zstd;
    protected byte[] dictionary;
    protected byte[] zeroes;

    @Before
    public void loadText() throws IOException {
        try (InputStream in = new FileInputStream(new File("../../LICENSE"))) {
            this.text = StreamUtil.toByteArray(in);
        }
        try (InputStream in = ZstdTest.class.getResourceAsStream("/LICENSE.zst")) {
            this.zstd = StreamUtil.toByteArray(in);
        }
        try (InputStream in = ZstdTest.class.getResourceAsStream("/dictionary")) {
            this.dictionary = StreamUtil.toByteArray(in);
        }
        this.zeroes = new byte[1 << 20];
    }

    @Test
    public void testFrameContentSize() {
        this.forEachBufferType(1, buffers -> {
            ByteBuf buf = buffers[0].writeBytes(this.zstd);
            long size = Zstd.PROVIDER.frameContentSizeLong(buf);
            checkState(size == 1232, "size (%d) != 1232", size);
        });
    }

    @Test
    public void testDigestDeflateDictionary() {
        this.forEachBufferType(1, buffers -> {
            ByteBuf buf = buffers[0].writeBytes(this.dictionary);
            try (ZstdDeflateDictionary dict = Zstd.PROVIDER.loadDeflateDictionary(buf)) {
            }
        });
        this.forEachBufferType(1, buffers -> {
            ByteBuf buf = buffers[0].writeBytes(this.dictionary);
            try (ZstdInflateDictionary dict = Zstd.PROVIDER.loadInflateDictionary(buf)) {
                int id = dict.id();
                checkState(id == 1885204170, "id (%d) != 1885204170", id);
            }
        });
    }

    @Test
    public void testOpenClose() {
        try (PDeflater deflater = Zstd.PROVIDER.deflater()) {
            System.out.println(deflater.getClass());
        }
        try (PInflater inflater = Zstd.PROVIDER.inflater()) {
            System.out.println(inflater.getClass());
        }
    }

    @Test
    public void testBlockCompression() throws IOException {
        try (PDeflater deflater = Zstd.PROVIDER.deflater()) {
            //one-shot
            this.forEachBufferType(2, buffers -> {
                ByteBuf src = buffers[0].writeBytes(this.text);
                ByteBuf dst = buffers[1].ensureWritable(deflater.provider().compressBound(src.readableBytes()));

                checkState(deflater.compress(src, dst), "compression failed!");
            });
            this.forEachBufferType(3, buffers -> {
                ByteBuf src = buffers[0].writeBytes(this.text);
                ByteBuf dst = buffers[1].ensureWritable(deflater.provider().compressBound(src.readableBytes()));
                ByteBuf dict = buffers[2];
                SlashDevSlashNull.INSTANCE.read(dict, 1024);

                checkState(deflater.compress(src, dst, dict), "compression failed!");
            });

            //growing
            this.forEachBufferType(2, buffers -> {
                ByteBuf src = buffers[0].writeBytes(this.text);
                ByteBuf dst = buffers[1];

                deflater.compressGrowing(src, dst);
            });
            this.forEachBufferType(3, buffers -> {
                ByteBuf src = buffers[0].writeBytes(this.text);
                ByteBuf dst = buffers[1];
                ByteBuf dict = buffers[2];
                SlashDevSlashNull.INSTANCE.read(dict, 1024);

                deflater.compressGrowing(src, dst, dict);
            });
        }
    }

    @Test
    public void testBlockDecompression() throws IOException {
        try (PInflater inflater = Zstd.PROVIDER.inflater()) {
            //one-shot
            this.forEachBufferType(2, buffers -> {
                ByteBuf src = buffers[0].writeBytes(this.zstd);
                ByteBuf dst = buffers[1].ensureWritable(8192);

                checkState(inflater.decompress(src, dst));
            });

            //growing
            this.forEachBufferType(2, buffers -> {
                ByteBuf src = buffers[0].writeBytes(this.zstd);
                ByteBuf dst = buffers[1];

                inflater.decompressGrowing(src, dst);
            });
        }
    }

    @Test
    public void testStreamCompression() throws IOException {
        try (PDeflater deflater = Zstd.PROVIDER.deflater()) {
            this.forEachBufferType(2, buffers -> {
                ByteBuf src = buffers[0].writeBytes(this.text);
                ByteBuf dst = buffers[1].ensureWritable(deflater.provider().compressBound(src.readableBytes()));

                try (DataOut out = deflater.compressionStream(DataOut.wrapView(dst))) {
                    out.write(src);
                }
            });
            this.forEachBufferType(3, buffers -> {
                ByteBuf src = buffers[0].writeBytes(this.text);
                ByteBuf dst = buffers[1].ensureWritable(deflater.provider().compressBound(src.readableBytes()));
                ByteBuf dict = buffers[2];
                SlashDevSlashNull.INSTANCE.read(dict, 1024);

                try (DataOut out = deflater.compressionStream(DataOut.wrapView(dst), dict)) {
                    out.write(src);
                }
            });
        }
    }

    @Test
    public void testStreamDecompression() throws IOException {
        try (PInflater inflater = Zstd.PROVIDER.inflater()) {
            this.forEachBufferType(2, buffers -> {
                ByteBuf src = buffers[0].writeBytes(this.zstd);
                ByteBuf dst = buffers[1].ensureWritable(8192);

                try (DataIn in = inflater.decompressionStream(DataIn.wrapView(src))) {
                    in.read(dst);
                    checkState(in.remaining() == 0L, "there was more data remaining!");
                }

                for (int i = 0; i < this.text.length; i++) {
                    checkState(dst.getByte(i) == this.text[i], "Difference at index %s (src=%s)", i, src);
                }
            });
        }
    }

    @Test
    public void testBlock() throws IOException {
        try (PDeflater deflater = Zstd.PROVIDER.deflater();
             PInflater inflater = Zstd.PROVIDER.inflater()) {
            //one-shot
            this.forEachBufferType(3, buffers -> {
                ByteBuf src = buffers[0].writeBytes(this.zeroes);
                ByteBuf compressed = buffers[1].ensureWritable(deflater.provider().compressBound(src.readableBytes()));
                ByteBuf uncompressed = buffers[2].ensureWritable(src.readableBytes());
                checkState(deflater.compress(src, compressed), "compression failed!");
                checkState(inflater.decompress(compressed, uncompressed), "decompression failed!");

                for (int i = 0; i < src.writerIndex(); i++) {
                    checkState(src.getByte(i) == uncompressed.getByte(i), "Difference at index %s (src=%s, uncompressed=%s)", i, src, uncompressed);
                }
            });

            //growing
            this.forEachBufferType(3, buffers -> {
                ByteBuf src = buffers[0].writeBytes(this.zeroes);
                ByteBuf compressed = buffers[1];
                ByteBuf uncompressed = buffers[2];
                deflater.compressGrowing(src, compressed);
                inflater.decompressGrowing(compressed, uncompressed);

                for (int i = 0; i < src.writerIndex(); i++) {
                    checkState(src.getByte(i) == uncompressed.getByte(i), "Difference at index %s (src=%s, uncompressed=%s)", i, src, uncompressed);
                }
            });
        }
    }

    @Test
    public void testBlockWithDictionary() throws IOException {
        try (PDeflater deflater = Zstd.PROVIDER.deflater();
             PInflater inflater = Zstd.PROVIDER.inflater()) {
            //one-shot
            this.forEachBufferType(4, buffers -> {
                ByteBuf src = buffers[0].writeBytes(this.text);
                ByteBuf compressed = buffers[1].ensureWritable(deflater.provider().compressBound(src.readableBytes()));
                ByteBuf uncompressed = buffers[2].ensureWritable(src.readableBytes());
                ByteBuf dict = buffers[3].writeBytes(this.dictionary);
                checkState(deflater.compress(src, compressed, dict), "compression failed!");
                checkState(inflater.decompress(compressed, uncompressed, dict), "decompression failed!");

                for (int i = 0; i < src.writerIndex(); i++) {
                    checkState(src.getByte(i) == uncompressed.getByte(i), "Difference at index %s (src=%s, uncompressed=%s)", i, src, uncompressed);
                }
            });

            //growing
            this.forEachBufferType(4, buffers -> {
                ByteBuf src = buffers[0].writeBytes(this.text);
                ByteBuf compressed = buffers[1];
                ByteBuf uncompressed = buffers[2];
                ByteBuf dict = buffers[3].writeBytes(this.dictionary);
                deflater.compressGrowing(src, compressed, dict);
                inflater.decompressGrowing(compressed, uncompressed, dict);

                for (int i = 0; i < src.writerIndex(); i++) {
                    checkState(src.getByte(i) == uncompressed.getByte(i), "Difference at index %s (src=%s, uncompressed=%s)", i, src, uncompressed);
                }
            });
        }
    }

    protected void forEachBufferType(int numBuffers, @NonNull IOConsumer<ByteBuf[]> callback) {
        this.forEachBufferType0(numBuffers, callback);
    }

    private void forEachBufferType0(int depth, @NonNull IOConsumer<ByteBuf[]> callback, @NonNull ByteBuf... args) {
        if (depth == 0) {
            for (ByteBuf buf : args) {
                buf.clear();
            }
            callback.accept(args);
        } else {
            Stream.of(true, false).forEach(direct -> {
                ByteBuf[] param = new ByteBuf[args.length + 1];
                System.arraycopy(args, 0, param, 0, args.length);
                param[args.length] = direct ? PooledByteBufAllocator.DEFAULT.directBuffer() : PooledByteBufAllocator.DEFAULT.heapBuffer();
                this.forEachBufferType0(depth - 1, callback, param);
                checkState(param[args.length].release(), "buffer #%s wasn't released... (reference count is still %s)", args.length, param[args.length].refCnt());
            });
        }
    }
}
