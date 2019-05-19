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

import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.util.PArrays;
import net.daporkchop.lib.common.util.PorkUtil;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Fixes issues in the original implementation with finding generics in interfaces.
 */
@FunctionalInterface
public interface TypeParameterMatcher extends Predicate<Object> {
    Map<Class<?>, TypeParameterMatcher> getCache = Collections.synchronizedMap(PorkUtil.newSoftCache());
    Map<LookupKey, Class<?>> findCache = Collections.synchronizedMap(PorkUtil.newSoftCache());

    static TypeParameterMatcher get(final Class<?> parameterType) {
        return getCache.computeIfAbsent(parameterType, type -> type::isInstance);
    }

    static TypeParameterMatcher find(final Class<?> thisClass, final Class<?> parametrizedSuperclass, final String typeParamName) {
        LookupKey key = new LookupKey(thisClass, parametrizedSuperclass, typeParamName);

        Class<?> type = findCache.get(key);
        if (type == null)   {
            findCache.put(key, type = findRecursive(thisClass, thisClass, parametrizedSuperclass, typeParamName));
        }

        return get(type);
    }

    static Class<?> findRecursive(final Class<?> thisClass, Class<?> currentClass, Class<?> parametrizedSuperclass, String typeParamName) {
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

    static Class<?> check(final Class<?> thisClass, Class<?> currentClass, Class<?> parametrizedSuperclass, String typeParamName, Class<?> superClass) {
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

    @RequiredArgsConstructor
    final class LookupKey {
        protected final Class thisClass;
        protected final Class parametrizedSuperclass;
        protected final String typeParamName;

        @Override
        public int hashCode() {
            return (this.thisClass.hashCode() * 558506051 + this.parametrizedSuperclass.hashCode()) * 363584059 + this.typeParamName.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (obj instanceof LookupKey) {
                LookupKey key = (LookupKey) obj;
                return key.thisClass == this.thisClass && key.parametrizedSuperclass == this.parametrizedSuperclass && this.typeParamName.equals(key.typeParamName);
            } else {
                return false;
            }
        }
    }
}
