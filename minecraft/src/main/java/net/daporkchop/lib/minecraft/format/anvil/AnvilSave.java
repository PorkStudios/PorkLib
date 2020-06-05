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

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.minecraft.format.common.AbstractSave;
import net.daporkchop.lib.minecraft.format.common.DefaultDimension;
import net.daporkchop.lib.minecraft.save.SaveOptions;
import net.daporkchop.lib.minecraft.version.MinecraftVersion;
import net.daporkchop.lib.minecraft.version.java.JavaVersion;
import net.daporkchop.lib.minecraft.world.Dimension;
import net.daporkchop.lib.nbt.NBTOptions;
import net.daporkchop.lib.nbt.tag.CompoundTag;

import java.io.File;

/**
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public class AnvilSave extends AbstractSave<AnvilSaveOptions> {
    @Getter
    protected final NBTOptions chunkNBTOptions;

    public AnvilSave(SaveOptions options, CompoundTag levelData, File root) {
        super(new AnvilSaveOptions(options), root);

        this.chunkNBTOptions = NBTOptions.DEFAULT
                .withByteAlloc(options.byteAlloc())
                .withIntAlloc(options.intAlloc())
                .withLongAlloc(options.longAlloc());
                //.withObjectParser(null); //TODO

        this.version = this.extractVersion(levelData);

        //find worlds
        this.openWorld(new DefaultDimension(Dimension.ID_OVERWORLD, 0, true, true));
        if (PFiles.checkDirectoryExists(new File(this.root, "DIM-1"))) {
            this.openWorld(new DefaultDimension(Dimension.ID_NETHER, -1, false, false));
        }
        if (PFiles.checkDirectoryExists(new File(this.root, "DIM1"))) {
            this.openWorld(new DefaultDimension(Dimension.ID_END, 1, false, false));
        }

        this.validateState();
    }

    protected void openWorld(@NonNull Dimension dimension)  {
        this.worlds.put(dimension.id(), new AnvilWorld(this, this.options, dimension));
    }

    protected MinecraftVersion extractVersion(@NonNull CompoundTag levelData)   {
        CompoundTag versionTag = levelData.getCompound("Data").getCompound("Version", null);
        if (versionTag == null) { //older than 15w32a
            return JavaVersion.pre15w32a();
        }
        return JavaVersion.fromName(versionTag.getString("Name"));
    }
}
