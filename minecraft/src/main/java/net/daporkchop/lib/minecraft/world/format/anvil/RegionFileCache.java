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

package net.daporkchop.lib.minecraft.world.format.anvil;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.*;
import java.lang.ref.*;
import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegionFileCache {
    private static final int MAX_CACHE_SIZE = 256;

    private static final Map<File, Reference<RegionFile>> cache = new HashMap<>();

    public static synchronized RegionFile getRegionFile(File basePath, int chunkX, int chunkZ) {
        return getRegionFile(basePath, chunkX, chunkZ, true);
    }

    public static synchronized RegionFile getRegionFile(File basePath, int chunkX, int chunkZ, boolean create) {
        File file = new File(basePath, String.format("r.%d.%d.mca", chunkX >> 5, chunkZ >> 5));
        if (!file.exists() && !create)  {
            return null;
        }

        Reference<RegionFile> ref = cache.get(file);

        if (ref != null && ref.get() != null) {
            return ref.get();
        }

        if (!basePath.exists()) {
            basePath.mkdirs();
        }

        if (cache.size() >= MAX_CACHE_SIZE) {
            clear();
        }

        RegionFile reg = new RegionFile(file);
        cache.put(file, new SoftReference<>(reg));
        return reg;
    }

    public static synchronized void clear() {
        for (Reference<RegionFile> ref : cache.values()) {
            try {
                if (ref.get() != null) {
                    ref.get().close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        cache.clear();
    }
}