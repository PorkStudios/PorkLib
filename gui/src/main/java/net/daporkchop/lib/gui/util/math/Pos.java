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

package net.daporkchop.lib.gui.util.math;

import lombok.Data;

/**
 * A point in 2D space
 *
 * @author DaPorkchop_
 */
public interface Pos<Impl extends Pos> extends Constraint {
    static Pos at(int x, int y) {
        return new Default(x, y);
    }

    int getX();

    int getY();

    Impl addXY(int x, int y);

    default Impl addXY(int i) {
        return this.addXY(i, i);
    }

    default Impl subtractXY(int x, int y) {
        return this.addXY(-x, -y);
    }

    default Impl subtractXY(int i) {
        return this.addXY(-i, -i);
    }

    Impl multiplyXY(int x, int y);

    default Impl multiplyXY(int i) {
        return this.multiplyXY(i, i);
    }

    Impl divideXY(int x, int y);

    default Impl divideXY(int i) {
        return this.divideXY(i, i);
    }

    @Override
    default boolean hasXY() {
        return true;
    }

    @Override
    default boolean hasWH() {
        return false;
    }

    @Data
    class Default implements Pos<Default> {
        protected final int x;
        protected final int y;

        @Override
        public Default addXY(int x, int y) {
            return new Default(this.x + x, this.y + y);
        }

        @Override
        public Default multiplyXY(int x, int y) {
            return new Default(this.x * x, this.y * y);
        }

        @Override
        public Default divideXY(int x, int y) {
            return new Default(this.x / x, this.y / y);
        }
    }
}
