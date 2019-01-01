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

package net.daporkchop.lib.nbt.tag;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.nbt.tag.notch.CompoundTag;
import net.daporkchop.lib.nbt.tag.notch.ListTag;

import java.io.IOException;

/**
 * Represents an NBT tag.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public abstract class Tag {
    /**
     * The name of this tag.
     * <p>
     * This will never be {@code null} unless this is an element of a {@link ListTag}
     */
    private final String name;

    /**
     * Gets and casts this tag to a specific tag type
     *
     * @param <T> the type to cast to
     * @return this tag casted to the given type
     */
    @SuppressWarnings("unchecked")
    public <T extends Tag> T getAs() {
        return (T) this;
    }

    /**
     * Gets this tag as a {@link CompoundTag}
     *
     * @return this tag as a {@link CompoundTag}
     */
    public CompoundTag getAsCompoundTag() {
        return this.getAs();
    }

    /**
     * Gets this tag as a {@link ListTag}
     *
     * @param <T> the type of tag contained in the list
     * @return this tag as a {@link ListTag}
     */
    public <T extends Tag> ListTag<T> getAsList() {
        return this.getAs();
    }

    /**
     * Reads this tag from a stream
     *
     * @param in       the input stream to read from
     * @param registry the registry of NBT tag ids
     * @throws IOException if an IO exception occurs you dummy
     */
    public abstract void read(@NonNull DataIn in, @NonNull TagRegistry registry) throws IOException;

    /**
     * Writes this tag to a stream
     *
     * @param out      the output stream to write to
     * @param registry the registry of NBT tag ids
     * @throws IOException if an IO exception occurs you dummy
     */
    public abstract void write(@NonNull DataOut out, @NonNull TagRegistry registry) throws IOException;

    @Override
    public abstract String toString();
}
