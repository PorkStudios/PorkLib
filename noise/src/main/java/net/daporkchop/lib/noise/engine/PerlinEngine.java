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

package net.daporkchop.lib.noise.engine;

import static net.daporkchop.lib.math.primitive.Floor.floorI;

/**
 * Implements the Perlin noise algorithm
 * <p>
 * Based on https://github.com/nvpro-samples/shared_sources/blob/master/noise/noise1234.cpp
 *
 * @author DaPorkchop_
 */
public class PerlinEngine extends BasicSeedEngine {
    private static double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    private static double fade(double t) {
        return t * t * t * (t * (t * 6.0d - 15.0d) + 10.0d);
    }

    private static double grad(int hash, double x) {
        int h = hash & 15;
        double grad = 1.0f + (h & 7);  // Gradient value 1.0, 2.0, ..., 8.0
        if ((h & 8) == 0) grad = -grad;         // and a random sign for the gradient
        return (grad * x);           // Multiply the gradient with the distance
    }

    private static double grad(int hash, double x, double y) {
        int h = hash & 7;      // Convert low 3 bits of hash code
        double u = h < 4 ? x : y;  // into 8 simple gradient directions,
        double v = h < 4 ? y : x;  // and compute the dot product with (x,y).
        return ((h & 1) == 0 ? -u : u) + ((h & 2) == 0 ? -2.0f * v : 2.0f * v);
    }

    private static double grad(int hash, double x, double y, double z) {
        int h = hash & 15;     // Convert low 4 bits of hash code into 12 simple
        double u = h < 8 ? x : y; // gradient directions, and compute dot product.
        double v = h < 4 ? y : h == 12 || h == 14 ? x : z; // Fix repeats at h = 12 to 15
        return ((h & 1) == 0 ? -u : u) + ((h & 2) == 0 ? -v : v);
    }

    private static double grad(int hash, double x, double y, double z, double t) {
        int h = hash & 31;      // Convert low 5 bits of hash code into 32 simple
        double u = h < 24 ? x : y; // gradient directions, and compute dot product.
        double v = h < 16 ? y : z;
        double w = h < 8 ? z : t;
        return ((h & 1) == 0 ? -u : u) + ((h & 2) == 0 ? -v : v) + ((h & 4) == 0 ? -w : w);
    }

    public PerlinEngine(long seed) {
        super(seed);
    }

    @Override
    public double get(double x) {
        int ix0 = floorI(x); // Integer part of x
        double fx0 = x - ix0;       // Fractional part of x
        double fx1 = fx0 - 1.0f;
        int ix1 = (ix0 + 1) & 0xff;
        ix0 = ix0 & 0xff;    // Wrap to 0..255

        double s = PerlinEngine.fade(fx0);

        double n0 = PerlinEngine.grad(this.p[ix0], fx0);
        double n1 = PerlinEngine.grad(this.p[ix1], fx1);
        return PerlinEngine.lerp(s, n0, n1) * 0.25d;
    }

    @Override
    public double get(double x, double y) {
        int ix0 = floorI(x); // Integer part of x
        int iy0 = floorI(y); // Integer part of y
        double fx0 = x - ix0;        // Fractional part of x
        double fy0 = y - iy0;        // Fractional part of y
        double fx1 = fx0 - 1.0f;
        double fy1 = fy0 - 1.0f;
        int ix1 = (ix0 + 1) & 0xff;  // Wrap to 0..255
        int iy1 = (iy0 + 1) & 0xff;
        ix0 = ix0 & 0xff;
        iy0 = iy0 & 0xff;

        double t = PerlinEngine.fade(fy0);
        double s = PerlinEngine.fade(fx0);

        double nx0 = PerlinEngine.grad(this.p[ix0 + this.p[iy0]], fx0, fy0);
        double nx1 = PerlinEngine.grad(this.p[ix0 + this.p[iy1]], fx0, fy1);
        double n0 = PerlinEngine.lerp(t, nx0, nx1);

        nx0 = PerlinEngine.grad(this.p[ix1 + this.p[iy0]], fx1, fy0);
        nx1 = PerlinEngine.grad(this.p[ix1 + this.p[iy1]], fx1, fy1);
        double n1 = PerlinEngine.lerp(t, nx0, nx1);

        return PerlinEngine.lerp(s, n0, n1) * 0.661703888d;
    }

