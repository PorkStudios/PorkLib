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

import net.daporkchop.lib.math.vector.d.DoubleVector2;
import net.daporkchop.lib.math.vector.d.DoubleVector3;

/**
 * Represents an algorithm capable of generating noise values
 *
 * @author DaPorkchop_
 */
public interface INoiseEngine {
    /**
     * Generates a 1D noise value at the given coordinate
     *
     * @param x the x coordinate
     * @return a noise value normalized between -1 and 1
     */
    double get(double x);

    /**
     * Generates a 2D noise value at the given coordinates
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return a noise value normalized between -1 and 1
     */
    double get(double x, double y);

    default double get(DoubleVector2 vector) {
        return this.get(vector.getX(), vector.getY());
    }


    /**
     * Generates a 3D noise value at the given coordinates
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return a noise value normalized between -1 and 1
     */
    double get(double x, double y, double z);

    default double get(DoubleVector3 vector) {
        return this.get(vector.getX(), vector.getY(), vector.getZ());
    }

    /**
     * Generates a 4D noise value at the given coordinates
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @param w the w coordinate
     * @return a noise value normalized between -1 and 1
     */
    double get(double x, double y, double z, double w);

    /**
     * Gets the current seed used for generation
     *
     * @return the current seed used for generation
     */
    long getSeed();

    /**
     * Sets the noise generation seed to a new value.
     * Other threads using this engine won't see the updated seed until
     * this method returns.
     * Implementing classes are advised to have this method be synchronized.
     *
     * @param seed the new seed to use
     */
    void setSeed(long seed);
}
