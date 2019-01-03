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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.daporkchop.lib.http.SimpleHTTP;
import net.daporkchop.lib.math.vector.i.Vec2i;
import net.daporkchop.lib.minecraft.region.WorldScanner;
import net.daporkchop.lib.minecraft.registry.Registry;
import net.daporkchop.lib.minecraft.registry.ResourceLocation;
import net.daporkchop.lib.minecraft.tileentity.TileEntitySign;
import net.daporkchop.lib.minecraft.world.MinecraftSave;
import net.daporkchop.lib.minecraft.world.format.anvil.AnvilSaveFormat;
import net.daporkchop.lib.minecraft.world.impl.SaveBuilder;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author DaPorkchop_
 */
public class ScannerTest {
    private MinecraftSave getTestWorld() {
        return new SaveBuilder()
                .setFormat(new AnvilSaveFormat(new File(".", "run/testworld")))
                //.setFormat(new AnvilSaveFormat(new File("/media/daporkchop/TooMuchStuff/Misc/2b2t_org")))
                //.setFormat(new AnvilSaveFormat(new File("E:\\Misc\\2b2t_org")))
                .build();
    }

    @Test
    public void findAverageHeight() throws IOException {
        try (MinecraftSave save = this.getTestWorld()) {
            AtomicLong h = new AtomicLong(0L);
            AtomicLong c = new AtomicLong(0L);

            new WorldScanner(save.getWorld(0))
                    .addProcessor(col -> {
                        if ((col.getX() & 0x1F) == 31 && (col.getZ() & 0x1F) == 31) {
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
    public void findDoubleChests() throws IOException {
        try (MinecraftSave save = this.getTestWorld()) {
            Registry blocksRegistry = save.getRegistry(new ResourceLocation("minecraft:blocks"));
            int chestId = blocksRegistry.getId(new ResourceLocation("minecraft:chest"));
            int trappedChestId = blocksRegistry.getId(new ResourceLocation("minecraft:trapped_chest"));
            new WorldScanner(save.getWorld(0))
                    .addProcessor(col -> {
                        if ((col.getX() & 0x1F) == 31 && (col.getZ() & 0x1F) == 31) {
                            System.out.printf("Scanning region (%d,%d)\n", col.getX() >> 5, col.getZ() >> 5);
                        }
                    })
                    .addProcessor((current, estimatedTotal, world, x, z) -> {
                        for (int xx = 15; xx >= 0; xx--) {
                            for (int zz = (xx & 1) == 0 ? 15 : 14; zz >= 0; zz -= 2) {
                                for (int y = 255; y >= 0; y--) {
                                    int id = world.getBlockId(x + xx, y, z + zz);
                                    if (id == chestId) {
                                        if (false) {
                                            System.out.printf("Found chest at (%d,%d,%d)\n", x + xx, y, z + zz);
                                        }
                                        if (world.getBlockId(x + xx + 1, y, z + zz) == chestId
                                                || world.getBlockId(x + xx, y, z + zz + 1) == chestId) {
                                            System.out.printf("Found double chest at (%d,%d,%d)\n", x + xx, y, z + zz);
                                        }
                                    } else if (id == trappedChestId) {
                                        if (false) {
                                            System.out.printf("Found trapped chest at (%d,%d,%d)\n", x + xx, y, z + zz);
                                        }
                                        if (world.getBlockId(x + xx + 1, y, z + zz) == trappedChestId
                                                || world.getBlockId(x + xx, y, z + zz + 1) == trappedChestId) {
                                            System.out.printf("Found double trapped chest at (%d,%d,%d)\n", x + xx, y, z + zz);
                                        }
                                    }
                                }
                            }
                        }
                    })
                    .run(true);
        }
    }

    @Test
    public void findTileEntities() throws IOException {
        try (MinecraftSave save = this.getTestWorld()) {
            new WorldScanner(save.getWorld(0))
                    .addProcessor(col -> {
                        if ((col.getX() & 0x1F) == 31 && (col.getZ() & 0x1F) == 31) {
                            System.out.printf("Scanning region (%d,%d)\n", col.getX() >> 5, col.getZ() >> 5);
                        }
                    })
                    .addProcessor(col -> col.getTileEntities().forEach(tileEntity -> {
                        if (false) {
                            System.out.printf("Found TileEntity (id=%s, class=%s) at (%d,%d,%d)\n", tileEntity.getId().toString(), tileEntity.getClass().getCanonicalName(), tileEntity.getX(), tileEntity.getY(), tileEntity.getZ());
                        }
                        if (tileEntity instanceof TileEntitySign) {
                            TileEntitySign sign = (TileEntitySign) tileEntity;
                            System.out.printf("Found sign at (%d,%d,%d). Content: \n%s\n%s\n%s\n%s\n", tileEntity.getX(), tileEntity.getY(), tileEntity.getZ(), sign.getLine1(), sign.getLine2(), sign.getLine3(), sign.getLine4());
                        }
                    }))
                    .run(true);
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
            colorMap.put(new ResourceLocation("minecraft:water"), new Color[]{colors[25], colors[25], colors[25], colors[25], colors[25], colors[25], colors[25], colors[25], colors[25], colors[25], colors[25], colors[25], colors[25], colors[25], colors[25], colors[25]});
            colorMap.put(new ResourceLocation("minecraft:snow_layer"), new Color[]{colors[8], colors[8], colors[8], colors[8], colors[8], colors[8], colors[8], colors[8], colors[8], colors[8], colors[8], colors[8], colors[8], colors[8], colors[8], colors[8]});
        }
        try (MinecraftSave save = this.getTestWorld()) {
            Registry registry = save.getRegistry(new ResourceLocation("minecraft:blocks"));
            File out = new File(".", "run/out");
            out.mkdirs();
            LoadingCache<Vec2i, BufferedImage> outCache = CacheBuilder.newBuilder()
                    .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                    .maximumSize(Runtime.getRuntime().availableProcessors() << 1)
                    .removalListener(new RemovalListener<Vec2i, BufferedImage>() {
                        @Override
                        public void onRemoval(RemovalNotification<Vec2i, BufferedImage> notification) {
                            Vec2i pos = notification.getKey();
                            BufferedImage image = notification.getValue();
                            try {
                                ImageIO.write(image, "png", new File(out, String.format("img.%d.%d.png", pos.getX(), pos.getY())));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    })
                    .build(new CacheLoader<Vec2i, BufferedImage>() {
                        @Override
                        public BufferedImage load(Vec2i pos) throws Exception {
                            File f = new File(out, String.format("img.%d.%d.png", pos.getX(), pos.getY()));
                            if (f.exists()) {
                                try {
                                    return ImageIO.read(f);
                                } catch (Exception e) {
                                }
                            }
                            return new BufferedImage(16 * 4 * 32, 16 * 4 * 32, BufferedImage.TYPE_INT_ARGB);
                        }
                    });

            new WorldScanner(save.getWorld(0))
                    .addProcessor(col -> {
                        if ((col.getX() & 0x1F) == 31 && (col.getZ() & 0x1F) == 31) {
                            System.out.printf("Mapping region (%d,%d)\n", col.getX() >> 5, col.getZ() >> 5);
                        }
                    })
                    .addProcessor(col -> {
                        BufferedImage realOut = outCache.getUnchecked(new Vec2i(col.getX() >> 7, col.getZ() >> 7));
                        for (int x = 15; x >= 0; x--) {
                            for (int z = 15; z >= 0; z--) {
                                int id;
                                for (int y = 255; y >= 0; y--) {
                                    if ((id = col.getBlockId(x, y, z)) != 0) {
                                        int meta = col.getBlockMeta(x, y, z);
                                        ResourceLocation registryName = registry.getName(id);
                                        Color[] colors = colorMap.get(registryName);
                                        if (colors == null || colors[meta] == null) {
                                            continue;
                                        }
                                        //if (colors != null) {
                                            /*if (colors[meta] == null) {
                                                System.out.printf("Warning: block %s with meta %d has no color!\n", registryName.toString(), meta);
                                                continue;
                                            }*/
                                        int color = 0xFF000000 | colors[meta].getRGB();
                                        if (color == 0xFF000000) {
                                            continue;
                                        }
                                        realOut.setRGB(x + ((col.getX() & 0x7F) << 4), z + ((col.getZ() & 0x7F) << 4), color);
                                        //}
                                        break;
                                    }
                                }
                            }
                        }
                    })
                    .run(true);

            outCache.invalidateAll();
            outCache.cleanUp();
        }
    }
}
