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
        public int getBiomeId(int x, int z) {
            return 0;
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
        public void setBiomeId(int x, int z, int id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setBiomeArray(byte[] biomes) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void release() throws AlreadyReleasedException {
            //no-op
        }
    }
}
