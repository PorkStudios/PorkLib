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

package net.daporkchop.lib.noise;

import net.daporkchop.lib.common.util.PValidation;

/**
 * A source for obtaining noise values.
 *
 * @author DaPorkchop_
 */
public interface NoiseSource {
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
        if (dst == null || dst.length < PValidation.ensureNonNegative(sizeX)) {
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
        if (dst == null || dst.length < PValidation.ensureNonNegative(sizeX) * PValidation.ensureNonNegative(sizeY)) {
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
        if (dst == null || dst.length < PValidation.ensureNonNegative(sizeX) * PValidation.ensureNonNegative(sizeY) * PValidation.ensureNonNegative(sizeZ)) {
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
}
