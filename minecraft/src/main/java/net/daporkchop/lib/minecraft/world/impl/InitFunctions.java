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

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.math.vector.i.Vec2i;
import net.daporkchop.lib.minecraft.tileentity.TileEntity;
import net.daporkchop.lib.minecraft.tileentity.TileEntityBase;
import net.daporkchop.lib.minecraft.tileentity.TileEntitySign;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.Column;
import net.daporkchop.lib.minecraft.world.MinecraftSave;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.minecraft.world.format.WorldManager;
import net.daporkchop.lib.nbt.tag.notch.CompoundTag;
import net.daporkchop.lib.nbt.tag.notch.StringTag;
import net.daporkchop.lib.primitive.function.bifunction.IntObjObjBiFunction;

import java.util.function.BiFunction;

/**
 * @author DaPorkchop_
 */
@Accessors(chain = true)
@Setter
@Getter
public class InitFunctions {
    @NonNull
    private WorldCreator worldCreator = WorldImpl::new;

    @NonNull
    private BiFunction<Vec2i, World, Column> columnCreator = ColumnImpl::new;

    @NonNull
    private IntObjObjBiFunction<Column, Chunk> chunkCreator = ChunkImpl::new;

    @NonNull
    private BiFunction<World, CompoundTag, TileEntity> tileEntityCreator = (world, tag) -> {
        String id = tag.<StringTag>get("id").getValue();
        switch (id) {
            case "minecraft:sign":
                return new TileEntitySign(world, tag);
            default:
                return new TileEntityBase(world, tag);
        }
    }; //TODO: cleaner

    @FunctionalInterface
    public interface WorldCreator {
        World create(int id, WorldManager manager, MinecraftSave save);
    }
}
