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

package net.daporkchop.lib.gui.component.type.misc;

import lombok.NonNull;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.state.misc.RadioButtonGroupState;
import net.daporkchop.lib.gui.component.type.functional.RadioButton;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
public interface RadioButtonGroup extends Component<RadioButtonGroup, RadioButtonGroupState> {
    RadioButton getSelected();
    Collection<RadioButton> getChildren();
    default RadioButton getButtonByName(@NonNull String name)   {
        return this.getChildren().stream()
                .filter(button -> button.getName().equals(name))
                .findAny().orElseThrow(() -> new NoSuchElementException(name));
    }
    default RadioButton getButtonByQualifiedName(@NonNull String qualifiedName)   {
        return this.getChildren().stream()
                .filter(button -> button.getQualifiedName().equals(qualifiedName))
                .findAny().orElseThrow(() -> new NoSuchElementException(qualifiedName));
    }
    RadioButtonGroup add(@NonNull RadioButton button);
    RadioButtonGroup remove(@NonNull String qualifiedName);
    default RadioButtonGroup remove(@NonNull RadioButton button)    {
        return this.remove(button.getQualifiedName());
    }

    @Override
    default RadioButtonGroupState getState() {
        return RadioButtonGroupState.DEFAULT;
    }
}
