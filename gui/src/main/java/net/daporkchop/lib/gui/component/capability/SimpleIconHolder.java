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

package net.daporkchop.lib.gui.component.capability;

import lombok.NonNull;
import net.daporkchop.lib.imaging.bitmap.PIcon;
import net.daporkchop.lib.gui.component.Element;
import net.daporkchop.lib.gui.component.state.ElementState;
import net.daporkchop.lib.gui.util.Alignment;
import net.daporkchop.lib.gui.util.HorizontalAlignment;
import net.daporkchop.lib.gui.util.VerticalAlignment;

/**
 * Specifies that a given element type may have an icon
 *
 * @author DaPorkchop_
 */
public interface SimpleIconHolder<Impl extends SimpleIconHolder, State extends ElementState<? extends Element, State>> extends Element<Impl, State> {
    PIcon getIcon();
    Impl setIcon(PIcon icon);

    default HorizontalAlignment getIconHAlignment()  {
        return HorizontalAlignment.CENTER;
    }
    default Impl setIconHAlignment(@NonNull HorizontalAlignment alignment)   {
        return (Impl) this;
    }

    default VerticalAlignment getIconVAlignment()  {
        return VerticalAlignment.CENTER;
    }
    default Impl setIconVAlignment(@NonNull VerticalAlignment alignment)   {
        return (Impl) this;
    }

    default Alignment getIconPos()  {
        return Alignment.getFrom(this.getIconHAlignment(), this.getIconVAlignment());
    }
    @SuppressWarnings("unchecked")
    default Impl setIconPos(@NonNull Alignment alignment)   {
        return (Impl) this.setIconHAlignment(alignment.getHorizontal()).setIconVAlignment(alignment.getVertical());
    }
}
