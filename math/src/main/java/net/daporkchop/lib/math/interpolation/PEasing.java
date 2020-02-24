/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.math.interpolation;

import lombok.experimental.UtilityClass;

import static java.lang.StrictMath.*;

/**
 * Implementations of some of Robert Penner's easing functions.
 * <p>
 * Adapted from <a href="https://github.com/glslify/glsl-easings">glsl-easings</a>.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PEasing {
    public static float sinInOut(float t) {
        return -0.5f * ((float) cos(t * Math.PI) - 1.0f);
    }

    public static double sinInOut(double t) {
        return -0.5d * (cos(t * Math.PI) - 1.0d);
    }

    public static float quadraticInOut(float t) {
        float p = t * t * 2.0f;
        return t < 0.5f ? p : -p + (4.0f * t) - 1.0f;
    }

    public static double quadraticInOut(double t) {
        double p = t * t * 2.0d;
        return t < 0.5d ? p : -p + (4.0d * t) - 1.0d;
    }

    public static float cubicInOut(float t)   {
        if (t < 0.5f)   {
            return t * t * t * 4.0f;
        } else {
            t = t * 2.0f - 2.0f;
            return t * t * t * 0.5f + 1.0f;
        }
    }

    public static double cubicInOut(double t)   {
        if (t < 0.5d)   {
            return t * t * t * 4.0d;
        } else {
            t = t * 2.0d - 2.0d;
            return t * t * t * 0.5d + 1.0d;
        }
    }
}
