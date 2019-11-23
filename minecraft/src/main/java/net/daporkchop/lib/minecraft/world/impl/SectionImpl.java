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

package net.daporkchop.lib.minecraft.world.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.daporkchop.lib.minecraft.util.NibbleArray;
import net.daporkchop.lib.minecraft.world.Section;
import net.daporkchop.lib.minecraft.world.Chunk;

/**
 * A chunk as defined in Minecraft 1.12.
 * <p>
 * This will need a rework in 1.13 to work with the new local registry system
 *
 * @author DaPorkchop_
 */
@Getter
@Setter
@RequiredArgsConstructor
public class SectionImpl implements Section {
    private final int y;

    private final Chunk chunk;

    private byte[] blockIds;
    private NibbleArray add;
    private NibbleArray meta;
    private NibbleArray blockLight;
    private NibbleArray skyLight;

    @Override
    public int getBlockId(int x, int y, int z) {
        return (this.blockIds[y << 8 | z << 4 | x] & 0xFF) | (this.add == null ? 0 : this.add.get(x, y, z) << 8);
    }

    @Override
    public int getBlockMeta(int x, int y, int z) {
        return this.meta.get(x, y, z);
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        return this.blockLight.get(x, y, z);
    }

    @Override
    public int getSkyLight(int x, int y, int z) {
        return this.skyLight.get(x, y, z);
    }

    @Override
    public void setBlockId(int x, int y, int z, int id) {
        this.blockIds[y << 8 | z << 4 | x] = (byte) (id & 0xFF);
        if (this.add != null) {
            this.add.set(x, y, z, (id >> 8) & 0xF);
        }
    }

    @Override
    public void setBlockMeta(int x, int y, int z, int meta) {
        this.meta.set(x, y, z, meta);
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        this.blockLight.set(x, y, z, level);
    }

    @Override
    public void setSkyLight(int x, int y, int z, int level) {
        this.skyLight.set(x, y, z, level);
    }
}
