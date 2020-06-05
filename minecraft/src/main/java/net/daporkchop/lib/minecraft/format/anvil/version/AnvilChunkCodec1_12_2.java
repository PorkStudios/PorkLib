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

package net.daporkchop.lib.minecraft.format.anvil.version;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.compat.datafix.DataCodec;
import net.daporkchop.lib.minecraft.format.anvil.AnvilWorld;
import net.daporkchop.lib.minecraft.format.anvil.chunk.VanillaAnvilChunk;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.Section;
import net.daporkchop.lib.nbt.tag.CompoundTag;

/**
 * Codec for serialization of chunks in the pre-flattening format used by Minecraft versions 1.12.2 and older.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class AnvilChunkCodec1_12_2 implements DataCodec<Chunk, CompoundTag> {
    @NonNull
    protected final AnvilWorld world;

    @Override
    public Chunk decode(@NonNull CompoundTag root) {
        CompoundTag level = root.getCompound("Level");
        int x = level.getInt("xPos");
        int z = level.getInt("zPos");

        Section[] sections = new Section[16];
        Chunk chunk = new VanillaAnvilChunk(this.world.retain(), x, z, sections);
        //TODO: actually set sections
        return chunk;
    }

    @Override
    public CompoundTag encode(@NonNull Chunk value) {
        throw new UnsupportedOperationException(); //TODO
    }
}
