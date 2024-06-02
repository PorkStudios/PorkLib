/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2024 DaPorkchop_
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

package net.daporkchop.lib.common.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.function.throwing.TConsumer;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Helper methods for working with closeable resources.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class ResourceUtil {
    /**
     * {@link AutoCloseable#close() Closes} the provided object.
     * <p>
     * If the provided object is {@code null}, it is ignored.
     *
     * @param toClose the object to close
     * @throws Exception if an exception occurs while closing the object
     */
    public static void close(AutoCloseable toClose) throws Exception {
        if (toClose != null) {
            toClose.close();
        }
    }

    /**
     * {@link AutoCloseable#close() Closes} the provided object.
     * <p>
     * If the provided object is {@code null}, it is ignored.
     * <p>
     * Unlike {@link #close(AutoCloseable)}, this function does not throw exceptions. Instead, it catches them and adds them to the provided "root" exception. This
     * is intended to be used when cleaning up resources after an exception has already been thrown, so that the original exception doesn't get hidden by any
     * exceptions throwing while closing the values.
     *
     * @param root    the root exception. Any exceptions thrown while closing the object will be added to the root exception as
     *                a suppressed exception
     * @param toClose the object to close
     * @return the given root exception
     */
    public static <T extends Throwable> T closeSuppressed(T root, AutoCloseable toClose) {
        try {
            close(toClose);
        } catch (Throwable t) {
            root.addSuppressed(t);
        }

        return root;
    }

    /**
     * {@link AutoCloseable#close() Closes} all of the provided objects.
     * <p>
     * Any {@code null} objects will be ignored.
     * <p>
     * This will attempt to close every object in the given {@link Iterable}, even if one of them throws an exception.
     *
     * @param toClose the objects to close
     * @throws Exception if an exception occurs while closing the objects
     */
    @SuppressWarnings({"RedundantThrows", "ForLoopReplaceableByForEach"})
    public static void closeAll(Iterable<? extends AutoCloseable> toClose) throws Exception {
        if (toClose == null) {
            return;
        }

        Throwable root = null;
        try {
            for (Iterator<? extends AutoCloseable> itr = toClose.iterator(); itr.hasNext(); ) { //iterate over all the values
                AutoCloseable value = itr.next();
                if (value != null) { //the value is non-null, try to close it
                    try {
                        value.close();
                    } catch (Throwable t) { //there was an exception, save it for later
                        if (root == null) { //this is the first exception which has occurred
                            root = t;
                        } else { //add the exception onto the first exception
                            root.addSuppressed(t);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            //we could end up here if the iterator throws an exception
            if (root != null) {
                t.addSuppressed(root);
            }
            throw PorkUtil.throwUnchecked(t);
        }

        if (root != null) { //at least one value threw an exception while being closed, rethrow it
            throw PorkUtil.throwUnchecked(root);
        }
    }

    /**
     * {@link AutoCloseable#close() Closes} all of the provided objects.
     * <p>
     * Any {@code null} objects will be ignored.
     * <p>
     * This will attempt to close every object in the given {@link Iterable}, even if one of them throws an exception.
     * <p>
     * Unlike {@link #closeAll(Iterable)}, this function does not throw exceptions. Instead, it catches them and adds them to the provided "root" exception. This
     * is intended to be used when cleaning up resources after an exception has already been thrown, so that the original exception doesn't get hidden by any
     * exceptions throwing while closing the values.
     *
     * @param root    the root exception. Any exceptions thrown while closing the objects will be added to the root exception as
     *                suppressed exceptions
     * @param toClose the objects to close
     * @return the given root exception
     */
    public static <T extends Throwable> T closeAllSuppressed(T root, Iterable<? extends AutoCloseable> toClose) {
        try {
            closeAll(toClose);
        } catch (Throwable t) {
            root.addSuppressed(t);
        }

        return root;
    }

    /**
     * {@link AutoCloseable#close() Closes} all of the provided objects.
     * <p>
     * Any {@code null} objects will be ignored.
     * <p>
     * This will attempt to close every object in the given array, even if one of them throws an exception.
     *
     * @param toClose the objects to close
     * @throws Exception if an exception occurs while closing the objects
     */
    public static void closeAll(AutoCloseable... toClose) throws Exception {
        if (toClose != null) {
            closeAll(Arrays.asList(toClose));
        }
    }

    /**
     * {@link AutoCloseable#close() Closes} all of the provided objects.
     * <p>
     * Any {@code null} objects will be ignored.
     * <p>
     * This will attempt to close every object in the given array, even if one of them throws an exception.
     * <p>
     * Unlike {@link #closeAll(AutoCloseable[])}, this function does not throw exceptions. Instead, it catches them and adds them to the provided "root" exception. This
     * is intended to be used when cleaning up resources after an exception has already been thrown, so that the original exception doesn't get hidden by any
     * exceptions throwing while closing the values.
     *
     * @param root    the root exception. Any exceptions thrown while closing the objects will be added to the root exception as
     *                suppressed exceptions
     * @param toClose the objects to close
     * @return the given root exception
     */
    public static <T extends Throwable> T closeAllSuppressed(T root, AutoCloseable... toClose) {
        try {
            closeAll(toClose);
        } catch (Throwable t) {
            root.addSuppressed(t);
        }

        return root;
    }

    /**
     * {@link AutoCloseable#close() Closes} all of the provided objects.
     * <p>
     * Any {@code null} objects will be ignored.
     * <p>
     * This will attempt to close every object in the given array, even if one of them throws an exception.
     *
     * @param toClose the objects to close
     * @param off     the index of the first object to close
     * @param len     the number of objects to close
     * @throws Exception if an exception occurs while closing the objects
     */
    public static void closeAll(AutoCloseable[] toClose, int off, int len) throws Exception {
        closeAll(Arrays.asList(toClose).subList(off, off + len));
    }

    /**
     * {@link AutoCloseable#close() Closes} all of the provided objects.
     * <p>
     * Any {@code null} objects will be ignored.
     * <p>
     * This will attempt to close every object in the given array, even if one of them throws an exception.
     * <p>
     * Unlike {@link #closeAll(AutoCloseable[], int, int)}, this function does not throw exceptions. Instead, it catches them and adds them to the provided "root" exception. This
     * is intended to be used when cleaning up resources after an exception has already been thrown, so that the original exception doesn't get hidden by any
     * exceptions throwing while closing the values.
     *
     * @param root    the root exception. Any exceptions thrown while closing the objects will be added to the root exception as
     *                suppressed exceptions
     * @param toClose the objects to close
     * @param off     the index of the first object to close
     * @param len     the number of objects to close
     * @return the given root exception
     */
    public static <T extends Throwable> T closeAllSuppressed(T root, AutoCloseable[] toClose, int off, int len) {
        try {
            closeAll(toClose, off, len);
        } catch (Throwable t) {
            root.addSuppressed(t);
        }

        return root;
    }

    /**
     * Closes the provided object using the provided close function.
     * <p>
     * If the provided object is {@code null}, it is ignored.
     *
     * @param closeFunction the close function
     * @param toClose       the object to close
     * @throws T if an exception occurs while closing the object
     */
    public static <V, T extends Throwable> void close(@NonNull TConsumer<V, T> closeFunction, V toClose) throws T {
        if (toClose != null) {
            closeFunction.acceptThrowing(toClose);
        }
    }

    /**
     * Closes the provided object using the provided close function.
     * <p>
     * If the provided object is {@code null}, it is ignored.
     * <p>
     * Unlike {@link #close(AutoCloseable)}, this function does not throw exceptions. Instead, it catches them and adds them to the provided "root" exception. This
     * is intended to be used when cleaning up resources after an exception has already been thrown, so that the original exception doesn't get hidden by any
     * exceptions throwing while closing the values.
     *
     * @param root          the root exception. Any exceptions thrown while closing the object will be added to the root exception as
     *                      a suppressed exception
     * @param closeFunction the close function
     * @param toClose       the object to close
     * @return the given root exception
     */
    public static <V, T extends Throwable> T closeSuppressed(T root, @NonNull TConsumer<V, ?> closeFunction, V toClose) {
        try {
            close(closeFunction, toClose);
        } catch (Throwable t) {
            root.addSuppressed(t);
        }

        return root;
    }

    /**
     * Closes all of the provided objects using the provided close function.
     * <p>
     * Any {@code null} objects will be ignored.
     * <p>
     * This will attempt to close every object in the given {@link Iterable}, even if one of them throws an exception.
     *
     * @param closeFunction the close function
     * @param toClose       the objects to close
     * @throws T if an exception occurs while closing the objects
     */
    @SuppressWarnings({"RedundantThrows", "ForLoopReplaceableByForEach"})
    public static <V, T extends Throwable> void closeAll(@NonNull TConsumer<V, T> closeFunction, Iterable<? extends V> toClose) throws T {
        if (toClose == null) {
            return;
        }

        Throwable root = null;
        try {
            for (Iterator<? extends V> itr = toClose.iterator(); itr.hasNext(); ) { //iterate over all the values
                V value = itr.next();
                if (value != null) { //the value is non-null, try to close it
                    try {
                        closeFunction.acceptThrowing(value);
                    } catch (Throwable t) { //there was an exception, save it for later
                        if (root == null) { //this is the first exception which has occurred
                            root = t;
                        } else { //add the exception onto the first exception
                            root.addSuppressed(t);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            //we could end up here if the iterator throws an exception
            if (root != null) {
                t.addSuppressed(root);
            }
            throw PorkUtil.throwUnchecked(t);
        }

        if (root != null) { //at least one value threw an exception while being closed, rethrow it
            throw PorkUtil.throwUnchecked(root);
        }
    }

    /**
     * Closes all of the provided objects using the provided close function.
     * <p>
     * Any {@code null} objects will be ignored.
     * <p>
     * This will attempt to close every object in the given {@link Iterable}, even if one of them throws an exception.
     * <p>
     * Unlike {@link #closeAll(Iterable)}, this function does not throw exceptions. Instead, it catches them and adds them to the provided "root" exception. This
     * is intended to be used when cleaning up resources after an exception has already been thrown, so that the original exception doesn't get hidden by any
     * exceptions throwing while closing the values.
     *
     * @param root          the root exception. Any exceptions thrown while closing the objects will be added to the root exception as
     *                      suppressed exceptions
     * @param closeFunction the close function
     * @param toClose       the objects to close
     * @return the given root exception
     */
    public static <V, T extends Throwable> T closeAllSuppressed(T root, @NonNull TConsumer<V, ?> closeFunction, Iterable<? extends AutoCloseable> toClose) {
        try {
            closeAll(toClose);
        } catch (Throwable t) {
            root.addSuppressed(t);
        }

        return root;
    }

    /**
     * Closes all of the provided objects using the provided close function.
     * <p>
     * Any {@code null} objects will be ignored.
     * <p>
     * This will attempt to close every object in the given array, even if one of them throws an exception.
     *
     * @param closeFunction the close function
     * @param toClose       the objects to close
     * @throws T if an exception occurs while closing the objects
     */
    public static <V, T extends Throwable> void closeAll(@NonNull TConsumer<V, T> closeFunction, V... toClose) throws T {
        if (toClose != null) {
            closeAll(closeFunction, Arrays.asList(toClose));
        }
    }

    /**
     * Closes all of the provided objects using the provided close function.
     * <p>
     * Any {@code null} objects will be ignored.
     * <p>
     * This will attempt to close every object in the given array, even if one of them throws an exception.
     * <p>
     * Unlike {@link #closeAll(AutoCloseable[])}, this function does not throw exceptions. Instead, it catches them and adds them to the provided "root" exception. This
     * is intended to be used when cleaning up resources after an exception has already been thrown, so that the original exception doesn't get hidden by any
     * exceptions throwing while closing the values.
     *
     * @param root          the root exception. Any exceptions thrown while closing the objects will be added to the root exception as
     *                      suppressed exceptions
     * @param closeFunction the close function
     * @param toClose       the objects to close
     * @return the given root exception
     */
    public static <V, T extends Throwable> T closeAllSuppressed(T root, @NonNull TConsumer<V, ?> closeFunction, V... toClose) {
        try {
            closeAll(closeFunction, toClose);
        } catch (Throwable t) {
            root.addSuppressed(t);
        }

        return root;
    }

    /**
     * Closes all of the provided objects using the provided close function.
     * <p>
     * Any {@code null} objects will be ignored.
     * <p>
     * This will attempt to close every object in the given array, even if one of them throws an exception.
     *
     * @param closeFunction the close function
     * @param toClose       the objects to close
     * @param off           the index of the first object to close
     * @param len           the number of objects to close
     * @throws T if an exception occurs while closing the objects
     */
    public static <V, T extends Throwable> void closeAll(@NonNull TConsumer<V, T> closeFunction, V[] toClose, int off, int len) throws T {
        closeAll(closeFunction, Arrays.asList(toClose).subList(off, off + len));
    }

    /**
     * Closes all of the provided objects using the provided close function.
     * <p>
     * Any {@code null} objects will be ignored.
     * <p>
     * This will attempt to close every object in the given array, even if one of them throws an exception.
     * <p>
     * Unlike {@link #closeAll(AutoCloseable[], int, int)}, this function does not throw exceptions. Instead, it catches them and adds them to the provided "root" exception. This
     * is intended to be used when cleaning up resources after an exception has already been thrown, so that the original exception doesn't get hidden by any
     * exceptions throwing while closing the values.
     *
     * @param root          the root exception. Any exceptions thrown while closing the objects will be added to the root exception as
     *                      suppressed exceptions
     * @param closeFunction the close function
     * @param toClose       the objects to close
     * @param off           the index of the first object to close
     * @param len           the number of objects to close
     * @return the given root exception
     */
    public static <V, T extends Throwable> T closeAllSuppressed(T root, @NonNull TConsumer<V, ?> closeFunction, V[] toClose, int off, int len) {
        try {
            closeAll(closeFunction, toClose, off, len);
        } catch (Throwable t) {
            root.addSuppressed(t);
        }

        return root;
    }
}
