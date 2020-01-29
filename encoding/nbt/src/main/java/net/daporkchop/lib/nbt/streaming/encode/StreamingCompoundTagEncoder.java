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

package net.daporkchop.lib.nbt.streaming.encode;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.nbt.tag.Tag;
import net.daporkchop.lib.nbt.tag.TagRegistry;

/**
 * @author DaPorkchop_
 */
public final class StreamingCompoundTagEncoder extends StreamingNBTEncoder {
    public StreamingCompoundTagEncoder(@NonNull ByteBuf out) {
        super(out);

        this.writeInitial("");
    }

    public StreamingCompoundTagEncoder(@NonNull ByteBuf out, @NonNull TagRegistry registry) {
        super(out, registry);

        this.writeInitial("");
    }

    public StreamingCompoundTagEncoder(@NonNull ByteBuf out, @NonNull CharSequence name) {
        super(out);

        this.writeInitial(name);
    }

    public StreamingCompoundTagEncoder(@NonNull ByteBuf out, @NonNull TagRegistry registry, @NonNull CharSequence name) {
        super(out, registry);

        this.writeInitial(name);
    }

    protected StreamingCompoundTagEncoder(@NonNull StreamingNBTEncoder parent) {
        super(parent);
    }

    private void writeInitial(@NonNull CharSequence name) {
        this.out.writeByte(this.compoundTagId);
        this.appendText(name);
    }

    @Override
    public void close() {
        this.out.writeByte(0); //close compound tag
    }

    public void appendBoolean(@NonNull CharSequence name, boolean value) {
        this.appendByte(name, value ? (byte) 1 : 0);
    }

    public void appendByte(@NonNull CharSequence name, byte value) {
        this.out.writeByte(this.byteTagId);
        this.appendText(name);
        this.out.writeByte(value);
    }

    public void appendShort(@NonNull CharSequence name, short value) {
        this.out.writeByte(this.shortTagId);
        this.appendText(name);
        this.out.writeShort(value);
    }

    public void appendInt(@NonNull CharSequence name, int value) {
        this.out.writeByte(this.intTagId);
        this.appendText(name);
        this.out.writeInt(value);
    }

    public void appendLong(@NonNull CharSequence name, long value) {
        this.out.writeByte(this.longTagId);
        this.appendText(name);
        this.out.writeLong(value);
    }

    public void appendFloat(@NonNull CharSequence name, float value) {
        this.out.writeByte(this.floatTagId);
        this.appendText(name);
        this.out.writeFloat(value);
    }

    public void appendDouble(@NonNull CharSequence name, double value) {
        this.out.writeByte(this.doubleTagId);
        this.appendText(name);
        this.out.writeDouble(value);
    }

    public void appendByteArray(@NonNull CharSequence name, @NonNull byte[] value) {
        this.out.writeByte(this.byteArrayTagId);
        this.appendText(name);
        this.out.writeInt(value.length).writeBytes(value);
    }

    public void appendString(@NonNull CharSequence name, @NonNull CharSequence value) {
        this.out.writeByte(this.doubleTagId);
        this.appendText(name);
        this.appendText(value);
    }

    public StreamingCompoundTagEncoder pushCompound(@NonNull CharSequence name) {
        this.out.writeByte(this.compoundTagId);
        this.appendText(name);
        return new StreamingCompoundTagEncoder(this);
    }

    public StreamingListTagEncoder pushList(@NonNull CharSequence name, @NonNull Class<? extends Tag> tagClass) {
        this.out.writeByte(this.listTagId);
        this.appendText(name);
        return new StreamingListTagEncoder(this, tagClass);
    }

    public void appendIntArray(@NonNull CharSequence name, @NonNull int[] value) {
        this.out.writeByte(this.intArrayTagId);
        this.appendText(name);
        this.out.writeInt(value.length);
        for (int i : value) {
            this.out.writeInt(i);
        }
    }

    public void appendLongArray(@NonNull CharSequence name, @NonNull long[] value) {
        this.out.writeByte(this.longArrayTagId);
        this.appendText(name);
        this.out.writeInt(value.length);
        for (long l : value) {
            this.out.writeLong(l);
        }
    }
}
