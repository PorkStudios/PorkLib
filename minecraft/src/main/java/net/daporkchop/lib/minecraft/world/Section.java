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

package net.daporkchop.lib.minecraft.world;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.minecraft.util.BlockAccess;
import net.daporkchop.lib.minecraft.util.world.Dirtiable;
import net.daporkchop.lib.unsafe.capability.Releasable;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * A 16³ section of blocks.
 *
 * @author DaPorkchop_
 */
public interface Section extends BlockAccess, Dirtiable, Releasable {
    Section EMPTY_SECTION = new Empty();

    Chunk chunk();

    int getY();

    @Override
    default boolean dirty() {
        return this.chunk().dirty();
    }

    default boolean markDirty() {
        return this.chunk().markDirty();
    }

    default void save() {
        this.chunk().save();
    }

    @Override
    default int minX() {
        return 0;
    }

    @Override
    default int minY() {
        return 0;
    }

    @Override
    default int minZ() {
        return 0;
    }

    @Override
    default int maxX() {
        return 16;
    }

    @Override
    default int maxY() {
        return 16;
    }

    @Override
    default int maxZ() {
        return 16;
    }

    @Override
    default int getHighestBlock(int x, int z) {
        for (int y = 15; y >= 0; y--)   {
            if (this.getBlockId(x, y, z) != 0)  {
                return y;
            }
        }
        return 0;
    }

    /**
     * An immutable, empty section.
     *
     * @author DaPorkchop_
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    final class Empty implements Section {
        @NonNull
        @Accessors(fluent = true)
        private Chunk chunk;

        private int y;

        @Override
        public int getBlockId(int x, int y, int z) {
            return 0;
        }

        @Override
        public int getBlockMeta(int x, int y, int z) {
            return 0;
        }

        @Override
        public int getBlockLight(int x, int y, int z) {
            return 0;
        }

        @Override
        public int getSkyLight(int x, int y, int z) {
            return 15;
        }

        @Override
        public void setBlockId(int x, int y, int z, int id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setBlockMeta(int x, int y, int z, int meta) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setBlockLight(int x, int y, int z, int level) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setSkyLight(int x, int y, int z, int level) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void release() throws AlreadyReleasedException {
            //no-op
        }
    }
}
