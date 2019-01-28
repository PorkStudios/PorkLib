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

package net.daporkchop.lib.nds;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.nds.header.RomHeadersNDS;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * A ROM for the Nintendo DS
 *
 * @author DaPorkchop_
 */
@Getter
public class RomNDS implements AutoCloseable {
    protected final Path path;
    protected final FileChannel channel;
    protected MappedByteBuffer headerMap;
    protected RomHeadersNDS headers;

    public RomNDS(@NonNull File file) throws IOException {
        this(file.toPath());
    }

    public RomNDS(@NonNull Path path) throws IOException {
        this.path = path;
        this.channel = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE);
    }

    public RomHeadersNDS getHeaders() throws IOException {
        synchronized (this) {
            if (this.headerMap == null) {
                this.headerMap = this.channel.map(FileChannel.MapMode.READ_WRITE, 0L, 0x170 + 0x90);
            }
            if (this.headers == null)   {
                this.headers = RomHeadersNDS.load(this.channel, this.headerMap);
            }
        }
        return this.headers;
    }

    @Override
    public void close() throws IOException {
        if (this.headerMap != null) {
            PorkUtil.release(this.headerMap);
            this.headerMap = null;
        }
        this.channel.close();
    }
}
