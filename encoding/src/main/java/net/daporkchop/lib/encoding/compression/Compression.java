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

package net.daporkchop.lib.encoding.compression;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream;
import org.apache.commons.compress.compressors.deflate.DeflateParameters;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipParameters;
import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorInputStream;
import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorOutputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.LZMAInputStream;
import org.tukaani.xz.LZMAOutputStream;
import org.tukaani.xz.XZInputStream;
import org.tukaani.xz.XZOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;

/**
 * Contains static references to a number of standard compression algorithms.
 *
 * @author DaPorkchop_
 */
//TODO: strip some dependencies off of this
@UtilityClass
public class Compression {
    public static final CompressionHelper NONE = CompressionHelper.builder("Uncompressed")
            .setInputStreamWrapperSimple(in -> in)
            .setOutputStreamWrapperSimple(out -> out)
            .build();

    public static final CompressionHelper GZIP_LOW = CompressionHelper.<GzipParameters>builder("GZip", "Low")
            .setParams(new GzipParameters() {
                {
                    this.setCompressionLevel(0);
                }
            })
            .setInputStreamWrapperSimple(GzipCompressorInputStream::new)
            .setOutputStreamWrapper(GzipCompressorOutputStream::new)
            .build();

    public static final CompressionHelper GZIP_NORMAL = CompressionHelper.<GzipParameters>builder("GZip", "Normal")
            .setParams(new GzipParameters() {
                {
                    this.setCompressionLevel(4);
                }
            })
            .setInputStreamWrapperSimple(GzipCompressorInputStream::new)
            .setOutputStreamWrapper(GzipCompressorOutputStream::new)
            .build();

    public static final CompressionHelper GZIP_HIGH = CompressionHelper.<GzipParameters>builder("GZip", "High")
            .setParams(new GzipParameters() {
                {
                    this.setCompressionLevel(8);
                }
            })
            .setInputStreamWrapperSimple(GzipCompressorInputStream::new)
            .setOutputStreamWrapper(GzipCompressorOutputStream::new)
            .build();

    public static final CompressionHelper BZIP2_LOW = CompressionHelper.<Integer>builder("BZip2", "Low")
            .setParams(1)
            .setInputStreamWrapperSimple(BZip2CompressorInputStream::new)
            .setOutputStreamWrapper(BZip2CompressorOutputStream::new)
            .build();

    public static final CompressionHelper BZIP2_NORMAL = CompressionHelper.<Integer>builder("BZip2", "Normal")
            .setParams(1)
            .setInputStreamWrapperSimple(BZip2CompressorInputStream::new)
            .setOutputStreamWrapper(BZip2CompressorOutputStream::new)
            .build();

    public static final CompressionHelper BZIP2_HIGH = CompressionHelper.<Integer>builder("BZip2", "High")
            .setParams(1)
            .setInputStreamWrapperSimple(BZip2CompressorInputStream::new)
            .setOutputStreamWrapper(BZip2CompressorOutputStream::new)
            .build();

    public static final CompressionHelper DEFLATE_LOW = CompressionHelper.<DeflateParameters>builder("Deflate", "Low")
            .setParams(new DeflateParameters() {
                {
                    this.setCompressionLevel(0);
                }
            })
            .setInputStreamWrapper(DeflateCompressorInputStream::new)
            .setOutputStreamWrapper(DeflateCompressorOutputStream::new)
            .build();

    public static final CompressionHelper DEFLATE_NORMAL = CompressionHelper.<DeflateParameters>builder("Deflate", "Normal")
            .setParams(new DeflateParameters() {
                {
                    this.setCompressionLevel(4);
                }
            })
            .setInputStreamWrapper(DeflateCompressorInputStream::new)
            .setOutputStreamWrapper(DeflateCompressorOutputStream::new)
            .build();

    public static final CompressionHelper DEFLATE_HIGH = CompressionHelper.<DeflateParameters>builder("Deflate", "High")
            .setParams(new DeflateParameters() {
                {
                    this.setCompressionLevel(8);
                }
            })
            .setInputStreamWrapper(DeflateCompressorInputStream::new)
            .setOutputStreamWrapper(DeflateCompressorOutputStream::new)
            .build();

    public static final CompressionHelper LZ4_BLOCK = CompressionHelper.builder("LZ4", "Block")
            .setInputStreamWrapperSimple(BlockLZ4CompressorInputStream::new)
            .setOutputStreamWrapperSimple(BlockLZ4CompressorOutputStream::new)
            .build();

    public static final CompressionHelper LZ4_FRAMED_64KB = CompressionHelper.<FramedLZ4CompressorOutputStream.Parameters>builder("LZ4", "Framed - 64KB")
            .setParams(new FramedLZ4CompressorOutputStream.Parameters(FramedLZ4CompressorOutputStream.BlockSize.K64))
            .setInputStreamWrapperSimple(BlockLZ4CompressorInputStream::new)
            .setOutputStreamWrapperSimple(BlockLZ4CompressorOutputStream::new)
            .build();

    public static final CompressionHelper LZ4_FRAMED_256KB = CompressionHelper.<FramedLZ4CompressorOutputStream.Parameters>builder("LZ4", "Framed - 256KB")
            .setParams(new FramedLZ4CompressorOutputStream.Parameters(FramedLZ4CompressorOutputStream.BlockSize.K256))
            .setInputStreamWrapperSimple(BlockLZ4CompressorInputStream::new)
            .setOutputStreamWrapperSimple(BlockLZ4CompressorOutputStream::new)
            .build();

