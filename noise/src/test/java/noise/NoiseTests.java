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

package noise;

import lombok.experimental.UtilityClass;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.noise.engine.OpenSimplexNoiseEngine;
import net.daporkchop.lib.noise.engine.PerlinNoiseEngine;
import net.daporkchop.lib.noise.engine.PorkianV2NoiseEngine;
import net.daporkchop.lib.noise.engine.SimplexNoiseEngine;
import net.daporkchop.lib.random.impl.FastPRandom;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Contains an array of all {@link NoiseSource}s to test.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class NoiseTests {
    public final NoiseSource[] DEFAULT_SOURCES = {
            new PorkianV2NoiseEngine(new FastPRandom()),
            new PorkianV2NoiseEngine(new FastPRandom()).octaves(8),
            new PerlinNoiseEngine(new FastPRandom()),
            new PerlinNoiseEngine(new FastPRandom()).octaves(8),
            new SimplexNoiseEngine(new FastPRandom()),
            new SimplexNoiseEngine(new FastPRandom()).octaves(8),
            new OpenSimplexNoiseEngine(new FastPRandom()),
            new OpenSimplexNoiseEngine(new FastPRandom()).octaves(8)
    };

    public final NoiseSource[] ALL_SOURCES = Arrays.stream(DEFAULT_SOURCES)
            .flatMap(src -> Stream.of(src, src.weighted()))
            .toArray(NoiseSource[]::new);
}
