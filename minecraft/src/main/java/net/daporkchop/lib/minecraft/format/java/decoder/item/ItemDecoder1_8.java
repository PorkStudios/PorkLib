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
import net.daporkchop.lib.minecraft.format.java.JavaFixers;
import net.daporkchop.lib.minecraft.format.java.JavaSaveOptions;
import net.daporkchop.lib.minecraft.format.java.decoder.JavaItemDecoder;
import net.daporkchop.lib.minecraft.item.ItemMeta;
import net.daporkchop.lib.minecraft.item.ItemStack;
import net.daporkchop.lib.minecraft.registry.Registry;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.version.java.JavaVersion;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.nbt.tag.CompoundTag;
import net.daporkchop.lib.nbt.tag.ListTag;
import net.daporkchop.lib.nbt.tag.StringTag;
import net.daporkchop.lib.primitive.map.ObjIntMap;
import net.daporkchop.lib.primitive.map.open.ObjIntOpenHashMap;

import java.util.stream.Collectors;

/**
 * @author DaPorkchop_
 */
public class ItemDecoder1_8 implements JavaItemDecoder {
    @Override
    public ItemStack decode(@NonNull CompoundTag root, @NonNull JavaVersion version, @NonNull World world) {
        CompoundTag tag = root.getCompound("tag", null);
        return new ItemStack(this.getId(root, tag), this.getCount(root, tag), this.getDamage(root, tag), this.getMeta(root, tag, version, world));
    }

    protected Identifier getId(@NonNull CompoundTag root, CompoundTag tag) {
        return Identifier.fromString(root.getString("id", "stone"));
    }

    protected int getCount(@NonNull CompoundTag root, CompoundTag tag) {
        return root.getByte("Count", (byte) 1);
    }

    protected int getDamage(@NonNull CompoundTag root, CompoundTag tag) {
        return root.getShort("Damage", (short) 0);
    }

    protected ItemMeta getMeta(@NonNull CompoundTag root, CompoundTag tag, @NonNull JavaVersion version, @NonNull World world) {
        if (this.hasMeta(tag)) {
            ItemMeta meta = new ItemMeta();
            this.getGeneralMeta(tag, meta, version, world);
            this.getBlocksMeta(tag, meta, version, world);
            this.getEnchantmentsMeta(tag, meta, version, world);
            return meta;
        } else {
            return null;
        }
    }

    protected boolean hasMeta(CompoundTag tag) {
        return tag != null && tag.size() > 0 && (tag.size() > 1 || !tag.contains("Damage"));
    }

    protected void getGeneralMeta(@NonNull CompoundTag tag, @NonNull ItemMeta meta, @NonNull JavaVersion version, @NonNull World world) {
        meta.unbreakable(tag.getBoolean("Unbreakable", false));

        ListTag<StringTag> canDestroy = tag.getList("CanDestroy", StringTag.class, null);
        if (canDestroy != null) {
            meta.canDestroy(canDestroy.stream().map(StringTag::value).map(Identifier::fromString).collect(Collectors.toSet()));
        }

        if (tag.contains("CustomModelData")) {
            meta.customModelData(tag.getInt("CustomModelData"));
        }
    }

    protected void getBlocksMeta(@NonNull CompoundTag tag, @NonNull ItemMeta meta, @NonNull JavaVersion version, @NonNull World world) {
        ListTag<StringTag> canPlaceOn = tag.getList("CanPlaceOn", StringTag.class, null);
        if (canPlaceOn != null) {
            meta.canDestroy(canPlaceOn.stream().map(StringTag::value).map(Identifier::fromString).collect(Collectors.toSet()));
        }

        CompoundTag blockEntityTag = tag.getCompound("BlockEntityTag", null);
        if (blockEntityTag != null) {
            meta.tileEntity(world.parent().options().get(JavaSaveOptions.FIXERS)
                    .tileEntity().ceilingEntry(version).getValue().decode(blockEntityTag, version, world));
        }

        //TODO: 1.8 uses the item damage to select the block state
    }

    protected void getEnchantmentsMeta(@NonNull CompoundTag tag, @NonNull ItemMeta meta, @NonNull JavaVersion version, @NonNull World world) {
        ListTag<CompoundTag> enchantments = tag.getList("ench", CompoundTag.class, null);
        if (enchantments != null) {
            ObjIntMap<Identifier> map = new ObjIntOpenHashMap<>();
            Registry enchantmentRegistry = world.parent().registriesFor(version).get(Identifier.fromString("minecraft:enchantment"));
            enchantments.forEach(enchantment -> map.put(enchantmentRegistry.get(enchantment.getShort("id")), enchantment.getShort("lvl")));
            meta.enchantments(map);
        }

        enchantments = tag.getList("StoredEnchantments", CompoundTag.class, null);
        if (enchantments != null) {
            ObjIntMap<Identifier> map = new ObjIntOpenHashMap<>();
            enchantments.forEach(enchantment -> map.put(Identifier.fromString(enchantment.getString("id")), enchantment.getInt("lvl")));
            meta.storedEnchantments(map);
        }

        meta.repairCost(tag.getInt("RepairCost", 0));
    }
}
