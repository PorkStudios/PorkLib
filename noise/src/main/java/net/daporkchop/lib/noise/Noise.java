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

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.noise.engine.INoiseEngine;
import net.daporkchop.lib.noise.func.IntDoubleConsumer;
import net.daporkchop.lib.noise.func.IntIntDoubleConsumer;
import net.daporkchop.lib.noise.func.IntIntIntDoubleConsumer;

/**
 * A wrapper for an {@link INoiseEngine}, providing useful helper methods
 *
 * @author DaPorkchop_
 */
public class Noise {
    /**
     * The actual implementation of a noise generator
     * This array is equally long as the number of octaves. Each
     * one has a random seed based on the input seed, which yields much
     * more natural-looking results
     */
    @Getter
    private final INoiseEngine[] engines;

    /**
     * The frequency at which to sample at
     */
    @Getter
    private final double frequency;

    /**
     * The number of octaves to use for generation
     */
    @Getter
    private final int octaves;

    /**
     * The persistence level for generation
     */
    @Getter
    private final double persistence;

    public Noise(@NonNull NoiseEngineType type, long seed, int octaves, double frequency, double persistence) {
        this.frequency = Math.abs(frequency);
        this.octaves = Math.abs(octaves);
        this.engines = new INoiseEngine[this.octaves];
        for (int i = 0; i < this.engines.length; i++) {
            this.engines[i] = type.getEngine(seed);
            seed *= 31L;
        }
        this.persistence = persistence;
    }

    /**
     * Gets a 1D noise value at the given position, using the octave count, frequency and
     * persistence specified in the constructor
     *
     * @param x the x coordinate to generate at
     * @return the noise value
     */
    public double get(double x) {
        double val = 0.0d;
        double frequency = this.frequency;
        double amplitude = 1.0d;
        double totalAmp = 0.0d;

        double d;
        for (int i = 0; i < this.octaves; i++) {
            d = this.engines[i].get(x * frequency + i * 8.93749d) * amplitude;

            totalAmp += amplitude;
            val += d;

            amplitude *= this.persistence;
            frequency *= 2d;
        }

        return val / totalAmp;
    }

    /**
     * Gets a 2D noise value at the given position, using the octave count, frequency and
     * persistence specified in the constructor
     *
     * @param x the x coordinate to generate at
     * @param y the y coordinate to generate at
     * @return the noise value
     */
    public double get(double x, double y) {
        double val = 0.0d;
        double frequency = this.frequency;
        double amplitude = 1.0d;
        double totalAmp = 0.0d;

        double d;
        for (int i = 0; i < this.octaves; i++) {
            d = this.engines[i].get(x * frequency + i * 8.93749d,
                    y * frequency + i * 3.8457921d) * amplitude;

            totalAmp += amplitude;
            val += d;

            amplitude *= this.persistence;
            frequency *= 2d;
        }

        return val / totalAmp;
    }

    /**
     * Gets a 3D noise value at the given position, using the octave count, frequency and
     * persistence specified in the constructor
     *
     * @param x the x coordinate to generate at
     * @param y the y coordinate to generate at
     * @param z the z coordinate to generate at
     * @return the noise value
     */
    public double get(double x, double y, double z) {
        double val = 0.0d;
        double frequency = this.frequency;
        double amplitude = 1.0d;
        double totalAmp = 0.0d;

        double d;
        for (int i = 0; i < this.octaves; i++) {
            d = this.engines[i].get(x * frequency + i * 8.93749d,
                    y * frequency + i * 3.8457921d,
                    z * frequency + i * 14.398571873d) * amplitude;

            totalAmp += amplitude;
            val += d;

            amplitude *= this.persistence;
            frequency *= 2d;
        }

        return val / totalAmp;
    }

