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

import lombok.NonNull;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author DaPorkchop_
 */
public class RomIcon {
    protected final short[] palette;
    protected final byte[] pixels;

    public RomIcon(@NonNull ByteBuffer buf) {
        this.palette = new short[16];
        this.pixels = new byte[0x200];
        buf.position(0x20);
        buf.get(this.pixels);
        //buf.position(0x220);
        //buf.order(ByteOrder.BIG_ENDIAN);
        for (int i = 0; i < 16; i++)    {
            this.palette[i] = buf.getShort();
        }
    }

    public short getColor(int x, int y) {
        int tileX = x >>> 3;
        int tileY = y >>> 3;
        int b = this.pixels[(((tileY << 2) | tileX) << 5) | ((y & 0x7) << 2) | ((x >>> 1) & 0x3)] & 0xFF;
        return this.palette[(b >>> ((x & 1) == 0 ? 0 : 4)) & 0xF];
    }

    public BufferedImage getAsBufferedImage()   {
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        for (int x = 31; x >= 0; x--)   {
            for (int y = 31; y >= 0; y--)   {
                short color = this.getColor(x, y);
                //color = this.palette[x & 0xF];
                img.setRGB(x, y, (color == 0 ? 0 : 0xFF000000) | (((color >>> 10) & 0x1F) << 3) | (((color >>> 5) & 0x1F) << 11) | ((color & 0x1F) << 19));
            }
        }
        return img;
    }
}
