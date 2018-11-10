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

import lombok.Getter;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.db.object.serializer.ValueSerializer;
import net.daporkchop.lib.nbt.NBTIO;
import net.daporkchop.lib.nbt.tag.impl.notch.CompoundTag;

import java.io.IOException;

/**
 * Reads and writes an NBT compound tag
 *
 * @author DaPorkchop_
 */
public class NBTSerializer extends ValueSerializer<CompoundTag> {
    @Getter
    private static final ValueSerializer<CompoundTag> instance = new NBTSerializer();

    private NBTSerializer() {
    }

    @Override
    public void write(CompoundTag value, DataOut out) throws IOException {
        byte[] b = NBTIO.write(value);
        out.writeBytesSimple(b);
    }

    @Override
    public CompoundTag read(DataIn in) throws IOException {
        return NBTIO.read(in.readBytesSimple());
    }
}