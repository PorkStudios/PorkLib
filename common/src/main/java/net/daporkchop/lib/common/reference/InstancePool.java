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

package net.daporkchop.lib.common.reference;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.util.PConstants;
import net.daporkchop.lib.common.util.PorkUtil;
import sun.misc.SoftCache;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A pool for sharing instances of classes that only really need one instance to be around
 * at a time.
 * <p>
 * Having these instances pooled is better than creating new instances all the time (because
 * heap usage) and is also better than having a single static instance (because the class can
 * never be unloaded in that scenario). This pool is backed by a {@link SoftCache}, meaning that
 * only one object instance has to be kept loaded statically.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unchecked")
public class InstancePool {
    protected static final Map<Class<?>, Object> MAP = PorkUtil.newSoftCache();
    protected static final ReadWriteLock LOCK = new ReentrantReadWriteLock();

    public static <T> T getInstance(@NonNull Class<T> clazz) {
        T val;
        LOCK.readLock().lock();
        try {
            val = (T) MAP.get(clazz);
        } finally {
            LOCK.readLock().unlock();
        }
        if (val == null) {
            try {
                Constructor<T> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                val = constructor.newInstance();
                LOCK.writeLock().lock();
                try {
                    MAP.putIfAbsent(clazz, val);
                } finally {
                    LOCK.writeLock().unlock();
                }
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(String.format("Class %s doesn't have a no-args constructor!", clazz.getCanonicalName()), e);
            } catch (IllegalAccessException e) {
                throw PConstants.p_exception(e); //not possible
            } catch (InstantiationException | InvocationTargetException e) {
                throw new IllegalStateException(String.format("Exception while invoking no-args constructor on class %s!", clazz.getCanonicalName()), e);
            }
        }
        return val;
    }
}
