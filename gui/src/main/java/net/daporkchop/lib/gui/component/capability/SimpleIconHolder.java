/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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
