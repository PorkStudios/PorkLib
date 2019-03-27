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

package net.daporkchop.lib.gui.component.orientation.advanced;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.component.orientation.Orientation;
import net.daporkchop.lib.gui.component.orientation.advanced.calculator.AdvancedCalculator;
import net.daporkchop.lib.gui.component.orientation.advanced.calculator.DistUnit;
import net.daporkchop.lib.gui.component.orientation.advanced.calculator.NullCalculator;
import net.daporkchop.lib.gui.util.math.BoundingBox;
import net.daporkchop.lib.math.arrays.PArrays;

import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
@Getter
@SuppressWarnings("unchecked")
public class AdvancedOrientation<T extends Component> implements Orientation<T> {
    protected final Calculator<T>[] calculators = NullCalculator.getBaseArray();

    protected final Axis[] calcAxes = {
            Axis.X, Axis.Y, Axis.WIDTH, Axis.HEIGHT
    };
    protected final int[] cache = new int[4];

    @Override
    public BoundingBox update(@NonNull BoundingBox bb, @NonNull Container parent, @NonNull T component) {
        int[] cache = this.cache;
        cache[0] = cache[1] = cache[2] = cache[3] = -1;
        for (int i = 0; i < 4; i++) {
            cache[i] = this.calculators[i].get(bb, parent, component, cache);
        }
        return new BoundingBox(cache[0], cache[1], cache[2], cache[3]);
    }

    public AdvancedOrientation<T> setPriority(@NonNull Axis axis, @NonNull UpdatePriority priority) {
        int ordinal = priority.ordinal();
        if (this.calcAxes[ordinal] == axis) {
            return this;
        }
        for (int i = 3; i >= 0; i--) {
            if (this.calcAxes[i] == axis) {
                Axis tempAxis = this.calcAxes[ordinal];
                this.calcAxes[ordinal] = axis;
                this.calcAxes[i] = tempAxis;

                Calculator<T> tempCalculator = this.calculators[ordinal];
                this.calculators[ordinal] = this.calculators[i];
                this.calculators[i] = tempCalculator;
                return this;
            }
        }
        throw new IllegalStateException(String.format("Cannot set orientation for axis: %s", axis));
    }

    public UpdatePriority getPriority(@NonNull Axis axis) {
        return UpdatePriority.values()[PArrays.indexOf(this.calcAxes, axis)];
    }

    public AdvancedOrientation<T> configureAxis(@NonNull Axis axis, @NonNull Consumer<AdvancedCalculator<T>> initializer) {
        AdvancedCalculator<T> calculator = new AdvancedCalculator<>();
        this.calculators[PArrays.indexOf(this.calcAxes, axis)] = calculator;
        initializer.accept(calculator);
        return this;
    }

    public AdvancedOrientation<T> configureAxis(@NonNull Axis axis, @NonNull Axis other) {
        this.calculators[PArrays.indexOf(this.calcAxes, axis)] = this.calculators[PArrays.indexOf(this.calcAxes, other)];
        return this;
    }

    public AdvancedOrientation<T> configureAxis(@NonNull Axis axis, @NonNull UpdatePriority priority, @NonNull Consumer<AdvancedCalculator<T>> initializer) {
        return this.setPriority(axis, priority).configureAxis(axis, initializer);
    }

    //convenience methods for relative positioning to other components
    public AdvancedOrientation<T> below(@NonNull String... targets) {
        return this.relativeMin(Axis.Y, Double.NaN, Axis.BELOW, targets);
    }

    public AdvancedOrientation<T> below(double padding, @NonNull String... targets) {
        return this.relativeMin(Axis.Y, padding, Axis.BELOW, targets);
    }

    public AdvancedOrientation<T> right(@NonNull String... targets) {
        return this.relativeMin(Axis.X, Double.NaN, Axis.RIGHT, targets);
    }

    public AdvancedOrientation<T> right(double padding, @NonNull String... targets) {
        return this.relativeMin(Axis.X, padding, Axis.RIGHT, targets);
    }

    public AdvancedOrientation<T> xMin(@NonNull Axis axis, @NonNull String... targets) {
        return this.relativeMin(axis, Double.NaN, Axis.X, targets);
    }

    public AdvancedOrientation<T> xMin(@NonNull Axis axis, double padding, @NonNull String... targets) {
        return this.relativeMin(axis, padding, Axis.X, targets);
    }

