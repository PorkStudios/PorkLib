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
