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

package net.daporkchop.lib.nbt;

import net.daporkchop.lib.nbt.tag.Tag;
import net.daporkchop.lib.nbt.tag.impl.notch.ByteArrayTag;
import net.daporkchop.lib.nbt.tag.impl.notch.ByteTag;
import net.daporkchop.lib.nbt.tag.impl.notch.CompoundTag;
import net.daporkchop.lib.nbt.tag.impl.notch.DoubleTag;
import net.daporkchop.lib.nbt.tag.impl.notch.FloatTag;
import net.daporkchop.lib.nbt.tag.impl.notch.IntArrayTag;
import net.daporkchop.lib.nbt.tag.impl.notch.IntTag;
import net.daporkchop.lib.nbt.tag.impl.notch.ListTag;
import net.daporkchop.lib.nbt.tag.impl.notch.LongTag;
import net.daporkchop.lib.nbt.tag.impl.notch.ShortTag;
import net.daporkchop.lib.nbt.tag.impl.notch.StringTag;
import net.daporkchop.lib.nbt.tag.impl.pork.DoubleArrayTag;
import net.daporkchop.lib.nbt.tag.impl.pork.FloatArrayTag;
import net.daporkchop.lib.nbt.tag.impl.pork.LongArrayTag;
import net.daporkchop.lib.nbt.tag.impl.pork.ShortArrayTag;
import net.daporkchop.lib.nbt.tag.impl.pork.StringArrayTag;
import net.daporkchop.lib.nbt.tag.impl.pork.object.ObjectTag;

public enum TagType {
    //Official (Notchian) tags
    TAG_END(null),
    TAG_BYTE(ByteTag::new),
    TAG_SHORT(ShortTag::new),
    TAG_INT(IntTag::new),
    TAG_LONG(LongTag::new),
    TAG_FLOAT(FloatTag::new),
    TAG_DOUBLE(DoubleTag::new),
    TAG_BYTE_ARRAY(ByteArrayTag::new),
    TAG_STRING(StringTag::new),
    TAG_LIST(ListTag::new),
    TAG_COMPOUND(CompoundTag::new),
    TAG_INT_ARRAY(IntArrayTag::new),
    //Custom (Porkian) tags
    TAG_SHORT_ARRAY(ShortArrayTag::new),
    TAG_LONG_ARRAY(LongArrayTag::new),
    TAG_FLOAT_ARRAY(FloatArrayTag::new),
    TAG_DOUBLE_ARRAY(DoubleArrayTag::new),
    TAG_STRING_ARRAY(StringArrayTag::new),
    TAG_OBJECT(ObjectTag::new);

    private final TagProvider provider;

    TagType(TagProvider provider) {
        this.provider = provider;
    }

    public static TagType getFromId(byte id) {
        if ((id & 0xFF) > values().length) throw new IllegalArgumentException("Invalid tag ID: " + id);
        return values()[id & 0xFF];
    }

    public byte getId() {
        return (byte) ordinal();
    }

    public Tag createInstance(String name) {
        return provider.provide(name);
    }
}

