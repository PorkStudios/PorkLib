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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * Helper class for dealing with {@link Type}.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PTypes {
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
                        return isAssignableFrom(target, wildcard(source.getBounds(), EMPTY_TYPE_ARRAY));

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
                    if (false && !isAssignableFrom(formalTypeParameters[i], actualTypeArguments[i])) { //TODO
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
                return raw(resolver, type.getRawType()); //discard parameters
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

                Type originalRawType = type.getRawType();
                Type resolvedRawType = resolve(resolver, originalRawType);

                Type originalOwnerType = type.getOwnerType();
                Type resolvedOwnerType = originalOwnerType != null ? resolve(resolver, originalOwnerType) : null;

                //noinspection ArrayEquality
                return originalActualTypeArguments != resolvedActualTypeArguments || originalRawType != resolvedRawType || originalOwnerType != resolvedOwnerType
                        ? parameterized(resolvedActualTypeArguments, resolvedRawType, resolvedOwnerType) //one of the child types changed, we need to re-create the type instance
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
