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
import net.daporkchop.lib.minecraft.block.BlockRegistry;
import net.daporkchop.lib.minecraft.block.java.JavaBlockRegistry;
import net.daporkchop.lib.minecraft.format.common.AbstractSave;
import net.daporkchop.lib.minecraft.format.common.DefaultDimension;
import net.daporkchop.lib.minecraft.registry.Registries;
import net.daporkchop.lib.minecraft.registry.java.JavaRegistries;
import net.daporkchop.lib.minecraft.save.SaveOptions;
import net.daporkchop.lib.minecraft.version.MinecraftEdition;
import net.daporkchop.lib.minecraft.version.MinecraftVersion;
import net.daporkchop.lib.minecraft.version.java.JavaVersion;
import net.daporkchop.lib.minecraft.world.Dimension;
import net.daporkchop.lib.nbt.NBTOptions;
import net.daporkchop.lib.nbt.tag.CompoundTag;

import java.io.File;

import static net.daporkchop.lib.common.util.PValidation.checkArg;

/**
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public class AnvilSave extends AbstractSave<JavaVersion> {
    @Getter
    protected final NBTOptions chunkNBTOptions;

    public AnvilSave(SaveOptions options, CompoundTag levelData, File root) {
        super(options, root);

        this.chunkNBTOptions = NBTOptions.DEFAULT
                .withByteAlloc(options.get(SaveOptions.BYTE_ALLOC))
                .withIntAlloc(options.get(SaveOptions.INT_ALLOC))
                .withLongAlloc(options.get(SaveOptions.LONG_ALLOC));
                //.withObjectParser(null); //TODO

        this.version = this.extractVersion(levelData);
        this.registries = JavaRegistries.forVersion(this.version);
        this.blockRegistry = JavaBlockRegistry.forVersion(this.version);

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
        this.worlds.put(dimension.id(), new AnvilWorld(this, dimension));
    }

    protected JavaVersion extractVersion(@NonNull CompoundTag levelData)   {
        CompoundTag versionTag = levelData.getCompound("Data").getCompound("Version", null);
        if (versionTag == null) { //older than 15w32a
            return JavaVersion.pre15w32a();
        }
        return JavaVersion.fromName(versionTag.getString("Name"));
    }

    @Override
    public Registries registriesFor(@NonNull MinecraftVersion version) {
        checkArg(version instanceof JavaVersion, "invalid version: %s", version);
        JavaVersion java = (JavaVersion) version;
        checkArg(this.version.data() <= java.data(), "version (%s) may not be newer than save version (%s)", java, this.version);
        if (this.version == java)   {
            return this.registries;
        }
        return JavaRegistries.forVersion(java);
    }

    @Override
    public BlockRegistry blockRegistryFor(@NonNull MinecraftVersion version) {
        checkArg(version instanceof JavaVersion, "invalid version: %s", version);
        JavaVersion java = (JavaVersion) version;
        checkArg(this.version.data() <= java.data(), "version (%s) may not be newer than save version (%s)", java, this.version);
        if (this.version == java)   {
            return this.blockRegistry;
        }
        return JavaBlockRegistry.forVersion(java);
    }
}
