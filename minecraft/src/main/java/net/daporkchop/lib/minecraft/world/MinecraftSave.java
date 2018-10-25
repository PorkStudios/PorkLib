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

package net.daporkchop.lib.minecraft.world;

import lombok.NonNull;
import net.daporkchop.lib.minecraft.registry.Registry;
import net.daporkchop.lib.minecraft.registry.RegistryEntry;
import net.daporkchop.lib.minecraft.registry.RegistryType;
import net.daporkchop.lib.minecraft.world.format.SaveManager;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * A Minecraft save file consists of multiple {@link World}s identified by {@link Dimension}s
 *
 * @author DaPorkchop_
 */
public interface MinecraftSave {
    /**
     * @return the save manager for this level
     */
    SaveManager getSaveManager();

    /**
     * Get a registry for a given registry type
     *
     * @param type the type of registry
     * @param <T>  convenience parameter, allows the returned value to be an instance of the
     *             desired subclass of {@link RegistryEntry} without manual casting
     * @return a registry for the given type
     */
    <T extends RegistryEntry> Registry<T> getRegistry(@NonNull RegistryType type);

    /**
     * @return All dimension -> world mappings
     */
    Map<Dimension, World> getDimensionWorldMap();

    default Set<Dimension> getDimensions()   {
        return this.getDimensionWorldMap().keySet();
    }

    default Collection<World> getWorlds()   {
        return this.getDimensionWorldMap().values();
    }

    default World getWorld(@NonNull Dimension dimension)    {
        if (!this.getRegistry(RegistryType.DIMENSION).hasValue(dimension))  {
            throw new IllegalArgumentException(String.format("Dimension %s does not exist!", dimension.getRegistryName().toString()));
        }
        return this.getDimensionWorldMap().get(dimension);
    }
}
