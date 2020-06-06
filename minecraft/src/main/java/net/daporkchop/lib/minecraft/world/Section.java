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

package net.daporkchop.lib.minecraft.world;

import net.daporkchop.lib.common.misc.refcount.RefCounted;
import net.daporkchop.lib.math.access.IntHolderXYZ;
import net.daporkchop.lib.minecraft.block.BlockAccess;
import net.daporkchop.lib.minecraft.format.common.storage.BlockStorage;
import net.daporkchop.lib.minecraft.format.common.nibble.NibbleArray;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * Representation of a Minecraft chunk section, consisting of a 16³ volume of blocks, along with light levels for block and (optionally) sky light.
 *
 * @author DaPorkchop_
 */
public interface Section extends BlockAccess, LightAccess, IntHolderXYZ, RefCounted {
    /**
     * @return the {@link Chunk} that loaded this section
     */
    Chunk parent();

    /**
     * @return this section's X coordinate
     */
    @Override
    int x();

    /**
     * @return this section's Y coordinate
     */
    @Override
    int y();

    /**
     * @return this section's Z coordinate
     */
    @Override
    int z();

    /**
     * @return the {@link BlockStorage} used by this section for storing block data
     */
    BlockStorage blockStorage();

    /**
     * @return the {@link NibbleArray} used by this section for storing block light data
     */
    NibbleArray blockLightStorage();

    /**
     * @return the {@link NibbleArray} used by this section for storing sky light data
     * @throws UnsupportedOperationException if this section does not have sky light (see {@link #hasSkyLight()})
     */
    NibbleArray skyLightStorage();

    @Override
    int refCnt();

    @Override
    Section retain() throws AlreadyReleasedException;

    @Override
    boolean release() throws AlreadyReleasedException;
}
