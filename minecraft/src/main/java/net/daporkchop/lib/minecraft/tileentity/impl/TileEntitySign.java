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

package net.daporkchop.lib.minecraft.tileentity.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.minecraft.registry.ResourceLocation;
import net.daporkchop.lib.minecraft.tileentity.TileEntityBase;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.nbt.tag.CompoundTag;
import net.daporkchop.lib.nbt.tag.StringTag;

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
