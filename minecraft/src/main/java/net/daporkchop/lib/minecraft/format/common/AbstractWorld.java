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

package net.daporkchop.lib.minecraft.format.common;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.collections.map.map2d.Map2d;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.concurrent.PFuture;
import net.daporkchop.lib.minecraft.registry.BlockRegistry;
import net.daporkchop.lib.minecraft.save.Save;
import net.daporkchop.lib.minecraft.save.SaveOptions;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.minecraft.world.WorldProvider;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * Base implementation of {@link World}.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public abstract class AbstractWorld<S extends Save, O extends SaveOptions> extends AbstractRefCounted implements World {
    protected final S parent;
    protected final O options;
    protected final Identifier id;
    protected final BlockRegistry blockRegistry;

    protected final WorldProvider provider;

    protected final Map2d<Chunk> chunks;
    protected final Map2d<PFuture<Chunk>> loadingChunks;

    public AbstractWorld(@NonNull S parent, @NonNull O options, @NonNull Identifier id) {
        this.parent = parent;
        this.options = options;

        this.chunks = null;
        this.loadingChunks = null; //TODO

        this.id = id;

        this.blockRegistry = this.getBlockRegistry0();
        this.provider = this.getProvider0();
    }

    protected abstract BlockRegistry getBlockRegistry0();

    protected abstract WorldProvider getProvider0();

    @Override
    public World retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }

    @Override
    protected void doRelease() {
        this.provider.release();
    }
}
