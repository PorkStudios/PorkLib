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
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.pool.handle.HandledPool;
import net.daporkchop.lib.compression.context.PInflater;
import net.daporkchop.lib.compression.zlib.Zlib;
import net.daporkchop.lib.compression.zlib.ZlibMode;
import net.daporkchop.lib.compression.zlib.options.ZlibInflaterOptions;
import net.daporkchop.lib.concurrent.PFuture;
import net.daporkchop.lib.concurrent.PFutures;
import net.daporkchop.lib.minecraft.format.anvil.region.RawChunk;
import net.daporkchop.lib.minecraft.format.anvil.region.RegionConstants;
import net.daporkchop.lib.minecraft.format.anvil.region.RegionFile;
import net.daporkchop.lib.minecraft.format.anvil.region.RegionFileCache;
import net.daporkchop.lib.minecraft.format.java.JavaFixers;
import net.daporkchop.lib.minecraft.save.SaveOptions;
import net.daporkchop.lib.minecraft.version.DataVersion;
import net.daporkchop.lib.minecraft.version.java.JavaVersion;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.Section;
import net.daporkchop.lib.minecraft.world.WorldStorage;
import net.daporkchop.lib.nbt.NBTFormat;
import net.daporkchop.lib.nbt.NBTOptions;
import net.daporkchop.lib.nbt.tag.CompoundTag;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.io.File;
import java.io.IOException;
import java.util.Spliterator;
import java.util.concurrent.Executor;

/**
 * Implementation of {@link WorldStorage} for the Anvil save format.
 *
 * @author DaPorkchop_
 */
//TODO: this needs to be optimized
@Accessors(fluent = true)
public class AnvilWorldStorage extends AbstractRefCounted implements WorldStorage {
    protected static final HandledPool<PInflater> ZLIB_INFLATER_CACHE;
    protected static final HandledPool<PInflater> GZIP_INFLATER_CACHE;

    static {
        if (Zlib.PROVIDER.isNative()) {
            ZlibInflaterOptions inflaterOptions = Zlib.PROVIDER.inflateOptions().withMode(ZlibMode.AUTO);
            GZIP_INFLATER_CACHE = ZLIB_INFLATER_CACHE = HandledPool.threadLocal(() -> Zlib.PROVIDER.inflater(inflaterOptions), 1);
        } else {
            ZlibInflaterOptions zlibInflaterOptions = Zlib.PROVIDER.inflateOptions().withMode(ZlibMode.ZLIB);
            ZLIB_INFLATER_CACHE = HandledPool.threadLocal(() -> Zlib.PROVIDER.inflater(zlibInflaterOptions), 1);

            ZlibInflaterOptions gzipInflaterOptions = Zlib.PROVIDER.inflateOptions().withMode(ZlibMode.GZIP);
            GZIP_INFLATER_CACHE = HandledPool.threadLocal(() -> Zlib.PROVIDER.inflater(gzipInflaterOptions), 1);
        }
    }

    protected static Handle<PInflater> inflater(int version) {
        switch (version) {
            case RegionConstants.ID_ZLIB: //by far the more common one
                return ZLIB_INFLATER_CACHE.get();
            case RegionConstants.ID_GZIP:
                return GZIP_INFLATER_CACHE.get();
        }
        throw new IllegalArgumentException("Unknown compression version: " + version);
    }

    protected final File root;
    protected final RegionFile regionCache;

    protected final SaveOptions options;
    protected final JavaFixers fixers;
    protected final NBTOptions nbtOptions;
    protected final Executor ioExecutor;

    @Getter
    protected final JavaVersion worldVersion;

    public AnvilWorldStorage(@NonNull File root, @NonNull AnvilWorld world, @NonNull NBTOptions nbtOptions, JavaVersion worldVersion) {
        this.root = PFiles.ensureDirectoryExists(root);
        this.regionCache = new RegionFileCache(world.options(), new File(root, "region"));

        this.options = world.options();
        this.fixers = this.options.get(AnvilSaveOptions.FIXERS);
        this.ioExecutor = this.options.get(SaveOptions.IO_EXECUTOR);
        this.nbtOptions = nbtOptions;
        this.worldVersion = worldVersion;
    }

    @Override
    public Chunk loadChunk(int x, int z) throws IOException {
        CompoundTag tag = null;
        try {
            ByteBuf uncompressed = null;
            try {
                try (RawChunk chunk = this.regionCache.read(x, z)) {
                    if (chunk == null) { //chunk doesn't exist on disk
                        return null;
                    }
                    uncompressed = this.options.get(SaveOptions.NETTY_ALLOC).ioBuffer(1 << 18); //256 KiB
                    try (Handle<PInflater> handle = inflater(chunk.data().readByte() & 0xFF)) {
                        handle.get().decompress(chunk.data(), uncompressed);
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
            return this.fixers.chunk().decodeAt(tag, version, this.worldVersion); //upgrade chunk to the same data version as the world itself
        } finally {
            if (tag != null) {
                tag.release();
            }
        }
    }

    @Override
    public Section loadSection(@NonNull Chunk parent, int y) throws IOException {
        return null;
    }

    @Override
    public PFuture<Chunk> loadChunkAsync(int x, int z) {
        return PFutures.computeThrowableAsync(() -> this.loadChunk(x, z), this.ioExecutor);
    }

    @Override
    public PFuture<Section> loadSectionAsync(@NonNull Chunk parent, int y) {
        return null;
    }

    @Override
    public void save(@NonNull Iterable<Chunk> chunks, @NonNull Iterable<Section> sections) throws IOException {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public PFuture<Void> saveAsync(@NonNull Iterable<Chunk> chunks, @NonNull Iterable<Section> sections) {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public PFuture<Void> flushAsync() {
        return PFutures.successful(null, this.ioExecutor);
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
