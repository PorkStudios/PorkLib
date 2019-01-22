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

package example.binary;

import net.daporkchop.lib.binary.buf.file.PFileDispatcherImpl;
import net.daporkchop.lib.binary.util.unsafe.block.MemoryBlock;
import net.daporkchop.lib.binary.util.unsafe.offset.OffsetLookup;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.reflection.lambda.LambdaBuilder;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntFunction;

/**
 * haha get pranked this isn't even an example it's just me messing around
 *
 * @author DaPorkchop_
 */
public class TestingMethodThings {
    public static void main(String... args) {
        testMethodThing();

        //OffsetLookup.INSTANCE.hashCode();

        if (false) {
            MemoryBlock block = MemoryBlock.wrap(new byte[1024]);
            for (int i = 1023; i >= 0; i--) {
                block.setByte(i, (byte) ThreadLocalRandom.current().nextInt());
            }
            byte[] arr = new byte[1024];
            block.getBytes(0L, arr);
            for (int i = 1023; i >= 0; i--) {
                if (arr[i] != block.getByte(i)) {
                    throw new IllegalStateException();
                }
            }
            ThreadLocalRandom.current().nextBytes(arr);
            block.setBytes(0L, arr);
            for (int i = 1023; i >= 0; i--) {
                if (arr[i] != block.getByte(i)) {
                    throw new IllegalStateException();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void testMethodThing() {
        IntFunction<ByteBuffer> BUFFER = LambdaBuilder.of(IntFunction.class)
                .setMethodHolder(ByteBuffer.class)
                .setMethodName("allocateDirect")
                .setStatic()
                .setInterfaceName("apply")
                .returnType().setType(ByteBuffer.class).setInterfaceGeneric(true).build()
                .param().setType(int.class).build()
                .build();
        for (Method m : BUFFER.getClass().getDeclaredMethods()) {
            if (false)  {
                System.out.println(m);
            }
        }
        System.out.println(BUFFER.apply(1024));

        System.out.println(PFileDispatcherImpl.READ0);
    }
}
