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

package net.daporkchop.lib.minecraft.format.java.decoder.tile;

import lombok.NonNull;
import net.daporkchop.lib.minecraft.format.java.JavaSaveOptions;
import net.daporkchop.lib.minecraft.format.java.decoder.JavaTileEntityDecoder;
import net.daporkchop.lib.minecraft.tileentity.TileEntity;
import net.daporkchop.lib.minecraft.tileentity.TileEntityJukebox;
import net.daporkchop.lib.minecraft.version.java.JavaVersion;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.nbt.tag.CompoundTag;

/**
 * @author DaPorkchop_
 */
public class JukeboxDecoder1_8 implements JavaTileEntityDecoder {
    @Override
    public TileEntity decode(@NonNull CompoundTag tag, @NonNull JavaVersion version, @NonNull World world) {
        TileEntityJukebox jukebox = new TileEntityJukebox();

        //custom name
        CompoundTag record = tag.getCompound("RecordItem", null);
        if (record != null) {
            jukebox.record(world.parent().options().get(JavaSaveOptions.FIXERS)
                    .item().ceilingEntry(version).getValue().decode(record, version, world));
        }
        return jukebox;
    }
}
