/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2021 DaPorkchop_
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

package net.daporkchop.lib.minecraft.world.format.anvil;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.common.ref.Ref;
import net.daporkchop.lib.common.ref.ThreadRef;
import net.daporkchop.lib.compression.PInflater;
import net.daporkchop.lib.compression.zlib.Zlib;
import net.daporkchop.lib.math.vector.i.Vec2i;
import net.daporkchop.lib.minecraft.entity.Entity;
import net.daporkchop.lib.minecraft.registry.ResourceLocation;
import net.daporkchop.lib.minecraft.tileentity.TileEntity;
import net.daporkchop.lib.minecraft.util.SectionLayer;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.Section;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.minecraft.world.format.WorldManager;
import net.daporkchop.lib.minecraft.world.format.anvil.region.RegionConstants;
import net.daporkchop.lib.minecraft.world.format.anvil.region.RegionFile;
import net.daporkchop.lib.minecraft.world.impl.section.DirectSectionImpl;
import net.daporkchop.lib.minecraft.world.impl.section.HeapSectionImpl;
import net.daporkchop.lib.minecraft.world.impl.vanilla.VanillaChunkImpl;
import net.daporkchop.lib.nbt.NBTInputStream;
import net.daporkchop.lib.nbt.alloc.NBTArrayAllocator;
import net.daporkchop.lib.nbt.tag.notch.ByteArrayTag;
import net.daporkchop.lib.nbt.tag.notch.CompoundTag;
import net.daporkchop.lib.nbt.tag.notch.IntArrayTag;
import net.daporkchop.lib.nbt.tag.notch.ListTag;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author DaPorkchop_
 */
@Getter
public class AnvilWorldManager implements WorldManager {
    protected static final Ref<HeapSectionImpl> CHUNK_CACHE    = ThreadRef.soft(() -> new HeapSectionImpl(-1, null));
    protected static final Pattern              REGION_PATTERN = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
    protected static final Ref<PInflater>       INFLATER_CACHE = ThreadRef.soft(() -> Zlib.PROVIDER.inflater(Zlib.MODE_AUTO));

    protected final AnvilSaveFormat format;
    protected final File            root;

    @NonNull
    @Setter
    protected World world;

    protected LoadingCache<Vec2i, RegionFile> regionFileCache;
    protected LoadingCache<Vec2i, Boolean>    regionExists;

    //TODO: make this configurable
    protected final NBTArrayAllocator arrayAllocator = new AnvilPooledNBTArrayAllocator(34 * 34 * 8 * 3, 34 * 34 * 8); //these sizes are suitable for WorldScanner with neighboring chunks


