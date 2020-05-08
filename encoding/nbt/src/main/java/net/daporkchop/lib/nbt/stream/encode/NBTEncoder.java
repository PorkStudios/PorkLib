/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.nbt.stream.encode;

import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataOut;

import java.io.IOException;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.nbt.tag.Tag.*;

/**
 * @author DaPorkchop_
 */
public final class NBTEncoder implements AutoCloseable {
    public static NBTEncoder beginCompound(@NonNull DataOut out, @NonNull String name) throws IOException {
        out.writeByte(TAG_COMPOUND);
        out.writeUTF(name);
        return new NBTEncoder(out, EncodeContext.create(null, TAG_COMPOUND, 0, 0));
    }

    protected final DataOut out;

    protected EncodeContext context;

    private NBTEncoder(@NonNull DataOut out, @NonNull EncodeContext context) {
        this.out = out;
        this.context = context;
    }

    public NBTEncoder putString(@NonNull String name, @NonNull String value) throws IOException  {
        checkState(this.context.id == TAG_COMPOUND, "Cannot append named tag to a non-compound tag!");
        this.out.writeByte(TAG_STRING);
        this.out.writeUTF(name);
        this.out.writeUTF(value);
        return this;
    }

    public NBTEncoder putString(@NonNull String value) throws IOException  {
        checkState(this.context.component == TAG_STRING, "Current tag cannot accept a string value!");
        checkState(this.context.length > 0, "Current tag cannot accept any more values!");
        this.out.writeUTF(value);
        this.context.length--;
        return this;
    }

    public NBTEncoder startString(@NonNull String name) throws IOException  {
        checkState(this.context.id == TAG_COMPOUND, "Cannot append named tag to a non-compound tag!");
        this.out.writeByte(TAG_STRING);
        this.out.writeUTF(name);
        this.context = EncodeContext.create(this.context, TAG_STRING, 1);
        return this;
    }

    public NBTEncoder startCompound(@NonNull String name) throws IOException {
        checkState(this.context.id == TAG_COMPOUND, "Cannot append named tag to a non-compound tag!");
        this.out.writeByte(TAG_COMPOUND);
        this.out.writeUTF(name);
        this.context = EncodeContext.create(this.context, TAG_COMPOUND, 0, 0);
        return this;
    }

    @Override
    public void close() throws IOException {
        EncodeContext context = this.context;
        int id = context.id;
        if (id == TAG_COMPOUND) {
            this.out.writeByte(TAG_END);
        } else {
            int remaining = context.length;
            checkState(remaining == 0, "Current component (id: %d) expected %d more values!", id, remaining);
        }

        if ((this.context = context.finish()) == null) {
            //root tag was finished!
            this.out.close();
        }
    }
}
