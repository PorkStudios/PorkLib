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

import lombok.NonNull;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.Container;

import static net.daporkchop.lib.math.primitive.PMath.floorI;

/**
 * Calculates a new bounding box for a component
 *
 * @author DaPorkchop_
 */
@FunctionalInterface
public interface ComponentUpdater<Impl extends Component> {
    @SuppressWarnings("unchecked")
    static <Impl extends Component> ComponentUpdater<Impl> of(@NonNull Object x, @NonNull Object y, @NonNull Object width, @NonNull Object height) {
        if (x instanceof Integer) {
            int i = (int) x;
            x = (SingleValueCalculator<Impl>) (bb, parent, component) -> i;
        } else if (x instanceof Double) {
            double d = (double) x;
            x = (SingleValueCalculator<Impl>) (bb, parent, component) -> floorI(bb.getX() * d);
        } else if (!(x instanceof SingleValueCalculator)) {
            throw new IllegalStateException(String.format("Invalid x type: %s", x == null ? "null" : x.getClass().getCanonicalName()));
        }

        if (y instanceof Integer) {
            int i = (int) y;
            y = (SingleValueCalculator<Impl>) (bb, parent, component) -> i;
        } else if (y instanceof Double) {
            double d = (double) y;
            y = (SingleValueCalculator<Impl>) (bb, parent, component) -> floorI(bb.getY() * d);
        } else if (!(y instanceof SingleValueCalculator)) {
            throw new IllegalStateException(String.format("Invalid y type: %s", y == null ? "null" : y.getClass().getCanonicalName()));
        }

        if (width instanceof Integer) {
            int i = (int) width;
            width = (SingleValueCalculator<Impl>) (bb, parent, component) -> i;
        } else if (width instanceof Double) {
            double d = (double) width;
            width = (SingleValueCalculator<Impl>) (bb, parent, component) -> floorI(bb.getY() * d);
        } else if (!(width instanceof SingleValueCalculator)) {
            throw new IllegalStateException(String.format("Invalid width type: %s", width == null ? "null" : width.getClass().getCanonicalName()));
        }

        if (height instanceof Integer) {
            int i = (int) height;
            height = (SingleValueCalculator<Impl>) (bb, parent, component) -> i;
        } else if (height instanceof Double) {
            double d = (double) height;
            height = (SingleValueCalculator<Impl>) (bb, parent, component) -> floorI(bb.getY() * d);
        } else if (!(height instanceof SingleValueCalculator)) {
            throw new IllegalStateException(String.format("Invalid height type: %s", height == null ? "null" : height.getClass().getCanonicalName()));
        }

        return of((SingleValueCalculator<Impl>) x, (SingleValueCalculator<Impl>) y, (SingleValueCalculator<Impl>) width, (SingleValueCalculator<Impl>) height);
    }

    static <Impl extends Component> ComponentUpdater<Impl> of(@NonNull SingleValueCalculator<Impl> x, @NonNull SingleValueCalculator<Impl> y, @NonNull SingleValueCalculator<Impl> width, @NonNull SingleValueCalculator<Impl> height) {
        return (bb, parent, component) -> new BoundingBox(x.get(bb, parent, component), y.get(bb, parent, component), width.get(bb, parent, component), height.get(bb, parent, component));
    }

    /**
     * Recalculates the bounding box of the component
     *
     * @param bb        the parent container's bounding box
     * @param parent    the parent container
     * @param component the component whose bounding box needs to be updated
     * @return the new bounding box for the component
     */
    BoundingBox update(@NonNull BoundingBox bb, @NonNull Container parent, @NonNull Impl component);

    @FunctionalInterface
    interface SingleValueCalculator<Impl extends Component> {
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
