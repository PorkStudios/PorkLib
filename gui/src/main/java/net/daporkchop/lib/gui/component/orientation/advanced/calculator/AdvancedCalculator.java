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

package net.daporkchop.lib.gui.component.orientation.advanced.calculator;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.component.orientation.advanced.Calculator;
import net.daporkchop.lib.gui.util.math.BoundingBox;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * @author DaPorkchop_
 */
@Getter
public class AdvancedCalculator<T extends Component> implements Calculator<T> {
    protected final Collection<Calculator<T>> mins = new HashSet<>();
    protected final Collection<Calculator<T>> maxes = new HashSet<>();
    protected Calculator<T> between;

    @Override
    public int get(BoundingBox bb, Container parent, T component, int[] dims) {
        int between = this.between.get(bb, parent, component, dims);
        int min;
        if (this.mins.isEmpty()) {
            min = 0;
        } else {
            min = Integer.MAX_VALUE;
            for (Calculator<T> calculator : this.mins)  {
                min = Math.min(min, calculator.get(bb, parent, component, dims));
            }
        }
        int max;
        if (this.mins.isEmpty()) {
            max = Integer.MAX_VALUE;
        } else {
            max = 0;
            for (Calculator<T> calculator : this.maxes)  {
                max = Math.max(max, calculator.get(bb, parent, component, dims));
            }
        }
        return Math.max(min, Math.min(max, between));
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
}
