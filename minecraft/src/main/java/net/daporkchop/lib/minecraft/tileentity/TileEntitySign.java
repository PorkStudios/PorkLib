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

package net.daporkchop.lib.minecraft.tileentity;

import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.StringTag;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.daporkchop.lib.minecraft.text.util.ChatUtils;
import net.daporkchop.lib.minecraft.world.World;

/**
 * @author DaPorkchop_
 */
@Getter
@Setter
public class TileEntitySign extends TileEntityBase {
    @NonNull
    private String line1;
    @NonNull
    private String line2;
    @NonNull
    private String line3;
    @NonNull
    private String line4;

    public TileEntitySign(World world, CompoundTag data) {
        super(world, data);
    }

    @Override
    protected void init() {
        super.init();
        this.line1 = ChatUtils.getOldText(((StringTag) this.data.getValue().get("Text1")).getValue());
        this.line2 = ChatUtils.getOldText(((StringTag) this.data.getValue().get("Text2")).getValue());
        this.line3 = ChatUtils.getOldText(((StringTag) this.data.getValue().get("Text3")).getValue());
        this.line4 = ChatUtils.getOldText(((StringTag) this.data.getValue().get("Text4")).getValue());
    }

    @Override
    public void save() {
        super.save();
        //TODO
    }
}