    public static final CompressionHelper LZ4_FRAMED_1MB = CompressionHelper.<FramedLZ4CompressorOutputStream.Parameters>builder("LZ4", "Framed - 1MB")
            .setParams(new FramedLZ4CompressorOutputStream.Parameters(FramedLZ4CompressorOutputStream.BlockSize.M1))
            .setInputStreamWrapperSimple(BlockLZ4CompressorInputStream::new)
            .setOutputStreamWrapperSimple(BlockLZ4CompressorOutputStream::new)
            .build();

    public static final CompressionHelper LZ4_FRAMED_4MB = CompressionHelper.<FramedLZ4CompressorOutputStream.Parameters>builder("LZ4", "Framed - 4MB")
            .setParams(new FramedLZ4CompressorOutputStream.Parameters(FramedLZ4CompressorOutputStream.BlockSize.M4))
            .setInputStreamWrapperSimple(BlockLZ4CompressorInputStream::new)
            .setOutputStreamWrapperSimple(BlockLZ4CompressorOutputStream::new)
            .build();

    /*public static final CompressionHelper LZMA2_LOW = CompressionHelper.<LZMA2Options>builder("LZMA2", "Low")
            .setParamsFunc(() -> new LZMA2Options(0))
            .setInputStreamWrapperSimple(in -> new LZMA2InputStream(in, -1))
            .setOutputStreamWrapper(LZMA2OutputStream::new)
            .build();

    public static final CompressionHelper LZMA2_NORMAL = CompressionHelper.<LZMA2Options>builder("LZMA2", "Normal")
            .setParamsFunc(() -> new LZMA2Options(0))
            .setInputStreamWrapperSimple(BlockLZ4CompressorInputStream::new)
            .setOutputStreamWrapperSimple(BlockLZ4CompressorOutputStream::new)
            .build();

    public static final CompressionHelper LZMA2_HIGH = CompressionHelper.<LZMA2Options>builder("LZMA2", "High")
            .setParamsFunc(() -> new LZMA2Options(0))
            .setInputStreamWrapperSimple(BlockLZ4CompressorInputStream::new)
            .setOutputStreamWrapperSimple(BlockLZ4CompressorOutputStream::new)
            .build();*/

    public static final CompressionHelper LZMA_LOW = CompressionHelper.<LZMA2Options>builder("LZMA", "Low")
            .setParamsFunc(() -> new LZMA2Options(0))
            .setInputStreamWrapperSimple(LZMAInputStream::new)
            .setOutputStreamWrapper((out, params) -> new LZMAWrapperOut(new LZMAOutputStream(out, params, -1L)))
            .build();

    public static final CompressionHelper LZMA_NORMAL = CompressionHelper.<LZMA2Options>builder("LZMA", "Normal")
            .setParamsFunc(() -> new LZMA2Options(6))
            .setInputStreamWrapperSimple(LZMAInputStream::new)
            .setOutputStreamWrapper((out, params) -> new LZMAWrapperOut(new LZMAOutputStream(out, params, -1L)))
            .build();

    public static final CompressionHelper LZMA_HIGH = CompressionHelper.<LZMA2Options>builder("LZMA", "High")
            .setParamsFunc(() -> new LZMA2Options(9))
            .setInputStreamWrapperSimple(LZMAInputStream::new)
            .setOutputStreamWrapper((out, params) -> new LZMAWrapperOut(new LZMAOutputStream(out, params, -1L)))
            .build();

    public static final CompressionHelper XZ_LOW = CompressionHelper.<LZMA2Options>builder("XZ", "Low")
            .setParamsFunc(() -> new LZMA2Options(0))
            .setInputStreamWrapperSimple(XZInputStream::new)
            .setOutputStreamWrapper(XZOutputStream::new)
            .build();

    public static final CompressionHelper XZ_NORMAL = CompressionHelper.<LZMA2Options>builder("XZ", "Normal")
            .setParamsFunc(() -> new LZMA2Options(6))
            .setInputStreamWrapperSimple(XZInputStream::new)
            .setOutputStreamWrapper(XZOutputStream::new)
            .build();

    public static final CompressionHelper XZ_HIGH = CompressionHelper.<LZMA2Options>builder("XZ", "High")
            .setParamsFunc(() -> new LZMA2Options(9))
            .setInputStreamWrapperSimple(XZInputStream::new)
            .setOutputStreamWrapper(XZOutputStream::new)
            .build();

    static {
        try {
            for (Field field : Compression.class.getDeclaredFields()) {
                if (field.getType() == CompressionHelper.class) {
                    //System.out.printf("Found compression algorithm: %s\n", field.getName());
                    CompressionHelper.registerCompressionType(field.getName(), (CompressionHelper) field.get(null));
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @RequiredArgsConstructor
    private static class LZMAWrapperOut extends OutputStream {
        @NonNull
        private final LZMAOutputStream out;

        @Override
        public void write(int b) throws IOException {
            this.out.write(b);
        }

        /*@Override
        public void write(byte[] b) throws IOException {
            this.out.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            this.out.write(b, off, len);
        }*/

        @Override
        public void close() throws IOException {
            this.out.close();
        }
    }
}
