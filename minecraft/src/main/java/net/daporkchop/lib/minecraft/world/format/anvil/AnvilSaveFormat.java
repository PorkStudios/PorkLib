/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.minecraft.world.format.anvil;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.minecraft.registry.IDRegistry;
import net.daporkchop.lib.minecraft.registry.IDRegistryBuilder;
import net.daporkchop.lib.minecraft.registry.ResourceLocation;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.MinecraftSave;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.minecraft.world.format.SaveFormat;
import net.daporkchop.lib.minecraft.world.format.WorldManager;
import net.daporkchop.lib.nbt.NBTInputStream;
import net.daporkchop.lib.nbt.tag.notch.CompoundTag;
import net.daporkchop.lib.nbt.tag.notch.ListTag;
import net.daporkchop.lib.primitive.lambda.biconsumer.IntObjBiConsumer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * @author DaPorkchop_
 */
@Getter
public class AnvilSaveFormat implements SaveFormat {
    protected static final Pattern DIM_PATTERN = Pattern.compile("^DIM(-?[0-9]+)$");

    private final File root;

    private CompoundTag levelDat;

    private MinecraftSave save;

    public AnvilSaveFormat(@NonNull File root) {
        this.root = root;

        if (root.exists()) {
            if (!root.isDirectory()) {
                throw new IllegalArgumentException(String.format("Anvil save root (%s) is a file!", root.getAbsolutePath()));
            }
        } else {
            //TODO
            throw new UnsupportedOperationException("create world");
        }
    }

    @Override
    public void init(@NonNull MinecraftSave save) throws IOException {
        this.save = save;
        {
            File levelDat_file = new File(this.root, "level.dat");
            if (levelDat_file.exists()) {
                if (levelDat_file.isDirectory()) {
                    throw new IllegalStateException(String.format("level.dat (%s) is a directory!", levelDat_file.getAbsolutePath()));
                }
            } else {
                throw new UnsupportedOperationException("create world");
            }
            try (NBTInputStream is = new NBTInputStream(new GZIPInputStream(new FileInputStream(levelDat_file)))) {
                this.levelDat = is.readTag();
            }
        }
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void loadWorlds(IntObjBiConsumer<WorldManager> callback) {
        callback.accept(0, new AnvilWorldManager(this, new File(this.root, "region")));

        //load other dimensions
        Matcher matcher = null;
        for (File file : this.root.listFiles()) {
            if (matcher == null)    {
                matcher = DIM_PATTERN.matcher(file.getName());
            } else {
                matcher.reset(file.getName());
            }
            if (matcher.find()) {
                callback.accept(Integer.parseInt(matcher.group(1)), new AnvilWorldManager(this, file));
            }
        }
    }

    @Override
    public void closeWorld(@NonNull World world) throws IOException {
        world.close();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loadRegistries(@NonNull BiConsumer<ResourceLocation, IDRegistry> callback) {
        CompoundTag fmlTag = this.levelDat.get("FML");
        if (fmlTag == null) {
            //TODO
            throw new UnsupportedOperationException("save must be created by FML, otherwise we can't read the registry! (this feature will be added Soon(tm))");
        } else {
            CompoundTag registriesTag = fmlTag.get("Registries");
            if (registriesTag == null) {
                throw new NullPointerException("Registries");
            }
            IDRegistryBuilder builder = IDRegistry.builder();
            registriesTag.getContents().forEach((key, nbt) -> {
                ResourceLocation registryName = new ResourceLocation(key);
                CompoundTag tag = nbt.getAsCompoundTag();
                builder.clear().name(registryName);
                for (CompoundTag subTag : tag.<ListTag<CompoundTag>>get("ids")) {
                    builder.register(new ResourceLocation(subTag.getString("K")), subTag.getInt("V", -1));
                }
                callback.accept(registryName, builder.build());
            });
        }
    }

    @Override
    public Chunk createColumnInstance(int x, int z) {
        return null;
    }
}
