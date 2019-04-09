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

package net.daporkchop.lib.gui.component.type.functional;

import lombok.NonNull;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.state.functional.SpinnerState;

import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;

import static net.daporkchop.lib.math.primitive.PMath.floorI;

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
