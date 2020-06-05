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

package net.daporkchop.lib.minecraft.format.common.vanilla;

import lombok.NonNull;
import net.daporkchop.lib.common.util.PArrays;
import net.daporkchop.lib.concurrent.PFuture;
import net.daporkchop.lib.minecraft.format.common.AbstractChunk;
import net.daporkchop.lib.minecraft.world.Section;
import net.daporkchop.lib.minecraft.world.World;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Base implementation of {@link net.daporkchop.lib.minecraft.world.Chunk} for vanilla chunks with exactly 16 sections.
 *
 * @author DaPorkchop_
 */
public abstract class VanillaChunk extends AbstractChunk {
    protected final Section[] sections;
    protected final Lock readLock;
    protected final Lock writeLock;

    public VanillaChunk(World parent, int x, int z, @NonNull Section[] sections) {
        super(parent, x, z);

        checkArg(sections.length == 16, "sections have be exactly 16 elements! (%d)", sections.length);
        this.sections = sections;

        ReadWriteLock lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }

    @Override
    public Section getSection(int y) {
        checkIndex(y >= 0 && y < 16, "y (%d)", y);
        this.readLock.lock();
        try {
            this.ensureNotReleased();
            return this.sections[y].retain();
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public Section getOrLoadSection(int y) {
        return this.getSection(y);
    }

    @Override
    public PFuture<Section> loadSection(int y) {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    protected void doRelease() {
        this.writeLock.lock();
        try {
            super.doRelease();
        } finally {
            this.writeLock.unlock();
        }
    }
}
