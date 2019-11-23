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

package net.daporkchop.lib.minecraft.world.impl.section;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.Section;
import net.daporkchop.lib.unsafe.PCleaner;
import net.daporkchop.lib.unsafe.capability.Releasable;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * An off-heap chunk section.
 *
 * @author DaPorkchop_
 */
@Getter
public final class DirectSectionImpl implements Section, Releasable {
    protected final long pos;

    protected final Chunk chunk;
    protected final PCleaner cleaner;

    protected final int y;



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
        return 0;
    }

    @Override
    public void setBlockId(int x, int y, int z, int id) {
    }

    @Override
    public void setBlockMeta(int x, int y, int z, int meta) {
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
    }

    @Override
    public void setSkyLight(int x, int y, int z, int level) {
    }

    @Override
    public void release() throws AlreadyReleasedException {
        if (!this.cleaner.tryClean())   {
            throw new AlreadyReleasedException();
        }
    }
}
