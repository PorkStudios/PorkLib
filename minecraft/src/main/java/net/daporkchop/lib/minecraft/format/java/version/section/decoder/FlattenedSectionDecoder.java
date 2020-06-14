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

package net.daporkchop.lib.minecraft.format.java.version.section.decoder;

import lombok.NonNull;
import net.daporkchop.lib.binary.bit.packed.PackedBitArray;
import net.daporkchop.lib.common.math.BinMath;
import net.daporkchop.lib.minecraft.block.BlockRegistry;
import net.daporkchop.lib.minecraft.block.BlockState;
import net.daporkchop.lib.minecraft.block.Property;
import net.daporkchop.lib.minecraft.format.common.storage.BlockStorage;
import net.daporkchop.lib.minecraft.format.common.storage.palette.PalettedBlockStorage;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.util.palette.ArrayPalette;
import net.daporkchop.lib.minecraft.util.palette.Palette;
import net.daporkchop.lib.minecraft.version.java.JavaVersion;
import net.daporkchop.lib.nbt.tag.CompoundTag;
import net.daporkchop.lib.nbt.tag.ListTag;
import net.daporkchop.lib.nbt.tag.LongArrayTag;
import net.daporkchop.lib.nbt.tag.StringTag;
import net.daporkchop.lib.nbt.tag.Tag;

import java.util.Map;

/**
 * @author DaPorkchop_
 */
public class FlattenedSectionDecoder extends LegacySectionDecoder {
    public static final JavaVersion VERSION = JavaVersion.fromName("1.15.2");

    @Override
    protected BlockStorage parseBlockStorage(@NonNull CompoundTag tag, @NonNull BlockRegistry blockRegistry) {
        ListTag<CompoundTag> paletteTag = tag.getList("Palette", CompoundTag.class);
        LongArrayTag blockStatesTag = tag.getTag("BlockStates");

        int bits = Math.max(BinMath.getNumBitsNeededFor(paletteTag.size()), 4);
        Palette palette = this.parseBlockPalette(bits, paletteTag, blockRegistry);

        if (blockStatesTag.handle() != null) {
            return new PalettedBlockStorage(blockRegistry, new PackedBitArray(bits, 4096, blockStatesTag.handle().retain()), palette);
        } else {
            return new PalettedBlockStorage(blockRegistry, new PackedBitArray(bits, 4096, blockStatesTag.value()), palette);
        }
    }

    protected Palette parseBlockPalette(int bits, @NonNull ListTag<CompoundTag> paletteTag, @NonNull BlockRegistry blockRegistry) {
        Palette palette = new ArrayPalette(bits);
        for (CompoundTag tag : paletteTag)  {
            BlockState state = blockRegistry.getDefaultState(Identifier.fromString(tag.getString("Name")));

            CompoundTag properties = tag.getCompound("Properties", null);
            if (properties != null) {
                //set properties
                for (Map.Entry<String, Tag> entry : properties) {
                    Property<Object> property = state.property(entry.getKey());
                    state = state.withProperty(property, property.decodeValue(((StringTag) entry.getValue()).value()));
                }
            }

            palette.get(state.runtimeId());
        }
        //System.out.println(paletteTag);
        //System.exit(1);
        return palette;
    }
}
