/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package binary;import net.daporkchop.lib.binary.bits.BitIn;
import net.daporkchop.lib.binary.bits.BitOut;
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
        for (int i = 50; i >= 0; i--) {
            int j = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
            DataOut out = DataOut.wrap(baos);
            out.writeVarInt(j);
            out.close();
            DataIn in = DataIn.wrap(new ByteArrayInputStream(baos.toByteArray()));
            int k = in.readVarInt();
            if (j != k) {
                throw new IllegalStateException(String.format("%d %d", j, k));
            }
            in.close();
            baos.reset();
        }
    }

    @Test
    public void testVarLong() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 500; i >= 0; i--) {
            long j = ThreadLocalRandom.current().nextLong();
            DataOut out = DataOut.wrap(baos);
            out.writeVarLong(j);
            out.close();
            DataIn in = DataIn.wrap(new ByteArrayInputStream(baos.toByteArray()));
            long k = in.readVarLong();
            if (j != k) {
                throw new IllegalStateException(String.format("%d %d", j, k));
            }
            in.close();
            baos.reset();
        }
    }

    @Test
    public void a() throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(new byte[1024]);
        BitIn bis = new BitIn(bais);
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
        BitOut bos = new BitOut(baos);
        bos.writeBits(3, 0);
        bos.writeBits(1, 0);
        bos.write(0);
        bos.writeBits(3, 0);
        bos.padToNextByte();
        bos.close();
    }
}
