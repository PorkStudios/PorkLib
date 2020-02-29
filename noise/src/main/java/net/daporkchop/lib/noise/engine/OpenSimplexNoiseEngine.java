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
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.random.PRandom;

import static net.daporkchop.lib.math.primitive.PMath.*;

/**
 * Adaptation of OpenSimplex noise with some slight optimizations.
 * <p>
 * <a href="https://gist.github.com/KdotJPG/b1270127455a94ac5d19">Source</a>
 *
 * @author DaPorkchop_
 */
public class OpenSimplexNoiseEngine implements NoiseSource {
    protected static final double STRETCH_CONSTANT_2D = -0.211324865405187d;
    protected static final double SQUISH_CONSTANT_2D  = 0.366025403784439d;
    protected static final double STRETCH_CONSTANT_3D = -1.0d / 6.0d;
    protected static final double SQUISH_CONSTANT_3D  = 1.0d / 3.0d;

    protected static final double NORM_CONSTANT_2D = 47.0d;
    protected static final double NORM_CONSTANT_3D = 103.0d;

    protected static final byte[] GRADIENTS_2D = {
            5, 2, 2, 5,
            -5, 2, -2, 5,
            5, -2, 2, -5,
            -5, -2, -2, -5,
    };

    protected static final byte[] GRADIENTS_3D = {
            -11, 4, 4, -4, 11, 4, -4, 4, 11,
            11, 4, 4, 4, 11, 4, 4, 4, 11,
            -11, -4, 4, -4, -11, 4, -4, -4, 11,
            11, -4, 4, 4, -11, 4, 4, -4, 11,
            -11, 4, -4, -4, 11, -4, -4, 4, -11,
            11, 4, -4, 4, 11, -4, 4, 4, -11,
            -11, -4, -4, -4, -11, -4, -4, -4, -11,
            11, -4, -4, 4, -11, -4, 4, -4, -11,
    };

    protected final byte[] p            = new byte[256];
    protected final byte[] pGradIndex3D = new byte[256];

    public OpenSimplexNoiseEngine(@NonNull PRandom random) {
        byte[] source = PerlinNoiseEngine.INITIAL_STATE_CACHE.get().clone();

        for (int i = 255; i >= 0; i--) {
            int r = random.nextInt(i + 1);
            this.p[i] = source[r];
            this.pGradIndex3D[i] = (byte) (((this.p[i] & 0xFF) % (GRADIENTS_3D.length / 3)) * 3);
            source[r] = source[i];
        }
    }

    @Override
    public double get(double x) {
        //TODO: optimized 1D implementation? is this even needed for anything?
        return this.get(x, 0.0d);
    }

