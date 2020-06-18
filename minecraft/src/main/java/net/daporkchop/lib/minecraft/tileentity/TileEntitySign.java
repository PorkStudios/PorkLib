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
import lombok.ToString;
import lombok.experimental.Accessors;
import net.daporkchop.lib.logging.format.component.EmptyTextComponent;
import net.daporkchop.lib.logging.format.component.TextComponent;
import net.daporkchop.lib.minecraft.text.format.ChatColor;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.version.MinecraftVersion;

/**
 * @author DaPorkchop_
 */
@Setter
@Getter
@Accessors(fluent = true, chain = true)
public class TileEntitySign extends BaseTileEntity {
    public static final Identifier ID = Identifier.fromString("minecraft:sign");

    @NonNull
    protected ChatColor color = ChatColor.BLACK;
    @NonNull
    protected TextComponent line1 = EmptyTextComponent.INSTANCE;
    @NonNull
    protected TextComponent line2 = EmptyTextComponent.INSTANCE;
    @NonNull
    protected TextComponent line3 = EmptyTextComponent.INSTANCE;
    @NonNull
    protected TextComponent line4 = EmptyTextComponent.INSTANCE;

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    protected void doToString(@NonNull StringBuilder builder) {
        builder.append("Text=[\"")
                .append(this.line1.toRawString()).append("\", \"")
                .append(this.line2.toRawString()).append("\", \"")
                .append(this.line3.toRawString()).append("\", \"")
                .append(this.line4.toRawString()).append("\"], ");
        if (this.color != ChatColor.BLACK)  {
            builder.append("Color=").append(this.color).append(", ");
        }
    }
}
