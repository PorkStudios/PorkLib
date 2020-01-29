/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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
