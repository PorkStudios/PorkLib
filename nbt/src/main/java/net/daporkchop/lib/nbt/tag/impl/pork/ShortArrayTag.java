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

package net.daporkchop.lib.nbt.tag.impl.pork;

import net.daporkchop.lib.nbt.TagType;
import net.daporkchop.lib.nbt.stream.NBTInputStream;
import net.daporkchop.lib.nbt.stream.NBTOutputStream;
import net.daporkchop.lib.nbt.tag.Tag;

import java.io.IOException;

public class ShortArrayTag extends Tag<short[]> {
    public ShortArrayTag(String name) {
        super(name);
    }

    public ShortArrayTag(String name, short[] value) {
        super(name, value);
    }

    @Override
    public void write(NBTOutputStream dos) throws IOException {
        short[] data = this.getValue();
        if (data == null) {
            dos.writeInt(0);
            return;
        }
        dos.writeInt(data.length);
        for (short s : data) {
            dos.writeByte((byte) s);
            dos.writeByte((byte) ((s >> 8) & 0xFF));
        }
    }

    @Override
    public void load(NBTInputStream dis) throws IOException {
        short[] data = new short[dis.readInt()];
        for (int i = 0; i < data.length; i++) {
            byte b1 = dis.readByte();
            byte b2 = dis.readByte();
            data[i] = (short) ((b2 << 8) + b1 & 0xFF);
        }
        this.setValue(data);
    }

    @Override
    public String toString() {
        return "ShortArrayTag " + this.getName() + " (size=" + this.getValue().length + ')';
    }

    @Override
    public TagType getType() {
        return TagType.TAG_SHORT_ARRAY;
    }

    @Override
    public Tag copy() {
        short[] data = this.getValue();
        short[] cp = new short[data.length];
        System.arraycopy(data, 0, cp, 0, data.length);
        return new ShortArrayTag(this.getName(), cp);
    }
}