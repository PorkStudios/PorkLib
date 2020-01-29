/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.logging.format;

import lombok.experimental.UtilityClass;

/**
 * Flags indicating additional formatting to apply to text
 * 
 * @author DaPorkchop_
 */
@UtilityClass
public class TextStyle {
    public int BOLD = 1 << 0;
    public int ITALIC = 1 << 1;
    public int UNDERLINE = 1 << 2;
    public int STRIKETHROUGH = 1 << 3;
    public int OVERLINE = 1 << 4;
    public int BLINKING = 1 << 5;
    
    public boolean isDefault(int flag)  {
        return flag == 0;
    }

    public boolean isBold(int flag) {
        return (flag & BOLD) != 0;
    }

    public boolean isItalic(int flag) {
        return (flag & ITALIC) != 0;
    }

    public boolean isUnderline(int flag) {
        return (flag & UNDERLINE) != 0;
    }

    public boolean isStrikethrough(int flag) {
        return (flag & STRIKETHROUGH) != 0;
    }

    public boolean isOverline(int flag) {
        return (flag & OVERLINE) != 0;
    }

    public boolean isBlinking(int flag) {
        return (flag & BLINKING) != 0;
    }
}
