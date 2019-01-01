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

package net.daporkchop.lib.nbt.util;

import lombok.NonNull;
import net.daporkchop.lib.binary.data.Serializer;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.nbt.NBTIO;
import net.daporkchop.lib.nbt.tag.notch.CompoundTag;

import java.io.IOException;

/**
 * Defines a class that can load and save objects of type <V> to/from an NBT tag.
 *
 * @author DaPorkchop_
 */
public interface NBTSerializer<V> extends Serializer<V> {
    @Override
    default void write(@NonNull V val, @NonNull DataOut out) throws IOException {
        CompoundTag tag = new CompoundTag("");
        this.write(val, tag);
        NBTIO.write(out, tag);
    }

    @Override
    default V read(@NonNull DataIn in) throws IOException {
        CompoundTag tag = NBTIO.read(in);
        return this.read(tag);
    }

    void write(@NonNull V val, @NonNull CompoundTag tag);

    V read(@NonNull CompoundTag tag);
}
