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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.daporkchop.lib.http.SimpleHTTP;
import net.daporkchop.lib.math.vector.i.Vec2i;
import net.daporkchop.lib.minecraft.region.WorldScanner;
import net.daporkchop.lib.minecraft.registry.Registry;
import net.daporkchop.lib.minecraft.registry.ResourceLocation;
import net.daporkchop.lib.minecraft.world.MinecraftSave;
import net.daporkchop.lib.minecraft.world.format.anvil.AnvilSaveFormat;
import net.daporkchop.lib.minecraft.world.impl.SaveBuilder;
import net.daporkchop.lib.primitive.map.IntegerObjectMap;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author DaPorkchop_
 */
public class ScannerTest {
    private MinecraftSave getTestWorld() {
        return new SaveBuilder()
                .setFormat(new AnvilSaveFormat(new File(".", "run/testworld")))
                .build();
    }

    @Test
    public void findAverageHeight() throws IOException {
        try (MinecraftSave save = this.getTestWorld()) {
            AtomicLong h = new AtomicLong(0L);
            AtomicLong c = new AtomicLong(0L);

            new WorldScanner(save.getWorld(0))
                    .addProcessor(col -> {
                        if (((col.getX() & 0x1F) | (col.getZ() & 0x1F)) == 0) {
                            System.out.printf("Scanning region (%d,%d)\n", col.getX() >> 5, col.getZ() >> 5);
                        }
                    })
                    .addProcessor(col -> {
                        c.addAndGet(256L);
                        for (int x = 15; x >= 0; x--) {
                            for (int z = 15; z >= 0; z--) {
                                for (int y = 255; y >= 0; y--) {
                                    if (col.getBlockId(x, y, z) != 0) {
                                        h.addAndGet(y);
                                        break;
                                    }
                                }
                            }
                        }
                    })
                    .run(true);

            System.out.printf("Average height: %.2f\n", (float) h.get() / (float) c.get());
            System.out.printf("Total values: %d\n", c.get());
            System.out.printf("Sum: %d\n", h.get());
        }
    }

    @Test
    public void makeSimpleMap() throws IOException {
        Map<ResourceLocation, Color[]> colorMap = new Hashtable<>();
        {
            String colorData = SimpleHTTP.getString("https://raw.githubusercontent.com/DaMatrix/betterMapArt/master/src/main/resources/colors.json");
            JsonParser parser = new JsonParser();
            JsonObject object = parser.parse(colorData).getAsJsonObject();
            Color[] colors;
            {
                JsonObject colorsJson = object.getAsJsonObject("colors");
                colors = new Color[colorsJson.size()];
                colorsJson.entrySet().forEach(entry ->
                        colors[Integer.parseInt(entry.getKey())] = new Color(
                                entry.getValue().getAsJsonObject().get("red").getAsInt(),
                                entry.getValue().getAsJsonObject().get("green").getAsInt(),
                                entry.getValue().getAsJsonObject().get("blue").getAsInt(),
                                entry.getValue().getAsJsonObject().get("alpha").getAsInt()
                        ));
            }
            JsonArray array = object.getAsJsonArray("blocks");
            array.forEach(element -> {
                Color[] states = colorMap.computeIfAbsent(new ResourceLocation(element.getAsJsonObject().get("registryName").getAsString()), name -> new Color[16]);
                Color col = colors[Integer.parseInt(element.getAsJsonObject().get("color").getAsString())];
                states[element.getAsJsonObject().get("meta").getAsInt()] = col;
            });
            colorMap.put(new ResourceLocation("minecraft:water"), new Color[]{colors[25],colors[25],colors[25],colors[25],colors[25],colors[25],colors[25],colors[25],colors[25],colors[25],colors[25],colors[25],colors[25],colors[25],colors[25],colors[25]});
        }
        try (MinecraftSave save = this.getTestWorld()) {
            Registry registry = save.getRegistry(new ResourceLocation("minecraft:blocks"));
            Map<Vec2i, BufferedImage[]> images = new Hashtable<>();
            AtomicInteger minX = new AtomicInteger(Integer.MAX_VALUE);
            AtomicInteger minZ = new AtomicInteger(Integer.MAX_VALUE);
            AtomicInteger maxX = new AtomicInteger(Integer.MIN_VALUE);
            AtomicInteger maxZ = new AtomicInteger(Integer.MIN_VALUE);

            new WorldScanner(save.getWorld(0))
                    .addProcessor(col -> {
                        if (((col.getX() & 0x1F) | (col.getZ() & 0x1F)) == 0) {
                            System.out.printf("Mapping region (%d,%d)\n", col.getX() >> 5, col.getZ() >> 5);
                        }
                    })
                    .addProcessor(col -> {
                        minX.updateAndGet(x -> Math.min(x, col.getX()));
                        minZ.updateAndGet(z -> Math.min(z, col.getZ()));
                        maxX.updateAndGet(x -> Math.max(x, col.getX()));
                        maxZ.updateAndGet(z -> Math.max(z, col.getZ()));
                        BufferedImage[] imageArray = images.computeIfAbsent(new Vec2i(col.getX() >> 5, col.getZ() >> 5), pos -> new BufferedImage[32 * 32]);
                        BufferedImage out = imageArray[((col.getX() & 0x1F) << 5) | (col.getZ() & 0x1F)] = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
                        for (int x = 15; x >= 0; x--) {
                            for (int z = 15; z >= 0; z--) {
                                int id = 0;
                                int meta = 0;
                                for (int y = 255; y >= 0; y--) {
                                    if ((id = col.getBlockId(x, y, z)) != 0) {
                                        meta = col.getBlockMeta(x, y, z);
                                        break;
                                    }
                                }
                                ResourceLocation registryName = registry.getName(id);
                                Color[] colors = colorMap.get(registryName);
                                if (colors != null) {
                                    if (colors[meta] == null) {
                                        //System.out.printf("Warning: block %s with meta %d has no color!\n", registryName.toString(), meta);
                                        continue;
                                    }
                                    out.setRGB(x, z, colors[meta].getRGB());
                                }
                            }
                        }
                    })
                    .run(true);

            File out = new File(".", "run/out");
            BufferedImage img = new BufferedImage(Math.abs(minX.get() - maxX.get()) * 16 + 16, Math.abs(minZ.get() - maxZ.get()) * 16 + 16, BufferedImage.TYPE_INT_ARGB);
            images.entrySet().parallelStream().forEach(entry -> {
                Vec2i regionPos = entry.getKey();
                BufferedImage[] imgs = entry.getValue();
                for (int x = 31; x >= 0; x--) {
                    for (int z = 31; z >= 0; z--) {
                        BufferedImage image = imgs[(x << 5) | z];
                        if (image == null) {
                            continue;
                        }
                        for (int xx = 15; xx >= 0; xx--)    {
                            for (int zz = 15; zz >= 0; zz--)    {
                                img.setRGB(
                                        img.getWidth() - 1 - (((regionPos.getX() * 32) + x - minX.get() + 1*0) * 16) - xx,
                                        img.getHeight() - 1 - (((regionPos.getY() * 32) + z - minZ.get() + 1*0) * 16) - zz,
                                        image.getRGB(xx, zz));
                            }
                        }
                    }
                }
            });
            ImageIO.write(img, "png", new File(out, "map.png"));
        }
    }
}
