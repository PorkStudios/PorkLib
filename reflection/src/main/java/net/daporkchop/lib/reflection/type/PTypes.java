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
import net.daporkchop.lib.reflection.type.collection.MapWithTypeKeys;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * Helper class for dealing with {@link Type}.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PTypes {
    /*
     * Implementation details:
     *
     * - we assume that ParameterizedType#getRawType() always returns Class<?>
     */

    public static final Type[] EMPTY_TYPE_ARRAY = {};

    /**
     * Gets an {@link GenericArrayType array type} with the given {@link GenericArrayType#getGenericComponentType() component type}.
     *
     * @param componentType the {@link GenericArrayType#getGenericComponentType() component type}
     * @return a {@link GenericArrayType}
     */
    public static GenericArrayType array(@NonNull Type componentType) {
        return validate(new AbstractGenericArrayType() {
            @Override
            public @NonNull Type getGenericComponentType() {
                return componentType;
            }
        });
    }

    /**
     * Gets a {@link ParameterizedType parameterized type} with the given {@link ParameterizedType#getActualTypeArguments() type arguments},
     * {@link ParameterizedType#getRawType() raw type} and {@link ParameterizedType#getOwnerType() owner type}.
     *
     * @param actualTypeArguments the {@link ParameterizedType#getActualTypeArguments() type arguments}
     * @param rawType             the {@link ParameterizedType#getRawType() raw type}
     * @param ownerType           the {@link ParameterizedType#getOwnerType() owner type}
     * @return a {@link ParameterizedType}
     */
    public static ParameterizedType parameterized(@NonNull Type @NonNull [] actualTypeArguments, @NonNull Class<?> rawType, Type ownerType) {
        return validate(new AbstractParameterizedType() {
            @Override
            public @NonNull Type @NonNull [] getActualTypeArguments() {
                return actualTypeArguments.clone();
            }

            @Override
            public @NonNull Class<?> getRawType() {
                return rawType;
            }

            @Override
            public Type getOwnerType() {
                return ownerType != null ? ownerType : rawType.getDeclaringClass();
            }
        });
    }

    /**
     * Gets a {@link WildcardType wildcard type} with the given {@link WildcardType#getUpperBounds() upper bounds} and {@link WildcardType#getLowerBounds() lower bounds}.
     *
     * @param upperBounds the {@link WildcardType#getUpperBounds() upper bounds}
     * @param lowerBounds the {@link WildcardType#getLowerBounds() lower bounds}
     * @return a {@link WildcardType}
     */
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

    /**
     * Gets a {@link WildcardType wildcard type} with the given {@link WildcardType#getLowerBounds() lower bound}.
     *
     * @param lowerBound the {@link WildcardType#getLowerBounds() lower bound}
     * @return a {@link WildcardType}
     */
    public static WildcardType wildcardSuper(@NonNull Type lowerBound) {
        return wildcard(new Type[]{ Object.class }, new Type[]{ lowerBound });
    }

    /**
     * Gets a {@link WildcardType wildcard type} with the given {@link WildcardType#getUpperBounds() upper bound}.
     *
     * @param upperBound the {@link WildcardType#getUpperBounds() upper bound}
     * @return a {@link WildcardType}
     */
    public static WildcardType wildcardExtends(@NonNull Type upperBound) {
        return wildcard(new Type[]{ upperBound }, EMPTY_TYPE_ARRAY);
    }

    /**
     * Gets a {@link TypeVariable type variable} with the given {@link TypeVariable#getBounds() bounds}, {@link TypeVariable#getGenericDeclaration() generic declaration},
     * {@link TypeVariable#getName() name} and {@link TypeVariable#getAnnotatedBounds() annotated bounds}, and using the given {@link AnnotatedElement} as a source of
     * annotations.
     *
     * @param bounds             the {@link TypeVariable#getBounds() bounds}
     * @param genericDeclaration the {@link TypeVariable#getGenericDeclaration() generic declaration}
     * @param name               the {@link TypeVariable#getName() name}
     * @param annotatedBounds    the {@link TypeVariable#getAnnotatedBounds() annotated bounds}
     * @param annotationSource   a {@link AnnotatedElement} to use as a source of annotations
     * @param <D>                the {@link TypeVariable#getGenericDeclaration() generic declaration}'s type
     * @return a {@link TypeVariable}
     */
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

    /**
     * Gets an {@link AnnotatedType annotated type} with the given {@link Type} and using the given {@link AnnotatedElement} as a source of annotations.
     *
     * @param type             the {@link Type}
     * @param annotationSource a {@link AnnotatedElement} to use as a source of annotations
     * @return a {@link AnnotatedType}
     */
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

                Type originalOwnerType = type.getOwnerType();
                Type canonicalOwnerType = originalOwnerType != null ? canonicalize(originalOwnerType) : null;

                //noinspection ArrayEquality
                return originalActualTypeArguments != canonicalActualTypeArguments || originalOwnerType != canonicalOwnerType
                        ? parameterized(canonicalActualTypeArguments, (Class<?>) type.getRawType(), canonicalOwnerType) //one of the child types changed, we need to re-create the type instance
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

    protected static Type[] canonicalizeArray(Type[] originalArray) {
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
    public static int hashCode(Type type) {
        return type != null ? canonicalize(type).hashCode() : 0;
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
    public static boolean equals(Type a, Type b) {
        if (a == b) {
            return true;
        } else if (a == null || b == null) { //if either value is null, the other is non-null
            return false;
        } else {
            return canonicalize(a).equals(canonicalize(b));
        }
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
    public static String toString(Type type) {
        return type != null ? canonicalize(type).getTypeName() : "null";
    }

    /**
     * Checks whether or not the given source type is either equal to, or is a subtype of, the given target type.
     * <p>
     * Defined as {@code source <: target} in the JLS.
     *
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.10">JLS 4.10. Subtyping</a>
     */
    @SuppressWarnings({ "UnnecessaryContinue", "unused", "RedundantCast" })
    public static boolean isSubtype(@NonNull Type t, @NonNull Type s) {
        do {
            if (t instanceof Class) {
                Class<?> target = (Class<?>) t;

                if (s instanceof Class<?>) { //class <: class
                    Class<?> source = (Class<?>) s;

                    //both types are classes, delegate to ordinary Class#isAssignableFrom (this has proper handling for array types)
                    return target.isAssignableFrom(source);
                } else if (s instanceof GenericArrayType) { //array <: class
                    GenericArrayType source = (GenericArrayType) s;

                    //JLS 4.10.3. Subtyping among Array Types: (slightly reformatted for better clarity)
                    //  The following rules define the direct supertype relation among array types:
                    //  - Object >1 T[]
                    //  - Cloneable >1 T[]
                    //  - java.io.Serializable >1 T[]
                    //  - If S and T are both reference types, then S[] >1 T[] iff S >1 T.

                    if (target == Object.class || target == Cloneable.class || target == Serializable.class) { //all array types are subtypes of Object, Cloneable and Serializable
                        return true;
                    } else if (target.isArray()) { //the target type is actually a class masquerading as an array!
                        //check if the source's component type is a subtype of the target's component type. this will have proper handling for primitive arrays (it'll end up
                        //  delegating to Class#isAssignableFrom, which essentially compares object identity for primitive types).
                        t = target.getComponentType();
                        s = source.getGenericComponentType();
                        continue;
                    } else { //an array type can't be a subtype of a reference type (leaving aside the special cases we already handled)
                        return false;
                    }
                } else if (s instanceof ParameterizedType) { //parameterized class <: class
                    ParameterizedType source = (ParameterizedType) s;

                    //JLS 4.10.2. Subtyping among Class and Interface types
                    //  Given a generic type declaration C<F_1,...,F_n> (n > 0), the direct supertypes of the parameterized type C<T_1,...,T_n>, where T_i (1 <= i <= n) is a type,
                    //  are all of the following:
                    //  - D<U_1 θ,...,U_k θ>, where D<U_1,...,U_k> is a generic type which is a direct supertype of the generic type C<T_1,...,T_n> and θ is the substitution
                    //    [F_1:=T_1,...,F_n:=T_n].
                    //  - C<S_1,...,S_n>, where S_i contains T_i (1 <= i <= n) (§4.5.1).
                    //  - The type Object, if C<F_1,...,F_n> is a generic interface type with no direct superinterfaces.
                    //  - The raw type C.

                    //the target type isn't parameterized, so the source type will be accepted regardless of its parameter values. let's try again using the raw source type as the
                    //  source type.
                    s = (Class<?>) source.getRawType();
                } else if (s instanceof WildcardType) { //wildcard <: class
                    WildcardType source = (WildcardType) s;

                    //JLS 4.10.2. Subtyping among Class and Interface types
                    //  Given a generic type declaration C<F_1,...,F_n> (n > 0), the direct supertypes of the parameterized type C<R_1,...,R_n> where at least one of the
                    //  R_i (1 <= i <= n) is a wildcard type argument, are the direct supertypes of the parameterized type C<X_1,...,X_n> which is the result of applying
                    //  capture conversion to C<R_1,...,R_n> (§5.1.10).

                    throw new UnsupportedOperationException(); //TODO
                } else if (s instanceof TypeVariable) { //type variable <: class
                    TypeVariable<?> source = (TypeVariable<?>) s;

                    //JLS 4.10.2. Subtyping among Class and Interface types
                    //  The direct supertypes of a type variable are the types listed in its bound.

                    //we want to know whether the target type is a supertype of any of the type variable's bounds (actually, the other way around: whether any of the type variable's
                    //  bounds is a subtype of the target type). this requires us to recurse into each bound type.
                    for (Type sourceBound : source.getBounds()) {
                        if (isSubtype(target, sourceBound)) { //break out as soon as we have a match
                            return true;
                        }
                    }
                    return false;
                } else {
                    throw new IllegalArgumentException("don't know how to handle " + s);
                }
            } else if (t instanceof GenericArrayType) {
                GenericArrayType target = (GenericArrayType) t;

                if (s instanceof Class<?>) { //class <: array
                    Class<?> source = (Class<?>) s;

                    if (source.isArray()) { //the source type is actually a class masquerading as an array!
                        //check if the source's component type is a subtype of the target's component type. this will have proper handling for primitive arrays (it'll end up
                        //  delegating to Class#isAssignableFrom, which essentially compares object identity for primitive types).
                        t = target.getGenericComponentType();
                        s = source.getComponentType();
                        continue;
                    } else { //a reference type can't be a subtype of an array type in any case
                        return false;
                    }
                } else if (s instanceof GenericArrayType) { //array <: array
                    GenericArrayType source = (GenericArrayType) s;

                    //check if the source's component type is a subtype of the target's component type. this will have proper handling for primitive arrays (it'll end up
                    //  delegating to Class#isAssignableFrom, which essentially compares object identity for primitive types).
                    t = target.getGenericComponentType();
                    s = source.getGenericComponentType();
                    continue;
                } else if (s instanceof ParameterizedType) { //parameterized class <: array
                    ParameterizedType source = (ParameterizedType) s;

                    //only non-array reference types can be parameterized. therefore, no parameterized type can ever be a subtype of an array type.
                    return false;
                } else if (s instanceof WildcardType) { //wildcard <: array
                    WildcardType source = (WildcardType) s;

                    throw new UnsupportedOperationException(); //TODO
                } else if (s instanceof TypeVariable) { //type variable <: array
                    TypeVariable<?> source = (TypeVariable<?>) s;

                    //JLS 4.10.2. Subtyping among Class and Interface types
                    //  The direct supertypes of a type variable are the types listed in its bound.

                    //we want to know whether the target type is a supertype of any of the type variable's bounds (actually, the other way around: whether any of the type variable's
                    //  bounds is a subtype of the target type). this requires us to recurse into each bound type.
                    for (Type sourceBound : source.getBounds()) {
                        if (isSubtype(target, sourceBound)) { //break out as soon as we have a match
                            return true;
                        }
                    }
                    return false;
                } else {
                    throw new IllegalArgumentException("don't know how to handle " + s);
                }
            } else if (t instanceof ParameterizedType) {
                ParameterizedType target = (ParameterizedType) t;

                if (s instanceof Class<?>) { //class <: parameterized class
                    Class<?> source = (Class<?>) s;

                    if (source.isArray()) { //the source type is actually a class masquerading as an array!
                        //only non-array reference types can be parameterized. therefore, no array type can ever be a subtype of an parameterized type.
                        return false;
                    }

                    //JLS 4.10.2. Subtyping among Class and Interface types
                    //  Given a generic type declaration C<F_1,...,F_n> (n > 0), the direct supertypes of the parameterized type C<T_1,...,T_n>, where T_i (1 <= i <= n) is a type,
                    //  are all of the following:
                    //  - D<U_1 θ,...,U_k θ>, where D<U_1,...,U_k> is a generic type which is a direct supertype of the generic type C<T_1,...,T_n> and θ is the substitution
                    //    [F_1:=T_1,...,F_n:=T_n].
                    //  - C<S_1,...,S_n>, where S_i contains T_i (1 <= i <= n) (§4.5.1).
                    //  - The type Object, if C<F_1,...,F_n> is a generic interface type with no direct superinterfaces.
                    //  - The raw type C.

                    Class<?> rawTarget = (Class<?>) target.getRawType();

                    if (source == rawTarget) {
                        //since the raw type C (the source type, in this case) is a supertype of the parameterized type C<...> (the target type, in this case), but not vice-versa, we
                        //  can say that the source type is NOT a subtype of the target type whenever the raw types are the same.
                        return false;
                    }

                    //the only way that the type could still somehow be assignable is if it inherits a suitable parameterization from another supertype.

                    if (!rawTarget.isAssignableFrom(source)) {
                        //the source type isn't a subtype of the raw target type, therefore it cannot
                    }
                    //return false;
                    throw new UnsupportedOperationException(); //TODO
                } else if (s instanceof GenericArrayType) { //array <: parameterized class
                    GenericArrayType source = (GenericArrayType) s;

                    //an array can never be parameterized, therefore it cannot be a subtype of a parameterized type
                    return false;
                } else if (s instanceof ParameterizedType) { //parameterized class <: parameterized class
                    ParameterizedType source = (ParameterizedType) s;

                    //JLS 4.10.2. Subtyping among Class and Interface types
                    //  Given a generic type declaration C<F_1,...,F_n> (n > 0), the direct supertypes of the parameterized type C<T_1,...,T_n>, where T_i (1 <= i <= n) is a type,
                    //  are all of the following:
                    //  - D<U_1 θ,...,U_k θ>, where D<U_1,...,U_k> is a generic type which is a direct supertype of the generic type C<T_1,...,T_n> and θ is the substitution
                    //    [F_1:=T_1,...,F_n:=T_n].
                    //  - C<S_1,...,S_n>, where S_i contains T_i (1 <= i <= n) (§4.5.1).
                    //  - The type Object, if C<F_1,...,F_n> is a generic interface type with no direct superinterfaces.
                    //  - The raw type C.

                    if (equals(target.getRawType(), source.getRawType())) { //the raw types are the same
                        //  - C<S_1,...,S_n>, where S_i contains T_i (1 <= i <= n) (§4.5.1).

                        //ensure that each of the source arguments is contained by the corresponding target arguments
                        Type[] targetArguments = target.getActualTypeArguments();
                        Type[] sourceArguments = source.getActualTypeArguments();
                        for (int i = 0; i < targetArguments.length; i++) {
                            if (!containsTypeArgument(targetArguments[i], sourceArguments[i])) {
                                return false;
                            }
                        }
                        return true;
                    }

                    throw new UnsupportedOperationException(); //TODO
                } else if (s instanceof WildcardType) { //wildcard <: parameterized class
                    WildcardType source = (WildcardType) s;

                    throw new UnsupportedOperationException(); //TODO
                } else if (s instanceof TypeVariable) { //type variable <: parameterized class
                    TypeVariable<?> source = (TypeVariable<?>) s;

                    throw new UnsupportedOperationException(); //TODO
                } else {
                    throw new IllegalArgumentException("don't know how to handle " + s);
                }
            } else if (t instanceof WildcardType) {
                WildcardType target = (WildcardType) t;

                if (s instanceof Class<?>) { //class <: wildcard
                    Class<?> source = (Class<?>) s;

                    throw new UnsupportedOperationException(); //TODO
                } else if (s instanceof GenericArrayType) { //array <: wildcard
                    GenericArrayType source = (GenericArrayType) s;

                    throw new UnsupportedOperationException(); //TODO
                } else if (s instanceof ParameterizedType) { //parameterized class <: wildcard
                    ParameterizedType source = (ParameterizedType) s;

                    throw new UnsupportedOperationException(); //TODO
                } else if (s instanceof WildcardType) { //wildcard <: wildcard
                    WildcardType source = (WildcardType) s;

                    throw new UnsupportedOperationException(); //TODO
                } else if (s instanceof TypeVariable) { //type variable <: wildcard
                    TypeVariable<?> source = (TypeVariable<?>) s;

                    throw new UnsupportedOperationException(); //TODO
                } else {
                    throw new IllegalArgumentException("don't know how to handle " + s);
                }
            } else if (t instanceof TypeVariable) {
                TypeVariable<?> target = (TypeVariable<?>) t;

                if (s instanceof Class<?>) { //class <: type variable
                    Class<?> source = (Class<?>) s;

                    throw new UnsupportedOperationException(); //TODO
                } else if (s instanceof GenericArrayType) { //array <: type variable
                    GenericArrayType source = (GenericArrayType) s;

                    throw new UnsupportedOperationException(); //TODO
                } else if (s instanceof ParameterizedType) { //parameterized class <: type variable
                    ParameterizedType source = (ParameterizedType) s;

                    throw new UnsupportedOperationException(); //TODO
                } else if (s instanceof WildcardType) { //wildcard <: type variable
                    WildcardType source = (WildcardType) s;

                    throw new UnsupportedOperationException(); //TODO
                } else if (s instanceof TypeVariable) { //type variable <: type variable
                    TypeVariable<?> source = (TypeVariable<?>) s;

                    throw new UnsupportedOperationException(); //TODO
                } else {
                    throw new IllegalArgumentException("don't know how to handle " + s);
                }
            } else {
                throw new IllegalArgumentException("don't know how to handle " + t);
            }
        } while (true);
    }

    protected static boolean isWildcard(Type t) {
        if (t instanceof Class || t instanceof GenericArrayType || t instanceof ParameterizedType) {
            return false;
        } else if (t instanceof WildcardType) {
            return true;
        } else if (t instanceof TypeVariable) {
            throw unresolvedException((TypeVariable<?>) t);
        } else {
            throw new IllegalArgumentException("don't know how to handle " + t.getClass().getTypeName());
        }
    }

    /**
     * Checks whether or not the given target type argument contains the given source type argument.
     * <p>
     * Defined as {@code source <= target} in the JLS.
     *
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.5.1">JLS 4.5.1. Type Arguments of Parameterized Types</a>
     */
    public static boolean containsTypeArgument(@NonNull Type t, @NonNull Type s) {
        //JLS 4.5.1. Type Arguments of Parameterized Types:
        //  A type argument T_1 is said to contain another type argument T_2, written T_2 <= T_1, if the set of types denoted by T_2 is provably a subset of the set of types
        //  denoted by T_1 under the reflexive and transitive closure of the following rules (where <: denotes subtyping (§4.10)):
        //  - ? extends T <= ? extends S if T <: S
        //  - ? extends T <= ?
        //  - ? super T <= ? super S if S <: T
        //  - ? super T <= ?
        //  - ? super T <= ? extends Object
        //  - T <= T
        //  - T <= ? extends T
        //  - T <= ? super T

        boolean isTargetWildcard = isWildcard(t);
        boolean isSourceWildcard = isWildcard(s);

        if (isTargetWildcard) {
            WildcardType target = (WildcardType) t;
            Type[] targetUpperBounds = target.getUpperBounds();
            Type[] targetLowerBounds = target.getLowerBounds();

            assert targetUpperBounds.length <= 1 : "target has " + targetUpperBounds.length + " upper bounds!";
            assert targetLowerBounds.length <= 1 : "target has " + targetLowerBounds.length + " lower bounds!";

            if (!isSourceWildcard) { //the target type is a wildcard, but the source type is not
                //relevant rules:
                //  - ? extends T <= ? extends S if T <: S
                //  - ? extends T <= ?
                //  - ? super T <= ? super S if S <: T
                //  - ? super T <= ?
                //  - ? super T <= ? extends Object
                //  - T <= ? extends T
                //  - T <= ? super T

                //a non-wildcard source type is contained by a wildcard target type if wrapped into a wildcard type which points in the same direction (super/extends) as the target.
                //  we just need to determine which direction to wrap it in...

                if (isWildcardUnbounded(targetUpperBounds, targetLowerBounds)) { //the target type is an unbounded wildcard
                    //relevant rules:
                    //  - ? extends T <= ?
                    //  - ? super T <= ?
                    //  - ? super T <= ? extends Object

                    //it doesn't matter which way we wrap the source type, it'll always be contained
                    return true;
                } else if (isWildcardSuper(targetUpperBounds, targetLowerBounds)) { //the target type is of the form ? super Y
                    //wrap the source type into a wildcard of the form ? super X, then proceed as if both types were originally wildcards
                    s = wildcard(EMPTY_TYPE_ARRAY, new Type[]{ s });
                } else if (isWildcardExtends(targetUpperBounds, targetLowerBounds)) { //the target type is of the form ? extends Y
                    //wrap the source type into a wildcard of the form ? extends X, then proceed as if both types were originally wildcards
                    s = wildcard(new Type[]{ s }, EMPTY_TYPE_ARRAY);
                } else { //impossible
                    throw new AssertionError();
                }
            }

            //both the source and target types are wildcards

            WildcardType source = (WildcardType) s;
            Type[] sourceUpperBounds = source.getUpperBounds();
            Type[] sourceLowerBounds = source.getLowerBounds();

            assert sourceLowerBounds.length <= 1 : "source has " + sourceLowerBounds.length + " lower bounds!";
            assert sourceUpperBounds.length <= 1 : "source has " + sourceUpperBounds.length + " upper bounds!";

            //relevant rules:
            //  - ? extends T <= ? extends S if T <: S
            //  - ? extends T <= ?
            //  - ? super T <= ? super S if S <: T
            //  - ? super T <= ?
            //  - ? super T <= ? extends Object

            if (isWildcardSuper(sourceUpperBounds, sourceLowerBounds)) { //source type is of the form ? super X
                //relevant rules:
                //  - ? super T <= ? super S if S <: T
                //  - ? super T <= ?
                //  - ? super T <= ? extends Object

                if (isWildcardSuper(targetUpperBounds, targetLowerBounds)) { //target type is of the form ? super Y
                    //relevant rules:
                    //  - ? super T <= ? super S if S <: T

                    //source type is contained by target type if target type's bound is a subtype of source type's bound
                    return isSubtype(sourceLowerBounds[0], targetLowerBounds[0]);
                } else if (isWildcardUnbounded(targetUpperBounds, targetLowerBounds)) { //target type is of the form ? or ? extends Object
                    //relevant rules:
                    //  - ? super T <= ?
                    //  - ? super T <= ? extends Object

                    //source type is always contained
                    return true;
                } else if (isWildcardExtends(targetUpperBounds, targetLowerBounds)) { //target type is of the form ? extends Y
                    //relevant rules:
                    //  <none>

                    //a wildcard ? super X cannot be contained by any wildcard ? extends Y, regardless of the specific bound types
                    return false;
                } else { //impossible
                    throw new AssertionError();
                }
            } else if (isWildcardUnbounded(sourceUpperBounds, sourceLowerBounds) //target type is of the form ? or ? extends Object
                       || isWildcardExtends(sourceUpperBounds, sourceLowerBounds)) { //source type is of the form ? extends X
                //relevant rules:
                //  - ? extends T <= ? extends S if T <: S
                //  - ? extends T <= ?

                if (isWildcardSuper(targetUpperBounds, targetLowerBounds)) { //target type is of the form ? super Y
                    //relevant rules:
                    //  <none>

                    //a wildcard ? extends X cannot be contained by any wildcard ? super Y, regardless of the specific bound types
                    return false;
                } else if (isWildcardUnbounded(targetUpperBounds, targetLowerBounds)) { //target type is of the form ?
                    //relevant rules:
                    //  - ? extends T <= ?

                    //source type is always contained
                    return true;
                } else if (isWildcardExtends(targetUpperBounds, targetLowerBounds)) { //target type is of the form ? extends Y
                    //relevant rules:
                    //  - ? extends T <= ? extends S if T <: S

                    //source type is contained by target type if source type's bound is a subtype of target type's bound
                    Type sourceBoundType = isWildcardUnbounded(sourceUpperBounds, sourceLowerBounds) && sourceUpperBounds.length == 0
                            ? Object.class //sourceUpperBounds array could theoretically be empty if the wildcard is unbounded
                            : sourceUpperBounds[0];
                    return isSubtype(targetUpperBounds[0], sourceBoundType);
                } else { //impossible
                    throw new AssertionError();
                }
            } else { //impossible
                throw new AssertionError();
            }
        } else {
            if (isSourceWildcard) { //the source type is a wildcard, the target type is a non-wildcard
                //relevant rules:
                //  <none>

                //a non-wildcard type cannot contain a wildcard type.
                return false;
            } else { //neither type is a wildcard type
                //relevant rules:
                //  - T <= T

                //a non-wildcard type can only contain another non-wildcard type if they both denote the same type. we only need to check if the two types are equal!
                return equals(t, s);
            }
        }
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
                Class<?> rawType = (Class<?>) Objects.requireNonNull(type.getRawType(), "rawType");
                Type[] actualTypeArguments = requireArrayNonNull(type.getActualTypeArguments(), "actualTypeArguments");

                //ensure all argument types are valid
                for (Type actualTypeArgument : actualTypeArguments) {
                    validate(actualTypeArgument);
                }

                TypeVariable<?>[] formalTypeParameters = rawType.getTypeParameters();

                //ensure correct arity of argument count
                checkState(formalTypeParameters.length == actualTypeArguments.length,
                        "wrong number of type arguments: %s declares %d type parameters, but found %d arguments",
                        rawType, formalTypeParameters.length, actualTypeArguments.length);

                //ensure all the type parameters are within their bounds
                for (int i = 0; i < actualTypeArguments.length; i++) {
                    if (false && !isSubtype(formalTypeParameters[i], actualTypeArguments[i])) { //TODO
                        throw new IllegalStateException("invalid type arguments: parameter " + i + " (\"" + formalTypeParameters[i] + "\") expects "
                                                        + wildcard(formalTypeParameters[i].getBounds(), EMPTY_TYPE_ARRAY)
                                                        + ", but found " + actualTypeArguments[i]);
                    }
                }
            }

            @Override
            public void acceptWildcard(@NonNull WildcardType type) {
                Type[] upperBounds = requireArrayNonNull(type.getUpperBounds(), "upperBounds");
                Type[] lowerBounds = requireArrayNonNull(type.getLowerBounds(), "lowerBounds");

                checkState(upperBounds.length <= 1, "upperBounds must have at most one element!");
                checkState(lowerBounds.length <= 1, "lowerBounds may have at most one element!");
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

    /**
     * Gets the raw {@link Class} representation of the given {@link Type}.
     *
     * @param type the {@link Type}
     * @return the raw type
     * @throws IllegalArgumentException if the given {@link Type} doesn't have a raw {@link Class} representation
     * @throws NoSuchElementException   if the given {@link Type} references a {@link TypeVariable} which cannot be resolved
     * @see #raw(TypeVariableResolver, Type)
     */
    @Deprecated
    public static Class<?> raw(@NonNull Type type) {
        return raw(resolver(), type);
    }

    /**
     * Gets the raw {@link Class} representation of the given {@link Type}.
     *
     * @param resolver the {@link TypeVariableResolver} to use for resolving {@link TypeVariable}
     * @param type     the {@link Type}
     * @return the raw type
     * @throws IllegalArgumentException if the given {@link Type} doesn't have a raw {@link Class} representation
     * @throws NoSuchElementException   if the given {@link Type} references a {@link TypeVariable} which cannot be resolved
     */
    public static Class<?> raw(@NonNull TypeVariableResolver resolver, @NonNull Type type) {
        return applyWith(type, new TypedTypeFunction<Class<?>>() {
            @Override
            public Class<?> applyClass(@NonNull Class<?> type) {
                return type; //type is already raw
            }

            @Override
            public Class<?> applyGenericArray(@NonNull GenericArrayType type) {
                //get the array's raw component type
                Class<?> rawComponentType = raw(resolver, type);

                //get the array class corresponding to the raw component type
                return Array.newInstance(rawComponentType, 0).getClass(); //this is kinda gross but i'm not aware of any better way to do it
            }

            @Override
            public Class<?> applyParameterized(@NonNull ParameterizedType type) {
                return (Class<?>) type.getRawType(); //discard parameters
            }

            @Override
            public Class<?> applyWildcard(@NonNull WildcardType type) {
                throw new IllegalArgumentException("wildcard type doesn't have a raw equivalent!");
            }

            @Override
            public Class<?> applyVariable(@NonNull TypeVariable<?> type) {
                //try to resolve the type
                Optional<Type> optionalResolved = resolver.resolveTypeVariable(type);
                if (!optionalResolved.isPresent()) { //if it couldn't be resolved, there's no way to get the equivalent raw type
                    throw new NoSuchElementException("type variable " + type + " declared in " + type.getGenericDeclaration() + " couldn't be resolved, and"
                                                     + "therefore doesn't have a raw equivalent!");
                }

                Type resolved = optionalResolved.get();
                if (resolved instanceof TypeVariable) { //if it resolved to a type variable, throw exception to prevent infinite recursion
                    throw new IllegalArgumentException("type variable " + type + " declared in " + type.getGenericDeclaration() + " resolves to another type variable, and "
                                                       + "therefore doesn't have a raw equivalent!");
                }

                //try again with the now-resolved type
                return raw(resolver, resolved);
            }
        });
    }

    /**
     * Gets a default {@link TypeVariableResolver} which has no context, and will therefore be unable to resolve any types.
     *
     * @return the default {@link TypeVariableResolver}
     */
    public static TypeVariableResolver resolver() {
        return type -> Optional.empty();
    }

    public static TypeVariableResolver resolver(@NonNull Type context) {
        //before we do anything else, let's ensure the context has been canonicalized
        context = canonicalize(context);

        //get the raw context class
        Class<?> contextClass = raw(resolver(), context);
        if (contextClass.isArray()) {
            throw new IllegalArgumentException("cannot create a TypeVariableResolver from an array context!");
        }

        //recursively build the resolution table, then wrap it into a TypeVariableResolver
        Map<TypeVariable<?>, Type> resolvedTable = mapWithSpecificTypeKeys();
        buildResolver(contextClass, context, new IdentityHashMap<>(), resolvedTable);
        return resolvedTable.isEmpty() ? resolver() : resolver(resolvedTable);
    }

    protected static void buildResolver(Class<?> contextClass, Type contextType, Map<Class<?>, Type> visitedClasses, Map<TypeVariable<?>, Type> resolvedTable) {
        { //avoid visiting the same class multiple times
            Type existingInheritedType = visitedClasses.putIfAbsent(contextClass, contextType);
            if (existingInheritedType != null) { //the class has already been visited
                //this should be impossible for anything other than bytecode which was hand-crafted to be invalid, as this same check is also done by javac
                assert equals(contextType, existingInheritedType) : contextClass + " is inherited with unrelated types: " + contextType + " and " + existingInheritedType;

                //break out early so we don't process the same class again
                return;
            }
        }

        //save all the resolvable parameter values into the map
        indexResolvableParameters(contextType, resolvedTable);

        //this TypeVariableResolver will be able to resolve every type variable we've encountered so far during our recursion
        TypeVariableResolver resolver = resolver(resolvedTable);

        { //recurse into all superinterfaces
            Class<?>[] rawInterfaces = contextClass.getInterfaces();
            Type[] genericInterfaces = contextClass.getInterfaces();
            for (int i = 0; i < rawInterfaces.length; i++) {
                buildResolver(rawInterfaces[i], resolve(resolver, genericInterfaces[i]), visitedClasses, resolvedTable);
            }
        }

        { //recurse into superclass, if any
            Class<?> rawSuperclass = contextClass.getSuperclass();
            if (rawSuperclass != null) {
                buildResolver(rawSuperclass, resolve(resolver, contextClass.getGenericSuperclass()), visitedClasses, resolvedTable);
            }
        }
    }

    public static TypeVariableResolver resolver(@NonNull Map<TypeVariable<?>, Type> resolutionTable) {
        return variable -> Optional.ofNullable(resolutionTable.get(variable));
    }

    protected static Map<TypeVariable<?>, Type> indexResolvableParameters(Type context) {
        Map<TypeVariable<?>, Type> out = mapWithSpecificTypeKeys();
        indexResolvableParameters(context, out);
        return out;
    }

    protected static void indexResolvableParameters(Type context, Map<TypeVariable<?>, Type> out) {
        do {
            //process the current context type, then recurse into its owner (if any)
            context = applyWith(context, new TypedTypeFunction.ExceptionalByDefault<Type>() {
                @Override
                public Type applyClass(@NonNull Class<?> context) {
                    return null; //no more parameterized owner types remain
                }

                @Override
                public Type applyParameterized(@NonNull ParameterizedType context) {
                    Type[] arguments = context.getActualTypeArguments();
                    TypeVariable<? extends Class<?>>[] variables = ((Class<?>) context.getRawType()).getTypeParameters();
                    for (int i = 0; i < variables.length; i++) {
                        Type existingResolvedType = out.putIfAbsent(variables[i], arguments[i]);

                        //should be impossible
                        assert existingResolvedType == null : "type variable " + variables[i] + " declared in " + variables[i].getGenericDeclaration() + " was already resolved?!?";
                    }
                    return context.getOwnerType();
                }
            });
        } while (context != null);
    }

    /**
     * Resolves all {@link TypeVariable}s within the given {@link Type}.
     *
     * @param resolver the {@link TypeVariableResolver} to use
     * @param type     the {@link Type} to resolve
     * @return the resolved {@link Type}
     */
    public static Type resolve(@NonNull TypeVariableResolver resolver, @NonNull Type type) {
        return applyWith(type, new TypedTypeFunction<Type>() {
            @Override
            public Type applyClass(@NonNull Class<?> type) {
                return type; //ordinary class, doesn't need to be resolved
            }

            @Override
            public Type applyGenericArray(@NonNull GenericArrayType type) {
                Type originalComponentType = type.getGenericComponentType();
                Type resolvedComponentType = resolve(resolver, type);

                return originalComponentType != resolvedComponentType
                        ? array(resolvedComponentType) //the component type changed, we need to re-create the type instance
                        : originalComponentType; //the component type is unchanged, return original type unmodified
            }

            @Override
            public Type applyParameterized(@NonNull ParameterizedType type) {
                Type[] originalActualTypeArguments = type.getActualTypeArguments();
                Type[] resolvedActualTypeArguments = resolveArray(resolver, originalActualTypeArguments);

                Type originalOwnerType = type.getOwnerType();
                Type resolvedOwnerType = originalOwnerType != null ? resolve(resolver, originalOwnerType) : null;

                //noinspection ArrayEquality
                return originalActualTypeArguments != resolvedActualTypeArguments || originalOwnerType != resolvedOwnerType
                        ? parameterized(resolvedActualTypeArguments, (Class<?>) type.getRawType(), resolvedOwnerType) //one of the child types changed, we need to re-create the type instance
                        : type; //all of the child types are unchanged, return original type unmodified
            }

            @Override
            public Type applyWildcard(@NonNull WildcardType type) {
                Type[] originalUpperBounds = type.getUpperBounds();
                Type[] resolvedUpperBounds = resolveArray(resolver, originalUpperBounds);

                Type[] originalLowerBounds = type.getLowerBounds();
                Type[] resolvedLowerBounds = resolveArray(resolver, originalLowerBounds);

                //noinspection ArrayEquality
                return originalUpperBounds != resolvedUpperBounds || originalLowerBounds != resolvedLowerBounds
                        ? wildcard(resolvedUpperBounds, resolvedLowerBounds) //one of the child types changed, we need to re-create the type instance
                        : type; //all of the child types are unchanged, return original type unmodified
            }

            @Override
            public Type applyVariable(@NonNull TypeVariable<?> type) {
                Optional<Type> resolvedType = resolver.resolveTypeVariable(type);
                if (resolvedType.isPresent()) { //the type variable was resolved
                    return resolvedType.get();
                }

                Type[] originalBounds = type.getBounds();
                Type[] resolvedBounds = resolveArray(resolver, originalBounds);

                //noinspection ArrayEquality
                if (originalBounds != resolvedBounds) { //one of the child types changed, we need to re-create the type instance
                    //redirect the annotated bounds to the new target
                    AnnotatedType[] resolvedAnnotatedBounds = type.getAnnotatedBounds();
                    for (int i = 0; i < resolvedBounds.length; i++) {
                        resolvedAnnotatedBounds[i] = annotated(resolvedBounds[i], resolvedAnnotatedBounds[i]); //safe to modify in-place since the array has already been cloned
                    }

                    return variable(resolvedBounds, type.getGenericDeclaration(), type.getName(), resolvedAnnotatedBounds, type);
                } else { //all of the child types are unchanged, return original type unmodified
                    return type;
                }
            }
        });
    }

    protected static Type[] resolveArray(TypeVariableResolver resolver, Type[] originalArray) {
        Type[] resolvedArray = originalArray;

        for (int i = 0; i < originalArray.length; i++) {
            Type originalType = originalArray[i];
            Type resolvedType = resolve(resolver, originalType);

            if (originalType != resolvedType) {
                //noinspection ArrayEquality
                if (originalArray == resolvedArray) { //array hasn't been cloned yet because every type processed so far has been unchanged
                    resolvedArray = originalArray.clone();
                }
                resolvedArray[i] = resolvedType;
            }
        }

        return resolvedArray;
    }

    /*public static Type resolve(@NonNull Type context, @NonNull Type toResolve) {
    }

    private static Type resolve(Type context, Type toResolve, Set<TypeVariable<?>> typeVariables) {
    }

    public static Type inheritedGenericSupertype(@NonNull TypeVariableResolver resolver, @NonNull Type context, @NonNull Class<?> targetClass) {
        return inheritedGenericSupertype(context, raw(resolver, context), targetClass, Collections.newSetFromMap(new IdentityHashMap<>()));
    }

    protected static Type inheritedGenericSupertype(Type contextType, Class<?> contextClass, Class<?> targetClass, Set<Class<?>> visitedClasses) {
        if (contextClass == targetClass) { //the current context is the class we were searching for
            return contextType;
        }

        if (targetClass.isInterface()) { //the target class is an interface, so we should try to recurse into the superinterface
            Class<?>[] rawInterfaces = targetClass.getInterfaces();
            Type[] genericInterfaces = targetClass.getGenericInterfaces();
            for (int i = 0; i < rawInterfaces.length; i++) {
                if (rawInterfaces[i] == contextClass) { //the interface in question is the one we're searching for
                    return
                }
            }
        }

        { //recurse into superclass
            Class<?> superclass = contextClass.getSuperclass();
            if (superclass != null //the current context class actually has a superclass
                && visitedClasses.add(superclass) //avoid visiting the same class multiple times
                && targetClass.isAssignableFrom(superclass)) { //don't need to examine the superclass any further if it isn't assignable
                contextClass.g
                return inheritedGenericSupertype(cont)
            }
        }

        //if the target class is an interface, bfs through all of the check superinterfaces
        if (target.isInterface()) { //we
        }

        if (target.isInterface()) {
        }
    }*/

    protected static RuntimeException unresolvedException(TypeVariable<?> variable) {
        return new IllegalArgumentException("unresolved type variable: " + variable + " (declared in " + variable.getGenericDeclaration() + ')');
    }

    /**
     * Creates a new {@link Map} which uses {@link Type}s as a key type.
     *
     * @param <V> the value type
     * @return the created {@link Map}
     */
    public static <V> Map<Type, V> mapWithTypeKeys() {
        return new MapWithTypeKeys<>(new HashMap<>());
    }

    /**
     * Creates a new {@link Map} which uses a specific type of {@link Type}s as a key type.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @return the created {@link Map}
     */
    public static <K extends Type, V> Map<K, V> mapWithSpecificTypeKeys() {
        return uncheckedCast(mapWithTypeKeys());
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

    public static boolean isWildcardSuper(Type[] upperBounds, Type[] lowerBounds) {
        return lowerBounds.length > 0;
    }

    public static boolean isWildcardUnbounded(Type[] upperBounds, Type[] lowerBounds) {
        return lowerBounds.length == 0
               && (upperBounds.length == 0 || upperBounds[0] == Object.class);
    }

    public static boolean isWildcardExtends(Type[] upperBounds, Type[] lowerBounds) {
        return lowerBounds.length == 0
               && upperBounds.length != 0;
    }

    public interface TypedTypeConsumer {
        void acceptClass(@NonNull Class<?> type);

        void acceptGenericArray(@NonNull GenericArrayType type);

        void acceptParameterized(@NonNull ParameterizedType type);

        void acceptWildcard(@NonNull WildcardType type);

        void acceptVariable(@NonNull TypeVariable<?> type);

        interface NoopByDefault extends TypedTypeConsumer {
            @Override
            default void acceptClass(@NonNull Class<?> type) {
            }

            @Override
            default void acceptGenericArray(@NonNull GenericArrayType type) {
            }

            @Override
            default void acceptParameterized(@NonNull ParameterizedType type) {
            }

            @Override
            default void acceptWildcard(@NonNull WildcardType type) {
            }

            @Override
            default void acceptVariable(@NonNull TypeVariable<?> type) {
            }
        }

        interface ExceptionalByDefault extends TypedTypeConsumer {
            @Override
            default void acceptClass(@NonNull Class<?> type) {
                throw new IllegalArgumentException("unsupported argument type: " + Class.class.getTypeName() + " (given: " + type + ')');
            }

            @Override
            default void acceptGenericArray(@NonNull GenericArrayType type) {
                throw new IllegalArgumentException("unsupported argument type: " + GenericArrayType.class.getTypeName() + " (given: " + type + ')');
            }

            @Override
            default void acceptParameterized(@NonNull ParameterizedType type) {
                throw new IllegalArgumentException("unsupported argument type: " + ParameterizedType.class.getTypeName() + " (given: " + type + ')');
            }

            @Override
            default void acceptWildcard(@NonNull WildcardType type) {
                throw new IllegalArgumentException("unsupported argument type: " + WildcardType.class.getTypeName() + " (given: " + type + ')');
            }

            @Override
            default void acceptVariable(@NonNull TypeVariable<?> type) {
                throw new IllegalArgumentException("unsupported argument type: " + TypeVariable.class.getTypeName() + " (given: " + type + ')');
            }
        }
    }

    public interface TypedTypeFunction<R> {
        R applyClass(@NonNull Class<?> type);

        R applyGenericArray(@NonNull GenericArrayType type);

        R applyParameterized(@NonNull ParameterizedType type);

        R applyWildcard(@NonNull WildcardType type);

        R applyVariable(@NonNull TypeVariable<?> type);

        interface ExceptionalByDefault<R> extends TypedTypeFunction<R> {
            @Override
            default R applyClass(@NonNull Class<?> type) {
                throw new IllegalArgumentException("unsupported argument type: " + Class.class.getTypeName() + " (given: " + type + ')');
            }

            @Override
            default R applyGenericArray(@NonNull GenericArrayType type) {
                throw new IllegalArgumentException("unsupported argument type: " + GenericArrayType.class.getTypeName() + " (given: " + type + ')');
            }

            @Override
            default R applyParameterized(@NonNull ParameterizedType type) {
                throw new IllegalArgumentException("unsupported argument type: " + ParameterizedType.class.getTypeName() + " (given: " + type + ')');
            }

            @Override
            default R applyWildcard(@NonNull WildcardType type) {
                throw new IllegalArgumentException("unsupported argument type: " + WildcardType.class.getTypeName() + " (given: " + type + ')');
            }

            @Override
            default R applyVariable(@NonNull TypeVariable<?> type) {
                throw new IllegalArgumentException("unsupported argument type: " + TypeVariable.class.getTypeName() + " (given: " + type + ')');
            }
        }
    }
}
