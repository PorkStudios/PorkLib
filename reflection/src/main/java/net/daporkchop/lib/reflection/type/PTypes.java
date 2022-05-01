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

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.reflection.type.collection.MapWithTypeKeys;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

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
     * java.lang.reflect.Type has 5 sub-types:
     *   - Class<?>
     *   - GenericArrayType
     *   - ParameterizedType
     *   - WildcardType
     *   - TypeVariable<?>
     *
     * Many of the exact details of the java.lang.reflect.Type API are not clearly documented. This not only means that the user is pretty much forced to use the official implementation
     * as reference material in order to make anything work, but even that two Type instances which represent the exact same type might not be equal according to Object#equals(Object),
     * and could have different hash codes or toString() representations. I've resolved this vagueness by making a number of assumptions about certain Type sub-types' properties which I
     * am reasonably certain will always hold under all implementations. Additionally, I've defined an additional set of rules for converting a Type to its 'canonical' form, which
     * should (I hope) have no ambiguity, and will ensure that two types representing the same type are actually equal.
     *
     * Assumptions:
     *   - any sub-type properties which return an array will never contain any null elements
     *   - a ParameterizedType's actual type argument count is always the same as its raw type's type parameter count
     *   - each of a ParameterizedType's actual type arguments are always within the upper bounds defined by the corresponding type variable
     *   - a ParameterizedType's raw type is always a non-array Class<?>
     *   - a ParameterizedType's owner type is always a Class<?>, a ParameterizedType or null
     *   - a ParameterizedType's owner type will always be non-null as long as its raw type is a non-static class which inherits type parameters from an enclosing class. For instance, given
     *     the following: 'class Outer<A> { class Inner<B> {}}', the type 'Outer.Inner<?>' would not be valid as the type parameter A is not defined.
     *   - a WildcardType of the form '? super ...' has >= 1 lower bounds, and its upper bounds are either an empty array or [Object.class]
     *   - an unbounded WildcardType (i.e. a WildcardType of the form '?' or '? extends Object') has 0 lower bounds, and its upper bounds are either an empty array or [Object.class]
     *   - a WildcardType of the form '? extends ...' has 0 lower bounds and >= 1 upper bounds, none of which is Object.class
     *   - an unbounded TypeVariable (i.e. a TypeVariable of the form 'T' or 'T extends Object')'s upper bounds are either an empty array or [Object.class]
     *   - a bounded TypeVariable has >= 1 upper bound, none of which is Object.class
     *   - two TypeVariables are equal as long as their generic declarations and names are equal according to Object#equals(Object)
     *
     * Canonicalization rules:
     *   - a Class representing an array is replaced by a GenericArrayType using the class's component type as a component type. In other words, T[].class is replaced with GenericArrayType(T.class).
     *   - a ParameterizedType's owner type is always non-null as long as its raw type is not a static class and the raw type's enclosing class is non-null
     *   - a ParameterizedType with no type arguments and whose owner type is either a Class or null is replaced with its raw type
     *   - a WildcardType whose upper bounds are [Object.class] is replaced with a WildcardType with the same lower bounds and whose upper bounds are an empty array. This will only affect
     *     wildcards of the form '? super ...', '?' or '? extends Object'.
     *   - these rules are applied recursively
     */

    public static final Type[] EMPTY_TYPE_ARRAY = {};
    public static final AnnotatedType[] EMPTY_ANNOTATED_TYPE_ARRAY = {};

    protected static final Type[] OBJECT_CLASS_TYPE_ARRAY = { Object.class };

    protected static final WildcardType UNBOUNDED_WILDCARD_TYPE = wildcard(OBJECT_CLASS_TYPE_ARRAY, EMPTY_TYPE_ARRAY);

    /**
     * Gets an {@link GenericArrayType array type} with the given {@link GenericArrayType#getGenericComponentType() component type}.
     *
     * @param componentType the {@link GenericArrayType#getGenericComponentType() component type}
     * @return a {@link GenericArrayType}
     */
    public static GenericArrayType array(@NonNull Type componentType) {
        return new AbstractGenericArrayType.DefaultGenericArrayType(componentType);
    }

    /**
     * Gets a {@link ParameterizedType parameterized type} with the given {@link ParameterizedType#getActualTypeArguments() type arguments},
     * {@link ParameterizedType#getRawType() raw type} and {@link ParameterizedType#getOwnerType() owner type}.
     *
     * @param rawType             the {@link ParameterizedType#getRawType() raw type}
     * @param ownerType           the {@link ParameterizedType#getOwnerType() owner type}
     * @param actualTypeArguments the {@link ParameterizedType#getActualTypeArguments() type arguments}
     * @return a {@link ParameterizedType}
     */
    public static ParameterizedType parameterized(@NonNull Class<?> rawType, Type ownerType, @NonNull Type @NonNull ... actualTypeArguments) {
        //canonicalize actualTypeArguments array
        switch (actualTypeArguments.length) {
            case 0:
                actualTypeArguments = EMPTY_TYPE_ARRAY;
                break;
            case 1:
                if (actualTypeArguments[0] == Object.class) {
                    actualTypeArguments = OBJECT_CLASS_TYPE_ARRAY;
                }
                break;
        }

        //if ownerType is unset, have it fall back to declaring class (behavior copied from sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl constructor)
        if (ownerType == null) {
            ownerType = rawType.getDeclaringClass();
        }

        return new AbstractParameterizedType.DefaultParameterizedType(actualTypeArguments, rawType, ownerType);
    }

    /**
     * Gets a {@link WildcardType wildcard type} with the given {@link WildcardType#getUpperBounds() upper bounds} and {@link WildcardType#getLowerBounds() lower bounds}.
     *
     * @param upperBounds the {@link WildcardType#getUpperBounds() upper bounds}
     * @param lowerBounds the {@link WildcardType#getLowerBounds() lower bounds}
     * @return a {@link WildcardType}
     */
    public static WildcardType wildcard(@NonNull Type @NonNull [] upperBounds, @NonNull Type @NonNull [] lowerBounds) {
        //canonicalize upperBounds array
        switch (upperBounds.length) {
            case 0:
                upperBounds = EMPTY_TYPE_ARRAY;
                break;
            case 1:
                if (upperBounds[0] == Object.class) {
                    upperBounds = OBJECT_CLASS_TYPE_ARRAY;
                }
                break;
        }

        //canonicalize lowerBounds array
        switch (lowerBounds.length) {
            case 0:
                lowerBounds = EMPTY_TYPE_ARRAY;
                break;
            case 1:
                if (lowerBounds[0] == Object.class) {
                    lowerBounds = OBJECT_CLASS_TYPE_ARRAY;
                }
                break;
        }

        return new AbstractWildcardType.DefaultWildcardType(upperBounds, lowerBounds);
    }

    /**
     * Gets a {@link WildcardType wildcard type} with the given {@link WildcardType#getLowerBounds() lower bound}.
     *
     * @param lowerBound the {@link WildcardType#getLowerBounds() lower bound}
     * @return a {@link WildcardType}
     */
    public static WildcardType wildcardSuper(@NonNull Type lowerBound) {
        return wildcard(OBJECT_CLASS_TYPE_ARRAY, lowerBound == Object.class ? OBJECT_CLASS_TYPE_ARRAY : new Type[]{ lowerBound });
    }

    /**
     * Gets a {@link WildcardType wildcard type} with the given {@link WildcardType#getLowerBounds() lower bound}s.
     *
     * @param lowerBounds the {@link WildcardType#getLowerBounds() lower bound}s
     * @return a {@link WildcardType}
     */
    public static WildcardType wildcardSuper(@NonNull Type @NonNull ... lowerBounds) {
        switch (lowerBounds.length) {
            case 0:
                return wildcardUnbounded();
            case 1:
                return wildcardSuper(lowerBounds[0]);
            default:
                return wildcard(OBJECT_CLASS_TYPE_ARRAY, lowerBounds);
        }
    }

    /**
     * Gets a {@link WildcardType wildcard type} with the given {@link WildcardType#getUpperBounds() upper bound}.
     *
     * @param upperBound the {@link WildcardType#getUpperBounds() upper bound}
     * @return a {@link WildcardType}
     */
    public static WildcardType wildcardExtends(@NonNull Type upperBound) {
        return wildcard(upperBound == Object.class ? OBJECT_CLASS_TYPE_ARRAY : new Type[]{ upperBound }, EMPTY_TYPE_ARRAY);
    }

    /**
     * Gets a {@link WildcardType wildcard type} with the given {@link WildcardType#getUpperBounds() upper bound}s.
     *
     * @param upperBounds the {@link WildcardType#getUpperBounds() upper bound}s
     * @return a {@link WildcardType}
     */
    public static WildcardType wildcardExtends(@NonNull Type @NonNull ... upperBounds) {
        switch (upperBounds.length) {
            case 0:
                return wildcardUnbounded();
            case 1:
                return wildcardExtends(upperBounds[0]);
            default:
                return wildcard(upperBounds, EMPTY_TYPE_ARRAY);
        }
    }

    /**
     * Gets an unbounded {@link WildcardType wildcard type}.
     *
     * @return a {@link WildcardType}
     */
    public static WildcardType wildcardUnbounded() {
        return UNBOUNDED_WILDCARD_TYPE;
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
        //canonicalize bounds array
        switch (bounds.length) {
            case 0:
                bounds = EMPTY_TYPE_ARRAY;
                break;
            case 1:
                if (bounds[0] == Object.class) {
                    bounds = OBJECT_CLASS_TYPE_ARRAY;
                }
                break;
        }

        //canonicalize annotatedBounds array
        if (annotatedBounds.length == 0) {
            annotatedBounds = EMPTY_ANNOTATED_TYPE_ARRAY;
        }

        //unwrap annotationSource if necessary
        while (annotationSource instanceof DelegatingAnnotatedElement) {
            annotationSource = ((DelegatingAnnotatedElement) annotationSource).annotationSource();
        }

        return new AbstractTypeVariable.DefaultTypeVariable<>(bounds, genericDeclaration, name, annotatedBounds, annotationSource);
    }

    /**
     * Gets an {@link AnnotatedType annotated type} with the given {@link Type} and using the given {@link AnnotatedElement} as a source of annotations.
     *
     * @param type             the {@link Type}
     * @param annotationSource a {@link AnnotatedElement} to use as a source of annotations
     * @return a {@link AnnotatedType}
     */
    public static AnnotatedType annotated(@NonNull Type type, @NonNull AnnotatedElement annotationSource) {
        //unwrap annotationSource if necessary
        while (annotationSource instanceof DelegatingAnnotatedElement) {
            annotationSource = ((DelegatingAnnotatedElement) annotationSource).annotationSource();
        }

        @RequiredArgsConstructor
        @Getter
        class AnnotationRedirectingDelegate implements AnnotatedType, DelegatingAnnotatedElement {
            private final Type type;
            @Accessors(fluent = true)
            private final AnnotatedElement annotationSource;
        }

        return new AnnotationRedirectingDelegate(type, annotationSource);
    }

    /**
     * Gets a {@link Type} representing the given {@link Class}, but with all type parameters filled in by their corresponding {@link TypeVariable}s.
     *
     * @param clazz the {@link Class}
     * @return the {@link Type}
     */
    public static Type fillParametersWithTypeVariables(@NonNull Class<?> clazz) {
        Type ownerType = null;
        Class<?> ownerClass = clazz.getEnclosingClass();
        if (ownerClass != null) { //class isn't a top-level class, it might have inherited some parameters from its enclosing class
            ownerType = fillParametersWithTypeVariables(ownerClass);
        }

        TypeVariable<?>[] formalArgs = clazz.getTypeParameters();
        if (formalArgs.length != 0 //the class has at least one type parameter
            || !(ownerType instanceof Class)) { //at least one of the enclosing classes has inherited a type variable from its supertype
            return parameterized(clazz, ownerType, formalArgs);
        }

        //there are no parameters to fill in
        return clazz;
    }

    /**
     * Gets a {@link Type} representing the given {@link Class}, but with all type parameters filled in by unbounded wildcards.
     *
     * @param clazz the {@link Class}
     * @return the {@link Type}
     */
    public static Type fillParametersWithWildcards(@NonNull Class<?> clazz) {
        Type ownerType = null;
        Class<?> ownerClass = clazz.getEnclosingClass();
        if (ownerClass != null) { //class isn't a top-level class, it might have inherited some parameters from its enclosing class
            ownerType = fillParametersWithWildcards(ownerClass);
        }

        TypeVariable<?>[] formalArgs = clazz.getTypeParameters();
        if (formalArgs.length != 0 //the class has at least one type parameter
            || !(ownerType instanceof Class)) { //at least one of the enclosing classes has inherited a type variable from its supertype
            Type[] actualTypeArguments;
            if (formalArgs.length == 0) { //there are no type arguments, use global empty array instance
                actualTypeArguments = EMPTY_TYPE_ARRAY;
            } else { //allocate a new array and fill it with unbounded wildcards
                actualTypeArguments = new Type[formalArgs.length];
                Arrays.fill(actualTypeArguments, wildcardUnbounded());
            }

            return parameterized(clazz, ownerType, actualTypeArguments);
        }

        //there are no parameters to fill in
        return clazz;
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
     * @param t the {@link Type}
     * @return the canonical {@link Type}
     */
    @SuppressWarnings("ArrayEquality")
    public static Type canonicalize(@NonNull Type t) {
        //see the comment at the top of this file for the rules enforced by this method

        if (t instanceof Class) {
            Class<?> type = (Class<?>) t;

            return type.isArray()
                    ? array(canonicalize(type.getComponentType())) //canonicalization: "T[].class is replaced with GenericArrayType(T.class)"
                    : type; //ordinary class, return original type unmodified
        } else if (t instanceof GenericArrayType) { //we need to canonicalize the array's component type
            GenericArrayType type = (GenericArrayType) t;

            Type originalComponentType = type.getGenericComponentType();
            Type canonicalComponentType = canonicalize(originalComponentType);

            return originalComponentType != canonicalComponentType
                    ? array(canonicalComponentType) //the component type changed, we need to re-create the type instance
                    : type; //the component type is unchanged, return original type unmodified
        } else if (t instanceof ParameterizedType) { //we need to canonicalize the type's type arguments, owner type and raw type
            ParameterizedType type = (ParameterizedType) t;

            Class<?> rawType = (Class<?>) type.getRawType();

            Type originalOwnerType = type.getOwnerType();
            Type canonicalOwnerType = originalOwnerType != null ? canonicalize(originalOwnerType) : null;

            Type[] originalActualTypeArguments = type.getActualTypeArguments();

            //canonicalization: "a ParameterizedType's owner type is always non-null as long as its raw type is not a static class and the raw type's enclosing class is non-null"
            if (canonicalOwnerType == null && (rawType.getModifiers() & Modifier.STATIC) != 0) {
                canonicalOwnerType = rawType.getEnclosingClass();
            }

            //canonicalization: "a ParameterizedType with no type arguments and whose owner type is either a Class or null is replaced with its raw type"
            if (originalActualTypeArguments.length == 0 && (canonicalOwnerType == null || canonicalOwnerType instanceof Class)) {
                return rawType;
            }

            Type[] canonicalActualTypeArguments = canonicalizeArray(originalActualTypeArguments);

            return originalActualTypeArguments != canonicalActualTypeArguments || originalOwnerType != canonicalOwnerType
                    ? parameterized(rawType, canonicalOwnerType, canonicalActualTypeArguments) //one of the child types changed, we need to re-create the type instance
                    : type; //all of the child types are unchanged, return original type unmodified
        } else if (t instanceof WildcardType) { //we need to canonicalize all of the upper and lower bounds
            WildcardType type = (WildcardType) t;

            Type[] originalUpperBounds = type.getUpperBounds();
            Type[] canonicalUpperBounds = canonicalizeArray(originalUpperBounds);

            Type[] originalLowerBounds = type.getLowerBounds();
            Type[] canonicalLowerBounds = canonicalizeArray(originalLowerBounds);

            //canonicalization: "a WildcardType whose upper bounds are [Object.class] is replaced with a WildcardType with the same lower bounds and whose upper bounds are an empty array"
            if (canonicalUpperBounds.length == 1 && canonicalUpperBounds[0] == Object.class) {
                canonicalUpperBounds = EMPTY_TYPE_ARRAY;
            }

            return originalUpperBounds != canonicalUpperBounds || originalLowerBounds != canonicalLowerBounds
                    ? wildcard(canonicalUpperBounds, canonicalLowerBounds) //one of the child types changed, we need to re-create the type instance
                    : type; //all of the child types are unchanged, return original type unmodified
        } else if (t instanceof TypeVariable) { //we need to canonicalize the type's bounds
            TypeVariable<?> type = (TypeVariable<?>) t;

            Type[] originalBounds = type.getBounds();
            Type[] canonicalBounds = canonicalizeArray(originalBounds);

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
        } else {
            throw unsupportedTypeException(t);
        }
    }

    protected static Type[] canonicalizeArray(Type[] originalArray) {
        switch (originalArray.length) {
            case 0: //use the global empty Type[]
                return EMPTY_TYPE_ARRAY;
            case 1:
                if (originalArray[0] == Object.class) { //use the global Type[] which only contains Object.class
                    return OBJECT_CLASS_TYPE_ARRAY;
                }
        }

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
     * Equivalent to {@code t != null ? PTypes.canonicalize(t).hashCode() : 0}.
     *
     * @param t the {@link Type}
     * @return a hash code for the given {@link Type}
     */
    @SuppressWarnings("UnnecessaryContinue")
    public static int hashCode(Type t) {
        //XOR is both associative and commutative. all default implementations of Type#hashCode() combine intermediary results using XOR. we can do fake recursion in more cases
        //  by tracking the current hash "state" and XOR-ing additional values onto it.
        int state = 0;

        //this method simultaneously enforces the canonicalization rules described in the comment at the beginning of this class while computing the hash code.

        do {
            if (t == null) { //hashCode of null is 0
                return state;
            } else if (t instanceof Class<?>) {
                Class<?> type = (Class<?>) t;

                while (type.isArray()) { //canonicalization: "T[].class is replaced with GenericArrayType(T.class)"
                    //  we'll pretend like it's already been unrolled into a (chain of) GenericArrayType: the hash code will be genericComponentType.hashCode()

                    //tail "recursion" into component type
                    type = type.getComponentType();
                }

                //type is an ordinary class, return its hashCode unmodified
                return state ^ type.hashCode();
            } else if (t instanceof GenericArrayType) { //genericComponentType.hashCode()
                //tail "recursion" into component type
                t = ((GenericArrayType) t).getGenericComponentType();
                continue;
            } else if (t instanceof ParameterizedType) { //Arrays.hashCode(actualTypeArguments) ^ rawType.hashCode() ^ Objects.hashCode(ownerType)
                ParameterizedType type = (ParameterizedType) t;

                Class<?> rawType = (Class<?>) type.getRawType();
                Type ownerType = type.getOwnerType();
                Type[] actualTypeArguments = type.getActualTypeArguments();

                { //try to canonicalize the type
                    //canonicalization: "a ParameterizedType's owner type is always non-null as long as its raw type is not a static class and the raw type's enclosing class is non-null"
                    if (ownerType == null && (rawType.getModifiers() & Modifier.STATIC) != 0) {
                        ownerType = rawType.getEnclosingClass();
                    }

                    //canonicalization: "a ParameterizedType with no type arguments and whose owner type is either a Class or null is replaced with its raw type"
                    if (actualTypeArguments.length == 0 && (ownerType == null || ownerType instanceof Class)) {
                        t = rawType; //replace a with its raw type and try again
                        continue;
                    }
                }

                state ^= hashCodeArray(actualTypeArguments) ^ rawType.hashCode();

                //tail "recursion" into owner type
                t = ownerType;
                continue;
            } else if (t instanceof WildcardType) { //Arrays.hashCode(upperBounds) ^ Arrays.hashCode(lowerBounds)
                WildcardType type = (WildcardType) t;

                Type[] upperBounds = type.getUpperBounds();
                Type[] lowerBounds = type.getLowerBounds();

                //canonicalization: "a WildcardType whose upper bounds are [Object.class] is replaced with a WildcardType with the same lower bounds and whose upper bounds are an empty array"
                if (upperBounds.length == 1 && upperBounds[0] == Object.class) {
                    upperBounds = EMPTY_TYPE_ARRAY;
                }

                return state ^ hashCodeArray(upperBounds) ^ hashCodeArray(lowerBounds);
            } else if (t instanceof TypeVariable) { //genericDeclaration.hashCode() ^ name.hashCode()
                TypeVariable<?> type = (TypeVariable<?>) t;

                return state ^ type.getGenericDeclaration().hashCode() ^ type.getName().hashCode();
            } else {
                throw unsupportedTypeException(t);
            }
        } while (true);
    }

    //emulate Arrays.hashCode(Object[]), but without null handling and delegating element hashing to PTypes.hashCode(type)
    protected static int hashCodeArray(Type[] array) {
        int result = 1;
        for (Type element : array) {
            result = 31 * result + hashCode(element);
        }
        return result;
    }

    /**
     * Checks whether or not the two given {@link Type}s are functionally equal. This may not return the same result as {@link Object#equals(Object)}.
     * <p>
     * Equivalent to {@code a == b || (a != null && b != null && PTypes.canonicalize(a).equals(PTypes.canonicalize(b)))}.
     *
     * @param a a {@link Type}
     * @param b a {@link Type}
     * @return whether or not the given {@link Type}s are functionally equal
     */
    @SuppressWarnings({ "ArrayEquality" })
    public static boolean equals(Type a, Type b) {
        //this method simultaneously enforces the canonicalization rules described in the comment at the beginning of this class while comparing the types.

        do {
            if (a == b) { //objects are identity equal
                return true;
            } else if (a == null || b == null) { //exactly one of the given types is null
                return false;
            } else if (a instanceof Class) {
                Class<?> a0 = (Class<?>) a;

                //even if 'b instanceof Class', it wouldn't make a difference: we already checked above to see if the objects are identity equal. there are only two ways the types could
                //  still be equal:
                //  - a could be an array class (which isn't canonical and therefore hasn't been replaced with GenericArrayType), while b is a GenericArrayClass
                //  - b could be a non-canonical ParameterizedType which would be replaced by its raw type during canonicalization, and the raw type could be equal to a
                if (a0.isArray()) {
                    if (b instanceof GenericArrayType) {
                        //tail "recursion" with one level of array-ness removed
                        a = a0.getComponentType();
                        b = ((GenericArrayType) b).getGenericComponentType();
                        continue;
                    }
                } else if (b instanceof ParameterizedType) {
                    ParameterizedType b0 = (ParameterizedType) b;

                    Class<?> bRaw = (Class<?>) b0.getRawType();

                    if (a0 != bRaw) { //if the raw type isn't equal to a, canonicalization wouldn't make the types match
                        return false;
                    }

                    Type ownerType = b0.getOwnerType();
                    Type[] actualTypeArguments = b0.getActualTypeArguments();

                    //canonicalization: "a ParameterizedType's owner type is always non-null as long as its raw type is not a static class and the raw type's enclosing class is non-null"
                    if (ownerType == null && (bRaw.getModifiers() & Modifier.STATIC) != 0) {
                        ownerType = bRaw.getEnclosingClass();
                    }

                    //canonicalization: "a ParameterizedType with no type arguments and whose owner type is either a Class or null is replaced with its raw type"
                    //  we know that the raw types are equal, so once b has been replaced with its raw type a and b will be equal
                    return actualTypeArguments.length == 0 && (ownerType == null || ownerType instanceof Class);
                }
            } else if (a instanceof GenericArrayType) {
                GenericArrayType a0 = (GenericArrayType) a;

                if (b instanceof Class) { //b could be an array class (which isn't canonical and therefore hasn't been unrolled into a GenericArrayType)
                    Class<?> b0 = (Class<?>) b;

                    if (b0.isArray()) {
                        //tail "recursion" with one level of array-ness removed
                        a = a0.getGenericComponentType();
                        b = b0.getComponentType();
                        continue;
                    }
                } else if (b instanceof GenericArrayType) {
                    //tail "recursion" with one level of array-ness removed
                    a = a0.getGenericComponentType();
                    b = ((GenericArrayType) b).getGenericComponentType();
                    continue;
                }
            } else if (a instanceof ParameterizedType) {
                if (!(b instanceof ParameterizedType || b instanceof Class)) { //if b isn't either a parameterized type or a class, the types cannot be equal
                    return false;
                }

                ParameterizedType a0 = (ParameterizedType) a;

                Class<?> aRaw = (Class<?>) a0.getRawType();
                Type aOwnerType = a0.getOwnerType();
                Type[] aArgs = a0.getActualTypeArguments();

                { //try to canonicalize a
                    //canonicalization: "a ParameterizedType's owner type is always non-null as long as its raw type is not a static class and the raw type's enclosing class is non-null"
                    if (aOwnerType == null && (aRaw.getModifiers() & Modifier.STATIC) != 0) {
                        aOwnerType = aRaw.getEnclosingClass();
                    }

                    //canonicalization: "a ParameterizedType with no type arguments and whose owner type is either a Class or null is replaced with its raw type"
                    if (aArgs.length == 0 && (aOwnerType == null || aOwnerType instanceof Class)) {
                        a = aRaw; //replace a with its raw type and try again
                        continue;
                    }
                }

                if (b instanceof ParameterizedType) {
                    ParameterizedType b0 = (ParameterizedType) b;

                    Class<?> bRaw = (Class<?>) b0.getRawType();
                    if (aRaw != bRaw) { //if the raw types aren't equal, the parameterized types can't be equal either
                        return false;
                    }

                    Type bOwnerType = b0.getOwnerType();
                    Type[] bArgs = b0.getActualTypeArguments();

                    { //try to canonicalize b
                        //canonicalization: "a ParameterizedType's owner type is always non-null as long as its raw type is not a static class and the raw type's enclosing class is non-null"
                        if (bOwnerType == null && (bRaw.getModifiers() & Modifier.STATIC) != 0) {
                            bOwnerType = bRaw.getEnclosingClass();
                        }

                        //canonicalization: "a ParameterizedType with no type arguments and whose owner type is either a Class or null is replaced with its raw type"
                        if (bArgs.length == 0 && (bOwnerType == null || bOwnerType instanceof Class)) {
                            //we already know that a is canonical but hasn't been replaced with a class. a parameterized type can't equal a class, so the types aren't equal
                            return false;
                        }
                    }

                    //check if the arguments are equal
                    if (aArgs != bArgs && (aArgs.length != bArgs.length || !equalsArray(aArgs, bArgs))) {
                        return false;
                    }

                    //tail "recursion" with owner type, if any
                    a = aOwnerType;
                    b = bOwnerType;
                    continue;
                }
            } else if (a instanceof WildcardType) {
                WildcardType a0 = (WildcardType) a;

                if (b instanceof WildcardType) {
                    WildcardType b0 = (WildcardType) b;

                    Type[] aUpperBounds = a0.getUpperBounds();
                    Type[] aLowerBounds = a0.getLowerBounds();

                    //canonicalization: "a WildcardType whose upper bounds are [Object.class] is replaced with a WildcardType with the same lower bounds and whose upper bounds are an empty array"
                    if (aUpperBounds.length == 1 && aUpperBounds[0] == Object.class) {
                        aUpperBounds = EMPTY_TYPE_ARRAY;
                    }

                    Type[] bUpperBounds = b0.getUpperBounds();
                    Type[] bLowerBounds = b0.getLowerBounds();

                    //canonicalization: "a WildcardType whose upper bounds are [Object.class] is replaced with a WildcardType with the same lower bounds and whose upper bounds are an empty array"
                    if (bUpperBounds.length == 1 && bUpperBounds[0] == Object.class) {
                        bUpperBounds = EMPTY_TYPE_ARRAY;
                    }

                    return (aUpperBounds == bUpperBounds || (aUpperBounds.length == bUpperBounds.length && equalsArray(aUpperBounds, bUpperBounds))) //upper bounds are equal
                           && (aLowerBounds == bLowerBounds || (aLowerBounds.length == bLowerBounds.length && equalsArray(aLowerBounds, bLowerBounds))); //lower bounds are equal
                }
            } else if (a instanceof TypeVariable && b instanceof TypeVariable) {
                TypeVariable<?> a0 = (TypeVariable<?>) a;
                TypeVariable<?> b0 = (TypeVariable<?>) b;

                //only generic declaration and variable name need to be compared
                return a0.getGenericDeclaration().equals(b0.getGenericDeclaration())
                       && a0.getName().equals(b0.getName());
            }

            //this would be skipped if we were doing a fast emulated tail "recursion", so if we get this far the types must not be equal
            return false;
        } while (true);
    }

    protected static boolean equalsArray(Type[] a, Type[] b) {
        if (a.length != b.length) {
            return false;
        } else {
            for (int i = 0; i < a.length; i++) {
                if (!equals(a[i], b[i])) { //abort immediately if any of the arguments don't match
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Gets a {@link String} representing the given {@link Type} which will be the same for any other functionally equal {@link Type}. This may not return the same result as
     * {@link Object#toString()}.
     * <p>
     * Equivalent to {@code type != null ? PTypes.canonicalize(type).getTypeName() : "null"}.
     *
     * @param type the {@link Type}
     * @return a {@link String} representation of the given {@link Type}
     */
    public static String toString(Type type) {
        if (type == null) {
            return "null";
        }

        StringBuilder builder = new StringBuilder();
        toString(builder, type);
        return builder.toString();
    }

    private static void toString(StringBuilder builder, Type t) {
        //this method simultaneously enforces the canonicalization rules described in the comment at the beginning of this class while stringifying the type.

        if (t instanceof Class) {
            Class<?> type = (Class<?>) t;

            if (type.isArray()) { //canonicalization: "T[].class is replaced with GenericArrayType(T.class)"
                //figure out how many dimensions the array has
                int dimensions = 0;
                do {
                    dimensions++;
                    type = type.getComponentType();
                } while (type.isArray());

                //append the component type's regular class name
                builder.append(type.getName());

                //suffix with as many "[]"s as there are array dimensions
                do {
                    builder.append("[]");
                } while (--dimensions != 0);
            } else { //the type is an ordinary class
                builder.append(type.getName());
            }
        } else if (t instanceof GenericArrayType) { //recursively stringify the array's component type, then append a "[]" suffix
            toString(builder, ((GenericArrayType) t).getGenericComponentType());
            builder.append("[]");
        } else if (t instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) t;

            Class<?> rawType = (Class<?>) type.getRawType();
            Type ownerType = type.getOwnerType();
            Type[] actualTypeArguments = type.getActualTypeArguments();

            { //try to canonicalize the type
                //canonicalization: "a ParameterizedType's owner type is always non-null as long as its raw type is not a static class and the raw type's enclosing class is non-null"
                if (ownerType == null && (rawType.getModifiers() & Modifier.STATIC) != 0) {
                    ownerType = rawType.getEnclosingClass();
                }

                //canonicalization: "a ParameterizedType with no type arguments and whose owner type is either a Class or null is replaced with its raw type"
                if (actualTypeArguments.length == 0 && (ownerType == null || ownerType instanceof Class)) {
                    //we know that the raw type can't be an array class, so we can be safe in the knowledge that it's an ordinary class and simply append its regular name
                    builder.append(rawType.getName());
                    return;
                }
            }

            if (ownerType != null) { //the type has an owner type, the raw type needs to be prefixed with it
                //prefix this type with the owner type by recursively stringify-ing the owner type
                toString(builder, ownerType);

                //append the raw type's name with it's owner class' name stripped from it. this will automatically include the '$' separator.
                String rawName = rawType.getName();
                builder.append(rawName, rawType.getEnclosingClass().getName().length(), rawName.length());
            } else { //simply append the raw type's name
                builder.append(rawType.getName());
            }

            if (actualTypeArguments.length > 0) { //recursively stringify the type arguments
                builder.append('<');
                toString(builder, actualTypeArguments[0]);
                for (int i = 1; i < actualTypeArguments.length; i++) {
                    builder.append(", ");
                    toString(builder, actualTypeArguments[i]);
                }
                builder.append('>');
            }
        } else if (t instanceof WildcardType) {
            WildcardType type = (WildcardType) t;

            Type[] upperBounds = type.getUpperBounds();
            Type[] lowerBounds = type.getLowerBounds();

            if (isWildcardUnbounded(upperBounds, lowerBounds)) { //?
                builder.append('?');
                return;
            }

            Type[] bounds;
            if (isWildcardSuper(upperBounds, lowerBounds)) { //? super X { & Y...}
                builder.append("? super ");
                bounds = lowerBounds;
            } else if (isWildcardExtendsAny(upperBounds, lowerBounds)) { //? extends X { & Y...}
                builder.append("? extends ");
                bounds = upperBounds;
            } else {
                throw new AssertionError(); //impossible
            }

            //recursively stringify bound types
            toString(builder, bounds[0]);
            for (int i = 1; i < bounds.length; i++) {
                builder.append(" & ");
                toString(builder, bounds[i]);
            }
        } else if (t instanceof TypeVariable) { //simply append the type variable's name
            builder.append(((TypeVariable) t).getName());
        } else {
            throw unsupportedTypeException(t);
        }
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
                    continue;
                } else if (!(s instanceof WildcardType || s instanceof TypeVariable)) { //WildcardType and TypeVariable are handled below
                    throw unsupportedTypeException(s);
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
                } else if (!(s instanceof WildcardType || s instanceof TypeVariable)) { //WildcardType and TypeVariable are handled below
                    throw unsupportedTypeException(s);
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
                    } else if (rawTarget.isAssignableFrom(source)) { //the raw source type is a subtype of the raw target type, it could inherit a valid parameterization from one of its superclasses
                        //  - D<U_1 θ,...,U_k θ>, where D<U_1,...,U_k> is a generic type which is a direct supertype of the generic type C<T_1,...,T_n> and θ is the substitution
                        //    [F_1:=T_1,...,F_n:=T_n].

                        //find the source's inherited generic parameters for the raw target class, then check again to see if those are acceptable
                        s = inheritedGenericSupertype(source, rawTarget);
                        continue;
                    } else { //if the raw source type isn't a raw subtype of the raw target type, no amount of type parameters will be able to fix that
                        return false;
                    }
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

                    Class<?> rawTarget = (Class<?>) target.getRawType();
                    Class<?> rawSource = (Class<?>) source.getRawType();

                    if (rawSource == rawTarget) { //the raw types are the same
                        //  - C<S_1,...,S_n>, where S_i contains T_i (1 <= i <= n) (§4.5.1).

                        //ensure that each of the source arguments is contained by the corresponding target arguments
                        Type[] targetArguments = target.getActualTypeArguments();
                        Type[] sourceArguments = source.getActualTypeArguments();
                        for (int i = 0; i < targetArguments.length; i++) {
                            if (!containsTypeArgument(targetArguments[i], sourceArguments[i])) {
                                return false;
                            }
                        }

                        //ensure that the source type's owner type is a subtype of the target type's owner type
                        Type targetOwner = target.getOwnerType();
                        Type sourceOwner = source.getOwnerType();
                        if (sourceOwner == targetOwner) { //the owner types are identity equal (the most likely scenario is that they're both null)
                            return true;
                        } else if (sourceOwner == null || targetOwner == null) { //exactly one of the owner types is null, but the other one isn't
                            return false;
                        } else { //both owner types are non-null
                            //check again using the owner types
                            t = targetOwner;
                            s = sourceOwner;
                            continue;
                        }
                    } else if (rawTarget.isAssignableFrom(rawSource)) { //the raw source type is a subtype of the raw target type
                        //  - D<U_1 θ,...,U_k θ>, where D<U_1,...,U_k> is a generic type which is a direct supertype of the generic type C<T_1,...,T_n> and θ is the substitution
                        //    [F_1:=T_1,...,F_n:=T_n].

                        //find the source's inherited generic parameters for the raw target class, then check again to see if those are acceptable
                        s = inheritedGenericSupertype(source, rawTarget);
                        continue;
                    } else { //if the raw source type isn't a raw subtype of the raw target type, no amount of type parameters will be able to fix that
                        return false;
                    }
                } else if (!(s instanceof WildcardType || s instanceof TypeVariable)) { //WildcardType and TypeVariable are handled below
                    throw unsupportedTypeException(s);
                }
            } else if (t instanceof TypeVariable) {
                TypeVariable<?> target = (TypeVariable<?>) t;

                //JLS 4.10.2. Subtyping among Class and Interface types
                //  The direct supertypes of a type variable are the types listed in its bound.

                //we want to know whether the source type is a subtype of all of the type variable's bounds. this requires us to recurse into each bound type.
                Type[] targetBounds = target.getBounds();
                switch (targetBounds.length) {
                    default:
                        for (Type targetBound : targetBounds) {
                            if (!isSubtype(targetBound, s)) { //break out as soon as one doesn't match
                                return false;
                            }
                        }
                        return true;

                    //special cases: use fast emulated tail "recursion" if there are 0 or 1 bounds
                    case 0: //target type has no bounds, therefore it implicitly extends Object
                        t = Object.class;
                        continue;
                    case 1: //target type has no bounds, therefore it implicitly extends Object
                        t = targetBounds[0];
                        continue;
                }
            } else if (t instanceof WildcardType) { //pretend like we actually support capture conversion
                WildcardType target = (WildcardType) t;

                Type[] targetUpperBounds = target.getUpperBounds();
                Type[] targetLowerBounds = target.getLowerBounds();

                if (isWildcardSuper(targetUpperBounds, targetLowerBounds)) { //target type is a wildcard of form ? super ...
                    /*
                     * code example of what's going on here:
                     *
                     *   List<? super CharSequence> list = new ArrayList<>();
                     *   list.add((CharSequence) ""); //list.add(""); would also be valid
                     */

                    //for some reason which i don't entirely understand, the super is accepting any SUBtype of its bound (which is what i would have expected to occur if the target type
                    //  were '? extends CharSequence', and this code actually compiles. i believe it's related to the following rules from the JLS:

                    //JLS 4.5.1. Type Arguments of Parameterized Types:
                    //  - ? super T <= ? super S if S <: T
                    //  - T <= ? super T

                    //my best guess is that these rules are somehow also being applied during capture conversion (see JLS 5.1.10. Capture Conversion), but since i haven't yet managed to
                    //  decipher the spec's definition of capture conversion, i can't actually support it. for now, i'll just assume that it does, in fact, get treated as an extends and
                    //  leave it at that.

                    //we want to know whether the source type is a subtype of all of the target wildcard's lower bounds. this requires us to recurse into each bound type.
                    switch (targetLowerBounds.length) {
                        default:
                            for (Type targetLowerBound : targetLowerBounds) {
                                if (!isSubtype(targetLowerBound, s)) { //break out as soon as one doesn't match
                                    return false;
                                }
                            }
                            return true;

                        //special cases: use fast emulated tail "recursion" if there are 0 or 1 bounds
                        case 0: //this is impossible, isWildcardSuper() requires that there be at least one lower bound
                            throw new AssertionError();
                        case 1: //target type has exactly one lower bound
                            t = targetLowerBounds[0];
                            continue;
                    }
                } else {
                    /*
                     * code example of what's going on here:
                     *
                     *   List<? extends String> l1 = Arrays.asList(null, null);
                     *   l0.set(0, null); //no actual may be passed as a value here, not even Object
                     */

                    return false;
                }
            } else {
                throw unsupportedTypeException(t);
            }

            if (s instanceof TypeVariable) { //special case: source type is a type variable
                TypeVariable<?> source = (TypeVariable<?>) s;

                //JLS 4.10.2. Subtyping among Class and Interface types
                //  The direct supertypes of a type variable are the types listed in its bound.

                //we want to know whether the target type is a supertype of any of the type variable's bounds (actually, the other way around: whether any of the type variable's
                //  bounds is a subtype of the target type). this requires us to recurse into each bound type.
                Type[] sourceBounds = source.getBounds();
                switch (sourceBounds.length) {
                    default:
                        for (Type sourceBound : sourceBounds) {
                            if (isSubtype(t, sourceBound)) { //break out as soon as we have a match
                                return true;
                            }
                        }
                        return false;

                    //special cases: use fast emulated tail "recursion" if there are 0 or 1 bounds
                    case 0: //source type has no bounds, therefore it implicitly extends Object
                        s = Object.class;
                        continue;
                    case 1: //source type has exactly one bound
                        s = sourceBounds[0];
                        continue;
                }
            } else /* if (s instanceof Wildcard) */ { //special case: source type is a wildcard, pretend like we actually support capture conversion
                WildcardType source = (WildcardType) s;

                Type[] sourceUpperBounds = source.getUpperBounds();
                Type[] sourceLowerBounds = source.getLowerBounds();

                if (sourceLowerBounds.length != 0) { //source type is a wildcard of form ? super ...
                    /*
                     * code example of what's going on here:
                     *
                     *   List<? super String> l0;
                     *   Object var = l0.get(0); //any type other than Object is not accepted
                     */

                    //the source type could be any supertype of its lower bound, all the way up to Object. therefore, the target type must be a supertype of Object.

                    s = Object.class;
                    continue; //tail "recursion"
                } else { //source type is either an unbounded wildcard (i.e. ? extends Object), or a wildcard of form ? extends ...
                    //we want to know whether the target type is a supertype of all of the source wildcard's upper bounds (actually, the other way around: whether all of the source wildcard's
                    //  upper bounds are a subtype of the target type). this requires us to recurse into each upper bound type.
                    switch (sourceUpperBounds.length) {
                        default:
                            for (Type sourceUpperBound : sourceUpperBounds) {
                                if (!isSubtype(t, sourceUpperBound)) { //break out as soon as one doesn't match
                                    return false;
                                }
                            }
                            return true;

                        //special cases: use fast emulated tail "recursion" if there are 0 or 1 bounds
                        case 0: //source type has no upper bounds, which makes it an unbounded wildcard (i.e. ? extends Object)
                            s = Object.class;
                            continue;
                        case 1: //source type has exactly one upper bound
                            s = sourceUpperBounds[0];
                            continue;
                    }
                }
            }
        } while (true);
    }

    protected static boolean isWildcard(Type t) {
        if (t instanceof Class || t instanceof GenericArrayType || t instanceof ParameterizedType || t instanceof TypeVariable) {
            return false;
        } else if (t instanceof WildcardType) {
            return true;
        } else {
            throw unsupportedTypeException(t);
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
                    s = wildcardSuper(s);
                } else if (isWildcardExtendsAny(targetUpperBounds, targetLowerBounds)) { //the target type is of the form ? extends Y
                    //wrap the source type into a wildcard of the form ? extends X, then proceed as if both types were originally wildcards
                    s = wildcardExtends(s);
                } else { //impossible
                    throw new AssertionError();
                }
            }

            //both the source and target types are wildcards

            WildcardType source = (WildcardType) s;
            Type[] sourceUpperBounds = source.getUpperBounds();
            Type[] sourceLowerBounds = source.getLowerBounds();

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
                } else if (isWildcardExtendsAny(targetUpperBounds, targetLowerBounds)) { //target type is of the form ? extends Y
                    //relevant rules:
                    //  <none>

                    //a wildcard ? super X cannot be contained by any wildcard ? extends Y, regardless of the specific bound types
                    return false;
                } else { //impossible
                    throw new AssertionError();
                }
            } else if (isWildcardUnbounded(sourceUpperBounds, sourceLowerBounds) //target type is of the form ? or ? extends Object
                       || isWildcardExtendsAny(sourceUpperBounds, sourceLowerBounds)) { //source type is of the form ? extends X
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
                } else if (isWildcardExtendsAny(targetUpperBounds, targetLowerBounds)) { //target type is of the form ? extends Y
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
     * @param t the {@link Type}
     * @throws RuntimeException if the type is invalid
     */
    public static void validate(@NonNull Type t) {
        do {
            if (t instanceof Class) {
                //no-op, class is always valid
            } else if (t instanceof GenericArrayType) { //we need to validate the array's component type
                GenericArrayType type = (GenericArrayType) t;

                //tail "recursion" into the component type
                t = type.getGenericComponentType();
                continue;
            } else if (t instanceof ParameterizedType) { //we need to validate the type's type arguments and owner type, and make sure that the raw type is a Class<?>
                ParameterizedType type = (ParameterizedType) t;

                Class<?> rawType = (Class<?>) Objects.requireNonNull(type.getRawType(), "rawType");
                Type[] actualTypeArguments = requireArrayNonNull(type.getActualTypeArguments(), "actualTypeArguments");

                //recursively validate all argument types
                for (Type actualTypeArgument : actualTypeArguments) {
                    validate(actualTypeArgument);
                }

                TypeVariable<?>[] formalTypeParameters = rawType.getTypeParameters();

                //ensure correct arity of argument count
                if (formalTypeParameters.length != actualTypeArguments.length) {
                    throw new IllegalStateException("wrong number of type arguments: " + rawType + " declares " + formalTypeParameters.length + " type parameters, but found "
                                                    + actualTypeArguments.length + " arguments");
                }

                //ensure all the type parameters are within their bounds

                //get a parameterized type with a matching raw type, but using all of the parameter's bounds as wildcards
                Type rawBoundedType = new AbstractParameterizedType.DefaultParameterizedType(
                        Stream.of(formalTypeParameters).map(param -> wildcardExtends(param.getBounds())).toArray(Type[]::new),
                        rawType, type.getOwnerType());
                //resolve any type variables from the type produced in the previous stage, to account for the case where one parameter references another parameter in its bounds
                rawBoundedType = resolve(resolver(type), rawBoundedType);
                //check if the given type is a valid subtype of the raw bounds with resolved type variables
                if (!isSubtype(rawBoundedType, type)) {
                    throw new IllegalStateException("invalid type arguments: " + toString(type) + " does not conform to bounds: " + toString(rawBoundedType));
                }

                t = type.getOwnerType();
                if (t != null) { //tail "recursion" into the owner type
                    continue;
                }
            } else if (t instanceof WildcardType) { //we need to validate the type's upper and lower bounds
                WildcardType type = (WildcardType) t;

                Type[] upperBounds = requireArrayNonNull(type.getUpperBounds(), "upperBounds");
                Type[] lowerBounds = requireArrayNonNull(type.getLowerBounds(), "lowerBounds");

                if (lowerBounds.length != 0) { //lower bounds are non-empty, meaning this wildcard is of the form ? super X { & Y...}
                    if (upperBounds.length > 1 || (upperBounds.length == 1 && upperBounds[0] != Object.class)) {
                        throw new IllegalStateException("if lowerBounds is non-empty, upperBounds must either be empty or [Object.class], but it was " + Arrays.toString(upperBounds));
                    }
                } else if (upperBounds.length != 0) { //upper bounds are non-empty, meaning this wildcard is of the form ? extends X { & Y...}
                    if (upperBounds.length > 1 && upperBounds[0] == Object.class) {
                        throw new IllegalStateException("if upperBounds[0] is Object.class, no additional upper bounds may be present, but upperBounds was " + Arrays.toString(upperBounds));
                    }
                }

                //recursively validate all upper bounds
                for (Type upperBound : upperBounds) {
                    validate(upperBound);
                }

                //recursively validate all lower bounds
                for (Type lowerBound : lowerBounds) {
                    validate(lowerBound);
                }
            } else if (t instanceof TypeVariable) { //we need to validate the type variable's bounds, and make sure that all the annotated bounds are strictly equal
                TypeVariable<?> type = (TypeVariable<?>) t;

                Type[] bounds = requireArrayNonNull(type.getBounds(), "bounds");

                //recursively validate all bounds
                for (Type bound : bounds) {
                    validate(bound);
                }

                AnnotatedType[] annotatedBounds = requireArrayNonNull(type.getAnnotatedBounds(), "annotatedBounds");

                //ensure correct arity of annotated bound count
                if (bounds.length != annotatedBounds.length) {
                    throw new IllegalStateException("length mismatch between bounds and annotated bounds: found " + bounds.length + " bounds, but " + annotatedBounds.length
                                                    + " annotated bounds");
                }

                //ensure all bounds are equal to their annotated counterparts
                for (int i = 0; i < bounds.length; i++) {
                    if (!equals(bounds[i], annotatedBounds[i].getType())) {
                        throw new IllegalStateException("bound #" + i + " differs between raw and annotated bounds: regular bound" + toString(bounds[i])
                                                        + " does not equal annotated bound " + toString(annotatedBounds[i].getType()));
                    }
                }
            } else {
                throw unsupportedTypeException(t);
            }

            //there's nothing left to be validated!
            //  this will be skipped by 'continue;' in the event of a tail "recursion".
            return;
        } while (true);
    }

    /**
     * Ensures that the given {@link Type} is valid.
     *
     * @param t the {@link Type}
     * @return the {@link Type}
     * @throws RuntimeException if the type is invalid
     */
    public static <T extends Type> T validateAndGet(T t) {
        validate(t);
        return t;
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
     */
    public static Class<?> raw(@NonNull Type type) {
        if (type instanceof Class) {
            return (Class<?>) type; //type is already raw
        } else if (type instanceof GenericArrayType) {
            //get the array's raw component type
            Class<?> rawComponentType = raw(((GenericArrayType) type).getGenericComponentType());

            //get the array class corresponding to the raw component type
            return Array.newInstance(rawComponentType, 0).getClass(); //this is kinda gross but i'm not aware of any better way to do it
        } else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType(); //discard type arguments
        } else if (type instanceof WildcardType) {
            throw wildcardNotAllowedException(type);
        } else if (type instanceof TypeVariable) {
            throw unresolvedException((TypeVariable<?>) type);
        } else {
            throw unsupportedTypeException(type);
        }
    }

    /**
     * Gets a default {@link TypeVariableResolver} which has no context, and will therefore be unable to resolve any types.
     *
     * @return the default {@link TypeVariableResolver}
     */
    public static TypeVariableResolver resolver() {
        return type -> Optional.empty();
    }

    /**
     * Gets a {@link TypeVariableResolver} which can resolve type variables in the given context.
     *
     * @param context the context to resolve type variables in
     * @return a {@link TypeVariableResolver}
     */
    public static TypeVariableResolver resolver(@NonNull Type context) {
        //get the raw context class
        Class<?> contextClass = raw(context);
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
            Type[] genericInterfaces = contextClass.getGenericInterfaces();
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

    /**
     * Gets a {@link TypeVariableResolver} which delegates to the given {@link Map}.
     *
     * @param resolutionTable the {@link Map} containing the type variable lookup entries
     * @return a {@link TypeVariableResolver}
     */
    public static TypeVariableResolver resolver(@NonNull Map<TypeVariable<?>, Type> resolutionTable) {
        return variable -> Optional.ofNullable(resolutionTable.get(variable));
    }

    protected static Map<TypeVariable<?>, Type> indexResolvableParameters(Type context) {
        Map<TypeVariable<?>, Type> out = mapWithSpecificTypeKeys();
        indexResolvableParameters(context, out);
        return out;
    }

    protected static void indexResolvableParameters(Type ctx, Map<TypeVariable<?>, Type> out) {
        do {
            //process the current context type
            if (ctx instanceof Class) {
                return; //no more parameterized owner types remain
            } else if (ctx instanceof ParameterizedType) {
                ParameterizedType context = (ParameterizedType) ctx;

                //enumerate all of the type's parameter arguments and save them into the index
                Type[] arguments = context.getActualTypeArguments();
                TypeVariable<? extends Class<?>>[] variables = ((Class<?>) context.getRawType()).getTypeParameters();
                for (int i = 0; i < variables.length; i++) {
                    Type existingResolvedType = out.putIfAbsent(variables[i], arguments[i]);

                    //should be impossible
                    assert existingResolvedType == null : "type variable " + variables[i] + " declared in " + variables[i].getGenericDeclaration() + " was already resolved?!?";
                }

                //"recurse" into the context's owner type
                ctx = context.getOwnerType();
            } else {
                throw unsupportedTypeException(ctx);
            }
        } while (ctx != null);
    }

    /**
     * Resolves all {@link TypeVariable}s within the given {@link Type}.
     *
     * @param resolver the {@link TypeVariableResolver} to use
     * @param t        the {@link Type} to resolve
     * @return the resolved {@link Type}
     */
    @SuppressWarnings("ArrayEquality")
    public static Type resolve(@NonNull TypeVariableResolver resolver, @NonNull Type t) {
        if (t instanceof Class) { //ordinary class, nothing needs to be resolved
            return t;
        } else if (t instanceof GenericArrayType) { //we need to resolve the array's component type
            GenericArrayType type = (GenericArrayType) t;

            Type originalComponentType = type.getGenericComponentType();
            Type resolvedComponentType = resolve(resolver, type);

            return originalComponentType != resolvedComponentType
                    ? array(resolvedComponentType) //the component type changed, we need to re-create the type instance
                    : originalComponentType; //the component type is unchanged, return original type unmodified
        } else if (t instanceof ParameterizedType) { //we need to resolve the type's type arguments and owner type
            ParameterizedType type = (ParameterizedType) t;

            Type[] originalActualTypeArguments = type.getActualTypeArguments();
            Type[] resolvedActualTypeArguments = resolveArray(resolver, originalActualTypeArguments);

            Type originalOwnerType = type.getOwnerType();
            Type resolvedOwnerType = originalOwnerType != null ? resolve(resolver, originalOwnerType) : null;

            return originalActualTypeArguments != resolvedActualTypeArguments || originalOwnerType != resolvedOwnerType
                    ? parameterized((Class<?>) type.getRawType(), resolvedOwnerType, resolvedActualTypeArguments) //one of the child types changed, we need to re-create the type instance
                    : type; //all of the child types are unchanged, return original type unmodified
        } else if (t instanceof WildcardType) { //we need to resolve the type's upper and lower bounds
            WildcardType type = (WildcardType) t;

            Type[] originalUpperBounds = type.getUpperBounds();
            Type[] resolvedUpperBounds = resolveArray(resolver, originalUpperBounds);

            Type[] originalLowerBounds = type.getLowerBounds();
            Type[] resolvedLowerBounds = resolveArray(resolver, originalLowerBounds);

            return originalUpperBounds != resolvedUpperBounds || originalLowerBounds != resolvedLowerBounds
                    ? wildcard(resolvedUpperBounds, resolvedLowerBounds) //one of the child types changed, we need to re-create the type instance
                    : type; //all of the child types are unchanged, return original type unmodified
        } else if (t instanceof TypeVariable) { //we need to try to resolve the type variable itself
            TypeVariable<?> type = (TypeVariable<?>) t;

            Optional<Type> resolvedType = resolver.resolveTypeVariable(type);
            if (resolvedType.isPresent()) { //the type variable was resolved
                return resolvedType.get();
            }

            Type[] originalBounds = type.getBounds();
            Type[] resolvedBounds = resolveArray(resolver, originalBounds);

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
        } else { //invalid or unknown type
            throw unsupportedTypeException(t);
        }
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

    /**
     * Finds the generic parameterization of the given target class inherited by the given context type.
     * <p>
     * Examples:
     * <ul>
     *     <li>{@code inheritedGenericSupertype(String.class, Comparable.class)} returns {@code Comparable<String>}</li>
     *     <li>{@code inheritedGenericSupertype(StringList.class, Iterable.class)} returns {@code Iterable<String>} (assuming {@code StringList} is a class which inherits from {@code List<String>})</li>
     *     <li>{@code inheritedGenericSupertype(ArrayList<? extends CharSequence>, Collection.class)} returns {@code Collection<? extends CharSequence>}</li>
     *     <li>{@code inheritedGenericSupertype(ArrayList<? extends CharSequence>, RandomAccess.class)} returns {@code RandomAccess.class}</li>
     * </ul>
     *
     * @param context     the context type which inherits from the target class
     * @param targetClass the target class to search for the inherited parameterization of
     * @return the generic parameterization of the given target class inherited by the given context type
     */
    public static Type inheritedGenericSupertype(@NonNull Type context, @NonNull Class<?> targetClass) {
        checkArg(!(context instanceof Class) || ((Class<?>) context).getTypeParameters().length == 0, "given type: %s has some type parameters, but no arguments were given!", context);

        //find the inheritance path from the context class to the target class
        Deque<Type> path = new ArrayDeque<>();
        checkState(findAnyPathToTarget(context, targetClass, Collections.newSetFromMap(new IdentityHashMap<>()), path),
                "unable to find inheritance path from %s to %s", context, targetClass);

        //iterate up the inheritance path, resolving type variables as we go

        Map<TypeVariable<?>, Type> resolveIndex = indexResolvableParameters(context);
        Type lastResolvedType = context;
        for (Iterator<Type> itr = path.descendingIterator(); itr.hasNext(); ) {
            //resolve any type variables in the current type
            lastResolvedType = resolve(resolver(resolveIndex), itr.next());

            //index the type arguments on the current type
            resolveIndex.clear();
            indexResolvableParameters(lastResolvedType, resolveIndex);
        }
        return lastResolvedType;
    }

    protected static boolean findAnyPathToTarget(Type contextType, Class<?> targetClass, Set<Class<?>> visitedClasses, Deque<Type> outputStack) {
        Class<?> contextClass = raw(contextType);

        if (!visitedClasses.add(contextClass) //we've already visited this class, avoid visiting it a second time
            || !targetClass.isAssignableFrom(contextClass)) { //skip the current class if it isn't a subtype of the target
            return false;
        } else if (contextClass == targetClass) { //we found the class we were searching for!
            return true;
        }

        if (targetClass.isInterface()) { //the class we're searching for is an interface, so we should recurse through the available interfaces
            for (Type interfaceType : contextClass.getGenericInterfaces()) {
                outputStack.push(interfaceType);
                if (findAnyPathToTarget(interfaceType, targetClass, visitedClasses, outputStack)) { //the target was found
                    return true;
                }
                outputStack.pop(); //we didn't find the type we were looking for, so let's pop it off the stack again
            }
        }

        if (!contextClass.isInterface()) { //we're currently in an ordinary class, let's recurse through the superclasses until we reach the target
            Type superclass = contextClass.getGenericSuperclass();
            outputStack.push(superclass);
            if (findAnyPathToTarget(superclass, targetClass, visitedClasses, outputStack)) { //the target was found
                return true;
            }
            outputStack.pop(); //we didn't find the type we were looking for, so let's pop it off the stack again
        }

        return false;
    }

    protected static RuntimeException unresolvedException(TypeVariable<?> variable) {
        return new IllegalArgumentException("unresolved type variable: " + variable + " (declared in " + variable.getGenericDeclaration() + ')');
    }

    protected static RuntimeException wildcardNotAllowedException(Object msg) {
        return new IllegalArgumentException("wildcard is not allowed in this scope: " + msg);
    }

    protected static RuntimeException unsupportedTypeException(Object msg) {
        return new IllegalArgumentException("unsupported type type: " + msg.getClass().getTypeName());
    }

    public static Stream<? super Class<?>> allSuperclasses(@NonNull Class<?> clazz) {
        Stream.Builder<? super Class<?>> builder = Stream.builder();
        do {
            builder.add(clazz);
        } while ((clazz = clazz.getSuperclass()) != null);
        return builder.build();
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

    /**
     * Checks whether or not the {@link WildcardType wildcard type} with the given {@link WildcardType#getUpperBounds() upper bounds} and {@link WildcardType#getLowerBounds() lower bounds}
     * is a wildcard of the form {@code ? super ...}.
     *
     * @param upperBounds the {@link WildcardType#getUpperBounds() upper bounds}
     * @param lowerBounds the {@link WildcardType#getLowerBounds() lower bounds}
     * @return whether or not the wildcard is of the form {@code ? super ...}
     */
    public static boolean isWildcardSuper(Type[] upperBounds, Type[] lowerBounds) {
        return lowerBounds.length > 0
               && (upperBounds.length == 0 || upperBounds[0] == Object.class);
    }

    /**
     * Checks whether or not the {@link WildcardType wildcard type} with the given {@link WildcardType#getUpperBounds() upper bounds} and {@link WildcardType#getLowerBounds() lower bounds}
     * is a wildcard of the form {@code ?} or {@code ? extends Object}.
     *
     * @param upperBounds the {@link WildcardType#getUpperBounds() upper bounds}
     * @param lowerBounds the {@link WildcardType#getLowerBounds() lower bounds}
     * @return whether or not the wildcard is of the form {@code ?} or {@code ? extends Object}
     */
    public static boolean isWildcardUnbounded(Type[] upperBounds, Type[] lowerBounds) {
        return lowerBounds.length == 0
               && (upperBounds.length == 0 || upperBounds[0] == Object.class);
    }

    /**
     * Checks whether or not the {@link WildcardType wildcard type} with the given {@link WildcardType#getUpperBounds() upper bounds} and {@link WildcardType#getLowerBounds() lower bounds}
     * is a wildcard of the form {@code ? extends ...}. The special case {@code ? extends Object} is excluded.
     *
     * @param upperBounds the {@link WildcardType#getUpperBounds() upper bounds}
     * @param lowerBounds the {@link WildcardType#getLowerBounds() lower bounds}
     * @return whether or not the wildcard is of the form {@code ? extends ...}
     * @see #isWildcardExtendsAny
     */
    public static boolean isWildcardExtendsStrict(Type[] upperBounds, Type[] lowerBounds) {
        return lowerBounds.length == 0
               && upperBounds.length != 0 && upperBounds[0] != Object.class;
    }

    /**
     * Checks whether or not the {@link WildcardType wildcard type} with the given {@link WildcardType#getUpperBounds() upper bounds} and {@link WildcardType#getLowerBounds() lower bounds}
     * is a wildcard of the form {@code ? extends ...}. The special case {@code ? extends Object} is <strong>not</strong> excluded.
     *
     * @param upperBounds the {@link WildcardType#getUpperBounds() upper bounds}
     * @param lowerBounds the {@link WildcardType#getLowerBounds() lower bounds}
     * @return whether or not the wildcard is of the form {@code ? extends ...}
     * @see #isWildcardExtendsStrict
     */
    public static boolean isWildcardExtendsAny(Type[] upperBounds, Type[] lowerBounds) {
        return lowerBounds.length == 0
               && upperBounds.length != 0;
    }
}