    @Override
    public double get(double x, double y, double z) {
        int ix0 = floorI(x); // Integer part of x
        int iy0 = floorI(y); // Integer part of y
        int iz0 = floorI(z); // Integer part of z
        double fx0 = x - ix0;        // Fractional part of x
        double fy0 = y - iy0;        // Fractional part of y
        double fz0 = z - iz0;        // Fractional part of z
        double fx1 = fx0 - 1.0f;
        double fy1 = fy0 - 1.0f;
        double fz1 = fz0 - 1.0f;
        int ix1 = (ix0 + 1) & 0xff; // Wrap to 0..255
        int iy1 = (iy0 + 1) & 0xff;
        int iz1 = (iz0 + 1) & 0xff;
        ix0 = ix0 & 0xff;
        iy0 = iy0 & 0xff;
        iz0 = iz0 & 0xff;

        double r = PerlinEngine.fade(fz0);
        double t = PerlinEngine.fade(fy0);
        double s = PerlinEngine.fade(fx0);

        double nxy0 = PerlinEngine.grad(this.p[ix0 + this.p[iy0 + this.p[iz0]]], fx0, fy0, fz0);
        double nxy1 = PerlinEngine.grad(this.p[ix0 + this.p[iy0 + this.p[iz1]]], fx0, fy0, fz1);
        double nx0 = PerlinEngine.lerp(r, nxy0, nxy1);

        nxy0 = PerlinEngine.grad(this.p[ix0 + this.p[iy1 + this.p[iz0]]], fx0, fy1, fz0);
        nxy1 = PerlinEngine.grad(this.p[ix0 + this.p[iy1 + this.p[iz1]]], fx0, fy1, fz1);
        double nx1 = PerlinEngine.lerp(r, nxy0, nxy1);

        double n0 = PerlinEngine.lerp(t, nx0, nx1);

        nxy0 = PerlinEngine.grad(this.p[ix1 + this.p[iy0 + this.p[iz0]]], fx1, fy0, fz0);
        nxy1 = PerlinEngine.grad(this.p[ix1 + this.p[iy0 + this.p[iz1]]], fx1, fy0, fz1);
        nx0 = PerlinEngine.lerp(r, nxy0, nxy1);

        nxy0 = PerlinEngine.grad(this.p[ix1 + this.p[iy1 + this.p[iz0]]], fx1, fy1, fz0);
        nxy1 = PerlinEngine.grad(this.p[ix1 + this.p[iy1 + this.p[iz1]]], fx1, fy1, fz1);
        nx1 = PerlinEngine.lerp(r, nxy0, nxy1);

        double n1 = PerlinEngine.lerp(t, nx0, nx1);

        return PerlinEngine.lerp(s, n0, n1) * 0.99009901d;
    }

