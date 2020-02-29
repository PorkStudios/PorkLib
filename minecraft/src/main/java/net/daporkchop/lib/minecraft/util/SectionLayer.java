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
