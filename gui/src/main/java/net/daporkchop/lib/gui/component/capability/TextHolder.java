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
import net.daporkchop.lib.gui.component.type.functional.Label;
import net.daporkchop.lib.gui.util.Alignment;
import net.daporkchop.lib.gui.util.HorizontalAlignment;
import net.daporkchop.lib.gui.util.VerticalAlignment;

import java.awt.Color;

/**
 * @author DaPorkchop_
 */
public interface TextHolder<Impl extends TextHolder> {
    String getText();
    Impl setText(String text);

    HorizontalAlignment getTextHAlignment();
    Impl setTextHAlignment(@NonNull HorizontalAlignment alignment);

    VerticalAlignment getTextVAlignment();
    Impl setTextVAlignment(@NonNull VerticalAlignment alignment);

    default Alignment getTextPos()  {
        return Alignment.getFrom(this.getTextHAlignment(), this.getTextVAlignment());
    }
    @SuppressWarnings("unchecked")
    default Impl setTextPos(@NonNull Alignment alignment)   {
        return (Impl) this.setTextHAlignment(alignment.getHorizontal()).setTextVAlignment(alignment.getVertical());
    }

    Impl setTextColor(Color color);
    Impl setTextColor(int argb);
}
