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

package net.daporkchop.lib.minecraft.tileentity.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.minecraft.registry.ResourceLocation;
import net.daporkchop.lib.minecraft.tileentity.TileEntityBase;
import net.daporkchop.lib.nbt.tag.notch.CompoundTag;

/**
 * Represents an unknown (unregistered) tile entity.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public final class UnknownTileEntity extends TileEntityBase {
    protected ResourceLocation id;
    protected CompoundTag data;

    @Override
    protected void doInit(@NonNull CompoundTag nbt) {
        this.data = nbt; //TODO: some way to make immutable tags? i should probably rewrite NBT lib to use interfaces for each tag type
        this.id = new ResourceLocation(nbt.getString("id"));
    }

    @Override
    protected void doDeinit() {
        this.id = null;
        this.data = null;
    }

    @Override
    public synchronized CompoundTag save() {
        return this.data;
    }

    @Override
    protected boolean isValidId(@NonNull String id) {
        //accept everything, it really doesn't matter (as long as it's not null)
        return true;
    }

    @Override
    public boolean dirty() {
        return false;
    }

    @Override
    public boolean markDirty() {
        throw new UnsupportedOperationException("markDirty");
    }

    @Override
    protected void resetDirty() {
        throw new UnsupportedOperationException("resetDirty");
    }

    @Override
    protected boolean checkAndResetDirty() {
        throw new UnsupportedOperationException("checkAndResetDirty");
    }
}