    public AdvancedOrientation<T> yMin(@NonNull Axis axis, @NonNull String... targets) {
        return this.relativeMin(axis, Double.NaN, Axis.Y, targets);
    }

    public AdvancedOrientation<T> yMin(@NonNull Axis axis, double padding, @NonNull String... targets) {
        return this.relativeMin(axis, padding, Axis.Y, targets);
    }

    public AdvancedOrientation<T> widthMin(@NonNull Axis axis, @NonNull String... targets) {
        return this.relativeMin(axis, Double.NaN, Axis.WIDTH, targets);
    }

    public AdvancedOrientation<T> widthMin(@NonNull Axis axis, double padding, @NonNull String... targets) {
        return this.relativeMin(axis, padding, Axis.WIDTH, targets);
    }

    public AdvancedOrientation<T> heightMin(@NonNull Axis axis, @NonNull String... targets) {
        return this.relativeMin(axis, Double.NaN, Axis.HEIGHT, targets);
    }

    public AdvancedOrientation<T> heightMin(@NonNull Axis axis, double padding, @NonNull String... targets) {
        return this.relativeMin(axis, padding, Axis.HEIGHT, targets);
    }

    public AdvancedOrientation<T> relativeMin(@NonNull Axis axis, double padding, @NonNull Axis relativeAxis, @NonNull String... targets) {
        if (Double.isNaN(padding))    {
            return this.configureAxis(axis, calc -> {
                for (String target : targets)   {
                    calc = calc.min(DistUnit.RELATIVE, target, relativeAxis);
                }
            });
        } else {
            return this.configureAxis(axis, calc -> {
                for (String target : targets)   {
                    calc = calc.min(DistUnit.RELATIVE, target, relativeAxis, DistUnit.PX, padding);
                }
            });
        }
    }

    //convenience methods for relative positioning to parent
    public AdvancedOrientation<T> x(double value)   {
        return this.configureAxis(Axis.X, calc -> calc.ease(DistUnit.MULT, value, Axis.WIDTH));
    }

    public AdvancedOrientation<T> x(double value, @NonNull Axis relativeTo)   {
        return this.configureAxis(Axis.X, calc -> calc.ease(DistUnit.MULT, value, relativeTo));
    }

    public AdvancedOrientation<T> y(double value)   {
        return this.configureAxis(Axis.Y, calc -> calc.ease(DistUnit.MULT, value, Axis.HEIGHT));
    }

    public AdvancedOrientation<T> y(double value, @NonNull Axis relativeTo)   {
        return this.configureAxis(Axis.Y, calc -> calc.ease(DistUnit.MULT, value, relativeTo));
    }

    public AdvancedOrientation<T> width(double value)   {
        return this.configureAxis(Axis.WIDTH, calc -> calc.ease(DistUnit.MULT, value, Axis.WIDTH));
    }

    public AdvancedOrientation<T> width(double value, @NonNull Axis relativeTo)   {
        return this.configureAxis(Axis.WIDTH, calc -> calc.ease(DistUnit.MULT, value, relativeTo));
    }

    public AdvancedOrientation<T> height(double value)   {
        return this.configureAxis(Axis.HEIGHT, calc -> calc.ease(DistUnit.MULT, value, Axis.HEIGHT));
    }

    public AdvancedOrientation<T> height(double value, @NonNull Axis relativeTo)   {
        return this.configureAxis(Axis.HEIGHT, calc -> calc.ease(DistUnit.MULT, value, relativeTo));
    }
    
    //convenience methods for copying the position from another element
    public AdvancedOrientation<T> copyX(@NonNull String componentName)  {
        return this.configureAxis(Axis.X, calc -> calc.ease(DistUnit.RELATIVE, componentName, Axis.X));
    }

    public AdvancedOrientation<T> copyY(@NonNull String componentName)  {
        return this.configureAxis(Axis.Y, calc -> calc.ease(DistUnit.RELATIVE, componentName, Axis.Y));
    }

    public AdvancedOrientation<T> copyWidth(@NonNull String componentName)  {
        return this.configureAxis(Axis.WIDTH, calc -> calc.ease(DistUnit.RELATIVE, componentName, Axis.WIDTH));
    }

    public AdvancedOrientation<T> copyHeight(@NonNull String componentName)  {
        return this.configureAxis(Axis.HEIGHT, calc -> calc.ease(DistUnit.RELATIVE, componentName, Axis.HEIGHT));
    }
}
