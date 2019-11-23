/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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

package net.daporkchop.lib.minecraft.world.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.math.vector.i.Vec2i;
import net.daporkchop.lib.math.vector.i.Vec3i;
import net.daporkchop.lib.minecraft.tileentity.TileEntity;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.MinecraftSave;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.minecraft.world.format.WorldManager;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author DaPorkchop_
 */
@Getter
public class WorldImpl implements World {
    private final int id;
    private final Map<Vec3i, TileEntity> loadedTileEntities = new ConcurrentHashMap<>();
    @NonNull
    private final WorldManager manager;
    @NonNull
    private final MinecraftSave save;
    private final LoadingCache<Vec2i, Chunk> loadedColumns = CacheBuilder.newBuilder()
            .concurrencyLevel(1)
            .maximumSize(34 * 34 * Runtime.getRuntime().availableProcessors())
            .expireAfterAccess(30L, TimeUnit.SECONDS) //TODO: configurable
            .removalListener((RemovalListener<Vec2i, Chunk>) n -> {
                if (n.getCause() != RemovalCause.REPLACED) {
                    n.getValue().unload();
                }
            })
            .build(new CacheLoader<Vec2i, Chunk>() {
                @Override
                public Chunk load(Vec2i key) throws Exception {
                    Chunk chunk = WorldImpl.this.save.getInitFunctions().getChunkFactory().create(key, WorldImpl.this);
                    //WorldImpl.this.manager.loadColumn(chunk);
                    return chunk;
                }
            });
    /*private final Map<Vec2i, Chunk> loadedColumns = new LinkedHashMap<Vec2i, Chunk>()  {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return this.size() > 256;
        }
    };*/

    public WorldImpl(int id, @NonNull WorldManager manager, @NonNull MinecraftSave save) {
        this.id = id;
        this.manager = manager;
        this.save = save;

        manager.setWorld(this);
    }

    @Override
    public Map<Vec2i, Chunk> getLoadedColumns() {
        return this.loadedColumns.asMap();
    }

    @Override
    public TileEntity getTileEntity(int x, int y, int z) {
        return this.loadedTileEntities.get(new Vec3i(x, y, z));
    }

    @Override
    public Chunk getColumn(int x, int z) {
        return this.loadedColumns.getUnchecked(new Vec2i(x, z));
        //return this.loadedColumns.computeIfAbsent(new Vec2i(x, z), pos -> this.save.getInitFunctions().getChunkFactory().apply(pos, this));
    }

    @Override
    public Chunk getColumnOrNull(int x, int z) {
        return this.loadedColumns.getIfPresent(new Vec2i(x, z));
        //return this.loadedColumns.get(new Vec2i(x, z));
    }

    @Override
    public void save() {
        this.loadedColumns.asMap().values().forEach(Chunk::save);
        //this.loadedColumns.values().forEach(Chunk::save);
        //TODO
    }

    @Override
    public void close() throws IOException {
        this.save();
        this.loadedColumns.invalidateAll();
        //this.loadedColumns.values().forEach(Chunk::unload);
        this.loadedTileEntities.clear();
        //TODO
    }
}
