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

package net.daporkchop.lib.unsafe;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * A wrapper around a static field using {@link PUnsafe}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public final class UnsafeStaticField {
    private final long offset;

    private final Object base;

    /**
     * Creates a new {@link UnsafeStaticField} instance from a given {@link Field}.
     *
     * @param field the {@link Field} to wrap
     * @throws IllegalArgumentException if the given {@link Field} is not static
     */
    public UnsafeStaticField(@NonNull Field field) throws IllegalArgumentException {
        if ((field.getModifiers() & Modifier.STATIC) == 0) {
            throw new IllegalArgumentException(String.format("Field \"%s\" is not static!", field));
        } else {
            this.offset = PUnsafe.staticFieldOffset(field);
            this.base = PUnsafe.staticFieldBase(field);
        }
    }

    /**
     * Creates a new {@link UnsafeStaticField} instance from a {@link Class} and the name of the field in the class.
     *
     * @param clazz the {@link Class} that the field is in
     * @param name  the name of the field
     * @throws NoSuchFieldException     if no field with the given name exists in the given class
     * @throws IllegalArgumentException if the given field is not static
     */
    public UnsafeStaticField(@NonNull Class<?> clazz, @NonNull String name) throws NoSuchFieldException, IllegalArgumentException {
        this(clazz.getDeclaredField(name));
    }

    public <T> T getObject() {
        @SuppressWarnings("unchecked")
        T value = (T) PUnsafe.getObject(this.base, this.offset);
        return value;
    }

    public boolean getBoolean() {
        return PUnsafe.getBoolean(this.base, this.offset);
    }

    public byte getByte() {
        return PUnsafe.getByte(this.base, this.offset);
    }

    public short getShort() {
        return PUnsafe.getShort(this.base, this.offset);
    }

    public char getChar() {
        return PUnsafe.getChar(this.base, this.offset);
    }

    public int getInt() {
        return PUnsafe.getInt(this.base, this.offset);
    }

    public long getLong() {
        return PUnsafe.getLong(this.base, this.offset);
    }

    public float getFloat() {
        return PUnsafe.getFloat(this.base, this.offset);
    }

    public double getDouble() {
        return PUnsafe.getDouble(this.base, this.offset);
    }

    public <T> void setObject(T value) {
        PUnsafe.putObject(this.base, this.offset, value);
    }

    public void setBoolean(boolean value) {
        PUnsafe.putBoolean(this.base, this.offset, value);
    }

    public void setByte(byte value) {
        PUnsafe.putByte(this.base, this.offset, value);
    }

    public void setShort(short value) {
        PUnsafe.putShort(this.base, this.offset, value);
    }

    public void setChar(char value) {
        PUnsafe.putChar(this.base, this.offset, value);
    }

    public void setInt(int value) {
        PUnsafe.putInt(this.base, this.offset, value);
    }

    public void setLong(long value) {
        PUnsafe.putLong(this.base, this.offset, value);
    }

    public void setFloat(float value) {
        PUnsafe.putFloat(this.base, this.offset, value);
    }

    public void setDouble(double value) {
        PUnsafe.putDouble(this.base, this.offset, value);
    }
}
