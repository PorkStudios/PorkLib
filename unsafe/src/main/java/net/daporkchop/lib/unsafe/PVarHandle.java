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

package net.daporkchop.lib.unsafe;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class PVarHandle {
    /**
     * Gets a {@link PVarHandle} for the given field.
     * <p>
     * <ul>
     *     <li>if the field is declared {@code final}, then the write, atomic update, numeric atomic update, and bitwise atomic update access modes are unsupported.</li>
     *     <li>if the field type is anything other than byte, short, char, int, long, float, or double then numeric atomic update access modes are unsupported.</li>
     *     <li>if the field type is anything other than boolean, byte, short, char, int or long then bitwise atomic update access modes are unsupported.</li>
     * </ul>
     * <p>
     * The returned {@link PVarHandle}'s {@link #coordinateTypes() coordinate types} will be: {@code [owner]}.
     *
     * @param lookup
     * @param owner
     * @param name
     * @param type
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static PVarHandle forField(@NonNull MethodHandles.Lookup lookup, @NonNull Class<?> owner, @NonNull String name, @NonNull Class<?> type) throws NoSuchFieldException, IllegalAccessException {
        if (UnsafePlatformInfo.JAVA_VERSION >= 9) { // use Java 9 VarHandle if supported
            return new VarHandle_Java9(lookup, owner, name, type, false);
        } else {
            return new UnsafeEmulated(lookup, lookup.findGetter(owner, name, type));
        }
    }

    /**
     * Gets a {@link PVarHandle} for the given field.
     * <p>
     * <ul>
     *     <li>if the field is declared {@code final}, then the write, atomic update, numeric atomic update, and bitwise atomic update access modes are unsupported.</li>
     *     <li>if the field type is anything other than byte, short, char, int, long, float, or double then numeric atomic update access modes are unsupported.</li>
     *     <li>if the field type is anything other than boolean, byte, short, char, int or long then bitwise atomic update access modes are unsupported.</li>
     * </ul>
     * <p>
     * The returned {@link PVarHandle}'s {@link #coordinateTypes() coordinate types} will be: {@code []}.
     *
     * @param lookup
     * @param owner
     * @param name
     * @param type
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static PVarHandle forStaticField(@NonNull MethodHandles.Lookup lookup, @NonNull Class<?> owner, @NonNull String name, @NonNull Class<?> type) throws NoSuchFieldException, IllegalAccessException {
        if (UnsafePlatformInfo.JAVA_VERSION >= 9) { // use Java 9 VarHandle if supported
            return new VarHandle_Java9(lookup, owner, name, type, true);
        } else {
            return new UnsafeEmulated(lookup, lookup.findStaticGetter(owner, name, type));
        }
    }

    public static PVarHandle forArrayElement(@NonNull MethodHandles.Lookup lookup, @NonNull Class<?> arrayClass) throws IllegalArgumentException {
        if (!arrayClass.isArray()) {
            throw new IllegalArgumentException(arrayClass + " is not an array type");
        }

        if (UnsafePlatformInfo.JAVA_VERSION >= 9) { // use Java 9 VarHandle if supported
            return new VarHandle_Java9(arrayClass);
        } else {
            return new UnsafeEmulated(arrayClass);
        }
    }

    //TODO: byte[]/ByteBuffer views?

    /**
     * Ensures that loads and stores before the fence will not be reordered with loads and stores after the fence.
     */
    public static void fullFence() {
        PUnsafe.fullFence();
    }

    /**
     * Ensures that loads before the fence will not be reordered with loads and stores after the fence.
     */
    public static void acquireFence() {
        PUnsafe.loadFence();
    }

    /**
     * Ensures that loads and stores before the fence will not be reordered with stores after the fence.
     */
    public static void releaseFence() {
        fullFence(); //TODO: this could be weakened, but for now we'll issue a full fence to play it safe
    }

    /**
     * Ensures that loads before the fence will not be reordered with loads after the fence.
     */
    public static void loadLoadFence() {
        fullFence(); //TODO: this could be weakened, but for now we'll issue a full fence to play it safe
    }

    /**
     * Ensures that stores before the fence will not be reordered with stores after the fence.
     */
    public static void storeStoreFence() {
        fullFence(); //TODO: this could be weakened, but for now we'll issue a full fence to play it safe
    }

    //TODO: maybe cache these handles?

    /**
     * Get a {@link MethodHandle} which invokes {@link #fullFence()}.
     *
     * @return a {@link MethodHandle}
     * @see #fullFence()
     */
    @SneakyThrows
    public static MethodHandle fullFenceInvoker() {
        return MethodHandles.lookup().findStatic(PVarHandle.class, "fullFence", MethodType.methodType(void.class));
    }

    /**
     * Get a {@link MethodHandle} which invokes {@link #acquireFence()}.
     *
     * @return a {@link MethodHandle}
     * @see #acquireFence()
     */
    @SneakyThrows
    public static MethodHandle acquireFenceInvoker() {
        return MethodHandles.lookup().findStatic(PVarHandle.class, "acquireFence", MethodType.methodType(void.class));
    }

    /**
     * Get a {@link MethodHandle} which invokes {@link #releaseFence()}.
     *
     * @return a {@link MethodHandle}
     * @see #releaseFence()
     */
    @SneakyThrows
    public static MethodHandle releaseFenceInvoker() {
        return MethodHandles.lookup().findStatic(PVarHandle.class, "releaseFence", MethodType.methodType(void.class));
    }

    /**
     * Get a {@link MethodHandle} which invokes {@link #loadLoadFence()}.
     *
     * @return a {@link MethodHandle}
     * @see #loadLoadFence()
     */
    @SneakyThrows
    public static MethodHandle loadLoadFenceInvoker() {
        return MethodHandles.lookup().findStatic(PVarHandle.class, "loadLoadFence", MethodType.methodType(void.class));
    }

    /**
     * Get a {@link MethodHandle} which invokes {@link #storeStoreFence()}.
     *
     * @return a {@link MethodHandle}
     * @see #storeStoreFence()
     */
    @SneakyThrows
    public static MethodHandle storeStoreFenceInvoker() {
        return MethodHandles.lookup().findStatic(PVarHandle.class, "storeStoreFence", MethodType.methodType(void.class));
    }

    /**
     * The type of variable referred to by this {@link PVarHandle}.
     */
    protected final @NonNull Class<?> type;

    /**
     * @return an unmodifiable {@link List} containing the coordinate types for accessing this handle
     */
    public abstract List<Class<?>> coordinateTypes();

    /**
     * @return the type of variable referred to by this {@link PVarHandle}
     */
    public final Class<?> varType() {
        return this.type;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[varType=" + this.type.getName() + ", coord=" + this.coordinateTypes() + ']';
    }

    //
    // SIMPLE LOAD/STORE ACCESS MODES
    //

    /**
     * Get a {@link MethodHandle} which reads the value of a variable as if the variable were declared non-{@code volatile}.
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn) -> T}.
     *
     * @return a {@link MethodHandle}
     */
    public abstract MethodHandle getPlainInvoker();

    /**
     * Get a {@link MethodHandle} which writes the value of a variable as if the variable were declared non-{@code volatile}.
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn, T newValue) -> void}.
     *
     * @return a {@link MethodHandle}
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support writes
     */
    public abstract MethodHandle setPlainInvoker() throws UnsupportedOperationException;

    /**
     * Get a {@link MethodHandle} which reads the value of a variable as if the variable were declared {@code volatile}.
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn) -> T}.
     *
     * @return a {@link MethodHandle}
     */
    public abstract MethodHandle getVolatileInvoker();

    /**
     * Get a {@link MethodHandle} which writes the value of a variable as if the variable were declared {@code volatile}.
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn, T newValue) -> void}.
     *
     * @return a {@link MethodHandle}
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support writes
     */
    public abstract MethodHandle setVolatileInvoker() throws UnsupportedOperationException;

    /**
     * Get a {@link MethodHandle} which reads the value of a variable, in program order, but with no assurance of memory ordering effects with respect to other threads.
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn) -> T}.
     *
     * @return a {@link MethodHandle}
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support writes
     */
    public abstract MethodHandle getOpaqueInvoker();

    /**
     * Get a {@link MethodHandle} which writes the value of a variable, in program order, but with no assurance of memory ordering effects with respect to other threads.
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn, T newValue) -> void}.
     *
     * @return a {@link MethodHandle}
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support writes
     */
    public abstract MethodHandle setOpaqueInvoker() throws UnsupportedOperationException;

    /**
     * Get a {@link MethodHandle} which reads the value of a variable and ensures that subsequent loads and stores are not reordered before this access.
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn) -> T}.
     *
     * @return a {@link MethodHandle}
     */
    public abstract MethodHandle getAcquireInvoker();

    /**
     * Get a {@link MethodHandle} which writes the value of a variable and ensures that prior loads and stores are not reordered after this access.
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn, T newValue) -> void}.
     *
     * @return a {@link MethodHandle}
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support writes
     */
    public abstract MethodHandle setReleaseInvoker() throws UnsupportedOperationException;

    //
    // ATOMIC UPDATE ACCESS MODES
    //

    /**
     * Get a {@link MethodHandle} which atomically sets the value of a variable to the newValue with the memory semantics of setVolatile(java.lang.Object...) if the variable's current value, referred to as the witness value, == the
     * expectedValue, as accessed with the memory semantics of getVolatile(java.lang.Object...).
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn, T expectedValue, T newValue) -> boolean}.
     *
     * @return a {@link MethodHandle}
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support writes
     */
    public abstract MethodHandle compareAndSetInvoker() throws UnsupportedOperationException;

    /**
     * Get a {@link MethodHandle} which possibly atomically sets the value of a variable to the newValue with the memory semantics of setVolatile(java.lang.Object...) if the variable's current value, referred to as the witness value, == the
     * expectedValue, as accessed with the memory semantics of getVolatile(java.lang.Object...).
     * <p>
     * The operation may fail spuriously even if the witness value does match the expected value.
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn, T expectedValue, T newValue) -> boolean}.
     *
     * @return a {@link MethodHandle}
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support writes
     */
    public abstract MethodHandle weakCompareAndSetInvoker() throws UnsupportedOperationException;

    /**
     * Get a {@link MethodHandle} which possibly atomically sets the value of a variable to the newValue with the memory semantics of set(java.lang.Object...) if the variable's current value, referred to as the witness value, == the
     * expectedValue, as accessed with the memory semantics of get(java.lang.Object...).
     * <p>
     * The operation may fail spuriously even if the witness value does match the expected value.
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn, T expectedValue, T newValue) -> boolean}.
     *
     * @return a {@link MethodHandle}
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support writes
     */
    public abstract MethodHandle weakCompareAndSetPlainInvoker() throws UnsupportedOperationException;

    /**
     * Get a {@link MethodHandle} which possibly atomically sets the value of a variable to the newValue with the memory semantics of set(java.lang.Object...) if the variable's current value, referred to as the witness value, == the
     * expectedValue, as accessed with the memory semantics of getAcquire(java.lang.Object...).
     * <p>
     * The operation may fail spuriously even if the witness value does match the expected value.
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn, T expectedValue, T newValue) -> boolean}.
     *
     * @return a {@link MethodHandle}
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support writes
     */
    public abstract MethodHandle weakCompareAndSetAcquireInvoker() throws UnsupportedOperationException;

    /**
     * Get a {@link MethodHandle} which possibly atomically sets the value of a variable to the newValue with the memory semantics of setRelease(java.lang.Object...) if the variable's current value, referred to as the witness value, == the
     * expectedValue, as accessed with the memory semantics of get(java.lang.Object...).
     * <p>
     * The operation may fail spuriously even if the witness value does match the expected value.
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn, T expectedValue, T newValue) -> boolean}.
     *
     * @return a {@link MethodHandle}
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support writes
     */
    public abstract MethodHandle weakCompareAndSetReleaseInvoker() throws UnsupportedOperationException;

    /**
     * Get a {@link MethodHandle} which atomically sets the value of a variable to the newValue with the memory semantics of setVolatile(java.lang.Object...) if the variable's current value, referred to as the witness value, == the expectedValue,
     * as accessed with the memory semantics of getVolatile(java.lang.Object...).
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn, T expectedValue, T newValue) -> T}.
     *
     * @return a {@link MethodHandle}
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support writes
     */
    public abstract MethodHandle compareAndExchangeInvoker() throws UnsupportedOperationException;

    /**
     * Get a {@link MethodHandle} which atomically sets the value of a variable to the newValue with the memory semantics of set(java.lang.Object...) if the variable's current value, referred to as the witness value, == the expectedValue,
     * as accessed with the memory semantics of getAcquire(java.lang.Object...).
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn, T expectedValue, T newValue) -> T}.
     *
     * @return a {@link MethodHandle}
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support writes
     */
    public abstract MethodHandle compareAndExchangeAcquireInvoker() throws UnsupportedOperationException;

    /**
     * Get a {@link MethodHandle} which atomically sets the value of a variable to the newValue with the memory semantics of setRelease(java.lang.Object...) if the variable's current value, referred to as the witness value, == the expectedValue,
     * as accessed with the memory semantics of get(java.lang.Object...).
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn, T expectedValue, T newValue) -> T}.
     *
     * @return a {@link MethodHandle}
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support writes
     */
    public abstract MethodHandle compareAndExchangeReleaseInvoker() throws UnsupportedOperationException;

    /**
     * Get a {@link MethodHandle} which atomically sets the value of a variable to the newValue with the memory semantics of setVolatile(java.lang.Object...) and returns the variable's previous value, as accessed with the memory semantics
     * of getVolatile(java.lang.Object...).
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn, T newValue) -> T}.
     *
     * @return a {@link MethodHandle}
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support writes
     */
    public abstract MethodHandle getAndSetInvoker() throws UnsupportedOperationException;

    /**
     * Get a {@link MethodHandle} which atomically sets the value of a variable to the newValue with the memory semantics of set(java.lang.Object...) and returns the variable's previous value, as accessed with the memory semantics
     * of getAcquire(java.lang.Object...).
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn, T newValue) -> T}.
     *
     * @return a {@link MethodHandle}
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support writes
     */
    public abstract MethodHandle getAndSetAcquireInvoker() throws UnsupportedOperationException;

    /**
     * Get a {@link MethodHandle} which atomically sets the value of a variable to the newValue with the memory semantics of setRelease(java.lang.Object...) and returns the variable's previous value, as accessed with the memory semantics
     * of get(java.lang.Object...).
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn, T newValue) -> T}.
     *
     * @return a {@link MethodHandle}
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support writes
     */
    public abstract MethodHandle getAndSetReleaseInvoker() throws UnsupportedOperationException;

    //
    // NUMERIC ATOMIC UPDATE ACCESS MODES
    //

    /**
     * Get a {@link MethodHandle} which atomically adds the value to the current value of a variable with the memory semantics of setVolatile(java.lang.Object...), and returns the variable's previous value, as accessed with the memory
     * semantics of getVolatile(java.lang.Object...).
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn, T delta) -> T}.
     *
     * @return a {@link MethodHandle}
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support writes
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support numeric operations
     */
    public abstract MethodHandle getAndAddInvoker() throws UnsupportedOperationException;

    /**
     * Get a {@link MethodHandle} which atomically adds the value to the current value of a variable with the memory semantics of set(java.lang.Object...), and returns the variable's previous value, as accessed with the memory
     * semantics of getAcquire(java.lang.Object...).
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn, T delta) -> T}.
     *
     * @return a {@link MethodHandle}
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support writes
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support numeric operations
     */
    public abstract MethodHandle getAndAddAcquireInvoker() throws UnsupportedOperationException;

    /**
     * Get a {@link MethodHandle} which atomically adds the value to the current value of a variable with the memory semantics of setRelease(java.lang.Object...), and returns the variable's previous value, as accessed with the memory
     * semantics of get(java.lang.Object...).
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn, T delta) -> T}.
     *
     * @return a {@link MethodHandle}
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support writes
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support numeric operations
     */
    public abstract MethodHandle getAndAddReleaseInvoker() throws UnsupportedOperationException;

    //
    // BITWISE ATOMIC UPDATE ACCESS MODES
    //

    /**
     * Get a {@link MethodHandle} which atomically sets the value of a variable to the result of bitwise OR between the variable's current value and the given mask with the memory semantics of setVolatile(java.lang.Object...), and
     * returns the variable's previous value, as accessed with the memory semantics of getVolatile(java.lang.Object...).
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn, T mask) -> T}.
     *
     * @return a {@link MethodHandle}
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support writes
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support bitwise operations
     */
    public abstract MethodHandle getAndBitwiseOrInvoker() throws UnsupportedOperationException;

    /**
     * Get a {@link MethodHandle} which atomically sets the value of a variable to the result of bitwise OR between the variable's current value and the given mask with the memory semantics of set(java.lang.Object...), and
     * returns the variable's previous value, as accessed with the memory semantics of getAcquire(java.lang.Object...).
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn, T mask) -> T}.
     *
     * @return a {@link MethodHandle}
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support writes
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support bitwise operations
     */
    public abstract MethodHandle getAndBitwiseOrAcquireInvoker() throws UnsupportedOperationException;

    /**
     * Get a {@link MethodHandle} which atomically sets the value of a variable to the result of bitwise OR between the variable's current value and the given mask with the memory semantics of setRelease(java.lang.Object...), and
     * returns the variable's previous value, as accessed with the memory semantics of get(java.lang.Object...).
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn, T mask) -> T}.
     *
     * @return a {@link MethodHandle}
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support writes
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support bitwise operations
     */
    public abstract MethodHandle getAndBitwiseOrReleaseInvoker() throws UnsupportedOperationException;

    /**
     * Get a {@link MethodHandle} which atomically sets the value of a variable to the result of bitwise AND between the variable's current value and the given mask with the memory semantics of setVolatile(java.lang.Object...), and
     * returns the variable's previous value, as accessed with the memory semantics of getVolatile(java.lang.Object...).
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn, T mask) -> T}.
     *
     * @return a {@link MethodHandle}
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support writes
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support bitwise operations
     */
    public abstract MethodHandle getAndBitwiseAndInvoker() throws UnsupportedOperationException;

    /**
     * Get a {@link MethodHandle} which atomically sets the value of a variable to the result of bitwise AND between the variable's current value and the given mask with the memory semantics of set(java.lang.Object...), and
     * returns the variable's previous value, as accessed with the memory semantics of getAcquire(java.lang.Object...).
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn, T mask) -> T}.
     *
     * @return a {@link MethodHandle}
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support writes
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support bitwise operations
     */
    public abstract MethodHandle getAndBitwiseAndAcquireInvoker() throws UnsupportedOperationException;

    /**
     * Get a {@link MethodHandle} which atomically sets the value of a variable to the result of bitwise AND between the variable's current value and the given mask with the memory semantics of setRelease(java.lang.Object...), and
     * returns the variable's previous value, as accessed with the memory semantics of get(java.lang.Object...).
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn, T mask) -> T}.
     *
     * @return a {@link MethodHandle}
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support writes
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support bitwise operations
     */
    public abstract MethodHandle getAndBitwiseAndReleaseInvoker() throws UnsupportedOperationException;

    /**
     * Get a {@link MethodHandle} which atomically sets the value of a variable to the result of bitwise XOR between the variable's current value and the given mask with the memory semantics of setVolatile(java.lang.Object...), and
     * returns the variable's previous value, as accessed with the memory semantics of getVolatile(java.lang.Object...).
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn, T mask) -> T}.
     *
     * @return a {@link MethodHandle}
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support writes
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support bitwise operations
     */
    public abstract MethodHandle getAndBitwiseXorInvoker() throws UnsupportedOperationException;

    /**
     * Get a {@link MethodHandle} which atomically sets the value of a variable to the result of bitwise XOR between the variable's current value and the given mask with the memory semantics of set(java.lang.Object...), and
     * returns the variable's previous value, as accessed with the memory semantics of getAcquire(java.lang.Object...).
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn, T mask) -> T}.
     *
     * @return a {@link MethodHandle}
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support writes
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support bitwise operations
     */
    public abstract MethodHandle getAndBitwiseXorAcquireInvoker() throws UnsupportedOperationException;

    /**
     * Get a {@link MethodHandle} which atomically sets the value of a variable to the result of bitwise XOR between the variable's current value and the given mask with the memory semantics of setRelease(java.lang.Object...), and
     * returns the variable's previous value, as accessed with the memory semantics of get(java.lang.Object...).
     * <p>
     * The method signature is of the form {@code (CT1 ct1, ..., CTn ctn, T mask) -> T}.
     *
     * @return a {@link MethodHandle}
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support writes
     * @throws UnsupportedOperationException if this {@link PVarHandle} doesn't support bitwise operations
     */
    public abstract MethodHandle getAndBitwiseXorReleaseInvoker() throws UnsupportedOperationException;

    /**
     * @author DaPorkchop_
     */
    private static final class UnsafeEmulated extends PVarHandle {
        static {
            if (UnsafePlatformInfo.JAVA_VERSION > 8) {
                throw new AssertionError("this implementation shouldn't be used on Java 9+!!!");
            }
        }

        private final MethodHandle getter;
        private final MethodHandle setter;

        private final Class<?> owner;
        private final int modifiers;

        private final Object staticFieldBase; //unused if the field is non-static
        private final long fieldOffset;

        private final Class<?> arrayClass; //if non-null, this is an array handle and all the other fields are ignored

        UnsafeEmulated(@NonNull MethodHandles.Lookup lookup, @NonNull MethodHandle getter) throws IllegalAccessException {
            super(getter.type().returnType());

            //set all other fields to default values
            this.arrayClass = null;

            //we have to get a Field instance anyway in order to get the unsafe field offset, so we may as well use it to get the field location and modifiers
            Field field = lookup.revealDirect(getter).reflectAs(Field.class, lookup);
            this.owner = field.getDeclaringClass();
            this.modifiers = field.getModifiers();

            assert this.type == field.getType() : this.type + " != " + field.getType();

            if (Modifier.isStatic(this.modifiers)) { //this is a static field
                this.staticFieldBase = PUnsafe.staticFieldBase(field);
                this.fieldOffset = PUnsafe.staticFieldOffset(field);
            } else { //this is an object field
                this.staticFieldBase = null;
                this.fieldOffset = PUnsafe.objectFieldOffset(field);
            }

            this.getter = getter;
            this.setter = Modifier.isFinal(this.modifiers) ? null : lookup.unreflectSetter(field);
        }

        UnsafeEmulated(@NonNull Class<?> arrayClass) {
            super(arrayClass.getComponentType());

            //set all other fields to default values
            this.getter = null;
            this.setter = null;
            this.owner = null;
            this.modifiers = 0;
            this.staticFieldBase = null;
            this.fieldOffset = 0L;

            this.arrayClass = arrayClass;
        }

        private void checkWriteSupported() {
            if (this.arrayClass != null) { //this is an array handle
                //immutable arrays aren't a thing on Java 8 so we will allow it
                return;
            }

            if (Modifier.isFinal(this.modifiers)) {
                throw new UnsupportedOperationException("write unsupported for final field: " + this);
            }
        }

        private boolean isNumericAtomicUpdateSupported() {
            return this.type == byte.class || this.type == short.class || this.type == char.class || this.type == int.class || this.type == long.class || this.type == float.class || this.type == double.class;
        }

        private void checkNumericAtomicUpdateSupported() {
            if (!this.isNumericAtomicUpdateSupported()) {
                throw new UnsupportedOperationException("numeric atomic update unsupported for type: " + this.type.getName());
            }
        }

        private boolean isBitwiseAtomicUpdateSupported() {
            return this.type == boolean.class || this.type == byte.class || this.type == short.class || this.type == char.class || this.type == int.class || this.type == long.class;
        }

        private void checkBitwiseAtomicUpdateSupported() {
            if (!this.isBitwiseAtomicUpdateSupported()) {
                throw new UnsupportedOperationException("bitwise atomic update unsupported for type: " + this.type.getName());
            }
        }

        private static String getRawTypeName(Class<?> type) {
            if (type.isPrimitive()) {
                String lowercaseName = type.getName();
                return Character.toUpperCase(lowercaseName.charAt(0)) + lowercaseName.substring(1);
            } else {
                return "Object";
            }
        }

        private static Class<?> getRawTypeClass(Class<?> type) {
            return type.isPrimitive() ? type : Object.class;
        }

        @SneakyThrows
        private MethodHandle bindBaseAndOffsetArguments(MethodHandle target) {
            assert target.type().parameterCount() >= 2 && target.type().parameterType(0) == Object.class && target.type().parameterType(1) == long.class
                    : target.type() + " is invalid, its parameters must start with (Object, long, ...)";

            if (this.arrayClass != null) { //this is an array handle
                //perform initial checks for null and bounds, then compute the actual offset

                Class<?> rawArrayClass = Array.newInstance(getRawTypeClass(this.type), 0).getClass();

                // (rawArray array, int index) -> long
                MethodHandle computeOffsetChecked = MethodHandles.lookup().findStatic(
                        PUnsafe.class,
                        "array" + getRawTypeName(this.type) + "ElementOffsetChecked",
                        MethodType.methodType(long.class, rawArrayClass, int.class));

                // (rawArray array, rawArray array, int index, ...) -> ...
                MethodHandle checkedTarget = MethodHandles.collectArguments(
                        target.asType(target.type().changeParameterType(0, rawArrayClass)),
                        1,
                        computeOffsetChecked);

                // (rawArray array, int index, ...) -> ...
                MethodHandle duplicatedCheckedTarget = MethodHandles.permuteArguments(
                        checkedTarget,
                        checkedTarget.type().dropParameterTypes(0, 1),
                        IntStream.concat(
                                IntStream.of(0),
                                IntStream.range(0, target.type().parameterCount())
                        ).toArray());

                //TODO: everything up to here could be cached for the raw type?

                return duplicatedCheckedTarget.asType(duplicatedCheckedTarget.type().changeParameterType(0, this.arrayClass));
            }

            if (Modifier.isStatic(this.modifiers)) { //this is a static field
                return MethodHandles.insertArguments(target, 0, this.staticFieldBase, this.fieldOffset);
            } else { //this is an object field
                //TODO: i think this needs to perform null checks?
                return MethodHandles.insertArguments(target.asType(target.type().changeParameterType(0, this.owner)), 1, this.fieldOffset);
            }
        }

        @SneakyThrows(ReflectiveOperationException.class)
        private static MethodHandle getInvokerEmulated(String prefix, String suffix, Class<?> type) {
            MethodHandle unsafeGet = MethodHandles.lookup().findStatic(
                    PUnsafe.class,
                    prefix + getRawTypeName(type) + suffix,
                    MethodType.methodType(getRawTypeClass(type), Object.class, long.class));

            return unsafeGet.asType(unsafeGet.type().changeReturnType(type));
        }

        @SneakyThrows(ReflectiveOperationException.class)
        private static MethodHandle setInvokerEmulated(String prefix, String suffix, Class<?> type) {
            MethodHandle unsafePut = MethodHandles.lookup().findStatic(
                    PUnsafe.class,
                    prefix + getRawTypeName(type) + suffix,
                    MethodType.methodType(void.class, Object.class, long.class, getRawTypeClass(type)));

            return unsafePut.asType(unsafePut.type().changeParameterType(2, type));
        }

        @Override
        public List<Class<?>> coordinateTypes() {
            if (this.arrayClass != null) { //this is an array handle
                return Collections.unmodifiableList(Arrays.asList(this.arrayClass, int.class));
            }

            if (Modifier.isStatic(this.modifiers)) {
                return Collections.emptyList();
            } else {
                return Collections.singletonList(this.owner);
            }
        }

        //
        // SIMPLE LOAD/STORE ACCESS MODES
        //

        @Override
        public MethodHandle getPlainInvoker() {
            if (this.arrayClass == null && !Modifier.isVolatile(this.modifiers)) { //if the field is non-volatile, we can use a standard getter MethodHandle
                return this.getter;
            } else { //if the field is volatile, we'll have to use Unsafe to load it with non-volatile ordering
                return this.bindBaseAndOffsetArguments(getInvokerEmulated("get", "", this.type));
            }
        }

        @Override
        public MethodHandle setPlainInvoker() throws UnsupportedOperationException {
            this.checkWriteSupported();

            if (this.arrayClass == null && !Modifier.isVolatile(this.modifiers)) { //if the field is non-volatile, we can use a standard setter MethodHandle
                return this.setter;
            } else { //if the field is volatile, we'll have to use Unsafe to load it with non-volatile ordering
                return this.bindBaseAndOffsetArguments(setInvokerEmulated("put", "", this.type));
            }
        }

        @Override
        public MethodHandle getVolatileInvoker() {
            if (this.arrayClass == null && Modifier.isVolatile(this.modifiers)) { //if the field is volatile, we can use a standard getter MethodHandle
                return this.getter;
            } else { //if the field is non-volatile, we'll have to use Unsafe to load it with volatile ordering
                return this.bindBaseAndOffsetArguments(getInvokerEmulated("get", "Volatile", this.type));
            }
        }

        @Override
        public MethodHandle setVolatileInvoker() throws UnsupportedOperationException {
            this.checkWriteSupported();

            if (this.arrayClass == null && Modifier.isVolatile(this.modifiers)) { //if the field is volatile, we can use a standard setter MethodHandle
                return this.setter;
            } else { //if the field is non-volatile, we'll have to use Unsafe to load it with volatile ordering
                return this.bindBaseAndOffsetArguments(setInvokerEmulated("put", "Volatile", this.type));
            }
        }

        @Override
        public MethodHandle getOpaqueInvoker() {
            //we don't have intrinsics for this on Java 8, fall back to standard volatile load
            return this.getVolatileInvoker();
        }

        @Override
        public MethodHandle setOpaqueInvoker() throws UnsupportedOperationException {
            //we don't have intrinsics for this on Java 8, fall back to standard volatile store
            return this.setVolatileInvoker();
        }

        @Override
        public MethodHandle getAcquireInvoker() {
            //the methods in PUnsafe already emulate this as efficiently as possible, just get a handle to those
            return this.bindBaseAndOffsetArguments(getInvokerEmulated("get", "Acquire", this.type));
        }

        @Override
        public MethodHandle setReleaseInvoker() throws UnsupportedOperationException {
            this.checkWriteSupported();

            //the methods in PUnsafe already emulate this as efficiently as possible, just get a handle to those
            return this.bindBaseAndOffsetArguments(setInvokerEmulated("put", "Release", this.type));
        }

        //
        // ATOMIC UPDATE ACCESS MODES
        //

        @Override
        @SneakyThrows(ReflectiveOperationException.class)
        public MethodHandle compareAndSetInvoker() throws UnsupportedOperationException {
            this.checkWriteSupported();

            MethodHandle unsafeCompareAndSet = MethodHandles.lookup().findStatic(
                    PUnsafeAtomics_Java8.class,
                    "compareAndSet" + getRawTypeName(this.type),
                    MethodType.methodType(boolean.class, Object.class, long.class, getRawTypeClass(this.type), getRawTypeClass(this.type)));

            return this.bindBaseAndOffsetArguments(unsafeCompareAndSet.asType(
                    MethodType.methodType(boolean.class, Object.class, long.class, this.type, this.type)));
        }

        @Override
        public MethodHandle weakCompareAndSetInvoker() throws UnsupportedOperationException {
            //we don't have intrinsics for this on Java 8, fall back to standard volatile CAS
            return this.compareAndSetInvoker();
        }

        @Override
        public MethodHandle weakCompareAndSetPlainInvoker() throws UnsupportedOperationException {
            //we don't have intrinsics for this on Java 8, fall back to standard volatile CAS
            return this.compareAndSetInvoker();
        }

        @Override
        public MethodHandle weakCompareAndSetAcquireInvoker() throws UnsupportedOperationException {
            //we don't have intrinsics for this on Java 8, fall back to standard volatile CAS
            return this.compareAndSetInvoker();
        }

        @Override
        public MethodHandle weakCompareAndSetReleaseInvoker() throws UnsupportedOperationException {
            //we don't have intrinsics for this on Java 8, fall back to standard volatile CAS
            return this.compareAndSetInvoker();
        }

        @Override
        @SneakyThrows(ReflectiveOperationException.class)
        public MethodHandle compareAndExchangeInvoker() throws UnsupportedOperationException {
            this.checkWriteSupported();

            //this isn't actually an intrinsic on Java 8, but we'd have to emulate it using CAS either way and it's easier to just use the existing function
            MethodHandle unsafeCompareAndExchange = MethodHandles.lookup().findStatic(
                    PUnsafeAtomics_Java8.class,
                    "compareAndExchange" + getRawTypeName(this.type),
                    MethodType.methodType(getRawTypeClass(this.type), Object.class, long.class, getRawTypeClass(this.type), getRawTypeClass(this.type)));

            return this.bindBaseAndOffsetArguments(unsafeCompareAndExchange.asType(
                    MethodType.methodType(this.type, Object.class, long.class, this.type, this.type)));
        }

        @Override
        public MethodHandle compareAndExchangeAcquireInvoker() throws UnsupportedOperationException {
            //we don't have intrinsics for this on Java 8, fall back to standard volatile compare-and-exchange
            return this.compareAndExchangeInvoker();
        }

        @Override
        public MethodHandle compareAndExchangeReleaseInvoker() throws UnsupportedOperationException {
            //we don't have intrinsics for this on Java 8, fall back to standard volatile compare-and-exchange
            return this.compareAndExchangeInvoker();
        }

        @Override
        @SneakyThrows(ReflectiveOperationException.class)
        public MethodHandle getAndSetInvoker() throws UnsupportedOperationException {
            this.checkWriteSupported();

            //this isn't actually an intrinsic on Java 8, but we'd have to emulate it using CAS either way and it's easier to just use the existing function
            MethodHandle unsafeGetAndSet = MethodHandles.lookup().findStatic(
                    PUnsafeAtomics_Java8.class,
                    "getAndSet" + getRawTypeName(this.type),
                    MethodType.methodType(getRawTypeClass(this.type), Object.class, long.class, getRawTypeClass(this.type)));

            return this.bindBaseAndOffsetArguments(unsafeGetAndSet.asType(
                    MethodType.methodType(this.type, Object.class, long.class, this.type)));
        }

        @Override
        public MethodHandle getAndSetAcquireInvoker() throws UnsupportedOperationException {
            //we don't have intrinsics for this on Java 8, fall back to standard volatile exchange
            return this.getAndSetInvoker();
        }

        @Override
        public MethodHandle getAndSetReleaseInvoker() throws UnsupportedOperationException {
            //we don't have intrinsics for this on Java 8, fall back to standard volatile exchange
            return this.getAndSetInvoker();
        }

        //
        // NUMERIC ATOMIC UPDATE ACCESS MODES
        //

        @Override
        @SneakyThrows(ReflectiveOperationException.class)
        public MethodHandle getAndAddInvoker() throws UnsupportedOperationException {
            this.checkWriteSupported();
            this.checkNumericAtomicUpdateSupported();
            assert this.type.isPrimitive() : this.type;

            //this isn't actually an intrinsic on Java 8, but we'd have to emulate it using CAS either way and it's easier to just use the existing function
            return this.bindBaseAndOffsetArguments(MethodHandles.lookup().findStatic(
                    PUnsafeAtomics_Java8.class,
                    "getAndAdd" + getRawTypeName(this.type),
                    MethodType.methodType(this.type, Object.class, long.class, this.type)));
        }

        @Override
        public MethodHandle getAndAddAcquireInvoker() throws UnsupportedOperationException {
            //we don't have intrinsics for this on Java 8, fall back to standard volatile implementation
            return this.getAndAddInvoker();
        }

        @Override
        public MethodHandle getAndAddReleaseInvoker() throws UnsupportedOperationException {
            //we don't have intrinsics for this on Java 8, fall back to standard volatile implementation
            return this.getAndAddInvoker();
        }

        //
        // BITWISE ATOMIC UPDATE ACCESS MODES
        //

        @Override
        @SneakyThrows(ReflectiveOperationException.class)
        public MethodHandle getAndBitwiseOrInvoker() throws UnsupportedOperationException {
            this.checkWriteSupported();
            this.checkBitwiseAtomicUpdateSupported();

            //this isn't actually an intrinsic on Java 8, but we'd have to emulate it using CAS either way and it's easier to just use the existing function
            return this.bindBaseAndOffsetArguments(MethodHandles.lookup().findStatic(
                    PUnsafeAtomics_Java8.class,
                    "getAndBitwiseOr" + getRawTypeName(this.type),
                    MethodType.methodType(this.type, Object.class, long.class, this.type)));
        }

        @Override
        public MethodHandle getAndBitwiseOrAcquireInvoker() throws UnsupportedOperationException {
            //we don't have intrinsics for this on Java 8, fall back to standard volatile implementation
            return this.getAndBitwiseOrInvoker();
        }

        @Override
        public MethodHandle getAndBitwiseOrReleaseInvoker() throws UnsupportedOperationException {
            //we don't have intrinsics for this on Java 8, fall back to standard volatile implementation
            return this.getAndBitwiseOrInvoker();
        }

        @Override
        @SneakyThrows(ReflectiveOperationException.class)
        public MethodHandle getAndBitwiseAndInvoker() throws UnsupportedOperationException {
            this.checkWriteSupported();
            this.checkBitwiseAtomicUpdateSupported();

            //this isn't actually an intrinsic on Java 8, but we'd have to emulate it using CAS either way and it's easier to just use the existing function
            return this.bindBaseAndOffsetArguments(MethodHandles.lookup().findStatic(
                    PUnsafeAtomics_Java8.class,
                    "getAndBitwiseAnd" + getRawTypeName(this.type),
                    MethodType.methodType(this.type, Object.class, long.class, this.type)));
        }

        @Override
        public MethodHandle getAndBitwiseAndAcquireInvoker() throws UnsupportedOperationException {
            //we don't have intrinsics for this on Java 8, fall back to standard volatile implementation
            return this.getAndBitwiseAndInvoker();
        }

        @Override
        public MethodHandle getAndBitwiseAndReleaseInvoker() throws UnsupportedOperationException {
            //we don't have intrinsics for this on Java 8, fall back to standard volatile implementation
            return this.getAndBitwiseAndInvoker();
        }

        @Override
        @SneakyThrows(ReflectiveOperationException.class)
        public MethodHandle getAndBitwiseXorInvoker() throws UnsupportedOperationException {
            this.checkWriteSupported();
            this.checkBitwiseAtomicUpdateSupported();

            //this isn't actually an intrinsic on Java 8, but we'd have to emulate it using CAS either way and it's easier to just use the existing function
            return this.bindBaseAndOffsetArguments(MethodHandles.lookup().findStatic(
                    PUnsafeAtomics_Java8.class,
                    "getAndBitwiseXor" + getRawTypeName(this.type),
                    MethodType.methodType(this.type, Object.class, long.class, this.type)));
        }

        @Override
        public MethodHandle getAndBitwiseXorAcquireInvoker() throws UnsupportedOperationException {
            //we don't have intrinsics for this on Java 8, fall back to standard volatile implementation
            return this.getAndBitwiseXorInvoker();
        }

        @Override
        public MethodHandle getAndBitwiseXorReleaseInvoker() throws UnsupportedOperationException {
            //we don't have intrinsics for this on Java 8, fall back to standard volatile implementation
            return this.getAndBitwiseXorInvoker();
        }
    }

    /**
     * @author DaPorkchop_
     */
    @SuppressWarnings("unchecked")
    private static final class VarHandle_Java9 extends PVarHandle {
        private static final Class<?> VarHandle;
        private static final Class<? extends Enum> VarHandle$AccessMode;

        private static final MethodHandle MethodHandles_arrayElementVarHandle; // (Class<?>) -> VarHandle
        private static final MethodHandle MethodHandles$Lookup_findVarHandle; // (MethodHandles$Lookup, Class<?>, String, Class<?>) -> VarHandle
        private static final MethodHandle MethodHandles$Lookup_findStaticVarHandle; // (MethodHandles$Lookup, Class<?>, String, Class<?>) -> VarHandle

        private static final MethodHandle VarHandle_coordinateTypes; // (VarHandle) -> List<Class<?>>
        private static final MethodHandle VarHandle_isAccessModeSupported; // (VarHandle, VarHandle$AccessMode) -> boolean
        private static final MethodHandle VarHandle_toMethodHandle; // (VarHandle, VarHandle$AccessMode) -> MethodHandle

        static {
            try {
                VarHandle = Class.forName("java.lang.invoke.VarHandle");
                VarHandle$AccessMode = (Class<? extends Enum>) Class.forName("java.lang.invoke.VarHandle$AccessMode");

                MethodHandles_arrayElementVarHandle = MethodHandles.publicLookup().findStatic(MethodHandles.class, "arrayElementVarHandle", MethodType.methodType(VarHandle, Class.class));
                MethodHandles$Lookup_findVarHandle = MethodHandles.publicLookup().findVirtual(MethodHandles.Lookup.class, "findVarHandle", MethodType.methodType(VarHandle, Class.class, String.class, Class.class));
                MethodHandles$Lookup_findStaticVarHandle = MethodHandles.publicLookup().findVirtual(MethodHandles.Lookup.class, "findStaticVarHandle", MethodType.methodType(VarHandle, Class.class, String.class, Class.class));

                VarHandle_coordinateTypes = MethodHandles.publicLookup().findVirtual(VarHandle, "coordinateTypes", MethodType.methodType(List.class));
                VarHandle_isAccessModeSupported = MethodHandles.publicLookup().findVirtual(VarHandle, "isAccessModeSupported", MethodType.methodType(boolean.class, VarHandle$AccessMode));
                VarHandle_toMethodHandle = MethodHandles.publicLookup().findVirtual(VarHandle, "toMethodHandle", MethodType.methodType(MethodHandle.class, VarHandle$AccessMode));
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        }

        private final Object varHandle;

        //fields
        @SneakyThrows
        VarHandle_Java9(@NonNull MethodHandles.Lookup lookup, @NonNull Class<?> owner, @NonNull String name, @NonNull Class<?> type, boolean isStatic) throws NoSuchFieldException, IllegalAccessException {
            super(type);

            this.varHandle = isStatic
                    ? MethodHandles$Lookup_findStaticVarHandle.invoke(lookup, owner, name, type)
                    : MethodHandles$Lookup_findVarHandle.invoke(lookup, owner, name, type);
        }

        //arrays
        @SneakyThrows
        VarHandle_Java9(@NonNull Class<?> arrayClass) throws IllegalArgumentException {
            super(arrayClass.getComponentType());

            this.varHandle = MethodHandles_arrayElementVarHandle.invoke(arrayClass);
        }

        @Override
        @SneakyThrows
        public List<Class<?>> coordinateTypes() {
            return (List<Class<?>>) VarHandle_coordinateTypes.invoke(this.varHandle);
        }

        @SneakyThrows
        private MethodHandle invoker(String name) {
            Object accessMode = Enum.valueOf(VarHandle$AccessMode, name);
            if (!(boolean) VarHandle_isAccessModeSupported.invoke(this.varHandle, accessMode)) {
                throw new UnsupportedOperationException("this PVarHandle doesn't support " + name + ": " + this.varHandle);
            }

            return (MethodHandle) VarHandle_toMethodHandle.invoke(this.varHandle, accessMode);
        }

        //
        // SIMPLE LOAD/STORE ACCESS MODES
        //

        @Override
        public MethodHandle getPlainInvoker() {
            return this.invoker("GET");
        }

        @Override
        public MethodHandle setPlainInvoker() throws UnsupportedOperationException {
            return this.invoker("SET");
        }

        @Override
        public MethodHandle getVolatileInvoker() {
            return this.invoker("GET_VOLATILE");
        }

        @Override
        public MethodHandle setVolatileInvoker() throws UnsupportedOperationException {
            return this.invoker("SET_VOLATILE");
        }

        @Override
        public MethodHandle getOpaqueInvoker() {
            return this.invoker("GET_OPAQUE");
        }

        @Override
        public MethodHandle setOpaqueInvoker() throws UnsupportedOperationException {
            return this.invoker("SET_OPAQUE");
        }

        @Override
        public MethodHandle getAcquireInvoker() {
            return this.invoker("GET_ACQUIRE");
        }

        @Override
        public MethodHandle setReleaseInvoker() throws UnsupportedOperationException {
            return this.invoker("SET_RELEASE");
        }

        //
        // ATOMIC UPDATE ACCESS MODES
        //

        @Override
        public MethodHandle compareAndSetInvoker() throws UnsupportedOperationException {
            return this.invoker("COMPARE_AND_SET");
        }

        @Override
        public MethodHandle weakCompareAndSetInvoker() throws UnsupportedOperationException {
            return this.invoker("WEAK_COMPARE_AND_SET");
        }

        @Override
        public MethodHandle weakCompareAndSetPlainInvoker() throws UnsupportedOperationException {
            return this.invoker("WEAK_COMPARE_AND_SET_PLAIN");
        }

        @Override
        public MethodHandle weakCompareAndSetAcquireInvoker() throws UnsupportedOperationException {
            return this.invoker("WEAK_COMPARE_AND_SET_ACQUIRE");
        }

        @Override
        public MethodHandle weakCompareAndSetReleaseInvoker() throws UnsupportedOperationException {
            return this.invoker("WEAK_COMPARE_AND_SET_RELEASE");
        }

        @Override
        public MethodHandle compareAndExchangeInvoker() throws UnsupportedOperationException {
            return this.invoker("COMPARE_AND_EXCHANGE");
        }

        @Override
        public MethodHandle compareAndExchangeAcquireInvoker() throws UnsupportedOperationException {
            return this.invoker("COMPARE_AND_EXCHANGE_ACQUIRE");
        }

        @Override
        public MethodHandle compareAndExchangeReleaseInvoker() throws UnsupportedOperationException {
            return this.invoker("COMPARE_AND_EXCHANGE_RELEASE");
        }

        @Override
        public MethodHandle getAndSetInvoker() throws UnsupportedOperationException {
            return this.invoker("GET_AND_SET");
        }

        @Override
        public MethodHandle getAndSetAcquireInvoker() throws UnsupportedOperationException {
            return this.invoker("GET_AND_SET_ACQUIRE");
        }

        @Override
        public MethodHandle getAndSetReleaseInvoker() throws UnsupportedOperationException {
            return this.invoker("GET_AND_SET_RELEASE");
        }

        //
        // NUMERIC ATOMIC UPDATE ACCESS MODES
        //

        @Override
        public MethodHandle getAndAddInvoker() throws UnsupportedOperationException {
            return this.invoker("GET_AND_ADD");
        }

        @Override
        public MethodHandle getAndAddAcquireInvoker() throws UnsupportedOperationException {
            return this.invoker("GET_AND_ADD_ACQUIRE");
        }

        @Override
        public MethodHandle getAndAddReleaseInvoker() throws UnsupportedOperationException {
            return this.invoker("GET_AND_ADD_RELEASE");
        }

        //
        // BITWISE ATOMIC UPDATE ACCESS MODES
        //

        @Override
        public MethodHandle getAndBitwiseOrInvoker() throws UnsupportedOperationException {
            return this.invoker("GET_AND_BITWISE_OR");
        }

        @Override
        public MethodHandle getAndBitwiseOrAcquireInvoker() throws UnsupportedOperationException {
            return this.invoker("GET_AND_BITWISE_OR_ACQUIRE");
        }

        @Override
        public MethodHandle getAndBitwiseOrReleaseInvoker() throws UnsupportedOperationException {
            return this.invoker("GET_AND_BITWISE_OR_RELEASE");
        }

        @Override
        public MethodHandle getAndBitwiseAndInvoker() throws UnsupportedOperationException {
            return this.invoker("GET_AND_BITWISE_AND");
        }

        @Override
        public MethodHandle getAndBitwiseAndAcquireInvoker() throws UnsupportedOperationException {
            return this.invoker("GET_AND_BITWISE_AND_ACQUIRE");
        }

        @Override
        public MethodHandle getAndBitwiseAndReleaseInvoker() throws UnsupportedOperationException {
            return this.invoker("GET_AND_BITWISE_AND_RELEASE");
        }

        @Override
        public MethodHandle getAndBitwiseXorInvoker() throws UnsupportedOperationException {
            return this.invoker("GET_AND_BITWISE_XOR");
        }

        @Override
        public MethodHandle getAndBitwiseXorAcquireInvoker() throws UnsupportedOperationException {
            return this.invoker("GET_AND_BITWISE_XOR_ACQUIRE");
        }

        @Override
        public MethodHandle getAndBitwiseXorReleaseInvoker() throws UnsupportedOperationException {
            return this.invoker("GET_AND_BITWISE_XOR_RELEASE");
        }
    }
}
