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

package net.daporkchop.lib.logging.console;

import lombok.NonNull;

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
     *
     * This is not guaranteed to work in all environments, but most of the time it should.
     * @param title the new window title
     */
    void setTitle(@NonNull String title);

    /**
     * Set the text color for all text printed after this method call
     * @param color the new text color
     */
    void setTextColor(int color);

    void setTextColor(int r, int g, int b);
}
