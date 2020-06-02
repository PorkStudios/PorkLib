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

package net.daporkchop.lib.minecraft.world;

import net.daporkchop.lib.minecraft.util.Identifier;

/**
 * A description of a Minecraft dimension. A dimension describes various things about worlds using them, such as whether or not they have sky light or
 * rainfall. Having dimensions be separate from the IDs used to identify individual {@link World}s in a {@link net.daporkchop.lib.minecraft.save.Save}
 * allows us to support complex modded or server configuration, which might have e.g. multiple nether worlds.
 *
 * @author DaPorkchop_
 */
public interface Dimension {
    Identifier ID_OVERWORLD = Identifier.fromString("minecraft:overworld");
    Identifier ID_NETHER = Identifier.fromString("minecraft:nether");
    Identifier ID_END = Identifier.fromString("minecraft:the_end");

    /**
     * @return this dimension's ID, such as {@code "minecraft:overworld"} or {@code "minecraft:the_end"}
     */
    Identifier id();

    /**
     * @return this dimension's legacy or internal ID
     */
    int legacyId();

    /**
     * @return whether or not this dimension has sky light
     */
    boolean hasSkyLight();

    /**
     * @return whether or not this dimension has rain-/snowfall
     */
    boolean hasPrecipitation();
}
