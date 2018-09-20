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

package net.daporkchop.lib.nbt.tag.impl.notch;

import net.daporkchop.lib.nbt.TagType;
import net.daporkchop.lib.nbt.stream.NBTInputStream;
import net.daporkchop.lib.nbt.stream.NBTOutputStream;
import net.daporkchop.lib.nbt.tag.Tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListTag<T extends Tag> extends Tag<List<T>> {
    public ListTag(String name) {
        super(name);
    }

    public ListTag(String name, List<T> value) {
        super(name, value);
    }

    @Override
    public void write(NBTOutputStream dos) throws IOException {
        List<T> list = this.getValue();
        dos.writeByte(list.size() > 0 ? list.get(0).getType().getId() : 1);
        dos.writeInt(list.size());
        for (T t : list) t.write(dos);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void load(NBTInputStream dis) throws IOException {
        TagType type = TagType.getFromId(dis.readByte());
        int size = dis.readInt();

        List<T> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Tag tag = type.createInstance(null);
            tag.load(dis);
            list.add((T) tag);
        }
        this.setValue(list);
    }

    @Override
    public List<T> getValue() {
        return super.getValue() == null ? new ArrayList<>() : super.getValue();
    }

    @Override
    public void setValue(List<T> value) {
        super.setValue(value == null ? new ArrayList<>() : value);
    }

    @Override
    public String toString() {
        return "ListTag " + this.getName() + " (Size=" + this.getValue().size() + ')';
    }

    @Override
    public TagType getType() {
        return TagType.TAG_LIST;
    }

    @Override
    public Tag copy() {
        return new ListTag<>(this.getName(), new ArrayList<>(this.getValue()));
    }
}
