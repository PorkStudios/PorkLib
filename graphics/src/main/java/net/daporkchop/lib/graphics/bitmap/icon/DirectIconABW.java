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

package net.daporkchop.lib.graphics.bitmap.icon;

import net.daporkchop.lib.common.misc.refcount.RefCountedDirectMemory;
import net.daporkchop.lib.graphics.bitmap.PBitmap;
import net.daporkchop.lib.graphics.bitmap.PIcon;
import net.daporkchop.lib.graphics.bitmap.PImage;
import net.daporkchop.lib.graphics.bitmap.image.DirectImageABW;
import net.daporkchop.lib.graphics.bitmap.impl.AbstractDirectBitmapABW;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * An implementation of {@link PIcon} that uses the ABW color format, backed by direct memory.
 *
 * @author DaPorkchop_
 */
public final class DirectIconABW extends AbstractDirectBitmapABW implements PIcon {
    public DirectIconABW(int width, int height) {
        super(width, height);
    }

    public DirectIconABW(int width, int height, Object copySrcRef, long copySrcOff) {
        super(width, height, copySrcRef, copySrcOff);
    }

    public DirectIconABW(int width, int height, RefCountedDirectMemory memory) {
        super(width, height, memory);
    }

    @Override
    public PImage mutableCopy() {
        return new DirectImageABW(this.width, this.height, null, this.ptr);
    }

    @Override
    public PImage unsafeMutableView() {
        return new DirectImageABW(this.width, this.height, this.memory);
    }

    @Override
    public PIcon retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }
}
