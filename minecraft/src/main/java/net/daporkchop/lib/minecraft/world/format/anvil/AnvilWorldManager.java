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

import com.flowpowered.nbt.ByteArrayTag;
import com.flowpowered.nbt.ByteTag;
import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.stream.NBTInputStream;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

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

    public AnvilWorldManager(@NonNull AnvilSaveFormat format, @NonNull File root) {
        this.format = format;
        this.root = root;

        if (this.root.exists() && !this.root.isDirectory()) {
            throw new IllegalArgumentException(String.format("%s isn't a directory!", this.root.getAbsolutePath()));
        } else {
            this.root.mkdirs();
        }
    }

    @Override
    public boolean hasColumn(int x, int z) {
        RegionFile file = RegionFileCache.getRegionFile(this.root, x, z, false);
        if (file == null)   {
            return false;
        } else {
            return file.hasChunk(x & 0x1F, z & 0x1F);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loadColumn(Column column) {
        RegionFile file = RegionFileCache.getRegionFile(this.root, column.getX(), column.getZ());
        CompoundTag rootTag;
        //TODO: check if region contains chunk
        try (NBTInputStream is = new NBTInputStream(file.getChunkDataInputStream(column.getX() & 0x1F, column.getZ() & 0x1F), false)) {
            rootTag = (CompoundTag) ((CompoundTag) is.readTag()).getValue().get("Level");
        } catch (NullPointerException e)    {
            //this seems to happen for invalid/corrupt chunks
            for (int y = 15; y >= 0; y--)   {
                column.setChunk(y, null);
            }
            return;
        } catch (IOException e)  {
            throw new RuntimeException(e);
        }
        //ListTag<CompoundTag> entitiesTag = rootTag.getTypedList("Entities"); //TODO
        {
            ListTag<CompoundTag> sectionsTag = (ListTag<CompoundTag>) rootTag.getValue().get("Sections");
            //TODO: biomes, terrain populated flag etc.
            for (int y = 15; y >= 0; y--) {
                Chunk chunk = column.getChunk(y);
                CompoundTag tag = null;
                for (CompoundTag t : sectionsTag.getValue()) {
                    if (((ByteTag) t.getValue().get("Y")).getValue() == y) {
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
                        this.loadChunkImpl((ChunkImpl) chunk, tag.getValue());
                    } else {
                        SoftReference<ChunkImpl> ref = chunkImplCache.get();
                        ChunkImpl impl = ref.get();
                        if (impl == null) {
                            chunkImplCache.set(new SoftReference<>(impl = new ChunkImpl(-1, null)));
                        }
                        this.loadChunkImpl(impl, tag.getValue());
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
        {
            ListTag<CompoundTag> sectionsTag = (ListTag<CompoundTag>) rootTag.getValue().get("TileEntities");
            sectionsTag.getValue().stream()
                    .map(tag -> this.world.getSave().getInitFunctions().getTileEntityCreator().apply(this.world, tag))
                    .forEach(column.getTileEntities()::add);
            column.getTileEntities().forEach(tileEntity -> this.world.getLoadedTileEntities().put(tileEntity.getPos(), tileEntity));
        }
        this.world.getLoadedColumns().putIfAbsent(column.getPos(), column);
    }

    private void loadChunkImpl(@NonNull ChunkImpl impl, @NonNull CompoundMap map)   {
        impl.setBlockIds(((ByteArrayTag) map.get("Blocks")).getValue());
        impl.setMeta(new NibbleArray(((ByteArrayTag) map.get("Data")).getValue()));
        impl.setBlockLight(new NibbleArray(((ByteArrayTag) map.get("BlockLight")).getValue()));
        impl.setSkyLight(new NibbleArray(((ByteArrayTag) map.get("SkyLight")).getValue()));
        if (map.containsKey("Add")) {
            impl.setAdd(new NibbleArray(((ByteArrayTag) map.get("Add")).getValue()));
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

    public Collection<Vec2i> getRegions()   {
        Collection<Vec2i> positions = new ArrayDeque<>();
        for (File f : this.root.listFiles())    {
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
