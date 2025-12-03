/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2025 DaPorkchop_
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

package compression.zstd;

import compression.AbstractOneshotCompressionTest;
import compression.CompressionTestUtils;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.daporkchop.lib.compression.zstd.Zstd;
import net.daporkchop.lib.compression.zstd.ZstdOneshotFactory;
import net.daporkchop.lib.compression.zstd.ZstdOneshotProvider;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author DaPorkchop_
 */
public abstract class AbstractZstdOneshotTest extends AbstractOneshotCompressionTest<ZstdOneshotFactory> {
    protected byte[] dictionaryData;

    public AbstractZstdOneshotTest(@NonNull ZstdOneshotProvider provider) {
        super(provider.getOneshotFactory());
    }

    @Before
    public final void loadDictionary() throws IOException {
        this.dictionaryData = Files.readAllBytes(Paths.get(this.getDictionaryLocation()));
    }

    @SneakyThrows
    public URI getDictionaryLocation() {
        return this.getClass().getResource("/dictionary").toURI();
    }

    @Override
    @SneakyThrows
    public URI getCompressedDataLocation() {
        return this.getClass().getResource("/LICENSE.zst").toURI();
    }

    @Override
    public URI getExpectedDataLocation() {
        return Paths.get("../../LICENSE").toUri();
    }

    @Test
    public void testCreateCompressionDictionary_NioBuffer() {
        Assume.assumeTrue(this.factory.capabilities().supportsDictionary());

        CompressionTestUtils.forEachNioBufferTypeInput(this.dictionaryData, dictBuffer -> this.factory.makeCompressionDictionary(dictBuffer, Zstd.LEVEL_DEFAULT).close());
    }

    @Test
    public void testCreateCompressionDictionary_Netty4Buffer() {
        Assume.assumeTrue(this.factory.capabilities().supportsDictionary());

        CompressionTestUtils.forEachNetty4BufferTypeInput(this.dictionaryData, dictBuffer -> this.factory.makeCompressionDictionary(dictBuffer, Zstd.LEVEL_DEFAULT).close());
    }

    @Test
    public void testCreateDecompressionDictionary_NioBuffer() {
        Assume.assumeTrue(this.factory.capabilities().supportsDictionary());

        CompressionTestUtils.forEachNioBufferTypeInput(this.dictionaryData, dictBuffer -> this.factory.makeDecompressionDictionary(dictBuffer).close());
    }

    @Test
    public void testCreateDecompressionDictionary_Netty4Buffer() {
        Assume.assumeTrue(this.factory.capabilities().supportsDictionary());

        CompressionTestUtils.forEachNetty4BufferTypeInput(this.dictionaryData, dictBuffer -> this.factory.makeDecompressionDictionary(dictBuffer).close());
    }
}
