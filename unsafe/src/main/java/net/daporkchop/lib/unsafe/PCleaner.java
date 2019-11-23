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

package net.daporkchop.lib.unsafe;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import sun.misc.Cleaner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A wrapper around {@link Cleaner}.
 * <p>
 * Really serves very little purpose except to avoid the otherwise unavoidable "Internal API" warnings at compile-time
 * caused by referencing anything in {@link sun}, but also adds some convenience methods.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public final class PCleaner {
    protected static final long     CLEANER_NEXT_OFFSET  = PUnsafe.pork_getOffset(Cleaner.class, "next");
    protected static final long     CLEANER_THUNK_OFFSET = PUnsafe.pork_getOffset(Cleaner.class, "thunk");
    protected static final Runnable NOOP_RUNNABLE        = () -> {};

    protected static Method CLEANER_REMOVE;

    /**
     * Makes a new cleaner targeting a given object. When that object is garbage collected, the given
     * cleaner function will be executed.
     *
     * @param o       the target object. The cleaner will not run until this object has been garbage collected
     * @param cleaner the function to run once the target object has been garbage collected
     * @return an instance of {@link PCleaner}
     */
    public static PCleaner cleaner(@NonNull Object o, @NonNull Runnable cleaner) {
        return new PCleaner(Cleaner.create(o, cleaner));
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
     * - If you call {@link PUnsafe#reallocateMemory(long, long)}, make sure to replace the cleaner function by using {@link #setCleanTask(Runnable)}
     * - If for whatever reason you cannot do that, invalidate the cleaner using {@link #invalidate()}
     * It is highly advisable to use {@link #cleaner(Object, AtomicLong)} unless you are sure that
     * the address will not change.
     *
     * @param o    the target object. The cleaner will not run until this object has been garbage collected
     * @param addr the address of the memory to free once the target object has been garbage collected
     * @return an instance of {@link PCleaner}
     */
    public static PCleaner cleaner(@NonNull Object o, long addr) {
        return new PCleaner(Cleaner.create(o, () -> PUnsafe.freeMemory(addr)));
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
        return new PCleaner(Cleaner.create(o, () -> {
            long addr = addrRef.getAndSet(-1L);
            if (addr != -1L) {
                PUnsafe.freeMemory(addr);
            }
        }));
    }

    private static boolean remove(Cleaner cl) {
        if (CLEANER_REMOVE == null) {
            try {
                CLEANER_REMOVE = Cleaner.class.getDeclaredMethod("remove", Cleaner.class);
                CLEANER_REMOVE.setAccessible(true);
            } catch (NoSuchMethodException e) {
                PUnsafe.throwException(e);
                throw new RuntimeException(e);
            }
        }
        try {
            return (Boolean) CLEANER_REMOVE.invoke(null, cl);
        } catch (IllegalAccessException | InvocationTargetException e) {
            PUnsafe.throwException(e);
            throw new RuntimeException(e);
        }
    }

    @NonNull
    private final Cleaner delegate;

    /**
     * Runs this cleaner. If this cleaner has already been run, this function does nothing.
     */
    public void clean() {
        this.delegate.clean();
    }

    /**
     * Checks whether or not this cleaner has already been run
     *
     * @return whether or not this cleaner has already been run
     */
    public boolean isCleaned() {
        return PUnsafe.getObject(this.delegate, CLEANER_NEXT_OFFSET) == this.delegate;
    }

    /**
     * Attempts to run this cleaner.
     *
     * @return whether or not the cleaner was run
     */
    public boolean tryClean() {
        if (!remove(this.delegate)) {
            return false;
        } else {
            try {
                PUnsafe.<Runnable>getObject(this.delegate, CLEANER_THUNK_OFFSET).run();
                return true;
            } catch (Throwable t) {
                AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
                    if (System.err != null) {
                        new Error("Cleaner terminated abnormally", t).printStackTrace();
                    }
                    System.exit(1);
                    return null;
                });
                return false;
            }
        }
    }

    /**
     * Sets the function that will be executed when this cleaner runs.
     *
     * @param runnable the cleaner function
     */
    public void setCleanTask(@NonNull Runnable runnable) {
        PUnsafe.putObjectVolatile(this.delegate, CLEANER_THUNK_OFFSET, runnable);
    }

    /**
     * Disables this cleaner by setting the task to an empty function.
     *
     * @see #invalidate()
     */
    public void disable() {
        PUnsafe.putObjectVolatile(this.delegate, CLEANER_THUNK_OFFSET, NOOP_RUNNABLE);
    }

    /**
     * Completely disables a cleaner by first disabling and then running it.
     */
    public void invalidate() {
        this.disable();
        this.clean();
    }
}
