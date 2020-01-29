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
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.minecraft.registry.ResourceLocation;
import net.daporkchop.lib.minecraft.tileentity.TileEntityBase;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.nbt.tag.notch.CompoundTag;
import net.daporkchop.lib.nbt.tag.notch.StringTag;

/**
 * Implementation of a {@code minecraft:sign} tile entity.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public final class TileEntitySign extends TileEntityBase {
    public static final ResourceLocation ID = new ResourceLocation("minecraft:sign");

    private String line1;
    private String line2;
    private String line3;
    private String line4;

    @Override
    protected void doInit(@NonNull CompoundTag nbt) {
        this.line1 = nbt.getString("Text1");
        this.line2 = nbt.getString("Text2");
        this.line3 = nbt.getString("Text3");
        this.line4 = nbt.getString("Text4");
    }

    @Override
    protected void doDeinit() {
        this.line1 = this.line2 = this.line3 = this.line4 = null;
    }

    @Override
    protected void doSave(@NonNull CompoundTag nbt) {
        nbt.putString("Text1", this.line1);
        nbt.putString("Text2", this.line2);
        nbt.putString("Text3", this.line3);
        nbt.putString("Text4", this.line4);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public TileEntitySign line1(@NonNull String line)  {
        this.line1 = line;
        this.markDirty();
        return this;
    }

    public TileEntitySign line2(@NonNull String line)  {
        this.line2 = line;
        this.markDirty();
        return this;
    }

    public TileEntitySign line3(@NonNull String line)  {
        this.line3 = line;
        this.markDirty();
        return this;
    }

    public TileEntitySign line4(@NonNull String line)  {
        this.line4 = line;
        this.markDirty();
        return this;
    }

    public TileEntitySign lines(@NonNull String line1, @NonNull String line2, @NonNull String line3, @NonNull String line4) {
        this.line1 = line1;
        this.line2 = line2;
        this.line3 = line3;
        this.line4 = line4;
        this.markDirty();
        return this;
    }

    public TileEntitySign lines(@NonNull String[] lines) {
        if (lines.length != 4)  {
            throw new IllegalArgumentException(String.valueOf(lines.length));
        }
        if (lines[0] == null || lines[1] == null || lines[2] == null || lines[3] == null)   {
            throw new NullPointerException("lines");
        }
        this.line1 = lines[0];
        this.line2 = lines[1];
        this.line3 = lines[2];
        this.line4 = lines[3];
        this.markDirty();
        return this;
    }
}
