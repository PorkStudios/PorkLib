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

package net.daporkchop.lib.noise.engine;

import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

import static net.daporkchop.lib.math.primitive.PMath.*;

/**
 * Port of Ken Perlin's Simplex noise algorithm.
 * <p>
 * <a href="http://staffwww.itn.liu.se/~stegu/aqsis/aqsis-newnoise/simplexnoise1234.cpp">Source</a>
 *
 * @author DaPorkchop_
 */
public class SimplexNoiseEngine extends PerlinNoiseEngine {
    protected static final double STRETCH_CONSTANT_2D = 0.211324865405187d;
    protected static final double SQUISH_CONSTANT_2D  = 0.366025403784439d;
    protected static final double STRETCH_CONSTANT_3D = 1.0d / 6.0d;
    protected static final double SQUISH_CONSTANT_3D  = 1.0d / 3.0d;

    public SimplexNoiseEngine(@NonNull PRandom random) {
        super(random);
    }

    @Override
    public double get(double x) {
        int i0 = floorI(x);
        int i1 = i0 + 1;
        double x0 = x - i0;
        double x1 = x0 - 1.0d;

        double t0 = 1.0d - x0 * x0;
        t0 *= t0;
        double n0 = t0 * t0 * grad(this.p[i0 & 0xFF] & 0xFF, x0);

        double t1 = 1.0f - x1 * x1;
        t1 *= t1;
        double n1 = t1 * t1 * grad(this.p[i1 & 0xFF] & 0xFF, x1);
        return (n0 + n1) * 0.395d;
    }

    @Override
    public double get(double x, double y) {
        double s = (x + y) * SQUISH_CONSTANT_2D;
        double xs = x + s;
        double ys = y + s;
        int i = floorI(xs);
        int j = floorI(ys);

        double t = (i + j) * STRETCH_CONSTANT_2D;
        double X0 = i - t;
        double Y0 = j - t;
        double x0 = x - X0;
        double y0 = y - Y0;

        int i1 = x0 > y0 ? 1 : 0;
        int j1 = x0 > y0 ? 0 : 1;

        double x1 = x0 - i1 + STRETCH_CONSTANT_2D;
        double y1 = y0 - j1 + STRETCH_CONSTANT_2D;
        double x2 = x0 - 1.0d + 2.0d * STRETCH_CONSTANT_2D;
        double y2 = y0 - 1.0d + 2.0d * STRETCH_CONSTANT_2D;

        double n0 = 0.0d;
        double t0 = 0.5d - x0 * x0 - y0 * y0;
        if (t0 >= 0.0d) {
            t0 *= t0;
            n0 = t0 * t0 * grad(this.p[i + this.p[j & 0xFF] & 0xFF] & 0xFF, x0, y0);
        }

        double n1 = 0.0d;
        double t1 = 0.5d - x1 * x1 - y1 * y1;
        if (t1 >= 0.0d) {
            t1 *= t1;
            n1 = t1 * t1 * grad(this.p[i + i1 + this.p[j + j1 & 0xFF] & 0xFF] & 0xFF, x1, y1);
        }

        double n2 = 0.0d;
        double t2 = 0.5d - x2 * x2 - y2 * y2;
        if (t2 >= 0.0d) {
            t2 *= t2;
            n2 = t2 * t2 * grad(this.p[i + 1 + this.p[j + 1 & 0xFF] & 0xFF], x2, y2);
        }

        return (n0 + n1 + n2) * 40.0d;
    }

    @Override
    public double get(double x, double y, double z) {
        double s = (x + y + z) * SQUISH_CONSTANT_3D;
        double xs = x + s;
        double ys = y + s;
        double zs = z + s;
        int i = floorI(xs);
        int j = floorI(ys);
        int k = floorI(zs);

        double t = (i + j + k) * STRETCH_CONSTANT_3D;
        double X0 = i - t;
        double Y0 = j - t;
        double Z0 = k - t;
        double x0 = x - X0;
        double y0 = y - Y0;
        double z0 = z - Z0;

        int i1;
        int j1;
        int k1;
        int i2;
        int j2;
        int k2;

        if (x0 >= y0) {
            if (y0 >= z0) {
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            } else if (x0 >= z0) {
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            } else {
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            }
        } else {
            if (y0 < z0) {
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 0;
                j2 = 1;
                k2 = 1;
            } else if (x0 < z0) {
                i1 = 0;
                j1 = 1;
                k1 = 0;
                i2 = 0;
                j2 = 1;
                k2 = 1;
            } else {
                i1 = 0;
                j1 = 1;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            }
        }

        double x1 = x0 - i1 + STRETCH_CONSTANT_3D;
        double y1 = y0 - j1 + STRETCH_CONSTANT_3D;
        double z1 = z0 - k1 + STRETCH_CONSTANT_3D;
        double x2 = x0 - i2 + 2.0d * STRETCH_CONSTANT_3D;
        double y2 = y0 - j2 + 2.0d * STRETCH_CONSTANT_3D;
        double z2 = z0 - k2 + 2.0d * STRETCH_CONSTANT_3D;
        double x3 = x0 - 1.0d + 3.0d * STRETCH_CONSTANT_3D;
        double y3 = y0 - 1.0d + 3.0d * STRETCH_CONSTANT_3D;
        double z3 = z0 - 1.0d + 3.0d * STRETCH_CONSTANT_3D;

        double n0 = 0.0d;
        double t0 = 0.6d - x0 * x0 - y0 * y0 - z0 * z0;
        if (t0 >= 0.0d) {
            t0 *= t0;
            n0 = t0 * t0 * grad(this.p[i + this.p[j + this.p[k & 0xFF] & 0xFF] & 0xFF] & 0xFF, x0, y0, z0);
        }

        double n1 = 0.0d;
        double t1 = 0.6d - x1 * x1 - y1 * y1 - z1 * z1;
        if (t1 >= 0.0d) {
            t1 *= t1;
            n1 = t1 * t1 * grad(this.p[i + i1 + this.p[j + j1 + this.p[k + k1 & 0xFF] & 0xFF] & 0xFF] & 0xFF, x1, y1, z1);
        }

        double n2 = 0.0d;
        double t2 = 0.6d - x2 * x2 - y2 * y2 - z2 * z2;
        if (t2 >= 0.0d) {
            t2 *= t2;
            n2 = t2 * t2 * grad(this.p[i + i2 + this.p[j + j2 + this.p[k + k2 & 0xFF] & 0xFF] & 0xFF] & 0xFF, x2, y2, z2);
        }

        double n3 = 0.0d;
        double t3 = 0.6d - x3 * x3 - y3 * y3 - z3 * z3;
        if (t3 >= 0.0d) {
            t3 *= t3;
            n3 = t3 * t3 * grad(this.p[i + 1 + this.p[j + 1 + this.p[k + 1 & 0xFF] & 0xFF] & 0xFF], x3, y3, z3);
        }

        return (n0 + n1 + n2 + n3) * 32.0d;
    }
}
