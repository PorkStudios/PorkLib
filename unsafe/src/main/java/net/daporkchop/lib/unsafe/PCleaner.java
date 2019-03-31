package net.daporkchop.lib.unsafe;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import sun.misc.Cleaner;

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
    protected static final long CLEANER_NEXT_OFFSET = PUnsafe.pork_getOffset(Cleaner.class, "next");
    protected static final long CLEANER_THUNK_OFFSET = PUnsafe.pork_getOffset(Cleaner.class, "thunk");
    protected static final Runnable NULL_RUNNABLE = () -> {
    };

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
     * be invoked, with the address passed as the parameter), and the address reference set to {@code -1L}.
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

    @NonNull
    protected final Cleaner delegate;

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
     * Sets the function that will be executed when this cleaner runs.
     *
     * @param runnable the cleaner function
     */
    public synchronized void setCleanTask(@NonNull Runnable runnable) {
        PUnsafe.putObjectVolatile(this.delegate, CLEANER_THUNK_OFFSET, runnable);
    }

    /**
     * Disables this cleaner by setting the task to an empty function.
     *
     * @see #invalidate()
     */
    public synchronized void disable() {
        PUnsafe.putObjectVolatile(this.delegate, CLEANER_THUNK_OFFSET, NULL_RUNNABLE);
    }

    /**
     * Completely disables a cleaner by first disabling and then running it.
     */
    public synchronized void invalidate() {
        this.disable();
        this.clean();
    }
}
