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
import net.daporkchop.lib.compat.update.DataUpdater;
import net.daporkchop.lib.compat.update.DataUpdaterBuilder;
import net.daporkchop.lib.minecraft.format.anvil.version.chunk.decoder.FlattenedChunkDecoder;
import net.daporkchop.lib.minecraft.format.anvil.version.chunk.decoder.LegacyChunkDecoder;
import net.daporkchop.lib.minecraft.format.java.version.JavaDecoder;
import net.daporkchop.lib.minecraft.format.java.version.section.decoder.FlattenedSectionDecoder;
import net.daporkchop.lib.minecraft.format.java.version.section.decoder.LegacySectionDecoder;
import net.daporkchop.lib.minecraft.format.java.version.tile.decoder.SignDecoder1_13_2;
import net.daporkchop.lib.minecraft.format.java.version.tile.decoder.SignDecoderLatest;
import net.daporkchop.lib.minecraft.format.java.version.tile.decoder.UnknownTileDecoder;
import net.daporkchop.lib.minecraft.save.SaveOptions;
import net.daporkchop.lib.minecraft.tile.TileEntity;
import net.daporkchop.lib.minecraft.tile.TileEntitySign;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.version.java.JavaVersion;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.Section;
import net.daporkchop.lib.nbt.tag.CompoundTag;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import static net.daporkchop.lib.common.util.PValidation.checkState;

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
                new DataUpdaterBuilder<CompoundTag, JavaVersion, SaveOptions>()
                        .build(),
                new MapBuilder<>(new TreeMap<JavaVersion, JavaDecoder<Chunk>>())
                        .put(LegacyChunkDecoder.VERSION, new LegacyChunkDecoder())
                        .put(FlattenedChunkDecoder.VERSION, new FlattenedChunkDecoder())
                        .build(),
                new DataUpdaterBuilder<CompoundTag, JavaVersion, SaveOptions>()
                        .build(),
                new MapBuilder<>(new TreeMap<JavaVersion, JavaDecoder<Section>>())
                        .put(LegacySectionDecoder.VERSION, new LegacySectionDecoder())
                        .put(FlattenedSectionDecoder.VERSION, new FlattenedSectionDecoder())
                        .build(),
                new DataUpdaterBuilder<CompoundTag, JavaVersion, SaveOptions>()
                        .build(),
                new MapBuilder<>(new HashMap<Identifier, NavigableMap<JavaVersion, JavaDecoder<TileEntity>>>())
                        .put(TileEntitySign.ID, new MapBuilder<>(new TreeMap<JavaVersion, JavaDecoder<TileEntity>>())
                                .put(SignDecoder1_13_2.VERSION, new SignDecoder1_13_2())
                                .put(SignDecoderLatest.VERSION, new SignDecoderLatest())
                                .build())
                        .build(),
                new UnknownTileDecoder());
    }

    /**
     * @return the default {@link JavaFixers}
     */
    public static JavaFixers defaultFixers() {
        return Default.DEFAULT_JAVA_FIXERS;
    }

    @NonNull
    protected final DataUpdater<CompoundTag, JavaVersion, SaveOptions> chunkUpdater;

    @NonNull
    protected final NavigableMap<JavaVersion, JavaDecoder<Chunk>> chunkDecoder;

    @NonNull
    protected final DataUpdater<CompoundTag, JavaVersion, SaveOptions> sectionUpdater;

    @NonNull
    protected final NavigableMap<JavaVersion, JavaDecoder<Section>> sectionDecoder;

    @NonNull
    protected final DataUpdater<CompoundTag, JavaVersion, SaveOptions> tileEntityUpdater;

    @NonNull
    protected final Map<Identifier, NavigableMap<JavaVersion, JavaDecoder<TileEntity>>> tileEntityDecoder;

    @NonNull
    protected final JavaDecoder<TileEntity> tileEntityDecoderUnknown;

    /**
     * Decodes a chunk with minimal conversion.
     */
    public Chunk decodeChunkLazy(@NonNull CompoundTag tag, @NonNull JavaVersion version, @NonNull SaveOptions options) {
        JavaVersion targetVersion = this.chunkDecoder.ceilingKey(version);
        tag = this.chunkUpdater.update(version, targetVersion).apply(tag, options);
        return this.chunkDecoder.get(targetVersion).decode(tag, targetVersion, options);
    }

    /**
     * Decodes a chunk to exactly the given version.
     */
    public Chunk decodeChunk(@NonNull CompoundTag tag, @NonNull JavaVersion version, @NonNull JavaVersion targetVersion, @NonNull SaveOptions options) {
        checkState(this.chunkDecoder.containsKey(targetVersion), "no decoder for version: %s", targetVersion);
        tag = this.chunkUpdater.update(version, targetVersion).apply(tag, options);
        return this.chunkDecoder.get(targetVersion).decode(tag, targetVersion, options);
    }
}
