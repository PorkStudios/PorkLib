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

package minecraft.worldscanner;import net.daporkchop.lib.minecraft.registry.IDRegistry;
import net.daporkchop.lib.minecraft.registry.ResourceLocation;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.MinecraftSave;
import net.daporkchop.lib.minecraft.world.format.anvil.AnvilSaveFormat;
import net.daporkchop.lib.minecraft.world.impl.SaveBuilder;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author DaPorkchop_
 */
public class AnvilTest {
    @Test
    public void test() throws IOException {
        MinecraftSave save = new SaveBuilder()
                .setFormat(new AnvilSaveFormat(new File(".", "run/testworld")))
                .build();

        System.out.printf("%d registries\n", save.registries().size());
        save.registries().forEach((resourceLocation, registry) -> System.out.printf("  %s: %d entries\n", resourceLocation, registry.size()));

        IDRegistry blockRegistry = save.registry(new ResourceLocation("minecraft:blocks"));

        Chunk chunk = save.world(0).column(30, 6);
        chunk.load();
        int id;
        for (int y = 255; y >= 0; y--) {
            if ((id = chunk.getBlockId(7, y, 7)) != 0) {
                System.out.printf("Surface in chunk (%d,%d) is at y=%d\n", chunk.getX(), chunk.getZ(), y);
                ResourceLocation surfaceBlock = blockRegistry.lookup(id);
                if (surfaceBlock.equals("tconstruct:blueslime"))    {
                    System.out.printf("Surface block id id %d (registry name: %s)\n", id, surfaceBlock);
                } else {
                    throw new IllegalStateException(String.format("Expected block type \"tconstruct:blueslime\", but found \"%s\"!", surfaceBlock));
                }
                break;
            }
        }

        save.close();
    }
}
