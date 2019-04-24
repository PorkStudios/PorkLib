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

package net.daporkchop.lib.logging.console.ansi;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.awt.Color;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public enum VGAColor {
    BLACK(30, 40, new Color(0, 0, 0)),
    RED(31, 41, new Color(170, 0, 0)),
    GREEN(32, 42, new Color(0, 170, 0)),
    YELLOW(33, 43, new Color(170, 170, 0)),
    BLUE(34, 44, new Color(0, 0, 170)),
    MAGENTA(35, 45, new Color(170, 0, 170)),
    CYAN(36, 46, new Color(0, 170, 170)),
    WHITE(37, 47, new Color(170, 170, 170)),
    BRIGHT_BLACK(90, 100, new Color(85, 85, 85)),
    BRIGHT_RED(91, 101, new Color(255, 85, 85)),
    BRIGHT_GREEN(92, 102, new Color(85, 255, 85)),
    BRIGHT_YELLOW(93, 103, new Color(255, 255, 85)),
    BRIGHT_BLUE(94, 104, new Color(85, 85, 255)),
    BRIGHT_MAGENTA(95, 105, new Color(255, 85, 255)),
    BRIGHT_CYAN(96, 106, new Color(85, 255, 255)),
    BRIGHT_WHITE(97, 107, new Color(255, 255, 255))
    ;

    protected static final VGAColor[] COLORS = values();

    protected final int fg;
    protected final int bg;
    protected final int color;

    VGAColor(int fg, int bg, @NonNull Color color)  {
        this(fg, bg, color.getRGB() & 0xFFFFFF);
    }

    public static VGAColor closestTo(@NonNull Color color) {
        return closestTo(color.getRGB());
    }

    public static VGAColor closestTo(int val) {
        int r = (val >>> 16) & 0xFF;
        int g = (val >>> 8) & 0xFF;
        int b = val & 0xFF;

        VGAColor closest = null;
        val = Integer.MAX_VALUE; //reuse old variable for speed and stuff lol
        for (VGAColor color : COLORS)    {
            int vR = r - ((color.color >>> 16) & 0xFF);
            int vG = g - ((color.color >>> 8) & 0xFF);
            int vB = b - (color.color & 0xFF);
            int dist = vR * vR + vG * vG + vB * vB;
            if (dist < val) {
                val = dist;
                closest = color;
            }
        }
        return closest;
    }
}
