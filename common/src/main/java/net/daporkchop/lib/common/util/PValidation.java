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
import net.daporkchop.lib.common.util.exception.ValueCannotFitException;
import net.daporkchop.lib.common.util.exception.ValueOutOfBoundsException;

/**
 * @author DaPorkchop_
 */
@UtilityClass
public class PValidation {
    public long ensurePositive(long value) throws ValueOutOfBoundsException {
        if (value <= 0L) {
            throw new ValueOutOfBoundsException(value + " <= 0");
        }
        return value;
    }

    public int ensurePositive(int value) throws ValueOutOfBoundsException {
        if (value <= 0) {
            throw new ValueOutOfBoundsException(value + " <= 0");
        }
        return value;
    }

    public long ensureNonPositive(long value) throws ValueOutOfBoundsException {
        if (value > 0L) {
            throw new ValueOutOfBoundsException(value + " > 0");
        }
        return value;
    }

    public int ensureNonPositive(int value) throws ValueOutOfBoundsException {
        if (value > 0) {
            throw new ValueOutOfBoundsException(value + " > 0");
        }
        return value;
    }

    public long ensureNegative(long value) throws ValueOutOfBoundsException {
        if (value >= 0L) {
            throw new ValueOutOfBoundsException(value + " >= 0");
        }
        return value;
    }

    public int ensureNegative(int value) throws ValueOutOfBoundsException {
        if (value >= 0) {
            throw new ValueOutOfBoundsException(value + " >= 0");
        }
        return value;
    }

    public long ensureNonNegative(long value) throws ValueOutOfBoundsException {
        if (value < 0L) {
            throw new ValueOutOfBoundsException(value + " < 0");
        }
        return value;
    }

    public int ensureNonNegative(int value) throws ValueOutOfBoundsException {
        if (value < 0) {
            throw new ValueOutOfBoundsException(value + " < 0");
        }
        return value;
    }

    public int toInt(long value) throws ValueCannotFitException {
        int i = (int) value;
        if (i != value) {
            throw new ValueCannotFitException(value, 0);
        }
        return i;
    }

    public int toPositiveIntSafe(long value) throws ValueCannotFitException, ValueOutOfBoundsException  {
        return ensurePositive(toInt(value));
    }

    public int toNonPositiveIntSafe(long value) throws ValueCannotFitException, ValueOutOfBoundsException  {
        return ensureNonPositive(toInt(value));
    }

    public int toNegativeIntSafe(long value) throws ValueCannotFitException, ValueOutOfBoundsException  {
        return ensureNegative(toInt(value));
    }

    public int toNonNegativeIntSafe(long value) throws ValueCannotFitException, ValueOutOfBoundsException  {
        return ensureNonNegative(toInt(value));
    }
}
