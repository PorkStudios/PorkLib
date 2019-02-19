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

package net.daporkchop.lib.gui.component;

import lombok.NonNull;
import net.daporkchop.lib.gui.component.capability.Enableable;
import net.daporkchop.lib.gui.component.orientation.Orientation;
import net.daporkchop.lib.gui.component.orientation.SimpleDynamicOrientation;
import net.daporkchop.lib.gui.component.orientation.StaticOrientation;
import net.daporkchop.lib.gui.util.HorizontalAlignment;
import net.daporkchop.lib.gui.util.VerticalAlignment;
import net.daporkchop.lib.gui.util.math.BoundingBox;
import net.daporkchop.lib.gui.util.math.Constraint;

/**
 * @author DaPorkchop_
 */
@SuppressWarnings("unchecked")
public interface Component<Impl extends Component> extends Element<Impl>, Enableable<Impl> {
    @Override
    default Impl setBounds(@NonNull BoundingBox bounds) {
        return this.setOrientation(bounds);
    }

    Orientation<Impl> getOrientation();
    Impl setOrientation(@NonNull Orientation<Impl> orientation);

    default String getText()    {
        return "";
    }
    default Impl setText(@NonNull String text)  {
        return (Impl) this;
    }

    default VerticalAlignment getVerticalTextAlignment()    {
        return VerticalAlignment.CENTER;
    }
    default Impl setVerticalTextAlignment(@NonNull VerticalAlignment alignment) {
        return (Impl) this;
    }

    default HorizontalAlignment getHorizontalTextAlignment()    {
        return HorizontalAlignment.CENTER;
    }
    default Impl setHorizontalTextAlignment(@NonNull HorizontalAlignment alignment) {
        return (Impl) this;
    }

    //convenience methods
    default Impl setOrientation(@NonNull Object x, @NonNull Object y, @NonNull Object width, @NonNull Object height) {
        return (Impl) this.setOrientation(SimpleDynamicOrientation.of(x, y, width, height));
    }

    default Impl setOrientation(@NonNull Constraint constraint)    {
        return this.setOrientation(this.getBounds().set(constraint));
    }

    default Impl setOrientation(@NonNull BoundingBox bounds)    {
        return (Impl) this.setOrientation(new StaticOrientation<>(bounds));
    }
}
