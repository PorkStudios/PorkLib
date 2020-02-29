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

package net.daporkchop.lib.gui.util;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public enum Alignment {
    TOP_LEFT(HorizontalAlignment.LEFT, VerticalAlignment.TOP),
    TOP_CENTER(HorizontalAlignment.CENTER, VerticalAlignment.TOP),
    TOP_RIGHT(HorizontalAlignment.RIGHT, VerticalAlignment.TOP),
    CENTER_LEFT(HorizontalAlignment.LEFT, VerticalAlignment.CENTER),
    CENTER(HorizontalAlignment.CENTER, VerticalAlignment.CENTER),
    CENTER_RIGHT(HorizontalAlignment.RIGHT, VerticalAlignment.CENTER),
    BOTTOM_LEFT(HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM),
    BOTTOM_CENTER(HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM),
    BOTTOM_RIGHT(HorizontalAlignment.RIGHT, VerticalAlignment.BOTTOM);
    
    @NonNull
    protected final HorizontalAlignment horizontal;
    
    @NonNull
    protected final VerticalAlignment vertical;
    
    public static Alignment getFrom(@NonNull HorizontalAlignment horizontal, @NonNull VerticalAlignment vertical)   {
        switch (vertical) {
            case TOP:
                switch (horizontal) {
                    case LEFT:
                        return TOP_LEFT;
                    case CENTER:
                        return TOP_CENTER;
                    case RIGHT:
                        return TOP_RIGHT;
                }
            case CENTER:
                switch (horizontal) {
                    case LEFT:
                        return CENTER_LEFT;
                    case CENTER:
                        return CENTER;
                    case RIGHT:
                        return CENTER_RIGHT;
                }
            case BOTTOM:
                switch (horizontal) {
                    case LEFT:
                        return BOTTOM_LEFT;
                    case CENTER:
                        return BOTTOM_CENTER;
                    case RIGHT:
                        return BOTTOM_RIGHT;
                }
        }
        throw new IllegalStateException(String.format("horizontal=%s,vertical=%s", horizontal, vertical));
    }
}
