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

package net.daporkchop.lib.minecraft.text;

import lombok.Getter;

import java.awt.Color;
import java.util.Arrays;

/**
 * All format+color codes from the legacy formatting system.
 * <p>
 * See https://minecraft.gamepedia.com/Formatting_codes for more information.
 *
 * @author DaPorkchop_
 * @see MCTextType#LEGACY
 */
@Getter
public enum MCTextFormat {
    // color codes
    BLACK('0', 0, 0),
    DARK_BLUE('1', 0x0000AA, 0x00002A),
    DARK_GREEN('2', 0x00AA00, 0x002A00),
    DARK_AQUA('3', 0x00AAAA, 0x002A2A),
    DARK_RED('4', 0xAA0000, 0x2A0000),
    DARK_PURPLE('5', 0xAA00AA, 0x2A002A),
    GOLD('6', 0xFFAA00, 0x2A2A00),
    GRAY('7', 0xAAAAAA, 0x2A2A2A),
    DARK_GRAY('8', 0x555555, 0x151515),
    BLUE('9', 0x5555FF, 0x15153F),
    GREEN('a', 0x55FF55, 0x153F15),
    AQUA('b', 0x55FFFF, 0x153F3F),
    RED('c', 0xFF5555, 0x3F1515),
    LIGHT_PURPLE('d', 0xFF55FF, 0x3F153F),
    YELLOW('e', 0xFFFF55, 0x3F3F15),
    WHITE('f', 0xFFFFFF, 0x3F3F3F),
    // format codes
    OBFUSCATED('k'),
    BOLD('l'),
    STRIKETHROUGH('m'),
    UNDERLINE('n'),
    ITALIC('o'),
    RESET('r'),
    ;

    public static final MCTextFormat[] VALUES = values();
    public static final MCTextFormat[] COLOR_CODES = Arrays.stream(VALUES).filter(format -> format.color != -1 && format.bgColor != -1).toArray(MCTextFormat[]::new);

    /**
     * The single-letter identifier for this formatting code.
     */
    protected final char code;

    /**
     * The in-game color of this formatting code.
     * <p>
     * If {@code -1}, this formatting code has no color.
     */
    protected final int color;
    /**
     * The in-game background color of this formatting code.
     * <p>
     * Used for drawing the text shadow in-game.
     * <p>
     * If {@code -1}, this formatting code has no color.
     */
    protected final int bgColor;

    MCTextFormat(char code, int color, int bgColor) {
        this.code = code;
        this.color = color;
        this.bgColor = bgColor;
    }

    MCTextFormat(char code) {
        this(code, -1, -1);
    }

    public static MCTextFormat closestTo(Color color) {
        return color == null ? null : closestTo(color.getRGB());
    }

    public static MCTextFormat closestTo(int val) {
        int r = (val >>> 16) & 0xFF;
        int g = (val >>> 8) & 0xFF;
        int b = val & 0xFF;

        MCTextFormat closest = null;
        val = Integer.MAX_VALUE; //reuse old variable for speed and stuff lol
        for (MCTextFormat color : COLOR_CODES)    {
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
