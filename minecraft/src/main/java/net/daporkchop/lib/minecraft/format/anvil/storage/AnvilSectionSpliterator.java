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

package net.daporkchop.lib.minecraft.format.anvil.storage;

import lombok.NonNull;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.Section;

import java.io.File;
import java.util.Spliterator;
import java.util.function.Consumer;

import static net.daporkchop.lib.common.util.PValidation.checkState;

/**
 * Implementation of a {@link Spliterator} over the chunks in an Anvil world.
 *
 * @author DaPorkchop_
 */
public class AnvilSectionSpliterator extends AbstractAnvilSpliterator<Section> {
    protected AnvilCachedChunk chunk;
    protected int sectionY;

    public AnvilSectionSpliterator(@NonNull AnvilWorldStorage storage) {
        super(storage);
    }

    protected AnvilSectionSpliterator(@NonNull AnvilWorldStorage storage, @NonNull File[] regions, int index, int fence) {
        super(storage, regions, index, fence);
    }

    protected boolean nextChunk() {
        checkState(this.chunk == null);
        if ((this.chunk = this.next()) != null)  {
            this.sectionY = 0;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean tryAdvance(@NonNull Consumer<? super Section> action) {
        while (this.chunk != null || this.nextChunk())    {
            while (this.sectionY < 16)  {
                try (Section section = this.chunk.section(this.sectionY++)) {
                    if (section != null)    {
                        action.accept(section);
                        return true;
                    }
                }
            }

            //if we get this far the chunk is complete
            try {
                this.chunk.release();
            } finally {
                this.chunk = null;
            }
        }
        return false;
    }

    @Override
    public void forEachRemaining(@NonNull Consumer<? super Section> action) {
        this.doForEach(cachedChunk -> {
            for (int y = 0; y < 16; y++)    {
                try (Section section = cachedChunk.section(y))  {
                    if (section != null)    {
                        action.accept(section);
                    }
                }
            }
        });
    }

    @Override
    protected Spliterator<Section> sub(@NonNull AnvilWorldStorage storage, @NonNull File[] regions, int index, int fence) {
        return new AnvilSectionSpliterator(storage, regions, index, fence);
    }
}
