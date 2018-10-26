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
import net.daporkchop.lib.minecraft.world.Dimension;
import net.daporkchop.lib.minecraft.world.format.ChunkProvider;
import net.daporkchop.lib.minecraft.world.format.SaveManager;
import net.daporkchop.lib.nbt.NBTIO;
import net.daporkchop.lib.nbt.tag.impl.notch.CompoundTag;
import net.daporkchop.lib.primitive.tuple.ObjectObjectTuple;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * @author DaPorkchop_
 */
public class AnvilSaveManager implements SaveManager {
    @Getter
    private final File root;

    private CompoundTag levelDat;

    public AnvilSaveManager(@NonNull File root) {
        if (root.exists()) {
            if (!root.isDirectory()) {
                throw new IllegalArgumentException(String.format("File %s is not a directory!", root.getAbsolutePath()));
            }
        } else {
            if (!root.mkdirs()) {
                if (true) {
                    throw new UnsupportedOperationException("create world");
                    //TODO
                }
                throw new RuntimeException(String.format("Could not create directory %s", root.getAbsolutePath()));
            }
        }
        this.root = root;
    }

    @Override
    public void init() throws IOException {
        File level = new File(this.root, "level.dat");
        if (!level.exists()) {
            throw new UnsupportedOperationException("create world");
            //TODO
        }
        try (InputStream is = new FileInputStream(level)) {
            this.levelDat = NBTIO.readCompressed(is);
        }
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public Collection<ObjectObjectTuple<Dimension, ChunkProvider>> getProvidersForAllWorlds() {
        return null;
    }

    @Override
    public void loadRegistry(Registry registry) {
        CompoundTag tag = null;
        switch (registry.getType()) {
            case ITEM:
        }
    }
}
