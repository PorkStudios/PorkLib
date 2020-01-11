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
import net.daporkchop.lib.nbt.NBTOutputStream;
import net.daporkchop.lib.nbt.tag.TagRegistry;
import net.daporkchop.lib.nbt.tag.notch.ByteArrayTag;
import net.daporkchop.lib.nbt.tag.notch.ByteTag;
import net.daporkchop.lib.nbt.tag.notch.CompoundTag;
import net.daporkchop.lib.nbt.tag.notch.DoubleTag;
import net.daporkchop.lib.nbt.tag.notch.FloatTag;
import net.daporkchop.lib.nbt.tag.notch.IntArrayTag;
import net.daporkchop.lib.nbt.tag.notch.IntTag;
import net.daporkchop.lib.nbt.tag.notch.ListTag;
import net.daporkchop.lib.nbt.tag.notch.LongArrayTag;
import net.daporkchop.lib.nbt.tag.notch.LongTag;
import net.daporkchop.lib.nbt.tag.notch.ShortTag;
import net.daporkchop.lib.nbt.tag.notch.StringTag;

import java.nio.charset.StandardCharsets;

/**
 * An alternative to {@link NBTOutputStream} for encoding NBT data. This allows writing individual tags without ever having to create any tag instances, but
 * be aware that it can also allow writing broken NBT (e.g. multiple children in a compound tag with the same name).
 * <p>
 * Use at your own risk.
 *
 * @author DaPorkchop_
 * @see StreamingCompoundTagEncoder
 */
public abstract class StreamingNBTEncoder implements AutoCloseable {
    protected final ByteBuf out;
    protected final TagRegistry registry;

    protected final int byteTagId;
    protected final int shortTagId;
    protected final int intTagId;
    protected final int longTagId;
    protected final int floatTagId;
    protected final int doubleTagId;
    protected final int byteArrayTagId;
    protected final int stringTagId;
    protected final int listTagId;
    protected final int compoundTagId;
    protected final int intArrayTagId;
    protected final int longArrayTagId;

    public StreamingNBTEncoder(@NonNull ByteBuf out) {
        this.out = out;
        this.registry = TagRegistry.NOTCHIAN;

        //optimization for notchian tags
        this.byteTagId = 1;
        this.shortTagId = 2;
        this.intTagId = 3;
        this.longTagId = 4;
        this.floatTagId = 5;
        this.doubleTagId = 6;
        this.byteArrayTagId = 7;
        this.stringTagId = 8;
        this.listTagId = 9;
        this.compoundTagId = 10;
        this.intArrayTagId = 11;
        this.longArrayTagId = 12;
    }

    public StreamingNBTEncoder(@NonNull ByteBuf out, @NonNull TagRegistry registry) {
        this.out = out;
        this.registry = registry;

        this.byteTagId = registry.getId(ByteTag.class);
        this.shortTagId = registry.getId(ShortTag.class);
        this.intTagId = registry.getId(IntTag.class);
        this.longTagId = registry.getId(LongTag.class);
        this.floatTagId = registry.getId(FloatTag.class);
        this.doubleTagId = registry.getId(DoubleTag.class);
        this.byteArrayTagId = registry.getId(ByteArrayTag.class);
        this.stringTagId = registry.getId(StringTag.class);
        this.listTagId = registry.getId(ListTag.class);
        this.compoundTagId = registry.getId(CompoundTag.class);
        this.intArrayTagId = registry.getId(IntArrayTag.class);
        this.longArrayTagId = registry.getId(LongArrayTag.class);
    }
    
    protected StreamingNBTEncoder(@NonNull StreamingNBTEncoder parent)  {
        this.out = parent.out;
        this.registry = parent.registry;

        this.byteTagId = parent.byteTagId;
        this.shortTagId = parent.shortTagId;
        this.intTagId = parent.intTagId;
        this.longTagId = parent.longTagId;
        this.floatTagId = parent.floatTagId;
        this.doubleTagId = parent.doubleTagId;
        this.byteArrayTagId = parent.byteArrayTagId;
        this.stringTagId = parent.stringTagId;
        this.listTagId = parent.listTagId;
        this.compoundTagId = parent.compoundTagId;
        this.intArrayTagId = parent.intArrayTagId;
        this.longArrayTagId = parent.longArrayTagId;
    }

    @Override
    public abstract void close();

    protected void appendText(@NonNull CharSequence text) {
        int lengthIndex = this.out.writerIndex();
        int count = this.out.writeShort(-1).writeCharSequence(text, StandardCharsets.UTF_8);
        if (count >= 0 && count < 0x10000) {
            this.out.setShort(lengthIndex, count);
        } else {
            throw new IllegalStateException(String.valueOf(count));
        }
    }
}
