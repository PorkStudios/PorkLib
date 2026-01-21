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

package net.daporkchop.lib.unsafe;

import lombok.NonNull;
import net.daporkchop.lib.unsafe.cleaner.Java9Cleaner;
import net.daporkchop.lib.unsafe.cleaner.SunCleaner;
import sun.misc.Cleaner;

import java.util.function.BiFunction;

/**
 * A wrapper around {@link Cleaner}.
 * <p>
 * Really serves very little purpose except to avoid the otherwise unavoidable "Internal API" warnings at compile-time
 * caused by referencing anything in {@link sun}, but also adds some convenience methods.
 *
 * @author DaPorkchop_
 */
public abstract class PCleaner {
    @SuppressWarnings("StaticInitializerReferencesSubClass")
    private static final BiFunction<Object, Runnable, PCleaner> CLEANER_PROVIDER = UnsafePlatformInfo.JAVA_VERSION <= 8 ? SunCleaner::new : Java9Cleaner::new;

    /**
     * Makes a new cleaner targeting a given object. When that object is garbage collected, the given
     * cleaner function will be executed.
     *
     * @param o      the target object. The cleaner will not run until this object has been garbage collected
     * @param action the function to run once the target object has been garbage collected
     * @return an instance of {@link PCleaner}
     */
    public static PCleaner cleaner(@NonNull Object o, @NonNull Runnable action) {
        return CLEANER_PROVIDER.apply(o, action);
    }

    /**
     * Gets a {@link Runnable} which will call {@link PUnsafe#freeMemory(long)} on the given address parameter when {@link Runnable#run() run}.
     *
     * @param addr the memory address to free
     * @return a {@link Runnable}
     */
    public static Runnable freeDirectMemoryAction(long addr) {
        return () -> PUnsafe.freeMemory(addr);
    }

    /**
     * Makes a new cleaner targeting a given object. When that object is garbage collected, the given
     * memory address will be freed (i.e. {@link PUnsafe#freeMemory(long)} will be invoked, with the
     * given address passed as the parameter).
     *
     * @param o    the target object. The cleaner will not run until this object has been garbage collected
     * @param addr the address of the memory to free once the target object has been garbage collected
     * @return an instance of {@link PCleaner}
     */
    public static PCleaner cleaner(@NonNull Object o, long addr) {
        return cleaner(o, freeDirectMemoryAction(addr));
    }

    /**
     * Runs this cleaner.
     * <p>
     * If this cleaner has already been run or has been {@link #cancel() cancelled}, this method does nothing.
     */
    public abstract void clean();

    /**
     * Cancel this cleaner.
     * <p>
     * If this cleaner has already been run or has been {@link #cancel() cancelled}, this method does nothing.
     * <p>
     * If it hasn't been run yet, the cleaner will not be run in the future by a subsequent call to {@link #clean()} or when the target object is garbage collected.
     */
    public abstract void cancel();

    /**
     * Replaces this cleaner's action with a different action.
     * <p>
     * The previous action will be returned without being run.
     *
     * @return the previous action
     * @throws IllegalStateException if this cleaner has already been run or has been {@link #cancel() cancelled}
     */
    public abstract Runnable replace(@NonNull Runnable action);
}
