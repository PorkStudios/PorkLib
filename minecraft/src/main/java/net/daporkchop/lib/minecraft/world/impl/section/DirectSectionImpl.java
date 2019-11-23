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
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.Section;
import net.daporkchop.lib.unsafe.PCleaner;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.unsafe.capability.AccessibleDirectMemoryHolder;
import net.daporkchop.lib.unsafe.capability.DirectMemoryHolder;
import net.daporkchop.lib.unsafe.capability.Releasable;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * An off-heap chunk section.
 *
 * @author DaPorkchop_
 */
public final class DirectSectionImpl implements Section, AccessibleDirectMemoryHolder {
    public static final long SIZE_BLOCK        = (16 * 16 * 16) << 1;
    public static final long SIZE_NIBBLE_LAYER = (16 * 16 * 16) >>> 1;
    public static final long FULL_SIZE         = SIZE_BLOCK + SIZE_NIBBLE_LAYER * 3;

    public static final long OFFSET_BLOCK       = 0L;
    public static final long OFFSET_META        = OFFSET_BLOCK + SIZE_BLOCK;
    public static final long OFFSET_BLOCK_LIGHT = OFFSET_META + SIZE_NIBBLE_LAYER;
    public static final long OFFSET_SKY_LIGHT   = OFFSET_BLOCK_LIGHT + SIZE_NIBBLE_LAYER;

    public static int getNibble(long addr, int x, int y, int z) {
        addr += (y << 7) | (z << 3) | (x >>> 1);
        return (x & 1) == 0
                ? (PUnsafe.getByte(addr) & 0xF)
                : ((PUnsafe.getByte(addr) & 0xF0) >>> 4);
    }

    public static void setNibble(long addr, int x, int y, int z, int val) {
        addr += (y << 7) | (z << 3) | (x >>> 1);
        PUnsafe.putByte(addr, (x & 1) == 0
                ? (byte) ((PUnsafe.getByte(addr) & 0xF0) | val)
                : (byte) ((PUnsafe.getByte(addr) & 0xF) | (val << 4)));
    }

    protected final long addr;

    @Getter
    protected final Chunk    chunk;
    protected final PCleaner cleaner;

    @Getter
    protected final int y;

    public DirectSectionImpl(int y, Chunk chunk) {
        this.chunk = chunk;
        this.y = y;

        this.cleaner = PCleaner.cleaner(this, this.addr = PUnsafe.allocateMemory(FULL_SIZE));
    }

    @Override
    public int getBlockId(int x, int y, int z) {
        return PUnsafe.getChar(this.addr + ((y << 9) | (z << 5) | (x << 1)));
    }

    @Override
    public int getBlockMeta(int x, int y, int z) {
        return getNibble(this.addr + OFFSET_META, x, y, z);
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        return getNibble(this.addr + OFFSET_BLOCK_LIGHT, x, y, z);
    }

    @Override
    public int getSkyLight(int x, int y, int z) {
        return getNibble(this.addr + OFFSET_SKY_LIGHT, x, y, z);
    }

    @Override
    public void setBlockId(int x, int y, int z, int id) {
        PUnsafe.putChar(this.addr + ((y << 9) | (z << 5) | (x << 1)), (char) (id & 0xFFF));
    }

    @Override
    public void setBlockMeta(int x, int y, int z, int meta) {
        setNibble(this.addr + OFFSET_META, x, y, z, meta);
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        setNibble(this.addr + OFFSET_BLOCK_LIGHT, x, y, z, level);
    }

    @Override
    public void setSkyLight(int x, int y, int z, int level) {
        setNibble(this.addr + OFFSET_SKY_LIGHT, x, y, z, level);
    }

    @Override
    public void release() throws AlreadyReleasedException {
        if (!this.cleaner.tryClean()) {
            throw new AlreadyReleasedException();
        }
    }

    @Override
    public long memoryAddress() throws AlreadyReleasedException {
        return this.addr;
    }

    @Override
    public long memorySize() {
        return FULL_SIZE;
    }
}
