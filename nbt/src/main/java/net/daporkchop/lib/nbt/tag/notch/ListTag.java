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
 */

package net.daporkchop.lib.nbt.tag.notch;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.nbt.tag.Tag;
import net.daporkchop.lib.nbt.tag.TagRegistry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
@Getter
@Setter
public class ListTag<T extends Tag> extends Tag {
    @NonNull
    private List<T> value;

    public ListTag(String name) {
        super(name);
    }

    public ListTag(String name, @NonNull List<T> value) {
        super(name);
        this.value = value;
    }

    @Override
    public void read(@NonNull DataIn in, @NonNull TagRegistry registry) throws IOException {
        this.value = new ArrayList<>();
        byte type = in.readByte();
        int len = in.readInt();
        for (int i = 0; i < len; i++) {
            T tag = registry.create(type, null);
            tag.read(in, registry);
            this.value.add(tag);
        }
    }

    @Override
    public void write(@NonNull DataOut out, @NonNull TagRegistry registry) throws IOException {
        if (this.value.isEmpty()) {
            out.writeByte((byte) 0);
            out.writeInt(0);
        } else {
            byte id = registry.getId(this.value.get(0).getClass());
            out.writeByte(id);
            out.writeInt(this.value.size());
            for (T tag : this.value) {
                tag.write(out, registry);
            }
        }
    }

    @Override
    public String toString() {
        return String.format("ListTag(\"%s\"): %d tags", this.getName(), this.value.size());
    }

    public void forEach(@NonNull Consumer<T> consumer) {
        this.value.forEach(consumer);
    }
}
