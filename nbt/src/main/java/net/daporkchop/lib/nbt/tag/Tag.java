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

package net.daporkchop.lib.nbt.tag;

import net.daporkchop.lib.nbt.TagType;
import net.daporkchop.lib.nbt.stream.NBTInputStream;
import net.daporkchop.lib.nbt.stream.NBTOutputStream;
import net.daporkchop.lib.nbt.tag.impl.notch.EndTag;

import java.io.IOException;

public abstract class Tag<T> {

    private String name;
    private T value;

    protected Tag(String name) {
        this(name, null);
    }

    protected Tag(String name, T value) {
        if (name == null) {
            this.name = "";
        } else {
            this.name = name;
        }
        this.setValue(value);
    }

    public static Tag readNamedTag(NBTInputStream dis) throws IOException {
        byte type = dis.readByte();
        if (type == 0) return new EndTag();

        String name = dis.readUTF();

        Tag tag = TagType.getFromId(type).createInstance(name);
        tag.load(dis);

        return tag;
    }

    public static void writeNamedTag(Tag tag, NBTOutputStream dos) throws IOException {
        dos.writeByte(tag.getType().getId());
        if (tag.getType() == TagType.TAG_END) return;
        dos.writeUTF(tag.getName());

        tag.write(dos);
    }

    public abstract void write(NBTOutputStream dos) throws IOException;

    public abstract void load(NBTInputStream dis) throws IOException;

    public abstract String toString();

    public abstract TagType getType();

    public String getName() {
        if (this.name == null) return "";
        return this.name;
    }

    public Tag setName(String name) {
        if (name == null) {
            this.name = "";
        } else {
            this.name = name;
        }
        return this;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public abstract Tag copy();
}
