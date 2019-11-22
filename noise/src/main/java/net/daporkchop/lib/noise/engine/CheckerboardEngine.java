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

import lombok.Getter;

import static net.daporkchop.lib.math.primitive.PMath.*;

/**
 * @author DaPorkchop_
 */
public class CheckerboardEngine implements INoiseEngine {
    @Getter
    private volatile long seed;

    public CheckerboardEngine(long seed) {
        this.seed = seed;
    }

    @Override
    public double get(double x) {
        return (floorI(x) & 1) != 0 ? -1.0d : 1.0d;
    }

    @Override
    public double get(double x, double y) {
        return (floorI(x) & 1 ^ floorI(y) & 1) != 0 ? -1.0d : 1.0d;
    }

    @Override
    public double get(double x, double y, double z) {
        return (floorI(x) & 1 ^ floorI(y) & 1 ^ floorI(z) & 1) != 0 ? -1.0d : 1.0d;
    }

    @Override
    public double get(double x, double y, double z, double w) {
        return (floorI(x) & 1 ^ floorI(y) & 1 ^ floorI(z) & 1 ^ floorI(w) & 1) != 0 ? -1.0d : 1.0d;
    }

    @Override
    public void setSeed(long seed) {
        this.seed = seed;
    }
}
