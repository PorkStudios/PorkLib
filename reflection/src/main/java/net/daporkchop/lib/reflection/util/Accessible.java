/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
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

package net.daporkchop.lib.reflection.util;

import java.lang.reflect.Modifier;

/**
 * @author DaPorkchop_
 */
public interface Accessible {
    /**
     * Get this member's access modifiers
     *
     * @return this member's access modifiers
     */
    int getModifiers();

    /**
     * Gets this member's access level
     *
     * @return this member's access level
     */
    default Access getAccess() {
        return Access.getAccess(this.getModifiers());
    }

    /**
     * Checks if this member is public
     *
     * @return whether or not this member is public
     */
    default boolean isPublic() {
        return this.getAccess() == Access.PUBLIC;
    }

    /**
     * Checks if this member is private
     *
     * @return whether or not this member is private
     */
    default boolean isPrivate() {
        return this.getAccess() == Access.PRIVATE;
    }

    /**
     * Checks if this member is protected
     *
     * @return whether or not this member is protected
     */
    default boolean isProtected() {
        return this.getAccess() == Access.PROTECTED;
    }

    /**
     * Checks if this member is package-private
     *
     * @return whether or not this member is package-private
     */
    default boolean isPackagePrivate() {
        return this.getAccess() == Access.PACKAGE_PRIVATE;
    }

    /**
     * Checks if this member is static
     *
     * @return whether or not this member is static
     */
    default boolean isStatic() {
        return (this.getModifiers() & Modifier.STATIC) != 0;
    }

    /**
     * Checks if this member is final
     *
     * @return whether or not this member is final
     */
    default boolean isFinal() {
        return (this.getModifiers() & Modifier.FINAL) != 0;
    }

    /**
     * Checks if this member is synchronized
     *
     * @return whether or not this member is synchronized
     */
    default boolean isSynchronized() {
        return (this.getModifiers() & Modifier.SYNCHRONIZED) != 0;
    }

    /**
     * Checks if this member is volatile
     *
     * @return whether or not this member is volatile
     */
    default boolean isVolatile() {
        return (this.getModifiers() & Modifier.VOLATILE) != 0;
    }

    /**
     * Checks if this member is transient
     *
     * @return whether or not this member is transient
     */
    default boolean isTransient() {
        return (this.getModifiers() & Modifier.TRANSIENT) != 0;
    }

    /**
     * Checks if this member is native
     *
     * @return whether or not this member is native
     */
    default boolean isNative() {
        return (this.getModifiers() & Modifier.NATIVE) != 0;
    }

    /**
     * Checks if this member is abstract
     *
     * @return whether or not this member is abstract
     */
    default boolean isAbstract() {
        return (this.getModifiers() & Modifier.ABSTRACT) != 0;
    }

    /**
     * Checks if this member is synthetic
     *
     * @return whether or not this member is synthetic
     */
    default boolean isSynthetic()   {
        return (this.getModifiers() & 0x00001000) != 0;
    }
}
