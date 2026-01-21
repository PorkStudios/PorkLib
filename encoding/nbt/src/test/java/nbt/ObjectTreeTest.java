/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
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

package nbt;

import io.netty.buffer.Unpooled;
import net.daporkchop.lib.binary.oio.StreamUtil;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.nbt.NBTFormat;
import net.daporkchop.lib.nbt.stream.encode.NBTEncoder;
import net.daporkchop.lib.nbt.tag.CompoundTag;
import net.daporkchop.lib.nbt.tag.ListTag;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
public class ObjectTreeTest {
    @Test
    public void testHelloWorld() throws IOException {
        byte[] arr;
        try (InputStream in = ObjectTreeTest.class.getResourceAsStream("/hello_world.nbt")) {
            arr = StreamUtil.toByteArray(in);
        }
        CompoundTag tag = NBTFormat.BIG_ENDIAN.readCompound(DataIn.wrapView(Unpooled.wrappedBuffer(arr)));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        NBTFormat.BIG_ENDIAN.writeCompound(DataOut.wrap(baos), tag);
        checkState(Arrays.equals(arr, baos.toByteArray()));
        CompoundTag tag2 = NBTFormat.BIG_ENDIAN.readCompound(DataIn.wrapView(Unpooled.wrappedBuffer(baos.toByteArray())));
        checkState(tag.equals(tag2));
    }

    @Test
    public void testBigEndian() throws IOException {
        byte[] arr;
        try (InputStream in = new GZIPInputStream(ObjectTreeTest.class.getResourceAsStream("/bigtest.nbt"))) {
            arr = StreamUtil.toByteArray(in);
        }
        CompoundTag tag = NBTFormat.BIG_ENDIAN.readCompound(DataIn.wrapView(Unpooled.wrappedBuffer(arr)));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        NBTFormat.BIG_ENDIAN.writeCompound(DataOut.wrap(baos), tag);
        checkState(Arrays.equals(arr, baos.toByteArray()));
        CompoundTag tag2 = NBTFormat.BIG_ENDIAN.readCompound(DataIn.wrapView(Unpooled.wrappedBuffer(baos.toByteArray())));
        checkState(tag.equals(tag2));
    }

    @Test
    public void testLittleEndian() throws IOException {
        byte[] arr;
        try (InputStream in = ObjectTreeTest.class.getResourceAsStream("/runtime_block_states.dat")) {
            arr = StreamUtil.toByteArray(in);
        }
        ListTag<CompoundTag> tag = NBTFormat.LITTLE_ENDIAN.readList(DataIn.wrapView(Unpooled.wrappedBuffer(arr)));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        NBTFormat.LITTLE_ENDIAN.writeList(DataOut.wrap(baos), tag);
        checkState(Arrays.equals(arr, baos.toByteArray()));
        ListTag<CompoundTag> tag2 = NBTFormat.LITTLE_ENDIAN.readList(DataIn.wrapView(Unpooled.wrappedBuffer(baos.toByteArray())));
        checkState(tag.equals(tag2));
    }

    @Test
    public void testVarInt() throws IOException {
        byte[] arr;
        try (InputStream in = ObjectTreeTest.class.getResourceAsStream("/biome_definitions.dat")) {
            arr = StreamUtil.toByteArray(in);
        }
        CompoundTag tag = NBTFormat.VARINT.readCompound(DataIn.wrapView(Unpooled.wrappedBuffer(arr)));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        NBTFormat.VARINT.writeCompound(DataOut.wrap(baos), tag);
        checkState(Arrays.equals(arr, baos.toByteArray()));
        CompoundTag tag2 = NBTFormat.VARINT.readCompound(DataIn.wrapView(Unpooled.wrappedBuffer(baos.toByteArray())));
        checkState(tag.equals(tag2));
    }

    @Test
    public void testEncode() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (NBTEncoder encoder = NBTFormat.VARINT.encodeCompound(DataOut.wrap(baos), "Level")) {
            encoder.putByte("byte", 0)
                    .putString("text1", "Hello World!");
            encoder.startString("text2").appendString("asdf").close();

            encoder.startListString("list_string", 3)
                    .appendString("a").appendString("b").appendString("c").close();

            encoder.startByteArray("byte_array", 3)
                    .appendByte(1).appendBytes(new byte[2]).close();

            encoder.startListCompound("compounds", 2)
                    .appendCompound().closeTag()
                    .appendCompound().closeTag()
                    .close();

            encoder.startListList("lists", 2)
                    .appendListCompound(1).appendCompound().putString("key", "value").closeTag().closeTag()
                    .appendListString(2).appendString("Hello").appendString("World!").closeTag()
                    .close();

            byte[] arr;
            try (InputStream in = ObjectTreeTest.class.getResourceAsStream("/hello_world.nbt")) {
                arr = StreamUtil.toByteArray(in);
            }
            CompoundTag tag = NBTFormat.BIG_ENDIAN.readCompound(DataIn.wrapView(Unpooled.wrappedBuffer(arr)));

            encoder.putTag(tag.name(), tag)
                    .startListCompound("hello worlds", 2)
                    .appendTag(tag).appendTag(tag).close();
        }

        System.out.println(NBTFormat.VARINT.readCompound(DataIn.wrapView(Unpooled.wrappedBuffer(baos.toByteArray()))));
    }
}
