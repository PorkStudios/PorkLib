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

package net.daporkchop.lib.minecraft.format.java.decoder.item;

import lombok.NonNull;
import net.daporkchop.lib.minecraft.format.java.JavaSaveOptions;
import net.daporkchop.lib.minecraft.item.ItemMeta;
import net.daporkchop.lib.minecraft.item.ItemStack;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.version.java.JavaVersion;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.nbt.tag.CompoundTag;
import net.daporkchop.lib.nbt.tag.ListTag;
import net.daporkchop.lib.nbt.tag.StringTag;
import net.daporkchop.lib.primitive.map.ObjIntMap;
import net.daporkchop.lib.primitive.map.open.ObjIntOpenHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author DaPorkchop_
 */
public class ItemDecoder1_14 extends ItemDecoder1_13 {
    /*@Override
    protected void getBlocksMeta(@NonNull ItemStack stack, @NonNull CompoundTag tag, @NonNull ItemMeta meta, @NonNull JavaVersion version, @NonNull World world) {
        ListTag<StringTag> canPlaceOn = tag.getList("CanPlaceOn", StringTag.class, null);
        if (canPlaceOn != null) {
            meta.canDestroy(canPlaceOn.stream().map(StringTag::value).map(Identifier::fromString).collect(Collectors.toSet()));
        }

        CompoundTag blockEntityTag = tag.getCompound("BlockEntityTag", null);
        if (blockEntityTag != null) {
            meta.tileEntity(world.parent().options().get(JavaSaveOptions.FIXERS)
                    .tileEntity().ceilingEntry(version).getValue().decode(blockEntityTag, version, world));
        }

        CompoundTag blockStateTag = tag.getCompound("BlockStateTag", null);
        if (blockEntityTag != null) {
            //TODO
        }
    }

    @Override
    protected void getCrossbowMeta(@NonNull ItemStack stack, @NonNull CompoundTag tag, @NonNull ItemMeta meta, @NonNull JavaVersion version, @NonNull World world) {
        ListTag<CompoundTag> chargedProjectiles = tag.getList("ChargedProjectiles", CompoundTag.class, null);
        if (chargedProjectiles != null) {
            meta.chargedProjectiles(chargedProjectiles.stream().map(projectile -> this.decode(projectile, version, world)).collect(Collectors.toList()));
        }

        meta.charged(tag.getBoolean("Charged", false));
    }*/
}
