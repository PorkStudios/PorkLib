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

import net.daporkchop.lib.binary.PorkBuf;
import net.daporkchop.lib.binary.bit.BitInputStream;
import net.daporkchop.lib.binary.bit.BitOutputStream;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * @author DaPorkchop_
 */
public class TestMain {
    @Test
    public void test() {
        Random random;
        long a, b;
        for (int i = 0; i < 256; i++) {
            PorkBuf buf = PorkBuf.allocate(1024);
            random = new Random(i);
            while (buf.getWritePos() != 1024) {
                buf.putLong(random.nextLong());
            }
            buf.reset();
            random = new Random(i);
            while (buf.getReadPos() != 1024) {
                if ((a = buf.getLong()) != (b = random.nextLong())) {
                    throw new IllegalStateException("Invalid value " + a + " (expected: " + b + ")");
                }
            }
        }
    }

    @Test
    public void testWritten() {
        PorkBuf buf = PorkBuf.allocate(4);
        buf.setExpand(true);

        byte[] randomBytes = new byte[]{
                0, 1, 2, 3, 4, 5, 6
        };
        buf.putBytes(randomBytes);

        byte[] raw = buf.toArray();
        byte[] written = buf.getWrittenBytes();

        if (raw.length == written.length || written.length != randomBytes.length
                || !Arrays.equals(randomBytes, written)) {
            throw new IllegalStateException();
        } else {
            System.out.println("Success!");
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
