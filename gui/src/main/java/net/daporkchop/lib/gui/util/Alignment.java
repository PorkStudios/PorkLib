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