    /**
     * Gets a 4D noise value at the given position, using the octave count, frequency and
     * persistence specified in the constructor
     *
     * @param x the x coordinate to generate at
     * @param y the y coordinate to generate at
     * @param z the z coordinate to generate at
     * @param w the w coordinate to generate at
     * @return the noise value
     */
    public double get(double x, double y, double z, double w) {
        double val = 0.0d;
        double frequency = this.frequency;
        double amplitude = 1.0d;
        double totalAmp = 0.0d;

        double d;
        for (int i = 0; i < this.octaves; i++) {
            d = this.engines[i].get(x * frequency + i * 8.93749d,
                    y * frequency + i * 3.8457921d,
                    z * frequency + i * 14.398571873d,
                    w * frequency + i * 5.29836599d) * amplitude;

            totalAmp += amplitude;
            val += d;

            amplitude *= this.persistence;
            frequency *= 2d;
        }

        return val / totalAmp;
    }

    /**
     * Gets all 1d noise values in a range and writes them to an array
     *
     * @param x     the x coordinate to start at
     * @param sizeX the number of samples to take
     * @param distX the space between samples
     * @param d     the array to write to. a new one will be created and returned if null is passed here.
     * @return the array which the values were written to
     */
    public double[] get1d(double x, int sizeX, double distX, double[] d) {
        if (d == null) {
            d = new double[sizeX];
        } else if (d.length != sizeX) {
            throw new IllegalStateException("Array must be same length as size!");
        }

        for (int X = 0; X < sizeX; X++, x += distX) {
            d[X] = this.get(x);
        }

        return d;
    }

    /**
     * Gets all 2d noise values in a range and writes them to an array
     *
     * @param x     the x coordinate to start at
     * @param y     the y coordinate to start at
     * @param sizeX the number of samples to take on the X axis
     * @param sizeY the number of samples to take on the Y axis
     * @param distX the space between samples on the Y axis
     * @param distY the space between samples on the Y axis
     * @param d     the array to write to. a new one will be created and returned if null is passed here.
     * @return the array which the values were written to
     */
    public double[] get2d(double x, double y, int sizeX, int sizeY, double distX, double distY, double[] d) {
        if (d == null) {
            d = new double[sizeX * sizeY];
        } else if (d.length != sizeX * sizeY) {
            throw new IllegalStateException("Array must be same length as size!");
        }

        for (int X = 0; X < sizeX; X++, x += distX) {
            for (int Y = 0; Y < sizeY; Y++, y += distY) {
                d[X * sizeY + Y] = this.get(x, y);
            }
        }

        return d;
    }

    /**
     * Gets all 3d noise values in a range and writes them to an array
     *
     * @param x     the x coordinate to start at
     * @param y     the y coordinate to start at
     * @param z     the z coordinate to start at
     * @param sizeX the number of samples to take on the X axis
     * @param sizeY the number of samples to take on the Y axis
     * @param sizeZ the number of samples to take on the Z axis
     * @param distX the space between samples on the Y axis
     * @param distY the space between samples on the Y axis
     * @param distZ the space between samples on the Z axis
     * @param d     the array to write to. a new one will be created and returned if null is passed here.
     * @return the array which the values were written to
     */
    public double[] get3d(double x, double y, double z, int sizeX, int sizeY, int sizeZ, double distX, double distY, double distZ, double[] d) {
        if (d == null) {
            d = new double[sizeX * sizeY * sizeZ];
        } else if (d.length != sizeX * sizeY * sizeZ) {
            throw new IllegalStateException("Array must be same length as size!");
        }

        for (int X = 0; X < sizeX; X++, x += distX) {
            for (int Y = 0; Y < sizeY; Y++, y += distY) {
                for (int Z = 0; Z < sizeZ; Z++, z += distZ) {
                    d[(X * sizeY + Y) * sizeZ + Z] = this.get(x, y, z);
                }
            }
        }

        return d;
    }

