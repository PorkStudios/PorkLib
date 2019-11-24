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

package net.daporkchop.lib.minecraft.tileentity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.math.vector.i.Vec3i;
import net.daporkchop.lib.minecraft.registry.ResourceLocation;
import net.daporkchop.lib.minecraft.util.AbstractDirtiable;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.nbt.tag.notch.CompoundTag;
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
     * Checks whether the given text-encoded {@link ResourceLocation} indicates NBT data that can be loaded by this {@link TileEntity} implementation.
     *
     * @param id a {@link ResourceLocation} in text representation
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
