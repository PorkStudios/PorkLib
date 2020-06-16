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

package net.daporkchop.lib.minecraft.format.java.decoder.tile;

import lombok.NonNull;
import net.daporkchop.lib.minecraft.format.java.decoder.JavaDecoder;
import net.daporkchop.lib.minecraft.save.SaveOptions;
import net.daporkchop.lib.minecraft.tileentity.TileEntitySign;
import net.daporkchop.lib.minecraft.version.java.JavaVersion;
import net.daporkchop.lib.nbt.tag.CompoundTag;

/**
 * Decodes signs for versions prior to 1.14.
 *
 * @author DaPorkchop_
 */
public class SignDecoder1_13_2 implements JavaDecoder.TileEntity {
    public static final JavaVersion VERSION = JavaVersion.fromName("1.13.2");

    @Override
    public TileEntitySign decode(@NonNull CompoundTag tag, @NonNull JavaVersion version, @NonNull SaveOptions options) {
        return new TileEntitySign(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"), version)
                .line1(tag.getString("Text1", ""))
                .line2(tag.getString("Text2", ""))
                .line3(tag.getString("Text3", ""))
                .line4(tag.getString("Text4", ""));
    }
}
