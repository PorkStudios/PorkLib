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

package net.daporkchop.lib.imaging.bitmap.impl;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.imaging.bitmap.PBitmap;
import net.daporkchop.lib.imaging.util.exception.BitmapCoordinatesOutOfBoundsException;

/**
 * Base implementation of {@link PBitmap}.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public abstract class AbstractBitmap implements PBitmap {
    //together these fields make a long, so field alignment is all good
    protected final int width;
    protected final int height;

    public AbstractBitmap(int width, int height)    {
        if (width <= 0 || height <= 0)  {
            throw new IllegalArgumentException("width and height must be >0!");
        }

        this.width = width;
        this.height = height;
    }

    protected final void assertInBounds(int x, int y) throws BitmapCoordinatesOutOfBoundsException {
        if (x < 0 || x >= this.width || y < 0 || y >= this.width)   {
            throw new BitmapCoordinatesOutOfBoundsException(x, y);
        }
    }
}
