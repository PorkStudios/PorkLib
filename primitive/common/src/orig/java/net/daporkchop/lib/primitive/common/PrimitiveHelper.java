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

package net.daporkchop.lib.primitive.common;

import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.math.PMath;

import java.util.Objects;

/**
 * Various overloads used in primitive code.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PrimitiveHelper {
    //
    //
    // default hash functions
    //
    //

    public int hash(boolean v) {
        return v ? 1 : 0;
    }

    public long hash64(boolean v) {
        return v ? 1L : 0L;
    }

    public int hash(byte v) {
        return PMath.mix32(v);
    }

    public long hash64(byte v) {
        return PMath.mix64(v);
    }

    public int hash(short v) {
        return PMath.mix32(v);
    }

    public long hash64(short v) {
        return PMath.mix64(v);
    }

    public int hash(char v) {
        return PMath.mix32(v);
    }

    public long hash64(char v) {
        return PMath.mix64(v);
    }

    public int hash(int v) {
        return PMath.mix32(v);
    }

    public long hash64(int v) {
        return PMath.mix64(v);
    }

    public int hash(long v) {
        return PMath.mix32(v);
    }

    public long hash64(long v) {
        return PMath.mix64(v);
    }

    public int hash(float v) {
        return hash(Float.floatToIntBits(v));
    }

    public long hash64(float v) {
        return hash64(Float.floatToIntBits(v));
    }

    public int hash(double v) {
        return hash(Double.doubleToLongBits(v));
    }

    public long hash64(double v) {
        return hash64(Double.doubleToLongBits(v));
    }

    public int hash(Object v) {
        return Objects.hashCode(v);
    }

    public long hash64(Object v) {
        return Objects.hashCode(v);
    }

    //
    //
    // default equality functions
    //
    //

    public boolean eq(boolean a, boolean b) {
        return a == b;
    }

    public boolean eq(byte a, byte b) {
        return a == b;
    }

    public boolean eq(short a, short b) {
        return a == b;
    }

    public boolean eq(char a, char b) {
        return a == b;
    }

    public boolean eq(int a, int b) {
        return a == b;
    }

    public boolean eq(long a, long b) {
        return a == b;
    }

    public boolean eq(float a, float b) {
        return a == b || Float.floatToIntBits(a) == Float.floatToIntBits(b);
    }

    public boolean eq(double a, double b) {
        return a == b || Double.doubleToLongBits(a) == Double.doubleToLongBits(b);
    }

    public boolean eq(Object a, Object b) {
        return Objects.equals(a, b);
    }

    //
    //
    // default comparison functions
    //
    //

    public int compare(boolean a, boolean b) {
        return Boolean.compare(a, b);
    }

    public int compareReverse(boolean a, boolean b) {
        return Boolean.compare(b, a);
    }

    public int compare(byte a, byte b) {
        return Byte.compare(a, b);
    }

    public int compareReverse(byte a, byte b) {
        return Byte.compare(b, a);
    }

    public int compare(short a, short b) {
        return Short.compare(a, b);
    }

    public int compareReverse(short a, short b) {
        return Short.compare(b, a);
    }

    public int compare(char a, char b) {
        return Character.compare(a, b);
    }

    public int compareReverse(char a, char b) {
        return Character.compare(b, a);
    }

    public int compare(int a, int b) {
        return Integer.compare(a, b);
    }

    public int compareReverse(int a, int b) {
        return Integer.compare(b, a);
    }

    public int compare(long a, long b) {
        return Long.compare(a, b);
    }

    public int compareReverse(long a, long b) {
        return Long.compare(b, a);
    }

    public int compare(float a, float b) {
        return Float.compare(a, b);
    }

    public int compareReverse(float a, float b) {
        return Float.compare(b, a);
    }

    public int compare(double a, double b) {
        return Double.compare(a, b);
    }

    public int compareReverse(double a, double b) {
        return Double.compare(b, a);
    }

    public <T extends Comparable<? super T>> int compare(T a, T b) {
        return a.compareTo(b);
    }

    public <T extends Comparable<? super T>> int compareReverse(T a, T b) {
        return b.compareTo(a);
    }
}
