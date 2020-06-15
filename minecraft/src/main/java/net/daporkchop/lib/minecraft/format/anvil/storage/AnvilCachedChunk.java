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

package net.daporkchop.lib.minecraft.format.anvil.storage;

import lombok.NonNull;
import net.daporkchop.lib.minecraft.format.java.JavaFixers;
import net.daporkchop.lib.minecraft.save.SaveOptions;
import net.daporkchop.lib.minecraft.tile.TileEntity;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.util.dirty.AbstractReleasableDirtiable;
import net.daporkchop.lib.minecraft.version.java.JavaVersion;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.Section;
import net.daporkchop.lib.nbt.tag.CompoundTag;
import net.daporkchop.lib.nbt.tag.ListTag;

import static net.daporkchop.lib.common.util.PValidation.checkState;

/**
 * In-memory representation of a chunk cached by {@link AnvilWorldStorage}.
 * <p>
 * This class is thread-safe.
 *
 * @author DaPorkchop_
 */
public abstract class AnvilCachedChunk extends AbstractReleasableDirtiable {
    public abstract Chunk chunk();

    public abstract Section section(int y);

    public static class ReadOnly extends AnvilCachedChunk {
        protected final Chunk chunk;
        protected final Section[] sections = new Section[16];

        public ReadOnly(@NonNull CompoundTag tag, @NonNull JavaVersion version, @NonNull JavaFixers fixers, @NonNull SaveOptions options) {
            CompoundTag levelTag = tag.getCompound("Level");

            try (ListTag<CompoundTag> sectionTags = levelTag.remove("Sections")) { //remove tag to ensure that it's not accessed by the chunk decoder
                for (CompoundTag sectionTag : sectionTags) {
                    Section section = fixers.section().decode(sectionTag, version, options);
                    checkState(this.sections[section.y()] == null, "duplicate section at y=%d!", section.y());
                    this.sections[section.y()] = section;
                }
            }

            try (ListTag<CompoundTag> tileEntityTags = levelTag.remove("TileEntities")) { //remove tag to ensure that it's not accessed by the chunk decoder
                for (CompoundTag tileEntityTag : tileEntityTags) {
                    TileEntity tileEntity = fixers.tileEntity(Identifier.fromString(tileEntityTag.getString("id")))
                            .decode(tileEntityTag, version, options);
                    //TODO: add tile entity to chunk
                }
            }

            //TODO: entities

            this.chunk = fixers.chunk().decode(tag, version, options);
        }

        @Override
        public Chunk chunk() {
            return this.chunk.retain();
        }

        @Override
        public Section section(int y) {
            return this.sections[y].retain();
        }

        @Override
        protected void doRelease() {
            this.chunk.release();
            for (Section section : this.sections)   {
                section.release();
            }
        }
    }

    //TODO
    /*public static class ReadWrite extends AnvilCachedChunk {
        protected final JavaVersion version;

        protected CompoundTag chunkTag;
        protected final CompoundTag[] sectionTags = new CompoundTag[16];
        protected final CompoundTag[][] sectionBlockEntityTags = new CompoundTag[16][];

        public ReadWrite(@NonNull JavaVersion version) {
            super(version);
        }

        @Override
        protected void doRelease() {
            this.chunkTag.release();
            for (CompoundTag sectionTag : this.sectionTags) {
                if (sectionTag != null) {
                    sectionTag.release();
                }
            }
            for (CompoundTag[] blockEntityTags : this.sectionBlockEntityTags) {
                if (blockEntityTags != null) {
                    for (CompoundTag blockEntityTag : blockEntityTags) {
                        blockEntityTag.release();
                    }
                }
            }
        }
    }*/
}
