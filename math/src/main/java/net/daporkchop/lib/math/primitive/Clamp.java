/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

public class Clamp {
    private Clamp() {
    }

    public static long clampLong(long val, long min, long max) {
        return Min.minLong(Max.maxLong(val, min), max);
    }

    public static int clampInt(int val, int min, int max) {
        return Min.minInt(Max.maxInt(val, min), max);
    }

    public static short clampShort(short val, short min, short max) {
        return Min.minShort(Max.maxShort(val, min), max);
    }

    public static byte clampByte(byte val, byte min, byte max) {
        return Min.minByte(Max.maxByte(val, min), max);
    }

    public static float clampFloat(float val, float min, float max) {
        return Min.minFloat(Max.maxFloat(val, min), max);
    }

    public static double clampDouble(double val, double min, double max) {
        return Min.minDouble(Max.maxDouble(val, min), max);
    }
}
