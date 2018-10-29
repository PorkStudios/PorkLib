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

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.minecraft.registry.Registry;
import net.daporkchop.lib.minecraft.registry.ResourceLocation;
import net.daporkchop.lib.minecraft.world.Column;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.minecraft.world.format.SaveFormat;
import net.daporkchop.lib.minecraft.world.format.WorldManager;
import net.daporkchop.lib.nbt.NBTIO;
import net.daporkchop.lib.nbt.tag.impl.notch.CompoundTag;
import net.daporkchop.lib.primitive.lambda.consumer.bi.IntegerObjectConsumer;
import net.daporkchop.lib.primitive.tuple.ObjectObjectImmutableTuple;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiConsumer;

/**
 * @author DaPorkchop_
 */
@Getter
public class AnvilSaveFormat implements SaveFormat {
    private final File root;

    private CompoundTag levelDat;

    public AnvilSaveFormat(@NonNull File root)  {
        this.root = root;

        if (root.exists()) {
            if (!root.isDirectory())    {
                throw new IllegalArgumentException(String.format("Anvil save root (%s) is a file!", root.getAbsolutePath()));
            }
        } else {
            //TODO
            throw new UnsupportedOperationException("create world");
        }
    }

    @Override
    public void init() throws IOException {
        {
            File levelDat_file = new File(this.root, "level.dat");
            if (levelDat_file.exists()) {
                if (levelDat_file.isDirectory())    {
                    throw new IllegalStateException(String.format("level.dat (%s) is a directory!", levelDat_file.getAbsolutePath()));
                }
            } else {
                throw new UnsupportedOperationException("create world");
            }
            try (InputStream is = new FileInputStream(levelDat_file))   {
                this.levelDat = NBTIO.readCompressed(is);
            }
        }
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void loadWorlds(IntegerObjectConsumer<WorldManager> addFunction) {

    }

    @Override
    public void closeWorld(World world) {
    }

    @Override
    public void loadRegistries(BiConsumer<ResourceLocation, Registry> addFunction) {
        CompoundTag fmlTag = this.levelDat.getCompound("FML");
        if (fmlTag == null) {
            //TODO
            throw new UnsupportedOperationException("save must be created by FML, otherwise we can't read the registry! (this feature will be added Soon(tm))");
        } else {
            CompoundTag registriesTag = fmlTag.getCompound("Registries");
            if (registriesTag == null)  {
                throw new NullPointerException("Registries");
            }
            registriesTag.getValue().entrySet().stream()
                    .map(entry -> new ObjectObjectImmutableTuple<>(new ResourceLocation(entry.getKey()), (CompoundTag) entry.getValue()))
                    .forEach(tuple -> {
                        ResourceLocation registryName = tuple.getK();
                        Registry registry = new Registry(registryName);
                        tuple.getV().<CompoundTag>getTypedList("ids").getValue()
                                .forEach(tag -> registry.registerEntry(new ResourceLocation(tag.getString("K")), tag.getInt("V")));
                        addFunction.accept(registryName, registry);
                    });
        }
    }

    @Override
    public Column createColumnInstance(int x, int z) {
        return null;
    }
}
