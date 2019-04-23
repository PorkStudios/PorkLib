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

package net.daporkchop.lib.gui.component.state.functional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.daporkchop.lib.gui.component.state.ElementState;
import net.daporkchop.lib.gui.component.type.functional.CheckBox;
import net.daporkchop.lib.gui.component.type.functional.RadioButton;

/**
 * @author DaPorkchop_
 */
@AllArgsConstructor
@Getter
public enum RadioButtonState implements ElementState<RadioButton, RadioButtonState> {
    ENABLED(true, true, false, false),
    ENABLED_HOVERED(true, true, true, false),
    ENABLED_SELECTED(true, true, false, true),
    ENABLED_HOVERED_SELECTED(true, true, true, true),
    DISABLED(true, false, false, false),
    DISABLED_HOVERED(true, false, true, false),
    DISABLED_SELECTED(true, false, false, true),
    DISABLED_HOVERED_SELECTED(true, false, true, true),
    HIDDEN(false, false, false, false),
    ;

    protected boolean visible;
    protected boolean enabled;
    protected boolean hovered;
    protected boolean selected;
}
