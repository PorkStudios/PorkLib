/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

package net.daporkchop.lib.minecraft.world;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.Data;
import net.daporkchop.lib.binary.NBitArray;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;

import java.io.IOException;

/**
 * Represents a single 16³ chunk of data in a Minecraft world
 */
//TODO: huffman compression? (probably not needed)
@RequiredArgsConstructor
public class Chunk implements Data {
    //block ids   (8-bit)
    //meta        (4-bit)
    //sky light   (4-bit) (ignored for now)
    //block light (4-bit) (ignored for now)
    //TOTAL: 20 bits
    private final short[] data = new short[4096];

    @NonNull
    private final Column column;

    @Getter
    private int y;

    @Override
    public void read(DataIn in) throws IOException {
        this.y = in.read();
        for (int i = 4095; i >= 0; i--) {
            this.data[i] = (short) in.read();
        }
        for (int i = 4095; i >= 0; i--) {
            int v = in.read();
            this.data[i--] |= (v & 0xF) << 8;
            this.data[i] |= (v & 0xF0) << 4;
        }
    }

    @Override
    public void write(DataOut out) throws IOException {
        out.write(this.y);
        for (int i = 4095; i >= 0; i--) {
            out.write(this.data[i] & 0xFF);
        }
        for (int i = 4095; i >= 0; i--) {
            out.write(((this.data[i--] >> 8) & 0xF) | (this.data[i] >> 4));
        }
    }

    private int getIndex(int x, int y, int z)    {
        return (x << 8) | (y << 4) | z;
    }

    public int getBlockId(int x, int y, int z)  {
        assert x >= 0;
        assert y >= 0;
        assert z >= 0;
        assert x < 16;
        assert y < 16;
        assert z < 16;

        return this.data[this.getIndex(x, y, z)] & 0xFF;
    }

    public int getBlockMeta(int x, int y, int z)  {
        assert x >= 0;
        assert y >= 0;
        assert z >= 0;
        assert x < 16;
        assert y < 16;
        assert z < 16;

        return (this.data[this.getIndex(x, y, z)] >> 8) & 0xF;
    }
}
