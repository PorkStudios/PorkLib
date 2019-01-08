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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static net.daporkchop.lib.math.primitive.PMath.floorI;

/**
 * @author DaPorkchop_
 */
@AllArgsConstructor
public class PorkianEngine implements INoiseEngine {
    private static double shrinkFactor = 1.0d / 128.0d;

    private static double s(double t) {
        return t * t * t * (t * (t * 6.0d - 15.0d) + 10.0d);
    }
    @Getter
    @Setter
    private volatile long seed;

    @Override
    public double get(double x) {
        int xI = floorI(x);
        x = PorkianEngine.s(x - xI);
        return (this.rand(xI) * (1 - x) + this.rand(xI + 1) * x) * shrinkFactor - 1.0d;
    }

    @Override
    public double get(double x, double y) {
        int xI = floorI(x);
        int yI = floorI(y);
        x = PorkianEngine.s(x - xI);
        y = PorkianEngine.s(y - yI);
        return ((this.rand(xI, yI) * (1 - x) + this.rand(xI + 1, yI) * x) * (1 - y) +
                (this.rand(xI, yI + 1) * (1 - x) + this.rand(xI + 1, yI + 1) * x) * y) * shrinkFactor - 1.0d;
    }

    @Override
    public double get(double x, double y, double z) {
        int xI = floorI(x);
        int yI = floorI(y);
        int zI = floorI(z);
        x = PorkianEngine.s(x - xI);
        y = PorkianEngine.s(y - yI);
        z = PorkianEngine.s(z - zI);
        return (((this.rand(xI, yI, zI) * (1 - x) + this.rand(xI + 1, yI, zI) * x) * (1 - y) +
                (this.rand(xI, yI + 1, zI) * (1 - x) + this.rand(xI + 1, yI + 1, zI) * x) * y) * (1 - z) +
                ((this.rand(xI, yI, zI + 1) * (1 - x) + this.rand(xI + 1, yI, zI + 1) * x) * (1 - y) +
                        (this.rand(xI, yI + 1, zI + 1) * (1 - x) + this.rand(xI + 1, yI + 1, zI + 1) * x) * y) * z) * shrinkFactor - 1.0d;
    }

    @Override
    public double get(double x, double y, double z, double w) {
        int xI = floorI(x);
        int yI = floorI(y);
        int zI = floorI(z);
        int wI = floorI(w);
        x = PorkianEngine.s(x - xI);
        y = PorkianEngine.s(y - yI);
        z = PorkianEngine.s(z - zI);
        w = PorkianEngine.s(w - wI);
        return ((((this.rand(xI, yI, zI, wI) * (1 - x) + this.rand(xI + 1, yI, zI, wI) * x) * (1 - y) +
                (this.rand(xI, yI + 1, zI, wI) * (1 - x) + this.rand(xI + 1, yI + 1, zI, wI) * x) * y) * (1 - z) +
                ((this.rand(xI, yI, zI + 1, wI) * (1 - x) + this.rand(xI + 1, yI, zI + 1, wI) * x) * (1 - y) +
                        (this.rand(xI, yI + 1, zI + 1, wI) * (1 - x) + this.rand(xI + 1, yI + 1, zI + 1, wI) * x) * y) * z) * (1 - w) +
                (((this.rand(xI, yI, zI, wI + 1) * (1 - x) + this.rand(xI + 1, yI, zI, wI + 1) * x) * (1 - y) +
                        (this.rand(xI, yI + 1, zI, wI + 1) * (1 - x) + this.rand(xI + 1, yI + 1, zI, wI + 1) * x) * y) * (1 - z) +
                        ((this.rand(xI, yI, zI + 1, wI + 1) * (1 - x) + this.rand(xI + 1, yI, zI + 1, wI + 1) * x) * (1 - y) +
                                (this.rand(xI, yI + 1, zI + 1, wI + 1) * (1 - x) + this.rand(xI + 1, yI + 1, zI + 1, wI + 1) * x) * y) * z) * w) * shrinkFactor - 1.0d;
    }

    private double rand(int x) {
        long h = this.seed + x * 3729677702399770201L;
        h = (h ^ (h >> 26)) * 9002571511049629897L;
        return ((h ^ (h >> 32)) & 0xFF) + (h & 1);
    }

    private double rand(int x, int y) {
        long h = this.seed + x * 3729677702399770201L + y * 5021922074834582501L;
        h = (h ^ (h >> 26)) * 9002571511049629897L;
        return ((h ^ (h >> 32)) & 0xFF) + (h & 1);
    }

    private double rand(int x, int y, int z) {
        long h = this.seed + x * 3729677702399770201L + y * 5021922074834582501L + z * 2909220169413884401L;
        h = (h ^ (h >> 26)) * 9002571511049629897L;
        return ((h ^ (h >> 32)) & 0xFF) + (h & 1);
    }

    private double rand(int x, int y, int z, int w) {
        long h = this.seed + x * 3729677702399770201L + y * 5021922074834582501L + z * 2909220169413884401L + w * 7450524880328570311L;
        h = (h ^ (h >> 26)) * 9002571511049629897L;
        return ((h ^ (h >> 32)) & 0xFF) + (h & 1);
    }
}
