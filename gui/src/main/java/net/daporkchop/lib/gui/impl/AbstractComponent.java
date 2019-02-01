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

package net.daporkchop.lib.gui.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.util.DimensionCalculator;
import net.daporkchop.lib.gui.util.Dimensions;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public abstract class AbstractComponent<Impl extends Component> implements Component<Impl> {
    protected Dimensions currentDimensions;
    protected DimensionCalculator dimensionCalculator;
    @NonNull
    protected final String name;
    @NonNull
    protected String text = "";
    @NonNull
    protected String tooltip = "";

    @Override
    public void update(@NonNull Dimensions windowDimensions) {
        if (this.dimensionCalculator == null)   {
            throw new IllegalStateException("Dimension calculator is not set!");
        } else {
            this.currentDimensions = this.dimensionCalculator.update(windowDimensions);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Impl setDimensionCalculator(@NonNull DimensionCalculator dimensionCalculator) {
        this.dimensionCalculator = dimensionCalculator;
        return (Impl) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Impl setText(@NonNull String text) {
        this.text = text;
        return (Impl) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Impl setTooltip(@NonNull String tooltip) {
        this.tooltip = tooltip;
        return (Impl) this;
    }
}
