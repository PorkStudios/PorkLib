/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2024 DaPorkchop_
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
 */

package unsafe;

import lombok.SneakyThrows;
import net.daporkchop.lib.unsafe.PUnsafe;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * @author DaPorkchop_
 */
public class UnsafeByteOrderingTest {
    private static final int ITRS = 1024;

    @BeforeClass
    public static void requireUnalignedAccess() {
        assertTrue("Tests must be run on a platform which supports unaligned memory access!", PUnsafe.isUnalignedAccessSupported());
    }

    @Test
    public void testUnalignedNative() {
        this.doTest(PUnsafe.class);
    }

    @Test
    @SneakyThrows
    public void testUnalignedEmulated() {
        //load a copied version of the class which will see the different system property value
        System.setProperty("porklib.unsafe.forceAlignedAccess", "true");
        ClassLoader childClassLoader = new ClassLoader(null) {
            @Override
            @SneakyThrows
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                if (name.startsWith("net.daporkchop.lib.unsafe.")) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try (InputStream in = PUnsafe.class.getClassLoader().getResourceAsStream(name.replace('.', '/') + ".class")) {
                        if (in == null) {
                            throw new ClassNotFoundException(name);
                        }

                        byte[] buf = new byte[1024];
                        for (int i; (i = in.read(buf)) > 0; ) {
                            baos.write(buf, 0, i);
                        }
                    }
                    return super.defineClass(name, baos.toByteArray(), 0, baos.size());
                }

                return super.findClass(name);
            }
        };
        Class<?> punsafeClass = childClassLoader.loadClass(PUnsafe.class.getName());
        assertFalse("Failed to disable unaligned memory access!", (boolean) MethodHandles.publicLookup()
                .findStatic(punsafeClass, "isUnalignedAccessSupported", MethodType.methodType(boolean.class))
                .invokeExact());
        this.doTest(punsafeClass);
    }

    private void doTest(Class<?> punsafeClass) {
        this.doTest(punsafeClass, Character.BYTES, char.class, r -> (char) r.nextInt());
        this.doTest(punsafeClass, Short.BYTES, short.class, r -> (short) r.nextInt());
        this.doTest(punsafeClass, Integer.BYTES, int.class, SplittableRandom::nextInt);
        this.doTest(punsafeClass, Long.BYTES, long.class, SplittableRandom::nextLong);
        this.doTest(punsafeClass, Float.BYTES, float.class, r -> (float) r.nextDouble());
        this.doTest(punsafeClass, Double.BYTES, double.class, SplittableRandom::nextDouble);
    }

    private <BOX> void doTest(Class<?> punsafeClass, int size, Class<?> primitiveClass, Function<SplittableRandom, BOX> rng) {
        assert primitiveClass.isPrimitive() : primitiveClass;
        String primitiveName = primitiveClass.getName();
        primitiveName = Character.toUpperCase(primitiveName.charAt(0)) + primitiveName.substring(1);

        long address = PUnsafe.allocateMemory((long) size * ITRS);
        try {
            this.doTest(punsafeClass, size, primitiveClass, primitiveName, rng, address, ByteOrder.BIG_ENDIAN);
            this.doTest(punsafeClass, size, primitiveClass, primitiveName, rng, address, ByteOrder.LITTLE_ENDIAN);
        } finally {
            PUnsafe.freeMemory(address);
        }
    }

    @SneakyThrows
    private <BOX> void doTest(Class<?> punsafeClass, int size, Class<?> primitiveClass, String primitiveName, Function<SplittableRandom, BOX> rng,
                              long address, ByteOrder order) {
        String suffix = order == ByteOrder.BIG_ENDIAN ? "BE" : "LE";
        MethodHandle heap_getUnaligned = MethodHandles.publicLookup().findStatic(punsafeClass,
                "getUnaligned" + primitiveName + suffix, MethodType.methodType(primitiveClass, Object.class, long.class));
        MethodHandle heap_putUnaligned = MethodHandles.publicLookup().findStatic(punsafeClass,
                "putUnaligned" + primitiveName + suffix, MethodType.methodType(void.class, Object.class, long.class, primitiveClass));
        MethodHandle direct_getUnaligned = MethodHandles.publicLookup().findStatic(punsafeClass,
                "getUnaligned" + primitiveName + suffix, MethodType.methodType(primitiveClass, long.class));
        MethodHandle direct_putUnaligned = MethodHandles.publicLookup().findStatic(punsafeClass,
                "putUnaligned" + primitiveName + suffix, MethodType.methodType(void.class, long.class, primitiveClass));

        List<BOX> values = new ArrayList<>(ITRS);
        SplittableRandom r = new SplittableRandom(1337L);
        for (int i = 0; i < ITRS; i++) {
            values.add(rng.apply(r));
        }

        byte[] heapScratch = new byte[size * ITRS];
        byte[] heapReference = new byte[size * ITRS];
        ByteBuffer heapReferenceBuffer = ByteBuffer.wrap(heapReference);
        heapReferenceBuffer.order(order);
        MethodHandle bytebuffer_put = MethodHandles.publicLookup().findVirtual(ByteBuffer.class,
                "put" + primitiveName, MethodType.methodType(ByteBuffer.class, primitiveClass));
        for (int i = 0; i < ITRS; i++) {
            bytebuffer_put.invoke(heapReferenceBuffer, values.get(i));
        }

        //put off-heap
        for (int i = 0; i < ITRS; i++) {
            direct_putUnaligned.invoke(address + (long) i * size, values.get(i));
        }

        //get off-heap
        for (int i = 0; i < ITRS; i++) {
            assertEquals("off-heap", values.get(i), direct_getUnaligned.invoke(address + (long) i * size));
        }

        PUnsafe.copyMemory(null, address, heapScratch, PUnsafe.arrayByteBaseOffset(), heapScratch.length);
        assertArrayEquals("off-heap", heapReference, heapScratch);

        //put heap
        for (int i = 0; i < ITRS; i++) {
            heap_putUnaligned.invoke(heapScratch, PUnsafe.arrayByteBaseOffset() + (long) i * size, values.get(i));
        }

        //get off-heap
        for (int i = 0; i < ITRS; i++) {
            assertEquals("on-heap", values.get(i), heap_getUnaligned.invoke(heapScratch, PUnsafe.arrayByteBaseOffset() + (long) i * size));
        }

        assertArrayEquals("on-heap", heapReference, heapScratch);
    }
}
