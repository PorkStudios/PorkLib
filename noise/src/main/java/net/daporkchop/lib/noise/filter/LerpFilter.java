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

package net.daporkchop.lib.noise.filter;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.math.primitive.PMath;
import net.daporkchop.lib.noise.NoiseSource;

import static net.daporkchop.lib.math.primitive.PMath.lerp;

/**
 * Linearly interpolates between the noise values of two different {@link NoiseSource}s using a third {@link NoiseSource} as the bias {@code t}.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public final class LerpFilter implements NoiseSource {
    @Getter
    private final double min;
    @Getter
    private final double max;

    private final NoiseSource a;
    private final NoiseSource b;
    private final NoiseSource t;

    public LerpFilter(@NonNull NoiseSource a, @NonNull NoiseSource b, @NonNull NoiseSource t)   {
        this.a = a;
        this.b = b;
        this.t = t.toRange(0.0d, 1.0d);

        this.min = Math.min(this.a.min(), this.b.min());
        this.max = Math.max(this.a.max(), this.b.max());
    }

    @Override
    public double get(double x) {
        return lerp(this.a.get(x), this.b.get(x), this.t.get(x));
    }

    @Override
    public double get(double x, double y) {
        return lerp(this.a.get(x, y), this.b.get(x, y), this.t.get(x, y));
    }

    @Override
    public double get(double x, double y, double z) {
        return lerp(this.a.get(x, y, z), this.b.get(x, y, z), this.t.get(x, y, z));
    }

    @Override
    public String toString() {
        return String.format("lerp(a=%s,b=%s,t=%s)", this.a, this.b, this.t);
    }
}
