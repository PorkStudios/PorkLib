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

package net.daporkchop.lib.math.interpolation;

import lombok.NonNull;
import net.daporkchop.lib.math.arrays.grid.Grid1d;
import net.daporkchop.lib.math.arrays.grid.Grid2d;
import net.daporkchop.lib.math.arrays.grid.Grid3d;
import net.daporkchop.lib.math.interpolation.InterpolationEngine;

import static net.daporkchop.lib.math.primitive.PMath.floorI;

/**
 * never actually worked but i'm keeping this around since i spent so much time on it
 *
 * @author DaPorkchop_
 */
public class QuadraticInterpolationEngine implements InterpolationEngine {
    @Override
    public int requiredRadius() {
        return 2;
    }

    @Override
    public double getInterpolated(double x, @NonNull Grid1d grid) {
        return 0;
    }

    @Override
    public double getInterpolated(double x, double y, @NonNull Grid2d grid) {
        int xFloor = floorI(x);
        int yFloor = floorI(y);
        this.ensureInRange(xFloor, yFloor, grid);

        //x -= xFloor;
        //y -= yFloor;

        //double v1 = grid.getD(xFloor - 1, yFloor - 1);
        //double v3 = grid.getD(xFloor, yFloor - 1);
        //double v2 = grid.getD(xFloor + 1, yFloor - 1);

        //ok i'm trying to figure out this paper:
        //http://www.geocomputation.org/1999/082/gc_082.htm
        //i don't speak math so bear with me while i attempt to figure this out
        //cubic was easier because i got some code (https://www.paulinternet.nl/?page=bicubic) :(
        //probably gonna comment this all out later (to preserve it for posterity) and end up optimizing

        //values
        /*int off = 0;
        double a00 = grid.getD(xFloor - 1 + off, yFloor - 1 + off);
        double a10 = grid.getD(xFloor + off, yFloor - 1 + off);
        double a20 = grid.getD(xFloor + 1 + off, yFloor - 1 + off);
        double a01 = grid.getD(xFloor - 1 + off, yFloor + off);
        double a11 = grid.getD(xFloor + off, yFloor + off);
        double a21 = grid.getD(xFloor + 1 + off, yFloor + off);
        double a02 = grid.getD(xFloor - 1 + off, yFloor + 1 + off);
        double a12 = grid.getD(xFloor + off, yFloor + 1 + off);
        double a22 = grid.getD(xFloor + 1 + off, yFloor + 1 + off);*/

        /*
         * h7 h8 h9
         * h4 h5 h6
         * h1 h2 h3
         */
        /*double h1 = grid.getD(xFloor - 1, yFloor - 1);
        double h2 = grid.getD(xFloor, yFloor - 1);
        double h3 = grid.getD(xFloor + 1, yFloor - 1);
        double h4 = grid.getD(xFloor - 1, yFloor);
        double h5 = grid.getD(xFloor, yFloor);
        double h6 = grid.getD(xFloor + 1, yFloor);
        double h7 = grid.getD(xFloor - 1, yFloor + 1);
        double h8 = grid.getD(xFloor, yFloor + 1);
        double h9 = grid.getD(xFloor + 1, yFloor + 1);

        double a00 = h1;
        double a10 = h2 - h1;
        double a20 = h3 - h2 - h1;
        double a01 = h4 - h1;
        double a02 = h7 - h4 - h1;
        double a11 = h1 - h2 - h4 + h5;
        double a22 = (h1 - h2 - h4 + h5) - h8 - h6 + h9;
        double a12 = (h2 - h1) - h5 - h3 + h6;
        double a21 = (h4 - h1) - h7 - h5 + h8;

        return a00
                + a10 * x
                + a01 * y
                + a20 * x * x
                + a11 * x * y
                + a02 * y * y
                + a21 * x * x * y
                + a12 * x * y * y;
                //+ a22 * x * x * y * y;*/

        //at this point i covered half an A4 sheet of paper with random scribbles and made something that should work
        //here's a test in 1d
        /*double p0 = grid.getD(xFloor - 1, 0);
        double p1 = grid.getD(xFloor, 0);
        double p2 = grid.getD(xFloor + 1, 0);*/

        /*double p0 = grid.getD(xFloor, 0);
        double p2 = grid.getD(xFloor + 1, 0);
        double p1 = (p2 - p0) * x + p0;*/

        /*double p0 = grid.getD(xFloor, 0);
        double p1 = grid.getD(xFloor + 1, 0);
        double p2 = grid.getD(xFloor + 2, 0);*/

        //a=(p2-p0)/2-p2+p1
        //b=-0.5p0+0.5p2
        //c=p1
        //f(x)=ax²+bx+c
        //therefore:
        /*return x * x * ((p2 - p0) * 0.5d - p2 + p1)
                + x * (-0.5d * p0 + 0.5d * p2)
                + p1;*/

        //take 3
        //http://slideplayer.com/slide/4948480/16/images/10/Comparing+Linear+and+Quadratic+Interpolation.jpg
        double v0 = grid.getD(xFloor, 0);
        double v1 = grid.getD(xFloor + 1, 0);
        double v2 = grid.getD(xFloor + 2, 0);

        return (v1 + (x - xFloor) * (v2 - v1)) +
                (v2 - 2.0d * v1 - v0) * 0.5d * (x - xFloor) * (x - xFloor + 1);
        //still doesn't work :(
    }

    @Override
    public double getInterpolated(double x, double y, double z, @NonNull Grid3d grid) {
        return 0;
    }
}