    @Override
    public double get(double x, double y) {
        double stretchOffset = (x + y) * STRETCH_CONSTANT_2D;
        double xs = x + stretchOffset;
        double ys = y + stretchOffset;

        int xsb = floorI(xs);
        int ysb = floorI(ys);

        double squishOffset = (xsb + ysb) * SQUISH_CONSTANT_2D;
        double xb = xsb + squishOffset;
        double yb = ysb + squishOffset;

        double xins = xs - xsb;
        double yins = ys - ysb;

        double inSum = xins + yins;

        double dx0 = x - xb;
        double dy0 = y - yb;

        double dx_ext;
        double dy_ext;
        int xsv_ext;
        int ysv_ext;

        double value = 0.0d;

        double dx1 = dx0 - 1.0d - SQUISH_CONSTANT_2D;
        double dy1 = dy0 - 0.0d - SQUISH_CONSTANT_2D;
        double attn1 = 2.0d - dx1 * dx1 - dy1 * dy1;
        if (attn1 > 0.0d) {
            attn1 *= attn1;
            value += attn1 * attn1 * this.extrapolate(xsb + 1, ysb, dx1, dy1);
        }

        double dx2 = dx0 - 0.0d - SQUISH_CONSTANT_2D;
        double dy2 = dy0 - 1.0d - SQUISH_CONSTANT_2D;
        double attn2 = 2.0d - dx2 * dx2 - dy2 * dy2;
        if (attn2 > 0.0d) {
            attn2 *= attn2;
            value += attn2 * attn2 * this.extrapolate(xsb, ysb + 1, dx2, dy2);
        }

        if (inSum <= 1.0d) {
            double zins = 1.0d - inSum;
            if (zins > xins || zins > yins) {
                if (xins > yins) {
                    xsv_ext = xsb + 1;
                    ysv_ext = ysb - 1;
                    dx_ext = dx0 - 1.0d;
                    dy_ext = dy0 + 1.0d;
                } else {
                    xsv_ext = xsb - 1;
                    ysv_ext = ysb + 1;
                    dx_ext = dx0 + 1.0d;
                    dy_ext = dy0 - 1.0d;
                }
            } else {
                xsv_ext = xsb + 1;
                ysv_ext = ysb + 1;
                dx_ext = dx0 - 1.0d - 2.0d * SQUISH_CONSTANT_2D;
                dy_ext = dy0 - 1.0d - 2.0d * SQUISH_CONSTANT_2D;
            }
        } else {
            double zins = 2.0d - inSum;
            if (zins < xins || zins < yins) {
                if (xins > yins) {
                    xsv_ext = xsb + 2;
                    ysv_ext = ysb;
                    dx_ext = dx0 - 2.0d - 2.0d * SQUISH_CONSTANT_2D;
                    dy_ext = dy0 - 2.0d * SQUISH_CONSTANT_2D;
                } else {
                    xsv_ext = xsb;
                    ysv_ext = ysb + 2;
                    dx_ext = dx0 - 2.0d * SQUISH_CONSTANT_2D;
                    dy_ext = dy0 - 2.0d - 2.0d * SQUISH_CONSTANT_2D;
                }
            } else {
                dx_ext = dx0;
                dy_ext = dy0;
                xsv_ext = xsb;
                ysv_ext = ysb;
            }
            xsb += 1;
            ysb += 1;
            dx0 = dx0 - 1.0d - 2.0d * SQUISH_CONSTANT_2D;
            dy0 = dy0 - 1.0d - 2.0d * SQUISH_CONSTANT_2D;
        }

        double attn0 = 2.0d - dx0 * dx0 - dy0 * dy0;
        if (attn0 > 0.0d) {
            attn0 *= attn0;
            value += attn0 * attn0 * this.extrapolate(xsb, ysb, dx0, dy0);
        }

        double attn_ext = 2.0d - dx_ext * dx_ext - dy_ext * dy_ext;
        if (attn_ext > 0.0d) {
            attn_ext *= attn_ext;
            value += attn_ext * attn_ext * this.extrapolate(xsv_ext, ysv_ext, dx_ext, dy_ext);
        }

        return value / NORM_CONSTANT_2D;
    }

