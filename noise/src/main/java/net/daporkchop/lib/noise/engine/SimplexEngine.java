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

import static java.lang.StrictMath.sqrt;
import static net.daporkchop.lib.math.primitive.Floor.floorI;

/**
 * An implementation of Ken Perlin's Simplex noise algorithm
 * <p>
 * Based on https://github.com/nvpro-samples/shared_sources/blob/master/noise/simplexnoise1234.cpp
 *
 * @author DaPorkchop_
 */
public class SimplexEngine extends BasicSeedEngine {
    private static final char[][] simplex = {
            {0, 1, 2, 3}, {0, 1, 3, 2}, {0, 0, 0, 0}, {0, 2, 3, 1}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {1, 2, 3, 0},
            {0, 2, 1, 3}, {0, 0, 0, 0}, {0, 3, 1, 2}, {0, 3, 2, 1}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {1, 3, 2, 0},
            {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0},
            {1, 2, 0, 3}, {0, 0, 0, 0}, {1, 3, 0, 2}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {2, 3, 0, 1}, {2, 3, 1, 0},
            {1, 0, 2, 3}, {1, 0, 3, 2}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {2, 0, 3, 1}, {0, 0, 0, 0}, {2, 1, 3, 0},
            {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0},
            {2, 0, 1, 3}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0, 1, 2}, {3, 0, 2, 1}, {0, 0, 0, 0}, {3, 1, 2, 0},
            {2, 1, 0, 3}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 1, 0, 2}, {0, 0, 0, 0}, {3, 2, 0, 1}, {3, 2, 1, 0}};

    private static final double F2 = 0.5d * (sqrt(3.0d) - 1.0d);
    private static final double G2 = (3.0d - sqrt(3.0d)) / 6.0d;
    private static final double F3 = 0.333333333d;
    private static final double G3 = 0.166666667d;
    private static final double F4 = (sqrt(5.0d) - 1.0d) / 4.0d;
    private static final double G4 = (5.0d - sqrt(5.0d)) / 20.0d;

    private static double grad(int hash, double x) {
        int h = hash & 15;
        double grad = 1.0d + (h & 7);   // Gradient value 1.0, 2.0, ..., 8.0
        if ((h & 8) == 0) {
            grad = -grad;         // Set a random sign for the gradient
        }
        return (grad * x);           // Multiply the gradient with the distance
    }

    private static double grad(int hash, double x, double y) {
        int h = hash & 7;      // Convert low 3 bits of hash code
        double u = h < 4 ? x : y;  // into 8 simple gradient directions,
        double v = h < 4 ? y : x;  // and compute the dot product with (x,y).
        return ((h & 1) == 0 ? -u : u) + ((h & 2) == 0 ? -2.0d * v : 2.0d * v);
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

    public SimplexEngine(long seed) {
        super(seed);
    }

    @Override
    public double get(double x) {
        int i0 = floorI(x);
        int i1 = i0 + 1;
        double x0 = x - i0;
        double x1 = x0 - 1.0d;

        double n0, n1;

        double t0 = 1.0d - x0 * x0;
//  if(t0 < 0.0d) t0 = 0.0d;
        t0 *= t0;
        n0 = t0 * t0 * SimplexEngine.grad(this.p[i0 & 0xff], x0);

        double t1 = 1.0d - x1 * x1;
//  if(t1 < 0.0d) t1 = 0.0d;
        t1 *= t1;
        n1 = t1 * t1 * SimplexEngine.grad(this.p[i1 & 0xff], x1);

        return 0.395d * (n0 + n1);
    }

    @Override
    public double get(double x, double y) {
        double n0, n1, n2; // Noise contributions from the three corners

        // Skew the input space to determine which simplex cell we're in
        double s = (x + y) * F2; // Hairy factor for 2D
        double xs = x + s;
        double ys = y + s;
        int i = floorI(xs);
        int j = floorI(ys);

        double t = (double) (i + j) * G2;
        double X0 = i - t; // Unskew the cell origin back to (x,y) space
        double Y0 = j - t;
        double x0 = x - X0; // The x,y distances from the cell origin
        double y0 = y - Y0;

        // For the 2D case, the simplex shape is an equilateral triangle.
        // Determine which simplex we are in.
        int i1, j1; // Offsets for second (middle) corner of simplex in (i,j) coords
        if (x0 > y0) {
            i1 = 1;
            j1 = 0;
        } // lower triangle, XY order: (0,0)->(1,0)->(1,1)
        else {
            i1 = 0;
            j1 = 1;
        }      // upper triangle, YX order: (0,0)->(0,1)->(1,1)

        // A step of (1,0) in (i,j) means a step of (1-c,-c) in (x,y), and
        // a step of (0,1) in (i,j) means a step of (-c,1-c) in (x,y), where
        // c = (3-sqrt(3))/6

        double x1 = x0 - i1 + G2; // Offsets for middle corner in (x,y) unskewed coords
        double y1 = y0 - j1 + G2;
        double x2 = x0 - 1.0d + 2.0d * G2; // Offsets for last corner in (x,y) unskewed coords
        double y2 = y0 - 1.0d + 2.0d * G2;

        // Wrap the integer indices at 256, to avoid indexing p[] out of bounds
        int ii = i & 0xff;
        int jj = j & 0xff;

        // Calculate the contribution from the three corners
        double t0 = 0.5d - x0 * x0 - y0 * y0;
        if (t0 < 0.0d) n0 = 0.0d;
        else {
            t0 *= t0;
            n0 = t0 * t0 * SimplexEngine.grad(this.p[ii + this.p[jj]], x0, y0);
        }

        double t1 = 0.5d - x1 * x1 - y1 * y1;
        if (t1 < 0.0d) n1 = 0.0d;
        else {
            t1 *= t1;
            n1 = t1 * t1 * SimplexEngine.grad(this.p[ii + i1 + this.p[jj + j1]], x1, y1);
        }

        double t2 = 0.5d - x2 * x2 - y2 * y2;
        if (t2 < 0.0d) n2 = 0.0d;
        else {
            t2 *= t2;
            n2 = t2 * t2 * SimplexEngine.grad(this.p[ii + 1 + this.p[jj + 1]], x2, y2);
        }

        // Add contributions from each corner to get the final noise value.
        // The result is scaled to return values in the interval [-1,1].
        return 45.2d * (n0 + n1 + n2);
    }

    @Override
    public double get(double x, double y, double z) {
        double n0, n1, n2, n3; // Noise contributions from the four corners

        // Skew the input space to determine which simplex cell we're in
        double s = (x + y + z) * F3; // Very nice and simple skew factor for 3D
        double xs = x + s;
        double ys = y + s;
        double zs = z + s;
        int i = floorI(xs);
        int j = floorI(ys);
        int k = floorI(zs);

        double t = (double) (i + j + k) * G3;
        double X0 = i - t; // Unskew the cell origin back to (x,y,z) space
        double Y0 = j - t;
        double Z0 = k - t;
        double x0 = x - X0; // The x,y,z distances from the cell origin
        double y0 = y - Y0;
        double z0 = z - Z0;

        // For the 3D case, the simplex shape is a slightly irregular tetrahedron.
        // Determine which simplex we are in.
        int i1, j1, k1; // Offsets for second corner of simplex in (i,j,k) coords
        int i2, j2, k2; // Offsets for third corner of simplex in (i,j,k) coords

        /* This code would benefit from a backport from the GLSL version! */
        if (x0 >= y0) {
            if (y0 >= z0) {
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            } // X Y Z order
            else if (x0 >= z0) {
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            } // X Z Y order
            else {
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            } // Z X Y order
        } else { // x0<y0
            if (y0 < z0) {
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 0;
                j2 = 1;
                k2 = 1;
            } // Z Y X order
            else if (x0 < z0) {
                i1 = 0;
                j1 = 1;
                k1 = 0;
                i2 = 0;
                j2 = 1;
                k2 = 1;
            } // Y Z X order
            else {
                i1 = 0;
                j1 = 1;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            } // Y X Z order
        }

        // A step of (1,0,0) in (i,j,k) means a step of (1-c,-c,-c) in (x,y,z),
        // a step of (0,1,0) in (i,j,k) means a step of (-c,1-c,-c) in (x,y,z), and
        // a step of (0,0,1) in (i,j,k) means a step of (-c,-c,1-c) in (x,y,z), where
        // c = 1/6.

        double x1 = x0 - i1 + G3; // Offsets for second corner in (x,y,z) coords
        double y1 = y0 - j1 + G3;
        double z1 = z0 - k1 + G3;
        double x2 = x0 - i2 + 2.0d * G3; // Offsets for third corner in (x,y,z) coords
        double y2 = y0 - j2 + 2.0d * G3;
        double z2 = z0 - k2 + 2.0d * G3;
        double x3 = x0 - 1.0d + 3.0d * G3; // Offsets for last corner in (x,y,z) coords
        double y3 = y0 - 1.0d + 3.0d * G3;
        double z3 = z0 - 1.0d + 3.0d * G3;

        // Wrap the integer indices at 256, to avoid indexing p[] out of bounds
        int ii = i & 0xff;
        int jj = j & 0xff;
        int kk = k & 0xff;

        // Calculate the contribution from the four corners
        double t0 = 0.6d - x0 * x0 - y0 * y0 - z0 * z0;
        if (t0 < 0.0d) n0 = 0.0d;
        else {
            t0 *= t0;
            n0 = t0 * t0 * SimplexEngine.grad(this.p[ii + this.p[jj + this.p[kk]]], x0, y0, z0);
        }

        double t1 = 0.6d - x1 * x1 - y1 * y1 - z1 * z1;
        if (t1 < 0.0d) n1 = 0.0d;
        else {
            t1 *= t1;
            n1 = t1 * t1 * SimplexEngine.grad(this.p[ii + i1 + this.p[jj + j1 + this.p[kk + k1]]], x1, y1, z1);
        }

        double t2 = 0.6d - x2 * x2 - y2 * y2 - z2 * z2;
        if (t2 < 0.0d) n2 = 0.0d;
        else {
            t2 *= t2;
            n2 = t2 * t2 * SimplexEngine.grad(this.p[ii + i2 + this.p[jj + j2 + this.p[kk + k2]]], x2, y2, z2);
        }

        double t3 = 0.6d - x3 * x3 - y3 * y3 - z3 * z3;
        if (t3 < 0.0d) n3 = 0.0d;
        else {
            t3 *= t3;
            n3 = t3 * t3 * SimplexEngine.grad(this.p[ii + 1 + this.p[jj + 1 + this.p[kk + 1]]], x3, y3, z3);
        }

        // Add contributions from each corner to get the final noise value.
        // The result is scaled to stay just inside [-1,1]
        return 32.0d * (n0 + n1 + n2 + n3);
    }

    @Override
    public double get(double x, double y, double z, double w) {
        double n0, n1, n2, n3, n4; // Noise contributions from the five corners

        // Skew the (x,y,z,w) space to determine which cell of 24 simplices we're in
        double s = (x + y + z + w) * F4; // Factor for 4D skewing
        double xs = x + s;
        double ys = y + s;
        double zs = z + s;
        double ws = w + s;
        int i = floorI(xs);
        int j = floorI(ys);
        int k = floorI(zs);
        int l = floorI(ws);

        double t = (i + j + k + l) * G4; // Factor for 4D unskewing
        double X0 = i - t; // Unskew the cell origin back to (x,y,z,w) space
        double Y0 = j - t;
        double Z0 = k - t;
        double W0 = l - t;

        double x0 = x - X0;  // The x,y,z,w distances from the cell origin
        double y0 = y - Y0;
        double z0 = z - Z0;
        double w0 = w - W0;

        // For the 4D case, the simplex is a 4D shape I won't even try to describe.
        // To find out which of the 24 possible simplices we're in, we need to
        // determine the magnitude ordering of x0, y0, z0 and w0.
        // The method below is a good way of finding the ordering of x,y,z,w and
        // then find the correct traversal order for the simplex were in.
        // First, six pair-wise comparisons are performed between each possible pair
        // of the four coordinates, and the results are used to add up binary bits
        // for an integer index.
        int c1 = (x0 > y0) ? 32 : 0;
        int c2 = (x0 > z0) ? 16 : 0;
        int c3 = (y0 > z0) ? 8 : 0;
        int c4 = (x0 > w0) ? 4 : 0;
        int c5 = (y0 > w0) ? 2 : 0;
        int c6 = (z0 > w0) ? 1 : 0;
        int c = c1 + c2 + c3 + c4 + c5 + c6;

        int i1, j1, k1, l1; // The integer offsets for the second simplex corner
        int i2, j2, k2, l2; // The integer offsets for the third simplex corner
        int i3, j3, k3, l3; // The integer offsets for the fourth simplex corner

        // simplex[c] is a 4-vector with the numbers 0, 1, 2 and 3 in some order.
        // Many values of c will never occur, since e.g. x>y>z>w makes x<z, y<w and x<w
        // impossible. Only the 24 indices which have non-zero entries make any sense.
        // We use a thresholding to set the coordinates in turn from the largest magnitude.
        // The number 3 in the "simplex" array is at the position of the largest coordinate.
        i1 = simplex[c][0] >= 3 ? 1 : 0;
        j1 = simplex[c][1] >= 3 ? 1 : 0;
        k1 = simplex[c][2] >= 3 ? 1 : 0;
        l1 = simplex[c][3] >= 3 ? 1 : 0;
        // The number 2 in the "simplex" array is at the second largest coordinate.
        i2 = simplex[c][0] >= 2 ? 1 : 0;
        j2 = simplex[c][1] >= 2 ? 1 : 0;
        k2 = simplex[c][2] >= 2 ? 1 : 0;
        l2 = simplex[c][3] >= 2 ? 1 : 0;
        // The number 1 in the "simplex" array is at the second smallest coordinate.
        i3 = simplex[c][0] >= 1 ? 1 : 0;
        j3 = simplex[c][1] >= 1 ? 1 : 0;
        k3 = simplex[c][2] >= 1 ? 1 : 0;
        l3 = simplex[c][3] >= 1 ? 1 : 0;
        // The fifth corner has all coordinate offsets = 1, so no need to look that up.

        double x1 = x0 - i1 + G4; // Offsets for second corner in (x,y,z,w) coords
        double y1 = y0 - j1 + G4;
        double z1 = z0 - k1 + G4;
        double w1 = w0 - l1 + G4;
        double x2 = x0 - i2 + 2.0d * G4; // Offsets for third corner in (x,y,z,w) coords
        double y2 = y0 - j2 + 2.0d * G4;
        double z2 = z0 - k2 + 2.0d * G4;
        double w2 = w0 - l2 + 2.0d * G4;
        double x3 = x0 - i3 + 3.0d * G4; // Offsets for fourth corner in (x,y,z,w) coords
        double y3 = y0 - j3 + 3.0d * G4;
        double z3 = z0 - k3 + 3.0d * G4;
        double w3 = w0 - l3 + 3.0d * G4;
        double x4 = x0 - 1.0d + 4.0d * G4; // Offsets for last corner in (x,y,z,w) coords
        double y4 = y0 - 1.0d + 4.0d * G4;
        double z4 = z0 - 1.0d + 4.0d * G4;
        double w4 = w0 - 1.0d + 4.0d * G4;

        // Wrap the integer indices at 256, to avoid indexing p[] out of bounds
        int ii = i & 0xff;
        int jj = j & 0xff;
        int kk = k & 0xff;
        int ll = l & 0xff;

        // Calculate the contribution from the five corners
        double t0 = 0.6d - x0 * x0 - y0 * y0 - z0 * z0 - w0 * w0;
        if (t0 < 0.0d) n0 = 0.0d;
        else {
            t0 *= t0;
            n0 = t0 * t0 * SimplexEngine.grad(this.p[ii + this.p[jj + this.p[kk + this.p[ll]]]], x0, y0, z0, w0);
        }

        double t1 = 0.6d - x1 * x1 - y1 * y1 - z1 * z1 - w1 * w1;
        if (t1 < 0.0d) n1 = 0.0d;
        else {
            t1 *= t1;
            n1 = t1 * t1 * SimplexEngine.grad(this.p[ii + i1 + this.p[jj + j1 + this.p[kk + k1 + this.p[ll + l1]]]], x1, y1, z1, w1);
        }

        double t2 = 0.6d - x2 * x2 - y2 * y2 - z2 * z2 - w2 * w2;
        if (t2 < 0.0d) n2 = 0.0d;
        else {
            t2 *= t2;
            n2 = t2 * t2 * SimplexEngine.grad(this.p[ii + i2 + this.p[jj + j2 + this.p[kk + k2 + this.p[ll + l2]]]], x2, y2, z2, w2);
        }

        double t3 = 0.6d - x3 * x3 - y3 * y3 - z3 * z3 - w3 * w3;
        if (t3 < 0.0d) n3 = 0.0d;
        else {
            t3 *= t3;
            n3 = t3 * t3 * SimplexEngine.grad(this.p[ii + i3 + this.p[jj + j3 + this.p[kk + k3 + this.p[ll + l3]]]], x3, y3, z3, w3);
        }

        double t4 = 0.6d - x4 * x4 - y4 * y4 - z4 * z4 - w4 * w4;
        if (t4 < 0.0d) n4 = 0.0d;
        else {
            t4 *= t4;
            n4 = t4 * t4 * SimplexEngine.grad(this.p[ii + 1 + this.p[jj + 1 + this.p[kk + 1 + this.p[ll + 1]]]], x4, y4, z4, w4);
        }

        // Sum up and scale the result to cover the range [-1,1]
        return 27.0d * (n0 + n1 + n2 + n3 + n4);
    }
}
