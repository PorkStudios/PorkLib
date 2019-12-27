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

import net.daporkchop.lib.gui.component.state.functional.ProgressBarState;
import net.daporkchop.lib.gui.component.type.functional.ProgressBar;
import net.daporkchop.lib.gui.swing.GuiEngineSwing;
import net.daporkchop.lib.gui.swing.common.SwingMouseListener;
import net.daporkchop.lib.gui.swing.impl.SwingComponent;
import net.daporkchop.lib.gui.swing.impl.SwingContainer;

import javax.swing.*;

/**
 * @author DaPorkchop_
 */
public class SwingProgressBar extends SwingComponent<ProgressBar, JProgressBar, ProgressBarState> implements ProgressBar {
    public SwingProgressBar(String name) {
        super(name, new JProgressBar());

        this.swing.setMaximum(100);

        this.swing.addMouseListener(new SwingMouseListener<>(this));
    }

    @Override
    public int getProgress() {
        return this.swing.getValue();
    }

    @Override
    public ProgressBar setProgress(int progress) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            if (progress != this.swing.getValue()) {
                this.swing.setValue(progress);
            }
        } else {
            SwingUtilities.invokeLater(() -> this.setProgress(progress));
        }
        return this;
    }

    @Override
    public int getEnd() {
        return this.swing.getMaximum();
    }

    @Override
    public ProgressBar setEnd(int end) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            if (end != this.swing.getMaximum()) {
                this.swing.setMaximum(end);
            }
        } else {
            SwingUtilities.invokeLater(() -> this.setEnd(end));
        }
        return this;
    }

    @Override
    public ProgressBar step() {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.swing.setValue(this.swing.getValue() + 1);
        } else {
            SwingUtilities.invokeLater(this::step);
        }
        return this;
    }

    @Override
    public ProgressBar step(int step) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.swing.setValue(this.swing.getValue() + step);
        } else {
            SwingUtilities.invokeLater(() -> this.step(step));
        }
        return this;
    }

    @Override
    public boolean isInfinite() {
        return this.swing.isIndeterminate();
    }

    @Override
    public ProgressBar setInfinite(boolean infinite) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            if (infinite != this.isInfinite()) {
                this.swing.setIndeterminate(infinite);
            }
        } else {
            SwingUtilities.invokeLater(() -> this.setInfinite(infinite));
        }
        return this;
    }
}
