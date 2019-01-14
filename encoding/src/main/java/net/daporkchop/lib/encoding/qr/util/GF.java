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

package net.daporkchop.lib.encoding.qr.util;

import lombok.Getter;

@Getter
public class GF {
    public static final GF QR_CODE_FIELD_256 = new GF(0x011D, 256, 0);

    private final int[] expTable;
    private final int[] logTable;
    private final GFPoly zero;
    private final GFPoly one;
    private final int size;
    private final int primitive;
    private final int generatorBase;

    public GF(int primitive, int size, int b) {
        this.primitive = primitive;
        this.size = size;
        this.generatorBase = b;

        expTable = new int[size];
        logTable = new int[size];
        int x = 1;
        for (int i = 0; i < size; i++) {
            expTable[i] = x;
            x *= 2;
            if (x >= size) {
                x ^= primitive;
                x &= size - 1;
            }
        }
        for (int i = 0; i < size - 1; i++) {
            logTable[expTable[i]] = i;
        }
        zero = new GFPoly(this, new int[]{0});
        one = new GFPoly(this, new int[]{1});
    }

    public GFPoly getZero() {
        return zero;
    }

    public GFPoly getOne() {
        return one;
    }

    public GFPoly buildMonomial(int degree, int coefficient) {
        if (degree < 0) {
            throw new IllegalArgumentException();
        }
        if (coefficient == 0) {
            return zero;
        }
        int[] coefficients = new int[degree + 1];
        coefficients[0] = coefficient;
        return new GFPoly(this, coefficients);
    }

    public static int addOrSubtract(int a, int b) {
        return a ^ b;
    }

    public int exp(int a) {
        return expTable[a];
    }

    public int log(int a) {
        if (a == 0) {
            throw new IllegalArgumentException();
        }
        return logTable[a];
    }

    public int inverse(int a) {
        if (a == 0) {
            throw new ArithmeticException();
        }
        return expTable[size - logTable[a] - 1];
    }

    public int multiply(int a, int b) {
        if (a == 0 || b == 0) {
            return 0;
        }
        return expTable[(logTable[a] + logTable[b]) % (size - 1)];
    }
}
