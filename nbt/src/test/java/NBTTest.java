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

import lombok.NonNull;
import net.daporkchop.lib.nbt.NBTInputStream;
import net.daporkchop.lib.nbt.tag.notch.CompoundTag;
import net.daporkchop.lib.nbt.tag.notch.StringTag;
import org.junit.Test;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
public class NBTTest {
    @Test
    public void testHelloWorld() throws IOException {
        try (NBTInputStream in = new NBTInputStream(NBTTest.class.getResourceAsStream("hello_world.nbt")))  {
            CompoundTag tag = in.readTag();
            this.printTagRecursive(tag, 0);
        }
    }

    public void printTagRecursive(@NonNull CompoundTag tag, int depth) {
        if (depth == 0) {
            System.out.printf("CompoundTag \"%s\": %d children\n", tag.getName(), tag.getContents().size());
            this.printTagRecursive(tag, 2);
            return;
        }
        tag.forEach((name, subTag) -> {
            System.out.print(this.space(depth));
            if (subTag instanceof CompoundTag)  {
                CompoundTag compoundTag = subTag.getAsCompoundTag();
                System.out.printf("CompoundTag \"%s\": %d children\n", name, compoundTag.getContents().size());
                this.printTagRecursive(compoundTag, depth + 2);
            } else if (subTag instanceof StringTag) {
                System.out.printf("StringTag \"%s\": \"%s\"\n", name, subTag.<StringTag>getAs().getValue());
            }
        });
    }

    public String space(int count)  {
        char[] c = new char[count];
        for (int i = count - 1; i >= 0; i--)    {
            c[i] = ' ';
        }
        return new String(c);
    }
}
