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

package net.daporkchop.lib.minecraft.format.common.vanilla;

import lombok.NonNull;
import net.daporkchop.lib.concurrent.PFuture;
import net.daporkchop.lib.concurrent.PFutures;
import net.daporkchop.lib.minecraft.format.common.AbstractChunk;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.world.BlockState;
import net.daporkchop.lib.minecraft.world.Section;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.unsafe.PUnsafe;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * Base implementation of {@link net.daporkchop.lib.minecraft.world.Chunk} for vanilla chunks with exactly 16 sections.
 *
 * @author DaPorkchop_
 */
//this implementation is fast because we assume that each section that is loaded has a reference count of 1 and sections cannot be unloaded, thus
//avoiding lots of otherwise expensive synchronization
public abstract class VanillaChunk extends AbstractChunk {
    protected static void checkCoords(int x, int y, int z) {
        checkIndex(x >= 0 && x < 16, "x (%d)", x);
        checkIndex(y >= 0 && y < 256, "y (%d)", x);
        checkIndex(z >= 0 && z < 16, "z (%d)", z);
    }

    protected final Section[] sections;
    protected final PFuture<Section>[] futures;

    public VanillaChunk(World parent, int x, int z, @NonNull Section[] sections) {
        super(parent, x, z);

        checkArg(sections.length == 16, "sections have be exactly 16 elements! (%d)", sections.length);
        this.sections = sections;
        this.futures = uncheckedCast(new PFuture[16]);
    }

    @Override
    public Section getSection(int y) {
        checkIndex(y >= 0 && y < 16, "y (%d)", y);
        Section section = this.sections[y];
        return section != null ? section.retain() : null;
    }

    @Override
    public Section getOrLoadSection(int y) {
        checkIndex(y >= 0 && y < 16, "y (%d)", y);
        Section section = PUnsafe.getArrayVolatile(this.sections, y);
        if (section == null) {
            section = this.createEmptySection(y); //create new section
            if (!PUnsafe.compareAndSwapArray(this.sections, y, null, section)) { //section was created by another thread
                section.release(); //release new section as it is not going to be used
                section = PUnsafe.getArrayVolatile(this.sections, y); //use newly created one
            }
        }
        return section.retain();
    }

    @Override
    public PFuture<Section> loadSection(int y) {
        checkIndex(y >= 0 && y < 16, "y (%d)", y);
        PFuture<Section> future = PUnsafe.getArrayVolatile(this.sections, y);
        if (future == null) {
            future = PFutures.computeAsync(() -> this.getOrLoadSection(y), this.parent.parent().options().ioExecutor()); //this doesn't actually need to be async, but whatever lol
            if (!PUnsafe.compareAndSwapArray(this.futures, y, null, future)) {
                //future was created by another thread, use the new one. we don't need to cancel the redundant future, getOrLoadSection is atomic
                future = PUnsafe.getArrayVolatile(this.futures, y);
            }
        }
        return future;
    }

    @Override
    protected void doRelease() {
        super.doRelease();
        for (Section section : this.sections) {
            if (section != null) {
                section.release();
            }
        }
    }

    protected void checkLayer(int layer) {
        checkIndex(layer >= 0 && layer < this.layers(), "layer (%d)", layer);
    }

    protected abstract Section createEmptySection(int y);

    //we assume that the calling thread holds a reference to this chunk, and therefore that each section has a reference count of at least 1

    @Override
    public BlockState getBlockState(int x, int y, int z) {
        checkCoords(x, y, z);
        Section section = this.getSection(y >> 4);
        return section != null ? section.getBlockState(x, y & 0xF, z) : this.blockRegistry.air();
    }

    @Override
    public BlockState getBlockState(int x, int y, int z, int layer) {
        checkCoords(x, y, z);
        Section section = this.getSection(y >> 4);
        if (section != null) {
            return section.getBlockState(x, y & 0xF, z, layer);
        } else {
            this.checkLayer(layer);
            return this.blockRegistry.air();
        }
    }

    @Override
    public Identifier getBlockId(int x, int y, int z) {
        checkCoords(x, y, z);
        Section section = this.getSection(y >> 4);
        return section != null ? section.getBlockId(x, y & 0xF, z) : this.blockRegistry.air().id();
    }

    @Override
    public Identifier getBlockId(int x, int y, int z, int layer) {
        checkCoords(x, y, z);
        Section section = this.getSection(y >> 4);
        if (section != null) {
            return section.getBlockId(x, y & 0xF, z, layer);
        } else {
            this.checkLayer(layer);
            return this.blockRegistry.air().id();
        }
    }

    @Override
    public int getBlockLegacyId(int x, int y, int z) {
        checkCoords(x, y, z);
        Section section = this.getSection(y >> 4);
        return section != null ? section.getBlockLegacyId(x, y & 0xF, z) : this.blockRegistry.air().legacyId();
    }

