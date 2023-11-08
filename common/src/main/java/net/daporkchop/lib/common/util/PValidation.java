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

import lombok.experimental.UtilityClass;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.misc.string.PStrings.*;

/**
 * guava preconditions bad
 * <p>
 * porklib good
 * <p>
 * fite me
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PValidation {
    //
    //
    // argument methods
    //
    //

    public static void checkArg(boolean flag) {
        if (!flag) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkArg(boolean flag, boolean msg) {
        if (!flag) {
            throw new IllegalArgumentException(String.valueOf(msg));
        }
    }

    public static void checkArg(boolean flag, char msg) {
        if (!flag) {
            throw new IllegalArgumentException(String.valueOf(msg));
        }
    }

    public static void checkArg(boolean flag, int msg) {
        if (!flag) {
            throw new IllegalArgumentException(String.valueOf(msg));
        }
    }

    public static void checkArg(boolean flag, long msg) {
        if (!flag) {
            throw new IllegalArgumentException(String.valueOf(msg));
        }
    }

    public static void checkArg(boolean flag, float msg) {
        if (!flag) {
            throw new IllegalArgumentException(String.valueOf(msg));
        }
    }

    public static void checkArg(boolean flag, double msg) {
        if (!flag) {
            throw new IllegalArgumentException(String.valueOf(msg));
        }
    }

    public static void checkArg(boolean flag, Object msg) {
        if (!flag) {
            throw new IllegalArgumentException(String.valueOf(msg));
        }
    }

    public static void checkArg(boolean flag, String format, char a0) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0));
        }
    }

    public static void checkArg(boolean flag, String format, int a0) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0));
        }
    }

    public static void checkArg(boolean flag, String format, long a0) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0));
        }
    }

    public static void checkArg(boolean flag, String format, float a0) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0));
        }
    }

    public static void checkArg(boolean flag, String format, double a0) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0));
        }
    }

    public static void checkArg(boolean flag, String format, Object a0) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0));
        }
    }

    public static void checkArg(boolean flag, String format, char a0, char a1) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1));
        }
    }

    public static void checkArg(boolean flag, String format, int a0, int a1) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1));
        }
    }

    public static void checkArg(boolean flag, String format, long a0, long a1) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1));
        }
    }

    public static void checkArg(boolean flag, String format, float a0, float a1) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1));
        }
    }

    public static void checkArg(boolean flag, String format, double a0, double a1) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1));
        }
    }

    public static void checkArg(boolean flag, String format, Object a0, Object a1) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1));
        }
    }

    public static void checkArg(boolean flag, String format, char a0, Object a1) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1));
        }
    }

    public static void checkArg(boolean flag, String format, int a0, Object a1) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1));
        }
    }

    public static void checkArg(boolean flag, String format, long a0, Object a1) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1));
        }
    }

    public static void checkArg(boolean flag, String format, float a0, Object a1) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1));
        }
    }

    public static void checkArg(boolean flag, String format, double a0, Object a1) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1));
        }
    }

    public static void checkArg(boolean flag, String format, Object a0, char a1) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1));
        }
    }

    public static void checkArg(boolean flag, String format, Object a0, int a1) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1));
        }
    }

    public static void checkArg(boolean flag, String format, Object a0, long a1) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1));
        }
    }

    public static void checkArg(boolean flag, String format, Object a0, float a1) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1));
        }
    }

    public static void checkArg(boolean flag, String format, Object a0, double a1) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1));
        }
    }

    public static void checkArg(boolean flag, String format, char a0, char a1, char a2) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1, a2));
        }
    }

    public static void checkArg(boolean flag, String format, int a0, int a1, int a2) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1, a2));
        }
    }

    public static void checkArg(boolean flag, String format, long a0, long a1, long a2) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1, a2));
        }
    }

    public static void checkArg(boolean flag, String format, float a0, float a1, float a2) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1, a2));
        }
    }

    public static void checkArg(boolean flag, String format, double a0, double a1, double a2) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1, a2));
        }
    }

    public static void checkArg(boolean flag, String format, Object a0, Object a1, Object a2) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1, a2));
        }
    }

    public static void checkArg(boolean flag, String format, int a0, int a1, int a2, int a3) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1, a2, a3));
        }
    }

    public static void checkArg(boolean flag, String format, long a0, long a1, long a2, long a3) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1, a2, a3));
        }
    }

    public static void checkArg(boolean flag, String format, Object a0, Object a1, Object a2, Object a3) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1, a2, a3));
        }
    }

    public static void checkArg(boolean flag, String format, int a0, int a1, int a2, int a3, int a4) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1, a2, a3, a4));
        }
    }

    public static void checkArg(boolean flag, String format, long a0, long a1, long a2, long a3, long a4) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1, a2, a3, a4));
        }
    }

    public static void checkArg(boolean flag, String format, Object a0, Object a1, Object a2, Object a3, Object a4) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1, a2, a3, a4));
        }
    }

    public static void checkArg(boolean flag, String format, Object a0, Object a1, Object a2, Object a3, Object a4, Object a5) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, a0, a1, a2, a3, a4, a5));
        }
    }

    public static void checkArg(boolean flag, String format, Object... args) {
        if (!flag) {
            throw new IllegalArgumentException(fastFormat(format, args));
        }
    }

    //
    //
    // state methods
    //
    //

    public static void checkState(boolean flag) {
        if (!flag) {
            throw new IllegalStateException();
        }
    }

    public static void checkState(boolean flag, boolean msg) {
        if (!flag) {
            throw new IllegalStateException(String.valueOf(msg));
        }
    }

    public static void checkState(boolean flag, char msg) {
        if (!flag) {
            throw new IllegalStateException(String.valueOf(msg));
        }
    }

    public static void checkState(boolean flag, int msg) {
        if (!flag) {
            throw new IllegalStateException(String.valueOf(msg));
        }
    }

    public static void checkState(boolean flag, long msg) {
        if (!flag) {
            throw new IllegalStateException(String.valueOf(msg));
        }
    }

    public static void checkState(boolean flag, float msg) {
        if (!flag) {
            throw new IllegalStateException(String.valueOf(msg));
        }
    }

    public static void checkState(boolean flag, double msg) {
        if (!flag) {
            throw new IllegalStateException(String.valueOf(msg));
        }
    }

    public static void checkState(boolean flag, Object msg) {
        if (!flag) {
            throw new IllegalStateException(String.valueOf(msg));
        }
    }

    public static void checkState(boolean flag, String format, char a0) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0));
        }
    }

    public static void checkState(boolean flag, String format, int a0) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0));
        }
    }

    public static void checkState(boolean flag, String format, long a0) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0));
        }
    }

    public static void checkState(boolean flag, String format, float a0) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0));
        }
    }

    public static void checkState(boolean flag, String format, double a0) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0));
        }
    }

    public static void checkState(boolean flag, String format, Object a0) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0));
        }
    }

    public static void checkState(boolean flag, String format, char a0, char a1) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1));
        }
    }

    public static void checkState(boolean flag, String format, int a0, int a1) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1));
        }
    }

    public static void checkState(boolean flag, String format, long a0, long a1) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1));
        }
    }

    public static void checkState(boolean flag, String format, float a0, float a1) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1));
        }
    }

    public static void checkState(boolean flag, String format, double a0, double a1) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1));
        }
    }

    public static void checkState(boolean flag, String format, Object a0, Object a1) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1));
        }
    }

    public static void checkState(boolean flag, String format, char a0, Object a1) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1));
        }
    }

    public static void checkState(boolean flag, String format, int a0, Object a1) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1));
        }
    }

    public static void checkState(boolean flag, String format, long a0, Object a1) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1));
        }
    }

    public static void checkState(boolean flag, String format, float a0, Object a1) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1));
        }
    }

    public static void checkState(boolean flag, String format, double a0, Object a1) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1));
        }
    }

    public static void checkState(boolean flag, String format, Object a0, char a1) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1));
        }
    }

    public static void checkState(boolean flag, String format, Object a0, int a1) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1));
        }
    }

    public static void checkState(boolean flag, String format, Object a0, long a1) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1));
        }
    }

    public static void checkState(boolean flag, String format, Object a0, float a1) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1));
        }
    }

    public static void checkState(boolean flag, String format, Object a0, double a1) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1));
        }
    }

    public static void checkState(boolean flag, String format, char a0, char a1, char a2) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1, a2));
        }
    }

    public static void checkState(boolean flag, String format, int a0, int a1, int a2) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1, a2));
        }
    }

    public static void checkState(boolean flag, String format, long a0, long a1, long a2) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1, a2));
        }
    }

    public static void checkState(boolean flag, String format, float a0, float a1, float a2) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1, a2));
        }
    }

    public static void checkState(boolean flag, String format, double a0, double a1, double a2) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1, a2));
        }
    }

    public static void checkState(boolean flag, String format, Object a0, Object a1, Object a2) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1, a2));
        }
    }

    public static void checkState(boolean flag, String format, int a0, int a1, int a2, int a3) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1, a2, a3));
        }
    }

    public static void checkState(boolean flag, String format, long a0, long a1, long a2, long a3) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1, a2, a3));
        }
    }

    public static void checkState(boolean flag, String format, Object a0, Object a1, Object a2, Object a3) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1, a2, a3));
        }
    }

    public static void checkState(boolean flag, String format, int a0, int a1, int a2, int a3, int a4) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1, a2, a3, a4));
        }
    }

    public static void checkState(boolean flag, String format, long a0, long a1, long a2, long a3, long a4) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1, a2, a3, a4));
        }
    }

    public static void checkState(boolean flag, String format, Object a0, Object a1, Object a2, Object a3, Object a4) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1, a2, a3, a4));
        }
    }

    public static void checkState(boolean flag, String format, Object a0, Object a1, Object a2, Object a3, Object a4, Object a5) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, a0, a1, a2, a3, a4, a5));
        }
    }

    public static void checkState(boolean flag, String format, Object... args) {
        if (!flag) {
            throw new IllegalStateException(fastFormat(format, args));
        }
    }

    //
    //
    // index methods
    //
    //

    public static void checkIndex(boolean flag) {
        if (!flag) {
            throw new IndexOutOfBoundsException();
        }
    }

    public static void checkIndex(boolean flag, boolean msg) {
        if (!flag) {
            throw new IndexOutOfBoundsException(String.valueOf(msg));
        }
    }

    public static void checkIndex(boolean flag, char msg) {
        if (!flag) {
            throw new IndexOutOfBoundsException(String.valueOf(msg));
        }
    }

    public static void checkIndex(boolean flag, int msg) {
        if (!flag) {
            throw new IndexOutOfBoundsException(String.valueOf(msg));
        }
    }

    public static void checkIndex(boolean flag, long msg) {
        if (!flag) {
            throw new IndexOutOfBoundsException(String.valueOf(msg));
        }
    }

    public static void checkIndex(boolean flag, float msg) {
        if (!flag) {
            throw new IndexOutOfBoundsException(String.valueOf(msg));
        }
    }

    public static void checkIndex(boolean flag, double msg) {
        if (!flag) {
            throw new IndexOutOfBoundsException(String.valueOf(msg));
        }
    }

    public static void checkIndex(boolean flag, Object msg) {
        if (!flag) {
            throw new IndexOutOfBoundsException(String.valueOf(msg));
        }
    }

    public static void checkIndex(boolean flag, String format, char a0) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0));
        }
    }

    public static void checkIndex(boolean flag, String format, int a0) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0));
        }
    }

    public static void checkIndex(boolean flag, String format, long a0) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0));
        }
    }

    public static void checkIndex(boolean flag, String format, float a0) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0));
        }
    }

    public static void checkIndex(boolean flag, String format, double a0) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0));
        }
    }

    public static void checkIndex(boolean flag, String format, Object a0) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0));
        }
    }

    public static void checkIndex(boolean flag, String format, char a0, char a1) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1));
        }
    }

    public static void checkIndex(boolean flag, String format, int a0, int a1) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1));
        }
    }

    public static void checkIndex(boolean flag, String format, long a0, long a1) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1));
        }
    }

    public static void checkIndex(boolean flag, String format, float a0, float a1) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1));
        }
    }

    public static void checkIndex(boolean flag, String format, double a0, double a1) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1));
        }
    }

    public static void checkIndex(boolean flag, String format, Object a0, Object a1) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1));
        }
    }

    public static void checkIndex(boolean flag, String format, char a0, Object a1) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1));
        }
    }

    public static void checkIndex(boolean flag, String format, int a0, Object a1) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1));
        }
    }

    public static void checkIndex(boolean flag, String format, long a0, Object a1) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1));
        }
    }

    public static void checkIndex(boolean flag, String format, float a0, Object a1) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1));
        }
    }

    public static void checkIndex(boolean flag, String format, double a0, Object a1) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1));
        }
    }

    public static void checkIndex(boolean flag, String format, Object a0, char a1) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1));
        }
    }

    public static void checkIndex(boolean flag, String format, Object a0, int a1) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1));
        }
    }

    public static void checkIndex(boolean flag, String format, Object a0, long a1) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1));
        }
    }

    public static void checkIndex(boolean flag, String format, Object a0, float a1) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1));
        }
    }

    public static void checkIndex(boolean flag, String format, Object a0, double a1) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1));
        }
    }

    public static void checkIndex(boolean flag, String format, char a0, char a1, char a2) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1, a2));
        }
    }

    public static void checkIndex(boolean flag, String format, int a0, int a1, int a2) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1, a2));
        }
    }

    public static void checkIndex(boolean flag, String format, long a0, long a1, long a2) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1, a2));
        }
    }

    public static void checkIndex(boolean flag, String format, float a0, float a1, float a2) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1, a2));
        }
    }

    public static void checkIndex(boolean flag, String format, double a0, double a1, double a2) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1, a2));
        }
    }

    public static void checkIndex(boolean flag, String format, Object a0, Object a1, Object a2) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1, a2));
        }
    }

    public static void checkIndex(boolean flag, String format, int a0, int a1, int a2, int a3) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1, a2, a3));
        }
    }

    public static void checkIndex(boolean flag, String format, long a0, long a1, long a2, long a3) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1, a2, a3));
        }
    }

    public static void checkIndex(boolean flag, String format, Object a0, Object a1, Object a2, Object a3) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1, a2, a3));
        }
    }

    public static void checkIndex(boolean flag, String format, int a0, int a1, int a2, int a3, int a4) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1, a2, a3, a4));
        }
    }

    public static void checkIndex(boolean flag, String format, long a0, long a1, long a2, long a3, long a4) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1, a2, a3, a4));
        }
    }

    public static void checkIndex(boolean flag, String format, Object a0, Object a1, Object a2, Object a3, Object a4) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1, a2, a3, a4));
        }
    }

    public static void checkIndex(boolean flag, String format, Object a0, Object a1, Object a2, Object a3, Object a4, Object a5) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, a0, a1, a2, a3, a4, a5));
        }
    }

    public static void checkIndex(boolean flag, String format, Object... args) {
        if (!flag) {
            throw new IndexOutOfBoundsException(fastFormat(format, args));
        }
    }

    //
    //
    // integer methods
    //
    //

    public static int positive(int value) {
        checkArg(value > 0, "argument must be positive (given: %d)", value);
        return value;
    }

    public static long positive(long value) {
        checkArg(value > 0L, "argument must be positive (given: %d)", value);
        return value;
    }

    public static int positive(int value, Object name) {
        checkArg(value > 0, "%2$s must be positive (given: %1$d)", value, name);
        return value;
    }

    public static long positive(long value, Object name) {
        checkArg(value > 0L, "%2$s must be positive (given: %1$d)", value, name);
        return value;
    }

    public static int notPositive(int value) {
        checkArg(value <= 0, "argument must not be positive (given: %d)", value);
        return value;
    }

    public static long notPositive(long value) {
        checkArg(value <= 0L, "argument must not be positive (given: %d)", value);
        return value;
    }

    public static int notPositive(int value, Object name) {
        checkArg(value <= 0, "%2$s must not be positive (given: %1$d)", value, name);
        return value;
    }

    public static long notPositive(long value, Object name) {
        checkArg(value <= 0L, "%2$s must not be positive (given: %1$d)", value, name);
        return value;
    }

    public static int negtive(int value) {
        checkArg(value < 0, "argument must be negative (given: %d)", value);
        return value;
    }

    public static long negtive(long value) {
        checkArg(value < 0, "argument must be negative (given: %d)", value);
        return value;
    }

    public static int negtive(int value, Object name) {
        checkArg(value < 0, "%2$s must be negative (given: %1$d)", value, name);
        return value;
    }

    public static long negtive(long value, Object name) {
        checkArg(value < 0, "%2$s must be negative (given: %1$d)", value, name);
        return value;
    }

    public static int notNegative(int value) {
        checkArg(value >= 0, "argument must not be negative (given: %d)", value);
        return value;
    }

    public static long notNegative(long value) {
        checkArg(value >= 0L, "argument must not be negative (given: %d)", value);
        return value;
    }

    public static int notNegative(int value, Object name) {
        checkArg(value >= 0, "%2$s must not be negative (given: %1$d)", value, name);
        return value;
    }

    public static long notNegative(long value, Object name) {
        checkArg(value >= 0L, "%2$s must not be negative (given: %1$d)", value, name);
        return value;
    }

    public static int toInt(long value) {
        int i = (int) value;
        checkArg(i == value, "argument cannot be converted losslessly to an int (given: %d)", value);
        return i;
    }

    public static int toInt(long value, Object name) {
        int i = (int) value;
        checkArg(i == value, "%2$s cannot be converted losslessly to an int (given: %1$d)", value, name);
        return i;
    }

    public static short toShort(char value) {
        short s = (short) value;
        checkArg(s == value, "argument cannot be converted losslessly to a short (given: %d)", value);
        return s;
    }

    public static short toShort(char value, Object name) {
        short s = (short) value;
        checkArg(s == value, "%2$s cannot be converted losslessly to a short (given: %1$d)", value, name);
        return s;
    }

    public static short toShort(int value) {
        short s = (short) value;
        checkArg(s == value, "argument cannot be converted losslessly to a short (given: %d)", value);
        return s;
    }

    public static short toShort(int value, Object name) {
        short s = (short) value;
        checkArg(s == value, "%2$s cannot be converted losslessly to a short (given: %1$d)", value, name);
        return s;
    }

    public static short toShort(long value) {
        short s = (short) value;
        checkArg(s == value, "argument cannot be converted losslessly to a short (given: %d)", value);
        return s;
    }

    public static short toShort(long value, Object name) {
        short s = (short) value;
        checkArg(s == value, "%2$s cannot be converted losslessly to a short (given: %1$d)", value, name);
        return s;
    }

    public static char tochar(short value) {
        char c = (char) value;
        checkArg(c == value, "argument cannot be converted losslessly to a char (given: %d)", value);
        return c;
    }

    public static char tochar(short value, Object name) {
        char c = (char) value;
        checkArg(c == value, "%2$s cannot be converted losslessly to a char (given: %1$d)", value, name);
        return c;
    }

    public static char tochar(int value) {
        char c = (char) value;
        checkArg(c == value, "argument cannot be converted losslessly to a char (given: %d)", value);
        return c;
    }

    public static char tochar(int value, Object name) {
        char c = (char) value;
        checkArg(c == value, "%2$s cannot be converted losslessly to a char (given: %1$d)", value, name);
        return c;
    }

    public static char tochar(long value) {
        char c = (char) value;
        checkArg(c == value, "argument cannot be converted losslessly to a char (given: %d)", value);
        return c;
    }

    public static char tochar(long value, Object name) {
        char c = (char) value;
        checkArg(c == value, "%2$s cannot be converted losslessly to a char (given: %1$d)", value, name);
        return c;
    }

    public static byte toByte(short value) {
        byte b = (byte) value;
        checkArg(b == value, "argument cannot be converted losslessly to a byte (given: %d)", value);
        return b;
    }

    public static byte toByte(short value, Object name) {
        byte b = (byte) value;
        checkArg(b == value, "%2$s cannot be converted losslessly to a byte (given: %1$d)", value, name);
        return b;
    }

    public static byte toByte(char value) {
        byte b = (byte) value;
        checkArg(b == value, "argument cannot be converted losslessly to a byte (given: %d)", value);
        return b;
    }

    public static byte toByte(char value, Object name) {
        byte b = (byte) value;
        checkArg(b == value, "%2$s cannot be converted losslessly to a byte (given: %1$d)", value, name);
        return b;
    }

    public static byte toByte(int value) {
        byte b = (byte) value;
        checkArg(b == value, "argument cannot be converted losslessly to a byte (given: %d)", value);
        return b;
    }

    public static byte toByte(int value, Object name) {
        byte b = (byte) value;
        checkArg(b == value, "%2$s cannot be converted losslessly to a byte (given: %1$d)", value, name);
        return b;
    }

    public static byte toByte(long value) {
        byte b = (byte) value;
        checkArg(b == value, "argument cannot be converted losslessly to a byte (given: %d)", value);
        return b;
    }

    public static byte toByte(long value, Object name) {
        byte b = (byte) value;
        checkArg(b == value, "%2$s cannot be converted losslessly to a byte (given: %1$d)", value, name);
        return b;
    }

    public static int checkIndex(int totalSize, int index)  {
        notNegative(totalSize, "totalSize");
        checkIndex(index >= 0 && index < totalSize, "total: 0-%d, index: %d", totalSize, index);
        return index;
    }

    public static long checkIndex(long totalSize, long index)  {
        notNegative(totalSize, "totalSize");
        checkIndex(index >= 0L && index < totalSize, "total: 0-%d, index: %d", totalSize, index);
        return index;
    }

    public static int checkIndex(int totalStart, int totalEnd, int index)  {
        notNegative(totalStart, "totalStart");
        checkArg(totalEnd > totalStart, "total range is empty or backwards (totalStart: %d, totalEnd %d)", totalStart, totalEnd);
        checkIndex(index >= totalStart && index < totalEnd, "total: %d-%d, index: %d", totalStart, totalEnd, index);
        return index;
    }

    public static long checkIndex(long totalStart, long totalEnd, long index)  {
        notNegative(totalStart, "totalStart");
        checkArg(totalEnd > totalStart, "total range is empty or backwards (totalStart: %d, totalEnd %d)", totalStart, totalEnd);
        checkIndex(index >= totalStart && index < totalEnd, "total: %d-%d, index: %d", totalStart, totalEnd, index);
        return index;
    }

    public static void checkRange(int totalSize, int startInclusive, int endExclusive) {
        notNegative(totalSize, "totalSize");
        checkIndex(endExclusive > startInclusive && startInclusive >= 0 && endExclusive <= totalSize, "total: 0-%d, range: %d-%d", totalSize, startInclusive, endExclusive);
    }

    public static void checkRange(long totalSize, long startInclusive, long endExclusive) {
        notNegative(totalSize, "totalSize");
        checkIndex(endExclusive > startInclusive && startInclusive >= 0L && endExclusive <= totalSize, "total: 0-%d, range: %d-%d", totalSize, startInclusive, endExclusive);
    }

    public static void checkRange(int totalStart, int totalEnd, int startInclusive, int endExclusive) {
        notNegative(totalStart, "totalStart");
        checkArg(totalEnd > totalStart, "total range is empty or backwards (totalStart: %d, totalEnd %d)", totalStart, totalEnd);
        checkIndex(endExclusive > startInclusive && startInclusive >= totalStart && endExclusive <= totalEnd, "total: %d-%d, range: %d-%d", totalStart, totalEnd, startInclusive, endExclusive);
    }

    public static void checkRange(long totalStart, long totalEnd, long startInclusive, long endExclusive) {
        notNegative(totalStart, "totalStart");
        checkArg(totalEnd > totalStart, "total range is empty or backwards (totalStart: %d, totalEnd %d)", totalStart, totalEnd);
        checkIndex(endExclusive > startInclusive && startInclusive >= totalStart && endExclusive <= totalEnd, "total: %d-%d, range: %d-%d", totalStart, totalEnd, startInclusive, endExclusive);
    }

    public static void checkRangeLen(int totalSize, int startInclusive, int length) {
        notNegative(totalSize, "totalSize");
        checkIndex(startInclusive >= 0 && length >= 0 && addExact(startInclusive, length) <= totalSize, "total: 0-%d, range: %d-%d", totalSize, startInclusive, startInclusive + length);
    }

    public static void checkRangeLen(long totalSize, long startInclusive, long length) {
        notNegative(totalSize, "totalSize");
        checkIndex(startInclusive >= 0 && length >= 0L && addExact(startInclusive, length) <= totalSize, "total: 0-%d, range: %d-%d", totalSize, startInclusive, startInclusive + length);
    }

    public static void checkRangeLen(int totalStart, int totalEnd, int startInclusive, int length) {
        notNegative(totalStart, "totalStart");
        checkArg(totalEnd > totalStart, "total range is empty or backwards (totalStart: %d, totalEnd %d)", totalStart, totalEnd);
        checkIndex(startInclusive >= totalStart && length >= 0 && addExact(startInclusive, length) <= totalEnd, "total: %d-%d, range: %d-%d", totalStart, totalEnd, startInclusive, startInclusive + length);
    }

    public static void checkRangeLen(long totalStart, long totalEnd, long startInclusive, long length) {
        notNegative(totalStart, "totalStart");
        checkArg(totalEnd > totalStart, "total range is empty or backwards (totalStart: %d, totalEnd %d)", totalStart, totalEnd);
        checkIndex(startInclusive >= totalStart && length >= 0L && addExact(startInclusive, length) <= totalEnd, "total: %d-%d, range: %d-%d", totalStart, totalEnd, startInclusive, startInclusive + length);
    }
}
