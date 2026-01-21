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

package net.daporkchop.lib.collections.collection;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.collections.collection.lock.AutoLockedCollection;
import net.daporkchop.lib.collections.collection.lock.DefaultLockedCollection;
import net.daporkchop.lib.collections.collection.lock.LockedCollection;
import net.daporkchop.lib.collections.collection.rw.AutoReadWriteCollection;
import net.daporkchop.lib.collections.collection.rw.DefaultReadWriteCollection;
import net.daporkchop.lib.collections.collection.rw.ReadWriteCollection;

import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Helper methods for {@link Collection}.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PCollections {
    public static <V> LockedCollection<V> locked(@NonNull Collection<V> collection, boolean lockAutomatically) {
        return lockAutomatically ? lockedAuto(collection) : locked(collection);
    }

    public static <V> LockedCollection<V> locked(@NonNull Collection<V> collection) {
        return new DefaultLockedCollection<>(collection);
    }

    public static <V> LockedCollection<V> lockedAuto(@NonNull Collection<V> collection) {
        return new AutoLockedCollection<>(collection);
    }

    public static <V> LockedCollection<V> locked(@NonNull Collection<V> collection, @NonNull Lock lock, boolean lockAutomatically) {
        return lockAutomatically ? lockedAuto(collection, lock) : locked(collection, lock);
    }

    public static <V> LockedCollection<V> locked(@NonNull Collection<V> collection, @NonNull Lock lock) {
        return new DefaultLockedCollection<>(collection, lock);
    }

    public static <V> LockedCollection<V> lockedAuto(@NonNull Collection<V> collection, @NonNull Lock lock) {
        return new AutoLockedCollection<>(collection, lock);
    }

    public static <V> ReadWriteCollection<V> readWrite(@NonNull Collection<V> collection, boolean lockAutomatically) {
        return lockAutomatically ? readWriteAuto(collection) : readWrite(collection);
    }

    public static <V> ReadWriteCollection<V> readWrite(@NonNull Collection<V> collection) {
        return new DefaultReadWriteCollection<>(collection);
    }

    public static <V> ReadWriteCollection<V> readWriteAuto(@NonNull Collection<V> collection) {
        return new AutoReadWriteCollection<>(collection);
    }

    public static <V> ReadWriteCollection<V> readWrite(@NonNull Collection<V> collection, @NonNull ReadWriteLock lock, boolean lockAutomatically) {
        return lockAutomatically ? readWriteAuto(collection, lock) : readWrite(collection, lock);
    }

    public static <V> ReadWriteCollection<V> readWrite(@NonNull Collection<V> collection, @NonNull ReadWriteLock lock) {
        return new DefaultReadWriteCollection<>(collection, lock);
    }

    public static <V> ReadWriteCollection<V> readWriteAuto(@NonNull Collection<V> collection, @NonNull ReadWriteLock lock) {
        return new AutoReadWriteCollection<>(collection, lock);
    }

    public static <V> ReadWriteCollection<V> readWrite(@NonNull Collection<V> collection, @NonNull Lock readLock, @NonNull Lock writeLock, boolean lockAutomatically) {
        return lockAutomatically ? readWriteAuto(collection, readLock, writeLock) : readWrite(collection, readLock, writeLock);
    }

    public static <V> ReadWriteCollection<V> readWrite(@NonNull Collection<V> collection, @NonNull Lock readLock, @NonNull Lock writeLock) {
        return new DefaultReadWriteCollection<>(collection, readLock, writeLock);
    }

    public static <V> ReadWriteCollection<V> readWriteAuto(@NonNull Collection<V> collection, @NonNull Lock readLock, @NonNull Lock writeLock) {
        return new AutoReadWriteCollection<>(collection, readLock, writeLock);
    }
}
