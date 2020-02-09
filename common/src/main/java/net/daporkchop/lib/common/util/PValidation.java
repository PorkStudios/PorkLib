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

package net.daporkchop.lib.common.util;

import lombok.experimental.UtilityClass;

/**
 * @author DaPorkchop_
 */
@UtilityClass
public class PValidation {
    public long ensureNonNegative(long value) {
        if (value < 0L) {
            throw new IllegalArgumentException(value + " < 0");
        }
        return value;
    }

    public int ensureNonNegative(int value) {
        if (value < 0) {
            throw new IllegalArgumentException(value + " < 0");
        }
        return value;
    }

    public long ensureNonPositive(long value) {
        if (value > 0L) {
            throw new IllegalArgumentException(value + " > 0");
        }
        return value;
    }

    public int ensureNonPositive(int value) {
        if (value > 0) {
            throw new IllegalArgumentException(value + " > 0");
        }
        return value;
    }

    public long ensurePositive(long value) {
        if (value <= 0L) {
            throw new IllegalArgumentException(value + " <= 0");
        }
        return value;
    }

    public int ensurePositive(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException(value + " <= 0");
        }
        return value;
    }

    public long ensureNegative(long value) {
        if (value >= 0L) {
            throw new IllegalArgumentException(value + " >= 0");
        }
        return value;
    }

    public int ensureNegative(int value) {
        if (value >= 0) {
            throw new IllegalArgumentException(value + " >= 0");
        }
        return value;
    }
}
