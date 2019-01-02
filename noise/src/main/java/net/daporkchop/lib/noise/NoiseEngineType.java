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

package net.daporkchop.lib.noise;

import lombok.NonNull;
import net.daporkchop.lib.noise.engine.CheckerboardEngine;
import net.daporkchop.lib.noise.engine.INoiseEngine;
import net.daporkchop.lib.noise.engine.OpenSimplexEngine;
import net.daporkchop.lib.noise.engine.PerlinEngine;
import net.daporkchop.lib.noise.engine.PorkianEngine;
import net.daporkchop.lib.noise.engine.SimplexEngine;

/**
 * A list of all supported noise algorithms
 *
 * @author DaPorkchop_
 */
public enum NoiseEngineType {
    /**
     * Alternating values between -1 and 1
     */
    CHECKERBOARD(CheckerboardEngine::new),
    /**
     * An somewhat faster variant of Ken Perlin's Simplex noise
     */
    OPENSIMPLEX(OpenSimplexEngine::new),
    /**
     * Ken Perlin's original noise algorithm
     */
    PERLIN(PerlinEngine::new),
    /**
     * My own custom algorithm :D
     * <p>
     * Glorified value noise (very fast, not very natural-looking)
     */
    PORKIAN(PorkianEngine::new),
    /**
     * Ken Perlin's newest noise algorithm, much fewer artifacts than his original Perlin noise
     */
    SIMPLEX(SimplexEngine::new);

    private final LongEngineSupplier longSupplier;

    NoiseEngineType(@NonNull LongEngineSupplier longSupplier) {
        this.longSupplier = longSupplier;
    }

    public INoiseEngine getEngine(long seed) {
        return this.longSupplier.get(seed);
    }

    private interface LongEngineSupplier {
        INoiseEngine get(long seed);
    }
}
