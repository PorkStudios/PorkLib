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
    protected String gamecode;
    protected String makercode;
    protected int unitcode;
    protected int encryptionSeedSelect;
    protected int deviceCapacity;
    protected RegionNDS region;
    protected int version;
    protected int arm9OffsetRom;
    protected int arm9EntryAddress;
    protected int arm9RAMAddress;
    protected int arm9Size;
    protected int arm7OffsetRom;
    protected int arm7EntryAddress;
    protected int arm7RAMAddress;
    protected int arm7Size;
    protected int fntOffset;
    protected int fntSize;
    protected int fatOffset;
    protected int fatSize;
    protected int iconOffset;

    public synchronized RomHeadersNDS load() {
        if (this.loaded) {
            throw new IllegalStateException("Already loaded!");
        } else {
            this.loaded = true;

            byte[] buf;
            {
                buf = new byte[12];
                this.headersRegion.get(buf);
                int j;
                for (j = 0; j < buf.length; j++)    {
                    if (buf[j] == 0)    {
                        break;
                    }
                }
                this.name = new String(buf, 0, j);
            }
            {
                buf = new byte[4];
                this.headersRegion.get(buf);
                this.gamecode = "NTR-" + new String(buf);
            }
            {
                buf = new byte[2];
                this.headersRegion.get(buf);
                this.makercode = new String(buf);
            }
            this.unitcode = this.headersRegion.get() & 0xFF;
            this.encryptionSeedSelect = this.headersRegion.get() & 0xFF;
            this.deviceCapacity = 131072 << (this.headersRegion.get() & 0xFF);
            this.headersRegion.position(this.headersRegion.position() + 8);
            switch (this.headersRegion.get() & 0xFF) {
                case 0x80:
                    this.region = RegionNDS.CHINA;
                    break;
                case 0x40:
                    this.region = RegionNDS.KOREA;
                    break;
                default:
                    this.region = RegionNDS.NORMAL;
            }
            this.version = this.headersRegion.get() & 0xFF;
            this.headersRegion.position(this.headersRegion.position() + 1);
            this.arm9OffsetRom = this.headersRegion.getInt();
            this.arm9EntryAddress = this.headersRegion.getInt();
            this.arm9RAMAddress = this.headersRegion.getInt();
            this.arm9Size = this.headersRegion.getInt();
            this.arm7OffsetRom = this.headersRegion.getInt();
            this.arm7EntryAddress = this.headersRegion.getInt();
            this.arm7RAMAddress = this.headersRegion.getInt();
            this.arm7Size = this.headersRegion.getInt();
            this.fntOffset = this.headersRegion.getInt();
            this.fntSize = this.headersRegion.getInt();
            this.fatOffset = this.headersRegion.getInt();
            this.fatSize = this.headersRegion.getInt();
            this.headersRegion.position(this.headersRegion.position() + 8);
            this.iconOffset = this.headersRegion.getInt();
        }
        return this;
    }

    public boolean isDSi()  {
        return (this.unitcode & 0x2) != 0;
    }
}
