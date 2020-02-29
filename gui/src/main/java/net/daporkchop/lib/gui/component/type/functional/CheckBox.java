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
import net.daporkchop.lib.gui.component.capability.IconHolder;
import net.daporkchop.lib.gui.component.capability.TextHolder;
import net.daporkchop.lib.gui.component.state.functional.CheckBoxState;
import net.daporkchop.lib.gui.util.handler.StateListener;

import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
public interface CheckBox extends Component<CheckBox, CheckBoxState>, IconHolder<CheckBox, CheckBoxState>, TextHolder<CheckBox> {
    boolean isSelected();
    CheckBox setSelected(boolean selected);

    @Override
    default CheckBoxState getState() {
        return this.isVisible() ?
                this.isEnabled() ?
                        this.isHovered() ?
                                this.isSelected() ? CheckBoxState.ENABLED_HOVERED_SELECTED : CheckBoxState.ENABLED_HOVERED
                                : this.isSelected() ? CheckBoxState.ENABLED_SELECTED : CheckBoxState.ENABLED
                        : this.isHovered() ?
                        this.isSelected() ? CheckBoxState.DISABLED_HOVERED_SELECTED : CheckBoxState.DISABLED_HOVERED
                        : this.isSelected() ? CheckBoxState.DISABLED_SELECTED : CheckBoxState.DISABLED
                : CheckBoxState.HIDDEN;
    }

    //convenience methods
    @SuppressWarnings("unchecked")
    default CheckBox addSelectionListener(@NonNull Consumer<Boolean> callback) {
        return this.addStateListener(String.format("%s@%d", callback.getClass().getCanonicalName(), System.identityHashCode(callback)), new StateListener<CheckBox, CheckBoxState>() {
            protected boolean selected = CheckBox.this.isSelected();

            @Override
            public void onStateChange(@NonNull CheckBoxState state) {
                if (state.isSelected() != this.selected) {
                    callback.accept(this.selected = state.isSelected());
                }
            }
        });
    }
    default CheckBox addSelectedListener(@NonNull Runnable callback) {
        return this.addSelectionListener(selected -> {
            if (selected) {
                callback.run();
            }
        });
    }
    default CheckBox addDeselectedListener(@NonNull Runnable callback) {
        return this.addSelectionListener(selected -> {
            if (!selected) {
                callback.run();
            }
        });
    }
}
