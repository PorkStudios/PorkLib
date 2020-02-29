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

package net.daporkchop.lib.logging.console;

import lombok.NonNull;

import java.awt.Color;

/**
 * A base interface for making fancy-looking consoles.
 * <p>
 * Allows doing stuff like modifying the window title, text color/formatting, or moving the cursor.
 *
 * @author DaPorkchop_
 * @see net.daporkchop.lib.logging.console.ansi.ANSIConsole
 */
public interface Console {
    /**
     * Sets the title of the window.
     * <p>
     * This is not guaranteed to work in all environments, but most of the time it should.
     *
     * @param title the new window title
     */
    void setTitle(@NonNull String title);

    /**
     * Set the text color for all text printed after this method call.
     * <p>
     * No guarantees are made that the output color will be exactly the same as the color passed here as an argument, due to various system
     * limitations. However, implementations are required to have the output color be as similar as the given color as possible.
     *
     * @param rgb the new text color
     */
    default void setTextColorRGB(int rgb) {
        this.setTextColor(new Color(rgb));
    }

    /**
     * Set the text color for all text printed after this method call.
     * <p>
     * No guarantees are made that the output color will be exactly the same as the color passed here as an argument, due to various system
     * limitations. However, implementations are required to have the output color be as similar as the given color as possible.
     *
     * @param color the new text color. If {@code null}, the text color will be reset to default
     */
    void setTextColor(Color color);

    /**
     * Set the background color for all text printed after this method call.
     * <p>
     * No guarantees are made that the output color will be exactly the same as the color passed here as an argument, due to various system
     * limitations. However, implementations are required to have the output color be as similar as the given color as possible.
     *
     * @param rgb the new background color
     */
    default void setBackgroundColorRGB(int rgb) {
        this.setBackgroundColor(new Color(rgb));
    }

    /**
     * Set the background color for all text printed after this method call.
     * <p>
     * No guarantees are made that the output color will be exactly the same as the color passed here as an argument, due to various system
     * limitations. However, implementations are required to have the output color be as similar as the given color as possible.
     *
     * @param color the new background color. If {@code null}, the background color will be reset to default
     */
    void setBackgroundColor(Color color);

    /**
     * Makes all text printed after this method call be bold (or not).
     * @param state whether or not text should be bold
     */
    void setBold(boolean state);

    /**
     * Makes all text printed after this method call be italic (or not).
     * @param state whether or not text should be italic
     */
    void setItalic(boolean state);

    /**
     * Makes all text printed after this method call be underline (or not).
     * @param state whether or not text should be underline
     */
    void setUnderline(boolean state);

    /**
     * Makes all text printed after this method call be strikethrough (or not).
     * @param state whether or not text should be strikethrough
     */
    void setStrikethrough(boolean state);

    /**
     * Makes all text printed after this method call be overline (or not).
     * @param state whether or not text should be overline
     */
    void setOverline(boolean state);

    /**
     * Makes all text printed after this method call be blinking (or not).
     * @param state whether or not text should be blinking
     */
    void setBlinking(boolean state);

    /**
     * Sets all configurable aspects of text formatting at once for all text printed after this method call.
     *
     * @param format the new formatting to use. If {@code null}, all formatting will be reset to default.
     */
    void setFormat(TextFormat format);

    /**
     * Resets all text formatting to the defaults for all text printed after this method call.
     */
    default void resetFormat()  {
        this.setFormat(null);
    }
}
