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

package net.daporkchop.lib.noise;

import lombok.NonNull;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.noise.engine.NoopNoiseEngine;
import net.daporkchop.lib.noise.filter.LerpFilter;
import net.daporkchop.lib.noise.filter.OctaveFilter;
import net.daporkchop.lib.noise.filter.ScaleFilter;
import net.daporkchop.lib.noise.filter.WeightedFilter;
import net.daporkchop.lib.noise.filter.math.ScalarAddFilter;
import net.daporkchop.lib.noise.filter.math.ScalarMulFilter;
import net.daporkchop.lib.noise.filter.math.ScalarSubFilter;
import net.daporkchop.lib.noise.filter.math.SourceAddFilter;
import net.daporkchop.lib.noise.filter.math.SourceMulFilter;
import net.daporkchop.lib.noise.filter.math.SourceSubFilter;
import net.daporkchop.lib.noise.filter.range.RangeConversionFilter;

/**
 * A source for obtaining noise values.
 *
 * @author DaPorkchop_
 */
public interface NoiseSource {
    NoopNoiseEngine ZERO = new NoopNoiseEngine(0.0d, 0.0d, 1.0d);

    //range accessors

    /**
     * @return the minimum value that can be returned by this source
     */
    default double min() {
        return -1.0d;
    }

    /**
     * @return the maximum value that can be returned by this source
     */
    default double max() {
        return 1.0d;
    }

    //single value methods

    /**
     * Gets the noise value at the given 1D position.
     *
     * @param x the X position
     * @return the noise value at the given position
     */
    double get(double x);

    /**
     * Gets the noise value at the given 2D position.
     *
     * @param x the X position
     * @param y the Y position
     * @return the noise value at the given position
     */
    double get(double x, double y);

    /**
     * Gets the noise value at the given 3D position.
     *
     * @param x the X position
     * @param y the Y position
     * @param z the Z position
     * @return the noise value at the given position
     */
    double get(double x, double y, double z);

    //bulk generation methods

    /**
     * Gets a {@code double[]} filled with 1D noise values.
     *
     * @see #get(double[], double, double, int)
     */
    default double[] get(double startX, double stepX, int sizeX) {
        return this.get(null, startX, stepX, sizeX);
    }

    /**
     * Fills the given {@code double[]} with 1D noise values.
     *
     * @param dst    the {@code double[]} to store values into. If {@code null} or too small, a new one will be created and returned
     * @param startX the initial X position to start generating values at
     * @param stepX  the spacing along the X axis between samples
     * @param sizeX  the number of samples to take along the X axis
     * @return a {@code double[]} containing the computed values
     */
    default double[] get(double[] dst, double startX, double stepX, int sizeX) {
        if (dst == null || dst.length < PValidation.notNegative(sizeX)) {
            dst = new double[sizeX];
        }

        double x = startX;
        for (int currX = 0; currX < sizeX; currX++, x += stepX) {
            dst[currX] = this.get(x);
        }

        return dst;
    }

    /**
     * Gets a {@code double[]} filled with 2D noise values.
     *
     * @see #get(double[], double, double, double, double, int, int)
     */
    default double[] get(double startX, double startY, double stepX, double stepY, int sizeX, int sizeY) {
        return this.get(null, startX, startY, stepX, stepY, sizeX, sizeY);
    }

    /**
     * Fills the given {@code double[]} with 2D noise values.
     * <p>
     * The values will be indexed as {@code x * sizeX + y}, where {@code 0 <= x < sizeX} and {@code 0 <= y < sizeY}.
     *
     * @param dst    the {@code double[]} to store values into. If {@code null} or too small, a new one will be created and returned
     * @param startX the initial X position to start generating values at
     * @param startY the initial Y position to start generating values at
     * @param stepX  the spacing along the X axis between samples
     * @param stepY  the spacing along the Y axis between samples
     * @param sizeX  the number of samples to take along the X axis
     * @param sizeY  the number of samples to take along the Y axis
     * @return a {@code double[]} containing the computed values
     */
    default double[] get(double[] dst, double startX, double startY, double stepX, double stepY, int sizeX, int sizeY) {
        if (dst == null || dst.length < PValidation.notNegative(sizeX) * PValidation.notNegative(sizeY)) {
            dst = new double[sizeX * sizeY];
        }

        int i = 0;
        double x = startX;
        for (int currX = 0; currX < sizeX; currX++, x += stepX) {
            double y = startY;
            for (int currY = 0; currY < sizeY; currY++, y += stepY) {
                dst[i++] = this.get(x, y);
            }
        }

        return dst;
    }

    /**
     * Gets a {@code double[]} filled with 3D noise values.
     *
     * @see #get(double[], double, double, double, double, double, double, int, int, int)
     */
    default double[] get(double startX, double startY, double startZ, double stepX, double stepY, double stepZ, int sizeX, int sizeY, int sizeZ) {
        return this.get(null, startX, startY, startZ, stepX, stepY, stepZ, sizeX, sizeY, sizeZ);
    }

