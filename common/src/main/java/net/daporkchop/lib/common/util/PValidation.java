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

package net.daporkchop.lib.common.util;

import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.util.exception.ValueCannotFitException;
import net.daporkchop.lib.common.util.exception.ValueOutOfBoundsException;

import static net.daporkchop.lib.common.misc.string.PStrings.*;

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

    public int toPositiveIntSafe(long value) throws ValueCannotFitException, ValueOutOfBoundsException {
        return ensurePositive(toInt(value));
    }

    public int toNonPositiveIntSafe(long value) throws ValueCannotFitException, ValueOutOfBoundsException {
        return ensureNonPositive(toInt(value));
    }

    public int toNegativeIntSafe(long value) throws ValueCannotFitException, ValueOutOfBoundsException {
        return ensureNegative(toInt(value));
    }

    public int toNonNegativeIntSafe(long value) throws ValueCannotFitException, ValueOutOfBoundsException {
        return ensureNonNegative(toInt(value));
    }

    public int checkBounds(int value, int minInclusive, int maxExclusive) throws ValueOutOfBoundsException {
        if (value < minInclusive || value >= maxExclusive) {
            throw new ValueOutOfBoundsException(value, minInclusive, maxExclusive);
        }
        return value;
    }

    public int checkIndex(int value, int minInclusive, int maxExclusive) throws ValueOutOfBoundsException {
        if (value < minInclusive || value >= maxExclusive) {
            throw new IndexOutOfBoundsException(fastFormat("%s, expected %s <= value < %s", value, minInclusive, maxExclusive));
        }
        return value;
    }
}
