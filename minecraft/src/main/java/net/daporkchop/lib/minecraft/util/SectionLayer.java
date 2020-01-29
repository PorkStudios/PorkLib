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
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.Arrays;

/**
 * A simple 16³ array of nibbles (unsigned 4-bit values).
 *
 * @author DaPorkchop_
 */
@Getter
@RequiredArgsConstructor
public final class SectionLayer {
    public static int getNibble(byte[] data, int x, int y, int z) {
        int index = (y << 7) | (z << 3) | (x >>> 1);
        return (x & 1) == 0
                ? (data[index] & 0xF)
                : ((data[index] & 0xF0) >>> 4);
    }

    public static void setNibble(byte[] data, int x, int y, int z, int val) {
        int index = (y << 7) | (z << 3) | (x >>> 1);
        data[index] = (x & 1) == 0
                ? (byte) ((data[index] & 0xF0) | val)
                : (byte) ((data[index] & 0xF) | (val << 4));
    }

    @NonNull
    private final byte[] data;

    public SectionLayer() {
        this(new byte[2048]);
    }

    public int get(int x, int y, int z) {
        return getNibble(this.data, x, y, z);
    }

    public void set(int x, int y, int z, int val) {
        setNibble(this.data, x, y, z, val);
    }

    public void fill(int val) {
        PUnsafe.setMemory(this.data, PUnsafe.ARRAY_BYTE_BASE_OFFSET, this.data.length, (byte) ((val << 4) | val));
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
