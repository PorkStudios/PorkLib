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

import lombok.NonNull;
import net.daporkchop.lib.math.vector.i.Vec3i;
import net.daporkchop.lib.minecraft.block.BlockAccess;
import net.daporkchop.lib.random.PRandom;

import java.util.Collection;

/**
 * Generates terrain for a world.
 * <p>
 * Instances of this class are expected to be thread-safe: all methods (with the exception of {@link #init(long, String)}) must be able to be safely called
 * from multiple threads concurrently without affecting the operation of the generator.
 *
 * @author DaPorkchop_
 */
public interface Generator {
    /**
     * Initializes this terrain generator instance.
     * <p>
     * Will not be called more than once. Generation methods will never be called until this method has returned.
     *
     * @param seed    the world's seed
     * @param options additional generator options to be used. Will be empty if none are set
     */
    void init(long seed, @NonNull String options);

    /**
     * Generates the terrain for the given chunk section.
     *
     * @param random  an instance of {@link PRandom} initialized with a seed based on the section coordinates and the world seed
     * @param section the section to generate. Will be filled with nothing but air initially
     * @param x       the section's X coordinate
     * @param y       the section's Y coordinate
     * @param z       the section's Z coordinate
     */
    void generate(@NonNull PRandom random, @NonNull Section section, int x, int y, int z);

    /**
     * Gets the coordinates of the chunk sections that are required for the given chunk section to be populated.
     *
     * @param x the X coordinate of the section to be populated
     * @param y the Y coordinate of the section to be populated
     * @param z the Z coordinate of the section to be populated
     * @return the coordinates of the chunk sections that will be generated before the given section will be populated
     */
    Collection<Vec3i> populationSections(int x, int y, int z);

    /**
     * Populates the chunk section at the given coordinates.
     *
     * @param random   an instance of {@link PRandom} initialized with a seed based on the section coordinates and the world seed
     * @param world    an instance of {@link BlockAccess} providing read-write access to all chunk sections requested by {@link #populationSections(int, int, int)}
     * @param sectionX the X coordinate of the section to be populated
     * @param sectionY the Y coordinate of the section to be populated
     * @param sectionZ the Z coordinate of the section to be populated
     */
    void populate(@NonNull PRandom random, @NonNull BlockAccess world, int sectionX, int sectionY, int sectionZ);
}
