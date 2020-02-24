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
