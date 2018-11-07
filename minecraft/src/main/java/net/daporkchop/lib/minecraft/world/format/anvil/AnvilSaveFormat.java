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

package net.daporkchop.lib.minecraft.world.format.anvil;

import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.IntTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.StringTag;
import com.flowpowered.nbt.stream.NBTInputStream;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.minecraft.registry.Registry;
import net.daporkchop.lib.minecraft.registry.ResourceLocation;
import net.daporkchop.lib.minecraft.world.Column;
import net.daporkchop.lib.minecraft.world.MinecraftSave;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.minecraft.world.format.SaveFormat;
import net.daporkchop.lib.minecraft.world.format.WorldManager;
import net.daporkchop.lib.primitive.function.biconsumer.IntegerObjectBiConsumer;
import net.daporkchop.lib.primitive.tuple.ObjectObjectTuple;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.function.BiConsumer;

/**
 * @author DaPorkchop_
 */
@Getter
public class AnvilSaveFormat implements SaveFormat {
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
            try (NBTInputStream is = new NBTInputStream(new FileInputStream(levelDat_file), true)) {
                this.levelDat = (CompoundTag) is.readTag();
            }
        }
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void loadWorlds(IntegerObjectBiConsumer<WorldManager> addFunction) {
        addFunction.accept(0, new AnvilWorldManager(this, new File(this.root, "region")));
        //TODO: other dimensions
    }

    @Override
    public void closeWorld(World world) {
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loadRegistries(BiConsumer<ResourceLocation, Registry> addFunction) {
        CompoundTag fmlTag = (CompoundTag) this.levelDat.getValue().get("FML");
        if (fmlTag == null) {
            //TODO
            throw new UnsupportedOperationException("save must be created by FML, otherwise we can't read the registry! (this feature will be added Soon(tm))");
        } else {
            CompoundTag registriesTag = (CompoundTag) fmlTag.getValue().get("Registries");
            if (registriesTag == null) {
                throw new NullPointerException("Registries");
            }
            registriesTag.getValue().entrySet().stream()
                    .map(entry -> new ObjectObjectTuple<>(new ResourceLocation(entry.getKey()), (CompoundTag) entry.getValue()))
                    .forEach(tuple -> {
                        ResourceLocation registryName = tuple.getK();
                        Registry registry = new Registry(registryName);
                        ((ListTag<CompoundTag>) tuple.getV().getValue().get("ids")).getValue().forEach(tag -> registry.registerEntry(new ResourceLocation(((StringTag) tag.getValue().get("K")).getValue()), ((IntTag) tag.getValue().get("V")).getValue()));
                        addFunction.accept(registryName, registry);
                    });
        }
    }

    @Override
    public Column createColumnInstance(int x, int z) {
        return null;
    }
}
