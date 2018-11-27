/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.minecraft.world.format.anvil;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.daporkchop.lib.math.vector.i.Vec2i;
import net.daporkchop.lib.minecraft.util.NibbleArray;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.Column;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.minecraft.world.format.WorldManager;
import net.daporkchop.lib.minecraft.world.impl.ChunkImpl;
import net.daporkchop.lib.minecraft.world.impl.ColumnImpl;
import net.daporkchop.lib.nbt.NBTInputStream;
import net.daporkchop.lib.nbt.tag.notch.*;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @author DaPorkchop_
 */
@Getter
public class AnvilWorldManager implements WorldManager {
    private static final ThreadLocal<SoftReference<ChunkImpl>> chunkImplCache = ThreadLocal.withInitial(() -> new SoftReference<>(new ChunkImpl(-1, null)));

    private final AnvilSaveFormat format;
    private final File root;

    @NonNull
    @Setter
    private World world;

    private LoadingCache<Vec2i, RegionFile> regionFileCache;

    private LoadingCache<Vec2i, Boolean> regionExists;

    public AnvilWorldManager(@NonNull AnvilSaveFormat format, @NonNull File root) {
        this.format = format;
        this.root = root;

        if (this.root.exists() && !this.root.isDirectory()) {
            throw new IllegalArgumentException(String.format("%s isn't a directory!", this.root.getAbsolutePath()));
        } else {
            this.root.mkdirs();
        }

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
                        if (!file.exists()) {
                            if (file.createNewFile()) {
                                AnvilWorldManager.this.regionExists.put(key, true);
                            } else {
                                throw new IllegalStateException(String.format("Could not create %s", file.getAbsolutePath()));
                            }
                        } else {
                            AnvilWorldManager.this.regionExists.put(key, true);
                        }
                        return new RegionFile(file);
                    }
                });

        this.regionExists = CacheBuilder.newBuilder()
                .concurrencyLevel(1)
                .maximumSize(1024L)
                .expireAfterAccess(5L, TimeUnit.MINUTES)
                .build(new CacheLoader<Vec2i, Boolean>() {
                    @Override
                    public Boolean load(Vec2i key) throws Exception {
                        return new File(AnvilWorldManager.this.root, String.format("r.%d.%d.mca", key.getX(), key.getY())).exists();
                    }
                });
    }

    @Override
    public boolean hasColumn(int x, int z) {
        Vec2i pos = new Vec2i(x >> 5, z >> 5);
        if (!this.regionExists.getUnchecked(pos)) {
            return false;
        }
        RegionFile file = this.regionFileCache.getUnchecked(pos);
        return file.hasChunk(x & 0x1F, z & 0x1F);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loadColumn(Column column) {
        try {
            RegionFile file = this.regionFileCache.get(new Vec2i(column.getX() >> 5, column.getZ() >> 5));
            CompoundTag rootTag;
            //TODO: check if region contains chunk
            try (NBTInputStream is = new NBTInputStream(file.getChunkDataInputStream(column.getX() & 0x1F, column.getZ() & 0x1F))) {
                rootTag = is.readTag().get("Level");
            } catch (NullPointerException e) {
                //this seems to happen for invalid/corrupt chunks
                for (int y = 15; y >= 0; y--) {
                    column.setChunk(y, null);
                }
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //ListTag<CompoundTag> entitiesTag = rootTag.getTypedList("Entities"); //TODO
            {
                ListTag<CompoundTag> sectionsTag = rootTag.get("Sections");
                //TODO: biomes, terrain populated flag etc.
                for (int y = 15; y >= 0; y--) {
                    Chunk chunk = column.getChunk(y);
                    CompoundTag tag = null;
                    for (CompoundTag t : sectionsTag.getValue()) {
                        if (t.<ByteTag>get("Y").getValue() == y) {
                            tag = t;
                            break;
                        }
                    }
                    if (tag == null) {
                        column.setChunk(y, null);
                    } else {
                        if (chunk == null) {
                            column.setChunk(y, chunk = this.format.getSave().getInitFunctions().getChunkCreator().apply(y, column));
                        }
                        if (chunk instanceof ChunkImpl) {
                            this.loadChunkImpl((ChunkImpl) chunk, tag);
                        } else {
                            SoftReference<ChunkImpl> ref = chunkImplCache.get();
                            ChunkImpl impl = ref.get();
                            if (impl == null) {
                                chunkImplCache.set(new SoftReference<>(impl = new ChunkImpl(-1, null)));
                            }
                            this.loadChunkImpl(impl, tag);
                            for (int x = 15; x >= 0; x--) {
                                for (int yy = 15; yy >= 0; yy--) {
                                    for (int z = 15; z >= 0; z--) {
                                        chunk.setBlockId(x, yy, z, impl.getBlockId(x, yy, z));
                                        chunk.setBlockMeta(x, yy, z, impl.getBlockMeta(x, yy, z));
                                        chunk.setBlockLight(x, yy, z, impl.getBlockLight(x, yy, z));
                                        chunk.setSkyLight(x, yy, z, impl.getSkyLight(x, yy, z));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (column instanceof ColumnImpl && rootTag.contains("HeightMap")) {
                int[] heightMapI = rootTag.<IntArrayTag>get("HeightMap").getValue();
                byte[] heightMapB = new byte[16 * 16];
                for (int i = heightMapI.length - 1; i >= 0; i--) {
                    heightMapB[i] = (byte) heightMapI[i];
                }
                ((ColumnImpl) column).setHeightMap(heightMapB);
            }
            {
                ListTag<CompoundTag> sectionsTag = rootTag.get("TileEntities");
                sectionsTag.getValue().stream()
                        .map(tag -> this.world.getSave().getInitFunctions().getTileEntityCreator().apply(this.world, tag))
                        .forEach(column.getTileEntities()::add);
                column.getTileEntities().forEach(tileEntity -> this.world.getLoadedTileEntities().put(tileEntity.getPos(), tileEntity));
            }
            this.world.getLoadedColumns().put(column.getPos(), column);
        } catch (Exception e) {
            e.printStackTrace();
            column.unload();
        }
    }

    private void loadChunkImpl(@NonNull ChunkImpl impl, @NonNull CompoundTag tag) {
        impl.setBlockIds(tag.<ByteArrayTag>get("Blocks").getValue());
        impl.setMeta(new NibbleArray(tag.<ByteArrayTag>get("Data").getValue()));
        impl.setBlockLight(new NibbleArray(tag.<ByteArrayTag>get("BlockLight").getValue()));
        impl.setSkyLight(new NibbleArray(tag.<ByteArrayTag>get("SkyLight").getValue()));
        if (tag.contains("Add")) {
            impl.setAdd(new NibbleArray(tag.<ByteArrayTag>get("Add").getValue()));
        } else {
            impl.setAdd(null);
        }
    }

    @Override
    public void saveColumn(Column column) {
        /*CompoundTag tag1 = new CompoundTag();
        CompoundTag levelTag = new CompoundTag();
        tag1.putCompound("Level", levelTag);
        ListTag<CompoundTag> sectionsTag = new ListTag<>("Sections");
        levelTag.putList(sectionsTag);
        for (int y = 15; y >= 0; y--)   {
            Chunk chunk = column.getChunk(y);
            if (chunk != null)  {
                ChunkImpl impl;
                if (chunk instanceof ChunkImpl) {
                    impl = (ChunkImpl) chunk;
                } else {
                    SoftReference<ChunkImpl> ref = chunkImplCache.get();
                    impl = ref.get();
                    if (impl == null)   {
                        chunkImplCache.set(new SoftReference<>(impl = new ChunkImpl(-1, null)));
                        impl.setAdd(new NibbleArray());
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
                t.putByteArray("Blocks", impl.getBlockIds());
                t.putByteArray("Data", impl.getMeta().getData());
                t.putByteArray("BlockLight", impl.getBlockLight().getData());
                t.putByteArray("SkyLight", impl.getBlockLight().getData());
                if (impl.getAdd() != null)  {
                    t.putByteArray("Add", impl.getAdd().getData());
                }
                sectionsTag.getValue().add(t);
            }
        }
        RegionFile file = RegionFileCache.getRegionFile(this.root, column.getX(), column.getZ());
        try (OutputStream os = file.getChunkDataOutputStream(column.getX() & 0x1F, column.getZ() & 0x1F))   {
            NBTIO.write(tag1, os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        throw new UnsupportedOperationException();
    }

    public Collection<Vec2i> getRegions() {
        Collection<Vec2i> positions = new ArrayDeque<>();
        for (File f : this.root.listFiles()) {
            String name = f.getName();
            if (f.isFile() && name.endsWith(".mca")) {
                String[] split = name.split("\\.");
                int x = Integer.parseInt(split[1]);
                int z = Integer.parseInt(split[2]);
                positions.add(new Vec2i(x, z));
            }
        }
        return positions;
    }
}
