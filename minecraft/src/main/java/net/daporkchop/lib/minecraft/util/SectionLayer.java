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

package net.daporkchop.lib.minecraft.util;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.encoding.Hexadecimal;

import java.util.Arrays;

/**
 * A simple 16³ array of nibbles (unsigned 4-bit values).
 *
 * @author DaPorkchop_
 */
@Getter
@RequiredArgsConstructor
public final class SectionLayer {
    @NonNull
    private final byte[] data;

    public SectionLayer() {
        this(new byte[2048]);
    }

    public int get(int x, int y, int z) {
        int key = y << 8 | z << 4 | x;
        int index = key >> 1;
        int part = key & 1;
        return part == 0 ? this.data[index] & 15 : this.data[index] >> 4 & 15;
    }

    public void set(int x, int y, int z, int val) {
        int key = y << 8 | z << 4 | x;
        int index = key >> 1;
        int part = key & 1;
        if (part == 0) {
            this.data[index] = (byte) (this.data[index] & 240 | val & 15);
        } else {
            this.data[index] = (byte) (this.data[index] & 15 | (val & 15) << 4);
        }
    }

    public void fill(int val) {
        for (int index = 0; index < this.data.length << 1; index++) {
            int ind = index >> 1;
            int part = index & 1;
            if (part == 0) {
                this.data[ind] = (byte) (this.data[ind] & 240 | val & 15);
            } else {
                this.data[ind] = (byte) (this.data[ind] & 15 | (val & 15) << 4);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SectionLayer)) {
            return false;
        }

        SectionLayer sectionLayer = (SectionLayer) o;
        return Arrays.equals(this.data, sectionLayer.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.data);
    }

    @Override
    public String toString() {
        return String.format("SectionLayer(%s)", Hexadecimal.encode(this.data));
    }
}
