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
import net.daporkchop.lib.gui.component.capability.IconHolder;
import net.daporkchop.lib.gui.component.capability.TextHolder;
import net.daporkchop.lib.gui.component.state.functional.RadioButtonState;
import net.daporkchop.lib.gui.component.type.misc.RadioButtonGroup;
import net.daporkchop.lib.gui.util.event.handler.StateListener;

import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
public interface RadioButton extends Component<RadioButton, RadioButtonState>, IconHolder<RadioButton, RadioButtonState>, TextHolder<RadioButton> {
    boolean isSelected();
    RadioButton setSelected(boolean selected);

    RadioButtonGroup getGroup();

    @Override
    default RadioButtonState getState() {
        return this.isVisible() ?
                this.isEnabled() ?
                        this.isHovered() ?
                                this.isSelected() ? RadioButtonState.ENABLED_HOVERED_SELECTED : RadioButtonState.ENABLED_HOVERED
                                : this.isSelected() ? RadioButtonState.ENABLED_SELECTED : RadioButtonState.ENABLED
                        : this.isHovered() ?
                        this.isSelected() ? RadioButtonState.DISABLED_HOVERED_SELECTED : RadioButtonState.DISABLED_HOVERED
                        : this.isSelected() ? RadioButtonState.DISABLED_SELECTED : RadioButtonState.DISABLED
                : RadioButtonState.HIDDEN;
    }

    //convenience methods
    @SuppressWarnings("unchecked")
    default RadioButton addSelectionListener(@NonNull Consumer<Boolean> callback) {
        return this.addStateListener(String.format("%s@%d", callback.getClass().getCanonicalName(), System.identityHashCode(callback)), new StateListener<RadioButton, RadioButtonState>() {
            protected boolean selected = RadioButton.this.isSelected();

            @Override
            public void onStateChange(@NonNull RadioButtonState state) {
                if (state.isSelected() != this.selected) {
                    callback.accept(this.selected = state.isSelected());
                }
            }
        });
    }
    default RadioButton addSelectedListener(@NonNull Runnable callback) {
        return this.addSelectionListener(selected -> {
            if (selected) {
                callback.run();
            }
        });
    }
    default RadioButton addDeselectedListener(@NonNull Runnable callback) {
        return this.addSelectionListener(selected -> {
            if (!selected) {
                callback.run();
            }
        });
    }
}
