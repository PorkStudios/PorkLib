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

package net.daporkchop.lib.gui.component.orientation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.component.SubElement;
import net.daporkchop.lib.gui.util.math.BoundingBox;

import static net.daporkchop.lib.math.primitive.PMath.floorI;

/**
 * An implementation of {@link Orientation} that uses 4 separate functions to calculate the element's x,
 * y, width and height, respectively.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
public class SimpleDynamicOrientation<Impl extends SubElement<Impl>> implements Orientation<Impl> {
    /**
     * Creates a {@link SimpleDynamicOrientation} that uses 4 values for x, y, width and height, respectively.
     * <p>
     * Each value may be one of three types:
     * - a {@link SingleValueCalculator}: if this is given, the calculator will be used as normal
     * - an int:                          if this is given, it will be treated as an absolute coordinate
     * - a double:                        if this is given, it will be treated as a percentage (i.e. using 0.5d as the width would make it be half the width of the parent element)
     * <p>
     * If none of the above types, an exception will be thrown.
     * <p>
     * Note that when a double is used, x and y will use the parent element's width and height rather than
     * x and y, respectively.
     *
     * @param x      the element's x position
     * @param y      the element's y position
     * @param width  the element's width
     * @param height the element's height
     * @param <T>    the type of the element. Can generally be ignored
     * @return an instance of {@link SimpleDynamicOrientation} with the given settings
     */
    @SuppressWarnings("unchecked")
    public static <T extends SubElement<T>> SimpleDynamicOrientation<T> of(@NonNull Object x, @NonNull Object y, @NonNull Object width, @NonNull Object height) {
        return new SimpleDynamicOrientation<T>().setX(x).setY(y).setWidth(width).setHeight(height);
    }

    @NonNull
    protected SingleValueCalculator<Impl> x;

    @NonNull
    protected SingleValueCalculator<Impl> y;

    @NonNull
    protected SingleValueCalculator<Impl> width;

    @NonNull
    protected SingleValueCalculator<Impl> height;

    @SuppressWarnings("unchecked")
    public SimpleDynamicOrientation<Impl> setX(@NonNull Object x) {
        if (x instanceof Integer) {
            return this.setX((int) x);
        } else if (x instanceof Double || x instanceof Float) {
            return this.setX((double) x);
        } else if (x instanceof SingleValueCalculator) {
            return this.setX((SingleValueCalculator<Impl>) x);
        } else {
            throw new IllegalArgumentException(String.format("Invalid x value: %s", x.getClass().getCanonicalName()));
        }
    }

    public SimpleDynamicOrientation<Impl> setX(int x) {
        return this.setX((bb, parent, component) -> x);
    }

    public SimpleDynamicOrientation<Impl> setX(double xMult) {
        return this.setX((bb, parent, component) -> floorI(xMult * bb.getWidth()));
    }

    public SimpleDynamicOrientation<Impl> setX(@NonNull SingleValueCalculator<Impl> x) {
        this.x = x;
        return this;
    }

    @SuppressWarnings("unchecked")
    public SimpleDynamicOrientation<Impl> setY(@NonNull Object y) {
        if (y instanceof Integer) {
            return this.setY((int) y);
        } else if (y instanceof Double || y instanceof Float) {
            return this.setY((double) y);
        } else if (y instanceof SingleValueCalculator) {
            return this.setY((SingleValueCalculator<Impl>) y);
        } else {
            throw new IllegalArgumentException(String.format("Invalid y value: %s", y.getClass().getCanonicalName()));
        }
    }

    public SimpleDynamicOrientation<Impl> setY(int y) {
        return this.setY((bb, parent, component) -> y);
    }

    public SimpleDynamicOrientation<Impl> setY(double yMult) {
        return this.setY((bb, parent, component) -> floorI(yMult * bb.getHeight()));
    }

    public SimpleDynamicOrientation<Impl> setY(@NonNull SingleValueCalculator<Impl> y) {
        this.y = y;
        return this;
    }

    @SuppressWarnings("unchecked")
    public SimpleDynamicOrientation<Impl> setWidth(@NonNull Object width) {
        if (width instanceof Integer) {
            return this.setWidth((int) width);
        } else if (width instanceof Double || width instanceof Float) {
            return this.setWidth((double) width);
        } else if (width instanceof SingleValueCalculator) {
            return this.setWidth((SingleValueCalculator<Impl>) width);
        } else {
            throw new IllegalArgumentException(String.format("Invalid width value: %s", width.getClass().getCanonicalName()));
        }
    }

    public SimpleDynamicOrientation<Impl> setWidth(int width) {
        return this.setWidth((bb, parent, component) -> width);
    }

    public SimpleDynamicOrientation<Impl> setWidth(double widthMult) {
        return this.setWidth((bb, parent, component) -> floorI(widthMult * bb.getWidth()));
    }

    public SimpleDynamicOrientation<Impl> setWidth(@NonNull SingleValueCalculator<Impl> width) {
        this.width = width;
        return this;
    }

    @SuppressWarnings("unchecked")
    public SimpleDynamicOrientation<Impl> setHeight(@NonNull Object height) {
        if (height instanceof Integer) {
            return this.setHeight((int) height);
        } else if (height instanceof Double || height instanceof Float) {
            return this.setHeight((double) height);
        } else if (height instanceof SingleValueCalculator) {
            return this.setHeight((SingleValueCalculator<Impl>) height);
        } else {
            throw new IllegalArgumentException(String.format("Invalid height value: %s", height.getClass().getCanonicalName()));
        }
    }

    public SimpleDynamicOrientation<Impl> setHeight(int height) {
        return this.setHeight((bb, parent, component) -> height);
    }

    public SimpleDynamicOrientation<Impl> setHeight(double heightMult) {
        return this.setHeight((bb, parent, component) -> floorI(heightMult * bb.getHeight()));
    }

    public SimpleDynamicOrientation<Impl> setHeight(@NonNull SingleValueCalculator<Impl> height) {
        this.height = height;
        return this;
    }

    @Override
    public BoundingBox update(@NonNull BoundingBox bb, @NonNull Container parent, @NonNull Impl component) {
        return new BoundingBox(
                this.x.get(bb, parent, component),
                this.y.get(bb, parent, component),
                this.width.get(bb, parent, component),
                this.height.get(bb, parent, component)
        );
    }

    @FunctionalInterface
    public interface SingleValueCalculator<Impl extends SubElement<Impl>> {
        /**
         * Recalculates a single value for the bounding box of a component
         *
         * @param bb        the parent container's bounding box
         * @param parent    the parent container
         * @param component the component whose bounding box needs to be updated
         * @return a single value for the bounding box of the component
         */
        int get(@NonNull BoundingBox bb, @NonNull Container parent, @NonNull Impl component);
    }
}
