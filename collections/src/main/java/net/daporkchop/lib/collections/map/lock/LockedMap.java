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

package net.daporkchop.lib.collections.map.lock;

import net.daporkchop.lib.collections.collection.lock.LockedCollection;
import net.daporkchop.lib.collections.set.lock.LockedSet;
import net.daporkchop.lib.common.misc.LockableResource;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;

/**
 * A {@link Map} that uses a {@link Lock} to manage access to it rather than synchronizing on a mutex object.
 * <p>
 * Unlike a {@link Map} returned by {@link java.util.Collections#synchronizedMap(Map)}, of this interface are NOT expected to automatically lock and
 * unlock for every method invocation. This allows locking to be done manually by the user, providing performance in exchange for possible lack of
 * safety when used incorrectly.
 *
 * @author DaPorkchop_
 */
public interface LockedMap<K, V> extends Map<K, V>, LockableResource {
    @Override
    LockedSet<K> keySet();

    @Override
    LockedCollection<V> values();

    @Override
    LockedSet<Entry<K, V>> entrySet();

    @Override
    LockedMap<K, V> lockAndGet();

    @Override
    LockedMap<K, V> lockAndGetInterruptibly() throws InterruptedException;
}
