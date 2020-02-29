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

package net.daporkchop.lib.gui.component.orientation.advanced.calculator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.component.orientation.advanced.Calculator;
import net.daporkchop.lib.gui.util.math.BoundingBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author DaPorkchop_
 */
@Getter
public class SumCalculator<T extends Component> implements Calculator<T> {
    protected final Collection<Calculator<T>> degligates = new HashSet<>();

    @Override
    public int get(BoundingBox bb, Container parent, T component, int[] dims) {
        int i = 0;
        for (Calculator<T> calculator : this.degligates)    {
            i += calculator.get(bb, parent, component, dims);
        }
        return i;
    }

    @Override
    public SumCalculator<T> plus(@NonNull Calculator<T> other) {
        this.degligates.add(other);
        return this;
    }

    public Calculator<T> build() {
        if (this.degligates.isEmpty())  {
            return NullCalculator.getInstance();
        } else if (this.degligates.size() == 1)  {
            return this.degligates.iterator().next();
        } else {
            return this;
        }
    }
}
