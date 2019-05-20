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

package net.daporkchop.lib.network.util;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.util.PArrays;
import net.daporkchop.lib.common.util.PorkUtil;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class GenericMatcher {
    protected static final Map<Key, GenericMatcher> CACHE = PorkUtil.newSoftCache();

    /**
     * Finds the value of a generic type in a particular implementation of a class.
     *
     * @param thisClass  the implementation class. Must inherit from {@code superClass}!
     * @param superClass the class that holds the generic type that needs to be looked up
     * @param name       the generic type name
     * @return a {@link GenericMatcher}
     */
    public static GenericMatcher find(@NonNull Class<?> thisClass, @NonNull Class<?> superClass, @NonNull String name) {
        return CACHE.computeIfAbsent(new Key(thisClass, superClass, name), key -> doFind(key.thisClass, key.superClass, key.name));
    }

    private static GenericMatcher doFind(@NonNull Class<?> thisClass, @NonNull Class<?> superClass, @NonNull String name) {
        List<Class<?>> hierarchy = getHierarchy(thisClass, superClass);
        if (hierarchy == null)  {
            throw new IllegalArgumentException(String.format("%s does not inherit from %s!", thisClass, superClass));
        }
        return new GenericMatcher(lookup(hierarchy, name));
    }

    private static List<Class<?>> getHierarchy(@NonNull Class<?> current, @NonNull Class<?> superClass) {
        List<Class<?>> list = null;
        if (current == superClass) {
            list = new ArrayList<>();
        } else if (current.getSuperclass() != null)   {
            list = getHierarchy(current.getSuperclass(), superClass);
        }
        if (list == null && superClass.isInterface())   {
            Class<?>[] interfaces = current.getInterfaces();
            for (Class<?> interfaz : interfaces)    {
                if ((list = getHierarchy(interfaz, superClass)) != null)    {
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
        for (int i = 0; i < hierarchy.size() - 1; i++)    {
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
            if (index == -1)    {
                throw new IllegalArgumentException(String.format("Unknown generic name: \"%s\"", name));
            }

            Type genericSuperType;
            if (clazz.isInterface())    {
                genericSuperType = next.getGenericInterfaces()[PArrays.indexOf(next.getInterfaces(), clazz)];
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

    @NonNull
    protected final Class<?> type;

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
