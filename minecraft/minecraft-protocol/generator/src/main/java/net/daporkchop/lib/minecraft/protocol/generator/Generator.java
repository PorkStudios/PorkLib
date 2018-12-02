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

package net.daporkchop.lib.minecraft.protocol.generator;

import lombok.NonNull;
import net.daporkchop.lib.common.util.PorkUtil;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author DaPorkchop_
 */
public class Generator {
    public static void main(String... args) throws IOException {
        for (File platformDir : DataGenerator.IN_ROOT.listFiles()) {
            if (!platformDir.isDirectory()) {
                continue;
            }
            String platform = platformDir.getName();
            System.out.printf("Generating data for platform: %s\n", platform);
            DataGenerator.GENERATORS.get(platform)
                    .apply(platformDir)
                    .run(new File(DataGenerator.OUT_ROOT, platform));
        }

        System.out.printf("Generated %d files (%d bytes, %.2fMB)\n", ClassWriter.GENERATED.size(), ClassWriter.GENERATED_SIZE.get(), (double) ClassWriter.GENERATED_SIZE.get() / (1024.0d * 1024.0d));
        Map<String, File> onDisk = new HashMap<>();
        findOnDisk(DataGenerator.OUT_ROOT, onDisk);
        System.out.printf("Found %d files\n", onDisk.size());
        AtomicLong deletedCount = new AtomicLong(0L);
        AtomicLong deletedSize = new AtomicLong(0L);
        onDisk.entrySet().stream()
                .filter(e -> !ClassWriter.GENERATED.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .forEach(file -> {
                    deletedCount.incrementAndGet();
                    deletedSize.addAndGet(file.length());
                    if (file.exists() && !file.delete())  {
                        throw new IllegalStateException(String.format("Couldn't delete file: %s", file.getAbsolutePath()));
                    }
                });
        System.out.printf("Deleted %d old files (%d bytes, %.2fMB)\n", deletedCount.get(), deletedSize.get(), (double) deletedSize.get() / (1024.0d * 1024.0d));
    }

    private static void findOnDisk(@NonNull File file, @NonNull Map<String, File> map)    {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                findOnDisk(f, map);
            }
        } else if (file.isFile())   {
            map.put(String.format("%s.%s", ClassWriter.getPackageName(file), file.getName()), file);
        }
    }
}
