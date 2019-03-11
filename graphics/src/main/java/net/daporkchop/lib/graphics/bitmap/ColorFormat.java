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

package net.daporkchop.lib.graphics.bitmap;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Accessors(fluent = true)
public enum ColorFormat {
    ARGB(c -> c, c -> c, c -> (c >>> 24) & 0xFF, c -> (c >>> 16) & 0xFF, c -> (c >>> 8) & 0xFF, c -> c & 0xFF, 32, true, false),
    RGB(c -> c & 0xFFFFFF, c -> c, c -> 0xFF, c -> (c >>> 16) & 0xFF, c -> (c >>> 8) & 0xFF, c -> c & 0xFF, 24, false, false),
    ABW(c -> ((c >>> 16) & 0xFFFF) | ((c >>> 8) & 0xFF) | (c & 0xFF), c -> ((c & 0xFF00) << 16) | ((c & 0xFF) << 16) | ((c & 0xFF) << 8) | (c & 0xFF), c -> (c >>> 8) & 0xFF, c -> c & 0xFF, c -> c & 0xFF, c -> c & 0xFF, 16, true, true),
    BW(c -> ((c >>> 16) & 0xFF) | ((c >>> 8) & 0xFF) | (c & 0xFF), c -> 0xFF000000 | (c << 16) | (c << 8) | c, c -> 0xFF, c -> c, c -> c, c -> c, 8, false, true);

    @NonNull
    protected final ColorConverter fromArgb;
    @NonNull
    protected final ColorConverter toArgb;
    @NonNull
    protected final ColorConverter getA;
    @NonNull
    protected final ColorConverter getR;
    @NonNull
    protected final ColorConverter getG;
    @NonNull
    protected final ColorConverter getB;
    @Getter
    protected final int bits;
    @Getter
    protected final boolean hasAlpha;
    @Getter
    protected final boolean isBw;

    public int fromArgb(int argb) {
        return this.fromArgb.get(argb);
    }

    public int toArgb(int color) {
        return this.toArgb.get(color);
    }

    public int getA(int color) {
        return this.getA.get(color);
    }

    public int getR(int color) {
        return this.getR.get(color);
    }

    public int getG(int color) {
        return this.getG.get(color);
    }

    public int getB(int color) {
        return this.getB.get(color);
    }

    @FunctionalInterface
    private interface ColorConverter {
        int get(int color);
    }
}
