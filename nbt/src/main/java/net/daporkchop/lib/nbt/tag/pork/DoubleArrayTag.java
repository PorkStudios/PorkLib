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

package net.daporkchop.lib.nbt.tag.pork;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.nbt.tag.Tag;
import net.daporkchop.lib.nbt.tag.TagRegistry;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
@Getter
@Setter
public class DoubleArrayTag extends Tag {
    @NonNull
    private double[] value;

    public DoubleArrayTag(String name) {
        super(name);
    }

    public DoubleArrayTag(String name, @NonNull double[] value) {
        super(name);
        this.value = value;
    }

    @Override
    public void read(@NonNull DataIn in, @NonNull TagRegistry registry) throws IOException {
        int len = in.readInt();
        this.value = new double[len];
        for (int i = 0; i < len; i++)   {
            this.value[i] = in.readDouble();
        }
    }

    @Override
    public void write(@NonNull DataOut out, @NonNull TagRegistry registry) throws IOException {
        out.writeInt(this.value.length);
        for (int i = 0; i < this.value.length; i++) {
            out.writeDouble(this.value[i]);
        }
    }

    @Override
    public String toString() {
        return String.format("DoubleArrayTag(\"%s\"): %d double", this.getName(), this.value.length);
    }
}
