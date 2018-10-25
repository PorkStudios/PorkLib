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
import net.daporkchop.lib.binary.Data;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;

import java.io.IOException;

/**
 * A 16x16x256 block section of data
 */
//TODO: entities
//TODO: tile (block) entities
@NoArgsConstructor
public class Column implements Data {
    private final Chunk[] chunks = new Chunk[16];

    @Getter
    private int x;
    @Getter
    private int z;

    @Override
    public void read(DataIn in) throws IOException {
        this.x = in.readInt();
        this.z = in.readInt();
        {
            int mask = in.readShort() & 0xFFFF;
            for (int y = 15; y >= 0; y--) {
                if (((mask >> y) & 1) == 1) {
                    Chunk chunk = new Chunk(this);
                    chunk.read(in);
                    this.chunks[y] = chunk;
                } else {
                    this.chunks[y] = null;
                }
            }
        }
    }

    @Override
    public void write(DataOut out) throws IOException {
        out.writeInt(this.x);
        out.writeInt(this.z);
        {
            int mask = 0;
            for (int y = 15; y >= 0; y--) {
                if (this.chunks[y] != null) {
                    mask |= 1 << y;
                }
            }
            out.writeShort((short) mask);
            for (int y = 15; y >= 0; y--) {
                Chunk chunk = this.chunks[y];
                if (chunk != null) {
                    chunk.write(out);
                }
            }
        }
    }
}
