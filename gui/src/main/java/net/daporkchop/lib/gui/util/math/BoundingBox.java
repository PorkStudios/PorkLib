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

package net.daporkchop.lib.gui.util.math;

import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

/**
 * A 2D bounding box
 *
 * @author DaPorkchop_
 */
@Data
@ToString
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
