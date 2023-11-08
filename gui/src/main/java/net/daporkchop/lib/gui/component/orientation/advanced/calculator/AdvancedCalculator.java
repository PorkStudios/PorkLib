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

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.component.orientation.advanced.Axis;
import net.daporkchop.lib.gui.component.orientation.advanced.Calculator;
import net.daporkchop.lib.gui.util.math.BoundingBox;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
@Getter
public class AdvancedCalculator<T extends Component> implements Calculator<T> {
    protected final Collection<Calculator<T>> mins = new HashSet<>();
    protected final Collection<Calculator<T>> maxes = new HashSet<>();
    protected Calculator<T> between = NullCalculator.getInstance();

    @Override
    public int get(BoundingBox bb, Container parent, T component, int[] dims) {
        int between = this.between.get(bb, parent, component, dims);
        int min;
        if (this.mins.isEmpty()) {
            min = 0;
        } else {
            min = 0;
            for (Calculator<T> calculator : this.mins) {
                min = Math.max(min, calculator.get(bb, parent, component, dims));
            }
        }
        int max;
        if (this.maxes.isEmpty()) {
            max = Integer.MAX_VALUE;
        } else {
            max = Integer.MAX_VALUE;
            for (Calculator<T> calculator : this.maxes) {
                max = Math.min(max, calculator.get(bb, parent, component, dims));
            }
        }
        /*if (dims[0] != -1 && dims[1] == -1) {
            int j = 0;
        }*/ //debugger time!
        return Math.max(min, Math.min(max, between));
    }

    @Override
    public boolean hasMin() {
        return !this.mins.isEmpty();
    }

    @Override
    public int getMin(BoundingBox bb, Container parent, T component, int[] dims) {
        int min = -1;
        for (Calculator<T> calculator : this.mins) {
            min = Math.max(min, calculator.get(bb, parent, component, dims));
        }
        return min;
    }

    public AdvancedCalculator<T> min(@NonNull Calculator<T> calculator) {
        this.mins.add(calculator);
        return this;
    }

    public AdvancedCalculator<T> max(@NonNull Calculator<T> calculator) {
        this.maxes.add(calculator);
        return this;
    }

    public AdvancedCalculator<T> ease(Calculator<T> calculator) {
        this.between = calculator;
        return this;
    }

    public AdvancedCalculator<T> max(@NonNull Consumer<SumCalculator<T>> initializer) {
        SumCalculator<T> calculator = new SumCalculator<>();
        initializer.accept(calculator);
        return this.max(calculator);
    }

    public AdvancedCalculator<T> min(@NonNull Consumer<SumCalculator<T>> initializer) {
        SumCalculator<T> calculator = new SumCalculator<>();
        initializer.accept(calculator);
        return this.min(calculator);
    }

    public AdvancedCalculator<T> ease(@NonNull Consumer<SumCalculator<T>> initializer) {
        SumCalculator<T> calculator = new SumCalculator<>();
        initializer.accept(calculator);
        return this.ease(calculator);
    }

    //convenience methods
    public AdvancedCalculator<T> max(@NonNull Object... args) {
        return this.max(this.parse(args));
    }

    public AdvancedCalculator<T> min(@NonNull Object... args) {
        return this.min(this.parse(args));
    }

    public AdvancedCalculator<T> ease(@NonNull Object... args) {
        return this.ease(this.parse(args));
    }

    @SuppressWarnings("unchecked")
    protected Calculator<T> parse(@NonNull Object... args) {
        SumCalculator<T> calculator = new SumCalculator<>();
        if (args.length != 0) {
            DistUnit unit = null;
            double dVal = Double.NaN;
            Axis axis = null;
            String relative = null;
            for (Object o : args) {
                if (o instanceof DistUnit) {
                    if (unit != null) {
                        calculator = calculator.plus(unit.create(dVal, axis, relative));
                    }
                    unit = (DistUnit) o;
                    dVal = Double.NaN;
                    axis = null;
                    relative = null;
                } else if (o instanceof Calculator) {
                    if (unit != null) {
                        calculator = calculator.plus(unit.create(dVal, axis, relative));
                        unit = null;
                        dVal = Double.NaN;
                        axis = null;
                        relative = null;
                    }
                    calculator = calculator.plus((Calculator) o);
                } else if (unit == null) {
                    throw new IllegalStateException("DistUnit not set!");
                } else if (o instanceof Number) {
                    dVal = ((Number) o).doubleValue();
                } else if (o instanceof Axis) {
                    axis = (Axis) o;
                } else if (o instanceof String) {
                    relative = (String) o;
                }
            }
            if (unit != null) {
                calculator = calculator.plus(unit.create(dVal, axis, relative));
            }
        }
        return calculator.build();
    }
}
