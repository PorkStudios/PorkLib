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

package net.daporkchop.lib.minecraft.tile.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.util.PArrays;
import net.daporkchop.lib.minecraft.text.ChatFormat;
import net.daporkchop.lib.minecraft.tile.AbstractTileEntity;
import net.daporkchop.lib.minecraft.tile.TileEntitySign;
import net.daporkchop.lib.minecraft.version.MinecraftVersion;
import net.daporkchop.lib.nbt.tag.CompoundTag;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class DefaultTileEntitySign extends AbstractTileEntity implements TileEntitySign {
    protected final String[] lines = PArrays.fill(new String[4], "");
    protected ChatFormat color = ChatFormat.BLACK;

    public DefaultTileEntitySign(int x, int y, int z, MinecraftVersion version, CompoundTag nbt) {
        super(x, y, z, version, nbt);
    }

    public DefaultTileEntitySign(@NonNull MinecraftVersion version, @NonNull CompoundTag nbt) {
        super(version, nbt);
    }

    @Override
    public TileEntitySign line(int index, @NonNull String text) {
        this.lines[index] = text;
        this.markDirty();
        return this;
    }

    @Override
    public TileEntitySign color(@NonNull ChatFormat color) {
        this.color = color;
        this.markDirty();
        return this;
    }
}
