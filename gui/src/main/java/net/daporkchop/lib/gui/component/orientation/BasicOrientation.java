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
import lombok.Setter;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.component.SubElement;
import net.daporkchop.lib.gui.util.math.BoundingBox;

import static net.daporkchop.lib.math.primitive.PMath.floorI;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
public class BasicOrientation<Impl extends SubElement<Impl>> implements Orientation<Impl> {
    @SuppressWarnings("unchecked")
    static <Impl extends SubElement<Impl>> Orientation<Impl> of(@NonNull Object x, @NonNull Object y, @NonNull Object width, @NonNull Object height) {
        if (x instanceof Integer) {
            int i = (int) x;
            x = (BasicOrientation.SingleValueCalculator<Impl>) (bb, parent, component) -> i;
        } else if (x instanceof Double) {
            double d = (double) x;
            x = (BasicOrientation.SingleValueCalculator<Impl>) (bb, parent, component) -> floorI(bb.getX() * d);
        } else if (!(x instanceof BasicOrientation.SingleValueCalculator)) {
            throw new IllegalStateException(String.format("Invalid x type: %s", x == null ? "null" : x.getClass().getCanonicalName()));
        }

        if (y instanceof Integer) {
            int i = (int) y;
            y = (BasicOrientation.SingleValueCalculator<Impl>) (bb, parent, component) -> i;
        } else if (y instanceof Double) {
            double d = (double) y;
            y = (BasicOrientation.SingleValueCalculator<Impl>) (bb, parent, component) -> floorI(bb.getY() * d);
        } else if (!(y instanceof BasicOrientation.SingleValueCalculator)) {
            throw new IllegalStateException(String.format("Invalid y type: %s", y == null ? "null" : y.getClass().getCanonicalName()));
        }

        if (width instanceof Integer) {
            int i = (int) width;
            width = (BasicOrientation.SingleValueCalculator<Impl>) (bb, parent, component) -> i;
        } else if (width instanceof Double) {
            double d = (double) width;
            width = (BasicOrientation.SingleValueCalculator<Impl>) (bb, parent, component) -> floorI(bb.getY() * d);
        } else if (!(width instanceof BasicOrientation.SingleValueCalculator)) {
            throw new IllegalStateException(String.format("Invalid width type: %s", width == null ? "null" : width.getClass().getCanonicalName()));
        }

        if (height instanceof Integer) {
            int i = (int) height;
            height = (BasicOrientation.SingleValueCalculator<Impl>) (bb, parent, component) -> i;
        } else if (height instanceof Double) {
            double d = (double) height;
            height = (BasicOrientation.SingleValueCalculator<Impl>) (bb, parent, component) -> floorI(bb.getY() * d);
        } else if (!(height instanceof BasicOrientation.SingleValueCalculator)) {
            throw new IllegalStateException(String.format("Invalid height type: %s", height == null ? "null" : height.getClass().getCanonicalName()));
        }

        return new BasicOrientation<>((BasicOrientation.SingleValueCalculator<Impl>) x, (BasicOrientation.SingleValueCalculator<Impl>) y, (BasicOrientation.SingleValueCalculator<Impl>) width, (BasicOrientation.SingleValueCalculator<Impl>) height);
    }

    @NonNull
    protected BasicOrientation.SingleValueCalculator<Impl> x;

    @NonNull
    protected BasicOrientation.SingleValueCalculator<Impl> y;

    @NonNull
    protected BasicOrientation.SingleValueCalculator<Impl> width;

    @NonNull
    protected BasicOrientation.SingleValueCalculator<Impl> height;

    @SuppressWarnings("unchecked")
    public BasicOrientation<Impl> setY(@NonNull Object y)    {
        if (y instanceof Integer)   {
            return this.setY((int) y);
        } else if (y instanceof Double || y instanceof Float) {
            return this.setY((double) y);
        } else if (y instanceof SingleValueCalculator)  {
            return this.setY((SingleValueCalculator<Impl>) y);
        } else {
            throw new IllegalArgumentException(String.format("Invalid y value: %s", y.getClass().getCanonicalName()));
        }
    }

    public BasicOrientation<Impl> setY(int y)    {
        return this.setY((bb, parent, component) -> y);
    }

    public BasicOrientation<Impl> setY(double yMult)    {
        return this.setY((bb, parent, component) -> floorI(yMult * bb.getY()));
    }

    public BasicOrientation<Impl> setY(@NonNull BasicOrientation.SingleValueCalculator<Impl> y)    {
        this.y = y;
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
