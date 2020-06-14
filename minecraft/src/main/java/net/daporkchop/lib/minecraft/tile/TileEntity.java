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

package net.daporkchop.lib.minecraft.tile;

import net.daporkchop.lib.compat.Versioned;
import net.daporkchop.lib.math.access.IntHolderXYZ;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.util.dirty.Dirtiable;
import net.daporkchop.lib.minecraft.version.MinecraftVersion;
import net.daporkchop.lib.nbt.tag.CompoundTag;

/**
 * Base representation of a tile entity.
 * <p>
 * All setters for all subinterfaces of this class must also set the dirty flag.
 *
 * @author DaPorkchop_
 */
public interface TileEntity extends IntHolderXYZ, Dirtiable, Versioned<MinecraftVersion> {
    /**
     * @return this tile entity's X coordinate
     */
    @Override
    int x();

    /**
     * @return this tile entity's Y coordinate
     */
    @Override
    int y();

    /**
     * @return this tile entity's Z coordinate
     */
    @Override
    int z();

    /**
     * @return this entity's ID (e.g. {@code "minecraft:ender_chest"})
     */
    Identifier id();

    /**
     * Gets the NBT compound tag that this tile entity was loaded from.
     * <p>
     * This is only guaranteed to be valid when used for the exact same version as this instance (see {@link #version()}), and may not be set at all.
     *
     * @return the NBT compound tag that this tile entity was loaded from
     */
    CompoundTag nbt();
}
