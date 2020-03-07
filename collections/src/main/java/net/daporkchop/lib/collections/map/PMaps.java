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

package net.daporkchop.lib.collections.map;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.collections.map.lock.AutoLockedMap;
import net.daporkchop.lib.collections.map.lock.DefaultLockedMap;
import net.daporkchop.lib.collections.map.lock.LockedMap;
import net.daporkchop.lib.collections.map.rw.AutoReadWriteMap;
import net.daporkchop.lib.collections.map.rw.DefaultReadWriteMap;
import net.daporkchop.lib.collections.map.rw.ReadWriteMap;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Helper methods for dealing with maps.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PMaps {
    public static <K, V> LockedMap<K, V> locked(@NonNull Map<K, V> map, boolean lockAutomatically) {
        return lockAutomatically ? lockedAuto(map) : locked(map);
    }

    public static <K, V> LockedMap<K, V> locked(@NonNull Map<K, V> map) {
        return new DefaultLockedMap<>(map);
    }

    public static <K, V> LockedMap<K, V> lockedAuto(@NonNull Map<K, V> map) {
        return new AutoLockedMap<>(map);
    }

    public static <K, V> LockedMap<K, V> locked(@NonNull Map<K, V> map, @NonNull Lock lock, boolean lockAutomatically) {
        return lockAutomatically ? lockedAuto(map, lock) : locked(map, lock);
    }

    public static <K, V> LockedMap<K, V> locked(@NonNull Map<K, V> map, @NonNull Lock lock) {
        return new DefaultLockedMap<>(map, lock);
    }

    public static <K, V> LockedMap<K, V> lockedAuto(@NonNull Map<K, V> map, @NonNull Lock lock) {
        return new AutoLockedMap<>(map, lock);
    }

    public static <K, V>ReadWriteMap<K, V> readWrite(@NonNull Map<K, V> map, boolean lockAutomatically) {
        return lockAutomatically ? readWriteAuto(map) : readWrite(map);
    }

    public static <K, V>ReadWriteMap<K, V> readWrite(@NonNull Map<K, V> map) {
        return new DefaultReadWriteMap<>(map);
    }

    public static <K, V>ReadWriteMap<K, V> readWriteAuto(@NonNull Map<K, V> map) {
        return new AutoReadWriteMap<>(map);
    }

    public static <K, V>ReadWriteMap<K, V> readWrite(@NonNull Map<K, V> map, @NonNull ReadWriteLock lock, boolean lockAutomatically) {
        return lockAutomatically ? readWriteAuto(map, lock) : readWrite(map, lock);
    }

    public static <K, V>ReadWriteMap<K, V> readWrite(@NonNull Map<K, V> map, @NonNull ReadWriteLock lock) {
        return new DefaultReadWriteMap<>(map, lock);
    }

    public static <K, V>ReadWriteMap<K, V> readWriteAuto(@NonNull Map<K, V> map, @NonNull ReadWriteLock lock) {
        return new AutoReadWriteMap<>(map, lock);
    }

    public static <K, V>ReadWriteMap<K, V> readWrite(@NonNull Map<K, V> map, @NonNull Lock readLock, @NonNull Lock writeLock, boolean lockAutomatically) {
        return lockAutomatically ? readWriteAuto(map, readLock, writeLock) : readWrite(map, readLock, writeLock);
    }

    public static <K, V>ReadWriteMap<K, V> readWrite(@NonNull Map<K, V> map, @NonNull Lock readLock, @NonNull Lock writeLock) {
        return new DefaultReadWriteMap<>(map, readLock, writeLock);
    }

    public static <K, V>ReadWriteMap<K, V> readWriteAuto(@NonNull Map<K, V> map, @NonNull Lock readLock, @NonNull Lock writeLock) {
        return new AutoReadWriteMap<>(map, readLock, writeLock);
    }
}
