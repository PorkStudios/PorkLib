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

public class DoubleArrayTag extends Tag<double[]> {
    public DoubleArrayTag(String name) {
        super(name);
    }

    public DoubleArrayTag(String name, double[] value) {
        super(name, value);
    }

    @Override
    public void write(NBTOutputStream dos) throws IOException {
        double[] data = getValue();
        if (data == null) {
            dos.writeInt(0);
            return;
        }
        dos.writeInt(data.length);
        for (int i = 0; i < data.length; i++) {
            dos.writeDouble(data[i]);
        }
    }

    @Override
    public void load(NBTInputStream dis) throws IOException {
        double[] data = new double[dis.readInt()];
        for (int i = 0; i < data.length; i++) {
            data[i] = dis.readDouble();
        }
        this.setValue(data);
    }

    @Override
    public String toString() {
        return "DoubleArrayTag " + this.getName() + " (size=" + getValue().length + ")";
    }

    @Override
    public TagType getType() {
        return TagType.TAG_DOUBLE_ARRAY;
    }

    @Override
    public Tag copy() {
        double[] data = this.getValue();
        double[] cp = new double[data.length];
        System.arraycopy(data, 0, cp, 0, data.length);
        return new DoubleArrayTag(getName(), cp);
    }
}
