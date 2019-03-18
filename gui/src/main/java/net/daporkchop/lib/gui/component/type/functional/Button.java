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
import net.daporkchop.lib.gui.component.state.functional.ButtonState;
import net.daporkchop.lib.gui.util.event.handler.ClickHandler;

/**
 * A button can display text or an icon, and has a single click handler that is invoked when the
 * button is clicked.
 *
 * @author DaPorkchop_
 */
public interface Button extends Component<Button, ButtonState>, IconHolder<Button, ButtonState>, TextHolder<Button> {
    ClickHandler getClickHandler();

    Button setClickHandler(@NonNull ClickHandler handler);

    @Override
    default ButtonState getState() {
        return this.isVisible() ?
                this.isEnabled() ?
                        this.isHovered() ?
                                this.isMouseDown() ?
                                        ButtonState.ENABLED_CLICKED : ButtonState.ENABLED_HOVERED
                                : ButtonState.ENABLED
                        : this.isHovered() ? ButtonState.DISABLED_HOVERED : ButtonState.DISABLED
                : ButtonState.HIDDEN;
    }
}
