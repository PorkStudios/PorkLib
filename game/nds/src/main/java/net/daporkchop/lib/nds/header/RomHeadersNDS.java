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

package net.daporkchop.lib.nds.header;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * The headers on an NDS ROM
 * <p>
 * See http://problemkaputt.de/gbatek.htm#dscartridgeheader
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RomHeadersNDS {
    public static RomHeadersNDS load(@NonNull FileChannel channel, @NonNull MappedByteBuffer headersRegion) {
        if (headersRegion.capacity() != 0x170 + 0x90) {
            throw new IllegalArgumentException(String.format("Invalid map size: %d bytes", headersRegion.capacity()));
        }
        return new RomHeadersNDS(channel, headersRegion).load();
    }

    @Getter(value = AccessLevel.PROTECTED)
    @NonNull
    protected final FileChannel channel;
    @Getter(value = AccessLevel.PROTECTED)
    @NonNull
    protected final MappedByteBuffer headersRegion;
    protected boolean loaded;
    protected String name;

    public synchronized RomHeadersNDS load() {
        if (this.loaded) {
            throw new IllegalStateException("Already loaded!");
        } else {
            this.loaded = true;

            {
                byte[] buf = new byte[12];
                this.headersRegion.get(buf);
                int j;
                for (j = 0; j < buf.length; j++)    {
                    if (buf[j] == 0)    {
                        break;
                    }
                }
                this.name = new String(buf, 0, j);
            }
        }
        return this;
    }
}
