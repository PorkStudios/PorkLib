/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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
