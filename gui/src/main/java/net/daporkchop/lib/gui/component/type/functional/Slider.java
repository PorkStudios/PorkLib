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
import net.daporkchop.lib.gui.component.state.functional.SliderState;

import java.util.function.IntConsumer;
import java.util.function.IntFunction;

/**
 * @author DaPorkchop_
 */
public interface Slider extends Component<Slider, SliderState> {
    @Override
    default SliderState getState() {
        return this.isVisible() ?
                this.isEnabled() ?
                        this.isHovered() ? SliderState.ENABLED_HOVERED : SliderState.ENABLED
                        : this.isHovered() ? SliderState.DISABLED_HOVERED : SliderState.DISABLED
                : SliderState.HIDDEN;
    }

    int getValue();
    Slider setValue(int val);
    int getMaxValue();
    Slider setMaxValue(int val);
    int getMinValue();
    Slider setMinValue(int val);
    Slider setLimits(int min, int max);
    Slider setValAndLimits(int val, int min, int max);

    boolean areStepsDrawn();
    Slider setStepsDrawn(boolean stepsDrawn);
    default Slider drawSteps()  {
        return this.setStepsDrawn(true);
    }
    default Slider hideSteps()  {
        return this.setStepsDrawn(false);
    }

    int getStep();
    Slider setStep(int step);
    default int getMajorStep()  {
        return this.getStep();
    }
    default Slider setMajorStep(int step)   {
        return this.setStep(step);
    }
    default int getMinorStep()  {
        return this.getStep();
    }
    default Slider setMinorStep(int step)   {
        return this.setStep(step);
    }

    Slider addChangeListener(@NonNull String name, @NonNull IntConsumer callback);
    Slider removeChangeListener(@NonNull String name);
    default Slider addChangeListener(@NonNull IntConsumer callback)   {
        return this.addChangeListener(String.format("%s@%d", callback.getClass().getCanonicalName(), System.identityHashCode(callback)), callback);
    }
}
