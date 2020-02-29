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

package net.daporkchop.lib.unsafe.cleaner;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.unsafe.PCleaner;
import net.daporkchop.lib.unsafe.PUnsafe;
import sun.misc.Cleaner;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author DaPorkchop_
 */
@Getter
@SuppressWarnings("unchecked")
public final class SunCleaner extends PCleaner {
    protected static final long     CLEANER_NEXT_OFFSET  = PUnsafe.pork_getOffset(Cleaner.class, "next");
    protected static final long     CLEANER_THUNK_OFFSET = PUnsafe.pork_getOffset(Cleaner.class, "thunk");

    protected static final Predicate<Cleaner> CLEANER_REMOVE;
    protected static final Function<Cleaner, Cleaner>  CLEANER_ADD;

    static {
        try {
            Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
            constructor.setAccessible(true);
            MethodHandles.Lookup lookup =  constructor.newInstance(Cleaner.class, -1);

            PUnsafe.ensureClassInitialized(Cleaner.class);
            PUnsafe.ensureClassInitialized(Predicate.class);

            MethodType targetType = MethodType.methodType(boolean.class, Cleaner.class);
            CallSite site = LambdaMetafactory.metafactory(
                    lookup,
                    "test",
                    MethodType.methodType(Predicate.class),
                    MethodType.methodType(boolean.class, Object.class),
                    lookup.findStatic(Cleaner.class, "remove", targetType),
                    targetType);

            CLEANER_REMOVE = (Predicate<Cleaner>) site.getTarget().invoke();

            PUnsafe.ensureClassInitialized(Function.class);

            targetType = MethodType.methodType(Cleaner.class, Cleaner.class);
            site = LambdaMetafactory.metafactory(
                    lookup,
                    "apply",
                    MethodType.methodType(Function.class),
                    MethodType.methodType(Object.class, Object.class),
                    lookup.findStatic(Cleaner.class, "add", targetType),
                    targetType);

            CLEANER_ADD = (Function<Cleaner, Cleaner>) site.getTarget().invoke();
        } catch (NoClassDefFoundError e)  {
            System.err.println("sun.misc.Cleaner does not exist!");
            e.printStackTrace();
            PUnsafe.throwException(e);
            throw new RuntimeException(e);
        } catch (Throwable e)   {
            throw new RuntimeException("Unable to initialize SunCleaner!", e);
        }
    }

    @NonNull
    private final Cleaner delegate;

    public SunCleaner(@NonNull Object o, @NonNull Runnable cleaner) {
        this.delegate = Cleaner.create(o, cleaner);
    }

    @Override
    public boolean clean() {
        if (CLEANER_REMOVE.test(this.delegate)) {
            try {
                PUnsafe.<Runnable>getObject(this.delegate, CLEANER_THUNK_OFFSET).run();
                PUnsafe.putObject(this.delegate, CLEANER_THUNK_OFFSET, null);
            } catch (Throwable t)   {
                if (System.err != null) {
                    new Error("Cleaner terminated abnormally").printStackTrace();
                }
                System.exit(1);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean hasRun() {
        return PUnsafe.getObject(this.delegate, CLEANER_NEXT_OFFSET) == this.delegate;
    }
}
