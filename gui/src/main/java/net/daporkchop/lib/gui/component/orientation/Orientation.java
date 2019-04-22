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

import lombok.NonNull;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.util.math.BoundingBox;

import static net.daporkchop.lib.math.primitive.PMath.floorI;

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
