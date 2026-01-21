/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2025 DaPorkchop_
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

package net.daporkchop.lib.common.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.misc.classvalue.PClassValue;
import net.daporkchop.lib.common.system.PlatformInfo;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Helper methods for working with {@link java.lang.invoke.MethodHandle}s.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PMethodHandles {
    private static final class ArrayConstructor_Java9 {
        static final MethodHandle MethodHandles_arrayConstructor; // (Class) -> MethodHandle

        static {
            try {
                MethodHandles_arrayConstructor = MethodHandles.publicLookup()
                        .findStatic(MethodHandles.class, "arrayConstructor", MethodType.methodType(MethodHandle.class, Class.class));
            } catch (Throwable t) {
                throw PorkUtil.throwUnchecked(t);
            }
        }
    }

    private static final class ArrayConstructor_Legacy {
        static final MethodHandle Array_newInstance; // (Class, int) -> Object

        static {
            try {
                Array_newInstance = MethodHandles.publicLookup()
                        .findStatic(Array.class, "newInstance", MethodType.methodType(Object.class, Class.class, int.class));
            } catch (Throwable t) {
                throw PorkUtil.throwUnchecked(t);
            }
        }

        //TODO: this should probably have weak values
        static final PClassValue<MethodHandle> CACHE = PClassValue.create(arrayClass -> {
            checkArg(arrayClass.isArray(), arrayClass);

            return ArrayConstructor_Legacy.Array_newInstance
                    .bindTo(arrayClass.getComponentType())
                    .asType(MethodType.methodType(arrayClass, int.class));
        });
    }

    /**
     * Produces a method handle constructing arrays of a desired type, as if by the anewarray bytecode. The return type of the method handle will be the array type. The type of its sole argument will be int, which specifies the size of the array.
     * <p>
     * If the returned method handle is invoked with a negative array size, a NegativeArraySizeException will be thrown.
     *
     * @param arrayClass the array class
     * @return a method handle which can create arrays of the given type
     */
    @SneakyThrows
    public static MethodHandle arrayConstructor(Class<?> arrayClass) {
        if (PlatformInfo.JAVA_VERSION >= 9) {
            //invoke MethodHandles.arrayConstructor() directly if it's available
            return (MethodHandle) ArrayConstructor_Java9.MethodHandles_arrayConstructor.invokeExact(arrayClass);
        } else {
            //fallback:
            return ArrayConstructor_Legacy.CACHE.get(arrayClass);
        }
    }
}
