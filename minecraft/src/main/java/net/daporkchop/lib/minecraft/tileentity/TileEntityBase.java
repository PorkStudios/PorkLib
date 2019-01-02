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

package net.daporkchop.lib.minecraft.tileentity;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.minecraft.registry.ResourceLocation;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.nbt.tag.notch.CompoundTag;
import net.daporkchop.lib.nbt.tag.notch.IntTag;
import net.daporkchop.lib.nbt.tag.notch.StringTag;

/**
 * @author DaPorkchop_
 */
@Getter
public class TileEntityBase implements TileEntity {
    @NonNull
    protected final World world;

    @NonNull
    protected final CompoundTag data;

    protected int x;
    protected int y;
    protected int z;

    protected ResourceLocation id;

    public TileEntityBase(@NonNull World world, @NonNull CompoundTag data)  {
        this.world = world;
        this.data = data;
        this.init();
    }

    protected void init()   {
        this.id = new ResourceLocation(this.data.<StringTag>get("id").getValue());
        this.x = this.data.<IntTag>get("x").getValue();
        this.y = this.data.<IntTag>get("y").getValue();
        this.z = this.data.<IntTag>get("z").getValue();
    }

    public void save()   {
    }
}
