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

package net.daporkchop.lib.gui.component.orientation.advanced.calculator.dist;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.component.orientation.advanced.Axis;
import net.daporkchop.lib.gui.component.orientation.advanced.Calculator;
import net.daporkchop.lib.gui.util.math.BoundingBox;

import static net.daporkchop.lib.common.math.PMath.*;

/**
 * @author DaPorkchop_
 */
@Getter
public class MultCalculator<T extends Component> implements Calculator<T> {
    protected final double factor;
    protected final Axis axis;

    public MultCalculator(double factor, @NonNull Axis axis)    {
        this.factor = factor;
        this.axis = axis;
        if (!(axis == Axis.X || axis == Axis.Y || axis == Axis.WIDTH || axis == Axis.HEIGHT))   {
            throw new IllegalArgumentException(String.format("Invalid axis: %s", axis));
        }
    }

    @Override
    public int get(@NonNull BoundingBox bb, @NonNull Container parent, @NonNull T component, @NonNull int[] dims) {
        return floorI(this.axis.getFrom(bb, null, component) * this.factor);
    }
}
