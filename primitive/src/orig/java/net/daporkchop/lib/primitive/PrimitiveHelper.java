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

package net.daporkchop.lib.primitive;

import lombok.experimental.UtilityClass;

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

    public int hash(byte v) {
        return v;
    }

    public int hash(short v) {
        return v;
    }

    public int hash(char v) {
        return v;
    }

    public int hash(int v) {
        return v;
    }

    public int hash(long v) {
        return (int) ((v >>> 32L) ^ v);
    }

    public int hash(float v) {
        return hash(Float.floatToIntBits(v));
    }

    public int hash(double v) {
        return hash(Double.doubleToLongBits(v));
    }

    public int hash(Object v) {
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
        return a == b;
    }

    public boolean eq(double a, double b) {
        return a == b;
    }

    public boolean eq(Object a, Object b) {
        return Objects.equals(a, b);
    }
}
