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

package net.daporkchop.lib.reflection;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.reflection.util.Access;
import net.daporkchop.lib.reflection.util.Accessible;
import net.daporkchop.lib.reflection.util.AnnotationHolder;
import net.daporkchop.lib.reflection.util.Type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

import static net.daporkchop.lib.unsafe.PUnsafe.*;

/**
 * A Java field
 *
 * @author DaPorkchop_
 */
//TODO: cache instances of this class
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PField<V> implements Accessible, AnnotationHolder {
    protected static final Map<Field, PField> FIELD_CACHE = PorkUtil.newSoftCache();

    /**
     * Gets a field
     *
     * @param clazz the class that the field is stored in. If the field is owned by a superclass, the superclass
     *              should be passed here.
     * @param name  the name of the field
     * @param <V>   the type stored in the field
     * @return a field
     */
    public static <V> PField<V> of(@NonNull Class<?> clazz, @NonNull String name) {
        try {
            return of(clazz.getDeclaredField(name));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a field
     *
     * @param field the field
     * @param <V>   the type stored in the field
     * @return a field
     */
    @SuppressWarnings("unchecked")
    public static <V> PField<V> of(@NonNull Field field) {
        return FIELD_CACHE.computeIfAbsent(field, f -> new PField<>(
                objectFieldOffset(f),
                Type.getType(f),
                Access.getAccess(f),
                f.getModifiers(),
                f.getAnnotations(),
                f.getName(),
                (Class<V>) f.getType(),
                f.getDeclaringClass()
        ));
    }

    protected final long offset;
    @NonNull
    protected final Type type;
    @NonNull
    protected final Access access;
    protected final int modifiers;
    @NonNull
    protected final Annotation[] annotations;
    @NonNull
    protected final String name;
    @NonNull
    protected final Class<V> classType;
    @NonNull
    protected final Class<?> parentClass;

    //object methods

    /**
     * Gets this field's value on a given object
     *
     * @param o the object to get from
     * @return the value of this field on that object
     */
    @SuppressWarnings("unchecked")
    public V get(@NonNull Object o) {
        this.ensureType(Type.OBJECT);
        return (V) getObject(o, this.offset);
    }

    /**
     * Gets this field's value on a given object
     *
     * @param o the object to get from
     * @return the value of this field on that object
     */
    @SuppressWarnings("unchecked")
    public V getVolatile(@NonNull Object o) {
        this.ensureType(Type.OBJECT);
        return (V) getObjectVolatile(o, this.offset);
    }

    /**
     * Sets this field's value on a given object
     *
     * @param o     the object to set the field in
     * @param value the value to set the field to
     */
    @SuppressWarnings("unchecked")
    public void set(@NonNull Object o, V value) {
        this.ensureType(Type.OBJECT);
        putObject(o, this.offset, value);
    }

    /**
     * Sets this field's value on a given object
     *
     * @param o     the object to set the field in
     * @param value the value to set the field to
     */
    @SuppressWarnings("unchecked")
    public void setVolatile(@NonNull Object o, V value) {
        this.ensureType(Type.OBJECT);
        putObjectVolatile(o, this.offset, value);
    }

    //boolean methods

    /**
     * Gets this field's value on a given object
     *
     * @param o the object to get from
     * @return the value of this field on that object
     */
    public boolean getBoolean(@NonNull Object o) {
        this.ensureType(Type.BOOLEAN);
        return PUnsafe.getBoolean(o, this.offset);
    }

    /**
     * Gets this field's value on a given object
     *
     * @param o the object to get from
     * @return the value of this field on that object
     */
    public boolean getBooleanVolatile(@NonNull Object o) {
        this.ensureType(Type.BOOLEAN);
        return PUnsafe.getBooleanVolatile(o, this.offset);
    }

    /**
     * Sets this field's value on a given object
     *
     * @param o     the object to set the field in
     * @param value the value to set the field to
     */
    public void setBoolean(@NonNull Object o, boolean value) {
        this.ensureType(Type.BOOLEAN);
        putBoolean(o, this.offset, value);
    }

    /**
     * Sets this field's value on a given object
     *
     * @param o     the object to set the field in
     * @param value the value to set the field to
     */
    public void setBooleanVolatile(@NonNull Object o, boolean value) {
        this.ensureType(Type.BOOLEAN);
        putBooleanVolatile(o, this.offset, value);
    }

    //byte methods

    /**
     * Gets this field's value on a given object
     *
     * @param o the object to get from
     * @return the value of this field on that object
     */
    public byte getByte(@NonNull Object o) {
        this.ensureType(Type.BYTE);
        return PUnsafe.getByte(o, this.offset);
    }

    /**
     * Gets this field's value on a given object
     *
     * @param o the object to get from
     * @return the value of this field on that object
     */
    public byte getByteVolatile(@NonNull Object o) {
        this.ensureType(Type.BYTE);
        return PUnsafe.getByteVolatile(o, this.offset);
    }

    /**
     * Sets this field's value on a given object
     *
     * @param o     the object to set the field in
     * @param value the value to set the field to
     */
    public void setByte(@NonNull Object o, byte value) {
        this.ensureType(Type.BYTE);
        putByte(o, this.offset, value);
    }

    /**
     * Sets this field's value on a given object
     *
     * @param o     the object to set the field in
     * @param value the value to set the field to
     */
    public void setByteVolatile(@NonNull Object o, byte value) {
        this.ensureType(Type.BYTE);
        putByteVolatile(o, this.offset, value);
    }

    //short methods

    /**
     * Gets this field's value on a given object
     *
     * @param o the object to get from
     * @return the value of this field on that object
     */
    public short getShort(@NonNull Object o) {
        this.ensureType(Type.SHORT);
        return PUnsafe.getShort(o, this.offset);
    }

    /**
     * Gets this field's value on a given object
     *
     * @param o the object to get from
     * @return the value of this field on that object
     */
    public short getShortVolatile(@NonNull Object o) {
        this.ensureType(Type.SHORT);
        return PUnsafe.getShortVolatile(o, this.offset);
    }

    /**
     * Sets this field's value on a given object
     *
     * @param o     the object to set the field in
     * @param value the value to set the field to
     */
    public void setShort(@NonNull Object o, short value) {
        this.ensureType(Type.SHORT);
        putShort(o, this.offset, value);
    }

    /**
     * Sets this field's value on a given object
     *
     * @param o     the object to set the field in
     * @param value the value to set the field to
     */
    public void setShortVolatile(@NonNull Object o, short value) {
        this.ensureType(Type.SHORT);
        putShortVolatile(o, this.offset, value);
    }

    //int methods

    /**
     * Gets this field's value on a given object
     *
     * @param o the object to get from
     * @return the value of this field on that object
     */
    public int getInt(@NonNull Object o) {
        this.ensureType(Type.INT);
        return PUnsafe.getInt(o, this.offset);
    }

    /**
     * Gets this field's value on a given object
     *
     * @param o the object to get from
     * @return the value of this field on that object
     */
    public int getIntVolatile(@NonNull Object o) {
        this.ensureType(Type.INT);
        return PUnsafe.getIntVolatile(o, this.offset);
    }

    /**
     * Sets this field's value on a given object
     *
     * @param o     the object to set the field in
     * @param value the value to set the field to
     */
    public void setInt(@NonNull Object o, int value) {
        this.ensureType(Type.INT);
        putInt(o, this.offset, value);
    }

    /**
     * Sets this field's value on a given object
     *
     * @param o     the object to set the field in
     * @param value the value to set the field to
     */
    public void setIntVolatile(@NonNull Object o, int value) {
        this.ensureType(Type.INT);
        putIntVolatile(o, this.offset, value);
    }

    //long methods

    /**
     * Gets this field's value on a given object
     *
     * @param o the object to get from
     * @return the value of this field on that object
     */
    public long getLong(@NonNull Object o) {
        this.ensureType(Type.LONG);
        return PUnsafe.getLong(o, this.offset);
    }

    /**
     * Gets this field's value on a given object
     *
     * @param o the object to get from
     * @return the value of this field on that object
     */
    public long getLongVolatile(@NonNull Object o) {
        this.ensureType(Type.LONG);
        return PUnsafe.getLongVolatile(o, this.offset);
    }

    /**
     * Sets this field's value on a given object
     *
     * @param o     the object to set the field in
     * @param value the value to set the field to
     */
    public void setLong(@NonNull Object o, long value) {
        this.ensureType(Type.LONG);
        putLong(o, this.offset, value);
    }

    /**
     * Sets this field's value on a given object
     *
     * @param o     the object to set the field in
     * @param value the value to set the field to
     */
    public void setLongVolatile(@NonNull Object o, long value) {
        this.ensureType(Type.LONG);
        putLongVolatile(o, this.offset, value);
    }

    //float methods

    /**
     * Gets this field's value on a given object
     *
     * @param o the object to get from
     * @return the value of this field on that object
     */
    public float getFloat(@NonNull Object o) {
        this.ensureType(Type.FLOAT);
        return PUnsafe.getFloat(o, this.offset);
    }

    /**
     * Gets this field's value on a given object
     *
     * @param o the object to get from
     * @return the value of this field on that object
     */
    public float getFloatVolatile(@NonNull Object o) {
        this.ensureType(Type.FLOAT);
        return PUnsafe.getFloatVolatile(o, this.offset);
    }

    /**
     * Sets this field's value on a given object
     *
     * @param o     the object to set the field in
     * @param value the value to set the field to
     */
    public void setFloat(@NonNull Object o, float value) {
        this.ensureType(Type.FLOAT);
        putFloat(o, this.offset, value);
    }

    /**
     * Sets this field's value on a given object
     *
     * @param o     the object to set the field in
     * @param value the value to set the field to
     */
    public void setFloatVolatile(@NonNull Object o, float value) {
        this.ensureType(Type.FLOAT);
        putFloatVolatile(o, this.offset, value);
    }

    //double methods

    /**
     * Gets this field's value on a given object
     *
     * @param o the object to get from
     * @return the value of this field on that object
     */
    public double getDouble(@NonNull Object o) {
        this.ensureType(Type.DOUBLE);
        return PUnsafe.getDouble(o, this.offset);
    }

    /**
     * Gets this field's value on a given object
     *
     * @param o the object to get from
     * @return the value of this field on that object
     */
    public double getDoubleVolatile(@NonNull Object o) {
        this.ensureType(Type.DOUBLE);
        return PUnsafe.getDoubleVolatile(o, this.offset);
    }

    /**
     * Sets this field's value on a given object
     *
     * @param o     the object to set the field in
     * @param value the value to set the field to
     */
    public void setDouble(@NonNull Object o, double value) {
        this.ensureType(Type.DOUBLE);
        putDouble(o, this.offset, value);
    }

    /**
     * Sets this field's value on a given object
     *
     * @param o     the object to set the field in
     * @param value the value to set the field to
     */
    public void setDoubleVolatile(@NonNull Object o, double value) {
        this.ensureType(Type.DOUBLE);
        putDoubleVolatile(o, this.offset, value);
    }

    //char methods

    /**
     * Gets this field's value on a given object
     *
     * @param o the object to get from
     * @return the value of this field on that object
     */
    public char getChar(@NonNull Object o) {
        this.ensureType(Type.CHAR);
        return PUnsafe.getChar(o, this.offset);
    }

    /**
     * Gets this field's value on a given object
     *
     * @param o the object to get from
     * @return the value of this field on that object
     */
    public char getCharVolatile(@NonNull Object o) {
        this.ensureType(Type.CHAR);
        return PUnsafe.getCharVolatile(o, this.offset);
    }

    /**
     * Sets this field's value on a given object
     *
     * @param o     the object to set the field in
     * @param value the value to set the field to
     */
    public void setChar(@NonNull Object o, char value) {
        this.ensureType(Type.CHAR);
        putChar(o, this.offset, value);
    }

    /**
     * Sets this field's value on a given object
     *
     * @param o     the object to set the field in
     * @param value the value to set the field to
     */
    public void setCharVolatile(@NonNull Object o, char value) {
        this.ensureType(Type.CHAR);
        putCharVolatile(o, this.offset, value);
    }

    //misc

    protected void ensureType(Type type) {
        if (this.type != type && !(type.isObject() && type.isObject())) {
            throw new IllegalStateException(String.format("Invalid field type! We're %s but %s is required!", this.type, type));
        }
    }

    @Override
    public String toString() {
        return String.format("%s#%s", this.parentClass.getCanonicalName(), this.name);
    }
}
