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

import java.io.File;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Implementation of a {@link Spliterator} over the chunks in an Anvil world.
 *
 * @author DaPorkchop_
 */
public class AnvilChunkSpliterator extends AbstractAnvilSpliterator<Chunk> {
    public AnvilChunkSpliterator(@NonNull AnvilWorldStorage storage) {
        super(storage);
    }

    protected AnvilChunkSpliterator(@NonNull AnvilWorldStorage storage, @NonNull File[] regions, int index, int fence) {
        super(storage, regions, index, fence);
    }

    @Override
    public boolean tryAdvance(@NonNull Consumer<? super Chunk> action) {
        try (AnvilCachedChunk cachedChunk = this.next())  {
            if (cachedChunk == null)  {
                return false;
            }
            try (Chunk chunk = cachedChunk.chunk()) {
                action.accept(chunk);
            }
            return true;
        }
    }

    @Override
    protected Spliterator<Chunk> sub(@NonNull AnvilWorldStorage storage, @NonNull File[] regions, int index, int fence) {
        return new AnvilChunkSpliterator(storage, regions, index, fence);
    }
}
