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

package net.daporkchop.lib.minecraft.format.common.block;

import net.daporkchop.lib.common.misc.Cloneable;
import net.daporkchop.lib.common.misc.refcount.RefCounted;
import net.daporkchop.lib.minecraft.block.BlockRegistry;
import net.daporkchop.lib.minecraft.block.BlockAccess;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * A 16³ array of block states.
 *
 * @author DaPorkchop_
 * @see BlockRegistry
 */
public interface BlockStorage extends BlockAccess, Cloneable<BlockStorage>, RefCounted {
    /**
     * The number of blocks in a single block storage.
     */
    int NUM_BLOCKS = 16 * 16 * 16;

    static void checkCoords(int x, int y, int z) {
        checkIndex(x >= 0 && x < 16, "x");
        checkIndex(y >= 0 && y < 16, "y");
        checkIndex(z >= 0 && z < 16, "z");
    }

    @Override
    int refCnt();

    @Override
    BlockStorage retain() throws AlreadyReleasedException;

    @Override
    boolean release() throws AlreadyReleasedException;
}
