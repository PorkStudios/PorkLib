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
import lombok.NonNull;

/**
 * A 2D bounding box
 *
 * @author DaPorkchop_
 */
@Data
public class BoundingBox implements Pos<BoundingBox>, Size<BoundingBox> {
    protected final int x;
    protected final int y;
    protected final int width;
    protected final int height;

    public BoundingBox set(@NonNull Constraint constraint)  {
        if (constraint instanceof BoundingBox)  {
            return (BoundingBox) constraint;
        } else if (constraint instanceof Pos)   {
            Pos pos = (Pos) constraint;
            return new BoundingBox(pos.getX(), pos.getY(), this.width, this.height);
        } else if (constraint instanceof Size)   {
            Size size = (Size) constraint;
            return new BoundingBox(this.x, this.y, size.getWidth(), size.getHeight());
        } else {
            throw new IllegalArgumentException(String.format("Invalid constraint type: %s", constraint.getClass().getCanonicalName()));
        }
    }

    public BoundingBox add(int x, int y, int width, int height)   {
        return new BoundingBox(this.x + x, this.y + y,this.width + width, this.height + height);
    }

    public BoundingBox subtract(int x, int y, int width, int height)   {
        return new BoundingBox(this.x - x, this.y - y,this.width - width, this.height - height);
    }

    public BoundingBox multiply(int x, int y, int width, int height)   {
        return new BoundingBox(this.x * x, this.y * y,this.width * width, this.height * height);
    }

    public BoundingBox divide(int x, int y, int width, int height)   {
        return new BoundingBox(this.x / x, this.y / y,this.width / width, this.height / height);
    }

    @Override
    public BoundingBox addXY(int x, int y) {
        return new BoundingBox(this.x + x, this.y + y, this.width, this.height);
    }

    @Override
    public BoundingBox multiplyXY(int x, int y) {
        return new BoundingBox(this.x * x, this.y * y, this.width, this.height);
    }

    @Override
    public BoundingBox divideXY(int x, int y) {
        return new BoundingBox(this.x / x, this.y / y, this.width, this.height);
    }

    @Override
    public BoundingBox addWH(int width, int height) {
        return new BoundingBox(this.x, this.y, this.width + width, this.height + height);
    }

    @Override
    public BoundingBox multiplyWH(int width, int height) {
        return new BoundingBox(this.x, this.y, this.width * width, this.height * height);
    }

    @Override
    public BoundingBox divideWH(int width, int height) {
        return new BoundingBox(this.x, this.y, this.width / width, this.height / height);
    }

    @Override
    public boolean hasXY() {
        return true;
    }

    @Override
    public boolean hasWH() {
        return true;
    }
}