    /**
     * Gets all 4d noise values in a range and writes them to an array
     *
     * @param x     the x coordinate to start at
     * @param y     the y coordinate to start at
     * @param z     the z coordinate to start at
     * @param w     the w coordinate to start at
     * @param sizeX the number of samples to take on the X axis
     * @param sizeY the number of samples to take on the Y axis
     * @param sizeZ the number of samples to take on the Z axis
     * @param sizeW the number of samples to take on the W axis
     * @param distX the space between samples on the Y axis
     * @param distY the space between samples on the Y axis
     * @param distZ the space between samples on the Z axis
     * @param distW the space between samples on the W axisW
     * @param d     the array to write to. a new one will be created and returned if null is passed here.
     * @return the array which the values were written to
     */
    public double[] get4d(double x, double y, double z, double w, int sizeX, int sizeY, int sizeZ, int sizeW, double distX, double distY, double distZ, double distW, double[] d) {
        if (d == null) {
            d = new double[sizeX * sizeY * sizeZ * sizeW];
        } else if (d.length != sizeX * sizeY * sizeZ * sizeW) {
            throw new IllegalStateException("Array must be same length as size!");
        }

        for (int X = 0; X < sizeX; X++, x += distX) {
            for (int Y = 0; Y < sizeY; Y++, y += distY) {
                for (int Z = 0; Z < sizeZ; Z++, z += distZ) {
                    for (int W = 0; W < sizeW; W++, w += distW) {
                        d[((X * sizeY + Y) * sizeZ + Z) * sizeW + W] = this.get(x, y, z, w);
                    }
                }
            }
        }

        return d;
    }

    /**
     * Iterates over all 1D noise values in a range, linearly interpolating between values
     *
     * @param x        the x offset
     * @param sizeX    the total number of samples to take on the x axis
     * @param distX    the number of values to take before the next sample. the values between
     *                 will be calculated using linear interpolation of the neighboring
     *                 values.
     * @param consumer the function to execute on all the noise values.
     */
    public void forEach(double x, int sizeX, int distX, @NonNull IntDoubleConsumer consumer) {
        if (sizeX / distX == 0) {
            throw new IllegalArgumentException("sizeX / distX may not be 0! (distX must be a multiple of sizeX)");
        }
        int dXi = sizeX / distX;
        double[] dd = new double[dXi + 1];
        for (int a = 0; a < dd.length; a++) {
            dd[a] = this.get(x + a * distX);
        }
        for (int a = 0; a < dXi; a++) {
            int e = a * distX;
            double valX = dd[a];
            double incrX = (dd[a + 1] - valX) / (double) distX;
            for (int m = 0; m < distX; m++) {
                consumer.accept(e + m, valX + incrX * m);
            }
        }
    }

    /**
     * Iterates over all 2D noise values in a range, linearly interpolating between values
     *
     * @param x        the x offset
     * @param y        the y offset
     * @param sizeX    the total number of samples to take on the x axis
     * @param sizeY    the total number of samples to take on the y axis
     * @param distX    the number of values to take before the next sample. the values between
     *                 will be calculated using linear interpolation of the neighboring
     *                 values.
     * @param distY    the number of values to take before the next sample. the values between
     *                 will be calculated using linear interpolation of the neighboring
     *                 values.
     * @param consumer the function to execute on all the noise values.
     */
    public void forEach(double x, double y, int sizeX, int sizeY, int distX, int distY, @NonNull IntIntDoubleConsumer consumer) {
        if (sizeX / distX == 0) {
            throw new IllegalArgumentException("sizeX / distX may not be 0! (distX must be a multiple of sizeX)");
        }
        if (sizeY / distY == 0) {
            throw new IllegalArgumentException("sizeY / distY may not be 0! (distY must be a multiple of sizeY)");
        }

        int dXi = sizeX / distX;
        int dYi = sizeY / distY;
        double[][] dd = new double[dXi + 1][dYi + 1];

        for (int a = 0; a < dd.length; a++) {
            double[] ddA = dd[a];
            for (int b = 0; b < ddA.length; b++) {
                ddA[b] = this.get(x + a * distX, y + b * distY);
            }
        }

        for (int a = 0; a < dXi; a++) {
            int e = a * distX;
            for (int b = 0; b < dYi; b++) {
                int f = b * distY;
                double val1 = dd[a][b];
                double val2 = dd[a][b + 1];
                double val3 = (dd[a + 1][b] - val1) / (double) distX;
                double val4 = (dd[a + 1][b + 1] - val2) / (double) distX;

                for (int m = 0; m < distX; m++) {
                    double incr1Y = (val2 - val1) / (double) distY;
                    double incr2Y = val1 - incr1Y;

                    for (int n = 0; n < distY; n++) {
                        consumer.accept(e + m, f + n, incr2Y);
                        incr2Y += incr1Y;
                    }

                    val1 += val3;
                    val2 += val4;
                }
            }
        }
    }

