/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2021 DaPorkchop_
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

package net.daporkchop.lib.minecraft.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.minecraft.registry.ResourceLocation;
import net.daporkchop.lib.minecraft.util.world.AbstractDirtiable;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.nbt.tag.notch.CompoundTag;
import net.daporkchop.lib.nbt.tag.notch.DoubleTag;
import net.daporkchop.lib.nbt.tag.notch.ListTag;
import net.daporkchop.lib.unsafe.PUnsafe;

/**
 * Abstract, base implementation of {@link Entity}.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor
@Getter
public abstract class EntityBase extends AbstractDirtiable implements Entity {
    protected static final long CACHEDTAG_OFFSET = PUnsafe.pork_getOffset(EntityBase.class, "cachedTag");

    @Accessors(fluent = true)
    protected World world;

    protected volatile CompoundTag cachedTag;

    protected double x;
    protected double y;
    protected double z;

    @Override
    public synchronized void init(@NonNull World world, @NonNull CompoundTag nbt) {
        if (this.world != null) {
            throw new IllegalStateException("Already initialized!");
        } else if (!this.isValidId(nbt.getString("id"))) {
            throw new IllegalArgumentException(String.format("Invalid NBT tag! This is a \"%s\", cannot load data for \"%s\"!", this.id(), nbt.getString("id")));
        }

        this.world = world;
        ListTag<DoubleTag> pos = nbt.getList("Pos");
        this.x = pos.get(0).getValue();
        this.y = pos.get(1).getValue();
        this.z = pos.get(2).getValue();

        this.doInit(nbt);
        this.cachedTag = nbt;
    }

    /**
     * Method for implementations to define additional initialization logic in.
     *
     * @param nbt the NBT {@link CompoundTag} containing this entity's encoded state
     */
    protected abstract void doInit(@NonNull CompoundTag nbt);

    @Override
    public synchronized void deinit() {
        if (this.world == null) {
            throw new IllegalStateException("Not initialized!");
        }
    }

    /**
     * Method for implementations to define additional deinitialization logic in.
     */
    protected void doDeinit() {
    }

    @Override
    public synchronized CompoundTag save() {
        CompoundTag nbt = this.cachedTag;
        if (nbt == null || this.checkAndResetDirty()) {
            //if this entity was dirty, re-compute cached tag
            nbt = new CompoundTag();

            nbt.putString("id", this.id().toString());
            nbt.putDouble("x", this.x);
            nbt.putDouble("y", this.y);
            nbt.putDouble("z", this.z);

            this.cachedTag = nbt;
        }
        return nbt;
    }

    @Override
    public boolean markDirty() {
        CompoundTag cachedTag = this.cachedTag;
        boolean flag = super.markDirty();
        if (flag) {
            PUnsafe.compareAndSwapObject(this, CACHEDTAG_OFFSET, cachedTag, null);
        }
        return flag;
    }

    /**
     * Method for implementations to define additional save logic in.
     *
     * @param nbt the NBT {@link CompoundTag} to encode this entity's state into
     */
    protected void doSave(@NonNull CompoundTag nbt) {
    }

    /**
     * Checks whether the given text-encoded {@link ResourceLocation} indicates NBT data that can be loaded by this {@link Entity} implementation.
     *
     * @param id a {@link ResourceLocation} in text representation
     * @return whether the given resource location indicates valid NBT for this implementation
     */
    protected boolean isValidId(@NonNull String id) {
        return this.id().equals(id);
    }
}
