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

import net.daporkchop.lib.natives.PNatives;
import net.daporkchop.lib.natives.zlib.PDeflater;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.InflaterOutputStream;

/**
 * @author DaPorkchop_
 */
public class NativeTests {
    public static void main(String... args) throws IOException {
        PDeflater deflater = PNatives.ZLIB.get().deflater(8, false);

        byte[] arr = new byte[67108864];
        ThreadLocalRandom.current().nextBytes(arr);
        Arrays.fill(arr, 128, arr.length, (byte) 0);
        long src = PUnsafe.allocateMemory(arr.length);
        PUnsafe.copyMemory(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, null, src, arr.length);
        deflater.input(src, arr.length);

        long dst = PUnsafe.allocateMemory(arr.length);
        deflater.output(dst, arr.length);

        System.out.println(deflater.finished());
        deflater.deflateFinish();

        System.out.printf("Read: %d\nWritten: %d\n", deflater.readBytes(), deflater.writtenBytes());
        System.out.println(deflater.finished());

        PUnsafe.freeMemory(src);

        byte[] arr2 = new byte[(int) deflater.writtenBytes()];
        PUnsafe.copyMemory(null, dst, arr2, PUnsafe.ARRAY_BYTE_BASE_OFFSET, deflater.writtenBytes());
        PUnsafe.freeMemory(dst);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (InflaterOutputStream out = new InflaterOutputStream(baos))  {
            out.write(arr2);
        }
        arr2 = null;

        if (!Arrays.equals(arr, baos.toByteArray()))    {
            throw new IllegalStateException();
        }
    }
}
