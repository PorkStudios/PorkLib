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

package net.daporkchop.lib.reflection.lambda;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.function.throwing.EFunction;
import net.daporkchop.lib.common.util.PConstants;
import net.daporkchop.lib.common.util.PUnsafe;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A user-friendly wrapper around {@link java.lang.invoke.LambdaMetafactory} for making reflective calls
 * to otherwise inaccessible methods faster
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class LambdaBuilder<T> {
    public static final Function<Class<?>, MethodHandles.Lookup> LOOKUP_CREATOR;

    static {
        try {
            Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
            constructor.setAccessible(true);
            LOOKUP_CREATOR = (EFunction<Class<?>, MethodHandles.Lookup>) clazz -> constructor.newInstance(clazz, -1);
        } catch (NoSuchMethodException e) {
            throw PConstants.p_exception(e);
        }
    }

    /**
     * Convenience method to create a new {@link LambdaBuilder} for a given interface type
     *
     * @param interfaceClass the class of the interface to implement a method on
     * @param <T>            the type of the interface
     * @return a new {@link LambdaBuilder} with the given interface class
     */
    public static <T> LambdaBuilder<T> of(@NonNull Class<T> interfaceClass) {
        return new LambdaBuilder<T>().setInterfaceClass(interfaceClass);
    }

    /**
     * The interface class of the lambda to create
     */
    @NonNull
    protected Class<T> interfaceClass;

    /**
     * The class that contains the actual method to be invoked
     */
    @NonNull
    protected Class<?> methodHolder;

    /**
     * The name of the method to invoke (in the target class)
     */
    @NonNull
    protected String methodName;

    /**
     * The name of the method to implement (in the interface).
     * <p>
     * If not set, defaults to {@link #methodName}.
     */
    protected String interfaceName;

    /**
     * Whether or not the method is static (in the target class)
     */
    protected boolean methodStatic;

    /**
     * Sets whether or not the first parameter on the interface method is generic or not, for use when providing
     * an instance to invoke the function on.
     * <p>
     * Only relevant when {@link #methodStatic} is set to {@code false}
     */
    protected boolean interfaceTargetGeneric;

    /**
     * A list of parameters on the method
     */
    @Getter(AccessLevel.PACKAGE)
    protected final List<LambdaParam> params = new LinkedList<>();

    /**
     * The return type of the method
     */
    @NonNull
    protected LambdaParam returnType;

    /**
     * A function that will return an instance of <T> if lambda conversion fails.
     * <p>
     * If {@code null}, the lambda conversion exception will be thrown directly.
     */
    protected Supplier<T> fallback;

    /**
     * Creates a builder for a new {@link LambdaParam} that will be added to this {@link LambdaBuilder} when
     * built.
     *
     * @return an instance of {@link LambdaParam.Builder}
     */
    public LambdaParam.Builder<T> param() {
        return new LambdaParam.Builder<>(this, this.params::add);
    }

    /**
     * Creates a builder for a new {@link LambdaParam} that be set as the return type for this {@link LambdaBuilder}
     * when built.
     *
     * @return an instance of {@link LambdaParam.Builder}
     */
    public LambdaParam.Builder<T> returnType() {
        return new LambdaParam.Builder<>(this, this::setReturnType);
    }

    /**
     * Sets this lambda builder's target to be a static method.
     * <p>
     * Functionally equivalent to {@code LambdaBuilder#setMethodStatic(true)}.
     *
     * @return this {@link LambdaBuilder}
     */
    public LambdaBuilder<T> setStatic() {
        return this.setMethodStatic(true);
    }

    /**
     * Actually builds the lambda!
     * <p>
     * Uses all the settings in this instance of {@link LambdaBuilder} to create a new lambda.
     * <p>
     * Note that if the target method is not static, the interface method is required to have the first
     * parameter be an instance of T to be used as an instance.
     *
     * @return an instance of T that implements the method
     */
    @SuppressWarnings("unchecked")
    public T build() {
        if (this.interfaceClass == null) {
            throw new IllegalStateException("Interface class must be set!");
        } else if (this.methodHolder == null) {
            throw new IllegalStateException("Target class must be set!");
        } else if (this.methodName == null) {
            throw new IllegalStateException("Target method name must be set!");
        }

        try {
            MethodHandles.Lookup lookup = LOOKUP_CREATOR.apply(this.interfaceClass);

            //get method parameters
            Class<?>[] targetParams = this.params.stream().map(param -> param.isTargetGeneric() ? Object.class : param.getType()).toArray(Class[]::new);
            Class<?>[] interfaceParams;
            {
                List<LambdaParam> temp = new LinkedList<>(this.params);
                if (!this.methodStatic) {
                    temp.add(0, new LambdaParam(this.methodHolder, false, this.interfaceTargetGeneric));
                }
                interfaceParams = temp.stream().map(param -> param.isTargetGeneric() ? Object.class : param.getType()).toArray(Class[]::new);
            }

            Method targetMethod;
            Method interfaceMethod;
            //find actual real methods
            try {
                targetMethod = this.methodHolder.getDeclaredMethod(this.methodName, targetParams);
                interfaceMethod = this.interfaceClass.getDeclaredMethod(this.interfaceName == null ? this.methodName : this.interfaceName, interfaceParams);
            } catch (NoSuchMethodException e) {
                throw PConstants.p_exception(e);
            }

            //unreflect methods into handles
            MethodHandle targetHandle;
            MethodType targetType;
            MethodType interfaceType;
            //MethodHandle interfaceHandle;
            try {
                targetHandle = lookup.unreflect(targetMethod);
                targetType = MethodType.methodType(this.returnType.isGeneric() ? Object.class : this.returnType.getType(), targetParams);
                interfaceType = MethodType.methodType(this.returnType.isInterfaceGeneric() ? Object.class : this.returnType.getType(), interfaceParams);
                //interfaceHandle = LOOKUP.unreflect(interfaceMethod);
            } catch (IllegalAccessException e) {
                throw PConstants.p_exception(e);
            }

            //actually create lambda
            try {
                PUnsafe.ensureClassInitialized(this.interfaceClass);
                CallSite site = LambdaMetafactory.metafactory(
                        lookup,
                        interfaceMethod.getName(),
                        MethodType.methodType(this.interfaceClass),
                        targetType,
                        targetHandle,
                        interfaceType
                );
                MethodHandle target = site.getTarget();
                return (T) target.invoke();
            } catch (Throwable t) {
                throw PConstants.p_exception(t);
            }
        } catch (Throwable t)   {
            if (this.fallback == null)  {
                throw PConstants.p_exception(t);
            } else {
                return this.fallback.get();
            }
        }
    }
}
