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
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.common.misc.refcount.RefCounted;
import net.daporkchop.lib.concurrent.PFuture;
import net.daporkchop.lib.minecraft.registry.DimensionRegistry;
import net.daporkchop.lib.minecraft.save.Save;
import net.daporkchop.lib.minecraft.save.SaveOptions;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.version.MinecraftVersion;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.nbt.tag.CompoundTag;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;

/**
 * Base implementation of {@link Save}.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public abstract class AbstractSave<O extends SaveOptions> extends AbstractRefCounted implements Save {
    protected final O options;
    protected final CompoundTag levelData;
    protected final File root;
    protected final MinecraftVersion version;

    public AbstractSave(@NonNull File root, @NonNull SaveOptions options, @NonNull CompoundTag levelData)   {
        this.levelData = levelData;
        this.root = root;
        this.options = this.processOptions(options);
        this.version = this.getVersion();
    }

    protected abstract O processOptions(@NonNull SaveOptions options);

    protected abstract MinecraftVersion getVersion();

    @Override
    public DimensionRegistry dimensions() {
        return null;
    }

    @Override
    public Collection<World> loadedWorlds() {
        return null;
    }

    @Override
    public World world(int id) {
        return null;
    }

    @Override
    public World world(@NonNull Identifier id) {
        return null;
    }

    @Override
    public PFuture<World> loadWorld(int id) {
        return null;
    }

    @Override
    public PFuture<World> loadWorld(@NonNull Identifier id) {
        return null;
    }

    @Override
    public Save retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }

    @Override
    protected void doRelease() {
        this.levelData.release();
    }
}
