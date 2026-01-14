/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2026 DaPorkchop_
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
 */

package compression;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.daporkchop.lib.common.util.PNioBuffers;
import net.daporkchop.lib.compression.StreamingCompressionFactory;
import net.daporkchop.lib.compression.context.PStreamingCompressor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public abstract class AbstractStreamingCompressionTest<FACTORY extends StreamingCompressionFactory> {
    protected final @NonNull FACTORY factory;

    protected byte[] compressedData;
    protected byte[] expectedData;

    @Before
    public final void loadData() throws IOException {
        this.compressedData = Files.readAllBytes(Paths.get(this.getCompressedDataLocation()));
        this.expectedData = Files.readAllBytes(Paths.get(this.getExpectedDataLocation()));
    }

    public abstract URI getCompressedDataLocation();

    public abstract URI getExpectedDataLocation();

    @Test
    public void testCreateCompressor() {
        this.factory.makeStreamingCompressor().close();
    }

    @Test
    public void testCreateDecompressor() {
        this.factory.makeStreamingDecompressor().close();
    }

    @Test
    public void testCompress_OutputStream() throws IOException {
        byte[] expectedCompressedData;
        try (val compressor = this.factory.makeOneshotCompressor()) {
            val tmpDst = ByteBuffer.allocate(compressor.compressBound(this.expectedData.length));
            expectedCompressedData = PNioBuffers.toArray(tmpDst, 0, compressor.compress(ByteBuffer.wrap(this.expectedData), tmpDst));
        }

        try (val compressor = this.factory.makeStreamingCompressor()) {
            val baos = new ByteArrayOutputStream();

            //compress entire array through output stream
            baos.reset();
            try (val out = compressor.wrapCompressing(baos, PStreamingCompressor.FlushMode.NO)) {
                out.write(this.expectedData);
            }
            Assert.assertArrayEquals(expectedCompressedData, baos.toByteArray());

            //compress entire array one byte at a time
            baos.reset();
            try (val out = compressor.wrapCompressing(baos, PStreamingCompressor.FlushMode.NO)) {
                for (byte b : this.expectedData) {
                    out.write(b);
                }
            }
            Assert.assertArrayEquals(expectedCompressedData, baos.toByteArray());

            //compress half the array at a time
            baos.reset();
            try (val out = compressor.wrapCompressing(baos, PStreamingCompressor.FlushMode.NO)) {
                out.write(this.expectedData, 0, this.expectedData.length / 2);
                out.write(this.expectedData, this.expectedData.length / 2, this.expectedData.length - this.expectedData.length / 2);
            }
            Assert.assertArrayEquals(expectedCompressedData, baos.toByteArray());

            //compress entire array one byte at a time with intermediate ignored flushes
            baos.reset();
            try (val out = compressor.wrapCompressing(baos, PStreamingCompressor.FlushMode.NO)) {
                for (byte b : this.expectedData) {
                    out.write(b);
                    out.flush();
                }
            }
            Assert.assertArrayEquals(expectedCompressedData, baos.toByteArray());

            //compress half the array at a time with an intermediate flush
            baos.reset();
            try (val out = compressor.wrapCompressing(baos, PStreamingCompressor.FlushMode.NO)) {
                out.write(this.expectedData, 0, this.expectedData.length / 2);
                out.flush();
                out.write(this.expectedData, this.expectedData.length / 2, this.expectedData.length - this.expectedData.length / 2);
            }
            Assert.assertArrayEquals(expectedCompressedData, baos.toByteArray());

            //TODO: maybe test other flush modes? although we might need a decompressor to ensure that data is flushed when requested
        }
    }

    //TODO
    /*@Test
    public void testDecompress_NioBuffer() {
        try (val decompressor = this.factory.makeOneshotDecompressor()) {
            CompressionTestUtils.forEachNioBufferTypeInput(this.compressedData, src -> {
                CompressionTestUtils.forEachNioBufferTypeOutput(Math.toIntExact(decompressor.decompressedSizeExact(src).getAsLong()), dst -> {
                    int origSrcPosition = src.position();
                    int origDstPosition = dst.position();

                    int result = decompressor.decompress(src, dst);
                    assert result >= 0 : "decompression result: " + result;

                    Assert.assertEquals(origSrcPosition, src.position());
                    Assert.assertEquals(origDstPosition + result, dst.position());

                    Assert.assertArrayEquals(this.expectedData, PNioBuffers.toArray(dst, origDstPosition, result));
                });
            });
        }
    }

    @Test
    public void testRoundTrip_NioBuffer() throws DataFormatException {
        try (val compressor = this.factory.makeOneshotCompressor();
             val decompressor = this.factory.makeOneshotDecompressor()) {
            val compressed = ByteBuffer.allocate(compressor.compressBound(this.expectedData.length));
            assert compressor.compress(ByteBuffer.wrap(this.expectedData), compressed) >= 0 : "compression failed";
            compressed.flip();

            val decompressed = ByteBuffer.allocate(Math.toIntExact(decompressor.decompressedSizeBound(compressed)));
            assert decompressor.decompress(compressed, decompressed) >= 0 : "decompression failed";
            decompressed.flip();

            Assert.assertEquals(ByteBuffer.wrap(this.expectedData), decompressed);
        }
    }*/

    @Test
    public void testDecompress_OneCall() {
        try (val decompressor = this.factory.makeStreamingDecompressor()) {
            CompressionTestUtils.forEachNioBufferTypeInput(this.compressedData, src -> {
                src.mark();
                CompressionTestUtils.forEachNioBufferTypeOutput(this.expectedData.length, dst -> {
                    src.reset();

                    int origDstPosition = dst.position();

                    Assert.assertTrue(decompressor.decompress(src, dst, true));

                    Assert.assertEquals(src.limit(), src.position());
                    Assert.assertEquals(origDstPosition + this.expectedData.length, dst.position());

                    Assert.assertArrayEquals(this.expectedData, PNioBuffers.toArray(dst, origDstPosition, dst.position() - origDstPosition));
                });
            });
        }
    }
}
