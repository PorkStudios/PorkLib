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

package minecraft.worldscanner;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.http.Http;
import net.daporkchop.lib.math.vector.i.Vec2i;
import net.daporkchop.lib.minecraft.region.WorldScanner;
import net.daporkchop.lib.minecraft.registry.IDRegistry;
import net.daporkchop.lib.minecraft.registry.ResourceLocation;
import net.daporkchop.lib.minecraft.tileentity.impl.TileEntitySign;
import net.daporkchop.lib.minecraft.world.MinecraftSave;
import net.daporkchop.lib.minecraft.world.format.anvil.AnvilSaveFormat;
import net.daporkchop.lib.minecraft.world.format.anvil.region.RegionFile;
import net.daporkchop.lib.minecraft.world.format.anvil.region.RegionOpenOptions;
import net.daporkchop.lib.minecraft.world.impl.MinecraftSaveConfig;
import net.daporkchop.lib.minecraft.world.impl.SaveBuilder;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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
                .setInitFunctions(new MinecraftSaveConfig()
                        .openOptions(new RegionOpenOptions().access(RegionFile.Access.READ_ONLY)))
                .build();
    }

    @Test
    public void findAverageHeight() throws IOException {
        try (MinecraftSave save = this.getTestWorld()) {
            AtomicLong h = new AtomicLong(0L);
            AtomicLong c = new AtomicLong(0L);

            new WorldScanner(save.world(0))
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
            IDRegistry blocksRegistry = save.registry(new ResourceLocation("minecraft:blocks"));
            int chestId = blocksRegistry.lookup(new ResourceLocation("minecraft:chest"));
            int trappedChestId = blocksRegistry.lookup(new ResourceLocation("minecraft:trapped_chest"));
            new WorldScanner(save.world(0))
                    .addProcessor(col -> {
                        if ((col.getX() & 0x1F) == 31 && (col.getZ() & 0x1F) == 31) {
                            System.out.printf("Scanning region (%d,%d)\n", col.getX() >> 5, col.getZ() >> 5);
                        }
                    })
                    .addProcessor((current, estimatedTotal, chunk, access) -> {
                        int x = chunk.getX() << 4;
                        int z = chunk.getZ() << 4;
                        for (int xx = 15; xx >= 0; xx--) {
                            for (int zz = 15 - (xx & 1); zz >= 0; zz -= 2) {
                                for (int y = 255; y >= 0; y--) {
                                    int id = access.getBlockId(x + xx, y, z + zz);
                                    if (id == chestId) {
                                        if (false) {
                                            System.out.printf("Found chest at (%d,%d,%d)\n", x + xx, y, z + zz);
                                        }
                                        if (access.getBlockId(x + xx + 1, y, z + zz) == chestId
                                                || access.getBlockId(x + xx, y, z + zz + 1) == chestId) {
                                            System.out.printf("Found double chest at (%d,%d,%d)\n", x + xx, y, z + zz);
                                        }
                                    } else if (id == trappedChestId) {
                                        if (false) {
                                            System.out.printf("Found trapped chest at (%d,%d,%d)\n", x + xx, y, z + zz);
                                        }
                                        if (access.getBlockId(x + xx + 1, y, z + zz) == trappedChestId
                                                || access.getBlockId(x + xx, y, z + zz + 1) == trappedChestId) {
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
            new WorldScanner(save.world(0))
                    .addProcessor(col -> {
                        if ((col.getX() & 0x1F) == 31 && (col.getZ() & 0x1F) == 31) {
                            System.out.printf("Scanning region (%d,%d)\n", col.getX() >> 5, col.getZ() >> 5);
                        }
                    })
                    .addProcessor(col -> col.tileEntities().forEach(tileEntity -> {
                        if (false) {
                            System.out.printf("Found TileEntity (id=%s, class=%s) at (%d,%d,%d)\n", tileEntity.id().toString(), tileEntity.getClass().getCanonicalName(), tileEntity.getX(), tileEntity.getY(), tileEntity.getZ());
                        }
                        if (tileEntity instanceof TileEntitySign) {
                            TileEntitySign sign = (TileEntitySign) tileEntity;
                            System.out.printf("Found sign at (%d,%d,%d). Content: \n%s\n%s\n%s\n%s\n", tileEntity.getX(), tileEntity.getY(), tileEntity.getZ(), sign.line1(), sign.line2(), sign.line3(), sign.line4());
                        }
                    }))
                    .run(true);
        }
        PorkUtil.unsafe_forceGC();
    }

    @Test
    public void makeSimpleMap() throws IOException {
        Map<ResourceLocation, Color[]> colorMap = new HashMap<>();
        {
            String colorData = Http.getString("https://raw.githubusercontent.com/DaMatrix/betterMapArt/master/src/main/resources/colors.json");
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
            IDRegistry registry = save.registry(new ResourceLocation("minecraft:blocks"));
            File out = new File(".", "run/out");
            PFiles.rmContents(PFiles.ensureDirectoryExists(out));
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

            new WorldScanner(save.world(0))
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
                                        ResourceLocation registryName = registry.lookup(id);
                                        Color[] colors = registryName == null ? null : colorMap.get(registryName);
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
