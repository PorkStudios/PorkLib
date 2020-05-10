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

package net.daporkchop.lib.minecraft.tileentity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.math.vector.i.Vec3i;
import net.daporkchop.lib.minecraft.registry.Identifier;
import net.daporkchop.lib.minecraft.util.world.AbstractDirtiable;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.nbt.tag.CompoundTag;
import net.daporkchop.lib.unsafe.PUnsafe;

/**
 * Abstract, base implementation of {@link TileEntity}.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor
@Getter
@Accessors(fluent = true)
public abstract class TileEntityBase extends AbstractDirtiable implements TileEntity {
    protected static final long CACHEDTAG_OFFSET = PUnsafe.pork_getOffset(TileEntityBase.class, "cachedTag");

    protected World world;
    protected Vec3i pos;

    protected volatile CompoundTag cachedTag;

    @Override
    public synchronized void init(@NonNull World world, @NonNull CompoundTag nbt) {
        if (this.world != null) {
            throw new IllegalStateException("Already initialized!");
        } else if (!this.isValidId(nbt.getString("id"))) {
            throw new IllegalArgumentException(String.format("Invalid NBT tag! This is a \"%s\", cannot load data for \"%s\"!", this.id(), nbt.getString("id")));
        }

        this.world = world;
        this.pos = new Vec3i(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));

        this.doInit(nbt);
    }

    /**
     * Method for implementations to define additional initialization logic in.
     *
     * @param nbt the NBT {@link CompoundTag} containing this tile entity's encoded state
     */
    protected void doInit(@NonNull CompoundTag nbt) {
    }

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
        if (this.dirty() || nbt == null) {
            synchronized (this) {
                if (this.checkAndResetDirty()) {
                    //if this tile entity was dirty, re-compute cached tag
                    nbt = new CompoundTag();

                    nbt.putString("id", this.id().toString());
                    nbt.putInt("x", this.pos.getX());
                    nbt.putInt("y", this.pos.getY());
                    nbt.putInt("z", this.pos.getZ());

                    this.cachedTag = nbt;
                } else if ((nbt = this.cachedTag) == null) {
                    throw new IllegalStateException("Cached tag was still null, even after waiting for other thread to compute it!");
                }
            }
        }
        return nbt;
    }

    @Override
    public boolean markDirty() {
        CompoundTag cachedTag = this.cachedTag;
        boolean flag = super.markDirty();
        if (flag)   {
            PUnsafe.compareAndSwapObject(this, CACHEDTAG_OFFSET, cachedTag, null);
        }
        return flag;
    }

    /**
     * Method for implementations to define additional save logic in.
     *
     * @param nbt the NBT {@link CompoundTag} to encode this tile entity's state into
     */
    protected void doSave(@NonNull CompoundTag nbt) {
    }

    /**
     * Checks whether the given text-encoded {@link Identifier} indicates NBT data that can be loaded by this {@link TileEntity} implementation.
     *
     * @param id a {@link Identifier} in text representation
     * @return whether the given resource location indicates valid NBT for this implementation
     */
    protected boolean isValidId(@NonNull String id) {
        return this.id().equals(id);
    }

    @Override
    public int getX() {
        return this.pos.getX();
    }

    @Override
    public int getY() {
        return this.pos.getY();
    }

    @Override
    public int getZ() {
        return this.pos.getZ();
    }
}
