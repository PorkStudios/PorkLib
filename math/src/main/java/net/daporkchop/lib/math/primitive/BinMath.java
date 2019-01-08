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

package net.daporkchop.lib.math.primitive;

/**
 * @author DaPorkchop_
 */
public interface BinMath {
    static boolean isPow2(long value) {
        return value != 0L && (value & value - 1L) == 0L;
    }

    static boolean isPow2(int value) {
        return value != 0 && (value & value - 1) == 0;
    }

    static boolean isPow2(short value) {
        return value != 0 && (value & value - 1) == 0;
    }

    static boolean isPow2(byte value) {
        return value != 0 && (value & value - 1) == 0;
    }

    /**
     * Gets the minimum number of bits required to store a given number
     *
     * @param value the number to store
     * @return the minimum number of bits required
     */
    static int getNumBitsNeededFor(int value) {
        int count = 0;
        while (value > 0) {
            count++;
            value = value >> 1;
        }
        return count;
    }

    static long roundToNearestPowerOf2(long value) {
        long l = value - 1;
        l = l | l >> 1;
        l = l | l >> 2;
        l = l | l >> 4;
        l = l | l >> 8;
        l = l | l >> 16;
        l = l | l >> 32;
        return l + 1;
    }

    static int roundToNearestPowerOf2(int value) {
        int i = value - 1;
        i = i | i >> 1;
        i = i | i >> 2;
        i = i | i >> 4;
        i = i | i >> 8;
        i = i | i >> 16;
        return i + 1;
    }

    static short roundToNearestPowerOf2(short value) {
        short s = (short) (value - 1);
        s = (short) (s | s >> 1);
        s = (short) (s | s >> 2);
        s = (short) (s | s >> 4);
        s = (short) (s | s >> 8);
        return (short) (s + 1);
    }
}
