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
import net.daporkchop.lib.gui.util.DimensionCalculator;
import net.daporkchop.lib.gui.util.Dimensions;

import java.util.function.ToIntFunction;

/**
 * A single component of a GUI
 *
 * @author DaPorkchop_
 */
@SuppressWarnings("unchecked")
public interface GuiComponent<Impl> {
    DimensionCalculator getDimensionCalculator();

    Impl setDimensionCalculator(@NonNull DimensionCalculator dimensionCalculator);

    Dimensions getCurrentDimensions();

    String getName();

    String getText();

    void update(@NonNull Dimensions windowDimensions);

    //default methods
    default Impl setX(@NonNull ToIntFunction<Dimensions> x) {
        if (this.getDimensionCalculator() == null)  {
            this.setDimensionCalculator(DimensionCalculator.defaultInstance());
        }
        this.getDimensionCalculator().setX(x);
        return (Impl) this;
    }

    default Impl setY(@NonNull ToIntFunction<Dimensions> y) {
        if (this.getDimensionCalculator() == null)  {
            this.setDimensionCalculator(DimensionCalculator.defaultInstance());
        }
        this.getDimensionCalculator().setY(y);
        return (Impl) this;
    }

    default Impl setWidth(@NonNull ToIntFunction<Dimensions> width) {
        if (this.getDimensionCalculator() == null)  {
            this.setDimensionCalculator(DimensionCalculator.defaultInstance());
        }
        this.getDimensionCalculator().setWidth(width);
        return (Impl) this;
    }

    default Impl setHeight(@NonNull ToIntFunction<Dimensions> height) {
        if (this.getDimensionCalculator() == null)  {
            this.setDimensionCalculator(DimensionCalculator.defaultInstance());
        }
        this.getDimensionCalculator().setHeight(height);
        return (Impl) this;
    }
}
