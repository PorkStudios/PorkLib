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

package net.daporkchop.lib.minecraft.region.util;

import lombok.NonNull;
import net.daporkchop.lib.minecraft.util.BlockDataAccess;
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
     * @param access         an instance of {@link BlockDataAccess} which allows accessing the data of the chunk. It is guaranteed to contain at
     *                       least the block
     */
    void handle(long current, long estimatedTotal, @NonNull Chunk chunk, @NonNull BlockDataAccess access);

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
