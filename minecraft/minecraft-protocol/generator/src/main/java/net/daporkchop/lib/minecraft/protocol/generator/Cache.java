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

package net.daporkchop.lib.minecraft.protocol.generator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.StreamUtil;
import net.daporkchop.lib.http.SimpleHTTP;
import net.daporkchop.lib.logging.Logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Cache implements Logging {
    public static final Cache INSTANCE = new Cache();

    private static final Map<File, byte[]> CACHE = new ConcurrentHashMap<>();

    public byte[] getOrLoad(@NonNull File file, @NonNull String url)  {
        return CACHE.computeIfAbsent(file, sdkljflkdjf -> {
            try {
                byte[] b;
                if (file.exists())  {
                    try (InputStream in = new FileInputStream(file))    {
                        b = StreamUtil.readFully(in, -1, false);
                    }
                } else {
                    File parent = file.getParentFile();
                    if (!parent.exists() && !parent.mkdirs())   {
                        throw this.exception("Couldn't create directory: ${0}", parent);
                    } else if (!file.createNewFile())   {
                        throw this.exception("Couldn't create file: ${0}", file);
                    } else {
                        try (OutputStream out = new FileOutputStream(file)) {
                            out.write(b = SimpleHTTP.get(url));
                        }
                    }
                }
                return b;
            } catch (IOException e)  {
                throw new RuntimeException(e);
            }
        });
    }
}
