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

package net.daporkchop.lib.unsafe.cleaner;

import lombok.NonNull;
import lombok.SneakyThrows;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * @author DaPorkchop_
 */
public final class Java9Cleaner extends AbstractPCleaner {
    private static final MethodHandle Cleaner_register; // (Cleaner, Object, Runnable) -> Cleaner.Cleanable
    private static final MethodHandle Cleaner$Cleanable_clean; // (Cleaner.Cleanable) -> void

    private static final Object CLEANER_INSTANCE;

    static {
        try {
            Class<?> Cleaner = Class.forName("java.lang.ref.Cleaner");
            Class<?> Cleaner$Cleanable = Class.forName("java.lang.ref.Cleaner$Cleanable");

            Cleaner_register = MethodHandles.publicLookup().findVirtual(Cleaner, "register", MethodType.methodType(Cleaner$Cleanable, Object.class, Runnable.class));
            Cleaner$Cleanable_clean = MethodHandles.publicLookup().findVirtual(Cleaner$Cleanable, "clean", MethodType.methodType(void.class));

            CLEANER_INSTANCE = MethodHandles.publicLookup().findStatic(Cleaner, "create", MethodType.methodType(Cleaner)).invoke();
        } catch (Throwable t) {
            throw PUnsafe.throwException(t);
        }
    }

    private final Object cleanable;

    @SneakyThrows
    public Java9Cleaner(@NonNull Object o, @NonNull Runnable action) {
        super(action);
        this.cleanable = Cleaner_register.invoke(CLEANER_INSTANCE, o, this);
    }

    @Override
    @SneakyThrows
    public void clean() {
        Cleaner$Cleanable_clean.invoke(this.cleanable);
    }
}
