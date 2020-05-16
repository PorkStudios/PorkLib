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

package net.daporkchop.lib.minecraft.format.anvil;

import lombok.NonNull;
import net.daporkchop.lib.minecraft.format.common.AbstractSave;
import net.daporkchop.lib.minecraft.save.SaveOptions;
import net.daporkchop.lib.minecraft.version.MinecraftVersion;
import net.daporkchop.lib.nbt.tag.CompoundTag;

import java.io.File;

/**
 * @author DaPorkchop_
 */
public class AnvilSave extends AbstractSave<AnvilSaveOptions> {
    public AnvilSave(@NonNull File root, @NonNull SaveOptions options, @NonNull CompoundTag levelData) {
        super(root, options, levelData);
    }

    @Override
    protected AnvilSaveOptions processOptions(@NonNull SaveOptions options) {
        return options instanceof AnvilSaveOptions
               ? (AnvilSaveOptions) options.clone()
               : new AnvilSaveOptions().access(options.access()).ioExecutor(options.ioExecutor());
    }

    @Override
    protected MinecraftVersion getVersion() {
        CompoundTag versionTag = this.levelData.getCompound("Data").getCompound("Version", null);
        if (versionTag == null) {
            //the world is older than 15w32a
            return MinecraftVersion.UNKNOWN_JAVA;
        }
        return MinecraftVersion.fromNameAndDataVersion(
                versionTag.getString("Name"),
                versionTag.getInt("Id"),
                versionTag.getByte("Snapshot", (byte) 0) != 0);
    }
}
