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
import java.util.HashMap;
import java.util.Map;
import java.util.function.LongConsumer;

/**
 * @author DaPorkchop_
 */
public class SwingSpinner extends SwingComponent<Spinner, JSpinner, SpinnerState> implements Spinner {
    protected long value = 0L;
    protected long max = Long.MIN_VALUE;
    protected long min = Long.MIN_VALUE;
    protected long step = 1L;

    protected final Map<String, LongConsumer> listeners = new HashMap<>();

    public SwingSpinner(String name) {
        super(name, new JSpinner());

        this.updateModel();

        this.swing.addChangeListener(new SwingSpinnerChangeListener());
    }

    @Override
    public long getValue() {
        return (long) this.swing.getValue();
    }

    @Override
    public Spinner setValue(long val) {
        this.swing.setValue(val);
        return this;
    }

    @Override
    public Spinner setMaxValue(long val) {
        if (this.max != val)    {
            this.max = val;
            this.updateModel();
        }
        return this;
    }

    @Override
    public Spinner setMinValue(long val) {
        if (this.min != val)    {
            this.min = val;
            this.updateModel();
        }
        return this;
    }

    @Override
    public Spinner setStep(long step) {
        if (this.step != step)    {
            this.step = step;
            this.updateModel();
        }
        return this;
    }

    @Override
    public Spinner addChangeListener(@NonNull String name, @NonNull LongConsumer callback) {
        this.listeners.put(name, callback);
        return this;
    }

    @Override
    public Spinner removeChangeListener(@NonNull String name) {
        this.listeners.remove(name);
        return this;
    }

    protected void updateModel()    {
        this.swing.setModel(new SpinnerNumberModel((Long) this.value, this.min == Long.MIN_VALUE ? null : this.min, this.max == Long.MIN_VALUE ? null : this.max, (Long) this.step));
    }

    protected class SwingSpinnerChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            if (SwingSpinner.this.value != (long) SwingSpinner.this.swing.getValue())   {
                long val = SwingSpinner.this.value = (long) SwingSpinner.this.swing.getValue();
                SwingSpinner.this.listeners.values().forEach(callback -> callback.accept(val));
            }
        }
    }
}
