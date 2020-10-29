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
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.minecraft.entity.EntityRegistry;
import net.daporkchop.lib.minecraft.tileentity.TileEntityRegistry;
import net.daporkchop.lib.minecraft.util.factory.ChunkFactory;
import net.daporkchop.lib.minecraft.util.factory.EntityFactory;
import net.daporkchop.lib.minecraft.util.factory.SectionFactory;
import net.daporkchop.lib.minecraft.util.factory.TileEntityFactory;
import net.daporkchop.lib.minecraft.util.factory.WorldFactory;
import net.daporkchop.lib.minecraft.world.format.anvil.region.RegionOpenOptions;
import net.daporkchop.lib.minecraft.world.impl.section.HeapSectionImpl;
import net.daporkchop.lib.minecraft.world.impl.vanilla.VanillaChunkImpl;
import net.daporkchop.lib.minecraft.world.impl.vanilla.VanillaWorldImpl;

/**
 * @author DaPorkchop_
 */
@Setter
@Getter
@Accessors(fluent = true, chain = true)
public class MinecraftSaveConfig {
    @NonNull
    protected WorldFactory      worldFactory      = VanillaWorldImpl::new;
    @NonNull
    protected ChunkFactory      chunkFactory      = VanillaChunkImpl::new;
    @NonNull
    protected SectionFactory    sectionFactory    = HeapSectionImpl::new;
    @NonNull
    protected EntityFactory entityFactory = EntityRegistry.defaultRegistry();
    @NonNull
    protected TileEntityFactory tileEntityFactory = TileEntityRegistry.defaultRegistry(); //TODO: make this be a lazy reference to avoid creating the default registry if it's never used
    @NonNull
    protected RegionOpenOptions openOptions       = new RegionOpenOptions(); //TODO: this is specific to anvil, so move it out of here
}
