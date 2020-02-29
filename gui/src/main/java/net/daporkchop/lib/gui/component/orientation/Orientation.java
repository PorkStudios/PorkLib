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

package net.daporkchop.lib.gui.component.orientation;

import lombok.NonNull;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.util.math.BoundingBox;

import static net.daporkchop.lib.math.primitive.PMath.*;

/**
 * TODO: come up with a better name for this class
 * The {@link Orientation} of a {@link Component} handles updating the
 * position and size of the sub-element when it's updated.
 *
 * @author DaPorkchop_
 */
@FunctionalInterface
public interface Orientation<Impl extends Component> {
    /**
     * Recalculates the bounding box of the component
     *
     * @param bb        the parent container's bounding box
     * @param parent    the parent container
     * @param component the component whose bounding box needs to be updated
     * @return the new bounding box for the component
     */
    BoundingBox update(@NonNull BoundingBox bb, @NonNull Container parent, @NonNull Impl component);

    default BoundingBox getMin(@NonNull BoundingBox bb, @NonNull Container parent, @NonNull Impl component) {
        return null;
    }
}
