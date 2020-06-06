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

package net.daporkchop.lib.minecraft.save;

import lombok.NonNull;
import net.daporkchop.lib.common.misc.refcount.RefCounted;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.version.MinecraftVersion;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.io.File;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Representation of a Minecraft save, consisting of one or more {@link World}s identified by numeric IDs and {@link Identifier}s.
 * <p>
 * Every {@link World} loaded by a save keeps a reference to the save which is not released until the {@link World} itself is released.
 *
 * @author DaPorkchop_
 */
public interface Save extends RefCounted {
    /**
     * @return this save's root directory/file
     */
    File root();

    /**
     * @return the {@link MinecraftVersion} that this save was last accessed by
     */
    MinecraftVersion version();

    /**
     * @return the {@link SaveOptions} that this save was opened with
     */
    SaveOptions options();

    /**
     * @return the {@link Identifier}s of all of the worlds present in this save
     */
    Set<Identifier> worldIds();

    /**
     * @return a stream over all of the {@link World}s currently loaded by this save
     */
    Stream<World> worlds();

    /**
     * Gets the {@link World} with the given {@link Identifier}.
     *
     * @param id the {@link Identifier} of the {@link World}
     * @return the {@link World} with the given {@link Identifier}
     * @throws IllegalArgumentException if no world the given {@link Identifier} exists in this save
     */
    World world(@NonNull Identifier id);

    @Override
    Save retain() throws AlreadyReleasedException;
}
