/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

package net.daporkchop.lib.minecraft.world;

/**
 * A 16x16 block square reaching from the bottom to the top of the world. Pretty
 * much the heart and soul of vanilla Minecraft :P
 * <p>
 * The official name for this is "Chunk", however I've decided to go with the
 * Cubic Chunks naming style so as to allow for compatibility in The Future(tm)
 *
 * @author DaPorkchop_
 */
public interface Column {
    /**
     * @return The total number of chunks in this column.
     */
    int getChunkCount();

    /**
     * Returns a chunk, or null if none is present at the specified height.
     *
     * @param i the index of the chunk. In vanilla this ranges from 0-15
     * @return a chunk, or null if none is present at the specified height.
     */
    Chunk getChunk(int i);

    /**
     * Get the block ID at a given location relative to this column.
     *
     * @param x the x coordinate. must be in range 0-15
     * @param y the y coordinate. in vanilla, must be in range 0-255
     * @param z the x coordinate. must be in range 0-15
     * @return the block id at the given position
     */
    int getBlockId(int x, int y, int z);

    /**
     * Get the block meta at a given location relative to this column.
     *
     * @param x the x coordinate. must be in range 0-15
     * @param y the y coordinate. in vanilla, must be in range 0-255
     * @param z the x coordinate. must be in range 0-15
     * @return the block meta at the given position
     */
    int getBlockMeta(int x, int y, int z);

    /**
     * Get the block light at a given location relative to this column.
     *
     * @param x the x coordinate. must be in range 0-15
     * @param y the y coordinate. in vanilla, must be in range 0-255
     * @param z the x coordinate. must be in range 0-15
     * @return the block light at the given position
     */
    int getBlockLight(int x, int y, int z);

    /**
     * Get the sky light at a given location relative to this column.
     *
     * @param x the x coordinate. must be in range 0-15
     * @param y the y coordinate. in vanilla, must be in range 0-255
     * @param z the x coordinate. must be in range 0-15
     * @return the sky light at the given position
     */
    int getSkyLight(int x, int y, int z);

    /**
     * Set the block ID at a given location relative to this column.
     *
     * @param x  the x coordinate. must be in range 0-15
     * @param y  the y coordinate. in vanilla, must be in range 0-255
     * @param z  the x coordinate. must be in range 0-15
     * @param id the new id
     */
    void setBlockId(int x, int y, int z, int id);

    /**
     * Set the block meta at a given location relative to this column.
     *
     * @param x    the x coordinate. must be in range 0-15
     * @param y    the y coordinate. in vanilla, must be in range 0-255
     * @param z    the x coordinate. must be in range 0-15
     * @param meta the new meta
     */
    void setBlockMeta(int x, int y, int z, int meta);

    /**
     * Set the block light at a given location relative to this column.
     *
     * @param x     the x coordinate. must be in range 0-15
     * @param y     the y coordinate. in vanilla, must be in range 0-255
     * @param z     the x coordinate. must be in range 0-15
     * @param level the new level
     */
    void setBlockLight(int x, int y, int z, int level);

    /**
     * Set the sky light at a given location relative to this column.
     *
     * @param x     the x coordinate. must be in range 0-15
     * @param y     the y coordinate. in vanilla, must be in range 0-255
     * @param z     the x coordinate. must be in range 0-15
     * @param level the new level
     */
    void setSkyLight(int x, int y, int z, int level);
}
