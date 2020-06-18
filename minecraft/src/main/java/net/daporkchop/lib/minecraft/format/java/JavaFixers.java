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

package net.daporkchop.lib.minecraft.format.java;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.minecraft.format.anvil.decoder.chunk.FlattenedChunkDecoder;
import net.daporkchop.lib.minecraft.format.anvil.decoder.chunk.LegacyChunkDecoder;
import net.daporkchop.lib.minecraft.format.java.decoder.JavaChunkDecoder;
import net.daporkchop.lib.minecraft.format.java.decoder.JavaItemDecoder;
import net.daporkchop.lib.minecraft.format.java.decoder.JavaSectionDecoder;
import net.daporkchop.lib.minecraft.format.java.decoder.JavaTileEntityDecoder;
import net.daporkchop.lib.minecraft.format.java.decoder.item.ItemDecoder1_13;
import net.daporkchop.lib.minecraft.format.java.decoder.item.ItemDecoder1_8;
import net.daporkchop.lib.minecraft.format.java.decoder.item.ItemDecoder1_9;
import net.daporkchop.lib.minecraft.format.java.decoder.section.FlattenedSectionDecoder;
import net.daporkchop.lib.minecraft.format.java.decoder.section.LegacySectionDecoder;
import net.daporkchop.lib.minecraft.format.java.decoder.tile.ChestDecoder1_8;
import net.daporkchop.lib.minecraft.format.java.decoder.tile.SignDecoder1_8;
import net.daporkchop.lib.minecraft.format.java.decoder.tile.SignDecoder1_14;
import net.daporkchop.lib.minecraft.format.java.decoder.tile.UnknownTileDecoder;
import net.daporkchop.lib.minecraft.tileentity.TileEntityChest;
import net.daporkchop.lib.minecraft.tileentity.TileEntitySign;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.version.java.JavaVersion;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class JavaFixers {
    @RequiredArgsConstructor
    public static final class MapBuilder<K, V, M extends Map<K, V>> {
        @NonNull
        protected final M map;

        public MapBuilder<K, V, M> put(K key, V value) {
            this.map.put(key, value);
            return this;
        }

        public M build() {
            return this.map;
        }
    }

    private static final class Default {
        private static final JavaFixers DEFAULT_JAVA_FIXERS = new JavaFixers(
                new MapBuilder<>(new TreeMap<JavaVersion, JavaChunkDecoder>())
                        .put(LegacyChunkDecoder.VERSION, new LegacyChunkDecoder())
                        .put(FlattenedChunkDecoder.VERSION, new FlattenedChunkDecoder())
                        .build(),
                new MapBuilder<>(new TreeMap<JavaVersion, JavaSectionDecoder>())
                        .put(LegacySectionDecoder.VERSION, new LegacySectionDecoder())
                        .put(FlattenedSectionDecoder.VERSION, new FlattenedSectionDecoder())
                        .build(),
                new JavaTileEntityDecoder.Builder(new UnknownTileDecoder())
                        //
                        // these apply to all pre-DataVersion versions
                        //
                        .begin(JavaVersion.pre15w32a()) //legacy tile entity IDs
                        .putAlias("Airportal", Identifier.fromString("minecraft:end_portal"))
                        .putAlias("Banner", Identifier.fromString("minecraft:banner"))
                        .putAlias("Beacon", Identifier.fromString("minecraft:beacon"))
                        .putAlias("Cauldron", Identifier.fromString("minecraft:brewing_stand"))
                        .putAlias("Chest", Identifier.fromString("minecraft:chest"))
                        .putAlias("Comparator", Identifier.fromString("minecraft:comparator"))
                        .putAlias("Control", Identifier.fromString("minecraft:command_block"))
                        .putAlias("DLDetector", Identifier.fromString("minecraft:daylight_detector"))
                        .putAlias("Dropper", Identifier.fromString("minecraft:dropper"))
                        .putAlias("EnchantTable", Identifier.fromString("minecraft:enchanting_table"))
                        .putAlias("EndGateway", Identifier.fromString("minecraft:end_gateway"))
                        .putAlias("EnderChest", Identifier.fromString("minecraft:ender_chest"))
                        .putAlias("FlowerPot", Identifier.fromString("minecraft:flower_pot"))
                        .putAlias("Furnace", Identifier.fromString("minecraft:furnace"))
                        .putAlias("Hopper", Identifier.fromString("minecraft:hopper"))
                        .putAlias("MobSpawner", Identifier.fromString("minecraft:mob_spawner"))
                        .putAlias("Music", Identifier.fromString("minecraft:noteblock"))
                        .putAlias("Piston", Identifier.fromString("minecraft:piston"))
                        .putAlias("RecordPlayer", Identifier.fromString("minecraft:jukebox"))
                        .putAlias("Sign", Identifier.fromString("minecraft:sign"))
                        .putAlias("Skull", Identifier.fromString("minecraft:skull"))
                        .putAlias("Structure", Identifier.fromString("minecraft:structure_block"))
                        .putAlias("Trap", Identifier.fromString("minecraft:dispenser"))
                        //legacy tile entities
                        .putDecoder(TileEntityChest.ID, new ChestDecoder1_8())
                        .putDecoder(TileEntitySign.ID, new SignDecoder1_8())

                        //
                        // 1.11+
                        //
                        .begin(JavaVersion.fromName("1.11"))
                        .purgeAliases() //1.11+ doesn't have legacy tile entity IDs

                        //
                        // 1.14+
                        //
                        .begin(JavaVersion.latest())
                        .putDecoder(TileEntitySign.ID, new SignDecoder1_14())
                        .build(),
                new MapBuilder<>(new TreeMap<JavaVersion, JavaItemDecoder>())
                        .put(JavaVersion.pre15w32a(), new ItemDecoder1_8())
                        .put(JavaVersion.fromName("1.12.2"), new ItemDecoder1_9())
                        .put(JavaVersion.latest(), new ItemDecoder1_13())
                        .build());
    }

    /**
     * @return the default {@link JavaFixers}
     */
    public static JavaFixers defaultFixers() {
        return Default.DEFAULT_JAVA_FIXERS;
    }

    @NonNull
    protected final NavigableMap<JavaVersion, JavaChunkDecoder> chunk;

    @NonNull
    protected final NavigableMap<JavaVersion, JavaSectionDecoder> section;

    @NonNull
    protected final NavigableMap<JavaVersion, JavaTileEntityDecoder> tileEntity;

    @NonNull
    protected final NavigableMap<JavaVersion, JavaItemDecoder> item;
}
