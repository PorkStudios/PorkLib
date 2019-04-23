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

package net.daporkchop.lib.gui.swing;

import lombok.NonNull;
import net.daporkchop.lib.gui.util.HorizontalAlignment;
import net.daporkchop.lib.gui.util.VerticalAlignment;

import javax.swing.*;

/**
 * @author DaPorkchop_
 */
public interface SwingTextAlignment {
    static VerticalAlignment fromSwingVertical(int alignment) {
        switch (alignment) {
            case SwingConstants.TOP:
                return VerticalAlignment.TOP;
            case SwingConstants.BOTTOM:
                return VerticalAlignment.BOTTOM;
            case SwingConstants.CENTER:
                return VerticalAlignment.CENTER;
            default:
                return null;
        }
    }

    static int toSwingVertical(@NonNull VerticalAlignment alignment) {
        switch (alignment) {
            case TOP:
                return SwingConstants.TOP;
            case BOTTOM:
                return SwingConstants.BOTTOM;
            case CENTER:
                return SwingConstants.CENTER;
            default:
                return -1;
        }
    }

    static HorizontalAlignment fromSwingHorizontal(int alignment) {
        switch (alignment) {
            case SwingConstants.LEFT:
                return HorizontalAlignment.LEFT;
            case SwingConstants.RIGHT:
                return HorizontalAlignment.RIGHT;
            case SwingConstants.CENTER:
                return HorizontalAlignment.CENTER;
            default:
                return null;
        }
    }

    static int toSwingHorizontal(@NonNull HorizontalAlignment alignment) {
        switch (alignment) {
            case LEFT:
                return SwingConstants.LEFT;
            case RIGHT:
                return SwingConstants.RIGHT;
            case CENTER:
                return SwingConstants.CENTER;
            default:
                return -1;
        }
    }
}
