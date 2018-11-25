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

package net.daporkchop.lib.db.object.serializer.impl.nbt;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.db.object.serializer.ValueSerializer;
import net.daporkchop.lib.nbt.NBTIO;
import net.daporkchop.lib.nbt.tag.notch.CompoundTag;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Reads and writes an object to and from an NBT compound tag
 *
 * @author DaPorkchop_
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NBTObjectSerializer<T> extends ValueSerializer<T> {
    @NonNull
    private final BiConsumer<T, CompoundTag> writeFunction;

    @NonNull
    private final Function<CompoundTag, T> readFunction;

    @Override
    public void write(T value, DataOut out) throws IOException {
        CompoundTag tag = new CompoundTag();
        this.writeFunction.accept(value, tag);
        NBTIO.write(out, tag);
    }

    @Override
    public T read(DataIn in) throws IOException {
        CompoundTag tag = NBTIO.read(in);
        return this.readFunction.apply(tag);
    }
}
