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

package net.daporkchop.lib.minecraft.world.impl.section;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.minecraft.util.SectionLayer;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.Section;
import net.daporkchop.lib.nbt.alloc.NBTArrayHandle;
import net.daporkchop.lib.nbt.tag.notch.ByteArrayTag;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * A chunk section stored on the heap.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public final class HeapSectionImpl implements Section {
    private byte[]       blocks;
    private SectionLayer add;
    private SectionLayer meta;
    private SectionLayer blockLight;
    private SectionLayer skyLight;

    private NBTArrayHandle<byte[]> blocksHandle;
    private NBTArrayHandle<byte[]> addHandle;
    private NBTArrayHandle<byte[]> metaHandle;
    private NBTArrayHandle<byte[]> blockLightHandle;
    private NBTArrayHandle<byte[]> skyLightHandle;

    private final Chunk chunk;

    @Accessors(fluent = false)
    private final int y;

    public HeapSectionImpl(int y, Chunk chunk) {
        this.chunk = chunk;
        this.y = y;
    }

    @Override
    public int getBlockId(int x, int y, int z) {
        return (this.blocks[y << 8 | z << 4 | x] & 0xFF) | (this.add == null ? 0 : this.add.get(x, y, z) << 8);
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
        this.blocks[y << 8 | z << 4 | x] = (byte) (id & 0xFF);
        if (this.add != null) {
            this.add.set(x, y, z, (id >>> 8) & 0xF);
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

    @Override
    public void release() throws AlreadyReleasedException {
        if (this.blocks != null)    {
            this.blocks = null;
            if (this.blocksHandle != null)  {
                this.blocksHandle.release();
                this.blocksHandle = null;
            }
        }
        if (this.add != null)    {
            this.add = null;
            if (this.addHandle != null)  {
                this.addHandle.release();
                this.addHandle = null;
            }
        }
        if (this.meta != null)    {
            this.meta = null;
            if (this.metaHandle != null)  {
                this.metaHandle.release();
                this.metaHandle = null;
            }
        }
        if (this.blockLight != null)    {
            this.blockLight = null;
            if (this.blockLightHandle != null)  {
                this.blockLightHandle.release();
                this.blockLightHandle = null;
            }
        }
        if (this.skyLight != null)    {
            this.skyLight = null;
            if (this.skyLightHandle != null)  {
                this.skyLightHandle.release();
                this.skyLightHandle = null;
            }
        }
    }

    //setters
    public void setBlocks(ByteArrayTag tag) {
        this.blocks = tag == null ? null : tag.value();
        this.blocksHandle = tag == null ? null : tag.handle();
    }

    public void setAdd(ByteArrayTag tag) {
        this.add = tag == null ? null : new SectionLayer(tag.value());
        this.addHandle = tag == null ? null : tag.handle();
    }

    public void setMeta(ByteArrayTag tag) {
        this.meta = tag == null ? null : new SectionLayer(tag.value());
        this.metaHandle = tag == null ? null : tag.handle();
    }

    public void setBlockLight(ByteArrayTag tag) {
        this.blockLight = tag == null ? null : new SectionLayer(tag.value());
        this.blockLightHandle = tag == null ? null : tag.handle();
    }

    public void setSkyLight(ByteArrayTag tag) {
        this.skyLight = tag == null ? null : new SectionLayer(tag.value());
        this.skyLightHandle = tag == null ? null : tag.handle();
    }
}
