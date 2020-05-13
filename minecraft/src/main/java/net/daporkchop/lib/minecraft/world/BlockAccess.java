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
import net.daporkchop.lib.minecraft.registry.BlockRegistry;
import net.daporkchop.lib.minecraft.util.Identifier;

/**
 * Provides access to block and light data at given coordinates.
 *
 * @author DaPorkchop_
 */
public interface BlockAccess {
    /**
     * @return the {@link BlockRegistry} used for converting block IDs
     */
    BlockRegistry blockRegistry();

    /**
     * Gets the number of block layers that can be accessed.
     * <p>
     * This doesn't necessarily mean that there are this many block layers everywhere in the world. Rather, it should be considered an upper bound on
     * the number of block layers. Attempting to get block information in a non-existent layer will simply return air, and setting in a non-existent
     * layer will result in a new layer being allocated.
     * <p>
     * Guaranteed to always be at least 1.
     *
     * @return the number of block layers that can be accessed
     */
    int layers();

    /**
     * Checks whether or not this {@link BlockAccess} contains sky light.
     * <p>
     * If {@code false}, {@link #getSkyLight(int, int, int)} will always return {@code 0} and {@link #setSkyLight(int, int, int, int)} will always
     * throw {@link UnsupportedOperationException}.
     *
     * @return whether or not this {@link BlockAccess} contains sky light
     */
    boolean hasSkyLight();

    //
    //
    // getters
    //
    //

    /**
     * Gets the {@link BlockState} at the given coordinates in the default layer.
     *
     * @param x the X coordinate of the block to get
     * @param y the Y coordinate of the block to get
     * @param z the Z coordinate of the block to get
     * @return the {@link BlockState}
     */
    BlockState getBlockState(int x, int y, int z);

    /**
     * Gets the {@link BlockState} at the given coordinates in the given layer.
     *
     * @param x     the X coordinate of the block to get
     * @param y     the Y coordinate of the block to get
     * @param z     the Z coordinate of the block to get
     * @param layer the block layer index
     * @return the {@link BlockState}
     */
    BlockState getBlockState(int x, int y, int z, int layer);

    /**
     * Gets the {@link Identifier} of the block at the given coordinates in the default layer.
     *
     * @param x the X coordinate of the block to get
     * @param y the Y coordinate of the block to get
     * @param z the Z coordinate of the block to get
     * @return the {@link Identifier} of the block
     */
    Identifier getBlockId(int x, int y, int z);

    /**
     * Gets the {@link Identifier} of the block at the given coordinates in the given layer.
     *
     * @param x     the X coordinate of the block to get
     * @param y     the Y coordinate of the block to get
     * @param z     the Z coordinate of the block to get
     * @param layer the block layer index
     * @return the {@link Identifier} of the block
     */
    Identifier getBlockId(int x, int y, int z, int layer);

    /**
     * Gets the legacy ID of the block at the given coordinates in the default layer.
     *
     * @param x the X coordinate of the block to get
     * @param y the Y coordinate of the block to get
     * @param z the Z coordinate of the block to get
     * @return the legacy ID of the block
     */
    int getBlockLegacyId(int x, int y, int z);

    /**
     * Gets the legacy ID of the block at the given coordinates in the given layer.
     *
     * @param x     the X coordinate of the block to get
     * @param y     the Y coordinate of the block to get
     * @param z     the Z coordinate of the block to get
     * @param layer the block layer index
     * @return the legacy ID of the block
     */
    int getBlockLegacyId(int x, int y, int z, int layer);

    /**
     * Gets the metadata of the block at the given coordinates in the default layer.
     *
     * @param x the X coordinate of the block to get
     * @param y the Y coordinate of the block to get
     * @param z the Z coordinate of the block to get
     * @return the metadata of the block
     */
    int getBlockMeta(int x, int y, int z);

    /**
     * Gets the metadata of the block at the given coordinates in the given layer.
     *
     * @param x     the X coordinate of the block to get
     * @param y     the Y coordinate of the block to get
     * @param z     the Z coordinate of the block to get
     * @param layer the block layer index
     * @return the metadata of the block
     */
    int getBlockMeta(int x, int y, int z, int layer);

