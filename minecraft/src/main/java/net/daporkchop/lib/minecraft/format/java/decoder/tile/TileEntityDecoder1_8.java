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
import net.daporkchop.lib.logging.format.component.TextComponent;
import net.daporkchop.lib.minecraft.format.java.JavaSaveOptions;
import net.daporkchop.lib.minecraft.format.java.decoder.JavaItemDecoder;
import net.daporkchop.lib.minecraft.format.java.decoder.JavaTileEntityDecoder;
import net.daporkchop.lib.minecraft.item.inventory.DefaultInventory;
import net.daporkchop.lib.minecraft.item.inventory.Inventory;
import net.daporkchop.lib.minecraft.text.parser.MCFormatParser;
import net.daporkchop.lib.minecraft.tileentity.DefaultTileEntity;
import net.daporkchop.lib.minecraft.tileentity.TileEntity;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.version.java.JavaVersion;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.nbt.tag.CompoundTag;
import net.daporkchop.lib.nbt.tag.Tag;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import static net.daporkchop.lib.minecraft.tileentity.TileEntity.*;

/**
 * @author DaPorkchop_
 */
public class TileEntityDecoder1_8 implements JavaTileEntityDecoder {
    protected final Map<String, TileEntityDecoder> map = new HashMap<>();
    protected final ThreadLocal<Deque<CompoundTag>> overflow = ThreadLocal.withInitial(ArrayDeque::new);

    public TileEntityDecoder1_8() {
        this.map.put("x", TileEntityDecoder.NOOP);
        this.map.put("y", TileEntityDecoder.NOOP);
        this.map.put("z", TileEntityDecoder.NOOP);
        this.map.put("id", TileEntityDecoder.NOOP);

        this.map.put("keepPacked", (tile, tag) -> tile.put(KEEP_PACKED, tag.getBoolean("keepPacked")));
        this.map.put("Items", (AdvancedTileEntityDecoder) (tile, tag, version, world) -> {
            JavaItemDecoder decoder = world.parent().options().get(JavaSaveOptions.FIXERS).item().ceilingEntry(version).getValue();
            Inventory inventory = new DefaultInventory();
            tag.getList("Items", CompoundTag.class).forEach(item ->
                    inventory.set(item.getByte("Slot"), decoder.decode(item, version, world)));
            tile.put(TileEntity.INVENTORY, inventory);
        });
        this.map.put("Lock", (tile, tag) -> tile.put(LOCK, tag.getString("Lock")));

        this.map.put("CookTime", (tile, tag) -> tile.put(FURNACE_COOK_TIME, (int) tag.getShort("CookTime")));
        this.map.put("BurnTime", (tile, tag) -> tile.put(FURNACE_COOK_TIME, (int) tag.getShort("BurnTime")));
        this.map.put("CookTimeTotal", (tile, tag) -> tile.put(FURNACE_COOK_TIME, (int) tag.getShort("CookTimeTotal")));

        TileEntityDecoder sign = (tile, tag) -> {
            if (!tile.has(SIGN_TEXT)) {
                tile.put(SIGN_TEXT, new TextComponent[]{
                        MCFormatParser.DEFAULT.parse(tag.getString("Text1", "\"\"")),
                        MCFormatParser.DEFAULT.parse(tag.getString("Text2", "\"\"")),
                        MCFormatParser.DEFAULT.parse(tag.getString("Text3", "\"\"")),
                        MCFormatParser.DEFAULT.parse(tag.getString("Text4", "\"\""))
                });
            }
        };
        this.map.put("Text1", sign);
        this.map.put("Text2", sign);
        this.map.put("Text3", sign);
        this.map.put("Text4", sign);

        this.map.put("BrewTime", (tile, tag) -> tile.put(BREW_TIME, (int) tag.getShort("BrewTime")));

        this.map.put("RecordItem", (AdvancedTileEntityDecoder) (tile, tag, version, world) -> tile.put(JUKEBOX_RECORD,
                world.parent().options().get(JavaSaveOptions.FIXERS).item().ceilingEntry(version).getValue()
                        .decode(tag.getCompound("RecordItem"), version, world)));
    }

