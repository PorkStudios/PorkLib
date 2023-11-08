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

package net.daporkchop.lib.unsafe.cleaner;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.unsafe.PCleaner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ThreadFactory;

/**
 * Implementation of {@link PCleaner} for Java versions greater than 9.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public final class Java9Cleaner extends PCleaner implements Runnable {
    private static final Class<?> CLEANER_CLASS;
    private static final Class<?> CLEANABLE_CLASS;

    private static final Method CLEANER_REGISTER;
    private static final Method CLEANABLE_RUN;

    private static final Object CLEANER_INSTANCE;

    static {
        try {
            CLEANER_CLASS = Class.forName("java.lang.ref.Cleaner");
            CLEANABLE_CLASS = Class.forName("java.lang.ref.Cleaner$Cleanable");

            CLEANER_REGISTER = CLEANER_CLASS.getDeclaredMethod("register", Object.class, Runnable.class);
            CLEANABLE_RUN = CLEANABLE_CLASS.getDeclaredMethod("clean");

            Method create = CLEANER_CLASS.getDeclaredMethod("create", ThreadFactory.class);
            CLEANER_INSTANCE = create.invoke(null, (ThreadFactory) r -> new Thread(r, "PorkLib cleaner thread"));
        } catch (Throwable e) {
            throw new RuntimeException("Unable to initialize Java9Cleaner!", e);
        }
    }

    private final Object   cleanable;
    private       Runnable thunk;

    @Getter
    private       boolean  hasRun;

    public Java9Cleaner(@NonNull Object o, @NonNull Runnable cleaner) {
        this.thunk = cleaner;

        try {
            this.cleanable = CLEANER_REGISTER.invoke(CLEANER_INSTANCE, o, this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Couldn't register cleaner!", e);
        }
    }

    @Override
    public synchronized void run() {
        if (this.hasRun) {
            throw new IllegalStateException("Cleaner already run!");
        }

        try {
            this.thunk.run();
        } finally {
            this.thunk = null;
            this.hasRun = true;
        }
    }

    @Override
    public synchronized boolean clean() {
        if (!this.hasRun) {
            try {
                CLEANABLE_RUN.invoke(this.cleanable);
                if (!this.hasRun) {
                    throw new IllegalStateException("Cleaner didn't run!");
                }
                return true;
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Couldn't run cleaner!", e);
            }
        }
        return false;
    }
}
