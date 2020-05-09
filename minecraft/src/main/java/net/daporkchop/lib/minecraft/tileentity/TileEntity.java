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

package net.daporkchop.lib.minecraft.tileentity;

import net.daporkchop.lib.math.vector.i.IntVector3;
import net.daporkchop.lib.math.vector.i.Vec3i;
import net.daporkchop.lib.minecraft.registry.ResourceLocation;
import net.daporkchop.lib.minecraft.util.world.WorldMemberPersistent;

/**
 * A tile entity attaches an NBT {@link net.daporkchop.lib.nbt.tag.CompoundTag} to a block position in the world.
 *
 * Technically, they've been officially renamed to "Block Entities" in recent versions of the game, but I prefer "Tile Entity" and am sticking
 * with it as long as feasibly possible.
 *
 * @author DaPorkchop_
 */
public interface TileEntity extends WorldMemberPersistent, IntVector3.AddressableXYZ {
    /**
     * @return this tile entity's ID
     */
    ResourceLocation id();

    /**
     * @return this tile entity's current position
     * @deprecated once {@link Vec3i} is made into an interface, {@link TileEntity} will implement it making this method redundant
     */
    @Deprecated
    default Vec3i pos() {
        return new Vec3i(this.getX(), this.getY(), this.getZ());
    }
}
