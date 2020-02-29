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
