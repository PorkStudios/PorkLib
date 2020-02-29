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

package net.daporkchop.lib.minecraft.world.impl.vanilla;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.math.vector.i.Vec2i;
import net.daporkchop.lib.math.vector.i.Vec3i;
import net.daporkchop.lib.minecraft.tileentity.TileEntity;
import net.daporkchop.lib.minecraft.world.Section;
import net.daporkchop.lib.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Map;

/**
 * An implementation of a Chunk for vanilla Minecraft.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public final class VanillaChunkImpl extends AbstractVanillaChunk {
    @NonNull
    private final Vec2i pos;

    private final Collection<TileEntity> tileEntities = new ArrayDeque<>();
    private final Section[]              sections     = new Section[16];

    public VanillaChunkImpl(@NonNull Vec2i pos, World world) {
        super(world);

        this.pos = pos;
    }

    @Override
    public Section section(int y) {
        return this.sections[y];
    }

    @Override
    public void section(int y, Section section) {
        this.sections[y] = section;
    }

    @Override
    protected void doUnload() {
        super.doUnload();

        {
            Map<Vec3i, TileEntity> tes = this.world.loadedTileEntities();
            this.tileEntities.forEach(te -> tes.remove(te.pos(), te));
        }

        for (int y = 15; y >= 0; y--) {
            if (this.sections[y] != null) {
                this.sections[y].release();
                this.sections[y] = null;
            }
        }
    }
}
