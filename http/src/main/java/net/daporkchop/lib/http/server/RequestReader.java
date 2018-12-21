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

package net.daporkchop.lib.http.server;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.UTF8;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class RequestReader {
    @NonNull
    private final ByteBuf buf;

    public String readUntilSpace()  {
        int i = this.buf.readerIndex();
        int j = 0;
        for (; (this.buf.readByte() & 0xFF) != 0x20; j++)   {
        }
        return this.buf.slice(i, j).toString(UTF8.utf8);
    }

    public int skipUntil(int c) {
        int i = 0;
        for (int j = this.buf.readByte() & 0xFF; j != c; j = this.buf.readByte() & 0xFF)    {
            i++;
        }
        return i;
    }

    public String readUntil(int c)  {
        int i = this.buf.readerIndex();
        int j = 0;
        for (; (this.buf.readByte() & 0xFF) != c; j++)   {
        }
        return this.buf.slice(i, j).toString(UTF8.utf8);
    }

    public void skip(int bytes) {
        this.buf.skipBytes(bytes);
    }

    public int next()   {
        return this.buf.getByte(this.buf.readerIndex());
    }
}
