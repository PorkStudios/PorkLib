/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.nbt.tag.notch;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.nbt.NBTInputStream;
import net.daporkchop.lib.nbt.NBTOutputStream;
import net.daporkchop.lib.nbt.alloc.NBTArrayHandle;
import net.daporkchop.lib.nbt.tag.Tag;
import net.daporkchop.lib.nbt.tag.TagRegistry;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.io.IOException;

/**
 * A tag that contains a single {@code int[]}.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class IntArrayTag extends Tag {
    protected int[] value;

    protected NBTArrayHandle<int[]> handle;

    public IntArrayTag(String name) {
        super(name);
    }

    public IntArrayTag(String name, @NonNull int[] value) {
        super(name);
        this.value = value;
    }

    @Override
    public synchronized void read(NBTInputStream in, TagRegistry registry) throws IOException {
        final int length = in.readInt();
        this.value(in.alloc().intArray(length));
        final int[] value = this.value;
        for (int i = 0; i < length; i++) {
            value[i] = in.readInt();
        }
    }

    @Override
    public void write(NBTOutputStream out, TagRegistry registry) throws IOException {
        final int[] value = this.value;
        final int length = value.length;
        out.writeInt(length);
        for (int i = 0; i < length; i++) {
            out.writeInt(value[i]);
        }
    }

    @Override
    public synchronized void release() throws AlreadyReleasedException {
        if (this.value != null) {
            if (this.handle != null) {
                this.handle.release();
                this.handle = null;
            }
            this.value = null;
        } else {
            throw new AlreadyReleasedException();
        }
    }

    @Override
    public String toString() {
        return String.format("IntArrayTag(\"%s\"): %d ints", this.getName(), this.value.length);
    }

    public synchronized void value(@NonNull int[] value)    {
        this.value = value;
        this.handle = null;
    }

    public synchronized void value(@NonNull NBTArrayHandle<int[]> handle)   {
        this.value = handle.value();
        this.handle = handle;
    }
}