    /**
     * Gets the runtime ID of the block at the given coordinates in the default layer.
     *
     * @param x the X coordinate of the block to get
     * @param y the Y coordinate of the block to get
     * @param z the Z coordinate of the block to get
     * @return the runtime ID of the block
     */
    int getBlockRuntimeId(int x, int y, int z);

    /**
     * Gets the runtime ID of the block at the given coordinates in the given layer.
     *
     * @param x     the X coordinate of the block to get
     * @param y     the Y coordinate of the block to get
     * @param z     the Z coordinate of the block to get
     * @param layer the block layer index
     * @return the runtime ID of the block
     */
    int getBlockRuntimeId(int x, int y, int z, int layer);

    /**
     * Gets the block light level at the given coordinates.
     *
     * @param x the X coordinate to get the light level at
     * @param y the Y coordinate to get the light level at
     * @param z the Z coordinate to get the light level at
     * @return the block light level
     */
    int getBlockLight(int x, int y, int z);

    /**
     * Gets the sky light level at the given coordinates.
     *
     * @param x the X coordinate to get the light level at
     * @param y the Y coordinate to get the light level at
     * @param z the Z coordinate to get the light level at
     * @return the sky light level
     */
    int getSkyLight(int x, int y, int z);

    //
    //
    // setters
    //
    //

    /**
     * Sets the {@link BlockState} at the given coordinates in the default layer.
     *
     * @param x     the X coordinate of the block to set
     * @param y     the Y coordinate of the block to set
     * @param z     the Z coordinate of the block to set
     * @param state the new {@link BlockState}
     */
    void setBlockState(int x, int y, int z, @NonNull BlockState state);

    /**
     * Sets the {@link BlockState} at the given coordinates in the given layer.
     *
     * @param x     the X coordinate of the block to set
     * @param y     the Y coordinate of the block to set
     * @param z     the Z coordinate of the block to set
     * @param layer the block layer index
     * @param state the new {@link BlockState}
     */
    void setBlockState(int x, int y, int z, int layer, @NonNull BlockState state);

    /**
     * Sets the block state at the given coordinates in the default layer.
     *
     * @param x    the X coordinate of the block to set
     * @param y    the Y coordinate of the block to set
     * @param z    the Z coordinate of the block to set
     * @param id   the new {@link Identifier}
     * @param meta the new metadata
     * @throws IllegalArgumentException if the given metadata value was not registered for the given block {@link Identifier}
     */
    void setBlockState(int x, int y, int z, @NonNull Identifier id, int meta);

    /**
     * Sets the block state at the given coordinates in the given layer.
     *
     * @param x     the X coordinate of the block to set
     * @param y     the Y coordinate of the block to set
     * @param z     the Z coordinate of the block to set
     * @param layer the block layer index
     * @param id    the new {@link Identifier}
     * @param meta  the new metadata
     * @throws IllegalArgumentException if the given metadata value was not registered for the given block {@link Identifier}
     */
    void setBlockState(int x, int y, int z, int layer, @NonNull Identifier id, int meta);

    /**
     * Sets the block state at the given coordinates in the default layer.
     *
     * @param x        the X coordinate of the block to set
     * @param y        the Y coordinate of the block to set
     * @param z        the Z coordinate of the block to set
     * @param legacyId the new legacy ID
     * @param meta     the new metadata
     * @throws IllegalArgumentException if the given metadata value was not registered for the given legacy block ID
     */
    void setBlockState(int x, int y, int z, int legacyId, int meta);

    /**
     * Sets the block state at the given coordinates in the given layer.
     *
     * @param x        the X coordinate of the block to set
     * @param y        the Y coordinate of the block to set
     * @param z        the Z coordinate of the block to set
     * @param layer    the block layer index
     * @param legacyId the new legacy ID
     * @param meta     the new metadata
     * @throws IllegalArgumentException if the given metadata value was not registered for the given legacy block ID
     */
    void setBlockState(int x, int y, int z, int layer, int legacyId, int meta);

