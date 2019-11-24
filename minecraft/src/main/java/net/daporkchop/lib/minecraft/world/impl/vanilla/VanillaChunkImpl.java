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

package net.daporkchop.lib.minecraft.world.impl.vanilla;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.math.vector.i.Vec2i;
import net.daporkchop.lib.math.vector.i.Vec3i;
import net.daporkchop.lib.minecraft.tileentity.TileEntity;
import net.daporkchop.lib.minecraft.world.Section;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.unsafe.capability.Releasable;

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
        {
            Map<Vec3i, TileEntity> tes = this.world.loadedTileEntities();
            this.tileEntities.forEach(te -> tes.remove(te.pos(), te));
        }

        for (int y = 15; y >= 0; y--) {
            Section section = this.sections[y];
            this.sections[y] = null;
            if (section instanceof Releasable) {
                ((Releasable) section).release();
            }
        }
    }
}
