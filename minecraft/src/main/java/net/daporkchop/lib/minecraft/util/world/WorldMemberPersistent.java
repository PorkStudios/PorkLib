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

package net.daporkchop.lib.minecraft.util.world;

import lombok.NonNull;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.nbt.tag.CompoundTag;

/**
 * A member of a {@link World} that is stored persistently as an NBT {@link CompoundTag}.
 *
 * @author DaPorkchop_
 */
public interface WorldMemberPersistent extends Dirtiable {
    /**
     * Loads this member from it's NBT-encoded form.
     *
     * @param world the world that this member will be a part of. Implementations of this method must not add this member to the world themselves,
     *              as it is be handled transparently by the world manager
     * @param nbt   this member's NBT data
     * @throws IllegalStateException if this member is already initialized
     */
    void init(@NonNull World world, @NonNull CompoundTag nbt);

    /**
     * De-initializes this member.
     * <p>
     * This should not be called unless you know what you're doing! It can cause the entire world to be left in an unsafe state.
     * <p>
     * After this method is called, the behavior of all other methods is undefined until the next time {@link #init(World, CompoundTag)} is called.
     * @throws IllegalStateException if this member is not currently initialized
     */
    void deinit();

    /**
     * Encodes the current state of this member as an NBT {@link CompoundTag}.
     * <p>
     * The returned {@link CompoundTag} must be able to be loaded again later by an implementation if given to {@link #init(World, CompoundTag)}.
     * <p>
     * Calling this method will this cause instance's {@link #dirty()} flag to be reset.
     *
     * @return this member's current state as an NBT {@link CompoundTag}
     */
    CompoundTag save();
}
