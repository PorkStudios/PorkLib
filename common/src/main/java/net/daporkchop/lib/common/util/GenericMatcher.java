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

package net.daporkchop.lib.common.util;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * A helper class for looking up generic parameters in implementation classes.
 * <p>
 * Inspired by Netty's GenericParameterMatcher, but far more powerful.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class GenericMatcher {
    private static final Map<Key, Class<?>> CACHE = new WeakHashMap<>(); //TODO: this doesn't have soft keys

    /**
     * Finds the parameter of a generic type in a particular implementation of a class.
     *
     * @param thisClass  the implementation class. Must inherit from {@code superClass}!
     * @param superClass the class that holds the generic type that needs to be looked up
     * @param name       the generic type name
     * @return the class type that is passed to the generic parameter
     */
    public static <C> Class<?> find(@NonNull Class<? extends C> thisClass, @NonNull Class<C> superClass, @NonNull String name) {
        return CACHE.computeIfAbsent(new Key(thisClass, superClass, name), key -> doFind(key.thisClass, key.superClass, key.name));
    }

    /**
     * @see #find(Class, Class, String)
     */
    @SuppressWarnings("unchecked")
    public static <T, C> Class<T> uncheckedFind(@NonNull Class<? extends C> thisClass, @NonNull Class<C> superClass, @NonNull String name) {
        return (Class<T>) find(thisClass, superClass, name);
    }

    private static Class<?> doFind(@NonNull Class<?> thisClass, @NonNull Class<?> superClass, @NonNull String name) {
        List<Class<?>> hierarchy = getHierarchy(thisClass, superClass);
        if (hierarchy == null) {
            throw new IllegalArgumentException(String.format("%s does not inherit from %s!", thisClass, superClass));
        }
        return lookup(hierarchy, name);
    }

    private static List<Class<?>> getHierarchy(@NonNull Class<?> current, @NonNull Class<?> superClass) {
        List<Class<?>> list = null;
        if (current == superClass) {
            list = new ArrayList<>();
        } else if (current.getSuperclass() != null) {
            list = getHierarchy(current.getSuperclass(), superClass);
        }
        if (list == null && superClass.isInterface()) {
            Class<?>[] interfaces = current.getInterfaces();
            for (Class<?> interfaz : interfaces) {
                if ((list = getHierarchy(interfaz, superClass)) != null) {
                    break;
                }
            }
        }
        if (list != null) {
            list.add(current);
        }
        return list;
    }

    private static Class<?> lookup(@NonNull List<Class<?>> hierarchy, @NonNull String name) {
        for (int i = 0; i < hierarchy.size() - 1; i++) {
            Class<?> clazz = hierarchy.get(i);
            Class<?> next = hierarchy.get(i + 1);

            int index = -1;
            {
                Type[] genericTypes = clazz.getTypeParameters();
                for (int j = genericTypes.length - 1; j >= 0; j--) {
                    if (genericTypes[j].getTypeName().equals(name)) {
                        index = j;
                        break;
                    }
                }
            }
            if (index == -1) {
                throw new IllegalArgumentException(String.format("Unknown generic name: \"%s\"", name));
            }

            Type genericSuperType;
            if (clazz.isInterface()) {
                genericSuperType = next.getGenericInterfaces()[PArrays.linearSearch(next.getInterfaces(), clazz)];
            } else {
                genericSuperType = next.getGenericSuperclass();
            }
            if (!(genericSuperType instanceof ParameterizedType)) {
                return Object.class;
            }

            Type actualTypeParam = ((ParameterizedType) genericSuperType).getActualTypeArguments()[index];
            if (actualTypeParam instanceof ParameterizedType) {
                actualTypeParam = ((ParameterizedType) actualTypeParam).getRawType();
            }
            if (actualTypeParam instanceof Class) {
                return (Class<?>) actualTypeParam;
            }
            if (actualTypeParam instanceof GenericArrayType) {
                Type componentType = ((GenericArrayType) actualTypeParam).getGenericComponentType();
                if (componentType instanceof ParameterizedType) {
                    componentType = ((ParameterizedType) componentType).getRawType();
                }
                if (componentType instanceof Class) {
                    return Array.newInstance((Class<?>) componentType, 0).getClass();
                }
            }
            if (actualTypeParam instanceof TypeVariable) {
                // Resolved type parameter points to another type parameter.
                TypeVariable<?> v = (TypeVariable<?>) actualTypeParam;
                if (!(v.getGenericDeclaration() instanceof Class)) {
                    return Object.class;
                }

                //parametrizedSuperclass = (Class<?>) v.getGenericDeclaration();
                name = v.getName();
            }
        }
        return null;
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode
    private static final class Key {
        @NonNull
        protected final Class<?> thisClass;
        @NonNull
        protected final Class<?> superClass;
        @NonNull
        protected final String name;
    }
}
