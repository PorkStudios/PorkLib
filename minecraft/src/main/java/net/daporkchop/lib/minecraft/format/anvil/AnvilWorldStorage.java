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

package net.daporkchop.lib.minecraft.format.anvil;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.pool.handle.HandledPool;
import net.daporkchop.lib.compat.datafix.DataFixer;
import net.daporkchop.lib.compression.context.PInflater;
import net.daporkchop.lib.compression.zlib.Zlib;
import net.daporkchop.lib.compression.zlib.ZlibMode;
import net.daporkchop.lib.compression.zlib.options.ZlibInflaterOptions;
import net.daporkchop.lib.concurrent.PFuture;
import net.daporkchop.lib.minecraft.format.anvil.region.RawChunk;
import net.daporkchop.lib.minecraft.format.anvil.region.RegionFile;
import net.daporkchop.lib.minecraft.format.anvil.region.RegionFileCache;
import net.daporkchop.lib.minecraft.format.anvil.version.codec.chunk.LegacyChunkCodec;
import net.daporkchop.lib.minecraft.version.DataVersion;
import net.daporkchop.lib.minecraft.version.java.JavaVersion;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.Section;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.minecraft.world.WorldStorage;
import net.daporkchop.lib.nbt.NBTFormat;
import net.daporkchop.lib.nbt.NBTOptions;
import net.daporkchop.lib.nbt.tag.CompoundTag;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.io.File;
import java.io.IOException;
import java.util.Spliterator;

/**
 * @author DaPorkchop_
 */
public class AnvilWorldStorage extends AbstractRefCounted implements WorldStorage {
    protected static final ZlibInflaterOptions INFLATER_OPTIONS = Zlib.PROVIDER.inflateOptions().withMode(ZlibMode.AUTO);
    protected static final HandledPool<PInflater> INFLATER_CACHE = HandledPool.threadLocal(() -> Zlib.PROVIDER.inflater(INFLATER_OPTIONS), 1);

    protected final File root;
    protected final AnvilSaveOptions options;
    protected final NBTOptions nbtOptions;

    protected final RegionFile regionCache;

    protected final DataFixer<Chunk, CompoundTag, JavaVersion> chunkFixer;
    //protected final DataFixer<Section, CompoundTag, JavaVersion> sectionFixer;
    protected final JavaVersion worldVersion;

    public AnvilWorldStorage(@NonNull File root, @NonNull AnvilWorld world, @NonNull NBTOptions nbtOptions, JavaVersion worldVersion) {
        this.root = PFiles.ensureDirectoryExists(root);
        this.options = world.options();
        this.nbtOptions = nbtOptions;
        this.worldVersion = worldVersion;

        this.regionCache = new RegionFileCache(world.options(), new File(root, "region"));

        this.chunkFixer = DataFixer.<Chunk, CompoundTag, JavaVersion>builder()
                .addCodec(JavaVersion.fromName("1.12.2"), new LegacyChunkCodec(world))
                .build();
    }

    @Override
    public Chunk loadChunk(@NonNull World parent, int x, int z) throws IOException {
        CompoundTag tag = null;
        try {
            ByteBuf uncompressed = null;
            try {
                try (RawChunk chunk = this.regionCache.read(x, z)) {
                    if (chunk == null) { //chunk doesn't exist on disk
                        return null;
                    }
                    uncompressed = this.options.nettyAlloc().ioBuffer(1 << 18); //256 KiB
                    try (Handle<PInflater> handle = INFLATER_CACHE.get()) {
                        handle.get().decompress(chunk.data().skipBytes(1), uncompressed);
                    }
                } //release compressed chunk data before parsing NBT
                tag = NBTFormat.BIG_ENDIAN.readCompound(DataIn.wrap(uncompressed, false), this.nbtOptions);
            } finally { //release uncompressed chunk data before constructing chunk instance
                if (uncompressed != null) {
                    uncompressed.release();
                }
            }
            int dataVersion = tag.getInt("DataVersion", 0);
            JavaVersion version = dataVersion < DataVersion.DATA_15w32a ? JavaVersion.pre15w32a() : JavaVersion.fromDataVersion(dataVersion);
            return this.chunkFixer.decode(tag, version, this.worldVersion); //upgrade chunk to the same data version as the world itself
        } finally {
            if (tag != null) {
                tag.release();
            }
        }
    }

    /**
     * @deprecated you shouldn't be reading/writing sections on a vanilla anvil world...
     */
    @Override
    @Deprecated
    public Section loadSection(@NonNull Chunk parent, int x, int y, int z) throws IOException {
        Section section = parent.getSection(y);
        return section != null ? section.retain() : null;
    }

    @Override
    public void saveChunk(@NonNull Chunk chunk) throws IOException {
    }

    /**
     * @deprecated you shouldn't be reading/writing sections on a vanilla anvil world...
     */
    @Override
    @Deprecated
    public void saveSection(@NonNull Section section) throws IOException {
        this.saveChunk(section.parent());
    }

    @Override
    public PFuture<Void> saveChunkAsync(@NonNull Chunk chunk) {
        return null;
    }

    @Override
    public PFuture<Void> saveSectionAsync(@NonNull Section section) {
        return this.saveChunkAsync(section.parent());
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public PFuture<Void> flushAsync() {
        return null;
    }

    @Override
    public Spliterator<Chunk> allChunks() throws IOException {
        return null;
    }

    @Override
    public Spliterator<Section> allSections() throws IOException {
        return null;
    }

    @Override
    public WorldStorage retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }

    @Override
    protected void doRelease() {
        try {
            this.flush();
            this.regionCache.close();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
