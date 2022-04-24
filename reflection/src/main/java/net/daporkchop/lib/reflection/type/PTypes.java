/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
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

package net.daporkchop.lib.reflection.type;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Objects;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Helper class for dealing with {@link Type}.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PTypes {
    public static GenericArrayType array(@NonNull Type componentType) {
        return validate(new AbstractGenericArrayType() {
            @Override
            public @NonNull Type getGenericComponentType() {
                return componentType;
            }
        });
    }

    public static ParameterizedType parameterized(@NonNull Type @NonNull [] actualTypeArguments, @NonNull Type rawType, Type ownerType) {
        return validate(new AbstractParameterizedType() {
            @Override
            public @NonNull Type @NonNull [] getActualTypeArguments() {
                return actualTypeArguments.clone();
            }

            @Override
            public @NonNull Type getRawType() {
                return rawType;
            }

            @Override
            public Type getOwnerType() {
                return ownerType;
            }
        });
    }

    public static WildcardType wildcard(@NonNull Type @NonNull [] upperBounds, @NonNull Type @NonNull [] lowerBounds) {
        return validate(new AbstractWildcardType() {
            @Override
            public @NonNull Type @NonNull [] getUpperBounds() {
                return upperBounds.clone();
            }

            @Override
            public @NonNull Type @NonNull [] getLowerBounds() {
                return lowerBounds.clone();
            }
        });
    }

    public static <D extends GenericDeclaration> TypeVariable<D> variable(@NonNull Type[] bounds, @NonNull D genericDeclaration, @NonNull String name, @NonNull AnnotatedType[] annotatedBounds, @NonNull AnnotatedElement annotationSource) {
        AnnotatedElement realAnnotationSource = annotationSource instanceof DelegatingAnnotatedElement
                ? ((DelegatingAnnotatedElement) annotationSource).annotationSource()
                : annotationSource;

        //can't use an anonymous class because i need to extend a class and implement an interface
        class VariableTypeImpl extends AbstractTypeVariable<D> implements DelegatingAnnotatedElement {
            @Override
            public @NonNull Type @NonNull [] getBounds() {
                return bounds.clone();
            }

            @Override
            public @NonNull D getGenericDeclaration() {
                return genericDeclaration;
            }

            @Override
            public @NonNull String getName() {
                return name;
            }

            @Override
            public @NonNull AnnotatedType @NonNull [] getAnnotatedBounds() {
                return annotatedBounds.clone();
            }

            @Override
            public @NonNull AnnotatedElement annotationSource() {
                return realAnnotationSource;
            }
        }
        return validate(new VariableTypeImpl());
    }

    public static AnnotatedType annotated(@NonNull Type type, @NonNull AnnotatedElement annotationSource) {
        AnnotatedElement realAnnotationSource = annotationSource instanceof DelegatingAnnotatedElement
                ? ((DelegatingAnnotatedElement) annotationSource).annotationSource()
                : annotationSource;

        //can't use an anonymous class because i need to implement multiple interfaces
        class AnnotationRedirectingDelegate implements AnnotatedType, DelegatingAnnotatedElement {
            @Override
            public Type getType() {
                return type;
            }

            @Override
            public @NonNull AnnotatedElement annotationSource() {
                return realAnnotationSource;
            }
        }
        return new AnnotationRedirectingDelegate();
    }

    /**
     * Returns a {@link Type} which is functionally equivalent to the given {@link Type}, but may not be strictly equal according to {@link Object#equals(Object)}.
     * <p>
     * Given two non-canonical, but functionally equivalent {@link Type}s {@code a} and {@code b}, the following statements are true:
     * <br>
     * <ul>
     *     <li>{@code PTypes.equals(a, b)}</li>
     *     <li>{@code PTypes.equals(PTypes.canonicalize(a), PTypes.canonicalize(b))}</li>
     *     <li>{@code PTypes.canonicalize(a).equals(PTypes.canonicalize(b))}</li>
     * </ul>
     * <p>
     * This operation is not reversible.
     *
     * @param type the {@link Type}
     * @return the canonical {@link Type}
     */
    public static Type canonicalize(@NonNull Type type) {
        return applyWith(type, new TypedTypeFunction<Type>() {
            @Override
            public Type applyClass(@NonNull Class<?> type) {
                return type.isArray()
                        ? array(canonicalize(type.getComponentType())) //unroll into a (chain of) GenericArrayType
                        : type; //ordinary class, return original type unmodified
            }

            @Override
            public Type applyGenericArray(@NonNull GenericArrayType type) {
                Type originalComponentType = type.getGenericComponentType();
                Type canonicalComponentType = canonicalize(type);

                return originalComponentType != canonicalComponentType
                        ? array(canonicalComponentType) //the component type changed, we need to re-create the type instance 
                        : originalComponentType; //the component type is unchanged, return original type unmodified
            }

            @Override
            public Type applyParameterized(@NonNull ParameterizedType type) {
                Type[] originalActualTypeArguments = type.getActualTypeArguments();
                Type[] canonicalActualTypeArguments = canonicalizeArray(originalActualTypeArguments);

                Type originalRawType = type.getRawType();
                Type canonicalRawType = canonicalize(originalRawType);

                Type originalOwnerType = type.getOwnerType();
                Type canonicalOwnerType = originalOwnerType != null ? canonicalize(originalOwnerType) : null;

                //noinspection ArrayEquality
                return originalActualTypeArguments != canonicalActualTypeArguments || originalRawType != canonicalRawType || originalOwnerType != canonicalOwnerType
                        ? parameterized(canonicalActualTypeArguments, canonicalRawType, canonicalOwnerType) //one of the child types changed, we need to re-create the type instance
                        : type; //all of the child types are unchanged, return original type unmodified
            }

            @Override
            public Type applyWildcard(@NonNull WildcardType type) {
                Type[] originalUpperBounds = type.getUpperBounds();
                Type[] canonicalUpperBounds = canonicalizeArray(originalUpperBounds);

                Type[] originalLowerBounds = type.getLowerBounds();
                Type[] canonicalLowerBounds = canonicalizeArray(originalLowerBounds);

                //noinspection ArrayEquality
                return originalUpperBounds != canonicalUpperBounds || originalLowerBounds != canonicalLowerBounds
                        ? wildcard(canonicalUpperBounds, canonicalLowerBounds) //one of the child types changed, we need to re-create the type instance
                        : type; //all of the child types are unchanged, return original type unmodified
            }

            @Override
            public Type applyVariable(@NonNull TypeVariable<?> type) {
                Type[] originalBounds = type.getBounds();
                Type[] canonicalBounds = canonicalizeArray(originalBounds);

                //noinspection ArrayEquality
                if (originalBounds != canonicalBounds) { //one of the child types changed, we need to re-create the type instance
                    //redirect the annotated bounds to the new target
                    AnnotatedType[] canonicalAnnotatedBounds = type.getAnnotatedBounds();
                    for (int i = 0; i < canonicalBounds.length; i++) {
                        canonicalAnnotatedBounds[i] = annotated(canonicalBounds[i], canonicalAnnotatedBounds[i]); //safe to modify in-place since the array has already been cloned
                    }

                    return variable(canonicalBounds, type.getGenericDeclaration(), type.getName(), canonicalAnnotatedBounds, type);
                } else { //all of the child types are unchanged, return original type unmodified
                    return type;
                }
            }
        });
    }

    protected static Type[] canonicalizeArray(@NonNull Type[] originalArray) {
        Type[] canonicalArray = originalArray;

        for (int i = 0; i < originalArray.length; i++) {
            Type originalType = originalArray[i];
            Type canonicalType = canonicalize(originalType);

            if (originalType != canonicalType) {
                //noinspection ArrayEquality
                if (originalArray == canonicalArray) { //array hasn't been cloned yet because every type processed so far has been unchanged
                    canonicalArray = originalArray.clone();
                }
                canonicalArray[i] = canonicalType;
            }
        }

        return canonicalArray;
    }

    /**
     * Computes a hash code for the given {@link Type} which will be the same for any other functionally equal {@link Type}. This may not return the same result as
     * {@link Object#hashCode()}.
     * <p>
     * Equivalent to {@code PTypes.canonicalize(type).hashCode()}.
     *
     * @param type the {@link Type}
     * @return a hash code for the given {@link Type}
     */
    public static int hashCode(@NonNull Type type) {
        return canonicalize(type).hashCode();
    }

    /**
     * Checks whether or not the two given {@link Type}s are functionally equal. This may not return the same result as {@link Object#equals(Object)}.
     * <p>
     * Equivalent to {@code PTypes.canonicalize(a).equals(PTypes.canonicalize(b))}.
     *
     * @param a a {@link Type}
     * @param b a {@link Type}
     * @return whether or not the given {@link Type}s are functionally equal
     */
    public static boolean equals(@NonNull Type a, @NonNull Type b) {
        return canonicalize(a).equals(canonicalize(b));
    }

    /**
     * Gets a {@link String} representing the given {@link Type} which will be the same for any other functionally equal {@link Type}. This may not return the same result as
     * {@link Object#toString()}.
     * <p>
     * Equivalent to {@code PTypes.canonicalize(type).getTypeName()}.
     *
     * @param type the {@link Type}
     * @return a {@link String} representation of the given {@link Type}
     */
    public static String toString(@NonNull Type type) {
        return canonicalize(type).getTypeName();
    }

    //TODO: i am not entirely sure if this function yields the correct result in 100% of cases, but i don't know how best to verify it
    public static boolean isAssignableFrom(@NonNull Type target, @NonNull Type source) {
        //this uses two levels of applyWith(), first for the target type, then the source type, in order to cover every possible combination of assigning one type to another.

        return applyWith(target, new TypedTypeFunction<Boolean>() {
            @Override
            public Boolean applyClass(@NonNull Class<?> target) {
                return applyWith(source, new TypedTypeFunction<Boolean>() {
                    @Override
                    public Boolean applyClass(@NonNull Class<?> source) { //class <- class
                        //both types are classes, delegate to regular Class#isAssignableFrom
                        return target.isAssignableFrom(source);
                    }

                    @Override
                    public Boolean applyGenericArray(@NonNull GenericArrayType source) { //class <- array
                        if (target.isArray()) { //the target type is actually an array
                            //both types are arrays, strip one level of array-ness and recursively try again
                            return isAssignableFrom(target.getComponentType(), source.getGenericComponentType());
                        }

                        //the source type is an array, but the target type is an ordinary class. regardless of its component type, an array can only be assigned to a
                        //  non-array target if the target type is Object
                        return target == Object.class;
                    }

                    @Override
                    public Boolean applyParameterized(@NonNull ParameterizedType source) { //class <- parameterized type
                        //the target type is a class, so it doesn't care about generic parameters. strip parameters from source type and recursively try again
                        return isAssignableFrom(target, source.getRawType());
                    }

                    @Override
                    public Boolean applyWildcard(@NonNull WildcardType source) { //class <- wildcard
                        //the source type could be any type which extends from every one of the upper bound types. therefore, as long as at least one of the source upper
                        //  bound types is assignable to the target type, every type assignable to the source type will be assignable to the target type, and the source
                        //  lower bound types don't even need to be taken into account!

                        for (Type upperBound : source.getUpperBounds()) {
                            if (isAssignableFrom(upperBound, target)) {
                                return true;
                            }
                        }
                        return false;
                    }

                    @Override
                    public Boolean applyVariable(@NonNull TypeVariable<?> source) { //class <- type variable
                        //pretend the source type is a wildcard and recursively try again
                        return isAssignableFrom(target, wildcard(source.getBounds(), AbstractWildcardType.EMPTY_TYPE_ARRAY));

                        //TODO: this is wrong, we should resolve the generic type
                    }
                });
            }

            @Override
            public Boolean applyGenericArray(@NonNull GenericArrayType target) {
                return applyWith(source, new TypedTypeFunction<Boolean>() {
                    @Override
                    public Boolean applyClass(@NonNull Class<?> source) { //array <- class
                        if (source.isArray()) { //the source type is actually an array
                            //both types are arrays, strip one level of array-ness and recursively try again
                            return isAssignableFrom(target.getGenericComponentType(), source.getComponentType());
                        }

                        //the source type is an array, but the target type is an ordinary class. regardless of its component type, an array can only be assigned to a
                        //  non-array target if the target type is Object. since the target type is a GenericArrayType, it cannot possibly be Object, so the assignment
                        //  is invalid.
                        return false;
                    }

                    @Override
                    public Boolean applyGenericArray(@NonNull GenericArrayType source) { //array <- array
                        //both types are arrays, strip one level of array-ness and recursively try again
                        return isAssignableFrom(target.getGenericComponentType(), source.getGenericComponentType());
                    }

                    @Override
                    public Boolean applyParameterized(@NonNull ParameterizedType source) { //array <- parameterized type
                        //an array type can't be parameterized, so the source type is certainly not an array and therefore can't possibly be assignable
                        return false;
                    }

                    @Override
                    public Boolean applyWildcard(@NonNull WildcardType source) { //array <- wildcard
                        //an array type can't be a wildcard, so the source type is certainly not an array and therefore can't possibly be assignable
                        return false;
                    }

                    @Override
                    public Boolean applyVariable(@NonNull TypeVariable<?> source) { //array <- type variable
                        throw new UnsupportedOperationException(); //TODO
                    }
                });
            }

            @Override
            public Boolean applyParameterized(@NonNull ParameterizedType target) {
                return applyWith(source, new TypedTypeFunction<Boolean>() {
                    @Override
                    public Boolean applyClass(@NonNull Class<?> source) { //parameterized type <- class
                        throw new UnsupportedOperationException(); //TODO
                    }

                    @Override
                    public Boolean applyGenericArray(@NonNull GenericArrayType source) { //parameterized type <- array
                        throw new UnsupportedOperationException(); //TODO
                    }

                    @Override
                    public Boolean applyParameterized(@NonNull ParameterizedType source) { //parameterized type <- parameterized type
                        throw new UnsupportedOperationException(); //TODO
                    }

                    @Override
                    public Boolean applyWildcard(@NonNull WildcardType source) { //parameterized type <- wildcard
                        throw new UnsupportedOperationException(); //TODO
                    }

                    @Override
                    public Boolean applyVariable(@NonNull TypeVariable<?> source) { //parameterized type <- type variable
                        throw new UnsupportedOperationException(); //TODO
                    }
                });
            }

            @Override
            public Boolean applyWildcard(@NonNull WildcardType target) {
                return applyWith(source, new TypedTypeFunction<Boolean>() {
                    @Override
                    public Boolean applyClass(@NonNull Class<?> source) { //wildcard <- class
                        throw new UnsupportedOperationException(); //TODO
                    }

                    @Override
                    public Boolean applyGenericArray(@NonNull GenericArrayType source) { //wildcard <- array
                        throw new UnsupportedOperationException(); //TODO
                    }

                    @Override
                    public Boolean applyParameterized(@NonNull ParameterizedType source) { //wildcard <- parameterized type
                        throw new UnsupportedOperationException(); //TODO
                    }

                    @Override
                    public Boolean applyWildcard(@NonNull WildcardType source) { //wildcard <- wildcard
                        throw new UnsupportedOperationException(); //TODO
                    }

                    @Override
                    public Boolean applyVariable(@NonNull TypeVariable<?> source) { //wildcard <- type variable
                        throw new UnsupportedOperationException(); //TODO
                    }
                });
            }

            @Override
            public Boolean applyVariable(@NonNull TypeVariable<?> target) {
                return applyWith(source, new TypedTypeFunction<Boolean>() {
                    @Override
                    public Boolean applyClass(@NonNull Class<?> source) { //type variable <- class
                        throw new UnsupportedOperationException(); //TODO
                    }

                    @Override
                    public Boolean applyGenericArray(@NonNull GenericArrayType source) { //type variable <- array
                        throw new UnsupportedOperationException(); //TODO
                    }

                    @Override
                    public Boolean applyParameterized(@NonNull ParameterizedType source) { //type variable <- parameterized type
                        throw new UnsupportedOperationException(); //TODO
                    }

                    @Override
                    public Boolean applyWildcard(@NonNull WildcardType source) { //type variable <- wildcard
                        throw new UnsupportedOperationException(); //TODO
                    }

                    @Override
                    public Boolean applyVariable(@NonNull TypeVariable<?> source) { //type variable <- type variable
                        throw new UnsupportedOperationException(); //TODO
                    }
                });
            }
        });
    }

    /**
     * Ensures that the given {@link Type} is valid.
     *
     * @param type the {@link Type}
     * @return the {@link Type}
     * @throws RuntimeException if the type is invalid
     */
    public static <T extends Type> T validate(@NonNull T type) {
        acceptWith(type, new TypedTypeConsumer() {
            @Override
            public void acceptClass(@NonNull Class<?> type) {
                //no-op, class is always valid
            }

            @Override
            public void acceptGenericArray(@NonNull GenericArrayType type) {
                //recursively validate the array's component type
                validate(type.getGenericComponentType());
            }

            @Override
            public void acceptParameterized(@NonNull ParameterizedType type) {
                Type rawType = Objects.requireNonNull(type.getRawType(), "rawType");
                Type[] actualTypeArguments = requireArrayNonNull(type.getActualTypeArguments(), "actualTypeArguments");

                //ensure all argument types are valid
                for (Type actualTypeArgument : actualTypeArguments) {
                    validate(actualTypeArgument);
                }

                TypeVariable<?>[] formalTypeParameters = ((GenericDeclaration) rawType).getTypeParameters();

                //ensure correct arity of argument count
                checkState(formalTypeParameters.length == actualTypeArguments.length,
                        "wrong number of type arguments: %s declares %d type parameters, but found %d arguments",
                        rawType, formalTypeParameters.length, actualTypeArguments.length);

                //ensure all the type parameters are within their bounds
                for (int i = 0; i < actualTypeArguments.length; i++) {
                    if (!isAssignableFrom(formalTypeParameters[i], actualTypeArguments[i])) {
                        throw new IllegalStateException("invalid type arguments: parameter " + i + " (\"" + formalTypeParameters[i] + "\") expects "
                                                        + wildcard(formalTypeParameters[i].getBounds(), AbstractWildcardType.EMPTY_TYPE_ARRAY)
                                                        + ", but found " + actualTypeArguments[i]);
                    }
                }
            }

            @Override
            public void acceptWildcard(@NonNull WildcardType type) {
                Type[] upperBounds = requireArrayNonNull(type.getUpperBounds(), "upperBounds");
                Type[] lowerBounds = requireArrayNonNull(type.getLowerBounds(), "lowerBounds");

                checkState(upperBounds.length != 0, "upperBounds may not be empty!");
                checkState(lowerBounds.length == 0 || AbstractWildcardType.containsOnlyObjectClass(upperBounds), "if lowerBounds are non-empty, upperBounds must be {Object.class}");
            }

            @Override
            public void acceptVariable(@NonNull TypeVariable<?> type) {
                Objects.requireNonNull(type.getGenericDeclaration(), "genericDeclaration");

                Type[] bounds = requireArrayNonNull(type.getBounds(), "bounds");
                AnnotatedType[] annotatedBounds = requireArrayNonNull(type.getAnnotatedBounds(), "annotatedBounds");

                //ensure correct arity of (annotated) bound count
                checkState(bounds.length == annotatedBounds.length, "bounds and annotated bounds must have the same length!");
                for (int i = 0; i < annotatedBounds.length; i++) { //ensure all bounds are equal to their annotated counterparts
                    checkState(bounds[i].equals(annotatedBounds[i].getType()), "bound type is not equal to annotated bound type");
                }
            }
        });
        return type;
    }

    protected static <T> T[] requireArrayNonNull(T[] array, String name) {
        Objects.requireNonNull(array, name);
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                throw new NullPointerException(name + '[' + i + ']');
            }
        }

        return array;
    }

    public static void acceptWith(@NonNull Type type, @NonNull TypedTypeConsumer action) {
        if (type instanceof Class) {
            action.acceptClass((Class<?>) type);
        } else if (type instanceof GenericArrayType) {
            action.acceptGenericArray((GenericArrayType) type);
        } else if (type instanceof ParameterizedType) {
            action.acceptParameterized((ParameterizedType) type);
        } else if (type instanceof WildcardType) {
            action.acceptWildcard((WildcardType) type);
        } else if (type instanceof TypeVariable) {
            action.acceptVariable((TypeVariable<?>) type);
        } else {
            throw new IllegalArgumentException("don't know how to handle " + type.getClass().getTypeName());
        }
    }

    public static <R> R applyWith(@NonNull Type type, @NonNull TypedTypeFunction<R> action) {
        if (type instanceof Class) {
            return action.applyClass((Class<?>) type);
        } else if (type instanceof GenericArrayType) {
            return action.applyGenericArray((GenericArrayType) type);
        } else if (type instanceof ParameterizedType) {
            return action.applyParameterized((ParameterizedType) type);
        } else if (type instanceof WildcardType) {
            return action.applyWildcard((WildcardType) type);
        } else if (type instanceof TypeVariable) {
            return action.applyVariable((TypeVariable<?>) type);
        } else {
            throw new IllegalArgumentException("don't know how to handle " + type.getClass().getTypeName());
        }
    }

    public interface TypedTypeConsumer {
        void acceptClass(@NonNull Class<?> type);

        void acceptGenericArray(@NonNull GenericArrayType type);

        void acceptParameterized(@NonNull ParameterizedType type);

        void acceptWildcard(@NonNull WildcardType type);

        void acceptVariable(@NonNull TypeVariable<?> type);
    }

    public interface TypedTypeFunction<R> {
        R applyClass(@NonNull Class<?> type);

        R applyGenericArray(@NonNull GenericArrayType type);

        R applyParameterized(@NonNull ParameterizedType type);

        R applyWildcard(@NonNull WildcardType type);

        R applyVariable(@NonNull TypeVariable<?> type);
    }
}
