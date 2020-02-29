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

package net.daporkchop.lib.gui.swing.type.functional;

import lombok.NonNull;
import net.daporkchop.lib.gui.component.state.functional.SliderState;
import net.daporkchop.lib.gui.component.type.functional.Slider;
import net.daporkchop.lib.gui.component.type.functional.Spinner;
import net.daporkchop.lib.gui.swing.GuiEngineSwing;
import net.daporkchop.lib.gui.swing.impl.SwingComponent;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntConsumer;

/**
 * @author DaPorkchop_
 */
public class SwingSlider extends SwingComponent<Slider, JSlider, SliderState> implements Slider {
    protected final Map<String, IntConsumer> listeners = new HashMap<>();

    public SwingSlider(String name) {
        super(name, new JSlider());

        this.swing.addChangeListener(new SwingSliderChangeListener());
    }

    @Override
    public int getValue() {
        return this.swing.getValue();
    }

    @Override
    public Slider setValue(int val) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.swing.setValue(val);
        } else {
            SwingUtilities.invokeLater(() -> this.setValue(val));
        }
        return this;
    }

    @Override
    public int getMaxValue() {
        return this.swing.getMaximum();
    }

    @Override
    public Slider setMaxValue(int val) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.swing.setMaximum(val);
        } else {
            SwingUtilities.invokeLater(() -> this.setMaxValue(val));
        }
        return this;
    }

    @Override
    public int getMinValue() {
        return this.swing.getMinimum();
    }

    @Override
    public Slider setMinValue(int val) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.swing.setMinimum(val);
        } else {
            SwingUtilities.invokeLater(() -> this.setMinValue(val));
        }
        return this;
    }

    @Override
    public Slider setLimits(int min, int max) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.swing.setModel(new DefaultBoundedRangeModel(this.swing.getValue(), 0, min, max));
        } else {
            SwingUtilities.invokeLater(() -> this.setLimits(min, max));
        }
        return this;
    }

    @Override
    public Slider setValAndLimits(int val, int min, int max) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.swing.setModel(new DefaultBoundedRangeModel(val, 0, min, max));
        } else {
            SwingUtilities.invokeLater(() -> this.setValAndLimits(val, min, max));
        }
        return this;
    }

    @Override
    public boolean areStepsDrawn() {
        return this.swing.getPaintTicks();
    }

    @Override
    public Slider setStepsDrawn(boolean stepsDrawn) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.swing.setPaintTicks(stepsDrawn);
        } else {
            SwingUtilities.invokeLater(() -> this.setStepsDrawn(stepsDrawn));
        }
        return this;
    }

    @Override
    public int getStep() {
        return this.getMajorStep();
    }

    @Override
    public Slider setStep(int step) {
        return this.setMajorStep(step);
    }

    @Override
    public int getMajorStep() {
        return this.swing.getMajorTickSpacing();
    }

    @Override
    public Slider setMajorStep(int step) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.swing.setMajorTickSpacing(step);
        } else {
            SwingUtilities.invokeLater(() -> this.setMajorStep(step));
        }
        return this;
    }

    @Override
    public int getMinorStep() {
        return this.swing.getMinorTickSpacing();
    }

    @Override
    public Slider setMinorStep(int step) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.swing.setMinorTickSpacing(step);
        } else {
            SwingUtilities.invokeLater(() -> this.setMinorStep(step));
        }
        return this;
    }

    @Override
    public Slider addChangeListener(@NonNull String name, @NonNull IntConsumer callback) {
        this.listeners.put(name, callback);
        return this;
    }

    @Override
    public Slider removeChangeListener(@NonNull String name) {
        this.listeners.remove(name);
        return this;
    }

    protected class SwingSliderChangeListener implements ChangeListener {
        protected int value = SwingSlider.this.swing.getValue();

        @Override
        public void stateChanged(ChangeEvent e) {
            if (this.value != SwingSlider.this.swing.getValue())   {
                this.value = SwingSlider.this.swing.getValue();
                SwingSlider.this.listeners.values().forEach(callback -> callback.accept(this.value));
            }
        }
    }
}
