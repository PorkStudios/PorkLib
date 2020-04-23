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

package net.daporkchop.lib.collections.set;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.collections.set.lock.AutoLockedSet;
import net.daporkchop.lib.collections.set.lock.DefaultLockedSet;
import net.daporkchop.lib.collections.set.lock.LockedSet;
import net.daporkchop.lib.collections.set.rw.AutoReadWriteSet;
import net.daporkchop.lib.collections.set.rw.DefaultReadWriteSet;
import net.daporkchop.lib.collections.set.rw.ReadWriteSet;

import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * @author DaPorkchop_
 */
@UtilityClass
public class PSets {
    public static <V> LockedSet<V> locked(@NonNull Set<V> set, boolean lockAutomatically) {
        return lockAutomatically ? lockedAuto(set) : locked(set);
    }

    public static <V> LockedSet<V> locked(@NonNull Set<V> set) {
        return new DefaultLockedSet<>(set);
    }

    public static <V> LockedSet<V> lockedAuto(@NonNull Set<V> set) {
        return new AutoLockedSet<>(set);
    }

    public static <V> LockedSet<V> locked(@NonNull Set<V> set, @NonNull Lock lock, boolean lockAutomatically) {
        return lockAutomatically ? lockedAuto(set, lock) : locked(set, lock);
    }

    public static <V> LockedSet<V> locked(@NonNull Set<V> set, @NonNull Lock lock) {
        return new DefaultLockedSet<>(set, lock);
    }

    public static <V> LockedSet<V> lockedAuto(@NonNull Set<V> set, @NonNull Lock lock) {
        return new AutoLockedSet<>(set, lock);
    }

    public static <V> ReadWriteSet<V> readWrite(@NonNull Set<V> set, boolean lockAutomatically) {
        return lockAutomatically ? readWriteAuto(set) : readWrite(set);
    }

    public static <V> ReadWriteSet<V> readWrite(@NonNull Set<V> set) {
        return new DefaultReadWriteSet<>(set);
    }

    public static <V> ReadWriteSet<V> readWriteAuto(@NonNull Set<V> set) {
        return new AutoReadWriteSet<>(set);
    }

    public static <V> ReadWriteSet<V> readWrite(@NonNull Set<V> set, @NonNull ReadWriteLock lock, boolean lockAutomatically) {
        return lockAutomatically ? readWriteAuto(set, lock) : readWrite(set, lock);
    }

    public static <V> ReadWriteSet<V> readWrite(@NonNull Set<V> set, @NonNull ReadWriteLock lock) {
        return new DefaultReadWriteSet<>(set, lock);
    }

    public static <V> ReadWriteSet<V> readWriteAuto(@NonNull Set<V> set, @NonNull ReadWriteLock lock) {
        return new AutoReadWriteSet<>(set, lock);
    }

    public static <V> ReadWriteSet<V> readWrite(@NonNull Set<V> set, @NonNull Lock readLock, @NonNull Lock writeLock, boolean lockAutomatically) {
        return lockAutomatically ? readWriteAuto(set, readLock, writeLock) : readWrite(set, readLock, writeLock);
    }

    public static <V> ReadWriteSet<V> readWrite(@NonNull Set<V> set, @NonNull Lock readLock, @NonNull Lock writeLock) {
        return new DefaultReadWriteSet<>(set, readLock, writeLock);
    }

    public static <V> ReadWriteSet<V> readWriteAuto(@NonNull Set<V> set, @NonNull Lock readLock, @NonNull Lock writeLock) {
        return new AutoReadWriteSet<>(set, readLock, writeLock);
    }
}
