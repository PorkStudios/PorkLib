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