    protected TileEntityDecoder1_8(@NonNull TileEntityDecoder1_8 parent) {
        this.map.putAll(parent.map);
    }

    @Override
    public TileEntity decode(@NonNull CompoundTag root, @NonNull JavaVersion version, @NonNull World world) {
        Deque<CompoundTag> overflowQueue = this.overflow.get();
        CompoundTag overflow = overflowQueue.isEmpty() ? new CompoundTag() : overflowQueue.pop();

        TileEntity tile = new DefaultTileEntity(this.getId(root));

        root.forEach((key, value) -> {
            TileEntityDecoder decoder = this.map.get(key);
            if (decoder != null) {
                decoder.decode(tile, root, version, world);
            } else {
                overflow.putTag(key, ((Tag) value).clone());
            }
        });

        if (overflow.isEmpty()) { //unknown NBT tags were found, store them
            overflowQueue.push(overflow);
        } else {
            tile.put(TileEntity.UNKNOWN_NBT, overflow);
        }
        return tile;
    }

    protected Identifier getId(@NonNull CompoundTag root) {
        String id = root.getString("id");
        return Identifier.fromString(LegacyIdLookup.LOOKUP.getOrDefault(id, id));
    }

    @FunctionalInterface
    public interface TileEntityDecoder {
        TileEntityDecoder NOOP = (tile, tag) -> {};

        default void decode(@NonNull TileEntity tile, @NonNull CompoundTag tag, @NonNull JavaVersion version, @NonNull World world) {
            this.decode(tile, tag);
        }

        void decode(@NonNull TileEntity tile, @NonNull CompoundTag tag);
    }

    @FunctionalInterface
    public interface AdvancedTileEntityDecoder extends TileEntityDecoder {
        @Override
        void decode(@NonNull TileEntity tile, @NonNull CompoundTag tag, @NonNull JavaVersion version, @NonNull World world);

        @Override
        default void decode(@NonNull TileEntity tile, @NonNull CompoundTag tag) {
            throw new UnsupportedOperationException();
        }
    }

    private static class LegacyIdLookup {
        protected static final Map<String, String> LOOKUP = new HashMap<>();

        static {
            LOOKUP.put("Airportal", "minecraft:end_portal");
            LOOKUP.put("Banner", "minecraft:banner");
            LOOKUP.put("Beacon", "minecraft:beacon");
            LOOKUP.put("Cauldron", "minecraft:brewing_stand");
            LOOKUP.put("Chest", "minecraft:chest");
            LOOKUP.put("Comparator", "minecraft:comparator");
            LOOKUP.put("Control", "minecraft:command_block");
            LOOKUP.put("DLDetector", "minecraft:daylight_detector");
            LOOKUP.put("Dropper", "minecraft:dropper");
            LOOKUP.put("EnchantTable", "minecraft:enchanting_table");
            LOOKUP.put("EndGateway", "minecraft:end_gateway");
            LOOKUP.put("EnderChest", "minecraft:ender_chest");
            LOOKUP.put("FlowerPot", "minecraft:flower_pot");
            LOOKUP.put("Furnace", "minecraft:furnace");
            LOOKUP.put("Hopper", "minecraft:hopper");
            LOOKUP.put("MobSpawner", "minecraft:mob_spawner");
            LOOKUP.put("Music", "minecraft:noteblock");
            LOOKUP.put("Piston", "minecraft:piston");
            LOOKUP.put("RecordPlayer", "minecraft:jukebox");
            LOOKUP.put("Sign", "minecraft:sign");
            LOOKUP.put("Skull", "minecraft:skull");
            LOOKUP.put("Structure", "minecraft:structure_block");
            LOOKUP.put("Trap", "minecraft:dispenser");
        }
    }
}