    public AnvilWorldManager(@NonNull AnvilSaveFormat format, @NonNull File root) {
        this.format = format;
        this.root = PFiles.ensureDirectoryExists(root);

        this.regionFileCache = CacheBuilder.newBuilder()
                .concurrencyLevel(1)
                .maximumSize(256L)
                .expireAfterAccess(5L, TimeUnit.MINUTES)
                .removalListener((RemovalListener<Vec2i, RegionFile>) v -> {
                    try {
                        v.getValue().close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .build(new CacheLoader<Vec2i, RegionFile>() {
                    @Override
                    public RegionFile load(Vec2i key) throws Exception {
                        File file = new File(AnvilWorldManager.this.root, String.format("r.%d.%d.mca", key.getX(), key.getY()));
                        return RegionFile.open(file, AnvilWorldManager.this.format.getSave().config().openOptions());
                    }
                });

        this.regionExists = CacheBuilder.newBuilder()
                .concurrencyLevel(1)
                .maximumSize(1024L)
                .expireAfterAccess(5L, TimeUnit.MINUTES)
                .build(new CacheLoader<Vec2i, Boolean>() {
                    @Override
                    public Boolean load(Vec2i key) throws Exception {
                        return AnvilWorldManager.this.regionFileCache.getIfPresent(key) != null
                                || new File(AnvilWorldManager.this.root, String.format("r.%d.%d.mca", key.getX(), key.getY())).exists();
                    }
                });
    }

    @Override
    public boolean hasColumn(int x, int z) {
        Vec2i pos = new Vec2i(x >> 5, z >> 5);
        if (!this.regionExists.getUnchecked(pos)) {
            return false;
        }
        return this.regionFileCache.getUnchecked(pos).hasChunk(x & 0x1F, z & 0x1F);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loadColumn(Chunk chunk) {
        try {
            CompoundTag rootTag;
            //TODO: check if region contains chunk
            {
                ByteBuf compressed = this.regionFileCache.get(new Vec2i(chunk.getX() >> 5, chunk.getZ() >> 5)).readDirect(chunk.getX() & 0x1F, chunk.getZ() & 0x1F);
                try {
                    byte compressionId = compressed.readByte();
                    switch (compressionId) {
                        case RegionConstants.ID_GZIP: //i can use the same instance for both compression types since it's using ZLIB_MODE_AUTO
                        case RegionConstants.ID_ZLIB: {
                            PInflater inflater = INFLATER_CACHE.get();
                            ByteBuf uncompressed = PooledByteBufAllocator.DEFAULT.directBuffer();
                            try {
                                inflater.fullInflateGrowing(compressed, uncompressed);
                                try (NBTInputStream in = new NBTInputStream(new ByteBufInputStream(uncompressed), this.arrayAllocator)) { //TODO: use DataIn again
                                    rootTag = in.readTag().getCompound("Level");
                                }
                            } finally {
                                uncompressed.release();
                                inflater.reset();
                            }
                        }
                        break;
                        default:
                            throw new IllegalStateException(String.format("Invalid compression type: %d", compressionId & 0xFF));
                    }
                } catch (NullPointerException e) {
                    //this seems to happen for invalid/corrupt chunks
                    for (int y = 15; y >= 0; y--) {
                        chunk.section(y, null);
                    }
                    return;
                } finally {
                    compressed.release();
                }
            }
            rootTag.<ListTag<CompoundTag>>get("Entities").getValue().stream()
                    .map(tag -> {
                        Entity entity = this.world.getSave().config().entityFactory().create(new ResourceLocation(tag.getString("id")));
                        entity.init(this.world, tag);
                        return entity;
                    })
                    .forEach(chunk.entities()::add);

            chunk.setBiomeArray(rootTag.getByteArray("Biomes"));

            {
                ListTag<CompoundTag> sectionsTag = rootTag.getList("Sections");
                //TODO: terrain populated flag etc.
                for (int y = 15; y >= 0; y--) {
                    Section section = chunk.section(y);
                    CompoundTag tag = null;
                    for (CompoundTag t : sectionsTag) {
                        if (t.getByte("Y") == y) {
                            tag = t;
                            break;
                        }
                    }
                    if (tag == null) {
                        chunk.section(y, null);
                    } else {
                        if (section == null) {
                            chunk.section(y, section = this.format.getSave().config().sectionFactory().create(y, chunk));
                        }
                        this.loadSection(section, tag);
                        if (section instanceof HeapSectionImpl) {
                            this.loadSection((HeapSectionImpl) section, tag);
                        }
                    }
                }
            }
            if (chunk instanceof VanillaChunkImpl && rootTag.contains("HeightMap")) {
                ((VanillaChunkImpl) chunk).heightMap(rootTag.<IntArrayTag>get("HeightMap").handle());
            }
            {
                ListTag<CompoundTag> sectionsTag = rootTag.get("TileEntities");
                sectionsTag.getValue().stream()
                        .map(tag -> {
                            TileEntity tileEntity = this.world.getSave().config().tileEntityFactory().create(new ResourceLocation(tag.getString("id")));
                            tileEntity.init(this.world, tag);
                            return tileEntity;
                        })
                        .forEach(chunk.tileEntities()::add);
            }
        } catch (Exception e) {
            new RuntimeException(String.format("Unable to parse chunk (%d,%d) in region (%d,%d)", chunk.getX(), chunk.getZ(), chunk.getX() >> 5, chunk.getZ() >> 5), e).printStackTrace();
            chunk.unload();
        }
    }

    private void loadSection(@NonNull Section section, @NonNull CompoundTag tag) {
        if (section instanceof HeapSectionImpl) {
            this.loadSection((HeapSectionImpl) section, tag);
        } else if (section instanceof DirectSectionImpl) {
            this.loadSection((DirectSectionImpl) section, tag);
        } else {
            HeapSectionImpl impl = CHUNK_CACHE.get();
            this.loadSection(impl, tag);
            for (int x = 15; x >= 0; x--) {
                for (int y = 15; y >= 0; y--) {
                    for (int z = 15; z >= 0; z--) {
                        section.setBlockId(x, y, z, impl.getBlockId(x, y, z));
                        section.setBlockMeta(x, y, z, impl.getBlockMeta(x, y, z));
                        section.setBlockLight(x, y, z, impl.getBlockLight(x, y, z));
                        section.setSkyLight(x, y, z, impl.getSkyLight(x, y, z));
                    }
                }
            }
        }
    }

    private void loadSection(@NonNull HeapSectionImpl impl, @NonNull CompoundTag tag) {
        impl.setBlocks(tag.get("Blocks"));
        impl.setMeta(tag.get("Data"));
        impl.setBlockLight(tag.get("BlockLight"));
        impl.setSkyLight(tag.get("SkyLight"));
        impl.setAdd(tag.get("Add"));
    }

    private void loadSection(@NonNull DirectSectionImpl impl, @NonNull CompoundTag tag) {
        final Object ref = impl.memoryRef();
        final long addr = impl.memoryOff();

        PUnsafe.copyMemory(
                tag.getByteArray("Data"),
                PUnsafe.ARRAY_BYTE_BASE_OFFSET,
                ref,
                addr + DirectSectionImpl.OFFSET_META,
                DirectSectionImpl.SIZE_NIBBLE_LAYER
        );
        PUnsafe.copyMemory(
                tag.getByteArray("BlockLight"),
                PUnsafe.ARRAY_BYTE_BASE_OFFSET,
                ref,
                addr + DirectSectionImpl.OFFSET_BLOCK_LIGHT,
                DirectSectionImpl.SIZE_NIBBLE_LAYER
        );
        PUnsafe.copyMemory(
                tag.getByteArray("SkyLight"),
                PUnsafe.ARRAY_BYTE_BASE_OFFSET,
                ref,
                addr + DirectSectionImpl.OFFSET_SKY_LIGHT,
                DirectSectionImpl.SIZE_NIBBLE_LAYER
        );

        byte[] blocks = tag.getByteArray("Blocks");
        if (tag.contains("Add")) {
            byte[] add = tag.getByteArray("Add");
            //this is very slow, but luckily it only has to run once
            for (int x = 15; x >= 0; x--) {
                for (int y = 15; y >= 0; y--) {
                    for (int z = 15; z >= 0; z--) {
                        impl.setBlockId(x, y, z, (blocks[(y << 8) | (z << 4) | x] & 0xFF) | (SectionLayer.getNibble(add, x, y, z) << 8));
                    }
                }
            }
        } else {
            for (int i = 0; i < 4096; i += 4) {
                //read 4 ids at a time and write them in one go to make sure things stay nice and fast
                PUnsafe.putLong(
                        addr + DirectSectionImpl.OFFSET_BLOCK + (i << 1),
                        (blocks[i] & 0xFF) | ((blocks[i + 1] & 0xFF) << 16) | ((blocks[i + 2] & 0xFFL) << 32) | ((blocks[i + 3] & 0xFFL) << 48)
                );
            }
        }
    }

    @Override
    public void saveColumn(Chunk chunk) {
        /*CompoundTag tag1 = new CompoundTag();
        CompoundTag levelTag = new CompoundTag();
        tag1.putCompound("Level", levelTag);
        ListTag<CompoundTag> sectionsTag = new ListTag<>("Sections");
        levelTag.putList(sectionsTag);
        for (int y = 15; y >= 0; y--)   {
            Chunk chunk = chunk.section(y);
            if (chunk != null)  {
                VanillaChunkImpl impl;
                if (chunk instanceof VanillaChunkImpl) {
                    impl = (VanillaChunkImpl) chunk;
                } else {
                    SoftReference<VanillaChunkImpl> ref = CHUNK_CACHE.get();
                    impl = ref.get();
                    if (impl == null)   {
                        CHUNK_CACHE.set(new SoftReference<>(impl = new VanillaChunkImpl(-1, null)));
                        impl.setAdd(new SectionLayer());
                    }
                    for (int x = 15; x >= 0; x--)   {
                        for (int yy = 15; yy >= 0; yy--)   {
                            for (int z = 15; z >= 0; z--)   {
                                impl.setBlockId(x, yy, z, chunk.getBlockId(x, yy, z));
                                impl.setBlockMeta(x, yy, z, chunk.getBlockMeta(x, yy, z));
                                impl.setBlockLight(x, yy, z, chunk.getBlockLight(x, yy, z));
                                impl.setSkyLight(x, yy, z, chunk.getSkyLight(x, yy, z));
                            }
                        }
                    }
                }
                CompoundTag t = new CompoundTag();
                t.putInt("Y", y);
                t.putByteArray("Blocks", impl.getBlocks());
                t.putByteArray("Data", impl.getMeta().getData());
                t.putByteArray("BlockLight", impl.getBlockLight().getData());
                t.putByteArray("SkyLight", impl.getBlockLight().getData());
                if (impl.getAdd() != null)  {
                    t.putByteArray("Add", impl.getAdd().getData());
                }
                sectionsTag.getValue().add(t);
            }
        }
        RegionFile file = RegionFileCache.getRegionFile(this.root, chunk.getX(), chunk.getZ());
        try (OutputStream os = file.getChunkDataOutputStream(chunk.getX() & 0x1F, chunk.getZ() & 0x1F))   {
            NBTIO.write(tag1, os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        throw new UnsupportedOperationException();
    }

    public Collection<Vec2i> getRegions() {
        Matcher matcher = REGION_PATTERN.matcher("");
        return Arrays.stream(this.root.listFiles())
                .map(file -> {
                    matcher.reset(file.getName());
                    return matcher.find() ? new Vec2i(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2))) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void close() throws IOException {
        this.regionExists.invalidateAll();
        this.regionExists = null;
        this.regionFileCache.invalidateAll();
        this.regionFileCache = null;
    }
}
