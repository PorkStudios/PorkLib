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

import net.daporkchop.lib.binary.bit.BitInputStream;
import net.daporkchop.lib.binary.bit.BitOutputStream;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author DaPorkchop_
 */
public class TestMain {
    @Test
    public void testVarInt() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 500; i >= 0; i--)   {
            int j = ThreadLocalRandom.current().nextInt();
            DataOut out = DataOut.wrap(baos);
            out.writeVarInt(j);
            out.close();
            DataIn in = DataIn.wrap(new ByteArrayInputStream(baos.toByteArray()));
            int k = in.readVarInt();
            if (j != k)   {
                throw new IllegalStateException(String.format("%d %d", j, k));
            }
            in.close();
            baos.reset();
        }
    }

    @Test
    public void testVarLong() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 500; i >= 0; i--)   {
            long j = ThreadLocalRandom.current().nextLong();
            DataOut out = DataOut.wrap(baos);
            out.writeVarLong(j);
            out.close();
            DataIn in = DataIn.wrap(new ByteArrayInputStream(baos.toByteArray()));
            long k = in.readVarLong();
            if (j != k)   {
                throw new IllegalStateException(String.format("%d %d", j, k));
            }
            in.close();
            baos.reset();
        }
    }

    @Test
    public void a() throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(new byte[1024]);
        BitInputStream bis = new BitInputStream(bais);
        bis.read();
        bis.readBits(1);
        bis.readBits(2);
        bis.read();
        bis.padToNextByte();
        bis.close();
    }

    @Test
    public void b() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BitOutputStream bos = new BitOutputStream(baos);
        bos.writeBits(3, 0);
        bos.writeBits(1, 0);
        bos.write(0);
        bos.writeBits(3, 0);
        bos.padToNextByte();
        bos.close();
    }
}