    /**
     * Fills the given {@code double[]} with 3D noise values.
     * <p>
     * The values will be indexed as {@code (x * sizeX + y) * sizeY + z}, where {@code 0 <= x < sizeX} and {@code 0 <= y < sizeY} and {@code 0 <= z < sizeZ}.
     *
     * @param dst    the {@code double[]} to store values into. If {@code null} or too small, a new one will be created and returned
     * @param startX the initial X position to start generating values at
     * @param startY the initial Y position to start generating values at
     * @param startZ the initial Z position to start generating values at
     * @param stepX  the spacing along the X axis between samples
     * @param stepY  the spacing along the Y axis between samples
     * @param stepZ  the spacing along the Z axis between samples
     * @param sizeX  the number of samples to take along the X axis
     * @param sizeY  the number of samples to take along the Y axis
     * @param sizeZ  the number of samples to take along the Z axis
     * @return a {@code double[]} containing the computed values
     */
    default double[] get(double[] dst, double startX, double startY, double startZ, double stepX, double stepY, double stepZ, int sizeX, int sizeY, int sizeZ) {
        if (dst == null || dst.length < PValidation.notNegative(sizeX) * PValidation.notNegative(sizeY) * PValidation.notNegative(sizeZ)) {
            dst = new double[sizeX * sizeY * sizeZ];
        }

        int i = 0;
        double x = startX;
        for (int currX = 0; currX < sizeX; currX++, x += stepX) {
            double y = startY;
            for (int currY = 0; currY < sizeY; currY++, y += stepY) {
                double z = startZ;
                for (int currZ = 0; currZ < sizeZ; currZ++, z += stepZ) {
                    dst[i++] = this.get(x, y, z);
                }
            }
        }

        return dst;
    }

    //filtering methods

    /**
     * @return a {@link NoiseSource} that will interpolate linearly between {@code a} and {@code b} using this {@link NoiseSource} as the bias {@code t}
     * @see LerpFilter
     */
    default NoiseSource lerped(@NonNull NoiseSource a, @NonNull NoiseSource b) {
        return new LerpFilter(a, b, this);
    }

    /**
     * @return this {@link NoiseSource} with the given number of octaves of fractal brownian motion applied
     * @see OctaveFilter
     */
    default NoiseSource octaves(int octaves) {
        return octaves == 1 ? this : new OctaveFilter(this, octaves);
    }

    /**
     * @return this {@link NoiseSource} with the given scale factor applied to all input coordinates
     * @see ScaleFilter
     */
    default NoiseSource scaled(double scale) {
        return scale == 1.0d ? this : new ScaleFilter(this, scale, scale, scale);
    }

    /**
     * @return this {@link NoiseSource} with the given scale factor applied to all input coordinates
     * @see ScaleFilter
     */
    default NoiseSource scaled(double scaleX, double scaleY, double scaleZ) {
        return scaleX == 1.0d && scaleY == 1.0d && scaleZ == 1.0d ? this : new ScaleFilter(this, scaleX, scaleY, scaleZ);
    }

    /**
     * @return this {@link NoiseSource} with a weight applied to the values
     * @see WeightedFilter
     */
    default NoiseSource weighted() {
        return new WeightedFilter(this);
    }

    /**
     * @return this {@link NoiseSource} with the values scaled to the given range
     * @see RangeConversionFilter
     */
    default NoiseSource toRange(double min, double max) {
        return min == this.min() && max == this.max() ? this : new RangeConversionFilter(this, min, max);
    }

    /**
     * @return this {@link NoiseSource} with the given value added to the values
     * @see ScalarAddFilter
     */
    default NoiseSource add(double val) {
        return val == 0.0d ? this : new ScalarAddFilter(this, val);
    }

    /**
     * @return this {@link NoiseSource} with the values from the given {@link NoiseSource} value added to the values
     * @see SourceAddFilter
     */
    default NoiseSource add(@NonNull NoiseSource val) {
        return val == this ? this.mul(2.0d) : new SourceAddFilter(this, val);
    }

    /**
     * @return this {@link NoiseSource} with the given value subtracted from the values
     * @see ScalarSubFilter
     */
    default NoiseSource sub(double val) {
        return val == 0.0d ? this : new ScalarSubFilter(this, val);
    }

    /**
     * @return this {@link NoiseSource} with the values from the given {@link NoiseSource} value subtracted from the values
     * @see SourceSubFilter
     */
    default NoiseSource sub(@NonNull NoiseSource val) {
        return val == this ? ZERO : new SourceSubFilter(this, val);
    }

    /**
     * @return this {@link NoiseSource} with the given value multiplied with the values
     * @see ScalarMulFilter
     */
    default NoiseSource mul(double val) {
        if (val == 0.0d)    {
            return ZERO;
        } else if (val == 1.0d) {
            return this;
        } else {
            return new ScalarMulFilter(this, val);
        }
    }

    /**
     * @return this {@link NoiseSource} with the values from the given {@link NoiseSource} value multiplied with the values
     * @see SourceMulFilter
     */
    default NoiseSource mul(@NonNull NoiseSource val) {
        return new SourceMulFilter(this, val);
    }
}
