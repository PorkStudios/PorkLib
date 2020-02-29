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
import net.daporkchop.lib.gui.component.state.functional.ButtonState;
import net.daporkchop.lib.gui.util.handler.ClickHandler;

/**
 * A button can display text or an icon, and has a single click handler that is invoked when the
 * button is clicked.
 *
 * @author DaPorkchop_
 */
public interface Button extends Component<Button, ButtonState>, IconHolder<Button, ButtonState>, TextHolder<Button> {
    ClickHandler getClickHandler();

    Button setClickHandler(ClickHandler handler);

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