    /**
     * Iterates over all 3D noise values in a range, linearly interpolating between values
     *
     * @param x        the x offset
     * @param y        the y offset
     * @param z        the z offset
     * @param sizeX    the total number of samples to take on the x axis
     * @param sizeY    the total number of samples to take on the y axis
     * @param sizeZ    the total number of samples to take on the z axis
     * @param distX    the number of values to take before the next sample. the values between
     *                 will be calculated using linear interpolation of the neighboring
     *                 values.
     * @param distY    the number of values to take before the next sample. the values between
     *                 will be calculated using linear interpolation of the neighboring
     *                 values.
     * @param distZ    the number of values to take before the next sample. the values between
     *                 will be calculated using linear interpolation of the neighboring
     *                 values.
     * @param consumer the function to execute on all the noise values.
     */
    public void forEach(double x, double y, double z, int sizeX, int sizeY, int sizeZ, int distX, int distY, int distZ, @NonNull IntIntIntDoubleConsumer consumer) {
        if (sizeX / distX == 0) {
            throw new IllegalArgumentException("sizeX / distX may not be 0! (distX must be a multiple of sizeX)");
        }
        if (sizeY / distY == 0) {
            throw new IllegalArgumentException("sizeY / distY may not be 0! (distY must be a multiple of sizeY)");
        }
        if (sizeZ / distZ == 0) {
            throw new IllegalArgumentException("sizeZ / distZ may not be 0! (distZ must be a multiple of sizeZ)");
        }

        int dXi = sizeX / distX;
        int dYi = sizeY / distY;
        int dZi = sizeZ / distZ;
        double[][][] dd = new double[dXi + 1][dYi + 1][dZi + 1];

        for (int a = 0; a < dd.length; a++) {
            double[][] ddA = dd[a];
            for (int b = 0; b < ddA.length; b++) {
                double[] ddB = ddA[b];
                for (int c = 0; c < ddB.length; c++) {
                    ddB[c] = this.get(x + a * distX, y + b * distY, z + c * distZ);
                }
            }
        }

        for (int a = 0; a < dXi; a++) {
            int e = a * distX;
            for (int b = 0; b < dYi; b++) {
                int f = b * distY;
                for (int c = 0; c < dZi; c++) {
                    int g = c * distZ;

                    double val1 = dd[a][b][c];
                    double val2 = dd[a + 1][b][c];
                    double val3 = dd[a][b][c + 1];
                    double val4 = dd[a + 1][b][c + 1];
                    double val5 = (dd[a][b + 1][c] - val1) / (double) distY;
                    double val6 = (dd[a + 1][b + 1][c] - val2) / (double) distY;
                    double val7 = (dd[a][b + 1][c + 1] - val3) / (double) distY;
                    double val8 = (dd[a + 1][b + 1][c + 1] - val4) / (double) distY;

                    for (int m = 0; m < distY; m++) {
                        double baseIncr = val1;
                        double baseIncr2 = val2;
                        double scaleY = (val3 - val1) / (double) distZ;
                        double scaleY2 = (val4 - val2) / (double) distZ;

                        for (int n = 0; n < distZ; n++) {
                            double scaleZ = (baseIncr2 - baseIncr) / (double) distX;
                            double scaleZ2 = baseIncr - scaleZ;

                            for (int o = 0; o < distX; o++) {
                                consumer.accept(e + o, f + m, g + n, scaleZ2);
                                scaleZ2 += scaleZ;
                            }

                            baseIncr += scaleY;
                            baseIncr2 += scaleY2;
                        }

                        val1 += val5;
                        val2 += val6;
                        val3 += val7;
                        val4 += val8;
                    }
                }
            }
        }
    }
}
