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

import lombok.NonNull;
import net.daporkchop.lib.binary.oio.StreamUtil;
import net.daporkchop.lib.nbt.NBTInputStream;
import net.daporkchop.lib.nbt.NBTOutputStream;
import net.daporkchop.lib.nbt.tag.Tag;
import net.daporkchop.lib.nbt.tag.notch.CompoundTag;
import net.daporkchop.lib.nbt.tag.notch.ListTag;
import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * @author DaPorkchop_
 */
public class NBTTest {
    public static void printTagRecursive(@NonNull Tag tag, int depth) {
        if (depth == 0) {
            System.out.printf("CompoundTag \"%s\": %d children\n", tag.getName(), tag.getAsCompoundTag().getContents().size());
            tag.getAsCompoundTag().forEachTag(subTag -> printTagRecursive(subTag, 2));
            return;
        }
        System.out.printf("%s%s\n", space(depth), tag);
        if (tag instanceof CompoundTag) {
            tag.getAsCompoundTag().forEachTag(subTag -> printTagRecursive(subTag, depth + 2));
        } else if (tag instanceof ListTag) {
            tag.<ListTag<? extends Tag>>getAs().forEach(subTag -> printTagRecursive(subTag, depth + 2));
        }
    }

    public static String space(int count) {
        char[] c = new char[count];
        for (int i = count - 1; i >= 0; i--) {
            c[i] = ' ';
        }
        return new String(c);
    }

    @Test
    public void testWriting() throws IOException {
        byte[] original_uncompressed;
        try (InputStream is = new GZIPInputStream(NBTTest.class.getResourceAsStream("bigtest.nbt"))) {
            original_uncompressed = StreamUtil.toByteArray(is);
        }
        CompoundTag tag;
        try (NBTInputStream in = new NBTInputStream(new ByteArrayInputStream(original_uncompressed))) {
            tag = in.readTag();
        }
        byte[] written;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             NBTOutputStream out = new NBTOutputStream(baos)) {
            out.writeTag(tag);
            out.flush();
            written = baos.toByteArray();
        }
        try (NBTInputStream in = new NBTInputStream(new ByteArrayInputStream(written))) {
            tag = in.readTag();
        }
        printTagRecursive(tag, 0);
        File file = new File("test_out/written.nbt");
        if (!file.exists()) {
            File parent = file.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                throw new IllegalStateException(String.format("Couldn't create directory: %s", parent.getAbsolutePath()));
            } else if (!file.createNewFile()) {
                throw new IllegalStateException(String.format("Couldn't create file: %s", file.getAbsolutePath()));
            }
        }
        try (NBTOutputStream out = new NBTOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            out.writeTag(tag);
        }
    }

    @Test
    public void testHelloWorld() throws IOException {
        try (NBTInputStream in = new NBTInputStream(NBTTest.class.getResourceAsStream("hello_world.nbt"))) {
            CompoundTag tag = in.readTag();
            printTagRecursive(tag, 0);
        }
    }

    @Test
    public void testBig() throws IOException {
        try (NBTInputStream in = new NBTInputStream(new GZIPInputStream(NBTTest.class.getResourceAsStream("bigtest.nbt")))) {
            CompoundTag tag = in.readTag();
            printTagRecursive(tag, 0);
        }
    }
}
