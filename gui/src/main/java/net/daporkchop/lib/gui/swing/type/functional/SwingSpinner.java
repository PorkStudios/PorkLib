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

package net.daporkchop.lib.gui.swing.type.functional;

import lombok.NonNull;
import net.daporkchop.lib.gui.component.state.functional.SpinnerState;
import net.daporkchop.lib.gui.component.type.functional.Spinner;
import net.daporkchop.lib.gui.swing.impl.SwingComponent;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;

/**
 * @author DaPorkchop_
 */
public class SwingSpinner extends SwingComponent<Spinner, JSpinner, SpinnerState> implements Spinner {
    protected double value = 0;
    protected double max = Double.POSITIVE_INFINITY;
    protected double min = Double.NEGATIVE_INFINITY;
    protected double step = 1;

    protected final Map<String, DoubleConsumer> listeners = new HashMap<>();

    public SwingSpinner(String name) {
        super(name, new JSpinner());

        this.updateModel();

        this.swing.addChangeListener(new SwingSpinnerChangeListener());
    }
    @Override
    public double getValueD() {
        return this.value = (double) this.swing.getValue();
    }

    @Override
    public Spinner setValueD(double val) {
        if (val != this.getValueD())    {
            this.swing.setValue(this.value = val);
        }
        return this;
    }

    @Override
    public double getMaxValueD() {
        return this.max;
    }

    @Override
    public Spinner setMaxValueD(double val) {
        if (this.max != val)    {
            this.max = val;
            this.updateModel();
        }
        return this;
    }

    @Override
    public double getMinValueD() {
        return this.min;
    }

    @Override
    public Spinner setMinValueD(double val) {
        if (this.min != val)    {
            this.min = val;
            this.updateModel();
        }
        return this;
    }

    @Override
    public double getStepD() {
        return this.step;
    }

    @Override
    public Spinner setStepD(double step) {
        if (this.step != step)    {
            this.step = step;
            this.updateModel();
        }
        return this;
    }

    @Override
    public Spinner setLimitsD(double min, double max) {
        if (this.min != min || this.max != max) {
            this.min = min;
            this.max = max;
            this.updateModel();
        }
        return this;
    }

    @Override
    public Spinner setValAndLimitsD(double val, double min, double max) {
        if (this.value != this.getValueD() || this.min != min || this.max != max) {
            this.value = val;
            this.min = min;
            this.max = max;
            this.updateModel();
        }
        return this;
    }

    @Override
    public Spinner addChangeListenerD(String name, DoubleConsumer callback) {
        this.listeners.put(name, callback);
        return this;
    }

    @Override
    public Spinner removeChangeListener(@NonNull String name) {
        this.listeners.remove(name);
        return this;
    }

    protected void updateModel()    {
        this.swing.setModel(new SpinnerNumberModel(this.value, this.min, this.max, this.step));
    }

    protected class SwingSpinnerChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            if (SwingSpinner.this.value != (double) SwingSpinner.this.swing.getValue())   {
                double val = SwingSpinner.this.value = (double) SwingSpinner.this.swing.getValue();
                SwingSpinner.this.listeners.values().forEach(callback -> callback.accept(val));
            }
        }
    }
}
