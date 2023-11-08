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

package net.daporkchop.lib.gui.component.state;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.daporkchop.lib.gui.component.type.Window;

/**
 * @author DaPorkchop_
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum WindowState implements ElementState<Window, WindowState> {
    /**
     * The window is still in it's construction phase (it has not yet been made visible for the first time).
     */
    CONSTRUCTION(false, false),
    /**
     * The window is visible, and is the currently active window.
     */
    VISIBLE(true, true),
    /**
     * The window is visible, but minimized.
     */
    VISIBLE_MINIMIZED(true, true),
    /**
     * The window is visible, but is not the currently active window.
     */
    VISIBLE_INACTIVE(true, true),
    /**
     * The window has been hidden programmatically.
     */
    HIDDEN(false, true),
    /**
     * The window has been closed by the user.
     */
    CLOSED(false, false),
    /**
     * The window has been released (by calling {@link Window#release()}), and is no longer usable.
     */
    RELEASED(false, false),;

    protected boolean visible = false;
    protected boolean enabled = true;

    @Override
    public boolean isHovered() {
        return false;
    }
}
