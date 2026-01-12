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

package compression.deflate;

import compression.AbstractOneshotCompressionTest;
import compression.deflate.util.DefaultModeDeflateOneshotFactory;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.daporkchop.lib.compression.deflate.DeflateOneshotFactory;
import net.daporkchop.lib.compression.deflate.DeflateOneshotProvider;
import net.daporkchop.lib.compression.deflate.util.DeflateWrapperFormat;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URI;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author DaPorkchop_
 */
@RunWith(Parameterized.class)
public abstract class AbstractDeflateOneshotTest extends AbstractOneshotCompressionTest<DeflateOneshotFactory> {
    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        return Arrays.stream(DeflateWrapperFormat.values())
                .map(format -> new Object[]{ format })
                .collect(Collectors.toList());
    }

    protected final DeflateWrapperFormat format;

    public AbstractDeflateOneshotTest(@NonNull DeflateOneshotProvider provider, @NonNull DeflateWrapperFormat format) {
        super(new DefaultModeDeflateOneshotFactory<>(provider.getOneshotFactory(), format));
        this.format = format;
    }

    @Override
    @SneakyThrows
    public URI getCompressedDataLocation() {
        return this.getClass().getResource("/LICENSE." + this.format.name().toLowerCase(Locale.ROOT)).toURI();
    }

    @Override
    public URI getExpectedDataLocation() {
        return Paths.get("../../LICENSE").toUri();
    }
}
