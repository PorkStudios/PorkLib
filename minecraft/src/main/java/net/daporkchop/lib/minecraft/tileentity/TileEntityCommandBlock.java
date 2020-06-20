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

package net.daporkchop.lib.minecraft.tileentity;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.logging.format.component.TextComponent;
import net.daporkchop.lib.minecraft.item.inventory.InventoryHolder;
import net.daporkchop.lib.minecraft.util.Identifier;

/**
 * @author DaPorkchop_
 */
@Getter
@Setter
@Accessors(fluent = true, chain = true)
public class TileEntityCommandBlock extends BaseTileEntity {
    public static final Identifier ID = Identifier.fromString("minecraft:command_block");

    protected TextComponent customName;
    protected String command;
    protected TextComponent lastOutput;
    protected boolean trackOutput = true;
    protected int successCount;

    protected boolean powered = false;
    protected boolean auto = false;
    protected boolean conditionMet = false;
    protected boolean updateLastExecution = true;
    protected long lastExecution = 0L;

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    protected void doToString(@NonNull StringBuilder builder) {
        if (this.customName != null) {
            builder.append("CustomName=").append(this.customName).append(", ");
        }
        if (this.command != null) {
            builder.append("Command=\"").append(this.command).append("\", ");
        }
        if (this.lastOutput != null) {
            builder.append("LastOutput=").append(this.lastOutput).append(", ");
        }
        builder.append("TrackOutput=").append(this.trackOutput).append(", ")
                .append("SuccessCount=").append(this.successCount).append(", ")
                .append("Powered=").append(this.powered).append(", ")
                .append("ConditionMet=").append(this.conditionMet).append(", ")
                .append("UpdateLastExecution=").append(this.updateLastExecution).append(", ")
                .append("LastExecution=").append(this.lastExecution).append(", ");
    }
}
