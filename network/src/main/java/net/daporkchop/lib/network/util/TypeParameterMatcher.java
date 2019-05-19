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

import net.daporkchop.lib.common.util.PArrays;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Fixes issues in the original implementation with finding generics in interfaces.
 */
public abstract class TypeParameterMatcher {
    protected static final Map<Class<?>, TypeParameterMatcher> getCache = new IdentityHashMap<>();
    protected static final Map<Class<?>, Map<String, TypeParameterMatcher>> findCache = new IdentityHashMap<>();

    private static final TypeParameterMatcher NOOP = new TypeParameterMatcher() {
        @Override
        public boolean match(Object msg) {
            return true;
        }
    };

    public static TypeParameterMatcher get(final Class<?> parameterType) {
        TypeParameterMatcher matcher = getCache.get(parameterType);
        if (matcher == null) {
            if (parameterType == Object.class) {
                matcher = NOOP;
            } else {
                matcher = new ReflectiveMatcher(parameterType);
            }
            getCache.put(parameterType, matcher);
        }

        return matcher;
    }

    public static TypeParameterMatcher find(final Object object, final Class<?> parametrizedSuperclass, final String typeParamName) {
        final Class<?> thisClass = object.getClass();

        Map<String, TypeParameterMatcher> map = findCache.get(thisClass);
        if (map == null) {
            map = new HashMap<>();
            findCache.put(thisClass, map);
        }

        TypeParameterMatcher matcher = map.get(typeParamName);
        if (matcher == null) {
            matcher = get(findRecursive(object.getClass(), object.getClass(), parametrizedSuperclass, typeParamName));
            map.put(typeParamName, matcher);
        }

        return matcher;
    }

    private static Class<?> findRecursive(final Class<?> thisClass, Class<?> currentClass, Class<?> parametrizedSuperclass, String typeParamName) {
        Class<?> clazz;
        if (currentClass.getSuperclass() != null) {
            if ((clazz = check(thisClass, currentClass, parametrizedSuperclass, typeParamName, currentClass.getSuperclass())) != null) {
                return clazz;
            } else if ((clazz = findRecursive(thisClass, currentClass.getSuperclass(), parametrizedSuperclass, typeParamName)) != null) {
                return clazz;
            }
        }
        for (Class<?> interfaz : currentClass.getInterfaces()) {
            if ((clazz = check(thisClass, currentClass, parametrizedSuperclass, typeParamName, interfaz)) != null) {
                return clazz;
            } else if ((clazz = findRecursive(thisClass, interfaz, parametrizedSuperclass, typeParamName)) != null) {
                return clazz;
            }
        }
        return null;
    }

    private static Class<?> check(final Class<?> thisClass, Class<?> currentClass, Class<?> parametrizedSuperclass, String typeParamName, Class<?> superClass) {
        if (superClass == parametrizedSuperclass) {
            int typeParamIndex = -1;
            TypeVariable<?>[] typeParams = superClass.getTypeParameters();
            for (int i = 0; i < typeParams.length; i++) {
                if (typeParamName.equals(typeParams[i].getName())) {
                    typeParamIndex = i;
                    break;
                }
            }

            if (typeParamIndex < 0) {
                throw new IllegalStateException("unknown type parameter '" + typeParamName + "': " + parametrizedSuperclass);
            }

            Type genericSuperType;
            if (superClass.isInterface()) {
                genericSuperType = currentClass.getGenericInterfaces()[PArrays.indexOf(currentClass.getInterfaces(), superClass)];
            } else {
                genericSuperType = currentClass.getGenericSuperclass();
            }
            if (!(genericSuperType instanceof ParameterizedType)) {
                return Object.class;
            }

            Type[] actualTypeParams = ((ParameterizedType) genericSuperType).getActualTypeArguments();

            Type actualTypeParam = actualTypeParams[typeParamIndex];
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
                currentClass = thisClass;
                if (!(v.getGenericDeclaration() instanceof Class)) {
                    return Object.class;
                }

                parametrizedSuperclass = (Class<?>) v.getGenericDeclaration();
                typeParamName = v.getName();
                if (parametrizedSuperclass.isAssignableFrom(thisClass)) {
                    return null;
                } else {
                    return Object.class;
                }
            }
        }
        return null;
    }

    private static Class<?> fail(Class<?> type, String typeParamName) {
        throw new IllegalStateException(
                "cannot determine the type of the type parameter '" + typeParamName + "': " + type);
    }

    TypeParameterMatcher() {
    }

    public abstract boolean match(Object msg);

    private static final class ReflectiveMatcher extends TypeParameterMatcher {
        private final Class<?> type;

        ReflectiveMatcher(Class<?> type) {
            this.type = type;
        }

        @Override
        public boolean match(Object msg) {
            return type.isInstance(msg);
        }
    }
}
