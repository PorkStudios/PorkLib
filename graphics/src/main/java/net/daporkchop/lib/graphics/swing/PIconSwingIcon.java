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

package net.daporkchop.lib.graphics.swing;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.graphics.PIcon;

import javax.swing.*;
import java.awt.*;

/**
 * A very, very inefficient implementation of {@link Icon} that uses a {@link PIcon} to store pixel values.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class PIconSwingIcon implements Icon {
    @NonNull
    protected final PIcon delegate;

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        for (int xx = this.delegate.getWidth() - 1; xx >= 0; xx--)  {
            for (int yy = this.delegate.getHeight() - 1; yy >= 0; yy--) {
                g.setPaintMode();
                g.setColor(new Color(this.delegate.getARGB(xx, yy)));
                g.drawRect(x + xx, y + yy, 1, 1);
            }
        }
    }

    @Override
    public int getIconWidth() {
        return this.delegate.getWidth();
    }

    @Override
    public int getIconHeight() {
        return this.delegate.getHeight();
    }
}
