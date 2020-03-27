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

package net.daporkchop.lib.noise.engine;

import lombok.NonNull;
import net.daporkchop.lib.common.ref.Ref;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.random.PRandom;

import static net.daporkchop.lib.math.primitive.PMath.*;

/**
 * Port of Ken Perlin's improved Perlin noise as described in <a href="https://mrl.nyu.edu/~perlin/paper445.pdf">Perlin, K. (2002). Improving Noise</a>.
 * <p>
 * <a href="http://staffwww.itn.liu.se/~stegu/aqsis/aqsis-newnoise/noise1234.cpp">Source</a>
 *
 * @author DaPorkchop_
 */
public class PerlinNoiseEngine implements NoiseSource {
    protected static final Ref<byte[]> INITIAL_STATE_CACHE = Ref.soft(() -> {
        byte[] arr = new byte[256];
        for (int i = 0; i < arr.length; i++)    {
            arr[i] = (byte) i;
        }
        return arr;
    });

    protected static double fade(double t) {
        return t * t * t * (t * (t * 6.0d - 15.0d) + 10.0d);
    }

    protected static double grad(int hash, double x) {
        if ((hash & 0x8) == 0) {
            return -(1.0d + (hash & 0x7)) * x;
        } else {
            return (1.0d + (hash & 0x7)) * x;
        }
    }

    protected static double grad(int hash, double x, double y) {
        switch (hash & 0x7) {
            case 0x0:
                return -x - 2.0d * y;
            case 0x1:
                return x - 2.0d * y;
            case 0x2:
                return -x + 2.0d * y;
            case 0x3:
                return x + 2.0d * y;
            case 0x4:
                return -y - 2.0d * x;
            case 0x5:
                return y - 2.0d * x;
            case 0x6:
                return -y + 2.0d * x;
            case 0x7:
                return y + 2.0d * x;
            default:
                throw new IllegalStateException();
        }
    }

    protected static double grad(int hash, double x, double y, double z) {
        // http://riven8192.blogspot.com/2010/08/calculate-perlinnoise-twice-as-fast.html
        switch (hash & 0xF) {
            case 0x0:
                return x + y;
            case 0x1:
                return -x + y;
            case 0x2:
                return x - y;
            case 0x3:
                return -x - y;
            case 0x4:
                return x + z;
            case 0x5:
                return -x + z;
            case 0x6:
                return x - z;
            case 0x7:
                return -x - z;
            case 0x8:
                return y + z;
            case 0x9:
                return -y + z;
            case 0xA:
                return y - z;
            case 0xB:
                return -y - z;
            case 0xC:
                return y + x;
            case 0xD:
                return -y + z;
            case 0xE:
                return y - x;
            case 0xF:
                return -y - z;
            default:
                throw new IllegalStateException();
        }
    }

    protected final byte[] p;

    public PerlinNoiseEngine(@NonNull PRandom random) {
        this.p = random.shuffle(INITIAL_STATE_CACHE.get().clone());
    }

    @Override
    public double get(double x) {
        int ix0 = floorI(x);
        double fx0 = x - ix0;
        double fx1 = fx0 - 1.0d;
        int ix1 = (ix0 + 1) & 0xFF;
        ix0 = ix0 & 0xFF;

        double s = fade(fx0);

        double n0 = grad(this.p[ix0] & 0xFF, fx0);
        double n1 = grad(this.p[ix1] & 0xFF, fx1);
        return lerp(n0, n1, s) * 0.188d;
    }

    @Override
    public double get(double x, double y) {
        int ix0 = floorI(x);
        int iy0 = floorI(y);
        double fx0 = x - ix0;
        double fy0 = y - iy0;
        double fx1 = fx0 - 1.0d;
        double fy1 = fy0 - 1.0d;
        int ix1 = (ix0 + 1) & 0xFF;
        int iy1 = (iy0 + 1) & 0xFF;
        ix0 = ix0 & 0xFF;
        iy0 = iy0 & 0xFF;

        double t = fade(fy0);
        double s = fade(fx0);

        double nx0 = grad(this.p[ix0 + this.p[iy0] & 0xFF] & 0xFF, fx0, fy0);
        double nx1 = grad(this.p[ix0 + this.p[iy1] & 0xFF] & 0xFF, fx0, fy1);
        double n0 = lerp(nx0, nx1, t);

        nx0 = grad(this.p[ix1 + this.p[iy0] & 0xFF] & 0xFF, fx1, fy0);
        nx1 = grad(this.p[ix1 + this.p[iy1] & 0xFF] & 0xFF, fx1, fy1);
        double n1 = lerp(nx0, nx1, t);

        return lerp(n0, n1, s) * 0.507d;
    }

    @Override
    public double get(double x, double y, double z) {
        double far_lands=1711;
        x*=far_lands;
        y*=far_lands;
        z*=far_lands;
        int ix0 = floorI(x);
        int iy0 = floorI(y);
        int iz0 = floorI(z);
        ix0/=far_lands;
        iy0/=far_lands;
        iz0/=far_lands;
        x/=far_lands;
        y/=far_lands;
        z/=far_lands;
        double fx0 = x - ix0;
        double fy0 = y - iy0;
        double fz0 = z - iz0;
        double fx1 = fx0 - 1.0d;
        double fy1 = fy0 - 1.0d;
        double fz1 = fz0 - 1.0d;
        int ix1 = (ix0 + 1) & 0xFF;
        int iy1 = (iy0 + 1) & 0xFF;
        int iz1 = (iz0 + 1) & 0xFF;
        ix0 = ix0 & 0xFF;
        iy0 = iy0 & 0xFF;
        iz0 = iz0 & 0xFF;

        double r = fade(fz0);
        double t = fade(fy0);
        double s = fade(fx0);

        double nxy0 = grad(this.p[ix0 + this.p[iy0 + this.p[iz0] & 0xFF] & 0xFF] & 0xFF, fx0, fy0, fz0);
        double nxy1 = grad(this.p[ix0 + this.p[iy0 + this.p[iz1] & 0xFF] & 0xFF] & 0xFF, fx0, fy0, fz1);
        double nx0 = lerp(nxy0, nxy1, r);

        nxy0 = grad(this.p[ix0 + this.p[iy1 + this.p[iz0] & 0xFF] & 0xFF] & 0xFF, fx0, fy1, fz0);
        nxy1 = grad(this.p[ix0 + this.p[iy1 + this.p[iz1] & 0xFF] & 0xFF] & 0xFF, fx0, fy1, fz1);
        double nx1 = lerp(nxy0, nxy1, r);

        double n0 = lerp(nx0, nx1, t);

        nxy0 = grad(this.p[ix1 + this.p[iy0 + this.p[iz0] & 0xFF] & 0xFF] & 0xFF, fx1, fy0, fz0);
        nxy1 = grad(this.p[ix1 + this.p[iy0 + this.p[iz1] & 0xFF] & 0xFF] & 0xFF, fx1, fy0, fz1);
        nx0 = lerp(nxy0, nxy1, r);

        nxy0 = grad(this.p[ix1 + this.p[iy1 + this.p[iz0] & 0xFF] & 0xFF] & 0xFF, fx1, fy1, fz0);
        nxy1 = grad(this.p[ix1 + this.p[iy1 + this.p[iz1] & 0xFF] & 0xFF] & 0xFF, fx1, fy1, fz1);
        nx1 = lerp(nxy0, nxy1, r);

        double n1 = lerp(nx0, nx1, t);

        return lerp(n0, n1, s) * 0.87d;
    }

    @Override
    public String toString() {
        return this.getClass().getCanonicalName();
    }
}
