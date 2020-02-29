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

package net.daporkchop.lib.gui.component.type.functional;

import lombok.NonNull;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.state.functional.SpinnerState;

import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;

import static net.daporkchop.lib.math.primitive.PMath.*;

/**
 * @author DaPorkchop_
 */
public interface Spinner extends Component<Spinner, SpinnerState> {
    @Override
    default SpinnerState getState() {
        return this.isVisible() ?
                this.isEnabled() ?
                        this.isHovered() ? SpinnerState.ENABLED_HOVERED : SpinnerState.ENABLED
                        : this.isHovered() ? SpinnerState.DISABLED_HOVERED : SpinnerState.DISABLED
                : SpinnerState.HIDDEN;
    }

    default int getValue()  {
        return floorI(this.getValueD());
    }
    default Spinner setValue(int val)   {
        return this.setValueD(val);
    }
    default int getMaxValue()   {
        return floorI(this.getMaxValueD());
    }
    default Spinner setMaxValue(int val)    {
        return this.setMaxValueD(val);
    }
    default int getMinValue()   {
        return floorI(this.getMinValueD());
    }
    default Spinner setMinValue(int val)    {
        return this.setMinValueD(val);
    }
    default int getStep()   {
        return floorI(this.getStepD());
    }
    default Spinner setStep(int step)   {
        return this.setStepD(step);
    }
    default Spinner setLimits(int min, int max) {
        return this.setLimitsD(min, max);
    }
    default Spinner setValAndLimits(int val, int min, int max)  {
        return this.setValAndLimitsD(val, min, max);
    }
    
    double getValueD();
    Spinner setValueD(double val);
    double getMaxValueD();
    Spinner setMaxValueD(double val);
    double getMinValueD();
    Spinner setMinValueD(double val);
    double getStepD();
    Spinner setStepD(double step);
    Spinner setLimitsD(double min, double max);
    Spinner setValAndLimitsD(double val, double min, double max);

    Spinner removeChangeListener(@NonNull String name);
    Spinner addChangeListenerD(@NonNull String name, @NonNull DoubleConsumer callback);
    default Spinner addChangeListenerD(@NonNull DoubleConsumer callback)   {
        return this.addChangeListenerD(String.format("%s@%d", callback.getClass().getCanonicalName(), System.identityHashCode(callback)), callback);
    }
    default Spinner addChangeListener(@NonNull String name, @NonNull IntConsumer callback)  {
        return this.addChangeListenerD(name, val -> callback.accept(floorI(val)));
    }
    default Spinner addChangeListener(@NonNull IntConsumer callback)   {
        return this.addChangeListener(String.format("%s@%d", callback.getClass().getCanonicalName(), System.identityHashCode(callback)), callback);
    }
}
