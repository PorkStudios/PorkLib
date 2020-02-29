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

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.orientation.advanced.Axis;
import net.daporkchop.lib.gui.component.orientation.advanced.Calculator;
import net.daporkchop.lib.gui.component.orientation.advanced.calculator.dist.CMCalculator;
import net.daporkchop.lib.gui.component.orientation.advanced.calculator.dist.MultCalculator;
import net.daporkchop.lib.gui.component.orientation.advanced.calculator.dist.PXCalculator;
import net.daporkchop.lib.gui.component.orientation.advanced.calculator.dist.RelativeCalculator;

import static net.daporkchop.lib.math.primitive.PMath.*;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public enum DistUnit {
    PX((dVal, axis, relative) -> new PXCalculator<>(floorI(dVal))),
    CM((dVal, axis, relative) -> new CMCalculator<>(floorI(dVal))),
    MULT((dVal, axis, relative) -> new MultCalculator(dVal, axis)),
    RELATIVE((dVal, axis, relative) -> new RelativeCalculator(axis, relative)),
    ;

    @NonNull
    protected final CalculatorSupplier calculatorSupplier;

    @SuppressWarnings("unchecked")
    public <T extends Component> Calculator<T> create(double dVal, Axis axis, String relative)   {
        return ((CalculatorSupplier<T>) this.calculatorSupplier).create(dVal, axis, relative);
    }

    @FunctionalInterface
    protected interface CalculatorSupplier<T extends Component>  {
        Calculator<T> create(double dVal, Axis axis, String relative);
    }
}
