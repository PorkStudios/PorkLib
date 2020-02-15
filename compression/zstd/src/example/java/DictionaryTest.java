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
import io.netty.buffer.PooledByteBufAllocator;
import net.daporkchop.lib.binary.netty.PUnpooled;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.compression.zstd.Zstd;
import net.daporkchop.lib.compression.zstd.ZstdCCtx;
import net.daporkchop.lib.compression.zstd.ZstdCDict;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

/**
 * @author DaPorkchop_
 */
public class DictionaryTest {
    public static final File RAW_DIR        = new File("/home/daporkchop/Desktop/raw");
    public static final File OUT_DIR_NORMAL = new File("/home/daporkchop/Desktop/compressed-normal");
    public static final File OUT_DIR_DICT   = new File("/home/daporkchop/Desktop/compressed-dict");
    public static final File DICT_FILE = new File("/home/daporkchop/Desktop/dict");

    public static void main(String... args) throws IOException {
        PFiles.rm(OUT_DIR_NORMAL);
        PFiles.rm(OUT_DIR_DICT);

        ByteBuf rawDict;
        try (FileChannel channel = FileChannel.open(DICT_FILE.toPath(), StandardOpenOption.READ))   {
            rawDict = PUnpooled.wrap(channel.map(FileChannel.MapMode.READ_ONLY, 0L, channel.size()), true);
        }

        long normalSize = 0L;
        long dictSize = 0L;

        try (ZstdCCtx cCtx = Zstd.PROVIDER.compressionContext();
             ZstdCDict cDict = Zstd.PROVIDER.compressionDictionary(rawDict, Zstd.LEVEL_MAX)) {
            for (File srcFile : RAW_DIR.listFiles()) {
                ByteBuf src;
                try (FileChannel channel = FileChannel.open(srcFile.toPath(), StandardOpenOption.READ)) {
                    src = PUnpooled.wrap(channel.map(FileChannel.MapMode.READ_ONLY, 0L, channel.size()), true);
                }
                ByteBuf compressed = PooledByteBufAllocator.DEFAULT.directBuffer(Zstd.PROVIDER.compressBound(src.readableBytes()), Zstd.PROVIDER.compressBound(src.readableBytes()));
                try {
                    if (!cCtx.compress(src, compressed, Zstd.LEVEL_MAX))    {
                        throw new IllegalStateException();
                    }
                    normalSize += compressed.readableBytes();
                    if (true)   {
                        continue;
                    }
                    try (FileChannel channel = FileChannel.open(PFiles.ensureFileExists(new File(OUT_DIR_NORMAL, srcFile.getName())).toPath(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING))   {
                        compressed.readBytes(channel, compressed.readableBytes());
                    }
                } finally {
                    src.release();
                    compressed.release();
                }
            }

            for (File srcFile : RAW_DIR.listFiles()) {
                ByteBuf src;
                try (FileChannel channel = FileChannel.open(srcFile.toPath(), StandardOpenOption.READ)) {
                    src = PUnpooled.wrap(channel.map(FileChannel.MapMode.READ_ONLY, 0L, channel.size()), true);
                }
                ByteBuf compressed = PooledByteBufAllocator.DEFAULT.directBuffer(Zstd.PROVIDER.compressBound(src.readableBytes()), Zstd.PROVIDER.compressBound(src.readableBytes()));
                try {
                    if (!cCtx.compress(src, compressed, cDict))    {
                        throw new IllegalStateException();
                    }
                    dictSize += compressed.readableBytes();
                    if (true)   {
                        continue;
                    }
                    try (FileChannel channel = FileChannel.open(PFiles.ensureFileExists(new File(OUT_DIR_DICT, srcFile.getName())).toPath(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING))   {
                        compressed.readBytes(channel, compressed.readableBytes());
                    }
                } finally {
                    src.release();
                    compressed.release();
                }
            }
        }

        System.out.printf("Normal: %.2f\nDictionary: %.2f\n", normalSize / (1024.0d * 1024.0d), dictSize / (1024.0d * 1024.0d));
    }
}
