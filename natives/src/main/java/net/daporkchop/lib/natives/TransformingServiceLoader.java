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
 */

package net.daporkchop.lib.natives;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.natives.util.exception.NoFeatureImplementationsFoundError;

import java.lang.invoke.MethodHandles;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author DaPorkchop_
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public final class TransformingServiceLoader<SERVICE, RESULT> implements Iterable<RESULT> {
    public static <SERVICE> TransformingServiceLoaderBuilder<SERVICE, SERVICE> builder(@NonNull MethodHandles.Lookup lookup, @NonNull Class<SERVICE> serviceClass) {
        return builder(lookup, serviceClass, lookup.lookupClass().getClassLoader());
    }

    public static <SERVICE> TransformingServiceLoaderBuilder<SERVICE, SERVICE> builder(@NonNull MethodHandles.Lookup lookup, @NonNull Class<SERVICE> serviceClass, @NonNull ClassLoader loader) {
        return new TransformingServiceLoaderBuilder<>(lookup, serviceClass, loader, Function.identity());
    }

    final @NonNull MethodHandles.Lookup lookup;
    final @NonNull Class<SERVICE> serviceClass;
    final @NonNull ClassLoader loader;

    /**
     * A filter which, if set, is used to determine which service implementations to exclude based on their class. Service implementation classes for which it returns
     * {@code false} will be skipped.
     * <p>
     * This filter <strong>may</strong> be tested before an instance of the class is constructed, resulting in potential performance benefits if the filter only accesses
     * information accessible via class metadata (e.g. from the class name, or using annotations).
     */
    final Predicate<? super Class<?>> classFilter;
    /**
     * A filter which, if set, is used to determine which service implementations to exclude. Service implementations for which it returns {@code false} will be skipped.
     */
    final Predicate<? super SERVICE> instanceFilter;

    /**
     * A function which will be applied to an instance of a service implementation to transform it into the result type immediately before it is returned.
     */
    final @NonNull Function<? super SERVICE, ? extends RESULT> resultMapper;

    public RESULT findFirst() throws NoFeatureImplementationsFoundError {
        NoFeatureImplementationsFoundError root = null;
        for (Iterator<SERVICE> itr = ServiceLoader.load(this.serviceClass, this.loader).iterator(); ; ) {
            try {
                if (!itr.hasNext()) {
                    break;
                }

                SERVICE instance = itr.next();

                //check if the implementation class filter matches
                //TODO: on Java 9+ we could perform this test before constructing the service instance
                if (this.classFilter != null && !this.classFilter.test(instance.getClass())) {
                    continue;
                }

                //check if the implementation instance filter matches
                if (this.instanceFilter != null && !this.instanceFilter.test(instance)) {
                    continue;
                }

                return this.resultMapper.apply(instance);
            } catch (ServiceConfigurationError e) {
                if (Boolean.getBoolean("porklib.native.printStackTraces")) {
                    e.printStackTrace();
                }

                if (root == null) {
                    root = new NoFeatureImplementationsFoundError("all implementations failed to load: " + this.serviceClass.getName());
                }
                root.addSuppressed(e);
            }
        }

        if (root == null) {
            root = new NoFeatureImplementationsFoundError("no implementations found: " + this.serviceClass.getName());
        }
        throw root;
    }

    /**
     * Gets an {@link Iterator} over all loadable services.
     * <p>
     * The returned {@link Iterator} will ignore any {@link ServiceConfigurationError}s thrown while constructing a service instance.
     *
     * @return an {@link Iterator} over all loadable services
     */
    @Override
    public Iterator<RESULT> iterator() {
        return new Iterator<RESULT>() {
            final Iterator<SERVICE> serviceIterator = ServiceLoader.load(TransformingServiceLoader.this.serviceClass, TransformingServiceLoader.this.loader).iterator();

            RESULT next;

            @Override
            public boolean hasNext() {
                while (this.next == null && this.serviceIterator.hasNext()) {
                    try {
                        //TODO: decide if we should bother wrapping the check for hasNext() with a try-catch

                        SERVICE instance = this.serviceIterator.next();

                        //check if the implementation class filter matches
                        //TODO: on Java 9+ we could perform this test before constructing the service instance
                        if (TransformingServiceLoader.this.classFilter != null && !TransformingServiceLoader.this.classFilter.test(instance.getClass())) {
                            continue;
                        }

                        //check if the implementation instance filter matches
                        if (TransformingServiceLoader.this.instanceFilter != null && !TransformingServiceLoader.this.instanceFilter.test(instance)) {
                            continue;
                        }

                        this.next = TransformingServiceLoader.this.resultMapper.apply(instance);
                        return true;
                    } catch (ServiceConfigurationError e) {
                        if (Boolean.getBoolean("porklib.native.printStackTraces")) {
                            e.printStackTrace();
                        }
                        //keep iterating
                    }
                }
                return this.next != null;
            }

            @Override
            public RESULT next() {
                if (this.next == null && !this.hasNext()) {
                    throw new NoSuchElementException();
                }

                val result = this.next;
                this.next = null;
                return result;
            }
        };
    }

    /**
     * @author DaPorkchop_
     */
    @AllArgsConstructor(access = AccessLevel.PACKAGE)
    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    @Setter
    @Accessors(fluent = true, chain = true)
    public static final class TransformingServiceLoaderBuilder<SERVICE, RESULT> {
        private final @NonNull MethodHandles.Lookup lookup;
        private final @NonNull Class<SERVICE> serviceClass;
        private final @NonNull ClassLoader loader;

        /**
         * @see TransformingServiceLoader#classFilter
         */
        private Predicate<? super Class<?>> classFilter;
        /**
         * @see TransformingServiceLoader#instanceFilter
         */
        private Predicate<? super SERVICE> instanceFilter;

        /**
         * @see TransformingServiceLoader#resultMapper
         */
        private @NonNull Function<? super SERVICE, ? extends RESULT> resultMapper;

        public <NEW_RESULT> TransformingServiceLoaderBuilder<SERVICE, NEW_RESULT> resultMapper(@NonNull Function<? super SERVICE, ? extends NEW_RESULT> resultMapper) {
            this.resultMapper = PorkUtil.uncheckedCast(resultMapper);
            return PorkUtil.uncheckedCast(this);
        }

        public TransformingServiceLoader<SERVICE, RESULT> build() {
            return new TransformingServiceLoader<>(
                    this.lookup, this.serviceClass, this.loader,
                    this.classFilter, this.instanceFilter,
                    this.resultMapper);
        }
    }
}
