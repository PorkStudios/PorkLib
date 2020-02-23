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

package net.daporkchop.lib.unsafe;

import lombok.NonNull;
import net.daporkchop.lib.unsafe.cleaner.Java9Cleaner;
import net.daporkchop.lib.unsafe.cleaner.SunCleaner;
import sun.misc.Cleaner;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
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
    private static final BiFunction<Object, Runnable, PCleaner> CLEANER_PROVIDER;

    static {
        int[] version = Arrays.stream(System.getProperty("java.specification.version", "1.6").split("\\.")).mapToInt(Integer::parseInt).toArray();
        int javaVersion = version[0] == 1 ? version[1] : version[0];

        CLEANER_PROVIDER = javaVersion <= 8 ? SunCleaner::new : Java9Cleaner::new;
    }

    /**
     * Makes a new cleaner targeting a given object. When that object is garbage collected, the given
     * cleaner function will be executed.
     *
     * @param o       the target object. The cleaner will not run until this object has been garbage collected
     * @param cleaner the function to run once the target object has been garbage collected
     * @return an instance of {@link PCleaner}
     */
    public static PCleaner cleaner(@NonNull Object o, @NonNull Runnable cleaner) {
        return CLEANER_PROVIDER.apply(o, cleaner);
    }

    /**
     * Makes a new cleaner targeting a given object. When that object is garbage collected, the given
     * memory address will be freed (i.e. {@link PUnsafe#freeMemory(long)} will be invoked, with the
     * given address passed as the parameter).
     * <p>
     * CAUTION!
     * This can be dangerous to use if the target address has a possibility of being changed (i.e.
     * via {@link PUnsafe#reallocateMemory(long, long)}) or being freed (i.e. via {@link PUnsafe#freeMemory(long)})
     * before the cleaner runs. Calling those methods before the cleaner runs will cause addr to no
     * longer be a valid pointer to an allocated memory block, and when the cleaner does run, the results
     * are undefined. If you plan to do something similar, make sure to do the following:
     * - Instead of calling {@link PUnsafe#freeMemory(long)}, use {@link #clean()}
     * It is highly advisable to use {@link #cleaner(Object, AtomicLong)} unless you are sure that
     * the address will not change.
     *
     * @param o    the target object. The cleaner will not run until this object has been garbage collected
     * @param addr the address of the memory to free once the target object has been garbage collected
     * @return an instance of {@link PCleaner}
     */
    public static PCleaner cleaner(@NonNull Object o, long addr) {
        return CLEANER_PROVIDER.apply(o, () -> PUnsafe.freeMemory(addr));
    }

    /**
     * Makes a new cleaner targeting a given object. When that object is garbage collected, the memory
     * address contained within the given {@link AtomicLong} will be freed (i.e. {@link PUnsafe#freeMemory(long)} will
     * be invoked, with the address passed as the parameter), and the pos reference set to {@code -1L}.
     * If, however, the address is already set to {@code -1L}, no action will be taken. This is preferable
     * over {@link #cleaner(Object, long)} in scenarios where the memory may be freed in advance due to the
     * fact that the address may be modified and removed without needing to update the cleaner.
     *
     * @param o       the target object. The cleaner will not run until this object has been garbage collected
     * @param addrRef a reference to the address of the memory to free once the target object has
     *                been garbage collected
     * @return an instance of {@link PCleaner}
     */
    public static PCleaner cleaner(@NonNull Object o, @NonNull AtomicLong addrRef) {
        return CLEANER_PROVIDER.apply(o, () -> {
            long addr = addrRef.getAndSet(-1L);
            if (addr > 0L) {
                PUnsafe.freeMemory(addr);
            }
        });
    }

    /**
     * Runs this cleaner. If this cleaner has already been run, this function does nothing.
     *
     * @return whether or not the cleaner was run
     */
    public abstract boolean clean();

    /**
     * @return whether or not this cleaner has already been run
     */
    public abstract boolean hasRun();
}