    @Override
    public double get(double x, double y, double z, double w) {
        int ix0 = floorI(x); // Integer part of x
        int iy0 = floorI(y); // Integer part of y
        int iz0 = floorI(z); // Integer part of y
        int iw0 = floorI(w); // Integer part of w
        double fx0 = x - ix0;        // Fractional part of x
        double fy0 = y - iy0;        // Fractional part of y
        double fz0 = z - iz0;        // Fractional part of z
        double fw0 = w - iw0;        // Fractional part of w
        double fx1 = fx0 - 1.0f;
        double fy1 = fy0 - 1.0f;
        double fz1 = fz0 - 1.0f;
        double fw1 = fw0 - 1.0f;
        int ix1 = (ix0 + 1) & 0xff;  // Wrap to 0..255
        int iy1 = (iy0 + 1) & 0xff;
        int iz1 = (iz0 + 1) & 0xff;
        int iw1 = (iw0 + 1) & 0xff;
        ix0 = ix0 & 0xff;
        iy0 = iy0 & 0xff;
        iz0 = iz0 & 0xff;
        iw0 = iw0 & 0xff;

        double q = PerlinEngine.fade(fw0);
        double r = PerlinEngine.fade(fz0);
        double t = PerlinEngine.fade(fy0);
        double s = PerlinEngine.fade(fx0);

        double nxyz0 = PerlinEngine.grad(this.p[ix0 + this.p[iy0 + this.p[iz0 + this.p[iw0]]]], fx0, fy0, fz0, fw0);
        double nxyz1 = PerlinEngine.grad(this.p[ix0 + this.p[iy0 + this.p[iz0 + this.p[iw1]]]], fx0, fy0, fz0, fw1);
        double nxy0 = PerlinEngine.lerp(q, nxyz0, nxyz1);

        nxyz0 = PerlinEngine.grad(this.p[ix0 + this.p[iy0 + this.p[iz1 + this.p[iw0]]]], fx0, fy0, fz1, fw0);
        nxyz1 = PerlinEngine.grad(this.p[ix0 + this.p[iy0 + this.p[iz1 + this.p[iw1]]]], fx0, fy0, fz1, fw1);
        double nxy1 = PerlinEngine.lerp(q, nxyz0, nxyz1);

        double nx0 = PerlinEngine.lerp(r, nxy0, nxy1);

        nxyz0 = PerlinEngine.grad(this.p[ix0 + this.p[iy1 + this.p[iz0 + this.p[iw0]]]], fx0, fy1, fz0, fw0);
        nxyz1 = PerlinEngine.grad(this.p[ix0 + this.p[iy1 + this.p[iz0 + this.p[iw1]]]], fx0, fy1, fz0, fw1);
        nxy0 = PerlinEngine.lerp(q, nxyz0, nxyz1);

        nxyz0 = PerlinEngine.grad(this.p[ix0 + this.p[iy1 + this.p[iz1 + this.p[iw0]]]], fx0, fy1, fz1, fw0);
        nxyz1 = PerlinEngine.grad(this.p[ix0 + this.p[iy1 + this.p[iz1 + this.p[iw1]]]], fx0, fy1, fz1, fw1);
        nxy1 = PerlinEngine.lerp(q, nxyz0, nxyz1);

        double nx1 = PerlinEngine.lerp(r, nxy0, nxy1);

        double n0 = PerlinEngine.lerp(t, nx0, nx1);

        nxyz0 = PerlinEngine.grad(this.p[ix1 + this.p[iy0 + this.p[iz0 + this.p[iw0]]]], fx1, fy0, fz0, fw0);
        nxyz1 = PerlinEngine.grad(this.p[ix1 + this.p[iy0 + this.p[iz0 + this.p[iw1]]]], fx1, fy0, fz0, fw1);
        nxy0 = PerlinEngine.lerp(q, nxyz0, nxyz1);

        nxyz0 = PerlinEngine.grad(this.p[ix1 + this.p[iy0 + this.p[iz1 + this.p[iw0]]]], fx1, fy0, fz1, fw0);
        nxyz1 = PerlinEngine.grad(this.p[ix1 + this.p[iy0 + this.p[iz1 + this.p[iw1]]]], fx1, fy0, fz1, fw1);
        nxy1 = PerlinEngine.lerp(q, nxyz0, nxyz1);

        nx0 = PerlinEngine.lerp(r, nxy0, nxy1);

        nxyz0 = PerlinEngine.grad(this.p[ix1 + this.p[iy1 + this.p[iz0 + this.p[iw0]]]], fx1, fy1, fz0, fw0);
        nxyz1 = PerlinEngine.grad(this.p[ix1 + this.p[iy1 + this.p[iz0 + this.p[iw1]]]], fx1, fy1, fz0, fw1);
        nxy0 = PerlinEngine.lerp(q, nxyz0, nxyz1);

        nxyz0 = PerlinEngine.grad(this.p[ix1 + this.p[iy1 + this.p[iz1 + this.p[iw0]]]], fx1, fy1, fz1, fw0);
        nxyz1 = PerlinEngine.grad(this.p[ix1 + this.p[iy1 + this.p[iz1 + this.p[iw1]]]], fx1, fy1, fz1, fw1);
        nxy1 = PerlinEngine.lerp(q, nxyz0, nxyz1);

        nx1 = PerlinEngine.lerp(r, nxy0, nxy1);

        double n1 = PerlinEngine.lerp(t, nx0, nx1);

        return PerlinEngine.lerp(s, n0, n1) * 0.835370364d;
    }
}
