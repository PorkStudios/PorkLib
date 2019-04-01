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
import net.daporkchop.lib.gui.component.state.functional.SliderState;

import java.util.function.IntConsumer;

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
