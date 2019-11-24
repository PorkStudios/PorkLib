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
import net.daporkchop.lib.minecraft.util.AbstractDirtiable;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.nbt.tag.notch.CompoundTag;

/**
 * Abstract, base implementation of {@link TileEntity}.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor
@Getter
@Accessors(fluent = true)
public abstract class TileEntityBase extends AbstractDirtiable implements TileEntity {
    protected World world;
    protected Vec3i pos;

    @Override
    public synchronized void init(@NonNull World world, @NonNull CompoundTag nbt) {
        if (this.world != null) {
            throw new IllegalStateException("Already initialized!");
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
        if (!this.id().equals(nbt.getString("id"))) {
            throw new IllegalArgumentException(String.format("Invalid NBT tag! This is a \"%s\", cannot load data for \"%s\"!", this.id(), nbt.getString("id")));
        }
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
    public CompoundTag save() {
        CompoundTag nbt = new CompoundTag();

        nbt.putString("id", this.id().toString());
        nbt.putInt("x", this.pos.getX());
        nbt.putInt("y", this.pos.getY());
        nbt.putInt("z", this.pos.getZ());

        return nbt;
    }

    /**
     * Method for implementations to define additional save logic in.
     *
     * @param nbt the NBT {@link CompoundTag} to encode this tile entity's state into
     */
    protected void doSave(@NonNull CompoundTag nbt) {
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