    @Override
    public int getBlockLegacyId(int x, int y, int z, int layer) {
        checkCoords(x, y, z);
        Section section = this.getSection(y >> 4);
        if (section != null) {
            return section.getBlockLegacyId(x, y & 0xF, z, layer);
        } else {
            this.checkLayer(layer);
            return this.blockRegistry.air().legacyId();
        }
    }

    @Override
    public int getBlockMeta(int x, int y, int z) {
        checkCoords(x, y, z);
        Section section = this.getSection(y >> 4);
        return section != null ? section.getBlockMeta(x, y & 0xF, z) : 0;
    }

    @Override
    public int getBlockMeta(int x, int y, int z, int layer) {
        checkCoords(x, y, z);
        Section section = this.getSection(y >> 4);
        if (section != null) {
            return section.getBlockMeta(x, y & 0xF, z, layer);
        } else {
            this.checkLayer(layer);
            return 0;
        }
    }

    @Override
    public int getBlockRuntimeId(int x, int y, int z) {
        checkCoords(x, y, z);
        Section section = this.getSection(y >> 4);
        return section != null ? section.getBlockRuntimeId(x, y & 0xF, z) : 0;
    }

    @Override
    public int getBlockRuntimeId(int x, int y, int z, int layer) {
        checkCoords(x, y, z);
        Section section = this.getSection(y >> 4);
        if (section != null) {
            return section.getBlockRuntimeId(x, y & 0xF, z, layer);
        } else {
            this.checkLayer(layer);
            return 0;
        }
    }

    @Override
    public void setBlockState(int x, int y, int z, @NonNull BlockState state) {
        checkCoords(x, y, z);
        this.getOrLoadSection(y >> 4).setBlockState(x, y & 0xF, z, state);
    }

    @Override
    public void setBlockState(int x, int y, int z, int layer, @NonNull BlockState state) {
        checkCoords(x, y, z);
        this.getOrLoadSection(y >> 4).setBlockState(x, y & 0xF, z, layer, state);
    }

    @Override
    public void setBlockState(int x, int y, int z, @NonNull Identifier id, int meta) {
        checkCoords(x, y, z);
        this.getOrLoadSection(y >> 4).setBlockState(x, y & 0xF, z, id, meta);
    }

    @Override
    public void setBlockState(int x, int y, int z, int layer, @NonNull Identifier id, int meta) {
        checkCoords(x, y, z);
        this.getOrLoadSection(y >> 4).setBlockState(x, y & 0xF, z, layer, id, meta);
    }

    @Override
    public void setBlockState(int x, int y, int z, int legacyId, int meta) {
        checkCoords(x, y, z);
        this.getOrLoadSection(y >> 4).setBlockState(x, y & 0xF, z, legacyId, meta);
    }

    @Override
    public void setBlockState(int x, int y, int z, int layer, int legacyId, int meta) {
        checkCoords(x, y, z);
        this.getOrLoadSection(y >> 4).setBlockState(x, y & 0xF, z, layer, legacyId, meta);
    }

    @Override
    public void setBlockId(int x, int y, int z, @NonNull Identifier id) {
        checkCoords(x, y, z);
        this.getOrLoadSection(y >> 4).setBlockId(x, y & 0xF, z, id);
    }

    @Override
    public void setBlockId(int x, int y, int z, int layer, @NonNull Identifier id) {
        checkCoords(x, y, z);
        this.getOrLoadSection(y >> 4).setBlockId(x, y & 0xF, z, layer, id);
    }

    @Override
    public void setBlockLegacyId(int x, int y, int z, int legacyId) {
        checkCoords(x, y, z);
        this.getOrLoadSection(y >> 4).setBlockLegacyId(x, y & 0xF, z, legacyId);
    }

    @Override
    public void setBlockLegacyId(int x, int y, int z, int layer, int legacyId) {
        checkCoords(x, y, z);
        this.getOrLoadSection(y >> 4).setBlockLegacyId(x, y & 0xF, z, layer, legacyId);
    }

    @Override
    public void setBlockMeta(int x, int y, int z, int meta) {
        checkCoords(x, y, z);
        this.getOrLoadSection(y >> 4).setBlockMeta(x, y & 0xF, z, meta);
    }

    @Override
    public void setBlockMeta(int x, int y, int z, int layer, int meta) {
        checkCoords(x, y, z);
        this.getOrLoadSection(y >> 4).setBlockMeta(x, y & 0xF, z, layer, meta);
    }

    @Override
    public void setBlockRuntimeId(int x, int y, int z, int runtimeId) {
        checkCoords(x, y, z);
        this.getOrLoadSection(y >> 4).setBlockRuntimeId(x, y & 0xF, z, runtimeId);
    }

    @Override
    public void setBlockRuntimeId(int x, int y, int z, int layer, int runtimeId) {
        checkCoords(x, y, z);
        this.getOrLoadSection(y >> 4).setBlockRuntimeId(x, y & 0xF, z, layer, runtimeId);
    }
}
