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
import net.daporkchop.lib.imaging.bitmap.PIcon;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.state.functional.DropdownState;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author DaPorkchop_
 */
public interface Dropdown<V> extends Component<Dropdown<V>, DropdownState> {
    Dropdown<V> clearValues();
    V getSelectedValue();
    Dropdown<V> setSelectedValue(V value);
    Dropdown<V> addValue(@NonNull V value);
    default Dropdown<V> addValues(@NonNull V... values) {
        for (V value : values) {
            this.addValue(value);
        }
        return this;
    }
    Dropdown<V> removeValue(@NonNull V value);

    boolean isDown();

    Dropdown<V> addValueSelectedListener(@NonNull String name, @NonNull Consumer<V> callback);
    default Dropdown<V> addValueSelectedListener(@NonNull Consumer<V> callback) {
        return this.addValueSelectedListener(String.format("%s@%d", callback.getClass().getCanonicalName(), System.identityHashCode(callback)), callback);
    }
    default Dropdown<V> addValueSelectedListener(@NonNull String name, @NonNull Runnable callback) {
        return this.addValueSelectedListener(name, value -> callback.run());
    }
    default Dropdown<V> addValueSelectedListener(@NonNull Runnable callback) {
        return this.addValueSelectedListener(String.format("%s@%d", callback.getClass().getCanonicalName(), System.identityHashCode(callback)), value -> callback.run());
    }
    Dropdown<V> removeValueSelectedListener(@NonNull String name);

    Dropdown<V> setRendererText(@NonNull Function<V, String> renderer);
    Dropdown<V> setRendererIcon(@NonNull Function<V, PIcon> renderer);

    @Override
    default DropdownState getState() {
        return this.isVisible() ?
                this.isEnabled() ?
                        this.isDown() ? DropdownState.ENABLED_SELECTING
                                : this.isHovered() ? DropdownState.ENABLED_HOVERED : DropdownState.ENABLED
                        : this.isHovered() ? DropdownState.DISABLED_HOVERED : DropdownState.DISABLED
                : DropdownState.HIDDEN;
    }
}
