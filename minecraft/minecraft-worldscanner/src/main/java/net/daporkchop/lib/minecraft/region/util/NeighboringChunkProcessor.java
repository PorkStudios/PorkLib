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

package net.daporkchop.lib.minecraft.region.util;

import lombok.NonNull;
import net.daporkchop.lib.minecraft.util.BlockAccess;
import net.daporkchop.lib.minecraft.world.BlockArea;
import net.daporkchop.lib.minecraft.world.Chunk;

/**
 * A processor that handles a chunk, along with all neighboring chunks at a time.
 *
 * @author DaPorkchop_
 */
@FunctionalInterface
public interface NeighboringChunkProcessor {
    /**
     * Handles a chunk.
     *
     * @param current        the number of chunks that have been processed until now
     * @param estimatedTotal the estimated total number of chunks
     * @param chunk          the chunk to be processed
     * @param access         an instance of {@link BlockAccess} which allows accessing the data of the chunk. It is guaranteed to contain at
     *                       least the block
     */
    void handle(long current, long estimatedTotal, @NonNull Chunk chunk, @NonNull BlockAccess access);

    /**
     * Gets a bitmask containing all layers that this chunk processor needs in order to run.
     * <p>
     * Defaults to all.
     *
     * @return a bitmask containing all layers that this chunk processor needs
     */
    default int requiredLayers() {
        return BlockArea.ALL_LAYERS;
    }
}