    @Override
    public double get(double x, double y, double z) {
        double stretchOffset = (x + y + z) * STRETCH_CONSTANT_3D;
        double xs = x + stretchOffset;
        double ys = y + stretchOffset;
        double zs = z + stretchOffset;

        int xsb = floorI(xs);
        int ysb = floorI(ys);
        int zsb = floorI(zs);

        double squishOffset = (xsb + ysb + zsb) * SQUISH_CONSTANT_3D;
        double xb = xsb + squishOffset;
        double yb = ysb + squishOffset;
        double zb = zsb + squishOffset;

        double xins = xs - xsb;
        double yins = ys - ysb;
        double zins = zs - zsb;

        double inSum = xins + yins + zins;

        double dx0 = x - xb;
        double dy0 = y - yb;
        double dz0 = z - zb;

        double dx_ext0;
        double dy_ext0;
        double dz_ext0;
        double dx_ext1;
        double dy_ext1;
        double dz_ext1;
        int xsv_ext0;
        int ysv_ext0;
        int zsv_ext0;
        int xsv_ext1;
        int ysv_ext1;
        int zsv_ext1;

        double value = 0.0d;
        if (inSum <= 1.0d) {
            byte aPoint = 0x01;
            double aScore = xins;
            byte bPoint = 0x02;
            double bScore = yins;
            if (aScore >= bScore && zins > bScore) {
                bScore = zins;
                bPoint = 0x04;
            } else if (aScore < bScore && zins > aScore) {
                aScore = zins;
                aPoint = 0x04;
            }
            double wins = 1.0d - inSum;
            if (wins > aScore || wins > bScore) {
                byte c = (bScore > aScore ? bPoint : aPoint);

                if ((c & 0x01) == 0) {
                    xsv_ext0 = xsb - 1;
                    xsv_ext1 = xsb;
                    dx_ext0 = dx0 + 1.0d;
                    dx_ext1 = dx0;
                } else {
                    xsv_ext0 = xsv_ext1 = xsb + 1;
                    dx_ext0 = dx_ext1 = dx0 - 1.0d;
                }

                if ((c & 0x02) == 0) {
                    ysv_ext0 = ysv_ext1 = ysb;
                    dy_ext0 = dy_ext1 = dy0;
                    if ((c & 0x01) == 0) {
                        ysv_ext1 -= 1;
                        dy_ext1 += 1.0d;
                    } else {
                        ysv_ext0 -= 1;
                        dy_ext0 += 1.0d;
                    }
                } else {
                    ysv_ext0 = ysv_ext1 = ysb + 1;
                    dy_ext0 = dy_ext1 = dy0 - 1.0d;
                }

                if ((c & 0x04) == 0) {
                    zsv_ext0 = zsb;
                    zsv_ext1 = zsb - 1;
                    dz_ext0 = dz0;
                    dz_ext1 = dz0 + 1.0d;
                } else {
                    zsv_ext0 = zsv_ext1 = zsb + 1;
                    dz_ext0 = dz_ext1 = dz0 - 1.0d;
                }
            } else {
                byte c = (byte) (aPoint | bPoint);

                if ((c & 0x01) == 0) {
                    xsv_ext0 = xsb;
                    xsv_ext1 = xsb - 1;
                    dx_ext0 = dx0 - 2.0d * SQUISH_CONSTANT_3D;
                    dx_ext1 = dx0 + 1.0d - SQUISH_CONSTANT_3D;
                } else {
                    xsv_ext0 = xsv_ext1 = xsb + 1;
                    dx_ext0 = dx0 - 1.0d - 2.0d * SQUISH_CONSTANT_3D;
                    dx_ext1 = dx0 - 1.0d - SQUISH_CONSTANT_3D;
                }

                if ((c & 0x02) == 0) {
                    ysv_ext0 = ysb;
                    ysv_ext1 = ysb - 1;
                    dy_ext0 = dy0 - 2.0d * SQUISH_CONSTANT_3D;
                    dy_ext1 = dy0 + 1.0d - SQUISH_CONSTANT_3D;
                } else {
                    ysv_ext0 = ysv_ext1 = ysb + 1;
                    dy_ext0 = dy0 - 1.0d - 2.0d * SQUISH_CONSTANT_3D;
                    dy_ext1 = dy0 - 1.0d - SQUISH_CONSTANT_3D;
                }

                if ((c & 0x04) == 0) {
                    zsv_ext0 = zsb;
                    zsv_ext1 = zsb - 1;
                    dz_ext0 = dz0 - 2.0d * SQUISH_CONSTANT_3D;
                    dz_ext1 = dz0 + 1.0d - SQUISH_CONSTANT_3D;
                } else {
                    zsv_ext0 = zsv_ext1 = zsb + 1;
                    dz_ext0 = dz0 - 1.0d - 2.0d * SQUISH_CONSTANT_3D;
                    dz_ext1 = dz0 - 1.0d - SQUISH_CONSTANT_3D;
                }
            }

            double attn0 = 2 - dx0 * dx0 - dy0 * dy0 - dz0 * dz0;
            if (attn0 > 0) {
                attn0 *= attn0;
                value += attn0 * attn0 * this.extrapolate(xsb, ysb, zsb, dx0, dy0, dz0);
            }

            double dx1 = dx0 - 1.0d - SQUISH_CONSTANT_3D;
            double dy1 = dy0 - 0.0d - SQUISH_CONSTANT_3D;
            double dz1 = dz0 - 0.0d - SQUISH_CONSTANT_3D;
            double attn1 = 2.0d - dx1 * dx1 - dy1 * dy1 - dz1 * dz1;
            if (attn1 > 0.0d) {
                attn1 *= attn1;
                value += attn1 * attn1 * this.extrapolate(xsb + 1, ysb, zsb, dx1, dy1, dz1);
            }

            double dx2 = dx0 - 0.0d - SQUISH_CONSTANT_3D;
            double dy2 = dy0 - 1.0d - SQUISH_CONSTANT_3D;
            double attn2 = 2.0d - dx2 * dx2 - dy2 * dy2 - dz1 * dz1;
            if (attn2 > 0.0d) {
                attn2 *= attn2;
                value += attn2 * attn2 * this.extrapolate(xsb, ysb + 1, zsb, dx2, dy2, dz1);
            }

            double dz3 = dz0 - 1.0d - SQUISH_CONSTANT_3D;
            double attn3 = 2.0d - dx2 * dx2 - dy1 * dy1 - dz3 * dz3;
            if (attn3 > 0.0d) {
                attn3 *= attn3;
                value += attn3 * attn3 * this.extrapolate(xsb, ysb, zsb + 1, dx2, dy1, dz3);
            }
        } else if (inSum >= 2) {
            byte aPoint = 0x06;
            double aScore = xins;
            byte bPoint = 0x05;
            double bScore = yins;
            if (aScore <= bScore && zins < bScore) {
                bScore = zins;
                bPoint = 0x03;
            } else if (aScore > bScore && zins < aScore) {
                aScore = zins;
                aPoint = 0x03;
            }
            double wins = 3.0d - inSum;
            if (wins < aScore || wins < bScore) {
                byte c = (bScore < aScore ? bPoint : aPoint);

                if ((c & 0x01) != 0) {
                    xsv_ext0 = xsb + 2;
                    xsv_ext1 = xsb + 1;
                    dx_ext0 = dx0 - 2.0d - 3.0d * SQUISH_CONSTANT_3D;
                    dx_ext1 = dx0 - 1.0d - 3.0d * SQUISH_CONSTANT_3D;
                } else {
                    xsv_ext0 = xsv_ext1 = xsb;
                    dx_ext0 = dx_ext1 = dx0 - 3.0d * SQUISH_CONSTANT_3D;
                }

                if ((c & 0x02) != 0) {
                    ysv_ext0 = ysv_ext1 = ysb + 1;
                    dy_ext0 = dy_ext1 = dy0 - 1.0d - 3.0d * SQUISH_CONSTANT_3D;
                    if ((c & 0x01) != 0) {
                        ysv_ext1 += 1;
                        dy_ext1 -= 1.0d;
                    } else {
                        ysv_ext0 += 1;
                        dy_ext0 -= 1.0d;
                    }
                } else {
                    ysv_ext0 = ysv_ext1 = ysb;
                    dy_ext0 = dy_ext1 = dy0 - 3.0d * SQUISH_CONSTANT_3D;
                }

                if ((c & 0x04) != 0) {
                    zsv_ext0 = zsb + 1;
                    zsv_ext1 = zsb + 2;
                    dz_ext0 = dz0 - 1.0d - 3.0d * SQUISH_CONSTANT_3D;
                    dz_ext1 = dz0 - 2.0d - 3.0d * SQUISH_CONSTANT_3D;
                } else {
                    zsv_ext0 = zsv_ext1 = zsb;
                    dz_ext0 = dz_ext1 = dz0 - 3.0d * SQUISH_CONSTANT_3D;
                }
            } else {
                byte c = (byte) (aPoint & bPoint);

                if ((c & 0x01) != 0) {
                    xsv_ext0 = xsb + 1;
                    xsv_ext1 = xsb + 2;
                    dx_ext0 = dx0 - 1.0d - SQUISH_CONSTANT_3D;
                    dx_ext1 = dx0 - 2.0d - 2.0d * SQUISH_CONSTANT_3D;
                } else {
                    xsv_ext0 = xsv_ext1 = xsb;
                    dx_ext0 = dx0 - SQUISH_CONSTANT_3D;
                    dx_ext1 = dx0 - 2.0d * SQUISH_CONSTANT_3D;
                }

                if ((c & 0x02) != 0) {
                    ysv_ext0 = ysb + 1;
                    ysv_ext1 = ysb + 2;
                    dy_ext0 = dy0 - 1.0d - SQUISH_CONSTANT_3D;
                    dy_ext1 = dy0 - 2.0d - 2.0d * SQUISH_CONSTANT_3D;
                } else {
                    ysv_ext0 = ysv_ext1 = ysb;
                    dy_ext0 = dy0 - SQUISH_CONSTANT_3D;
                    dy_ext1 = dy0 - 2.0d * SQUISH_CONSTANT_3D;
                }

                if ((c & 0x04) != 0) {
                    zsv_ext0 = zsb + 1;
                    zsv_ext1 = zsb + 2;
                    dz_ext0 = dz0 - 1.0d - SQUISH_CONSTANT_3D;
                    dz_ext1 = dz0 - 2.0d - 2.0d * SQUISH_CONSTANT_3D;
                } else {
                    zsv_ext0 = zsv_ext1 = zsb;
                    dz_ext0 = dz0 - SQUISH_CONSTANT_3D;
                    dz_ext1 = dz0 - 2.0d * SQUISH_CONSTANT_3D;
                }
            }

            double dx3 = dx0 - 1.0d - 2.0d * SQUISH_CONSTANT_3D;
            double dy3 = dy0 - 1.0d - 2.0d * SQUISH_CONSTANT_3D;
            double dz3 = dz0 - 2.0d * SQUISH_CONSTANT_3D;
            double attn3 = 2.0d - dx3 * dx3 - dy3 * dy3 - dz3 * dz3;
            if (attn3 > 0.0d) {
                attn3 *= attn3;
                value += attn3 * attn3 * this.extrapolate(xsb + 1, ysb + 1, zsb, dx3, dy3, dz3);
            }

            double dy2 = dy0 - 2.0d * SQUISH_CONSTANT_3D;
            double dz2 = dz0 - 1.0d - 2.0d * SQUISH_CONSTANT_3D;
            double attn2 = 2.0d - dx3 * dx3 - dy2 * dy2 - dz2 * dz2;
            if (attn2 > 0.0d) {
                attn2 *= attn2;
                value += attn2 * attn2 * this.extrapolate(xsb + 1, ysb, zsb + 1, dx3, dy2, dz2);
            }

            double dx1 = dx0 - 2.0d * SQUISH_CONSTANT_3D;
            double attn1 = 2.0d - dx1 * dx1 - dy3 * dy3 - dz2 * dz2;
            if (attn1 > 0.0d) {
                attn1 *= attn1;
                value += attn1 * attn1 * this.extrapolate(xsb, ysb + 1, zsb + 1, dx1, dy3, dz2);
            }

            dx0 = dx0 - 1.0d - 3.0d * SQUISH_CONSTANT_3D;
            dy0 = dy0 - 1.0d - 3.0d * SQUISH_CONSTANT_3D;
            dz0 = dz0 - 1.0d - 3.0d * SQUISH_CONSTANT_3D;
            double attn0 = 2.0d - dx0 * dx0 - dy0 * dy0 - dz0 * dz0;
            if (attn0 > 0.0d) {
                attn0 *= attn0;
                value += attn0 * attn0 * this.extrapolate(xsb + 1, ysb + 1, zsb + 1, dx0, dy0, dz0);
            }
        } else {
            double aScore;
            byte aPoint;
            boolean aIsFurtherSide;
            double bScore;
            byte bPoint;
            boolean bIsFurtherSide;

            double p1 = xins + yins;
            if (p1 > 1.0d) {
                aScore = p1 - 1.0d;
                aPoint = 0x03;
                aIsFurtherSide = true;
            } else {
                aScore = 1.0d - p1;
                aPoint = 0x04;
                aIsFurtherSide = false;
            }

            double p2 = xins + zins;
            if (p2 > 1.0d) {
                bScore = p2 - 1.0d;
                bPoint = 0x05;
                bIsFurtherSide = true;
            } else {
                bScore = 1.0d - p2;
                bPoint = 0x02;
                bIsFurtherSide = false;
            }

            double p3 = yins + zins;
            if (p3 > 1.0d) {
                double score = p3 - 1.0d;
                if (aScore <= bScore && aScore < score) {
                    aPoint = 0x06;
                    aIsFurtherSide = true;
                } else if (aScore > bScore && bScore < score) {
                    bPoint = 0x06;
                    bIsFurtherSide = true;
                }
            } else {
                double score = 1.0d - p3;
                if (aScore <= bScore && aScore < score) {
                    aPoint = 0x01;
                    aIsFurtherSide = false;
                } else if (aScore > bScore && bScore < score) {
                    bPoint = 0x01;
                    bIsFurtherSide = false;
                }
            }

            if (aIsFurtherSide == bIsFurtherSide) {
                if (aIsFurtherSide) {
                    dx_ext0 = dx0 - 1.0d - 3.0d * SQUISH_CONSTANT_3D;
                    dy_ext0 = dy0 - 1.0d - 3.0d * SQUISH_CONSTANT_3D;
                    dz_ext0 = dz0 - 1.0d - 3.0d * SQUISH_CONSTANT_3D;
                    xsv_ext0 = xsb + 1;
                    ysv_ext0 = ysb + 1;
                    zsv_ext0 = zsb + 1;

                    byte c = (byte) (aPoint & bPoint);
                    if ((c & 0x01) != 0) {
                        dx_ext1 = dx0 - 2.0d - 2.0d * SQUISH_CONSTANT_3D;
                        dy_ext1 = dy0 - 2.0d * SQUISH_CONSTANT_3D;
                        dz_ext1 = dz0 - 2.0d * SQUISH_CONSTANT_3D;
                        xsv_ext1 = xsb + 2;
                        ysv_ext1 = ysb;
                        zsv_ext1 = zsb;
                    } else if ((c & 0x02) != 0) {
                        dx_ext1 = dx0 - 2.0d * SQUISH_CONSTANT_3D;
                        dy_ext1 = dy0 - 2.0d - 2.0d * SQUISH_CONSTANT_3D;
                        dz_ext1 = dz0 - 2.0d * SQUISH_CONSTANT_3D;
                        xsv_ext1 = xsb;
                        ysv_ext1 = ysb + 2;
                        zsv_ext1 = zsb;
                    } else {
                        dx_ext1 = dx0 - 2.0d * SQUISH_CONSTANT_3D;
                        dy_ext1 = dy0 - 2.0d * SQUISH_CONSTANT_3D;
                        dz_ext1 = dz0 - 2.0d - 2.0d * SQUISH_CONSTANT_3D;
                        xsv_ext1 = xsb;
                        ysv_ext1 = ysb;
                        zsv_ext1 = zsb + 2;
                    }
                } else {
                    dx_ext0 = dx0;
                    dy_ext0 = dy0;
                    dz_ext0 = dz0;
                    xsv_ext0 = xsb;
                    ysv_ext0 = ysb;
                    zsv_ext0 = zsb;

                    byte c = (byte) (aPoint | bPoint);
                    if ((c & 0x01) == 0) {
                        dx_ext1 = dx0 + 1.0d - SQUISH_CONSTANT_3D;
                        dy_ext1 = dy0 - 1.0d - SQUISH_CONSTANT_3D;
                        dz_ext1 = dz0 - 1.0d - SQUISH_CONSTANT_3D;
                        xsv_ext1 = xsb - 1;
                        ysv_ext1 = ysb + 1;
                        zsv_ext1 = zsb + 1;
                    } else if ((c & 0x02) == 0) {
                        dx_ext1 = dx0 - 1.0d - SQUISH_CONSTANT_3D;
                        dy_ext1 = dy0 + 1.0d - SQUISH_CONSTANT_3D;
                        dz_ext1 = dz0 - 1.0d - SQUISH_CONSTANT_3D;
                        xsv_ext1 = xsb + 1;
                        ysv_ext1 = ysb - 1;
                        zsv_ext1 = zsb + 1;
                    } else {
                        dx_ext1 = dx0 - 1.0d - SQUISH_CONSTANT_3D;
                        dy_ext1 = dy0 - 1.0d - SQUISH_CONSTANT_3D;
                        dz_ext1 = dz0 + 1.0d - SQUISH_CONSTANT_3D;
                        xsv_ext1 = xsb + 1;
                        ysv_ext1 = ysb + 1;
                        zsv_ext1 = zsb - 1;
                    }
                }
            } else {
                byte c1;
                byte c2;
                if (aIsFurtherSide) {
                    c1 = aPoint;
                    c2 = bPoint;
                } else {
                    c1 = bPoint;
                    c2 = aPoint;
                }

                if ((c1 & 0x01) == 0) {
                    dx_ext0 = dx0 + 1.0d - SQUISH_CONSTANT_3D;
                    dy_ext0 = dy0 - 1.0d - SQUISH_CONSTANT_3D;
                    dz_ext0 = dz0 - 1.0d - SQUISH_CONSTANT_3D;
                    xsv_ext0 = xsb - 1;
                    ysv_ext0 = ysb + 1;
                    zsv_ext0 = zsb + 1;
                } else if ((c1 & 0x02) == 0) {
                    dx_ext0 = dx0 - 1.0d - SQUISH_CONSTANT_3D;
                    dy_ext0 = dy0 + 1.0d - SQUISH_CONSTANT_3D;
                    dz_ext0 = dz0 - 1.0d - SQUISH_CONSTANT_3D;
                    xsv_ext0 = xsb + 1;
                    ysv_ext0 = ysb - 1;
                    zsv_ext0 = zsb + 1;
                } else {
                    dx_ext0 = dx0 - 1.0d - SQUISH_CONSTANT_3D;
                    dy_ext0 = dy0 - 1.0d - SQUISH_CONSTANT_3D;
                    dz_ext0 = dz0 + 1.0d - SQUISH_CONSTANT_3D;
                    xsv_ext0 = xsb + 1;
                    ysv_ext0 = ysb + 1;
                    zsv_ext0 = zsb - 1;
                }

                dx_ext1 = dx0 - 2.0d * SQUISH_CONSTANT_3D;
                dy_ext1 = dy0 - 2.0d * SQUISH_CONSTANT_3D;
                dz_ext1 = dz0 - 2.0d * SQUISH_CONSTANT_3D;
                xsv_ext1 = xsb;
                ysv_ext1 = ysb;
                zsv_ext1 = zsb;
                if ((c2 & 0x01) != 0) {
                    dx_ext1 -= 2.0d;
                    xsv_ext1 += 2;
                } else if ((c2 & 0x02) != 0) {
                    dy_ext1 -= 2.0d;
                    ysv_ext1 += 2;
                } else {
                    dz_ext1 -= 2.0d;
                    zsv_ext1 += 2;
                }
            }

            double dx1 = dx0 - 1.0d - SQUISH_CONSTANT_3D;
            double dy1 = dy0 - SQUISH_CONSTANT_3D;
            double dz1 = dz0 - SQUISH_CONSTANT_3D;
            double attn1 = 2.0d - dx1 * dx1 - dy1 * dy1 - dz1 * dz1;
            if (attn1 > 0.0d) {
                attn1 *= attn1;
                value += attn1 * attn1 * this.extrapolate(xsb + 1, ysb, zsb, dx1, dy1, dz1);
            }

            double dx2 = dx0 - SQUISH_CONSTANT_3D;
            double dy2 = dy0 - 1.0d - SQUISH_CONSTANT_3D;
            double attn2 = 2.0d - dx2 * dx2 - dy2 * dy2 - dz1 * dz1;
            if (attn2 > 0.0d) {
                attn2 *= attn2;
                value += attn2 * attn2 * this.extrapolate(xsb, ysb + 1, zsb, dx2, dy2, dz1);
            }

            double dz3 = dz0 - 1.0d - SQUISH_CONSTANT_3D;
            double attn3 = 2.0d - dx2 * dx2 - dy1 * dy1 - dz3 * dz3;
            if (attn3 > 0.0d) {
                attn3 *= attn3;
                value += attn3 * attn3 * this.extrapolate(xsb, ysb, zsb + 1, dx2, dy1, dz3);
            }

            double dx4 = dx0 - 1.0d - 2.0d * SQUISH_CONSTANT_3D;
            double dy4 = dy0 - 1.0d - 2.0d * SQUISH_CONSTANT_3D;
            double dz4 = dz0 - 2.0d * SQUISH_CONSTANT_3D;
            double attn4 = 2.0d - dx4 * dx4 - dy4 * dy4 - dz4 * dz4;
            if (attn4 > 0.0d) {
                attn4 *= attn4;
                value += attn4 * attn4 * this.extrapolate(xsb + 1, ysb + 1, zsb, dx4, dy4, dz4);
            }

            double dy5 = dy0 - 2.0d * SQUISH_CONSTANT_3D;
            double dz5 = dz0 - 1.0d - 2.0d * SQUISH_CONSTANT_3D;
            double attn5 = 2.0d - dx4 * dx4 - dy5 * dy5 - dz5 * dz5;
            if (attn5 > 0.0d) {
                attn5 *= attn5;
                value += attn5 * attn5 * this.extrapolate(xsb + 1, ysb, zsb + 1, dx4, dy5, dz5);
            }

            double dx6 = dx0 - 2.0d * SQUISH_CONSTANT_3D;
            double attn6 = 2.0d - dx6 * dx6 - dy4 * dy4 - dz5 * dz5;
            if (attn6 > 0.0d) {
                attn6 *= attn6;
                value += attn6 * attn6 * this.extrapolate(xsb, ysb + 1, zsb + 1, dx6, dy4, dz5);
            }
        }

        double attn_ext0 = 2.0d - dx_ext0 * dx_ext0 - dy_ext0 * dy_ext0 - dz_ext0 * dz_ext0;
        if (attn_ext0 > 0.0d) {
            attn_ext0 *= attn_ext0;
            value += attn_ext0 * attn_ext0 * this.extrapolate(xsv_ext0, ysv_ext0, zsv_ext0, dx_ext0, dy_ext0, dz_ext0);
        }

        double attn_ext1 = 2.0d - dx_ext1 * dx_ext1 - dy_ext1 * dy_ext1 - dz_ext1 * dz_ext1;
        if (attn_ext1 > 0.0d) {
            attn_ext1 *= attn_ext1;
            value += attn_ext1 * attn_ext1 * this.extrapolate(xsv_ext1, ysv_ext1, zsv_ext1, dx_ext1, dy_ext1, dz_ext1);
        }

        return value / NORM_CONSTANT_3D;
    }

    protected double extrapolate(int xsb, int ysb, double dx, double dy) {
        int index = this.p[((this.p[xsb & 0xFF] & 0xFF) + ysb) & 0xFF] & 0x0E;
        return GRADIENTS_2D[index] * dx + GRADIENTS_2D[index + 1] * dy;
    }

    protected double extrapolate(int xsb, int ysb, int zsb, double dx, double dy, double dz) {
        int index = this.pGradIndex3D[((this.p[((this.p[xsb & 0xFF] & 0xFF) + ysb) & 0xFF] & 0xFF) + zsb) & 0xFF];
        return GRADIENTS_3D[index] * dx + GRADIENTS_3D[index + 1] * dy + GRADIENTS_3D[index + 2] * dz;
    }

    @Override
    public String toString() {
        return this.getClass().getCanonicalName();
    }
}
