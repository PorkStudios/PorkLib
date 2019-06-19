/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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

package net.daporkchop.lib.minecraft.world.impl;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.minecraft.registry.Registry;
import net.daporkchop.lib.minecraft.registry.ResourceLocation;
import net.daporkchop.lib.minecraft.world.MinecraftSave;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.minecraft.world.format.SaveFormat;
import net.daporkchop.lib.primitive.map.IntObjMap;
import net.daporkchop.lib.primitive.map.hash.opennode.IntObjOpenNodeHashMap;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
@Getter
public class MinecraftSaveImpl implements MinecraftSave {
    private final SaveFormat saveFormat;
    private final Map<ResourceLocation, Registry> registries = new Hashtable<>();
    private final IntObjMap<World> worlds = new IntObjOpenNodeHashMap<>();
    private final InitFunctions initFunctions;

    public MinecraftSaveImpl(@NonNull SaveBuilder builder) {
        this.saveFormat = builder.getFormat();
        this.initFunctions = builder.getInitFunctions();

        try {
            this.saveFormat.init(this);
        } catch (IOException e) {
            throw new RuntimeException("Unable to initialize save", e);
        }
        this.saveFormat.loadRegistries(this.registries::put);
        this.saveFormat.loadWorlds((id, worldManager) -> this.worlds.put(id, this.initFunctions.getWorldCreator().create(id, worldManager, this)));
    }

    @Override
    public void close() throws IOException {
        this.worlds.forEachValue(this.saveFormat::closeWorld);
        this.saveFormat.close();
    }
}
