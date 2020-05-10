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

package net.daporkchop.lib.minecraft.world.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.minecraft.registry.IDRegistry;
import net.daporkchop.lib.minecraft.registry.Identifier;
import net.daporkchop.lib.minecraft.world.MinecraftSave;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.minecraft.world.format.SaveFormat;
import net.daporkchop.lib.primitive.map.IntObjMap;
import net.daporkchop.lib.primitive.map.hash.opennode.IntObjOpenNodeHashMap;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class MinecraftSaveImpl implements MinecraftSave {
    private final SaveFormat          saveFormat;
    private final MinecraftSaveConfig config;
    private final Map<Identifier, IDRegistry> registries = new Hashtable<>();
    private final IntObjMap<World>                  worlds     = new IntObjOpenNodeHashMap<>();

    public MinecraftSaveImpl(@NonNull SaveBuilder builder) {
        this.saveFormat = builder.getFormat();
        this.config = builder.getInitFunctions();

        try {
            this.saveFormat.init(this);
        } catch (IOException e) {
            throw new RuntimeException("Unable to initialize save", e);
        }
        this.saveFormat.loadRegistries(this.registries::put);
        this.saveFormat.loadWorlds((id, worldManager) -> this.worlds.put(id, this.config.worldFactory().create(id, worldManager, this)));
    }

    @Override
    public void close() throws IOException {
        this.worlds.forEachValue(world -> {
            try {
                this.saveFormat.closeWorld(world);
            } catch (IOException e) {
                PUnsafe.throwException(e);
            }
        });
        this.saveFormat.close();
    }
}
