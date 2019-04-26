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
import net.daporkchop.lib.logging.console.Console;
import net.daporkchop.lib.logging.console.TextFormat;
import net.daporkchop.lib.logging.format.TextStyle;
import net.daporkchop.lib.logging.impl.DefaultLogger;

import java.awt.Color;
import java.io.PrintStream;
import java.util.StringJoiner;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class ANSIConsole implements ANSI, Console {
    @NonNull
    protected final PrintStream printer;

    //cached values because reasons
    protected VGAColor textColor = VGAColor.DEFAULT;
    protected VGAColor backgroundColor = VGAColor.DEFAULT;
    protected int style = 0;

    protected static String getUpdateTextFormatCommand(VGAColor textColor, VGAColor backgroundColor, int style)  {
        return String.format(
                "%c[0;%d;%d%sm",
                ESC,
                textColor.fg,
                backgroundColor.bg,
                getStyleStuff(style)
        );
    }

    protected static CharSequence getStyleStuff(int style)    {
        if (TextStyle.isDefault(style)) {
            return "";
        } else {
            StringBuilder builder = new StringBuilder(); //TODO: pool these
            if (TextStyle.isBold(style))    {
                builder.append(";1");
            }
            if (TextStyle.isItalic(style))  {
                builder.append(";3");
            }
            if (TextStyle.isUnderline(style))   {
                builder.append(";4");
            }
            if (TextStyle.isStrikethrough(style))   {
                builder.append(";9");
            }
            if (TextStyle.isOverline(style))    {
                builder.append(";53");
            }
            if (TextStyle.isBlinking(style))    {
                builder.append(";5");
            }
            return builder;
        }
    }

    public ANSIConsole()    {
        this(DefaultLogger.stdOut);
    }

    @Override
    public void setTitle(@NonNull String title) {
        this.printer.printf("%c]0;%s%c", ESC, title, BEL);
    }

    @Override
    public void setTextColor(Color color) {
        this.setTextColor(VGAColor.closestTo(color));
    }

    public void setTextColor(@NonNull VGAColor color)   {
        this.textColor = color;
        this.updateTextFormat();
    }

    @Override
    public void setBackgroundColor(Color color) {
        this.setBackgroundColor(VGAColor.closestTo(color));
    }

    public void setBackgroundColor(@NonNull VGAColor color)   {
        this.backgroundColor = color;
        this.updateTextFormat();
    }

    @Override
    public void setBold(boolean state) {
        if (state)  {
            this.style |= TextStyle.BOLD;
        } else if (TextStyle.isBold(this.style)) {
            this.style &= ~TextStyle.BOLD;
        }
        this.updateTextFormat();
    }

    @Override
    public void setItalic(boolean state) {
        if (state)  {
            this.style |= TextStyle.ITALIC;
        } else if (TextStyle.isItalic(this.style)) {
            this.style &= ~TextStyle.ITALIC;
        }
        this.updateTextFormat();
    }

    @Override
    public void setUnderline(boolean state) {
        if (state)  {
            this.style |= TextStyle.UNDERLINE;
        } else if (TextStyle.isUnderline(this.style)) {
            this.style &= ~TextStyle.UNDERLINE;
        }
        this.updateTextFormat();
    }

    @Override
    public void setStrikethrough(boolean state) {
        if (state)  {
            this.style |= TextStyle.STRIKETHROUGH;
        } else if (TextStyle.isStrikethrough(this.style)) {
            this.style &= ~TextStyle.STRIKETHROUGH;
        }
        this.updateTextFormat();
    }

    @Override
    public void setOverline(boolean state) {
        if (state)  {
            this.style |= TextStyle.OVERLINE;
        } else if (TextStyle.isOverline(this.style)) {
            this.style &= ~TextStyle.OVERLINE;
        }
        this.updateTextFormat();
    }

    @Override
    public void setBlinking(boolean state) {
        if (state)  {
            this.style |= TextStyle.BLINKING;
        } else if (TextStyle.isBlinking(this.style)) {
            this.style &= ~TextStyle.BLINKING;
        }
        this.updateTextFormat();
    }

    @Override
    public void setFormat(TextFormat format) {
        if (format == null) {
            this.textColor = this.backgroundColor = VGAColor.DEFAULT;
            this.style = 0;
        } else {
            this.textColor = VGAColor.closestTo(format.getTextColor());
            this.backgroundColor = VGAColor.closestTo(format.getBackgroundColor());
            this.style = format.getStyle();
        }
        this.updateTextFormat();
    }

    protected void updateTextFormat()   {
        this.printer.print(getUpdateTextFormatCommand(this.textColor, this.backgroundColor, this.style));
    }
}