    /**
     * Sets the {@link Identifier} of the block at the given coordinates in the default layer.
     * <p>
     * The metadata value will be automatically set to the default (normally {@code 0}).
     *
     * @param x  the X coordinate of the block to set
     * @param y  the Y coordinate of the block to set
     * @param z  the Z coordinate of the block to set
     * @param id the new {@link Identifier}
     */
    void setBlockId(int x, int y, int z, @NonNull Identifier id);

    /**
     * Sets the {@link Identifier} of the block at the given coordinates in the given layer.
     * <p>
     * The metadata value will be automatically set to the default (normally {@code 0}).
     *
     * @param x     the X coordinate of the block to set
     * @param y     the Y coordinate of the block to set
     * @param z     the Z coordinate of the block to set
     * @param layer the block layer index
     * @param id    the new {@link Identifier}
     */
    void setBlockId(int x, int y, int z, int layer, @NonNull Identifier id);

    /**
     * Sets the legacy ID of the block at the given coordinates in the default layer.
     * <p>
     * The metadata value will be automatically set to the default (normally {@code 0}).
     *
     * @param x        the X coordinate of the block to set
     * @param y        the Y coordinate of the block to set
     * @param z        the Z coordinate of the block to set
     * @param legacyId the new legacy ID
     */
    void setBlockLegacyId(int x, int y, int z, int legacyId);

    /**
     * Sets the legacy ID of the block at the given coordinates in the given layer.
     * <p>
     * The metadata value will be automatically set to the default (normally {@code 0}).
     *
     * @param x        the X coordinate of the block to set
     * @param y        the Y coordinate of the block to set
     * @param z        the Z coordinate of the block to set
     * @param layer    the block layer index
     * @param legacyId the new legacy ID
     */
    void setBlockLegacyId(int x, int y, int z, int layer, int legacyId);

    /**
     * Sets the metadata of the block at the given coordinates in the default layer.
     * <p>
     * The block ID will remain unaffected by this change.
     *
     * @param x    the X coordinate of the block to set
     * @param y    the Y coordinate of the block to set
     * @param z    the Z coordinate of the block to set
     * @param meta the new metadata
     * @throws IllegalArgumentException if the given metadata value was not registered for the block at the given coordinates
     */
    void setBlockMeta(int x, int y, int z, int meta);

    /**
     * Sets the legacy ID of the block at the given coordinates in the given layer.
     * <p>
     * The block ID will remain unaffected by this change.
     *
     * @param x     the X coordinate of the block to set
     * @param y     the Y coordinate of the block to set
     * @param z     the Z coordinate of the block to set
     * @param layer the block layer index
     * @param meta  the new metadata
     * @throws IllegalArgumentException if the given metadata value was not registered for the block at the given coordinates
     */
    void setBlockMeta(int x, int y, int z, int layer, int meta);

    /**
     * Sets the runtime ID of the block at the given coordinates in the default layer.
     *
     * @param x         the X coordinate of the block to set
     * @param y         the Y coordinate of the block to set
     * @param z         the Z coordinate of the block to set
     * @param runtimeId the new runtime ID
     */
    void setBlockRuntimeId(int x, int y, int z, int runtimeId);

    /**
     * Sets the runtime ID of the block at the given coordinates in the given layer.
     *
     * @param x         the X coordinate of the block to set
     * @param y         the Y coordinate of the block to set
     * @param z         the Z coordinate of the block to set
     * @param layer     the block layer index
     * @param runtimeId the new runtime ID
     */
    void setBlockRuntimeId(int x, int y, int z, int layer, int runtimeId);

    /**
     * Sets the block light level at the given coordinates.
     *
     * @param x     the X coordinate to set the light level at
     * @param y     the Y coordinate to set the light level at
     * @param z     the Z coordinate to set the light level at
     * @param level the new block light level
     */
    void setBlockLight(int x, int y, int z, int level);

    /**
     * Sets the sky light level at the given coordinates.
     *
     * @param x     the X coordinate to set the light level at
     * @param y     the Y coordinate to set the light level at
     * @param z     the Z coordinate to set the light level at
     * @param level the new sky light level
     */
    void setSkyLight(int x, int y, int z, int level);
}
